package com.chinatower.receive.core.alarm;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO;
import com.chinatower.platform.entity.device.BlackWhiteVO;
import com.chinatower.platform.entity.device.RangeJson;
import com.chinatower.platform.entity.device.TriggerRuleJson;
import com.chinatower.platform.service.WarningOrderFlowService;
import com.chinatower.receive.common.CommonService;
import com.chinatower.receive.constant.AlarmConstants;
import com.chinatower.receive.constant.TableConstant;
import com.chinatower.receive.core.alarm.common.AlarmBaseStategy;
import com.chinatower.receive.core.util.AlarmStrategyEnum;
import com.chinatower.receive.core.util.GisUtil;
import com.chinatower.receive.core.util.WarningOrderUtil;
import com.chinatower.receive.mapper.WarningOrderMapper;
import com.chinatower.receive.model.TriggerRuleVo;
import com.chinatower.receive.service.IAlarmSuppressConfigRepository;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.service.ISuppressStrategyService;
import com.chinatower.receive.util.IPKUtils;
import com.chinatower.receive.util.TableUtil;
import com.chinatower.receive.vo.dto.AlarmTypeInfoDTO;
import com.chinatower.receive.vo.dto.AndTuleVo;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 摄像机告警接收策略实现类
 * @Author: songxinfeng
 * @Date: 2024/8/6 10:21
 * @version release_v1.6.90
 */

@Component("cameraAlgorithmAlarmStrategy")
@Slf4j
public class CameraAlgorithmAlarmStrategy extends AlarmBaseStategy {

    @Autowired
    private GisUtil gisUtil;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private WarningOrderUtil warningOrderUtil;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ISuppressStrategyService strategyService;

    @Autowired
    private IAlarmSuppressConfigRepository configRepository;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Autowired
    private WarningOrderFlowService warningOrderFlowService;

    public static final String SQUARE_LEFT = "{";
    public static final String SQUARE_RIGHT = "}";

    public static final String BRACKET_LEFT = "(";
    public static final String BRACKET_RIGHT = ")";

    public static final String AND = " and ";

    public static final String SPACE = " ";

