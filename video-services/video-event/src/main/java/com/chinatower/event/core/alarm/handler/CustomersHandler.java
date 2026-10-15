package com.chinatower.event.core.alarm.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constant.AlarmConstants;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.AlarmTypeInfoDTO;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.vo.OrderCustomerVO;
import com.chinatower.event.vo.RadarParam;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.OrderConstants;
import com.chinatower.platform.entity.device.RangeJson;
import com.chinatower.platform.entity.device.TriggerRuleJson;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description 处理接收告警的租户信息
 * @Author guyuefei
 * @Date 2022/7/12
 * @Version release_v1.5
 **/
@Slf4j
@Component("customersHandler")
@Scope("prototype")
public class CustomersHandler  extends AbstractHandler{

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    public static final String SQUARE_LEFT = "{";
    public static final String SQUARE_RIGHT = "}";

    public static final String BRACKET_LEFT = "(";
    public static final String BRACKET_RIGHT = ")";

    public static final String AND = " and ";

    public static final String OR = " or ";

    public static final String SPACE = " ";

    public static final String IS_WU = "无";

    /**
     * 执行逻辑
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("处理接收告警的租户信息入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
            String deviceCode = receiveAlarmVO.getDeviceCode();
            String alarmSource = receiveAlarmVO.getAlarmSource();
            String localNode = receiveAlarmVO.getLocalNode();
            String algorithmCode = receiveAlarmVO.getAlgorithmCode();
            String taskId = receiveAlarmVO.getTaskId();
            String alarmType = String.valueOf(receiveAlarmVO.getAlarmType());

            List<OrderCustomerVO> customers = new ArrayList<>();

            //告警类型以18开头--告警来源为4，代表设备前端告警，过滤订购该设备的客户 add by hued
            if (alarmSource.equals(AlarmConstants.ALARM_SOURCE_4) && !StringUtils.isEmpty(deviceCode)) {
                // 增加告警类型限制
                AlarmTypeInfoDTO typeInfo = deviceRepository.getAlarmTypeInfoById(alarmType);
                if(Objects.isNull(typeInfo)){
                    log.info("摄像机告警下没有该告警类型{}，告警编码：{}", alarmType,receiveAlarmDTO.getReceiveAlarmVO().getAlarmCode());
                    return false;
                }
                // 摄像机告警告警标题为空时进行补偿
                if(StringUtils.isEmpty(receiveAlarmVO.getAlarmBody())){
                    receiveAlarmVO.setAlarmBody(typeInfo.getTypeName());
                }
                customers = deviceRepository.getCustomersByDevice(deviceCode, null,receiveAlarmVO);

                // 增加水位告警特别处理：筛选符合阈值规则的用户
                if(alarmType.equals("1818127") && !CollectionUtils.isEmpty(customers)){
                    Object frontExtendField = receiveAlarmVO.getFrontExtendField();
                    log.info("frontExtendField:{}", JSON.toJSONString(frontExtendField));
                    customers = filterEligibleTriggerRuleCustomers(customers, frontExtendField.toString());
                }

            }
            //物联设备来源，过滤订购该设备的客户
            else if (alarmSource.equals(AlarmConstants.ALARM_SOURCE_8) && !StringUtils.isEmpty(deviceCode)) {
                customers = deviceRepository.getCustomersBySensorDevice(new String[]{deviceCode}, null, OrderConstants.ORDER_SENSOR_CODE,null,alarmSource,localNode);
            }
            //雷达
            else if (WarningSourceEnum.RADAR_ALARM.getTypeValue().equals(alarmSource) && receiveAlarmVO.getExtendField() != null) {
                //查询该taskId下是否有雷达告警模板
                Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_TASK_ID_FLAG);
                //默认开关为0，关闭，1开启
                String flag =Constants.ZERO;
                if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                    flag = suppressDictData.getData().get(0).getDictValue();
                }
                // 获取雷达告警的透传字段  雷达报文中透传字段没有按照接口文档传json字符串
                receiveAlarmVO.setExtraField(JSON.toJSONString(receiveAlarmVO.getExtendField()));
                RadarParam extraField = JSON.parseObject(String.valueOf(receiveAlarmVO.getExtraField()), RadarParam.class);
                if (extraField.getRadarCodes() != null && extraField.getRadarCodes().length > 0 && !StringUtils.isEmpty(extraField.getRadarCodes()[0])) {
                    if(Constants.ZERO.equals(flag)){
                        customers = deviceRepository.getCustomersBySensorDevice(extraField.getRadarCodes(), null, OrderConstants.ORDER_RADAR_CODE, null,alarmSource,localNode);
                    }else{
                        if(!StringUtils.isEmpty(taskId)){
                            customers = deviceRepository.getCustomersBySensorDevice(extraField.getRadarCodes(), null, OrderConstants.ORDER_RADAR_CODE, taskId,alarmSource,localNode);
                        }
                    }
               }

            }
            // 运管推送taskId，则根据taskId过滤租户,否则，按照配置的设备、算法过滤
            else if(!StringUtils.isEmpty(taskId)){
                customers = deviceRepository.getCustomersList(null, null, taskId,alarmSource,localNode);
            }else if(!StringUtils.isEmpty(deviceCode) && !StringUtils.isEmpty(algorithmCode)){
                customers = deviceRepository.getCustomersList(deviceCode, algorithmCode, null,alarmSource,localNode);
                log.error("---------AI告警查询error---------,algorithmCode:{}", algorithmCode);
            }

            if(CollectionUtils.isEmpty(customers)){
                log.info("未查询到租户信息，告警编码：{}", receiveAlarmDTO.getReceiveAlarmVO().getAlarmCode());
                return false;
            }else {
                // 当前同一租户同一行业下只会有一个版本-------由于同一租户同一行业版本升级(eg：1.0升级为2.0)，历史存量的配置数据(1.0版本信息)并不会更新，
                // 因此可能上述查询租户信息会出现查询到该租户该行业的历史版本(1.0版本信息)，从而导致查询到旧版本(1.0)的流程，新版本(2.0)流程始终无法正式生效，因此作此版本覆盖，仅作版本覆盖
                List<OrderCustomerVO> versionCustomers = deviceRepository.queryRealNowVersion(customers);
                customers.forEach(vo ->
                    versionCustomers.forEach( version -> {
                        if(version.getTenantId().equals(vo.getTenantId()) && version.getIndustryCode().equals(vo.getIndustryCode())){
                            vo.setAppVerCode(version.getAppVerCode());
                        }
                    })
                );
                receiveAlarmDTO.setCustomers(customers);
            }
        }catch(Exception e){
            log.info("处理接收告警的租户信息异常：{}", e);
        }

        return this.excuteNextHandler(receiveAlarmDTO);
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
}
