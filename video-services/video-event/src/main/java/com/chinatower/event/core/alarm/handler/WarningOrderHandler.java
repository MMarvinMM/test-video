package com.chinatower.event.core.alarm.handler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.core.alarm.util.GisUtil;
import com.chinatower.event.core.alarm.util.WarningOrderUtil;
import com.chinatower.event.dto.ReceiveAlarmDTO;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.mapper.WarningOrderMapper;
import com.chinatower.event.repository.IAlarmSuppressConfigRepository;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.ISuppressStrategyService;
import com.chinatower.event.util.DateUtils;
import com.chinatower.event.utils.PKUtils;
import com.chinatower.event.vo.OrderCustomerVO;
import com.chinatower.event.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.device.AlgorithmMonitorAreaVO;
import com.chinatower.platform.entity.device.BlackWhiteVO;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Description 生成告警事件
 * @Author guyuefei
 * @Date 2022/7/18
 * @Version release_v1.5
 **/
@Component("warningOrderHandler")
@Scope("prototype")
@Slf4j
public class WarningOrderHandler extends AbstractHandler {

    @Autowired
    private WarningOrderUtil warningOrderUtil;
    @Autowired
    private ISuppressStrategyService strategyService;
    @Autowired
    private IAlarmSuppressConfigRepository configRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private IDeviceRepository deviceRepository;

    @Autowired
    private WarningOrderMapper warningOrderMapper;

    @Resource
    private GisUtil gisUtil;