    public static final String IS_WU = "无";

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        return WarningSourceEnum.CAMERA_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource());
    }

    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("摄像机告警上报入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try {
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();

            // ###1、接收告警租户信息
            List<OrderCustomerVO> customers = getCustomers(receiveAlarmVO);
            //告警为水位告警，根据租户触发规则再次过滤租户
            if("1818128".equals(receiveAlarmVO.getAlarmType())){
                customers = getCustomersByRule(receiveAlarmVO,customers);
            }
            // 增加水位告警特别处理：筛选符合阈值规则的用户
            if(("1818127").equals(receiveAlarmVO.getAlarmType()) && !CollectionUtils.isEmpty(customers)){
                String jsonStr = (String) receiveAlarmVO.getFrontExtendField();
                log.info("frontExtendField:{}", jsonStr);
                customers = filterEligibleTriggerRuleCustomers(customers, jsonStr);
                if(!CollectionUtils.isEmpty(customers)){
                    if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
                        jsonStr = jsonStr.substring(Constants.ONE_NUM, jsonStr.length() - Constants.ONE_NUM);
                    }
                    Map<String, Object> frontExtendFieldMap = JSON.parseObject(jsonStr, Map.class);
                    //可以推送告警，将前端透传字段合并放入透传字段中
                    setFrontExtendFields(frontExtendFieldMap, receiveAlarmVO);
                }
            }
            if(CollectionUtils.isEmpty(customers)){
                log.info("未查询到租户信息，告警编码：{}", receiveAlarmDTO.getReceiveAlarmVO().getAlarmCode());
                return ;
            }
            // 更新租户信息以兼容历史版本
            warningOrderUtil.updateCustomerVersions(customers);
            receiveAlarmDTO.setCustomers(customers);
            log.info("匹配接收告警的租户信息：{}", JSON.toJSONString(customers));

            // ###2、处理告警的地址和经纬度数据
            getAddress(receiveAlarmVO);
            log.info("通过经纬度获取省市区乡镇编码:" + JSONObject.toJSONString(receiveAlarmVO));

            // 根据经纬度获取网格信息
            warningOrderUtil.setAlarmGridInfo(receiveAlarmDTO);

            // ###3、填充告警工单相关参数
            warningOrderUtil.populateAlarmWorkOrderParams(receiveAlarmVO);

            // ###4、处理告警工单、告警压制、运营研判
            List<ReceiveAlarmVO> alarms = cameraWarningOrderHandler(receiveAlarmDTO);
            if (CollectionUtils.isEmpty(alarms)) {
                log.info("告警工单处理失败，告警编码：{}", receiveAlarmVO.getAlarmCode());
                return;
            }


        } catch(Exception e) {
            log.info("接收摄像机告警时发生异常：" + e);
        }
    }

    /**
     * 水位告警过滤租户
     * @param receiveAlarmVO
     * @param list
     * @return
     * @throws Exception
     */
    public List<OrderCustomerVO> getCustomersByRule(ReceiveAlarmVO receiveAlarmVO,List<OrderCustomerVO> list ) throws Exception {
        List<OrderCustomerVO> realList = new ArrayList<>();
        log.info("getCustomersByRule-水位告警前端算法字段入参:{}",JSON.toJSONString(receiveAlarmVO.getFrontExtendField()));
        if (receiveAlarmVO.getFrontExtendField() != null  && receiveAlarmVO.getFrontExtendField() instanceof String) {
            String jsonStr = (String) receiveAlarmVO.getFrontExtendField();
            if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
                jsonStr = jsonStr.substring(Constants.ONE_NUM, jsonStr.length() - Constants.ONE_NUM);
            }
            Map<String, Object> frontExtendFieldMap = JSON.parseObject(jsonStr, Map.class);

            //所有并且关系的租户字段匹配情况
            List<AndTuleVo> allAndList = new ArrayList<>();
            for (OrderCustomerVO customer:list) {
                here1:
                if(!StringUtils.isEmpty(customer.getTriggerRule())){
                    log.info("getCustomersByRule-水位告警租户规则:{},租户:{}",JSON.toJSONString(customer.getTriggerRule()),customer.getTenantId());
                    //该租户并且关系的租户匹配情况
                    AndTuleVo andTuleVo = new AndTuleVo();
                    List<String> flagList = new ArrayList<>();
                    andTuleVo.setCustomerVO(customer);
                    TriggerRuleVo ruleVo = JSON.parseObject(customer.getTriggerRule(),TriggerRuleVo.class);
                    for (TriggerRuleVo.RangeRule rule:ruleVo.getRangeRule()) {
                        if(!StringUtils.isEmpty(rule.getField()) && (rule.getMin() != null || rule.getMax() != null)){
                            for (Map.Entry<String, Object> entry : frontExtendFieldMap.entrySet()) {
                                String key = entry.getKey();
                                String value = String.valueOf(entry.getValue());
                                if(rule.getField().equals(key)){
                                    //上报数值
                                    BigDecimal paramValue = new BigDecimal(value);
                                    //0:< 1:<=
                                    String minCompare = Constants.ZERO;
                                    //0:< 1:<=
                                    String maxCompare = Constants.ZERO;
                                    //范围最小值
                                    BigDecimal min = null;
                                    //范围最大值
                                    BigDecimal max = null;
                                    if(rule.getMin() != null && !StringUtils.isEmpty(rule.getMin().getValue()) && !StringUtils.isEmpty(rule.getMin().getSymbol())){
                                        min = new BigDecimal(rule.getMin().getValue());
                                        if(rule.getMin().getSymbol().contains("=")){
                                            minCompare =Constants.ONE;
                                        }
                                    }
                                    if(rule.getMax() != null && !StringUtils.isEmpty(rule.getMax().getValue()) && !StringUtils.isEmpty(rule.getMax().getSymbol())){
                                        max = new BigDecimal(rule.getMax().getValue());
                                        if(rule.getMax().getSymbol().contains("=")){
                                            maxCompare =Constants.ONE;
                                        }
                                    }
                                    if(min != null && max !=null){
                                        //大小范围都存在
                                        if(Constants.ZERO.equals(minCompare) && Constants.ZERO.equals(maxCompare)){
                                            //范围均不带等号
                                            if(min.compareTo(paramValue) < 0 && max.compareTo(paramValue) >0){
                                                if("or".equals(ruleVo.getRelation())){
                                                    //或者关系的将匹配成功的标记记录，直接将该租户成功放行，可以推送告警
                                                    realList.add(customer);
                                                    break here1;
                                                }else if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将所有匹配成功的标记记录，后边看是否全部为”0“  可以
                                                    flagList.add(Constants.ZERO);
                                                }
                                            }else{
                                                if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将匹配补上的记录一次，后边根据判断过滤  不可以
                                                    flagList.add(Constants.ONE);
                                                    break here1;
                                                }
                                            }
                                        }else if(Constants.ZERO.equals(minCompare) && Constants.ONE.equals(maxCompare)){
                                            //大范围带等号
                                            if(min.compareTo(paramValue) < 0 && max.compareTo(paramValue) >=0){
                                                if("or".equals(ruleVo.getRelation())){
                                                    //或者关系的将匹配成功的标记记录，直接将该租户成功放行，可以推送告警
                                                    realList.add(customer);
                                                    break here1;
                                                }else if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将所有匹配成功的标记记录，后边看是否全部为”0“  可以
                                                    flagList.add(Constants.ZERO);
                                                }
                                            }else{
                                                if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将匹配补上的记录一次，后边根据判断过滤  不可以
                                                    flagList.add(Constants.ONE);
                                                    break here1;
                                                }
                                            }
                                        }else if(Constants.ONE.equals(minCompare) && Constants.ZERO.equals(maxCompare)){
                                            //小范围带等号
                                            if(min.compareTo(paramValue) <= 0 && max.compareTo(paramValue) >0){
                                                if("or".equals(ruleVo.getRelation())){
                                                    //或者关系的将匹配成功的标记记录，直接将该租户成功放行，可以推送告警
                                                    realList.add(customer);
                                                    break here1;
                                                }else if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将所有匹配成功的标记记录，后边看是否全部为”0“  可以
                                                    flagList.add(Constants.ZERO);
                                                }
                                            }else{
                                                if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将匹配补上的记录一次，后边根据判断过滤  不可以
                                                    flagList.add(Constants.ONE);
                                                    break here1;
                                                }
                                            }
                                        }else if(Constants.ONE.equals(minCompare) && Constants.ONE.equals(maxCompare)){
                                            //大小范围都带等号
                                            if(min.compareTo(paramValue) <= 0 && max.compareTo(paramValue) >=0){
                                                if("or".equals(ruleVo.getRelation())){
                                                    //或者关系的将匹配成功的标记记录，直接将该租户成功放行，可以推送告警
                                                    realList.add(customer);
                                                    break here1;
                                                }else if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将所有匹配成功的标记记录，后边看是否全部为”0“  可以
                                                    flagList.add(Constants.ZERO);
                                                }
                                            }else{
                                                if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将匹配补上的记录一次，后边根据判断过滤  不可以
                                                    flagList.add(Constants.ONE);
                                                    break here1;
                                                }
                                            }
                                        }
                                        if(min.compareTo(paramValue) < 0 && max.compareTo(paramValue) >=0){
                                            if("or".equals(ruleVo.getRelation())){
                                                //或者关系的将匹配成功的标记记录，直接将该租户成功放行，可以推送告警
                                                realList.add(customer);
                                                break here1;
                                            }else if("and".equals(ruleVo.getRelation())){
                                                //并且关系的将所有匹配成功的标记记录，后边看是否全部为”0“  可以
                                                flagList.add(Constants.ZERO);
                                            }
                                        }else{
                                            if("and".equals(ruleVo.getRelation())){
                                                //并且关系的将匹配补上的记录一次，后边根据判断过滤  不可以
                                                flagList.add(Constants.ONE);
                                                break here1;
                                            }
                                        }
                                    }else if(min != null){
                                        if(Constants.ONE.equals(minCompare)){
                                            //小范围带等号
                                            if(min.compareTo(paramValue) <= 0){
                                                if("or".equals(ruleVo.getRelation())){
                                                    //或者关系的将匹配成功的标记记录，直接将该租户成功放行，可以推送告警
                                                    realList.add(customer);
                                                    break here1;
                                                }else if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将所有匹配成功的标记记录，后边看是否全部为”0“  可以
                                                    flagList.add(Constants.ZERO);
                                                }
                                            }else{
                                                if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将匹配补上的记录一次，后边根据判断过滤  不可以
                                                    flagList.add(Constants.ONE);
                                                    break here1;
                                                }
                                            }
                                        }else if(Constants.ZERO.equals(minCompare)){
                                            //小范围不带等号
                                            if(min.compareTo(paramValue) < 0){
                                                if("or".equals(ruleVo.getRelation())){
                                                    //或者关系的将匹配成功的标记记录，直接将该租户成功放行，可以推送告警
                                                    realList.add(customer);
                                                    break here1;
                                                }else if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将所有匹配成功的标记记录，后边看是否全部为”0“  可以
                                                    flagList.add(Constants.ZERO);
                                                }
                                            }else{
                                                if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将匹配补上的记录一次，后边根据判断过滤  不可以
                                                    flagList.add(Constants.ONE);
                                                    break here1;
                                                }
                                            }
                                        }
                                    }else if(max != null){
                                        if(Constants.ONE.equals(maxCompare)){
                                            //大范围带等号
                                            if(max.compareTo(paramValue) >= 0){
                                                if("or".equals(ruleVo.getRelation())){
                                                    //或者关系的将匹配成功的标记记录，直接将该租户成功放行，可以推送告警
                                                    realList.add(customer);
                                                    break here1;
                                                }else if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将所有匹配成功的标记记录，后边看是否全部为”0“  可以
                                                    flagList.add(Constants.ZERO);
                                                }
                                            }else{
                                                if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将匹配补上的记录一次，后边根据判断过滤  不可以
                                                    flagList.add(Constants.ONE);
                                                    break here1;
                                                }
                                            }
                                        }else if(Constants.ZERO.equals(maxCompare)){
                                            //大范围不带等号
                                            if(max.compareTo(paramValue) > 0){
                                                if("or".equals(ruleVo.getRelation())){
                                                    //或者关系的将匹配成功的标记记录，直接将该租户成功放行，可以推送告警
                                                    realList.add(customer);
                                                    break here1;
                                                }else if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将所有匹配成功的标记记录，后边看是否全部为”0“  可以
                                                    flagList.add(Constants.ZERO);
                                                }
                                            }else{
                                                if("and".equals(ruleVo.getRelation())){
                                                    //并且关系的将匹配补上的记录一次，后边根据判断过滤  不可以
                                                    flagList.add(Constants.ONE);
                                                    break here1;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            andTuleVo.setFlag(flagList);
                            allAndList.add(andTuleVo);
                        }
                    }

                }
            }
            //判断所有并且租户的字段匹配情况
            for (AndTuleVo vo:allAndList) {
                boolean index = true;
                if(!CollectionUtils.isEmpty(vo.getFlag())){
                    for (String flag:vo.getFlag()) {
                        if(StringUtils.isEmpty(flag) || Constants.ONE.equals(flag)){
                            index=false;
                        }
                    }
                    if(index){
                        realList.add(vo.getCustomerVO());
                    }
                }
            }
            if(!CollectionUtils.isEmpty(realList)){
                //可以推送告警，将前端透传字段合并放入透传字段中
                setFrontExtendFields(frontExtendFieldMap, receiveAlarmVO);
            }
        }
        return realList;
    }

    private List<OrderCustomerVO> filterEligibleTriggerRuleCustomers(List<OrderCustomerVO> customers, String frontExtendField){
        log.info("进入水位告警的阈值范围校验===，需要过滤的客户数量为：{}", customers.size());
        customers = customers.stream().filter(vo -> !StringUtils.isEmpty(vo.getTriggerRule())).collect(Collectors.toList());
        log.info("过滤掉的triggerRule为空后的客户数量为：{}", customers.size());

        // 获取上报的水位数据
        String waterLevel = JSONObject.parseObject(frontExtendField).getString("waterLevel");

        // 根据triggerRule筛选客户
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Iterator<OrderCustomerVO> customerIterator = customers.iterator();
        while (customerIterator.hasNext()){
            OrderCustomerVO vo = customerIterator.next();
            String triggerRule = vo.getTriggerRule();
            TriggerRuleJson triggerRuleJson = JSON.parseObject(triggerRule, TriggerRuleJson.class);
            triggerRule = this.rangeUnity(triggerRuleJson).toString();

            triggerRule = triggerRule.replace("{waterLevel}", waterLevel);
            if(triggerRule.contains("and")){
                triggerRule = triggerRule.replace("and", "&&");
            }
            log.info("triggerRule:{}" , triggerRule);
            try {
                Object result = engine.eval(triggerRule);
                log.info("执行结果为：{}", result);
                if(result.toString().equals("false")){
                    customerIterator.remove();
                }
            } catch (ScriptException e) {
                log.info("执行告警阈值校验异常：{}", e);
            }
        }
        log.info("过滤掉不符合客户设置的阈值的后的客户数量为：{}", customers.size());
        return customers;
    }

    /**
     * 构建入参
     * @param triggerRuleJson 范围  与或
     * @return
     */
    private StringBuilder rangeUnity(TriggerRuleJson triggerRuleJson){
        List<RangeJson> rangeRule = triggerRuleJson.getRangeRule();
        StringBuilder sb = new StringBuilder();
        sb.append(BRACKET_LEFT);
        for (RangeJson rangeJson : rangeRule) {
            if (sb.length() > 1) {
                sb.append(SPACE).append(triggerRuleJson.getRelation()).append(SPACE);
            }
            sb.append(BRACKET_LEFT);
            if (null == rangeJson.getMin() && null == rangeJson.getMax()) {
                return null;
            }
            if (null != rangeJson.getMin() && com.chinatower.common.core.utils.StringUtils.isNotEmpty(rangeJson.getMin().getSymbol()) && !IS_WU.equals(rangeJson.getMin().getSymbol())) {
                sb.append(rangeJson.getMin().getValue())
                        .append(rangeJson.getMin().getSymbol())
                        .append(SQUARE_LEFT)
                        .append(rangeJson.getField()).append(SQUARE_RIGHT);
            }
            if (sb.length() > DeviceConstants.ONE_NUM) {
                if (null != rangeJson.getMax() && com.chinatower.common.core.utils.StringUtils.isNotEmpty(rangeJson.getMax().getSymbol()) && !IS_WU.equals(rangeJson.getMax().getSymbol())) {
                    if (null != rangeJson.getMin()) {
                        sb.append(AND);
                    }
                    sb.append(SQUARE_LEFT)
                            .append(rangeJson.getField())
                            .append(SQUARE_RIGHT)
                            .append(rangeJson.getMax().getSymbol())
                            .append(rangeJson.getMax().getValue());
                }
            } else {
                if (null != rangeJson.getMax()) {
                    sb.append(SQUARE_LEFT).append(rangeJson.getField()).append(SQUARE_RIGHT).append(rangeJson.getMax().getSymbol()).append(rangeJson.getMax().getValue());
                }
            }
            sb.append(BRACKET_RIGHT);
        }
        sb.append(BRACKET_RIGHT);
        log.info("rangeUnity =="+sb);
        return sb;

    }

    @Override
    public List<OrderCustomerVO> getCustomers(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        String deviceCode = receiveAlarmVO.getDeviceCode();
        String alarmSource = receiveAlarmVO.getAlarmSource();
        String alarmType = String.valueOf(receiveAlarmVO.getAlarmType());
        //告警类型以18开头--告警来源为4，代表设备前端告警，过滤订购该设备的客户
        if (alarmSource.equals(AlarmConstants.ALARM_SOURCE_4) && !StringUtils.isEmpty(deviceCode)) {
            // 增加告警类型限制
            AlarmTypeInfoDTO typeInfo = deviceRepository.getAlarmTypeInfoById(alarmType);
            if(Objects.isNull(typeInfo)){
                log.info("摄像机告警下没有该告警类型{}，告警编码：{}", alarmType,receiveAlarmVO.getAlarmCode());
                return Collections.emptyList();
            }
            // 摄像机告警告警标题为空时进行补偿
            if(StringUtils.isEmpty(receiveAlarmVO.getAlarmBody())){
                receiveAlarmVO.setAlarmBody(typeInfo.getTypeName());
            }
            return deviceRepository.getCustomersByDevice(deviceCode, null,receiveAlarmVO);
        }
        return Collections.emptyList();
    }

    @Override
    public void getAddress(ReceiveAlarmVO receiveAlarmVO) {
        this.setAlarmLocationAndAddress(receiveAlarmVO);
    }

    /**
     * 处理摄像机告警工单，包括告警压制和运营研判
     * @param receiveAlarmDTO 接收告警的数据传输对象
     * @return 处理后的告警对象列表
     */
    private List<ReceiveAlarmVO> cameraWarningOrderHandler(ReceiveAlarmDTO receiveAlarmDTO) {
        log.info("生成摄像机告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        List<ReceiveAlarmVO> successDatas = new ArrayList<>();
        try {
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
            List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();

            ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
            String alarmTime = receiveAlarmDTO.getReceiveAlarmVO().getAlarmTime();
            receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));

            // 接收告警集合
            List<ReceiveAlarmVO> alarms = new ArrayList<>();
            // 运营研判告警集合
            List<ReceiveAlarmVO> operations = new ArrayList<>();
            // 告警压制集合
            List<ReceiveAlarmVO> intercepts = new ArrayList<>();

            // 告警压制开关是否开启
            String isIntercept = warningOrderUtil.getInterceptSwitch();
            List<String> tenantIdList = customers.stream().map(OrderCustomerVO::getTenantId).collect(Collectors.toList());
            //联合告警压制需求代码合并
            Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap = getAlarmSuppressStrategy(receiveAlarmVO, customers, isIntercept);
            //获取需要特殊处理行业版本
            List<SysDictDataResponse> dictList = warningOrderUtil.getDict(Constants.APP_VER_CODE_TOPIC);
            //先把涉及的租户的雨天告警压制配置数据查询出来,防止后面循环查询
            Map<String, AlarmSuppressConfigDTO> configDTOMap = getAlarmSuppressConfig(customers);

            //在扩展字段中设置告警原因
            Map<String, Object> extendFieldData = handleExtendedFields(receiveAlarmVO, customers, tenantIdList);

            for (OrderCustomerVO customerObj : customers) {
                String[] appVers = customerObj.getAppVerCode().split(",");
                for (String appVer : appVers) {
                    //构建告警对象
                    ReceiveAlarmVO alarmVo = buildAlarmVo(receiveAlarmVO, customerObj, appVer,
                            (String) extendFieldData.get(Constants.VEHICLE_NUMBER),
                            (List<CheckpointVehicleLicensePlate>) extendFieldData.get(Constants.LICENSE_PLATES),
                            (String) extendFieldData.get(Constants.BLACK_WHITE_JSON));

                    //处理特殊行业版本
                    if (warningOrderUtil.judgeSpecialAppVer(dictList, alarmVo)) {
                        continue;
                    }

                    //判断并处理告警压制
                    if (this.judgeIntercept(alarmVo, isIntercept, strategyMap, configDTOMap, intercepts)) {
                        continue;
                    }

                    //处理运营研判,首先判断是否开启研判，并行研判
                    if (warningOrderUtil.doOperate(alarmVo, alarms, operations)) {
                        continue;
                    }
                    if(alarmVo != null && StringUtils.isEmpty(alarmVo.getJudgeType())){
                        alarmVo.setJudgeType(Constants.JUDGE_TYPE_COMMON);
                    }
                    alarms.add(alarmVo);
                }
            }
            receiveAlarmVODB.setIntercepts(intercepts);
            // 批量保存告警信息
            successDatas = batchCreateWarningOrder(alarms, receiveAlarmVODB);
            receiveAlarmVODB.setList(alarms);
            receiveAlarmVODB.setOperations(operations);
            String originalJson = JSON.toJSONString(receiveAlarmVODB.getList());
            // 告警信息入库
            commonService.saveAlarm(receiveAlarmVODB, alarmTime, Constants.FOUR, originalJson, receiveAlarmDTO.getAlarmPointList());
            // 回填研判信息
            successDatas.addAll(operations);

            // ###5、推送告警消息
            try{
                commonService.batchPushMessage(alarms);
                commonService.batchPushMessage(operations);
            }catch(Exception e){
                log.error("批量推送告警消息时发生异常：" + e);
            }
        } catch(Exception e) {
            log.info("生成摄像机告警事件异常：", e);
        }
        return successDatas;
    }

    private Map<String, Object> handleExtendedFields(ReceiveAlarmVO receiveAlarmVO, List<OrderCustomerVO> customers, List<String> tenantIdList) {
        Map<String, Object> resultMap = new HashMap<>();
        List<CheckpointVehicleLicensePlate> licensePlates = null;
        String vehicleNumber = null;
        String blackWhiteJson = null;
        //从字典获取卡口告警类型值
        String alarmTypeValue = getAlarmTypeValue();
        if (receiveAlarmVO.getAlarmType().equals(alarmTypeValue)) {
            if(receiveAlarmVO.getExtendField() != null && !Constants.ONE_BLANK.equals(JSON.toJSONString(receiveAlarmVO.getExtendField()))
                    && !Constants.TWO_BLANK.equals(JSON.toJSONString(receiveAlarmVO.getExtendField()))){
                List<Map> extendFieldList = JSON.parseArray(JSON.toJSONString(receiveAlarmVO.getExtendField()), Map.class);

                List<String> industryCodeList = customers.stream().map(OrderCustomerVO::getIndustryCode).collect(Collectors.toList());
                //获取上报的卡口告警中的车牌号
                vehicleNumber = extendFieldList.stream().flatMap(map -> ((List<Map<String, String>>) map.get(Constants.PARAM_ATTR_LIST)).stream())
                        .filter(attrMap -> "carNumber".equals(attrMap.get(Constants.PARAM_ATTR_TYPE)))
                        .map(attrMap -> attrMap.get(Constants.PARAM_ATTR_VALUE)).findFirst().orElse("");
                Map paramMap = new HashMap();
                paramMap.put(Constants.PARAM_TENANT_ID, tenantIdList);
                paramMap.put(Constants.PARAM_INDUSTRY_CODE, industryCodeList);
                paramMap.put(Constants.VEHICLE_NUMBER, vehicleNumber);
                //查询卡口车牌库
                licensePlates = warningOrderMapper.queryLicensePlateList(paramMap);
                //获取算法配置-前端算法配置中勾选的黑白名单
                paramMap.put(Constants.PARAM_DEVICE_CODE, receiveAlarmVO.getDeviceCode());
                paramMap.put(Constants.PARAM_ALARM_TYPE, receiveAlarmVO.getAlarmType());
                List<CheckpointVehicleLicensePlate> blackWhitePlateList = warningOrderMapper.getBlackWhiteJson(paramMap);
                //处理成json格式，如：{"white":[{"vehicleNumber":"豫A1923X","validPeriod":"00:00-23:59"}],"black":[{"vehicleNumber":"辽B56522","validPeriod":"00:00-23:59"}]}
                if(!CollectionUtils.isEmpty(blackWhitePlateList)){
                    List<Map<String, Object>> blackList = blackWhitePlateList.stream()
                            .filter(plate -> String.valueOf(plate.getBlackOrWhite()).equals(Constants.ONE))
                            .map(plate -> toPlateMap(plate))
                            .collect(Collectors.toList());
                    List<Map<String, Object>> whiteList = blackWhitePlateList.stream()
                            .filter(plate -> plate.getBlackOrWhite() == Constants.TWO_NUM)
                            .map(plate -> toPlateMap(plate))
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(blackList) || !CollectionUtils.isEmpty(whiteList)) {
                        Map<String, List<Map<String, Object>>> result = new HashMap<>();
                        if(!CollectionUtils.isEmpty(blackList)){
                            result.put("black", blackList);
                        }
                        if(!CollectionUtils.isEmpty(whiteList)){
                            result.put("white", whiteList);
                        }
                        blackWhiteJson = JSON.toJSONString(result);
                    } else {
                        blackWhiteJson = null;
                    }
                } else {
                    blackWhiteJson = null;
                }
            }
            //取前端告警识别结果frontExtendField,整合到扩展字段extendField中
            if (receiveAlarmVO.getFrontExtendField() != null  && receiveAlarmVO.getFrontExtendField() instanceof String) {
                String jsonStr = (String) receiveAlarmVO.getFrontExtendField();
                if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
                    jsonStr = jsonStr.substring(Constants.ONE_NUM, jsonStr.length() - Constants.ONE_NUM);
                }
                Map<String, Object> frontExtendFieldMap = JSON.parseObject(jsonStr, Map.class);
                String vehicleLitimg = (String) frontExtendFieldMap.get("vehicleLitimg");
                //其中将‘车牌小图’放入原图字段中
                if (!StringUtils.isEmpty(vehicleLitimg)) {
                    String fileImgUrlSrc = receiveAlarmVO.getOriginalPictureUrl();
                    String localNode =receiveAlarmVO.getLocalNode();
                    if(!StringUtils.isEmpty(localNode)){
                        String url;
                        //中心节点文件地址无需拼接属地编码
                        if("10000000001".equals(localNode)){
                            url = vehicleLitimg;
                        } else {
                            url = getUrl(receiveAlarmVO.getLocalNode(), vehicleLitimg);
                        }
                        if (!StringUtils.isEmpty(fileImgUrlSrc)) {
                            receiveAlarmVO.setOriginalPictureUrl(fileImgUrlSrc + Constants.COMMA + url);
                        } else {
                            receiveAlarmVO.setOriginalPictureUrl(url);
                        }
                    }
                }
                setFrontExtendFields(frontExtendFieldMap, receiveAlarmVO);
            }
        }
        resultMap.put(Constants.VEHICLE_NUMBER, vehicleNumber);
        resultMap.put(Constants.LICENSE_PLATES, licensePlates);
        resultMap.put(Constants.BLACK_WHITE_JSON, blackWhiteJson);
        return resultMap;
    }

    private ReceiveAlarmVO buildAlarmVo(ReceiveAlarmVO receiveAlarmVO, OrderCustomerVO customerObj, String appVer,
                                        String vehicleNumber, List<CheckpointVehicleLicensePlate> licensePlates, String blackWhiteJson) {
        ReceiveAlarmVO alarmVo = new ReceiveAlarmVO();
        customerObj.setDeviceCode(receiveAlarmVO.getDeviceCode());
        BeanUtils.copyProperties(receiveAlarmVO, alarmVo);
        BeanUtils.copyProperties(customerObj, alarmVo);
        alarmVo.setOperationNode(customerObj.getOperationNode());
        alarmVo.setParallelOperationNode(customerObj.getParallelOperationNode());
        alarmVo.setAlarmNode(getPlatformNode());
        alarmVo.setOperationNodeName(deviceRepository.getPlatformNameByPlatformCode(alarmVo.getAlarmNode()));
        if(!StringUtils.isEmpty(customerObj.getOperationDept())){
            alarmVo.setOperationDept(customerObj.getOperationDept()+Constants.COMMA);
        }
        if(!StringUtils.isEmpty(customerObj.getParallelOperationDept())){
            alarmVo.setParallelOperationDept(customerObj.getParallelOperationDept()+Constants.COMMA);
        }
        //更改工单编码生成规则 add by hued 20211103
        alarmVo.setWarningOrderId(IPKUtils.getWarnOrderId(alarmVo.getTenantId(), alarmVo.getAlarmTime()));
        alarmVo.setAppVerCode(appVer);
        alarmVo.setNotifyType(Constants.YES_FLAG);
        //设置告警原因 add by v1.6.40
        if(!CollectionUtils.isEmpty(licensePlates)){
            //通过租户+行业过滤
            List<CheckpointVehicleLicensePlate> filteredLicensePlates = licensePlates.stream()
                    .filter(licensePlate -> licensePlate.getTenantId().equals(customerObj.getTenantId())
                            && licensePlate.getIndustryCode().equals(customerObj.getIndustryCode())
                            && licensePlate.getAppVerCode().equals(appVer))
                    .collect(Collectors.toList());
            /**
             * 判断上报告警的车牌号在卡口车牌库中是否存在
             * 1、如果不存在车牌库中，直接上报告警，不显示告警原因
             * 2、如果存在车牌库中，继续走判断黑白名单的逻辑，如下：
             */
            if(!CollectionUtils.isEmpty(filteredLicensePlates)){  //代表车牌在车牌库存在
                //判断车牌是在黑名单、还是白名单、还是黑白都在
                boolean hasBlack = filteredLicensePlates.stream()
                        .anyMatch(licensePlate -> licensePlate.getBlackOrWhite() == Constants.INDEXASC_ONE);
                boolean hasWhite = filteredLicensePlates.stream()
                        .anyMatch(licensePlate -> licensePlate.getBlackOrWhite() == Constants.TWO_NUM);
                List<Map> extendFieldList = JSON.parseArray(JSON.toJSONString(alarmVo.getExtendField()), Map.class);

                if(!StringUtils.isEmpty(blackWhiteJson) && (hasBlack || hasWhite)) {//代表车牌在黑名单或白名单中
                    //分别获取在黑名单中勾选的车牌、白名单中勾选的车牌
                    //Map<String, List<BlackWhiteVO>> map1 = JSON.parseObject(blackWhiteJson, Map.class);
                    Map<String, List<BlackWhiteVO>> filteredMap = JSON.parseObject(blackWhiteJson, new TypeReference<Map<String, List<BlackWhiteVO>>>() {});
                    //进行版本过滤
                    Map<String, List<BlackWhiteVO>> map = new HashMap<>();
                    filteredMap.forEach((key, blackWhiteList) -> {
                        List<BlackWhiteVO> filteredList = new ArrayList<>();
                        blackWhiteList.forEach(blackWhiteVO -> {
                            if (appVer.equals(blackWhiteVO.getAppVerCode())) {
                                filteredList.add(blackWhiteVO);
                            }
                        });
                        map.put(key, filteredList); // 更新过滤后的List<BlackWhiteVO>到新的Map中
                    });
                    //黑名单
                    List<BlackWhiteVO> resultBlackList = JSON.parseArray(JSON.toJSONString(map.get("black")),BlackWhiteVO.class);
                    //白名单
                    List<BlackWhiteVO> resultWhiteList = JSON.parseArray(JSON.toJSONString(map.get("white")),BlackWhiteVO.class);

                    //黑白名单都未勾选 --不显示告警原因
                    if(CollectionUtils.isEmpty(resultBlackList) && CollectionUtils.isEmpty(resultWhiteList)){
                        alarmVo.setExtraField(JSON.toJSONString(extendFieldList));
                        return alarmVo;
                    }
                    /**
                     * 黑名单开启并勾选，且在生效时间段内，上报+告警原因：在黑名单中
                     * 黑名单开启并勾选，不在生效时间段内，上报+告警原因：不在白名单中
                     * 黑名单开启未勾选，上报+告警原因：不在白名单中
                     */
                    if(!CollectionUtils.isEmpty(resultBlackList)){
                        boolean existsInBlackList = resultBlackList.stream()
                                .anyMatch(item -> item.getVehicleNumber().equals(vehicleNumber));
                        if(existsInBlackList){  //代表车牌在黑名单中已勾选
                            String validPeriod = null;
                            for(BlackWhiteVO blackWhiteVO : resultBlackList){
                                if (blackWhiteVO.getVehicleNumber().equals(vehicleNumber)) {
                                    validPeriod = blackWhiteVO.getValidPeriod();
                                    break;
                                }
                            }
                            //判断是否在生效时间段内
                            boolean isWithinValidPeriod = ifExistsInVaildPeriod(alarmVo, validPeriod);
                            if(isWithinValidPeriod){
                                //设置‘告警原因：在黑名单中’到extendField中
                                alarmVo.setExtraField(getAlarmReasonField(extendFieldList, Constants.ONE));
                                return alarmVo;
                            }
                        }
                    }
                    /**
                     * 白名单开启并勾选，且在生效时间段内，不上报过滤
                     * 白名单开启并勾选，不在生效时间段内，上报+告警原因：不在白名单中
                     * 白名单开启未勾选，上报+告警原因：不在白名单中
                     */
                    if(!CollectionUtils.isEmpty(resultWhiteList)){
                        boolean existsInWhiteList = resultWhiteList.stream()
                                .anyMatch(item -> item.getVehicleNumber().equals(vehicleNumber));
                        if(existsInWhiteList){  //代表车牌在白名单中已勾选
                            String validPeriod = null;
                            for(BlackWhiteVO blackWhiteVO : resultWhiteList){
                                if (blackWhiteVO.getVehicleNumber().equals(vehicleNumber)) {
                                    validPeriod = blackWhiteVO.getValidPeriod();
                                    break;
                                }
                            }
                            //判断是否在生效时间段内
                            boolean isWithinValidPeriod = ifExistsInVaildPeriod(alarmVo, validPeriod);
                            if(isWithinValidPeriod){
                                throw new CustomException("车牌号："+ vehicleNumber + "在白名单中，直接过滤不上报告警！");
                            }
                        }
                    }
                    //设置‘告警原因：不在白名单中’
                    alarmVo.setExtraField(getAlarmReasonField(extendFieldList, Constants.TWO));
                    return alarmVo;
                }
            }
        }
        // 获取雷达告警和AI告警和摄像机告警的透传字段  报文中透传字段按照接口文档传json字符串
        if(alarmVo.getExtendField() != null && !Constants.ONE_BLANK.equals(JSON.toJSONString(alarmVo.getExtendField()))
                && !Constants.TWO_BLANK.equals(JSON.toJSONString(receiveAlarmVO.getExtendField()))){
            alarmVo.setExtraField(JSON.toJSONString(alarmVo.getExtendField()));
        }
        // 预置位定位法>特征库定位法>摄像机标定定位法>定位到摄像机  判断是否符合预置位定位
        if(!StringUtils.isEmpty(alarmVo.getPoints()) && Constants.ONE.equals(alarmVo.getAlarmSource())){
            dealPresetLocation(alarmVo);
        }

        //二次研判转化,去除原有判定逻辑，若没有上报是否二次研判，则默认二次研判为否。
        if(StringUtils.isEmpty(alarmVo.getIsSecondJudge()) && Constants.FOUR.equals(alarmVo.getAlarmSource())){
            alarmVo.setIsSecondJudge(Constants.ZERO);
        }
        return alarmVo;
    }

    private List<ReceiveAlarmVO> batchCreateWarningOrder(List<ReceiveAlarmVO> alarms, ReceiveAlarmDTO receiveAlarmVODB) {
        List<AlarmOriginalPointVO> alarmPointList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(alarms)) {
            log.info("批量保存告警信息入参：{}", JSON.toJSONString(alarms));
            // 创建流程
            List<Map> recordList = warningOrderFlowService.createRecord(alarms);

            // 回填当前环节相关字段
            warningOrderFlowService.setWarningLinkInfo(alarms,recordList);
            /*alarms.forEach(item -> {
                if(!WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(item.getAlarmSource())){
                    setConfidenceLevelAndAddAlarmPoint(item,alarmPointList);
                }
            });*/
            //是否有二次识别 默认否
            warningOrderUtil.setSecondConfig(alarms);
            receiveAlarmVODB.setAlarmPointList(alarmPointList);
            return alarms;
        }
        return new ArrayList<>();
    }
    private boolean ifExistsInVaildPeriod(ReceiveAlarmVO alarmVO, String validPeriod) {
        boolean isWithinValidPeriod = false;
        String alarmTime = alarmVO.getAlarmTime();
        String[] timeRanges = validPeriod.split(Constants.COMMA);
        for(String timeRange : timeRanges){
            String[] times = timeRange.split("-");
            String startTimeStr = times[0].trim();
            String endTimeStr = times[1].trim();
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);
            LocalTime alarmLocalTime = LocalTime.parse(alarmTime.substring(11, 19));
            if (alarmLocalTime.isAfter(startTime) && alarmLocalTime.isBefore(endTime)) {
                isWithinValidPeriod = true;
                break;
            }
        }
        return isWithinValidPeriod;
    }

    private String getAlarmReasonField(List<Map> extendFieldList, String flag) {
        //获取到extendField的attrList
        List<Map<String, String>> attrList = extendFieldList.stream()
                .findFirst()
                .map(item -> (List<Map<String, String>>) item.get(Constants.PARAM_ATTR_LIST))
                .orElse(new ArrayList<>());
        Map<String, String> attrMap = new HashMap<>();
        attrMap.put(Constants.PARAM_ATTR_TYPE, "alarmCause");
        attrMap.put(Constants.PARAM_ATTR_VALUE, Constants.ONE.equals(flag) ? "在黑名单中" : "不在白名单中");
        attrList.add(attrMap);
        //更新extendFieldList中的attrList
        extendFieldList.stream()
                .findFirst()
                .ifPresent(item -> item.put(Constants.PARAM_ATTR_LIST, attrList));
        return JSON.toJSONString(extendFieldList);
    }

    /**
     * 获取当前环境节点
     * @return
     */
    private String getPlatformNode() {
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(Constants.PLATFORM_CODE);
        // 获取属地化节点 （来自：铁塔系统管理-参数配置）
        AjaxResult configInfo = systemFeignClient.getConfigInfo(sysConfig);
        String sdhNode = null;
        if(Objects.nonNull(configInfo.get(Constants.RESULT_DATA))){
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(configInfo.get(Constants.RESULT_DATA)));
            sdhNode = jsonObject.getString(Constants.PARAM_CONFIG_VALUE);
        }
        return sdhNode;
    }

    /**
     * 获取联合告警压制策略
     *
     * @return
     */
    private Map<String, List<VmAlarmSuppressStrategyVO>> getAlarmSuppressStrategy(ReceiveAlarmVO receiveAlarmVO, List<OrderCustomerVO> customers, String isIntercept) {
        // 告警压制开关是否开启
        if (Constants.YES_FLAG.equals(isIntercept)) {
            //联合告警压制需求代码合并
            List<VmAlarmSuppressStrategyVO> strategyVoList = strategyService.queryStrategyList(receiveAlarmVO, customers);
            if (strategyVoList != null && !strategyVoList.isEmpty()) {
                return strategyVoList.stream().filter(item -> item.getTenantId() != null && !"".equals(item.getTenantId())).collect(Collectors.groupingBy(temp -> temp.getTenantId()+temp.getIndustryCode()));
            }
        }
        return null;
    }

    /**
     * 处理每个租户对该算法配置的预置位定位
     * @param alarmVo
     */
    private void dealPresetLocation(ReceiveAlarmVO alarmVo) {
        try {
            List<AlgorithmMonitorAreaVO> areaVOList = JSON.parseArray(alarmVo.getPoints(),AlgorithmMonitorAreaVO.class);
            //如果算法任务只有一个电子围栏并且设置了定位点，则定位到定位点，否则预置位定位法不生效。
            if(!CollectionUtils.isEmpty(areaVOList) && areaVOList.size() == 1 && ObjectUtil.isNotNull(areaVOList.get(0).getLatitude()) && ObjectUtil.isNotNull(areaVOList.get(0).getLongitude())){
                alarmVo.setLongitude(areaVOList.get(0).getLongitude());
                alarmVo.setLatitude(areaVOList.get(0).getLatitude());
                // 通过经纬度重新获取街道信息以及地址
                gisUtil.getAddressFromAmap(alarmVo,null);
            }
        }catch (Exception e){
            log.error("预置位定位异常: 入参：{} , 异常信息:{}",JSON.toJSONString(alarmVo), e);
        }
    }

    /**
     * 获取告警压制配置
     *
     * @param customers
     * @return
     */
    private Map<String, AlarmSuppressConfigDTO> getAlarmSuppressConfig(List<OrderCustomerVO> customers) {
        List<String> tenantIdsList = customers.stream().collect(Collectors.mapping(OrderCustomerVO::getTenantId, Collectors.toList()));
        if (!CollectionUtils.isEmpty(tenantIdsList)) {
            List<AlarmSuppressConfigDTO> configDTOList = configRepository.querySuppressConfigByTenantIds(tenantIdsList);
            return configDTOList.stream().collect(Collectors.toMap(k -> k.getTenantId() + "_" + k.getDeviceCode(), configDTO -> configDTO));
        }
        return null;
    }

    /**
     * 获取字典配置中告警类型值
     * @return
     */
    private String getAlarmTypeValue() {
        String alarmTypeValue = Constants.ALARM_TYPE_VALUE;
        Result<List<SysDictDataResponse>> dictDataType = systemFeignClient.dictDataType(Constants.CHECKPOINT_ALARM_TYPES);
        if (dictDataType != null && dictDataType.getData() != null && !dictDataType.getData().isEmpty()) {
            alarmTypeValue = dictDataType.getData().get(0).getDictValue();
        }
        return alarmTypeValue;
    }

    /**
     * 将frontExtendField值放入扩展字段中
     * @param frontExtendFieldMap
     * @param receiveAlarmVO
     */
    private void setFrontExtendFields(Map<String, Object> frontExtendFieldMap, ReceiveAlarmVO receiveAlarmVO) {
        //获取车辆分类、车牌颜色、车辆颜色枚举值
        Map<String, String> dictMap = getDictMap(systemFeignClient.dictDataType(Constants.SYS_VEHICLE_TYPES));
        Map<String, String> vColorDictMap = getDictMap(systemFeignClient.dictDataType(Constants.VEHICLE_COLOR));
        Map<String, String> vnColorDictMap = getDictMap(systemFeignClient.dictDataType(Constants.VEHICLE_NUM_COLOR));
        Map<String, String> waterQualityDictMap = getDictMap(systemFeignClient.dictDataType(Constants.WATER_QUALITY));
        Map<String, String> wlExtendedFieldsDictMap = getDictMap(systemFeignClient.dictDataType(Constants.WATER_LEVEL_EXTENDED_FIELDS));
        List<Map> extendFieldList = new ArrayList<>();
        if(receiveAlarmVO.getExtendField() != null && !Constants.ONE_BLANK.equals(JSON.toJSONString(receiveAlarmVO.getExtendField()))
                && !Constants.TWO_BLANK.equals(JSON.toJSONString(receiveAlarmVO.getExtendField()))){
            extendFieldList = JSON.parseArray(JSON.toJSONString(receiveAlarmVO.getExtendField()), Map.class);
        }
        List<Map<String, Object>> attrList = convertToAttrList(frontExtendFieldMap, dictMap, vColorDictMap, vnColorDictMap,waterQualityDictMap,wlExtendedFieldsDictMap);
        if (CollectionUtils.isEmpty(extendFieldList)) {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("attrList", attrList);
            extendFieldList.add(fieldMap);
        } else {
            extendFieldList.forEach(fieldList -> {
                List<Map> fieldAttrList = (List<Map>) fieldList.get("attrList");
                if (CollectionUtils.isEmpty(fieldAttrList)) {
                    fieldAttrList = new ArrayList<>();
                }
                fieldAttrList.addAll(attrList);
            });
        }
        log.info("卡口告警-setFrontExtendFields:{}",JSON.toJSONString(extendFieldList));
        receiveAlarmVO.setExtendField(extendFieldList);
    }

    /**
     * 获取字典map
     * @param dictDataType
     * @return
     */
    private Map<String, String> getDictMap(Result<List<SysDictDataResponse>> dictDataType) {
        return dictDataType.getData().stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel));
    }

    private List<Map<String, Object>> convertToAttrList(Map<String, Object> frontExtendFieldMap, Map<String, String> dictMap,
                                                        Map<String, String> vColorDictMap, Map<String, String> vnColorDictMap,Map<String, String> waterQualityDictMap,
                                                        Map<String, String> wlExtendedFieldsDictMap) {
        return frontExtendFieldMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> attrMap = new HashMap<>();
                    attrMap.put(Constants.PARAM_ATTR_TYPE, entry.getKey());
                    attrMap.put(Constants.PARAM_ATTR_VALUE, getAttrValue(entry.getValue(), dictMap, vColorDictMap, vnColorDictMap,waterQualityDictMap,wlExtendedFieldsDictMap));
                    return attrMap;
                })
                .collect(Collectors.toList());
    }

    /**
     * 匹配车辆分类、车牌颜色、车辆颜色字典值
     * @param value
     * @param dictMap 车辆分类
     * @param vColorDictMap 车辆颜色
     * @param vnColorDictMap 车牌颜色
     *
     * @return
     */
    private Object getAttrValue(Object value, Map<String, String> dictMap, Map<String, String> vColorDictMap, Map<String, String> vnColorDictMap,Map<String, String> waterQualityDictMap, Map<String, String> wlExtendedFieldsDictMap) {
        String dictValue = dictMap.get(value);
        if (dictValue != null) {
            return dictValue;
        }
        String vColorValue = vColorDictMap.get(value);
        if (vColorValue != null) {
            return vColorValue;
        }
        String vnColorValue = vnColorDictMap.get(value);
        if (vnColorValue != null) {
            return vnColorValue;
        }
        String waterQualityValue = waterQualityDictMap.get(value);
        if (vnColorValue != null) {
            return waterQualityValue;
        }
        String wlExtendedFields = wlExtendedFieldsDictMap.get(value);
        if (wlExtendedFields != null) {
            return wlExtendedFields;
        }
        return value;
    }

    public Map<String, Object> toPlateMap(CheckpointVehicleLicensePlate plate) {
        Map<String, Object> plateMap = new HashMap<>();
        plateMap.put("vehicleNumber", plate.getVehicleNumber());
        plateMap.put("validPeriod", plate.getValidPeriod());
        plateMap.put("appVerCode", plate.getAppVerCode());
        return plateMap;
    }

    /**
     * 地址拼接节点编号
     * @param accessNode
     * @param url
     * @return
     */
    public String getUrl(String accessNode,String url){
        if(StringUtils.isEmpty(url)){
            return url;
        }
        String[] urls = url.split(Constants.COMMA);
        String resUrl="";
        for (String realUrl:urls) {
            if(realUrl.contains("http") || realUrl.contains("https")){
                //将属地编码拼接在域名之后
                int thirdSlashIndex = realUrl.indexOf('/', realUrl.indexOf('/', realUrl.indexOf('/') + 1) + 1);
                // 拼接字符串，在第三个斜杠之后
                StringBuilder result = new StringBuilder(realUrl.substring(0, thirdSlashIndex + 1));
                result.append(accessNode+"/");
                result.append(realUrl.substring(thirdSlashIndex + 1));
                realUrl = result.toString();
            }else{
                realUrl = "/"+accessNode+realUrl;
            }
            if(StringUtils.isEmpty(resUrl)){
                resUrl = realUrl;
            }else{
                resUrl +=Constants.COMMA+realUrl;
            }
        }
        return resUrl;
    }

    @Override
    public String getStrategyName() {
        return AlarmStrategyEnum.CAMERA_ALARM.getAlarmStrategyBean();
    }
}