    /**
     * 执行逻辑
     *
     * @param receiveAlarmDTO
     * @return
     */
    @Override
    public boolean excute(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        log.info("生成告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        try{
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
            List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();

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

            //v1.6.40 摄像机-卡口告警，增加告警原因
            List<CheckpointVehicleLicensePlate> licensePlates = null;
            String vehicleNumber = null;
            String blackWhiteJson = null;
            //从字典获取卡口告警类型值
            String alarmTypeValue = Constants.ALARM_TYPE_VALUE;
            Result<List<SysDictDataResponse>> dictDataType = systemFeignClient.dictDataType(Constants.CHECKPOINT_ALARM_TYPES);
            if (dictDataType != null && dictDataType.getData() != null && !dictDataType.getData().isEmpty()) {
                alarmTypeValue = dictDataType.getData().get(0).getDictValue();
            }
            if(Constants.WARNING_SOURCE_4.equals(receiveAlarmVO.getAlarmSource()) && receiveAlarmVO.getAlarmType().equals(alarmTypeValue)){
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
                    paramMap.put("vehicleNumber", vehicleNumber);
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
                //v1.6.60 取前端告警识别结果frontExtendField,整合到扩展字段extendField中
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
            // 水位告警保存前端告警推送的数据
            if(Constants.WARNING_SOURCE_4.equals(receiveAlarmVO.getAlarmSource()) && receiveAlarmVO.getAlarmType().equals(Constants.WATER_LEVEL_ALARM_TYPE_VALUE)){
                log.info("进入水位告警setFrontExtendFields方法");
                if (receiveAlarmVO.getFrontExtendField() != null  && receiveAlarmVO.getFrontExtendField() instanceof String) {
                    String jsonStr = (String) receiveAlarmVO.getFrontExtendField();
                    if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
                        jsonStr = jsonStr.substring(Constants.ONE_NUM, jsonStr.length() - Constants.ONE_NUM);
                    }
                    Map<String, Object> frontExtendFieldMap = JSON.parseObject(jsonStr, Map.class);
                    setFrontExtendFields(frontExtendFieldMap, receiveAlarmVO);
                }
            }

            for (OrderCustomerVO customerObj : customers) {
                String[] appVers = customerObj.getAppVerCode().split(",");
                for (String appVer : appVers) {
                    //构建告警对象
                    ReceiveAlarmVO alarmVo = buildAlarmVo(receiveAlarmVO, customerObj, appVer, vehicleNumber, licensePlates, blackWhiteJson);

                    //处理特殊行业版本
                    if (warningOrderUtil.judgeSpecialAppVer(dictList, alarmVo)) {
                        continue;
                    }

                    // 预置位定位法>特征库定位法>摄像机标定定位法>定位到摄像机  判断是否符合预置位定位
                    if(!StringUtils.isEmpty(alarmVo.getPoints()) && Constants.ONE.equals(alarmVo.getAlarmSource())){
                        dealPresetLocation(alarmVo);
                    }

                    //二次研判转化,去除原有判定逻辑，若没有上报是否二次研判，则默认二次研判为否。
                    if(StringUtils.isEmpty(alarmVo.getIsSecondJudge()) && Constants.FOUR.equals(alarmVo.getAlarmSource())){
                        alarmVo.setIsSecondJudge(Constants.ZERO);
                    }

                    //判断并处理告警压制
                    if (judgeIntercept(alarmVo, isIntercept, strategyMap, configDTOMap, intercepts)) {
                        continue;
                    }

                    //处理运营研判,首先判断是否开启研判，并行研判
                    if (warningOrderUtil.doOperate(alarmVo, alarms, operations)) {
                        continue;
                    }
                    if(alarmVo!=null&& com.chinatower.common.core.utils.StringUtils.isEmpty(alarmVo.getJudgeType())){
                        alarmVo.setJudgeType(Constants.JUDGE_TYPE_COMMON);
                    }
                    alarms.add(alarmVo);
                }
            }

            receiveAlarmDTO.setIntercepts(intercepts);
            receiveAlarmDTO.setOperations(operations);
            receiveAlarmDTO.setList(alarms);
        }catch(Exception e){
            log.info("生成告警事件异常：", e);
        }

        return this.excuteNextHandler(receiveAlarmDTO);
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
        Map<String, String> wlExtendedFieldsDictMap = getDictMap(systemFeignClient.dictDataType(Constants.WATER_LEVEL_EXTENDED_FIELDS));
        List<Map> extendFieldList = new ArrayList<>();
        if(receiveAlarmVO.getExtendField() != null && !Constants.ONE_BLANK.equals(JSON.toJSONString(receiveAlarmVO.getExtendField()))
                && !Constants.TWO_BLANK.equals(JSON.toJSONString(receiveAlarmVO.getExtendField()))){
            extendFieldList = JSON.parseArray(JSON.toJSONString(receiveAlarmVO.getExtendField()), Map.class);
        }
        List<Map<String, Object>> attrList = convertToAttrList(frontExtendFieldMap, dictMap, vColorDictMap, vnColorDictMap, wlExtendedFieldsDictMap);
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

    private List<Map<String, Object>> convertToAttrList(Map<String, Object> frontExtendFieldMap, Map<String, String> dictMap,
                                                        Map<String, String> vColorDictMap, Map<String, String> vnColorDictMap,
                                                        Map<String, String> wlExtendedFieldsDictMap) {
        return frontExtendFieldMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> attrMap = new HashMap<>();
                    attrMap.put(Constants.PARAM_ATTR_TYPE, entry.getKey());
                    attrMap.put(Constants.PARAM_ATTR_VALUE, getAttrValue(entry.getValue(), dictMap, vColorDictMap, vnColorDictMap, wlExtendedFieldsDictMap));
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
     * @return
     */
    private Object getAttrValue(Object value, Map<String, String> dictMap, Map<String, String> vColorDictMap, Map<String, String> vnColorDictMap, Map<String, String> wlExtendedFieldsDictMap) {
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
        String wlExtendedFields = wlExtendedFieldsDictMap.get(value);
        if (wlExtendedFields != null) {
            return wlExtendedFields;
        }
        return value;
    }

    /**
     * 获取字典map
     * @param dictDataType
     * @return
     */
    private Map<String, String> getDictMap(Result<List<SysDictDataResponse>> dictDataType) {
        return dictDataType.getData().stream().collect(Collectors.toMap(SysDictDataResponse::getDictValue, SysDictDataResponse::getDictLabel));
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

    public Map<String, Object> toPlateMap(CheckpointVehicleLicensePlate plate) {
        Map<String, Object> plateMap = new HashMap<>();
        plateMap.put("vehicleNumber", plate.getVehicleNumber());
        plateMap.put("validPeriod", plate.getValidPeriod());
        plateMap.put("appVerCode", plate.getAppVerCode());
        return plateMap;
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
     * 构建告警对象
     *
     * @param receiveAlarmVO
     * @param customerObj
     * @param vehicleNumber
     * @param appVer
     * @param licensePlates
     * @param blackWhiteJson
     * @return
     */
    private ReceiveAlarmVO buildAlarmVo(ReceiveAlarmVO receiveAlarmVO, OrderCustomerVO customerObj, String appVer, String vehicleNumber,
                                        List<CheckpointVehicleLicensePlate> licensePlates, String blackWhiteJson) {
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
        alarmVo.setWarningOrderId(PKUtils.getWarnOrderId(alarmVo.getTenantId(), alarmVo.getAlarmTime()));
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
        return alarmVo;
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
     * 判断是否符合告警压制的条件，放入告警压制集合
     * 判断告警等级、压制规则
     *
     * @param alarmVO
     * @param isIntercept
     * @param strategyMap
     * @param configDTOMap
     * @param intercepts
     * @return
     */
    private boolean judgeIntercept(ReceiveAlarmVO alarmVO, String isIntercept, Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap,
                                   Map<String, AlarmSuppressConfigDTO> configDTOMap, List<ReceiveAlarmVO> intercepts) {

        String tenantId = alarmVO.getTenantId();
        String deviceCode = alarmVO.getDeviceCode();
        String mapKey = tenantId + "_" + deviceCode;
        String alarmTime = alarmVO.getAlarmTime();
        String alarmType = String.valueOf(alarmVO.getAlarmType());

        /*if (Constants.IMPORTANCE_LEVEL_2.equals(alarmVO.getImportanceLevel())) {
            // 如果告警重要等级为二级，直接落告警压制表
            intercepts.add(alarmVO);
            return true;
        } else if ("1".equals(isIntercept) && MapUtils.isNotEmpty(strategyMap)) {
            //联合告警压制需求代码合并
            List<VmAlarmSuppressStrategyVO> strategyTenantList = strategyMap.get(alarmVO.getTenantId()+alarmVO.getIndustryCode());
            if (strategyTenantList != null && !strategyTenantList.isEmpty()) {  //没有查询到匹配得告警压制策略
                boolean interceptFlag = strategyService.doStrategyIntercept(alarmVO, strategyTenantList);
                if (interceptFlag) {
                    // 压制成功，放入压制集合
                    intercepts.add(alarmVO);
                    return true;
                }
            }
        }*/

        if ("1".equals(isIntercept) && MapUtils.isNotEmpty(strategyMap)) {
            //联合告警压制需求代码合并
            List<VmAlarmSuppressStrategyVO> strategyTenantList = strategyMap.get(alarmVO.getTenantId()+alarmVO.getIndustryCode());
            if (strategyTenantList != null && !strategyTenantList.isEmpty()) {  //没有查询到匹配得告警压制策略
                boolean interceptFlag = strategyService.doStrategyIntercept(alarmVO, strategyTenantList);
                if (interceptFlag) {
                    // 压制成功，放入压制集合
                    intercepts.add(alarmVO);
                    return true;
                }
            }
        }

        //雨天模式告警规则生效则进行压制
        log.info("雨天压制获取到的配置信息：{}", configDTOMap);
        if (doIntercept(configDTOMap, mapKey, DateUtils.string2Date(alarmTime))) {
            alarmVO.setIntercept(AlgorithmEventVO.RAINY_ALGORITHM);
            intercepts.add(alarmVO);
            return true;
        }

        //获取研判压制,压制规则保存到redis中,redis中有数据时进行压制
        String key = Constants.INTERCEPT_CONFIG + tenantId + "_" + alarmVO.getIndustryCode() + "_" + deviceCode + "_" + alarmType;
        String interceptConfig = RedisUtil.get(key, RedisDBType.PRIMARY);
        log.info("研判压制获取到的redis值：{}", interceptConfig);
        if (!StringUtils.isEmpty(interceptConfig)) {
            alarmVO.setIntercept(AlgorithmEventVO.FOR_JUDGE);
            intercepts.add(alarmVO);
            return true;
        }

        return false;
    }

    /**
     * 校验是否走雨天压制规则
     *
     * @param configDTOMap
     * @param key
     * @return
     */
    private boolean doIntercept(Map<String, AlarmSuppressConfigDTO> configDTOMap, String key, Date alarmTime) {
        if (MapUtils.isNotEmpty(configDTOMap)) {
            AlarmSuppressConfigDTO configDTO = configDTOMap.get(key);
            if (null != configDTO && configDTO.getSuppressBeginTime().compareTo(alarmTime) <= 0 && configDTO.getSuppressEndTime().compareTo(alarmTime) >= 0) {
                return true;
            }
        }
        return false;
    }

}
