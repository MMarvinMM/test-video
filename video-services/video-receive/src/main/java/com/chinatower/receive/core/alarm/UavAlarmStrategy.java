package com.chinatower.receive.core.alarm;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.enums.WarningSourceEnum;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirLineService;
import com.chinatower.device.entity.dto.TelemetryReq;
import com.chinatower.device.entity.dto.UavCameraAttitudeReq;
import com.chinatower.device.entity.dto.UavFlyReq;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.dto.AlarmSuppressConfigDTO;
import com.chinatower.event.entity.dto.ReceiveUavAlarmVO;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.util.DateUtils;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.service.WarningOrderFlowService;
import com.chinatower.receive.common.CommonService;
import com.chinatower.receive.constant.TableConstant;
import com.chinatower.receive.core.alarm.common.AlarmBaseStategy;
import com.chinatower.receive.core.util.AlarmStrategyEnum;
import com.chinatower.receive.core.util.GisUtil;
import com.chinatower.receive.core.util.WarningOrderUtil;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.service.IAlarmSuppressConfigRepository;
import com.chinatower.receive.service.ISuppressStrategyService;
import com.chinatower.receive.util.AirAlarmLocationUtil;
import com.chinatower.receive.util.IPKUtils;
import com.chinatower.receive.util.TableUtil;
import com.chinatower.receive.vo.dto.AirAlarmLocationReq;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.entity.DeviceSiteReceiveVO;
import com.chinatower.receive.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 无人机告警策略实现类
 * @Author: lyy
 * @Date: 2024/11/01 15:11
 * @version release_v1.7.00
 */

@Component("uavAlarmStrategy")
@Slf4j
public class UavAlarmStrategy extends AlarmBaseStategy {

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
    private WarningOrderFlowService warningOrderFlowService;

    @Autowired
    private IAirLineService iAirLineService;

    @Autowired
    private ISuppressStrategyService strategyService;

    @Autowired
    private IAlarmSuppressConfigRepository configRepository;

    @Override
    public boolean jdugeAlarmType(ReceiveAlarmVO receiveAlarmVO) {
        return WarningSourceEnum.UAV_DEVICE_ALARM.getTypeValue().equals(receiveAlarmVO.getAlarmSource());
    }

    @Override
    public void receiveAlarm(ReceiveAlarmDTO receiveAlarmDTO) throws Exception {
        try {
            ReceiveUavAlarmVO receiveUavAlarmVO = montageUrl(receiveAlarmDTO);
            log.info("接收无人机告警入参：{}", JSON.toJSONString(receiveUavAlarmVO));

            //1、获取告警租户信息
            List<OrderCustomerVO> customerList = deviceRepository.getCustomersByRouteId(receiveUavAlarmVO.getFlightRouteId());
            if(CollectionUtils.isEmpty(customerList)){
                log.info("未查询到租户信息，航线ID：{}", receiveUavAlarmVO.getFlightRouteId());
                return ;
            }
            receiveAlarmDTO.setCustomers(customerList);
            log.info("====== 接收无人机告警 租户信息：{} ======",JSON.toJSONString(customerList));

            //2、填充告警参数
            populateUavAlarmOrderParams(receiveUavAlarmVO, receiveAlarmDTO);

            //3、处理告警地址以及经纬度
            gisUtil.getAddressFromAmap(receiveAlarmDTO.getReceiveAlarmVO(), null);

            //4、根据经纬度获取网格信息
            warningOrderUtil.setAlarmGridInfo(receiveAlarmDTO);

            //5、处理告警工单、告警推送
            List<ReceiveAlarmVO> alarms = uavWarningOrderHandler(receiveUavAlarmVO, receiveAlarmDTO);
            if (CollectionUtils.isEmpty(alarms)) {
                log.info("告警工单处理失败，航线ID：{}", receiveUavAlarmVO.getFlightRouteId());
                return;
            }
        } catch(Exception e) {
            log.info("接收无人机告警时发生异常：" + e);
        }
    }

    /**
     * 对工单图片进行拼接（非中心节点&受理节点与当前节点不一致得单子进行操作，其余保持原有路径）
     * @param receiveAlarmDTO
     * @return
     */
    private ReceiveUavAlarmVO montageUrl(ReceiveAlarmDTO receiveAlarmDTO) {
        ReceiveUavAlarmVO receiveUavAlarmVO = receiveAlarmDTO.getReceiveUavAlarmVOS();
        if(StringUtils.isEmpty(receiveUavAlarmVO.getAccessNode())){
            log.info("接入节点为空-入参:{}",JSON.toJSONString(receiveUavAlarmVO));
            return receiveUavAlarmVO;
        }
        //中心节点文件地址无需拼接属地编码
        if("10000000001".equals(receiveUavAlarmVO.getAccessNode())){
            return receiveUavAlarmVO;
        }
        //纯属化不拼接节点编码
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey("dependence_manage");
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        log.info("获取当前环境字段返回:{}",JSON.toJSONString(result));
        if(result != null && 200 == Integer.parseInt(String.valueOf(result.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")), SysConfig.class);
            if (sysConfigInfo != null && Constants.ZERO.equals(sysConfigInfo.getConfigValue())) {
                return receiveUavAlarmVO;
            }
        }
        receiveUavAlarmVO.setAlarmVideoUrl(getUrl(receiveUavAlarmVO.getAccessNode(),receiveUavAlarmVO.getAlarmVideoUrl()));
        receiveUavAlarmVO.setAlarmPictureUrl(getUrl(receiveUavAlarmVO.getAccessNode(),receiveUavAlarmVO.getAlarmPictureUrl()));
        receiveUavAlarmVO.setOriginalPictureUrl(getUrl(receiveUavAlarmVO.getAccessNode(),receiveUavAlarmVO.getOriginalPictureUrl()));
        return receiveUavAlarmVO;
    }

    private List<ReceiveAlarmVO> uavWarningOrderHandler(ReceiveUavAlarmVO receiveUavAlarmVO,ReceiveAlarmDTO receiveAlarmDTO) {
        log.info("生成无人机告警事件入参：{}", JSON.toJSONString(receiveAlarmDTO));
        List<ReceiveAlarmVO> successDatas = new ArrayList<>();
        try{
            ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
            List<OrderCustomerVO> customers = receiveAlarmDTO.getCustomers();

            ReceiveAlarmDTO receiveAlarmVODB = new ReceiveAlarmDTO();
            String alarmTime = receiveAlarmVO.getAlarmTime();
            receiveAlarmVODB.setTableName(TableUtil.getTableName(DateUtils.string2Date(alarmTime), TableConstant.WARN_ORDER_TEBLE));

            // 接收告警集合
            List<ReceiveAlarmVO> alarms = new ArrayList<>();

            // 告警压制集合
            List<ReceiveAlarmVO> intercepts = new ArrayList<>();

            // 告警压制开关是否开启
            String isIntercept = warningOrderUtil.getInterceptSwitch();

            //联合告警压制需求代码合并
            Map<String, List<VmAlarmSuppressStrategyVO>> strategyMap = getAlarmSuppressStrategy(receiveAlarmVO, customers, isIntercept);

            //先把涉及的租户的雨天告警压制配置数据查询出来,防止后面循环查询
            Map<String, AlarmSuppressConfigDTO> configDTOMap = getAlarmSuppressConfig(customers);

            for (OrderCustomerVO customerObj : customers) {
                ReceiveAlarmVO alarmVo = new ReceiveAlarmVO();
                customerObj.setDeviceCode(receiveAlarmVO.getDeviceCode());
                BeanUtils.copyProperties(receiveAlarmVO, alarmVo);
                BeanUtils.copyProperties(customerObj, alarmVo);
                alarmVo.setAlarmNode(getPlatformNode());
                String warningOrderId = IPKUtils.getWarnOrderId(alarmVo.getTenantId(), alarmVo.getAlarmTime());
                alarmVo.setWarningOrderId(warningOrderId);
                alarmVo.setAlarmCode(warningOrderId);
                //判断并处理告警压制
                if (this.judgeIntercept(alarmVo, isIntercept, strategyMap, configDTOMap, intercepts) && !Constants.ZERO.equals(alarmVo.getIsPresetMakeAlarm())) {
                    continue;
                }
                alarms.add(alarmVo);
            }
            receiveAlarmVODB.setIntercepts(intercepts);
            // 批量保存告警信息
            successDatas = batchCreateWarningOrder(alarms, receiveAlarmVODB);
            receiveAlarmVODB.setList(alarms);
            String originalJson = JSON.toJSONString(receiveAlarmVODB.getList());
            commonService.saveAlarm(receiveAlarmVODB, alarmTime, Constants.WARNING_SOURCE_12, originalJson,receiveAlarmDTO.getAlarmPointList());
            // 告警推送
            try{
                commonService.batchPushMessage(alarms);
            }catch (Exception e){
                log.error("====== 无人机告警推送失败,异常原因",e);
            }
        } catch(Exception e) {
            log.info("生成无人机告警事件异常：" + e);
        }
        return successDatas;
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

    private void populateUavAlarmOrderParams(ReceiveUavAlarmVO receiveUavAlarmVO, ReceiveAlarmDTO receiveAlarmDTO) {
        ReceiveAlarmVO receiveAlarmVO = receiveAlarmDTO.getReceiveAlarmVO();
        //查询告警类型名称
        List<Map> alarmTypeList = deviceRepository.getAlarmType();
        DeviceSiteReceiveVO uavDeviceList = deviceRepository.queryUavDeviceList(receiveUavAlarmVO.getDeviceCode());
        receiveAlarmVO.setTaskId(receiveUavAlarmVO.getTaskId());
        receiveAlarmVO.setAlarmTime(receiveUavAlarmVO.getAlarmTime());
        receiveAlarmVO.setExtraField(JSON.toJSONString(receiveUavAlarmVO.getExtendField()));
        receiveAlarmVO.setWarningSource(Constants.WARNING_SOURCE_12);
        receiveAlarmVO.setAlarmSource(Constants.WARNING_SOURCE_12);
        receiveAlarmVO.setWarningTypeId(receiveUavAlarmVO.getAlarmType());
        receiveAlarmVO.setAlarmType(receiveUavAlarmVO.getAlarmType());
        receiveAlarmVO.setAlgorithmCode(receiveUavAlarmVO.getAlgorithmCode());

        setAlarmBody(receiveAlarmVO, receiveUavAlarmVO, alarmTypeList);
        receiveAlarmVO.setDeviceCode(uavDeviceList != null ? uavDeviceList.getDeviceCode() : receiveUavAlarmVO.getDeviceCode());
        receiveAlarmVO.setDeviceName(uavDeviceList != null ? uavDeviceList.getDeviceName() : "");

        if (!StringUtils.isEmpty(receiveUavAlarmVO.getLatitude()) && !StringUtils.isEmpty(receiveUavAlarmVO.getLongitude())) {
            receiveAlarmVO.setLatitude(receiveUavAlarmVO.getLatitude());
            receiveAlarmVO.setLongitude(receiveUavAlarmVO.getLongitude());
        } else {
            //获取无人机真实经纬度
            double[] latAndLon = calculateAirAlarmLocation(receiveUavAlarmVO, receiveAlarmVO);
            receiveAlarmVO.setLatitude(String.valueOf(latAndLon[0]));
            receiveAlarmVO.setLongitude(String.valueOf(latAndLon[1]));
        }

        receiveAlarmVO.setFlightRouteId(receiveUavAlarmVO.getFlightRouteId());
        receiveAlarmVO.setTaskRecordId(receiveUavAlarmVO.getTaskRecordId());
        receiveAlarmVO.setImportanceLevel(!StringUtils.isEmpty(receiveUavAlarmVO.getImportanceLevel()) ? receiveUavAlarmVO.getImportanceLevel() : Constants.IMPORTANCE_LEVEL_1);
        receiveAlarmVO.setSecondJudgeResult(receiveUavAlarmVO.getSecondJudgeResult());
        receiveAlarmVO.setAlarmNode(getPlatformNode());
        receiveAlarmVO.setFlowNodeTime(DateUtils.currentDateToString());
        receiveAlarmVO.setNotifyType(Constants.YES_FLAG);
        receiveAlarmVO.setOriginalPictureUrl(receiveUavAlarmVO.getOriginalPictureUrl());
        receiveAlarmVO.setAlarmPictureUrl(receiveUavAlarmVO.getAlarmPictureUrl());
        receiveAlarmVO.setAlarmVideoUrl(receiveUavAlarmVO.getAlarmVideoUrl());
        receiveAlarmVO.setAlarmArea(receiveUavAlarmVO.getAlarmArea());
    }

    /**
     * 计算无人机经纬度
     * @param receiveUavAlarmVO
     * @param receiveAlarmVO
     * @return
     */
    private double[] calculateAirAlarmLocation(ReceiveUavAlarmVO receiveUavAlarmVO, ReceiveAlarmVO receiveAlarmVO) {
        AirAlarmLocationReq airAlarmLocationReq = new AirAlarmLocationReq();
        String lat = "";
        String lng = "";
        //获取遥测数据信息
        TelemetryReq telemetryReq = new TelemetryReq();
        telemetryReq.setAlarmTime(receiveAlarmVO.getAlarmTime());
        telemetryReq.setDeviceCode(receiveUavAlarmVO.getDeviceCode());
        try {
            Result result = iAirLineService.queryTelemetryData(telemetryReq);
            if (result != null && result.getData() != null) {
                Object data = result.getData();
                if (!(data instanceof String)) {
                    UavFlyReq uavFlyReq = JSON.parseObject(JSON.toJSONString(data), UavFlyReq.class);
                    log.info("无人机告警接收-获取遥测数据结果：{}", JSON.toJSONString(uavFlyReq));
                    if (uavFlyReq != null) {
                        lat=uavFlyReq.getLat();
                        lng=uavFlyReq.getLng();
                        if (StringUtils.isEmpty(uavFlyReq.getAttitudeHead()) || StringUtils.isEmpty(uavFlyReq.getAttitudePitch()) || StringUtils.isEmpty(uavFlyReq.getAttitudeRoll())) {
                            return new double[]{Double.parseDouble(lat), Double.parseDouble(lng)};
                        }
                        airAlarmLocationReq.setYaw(Optional.ofNullable(uavFlyReq.getAttitudeHead()).map(Double::valueOf).orElse(null)); //无人机航向角
                        airAlarmLocationReq.setPitch(Optional.ofNullable(uavFlyReq.getAttitudePitch()).map(Double::valueOf).orElse(null)); //无人机俯仰角
                        airAlarmLocationReq.setRoll(Optional.ofNullable(uavFlyReq.getAttitudeRoll()).map(Double::valueOf).orElse(null)); //无人机横滚角
                        airAlarmLocationReq.setLat_d(Optional.ofNullable(uavFlyReq.getLat()).map(Double::valueOf).orElse(null)); //无人机纬度
                        airAlarmLocationReq.setLon_d(Optional.ofNullable(uavFlyReq.getLng()).map(Double::valueOf).orElse(null)); //无人机经度
                        //设置云台信息
                        List<UavCameraAttitudeReq> uavCameraAttitude = uavFlyReq.getCameraAttitude();
                        if (!CollectionUtils.isEmpty(uavCameraAttitude)) {
                            UavCameraAttitudeReq cameraAttitude = uavCameraAttitude.get(0);
                            if (StringUtils.isEmpty(cameraAttitude.getHead()) ||
                                    StringUtils.isEmpty(cameraAttitude.getPitch()) ||
                                    StringUtils.isEmpty(cameraAttitude.getRoll()) ) {
                                return new double[]{Double.parseDouble(lat), Double.parseDouble(lng)};
                            }
                            airAlarmLocationReq.setYaw1(Optional.ofNullable(cameraAttitude.getHead()).map(Double::valueOf).orElse(null)); //云台航向角
                            airAlarmLocationReq.setPitch1(Optional.ofNullable(cameraAttitude.getPitch()).map(Double::valueOf).orElse(null)); //云台俯仰角
                            airAlarmLocationReq.setRoll1(Optional.ofNullable(cameraAttitude.getRoll()).map(Double::valueOf).orElse(null)); //云台横滚角
                            airAlarmLocationReq.setF(Optional.ofNullable(cameraAttitude.getRadio()).map(Number::doubleValue).orElse(null)); //焦距
                            airAlarmLocationReq.setK(Optional.ofNullable(cameraAttitude.getLens()).map(Number::doubleValue).orElse(null)); //相机放大倍数
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("无人机告警接收-查询遥测数据时发生异常：{}", e);
        }
        //获取点位信息，计算主点x、y偏移
        if(!CollectionUtils.isEmpty(receiveUavAlarmVO.getAlarmArea())) {
            try {
                // 获取置信度最大的告警区域的框图点位
                AlarmOriginalAreaVO alarmOriginalAreaVO = receiveUavAlarmVO.getAlarmArea().stream().max(Comparator.comparing(AlarmOriginalAreaVO::getConfidenceLevel)).orElse(new AlarmOriginalAreaVO());
                List<AlarmOriginalPointVO> alarmPointList = alarmOriginalAreaVO.getAlarmPointList();
                if (!CollectionUtils.isEmpty(alarmPointList)) {
                    // 按 y 值升序排列，找到 y 值最小的两个点
                    List<AlarmOriginalPointVO> smallestYPoints = alarmPointList.stream().sorted(Comparator.comparingDouble(point -> Double.parseDouble(point.getPercentY()))).limit(Constants.TWO_NUM).collect(Collectors.toList());
                    if (smallestYPoints.size() == Constants.TWO_NUM) {
                        // 获取 y 值（两点的 y 是相同的）
                        double centerY = Double.valueOf(smallestYPoints.get(0).getPercentY());
                        // 获取 x 坐标的中间点
                        double centerX = smallestYPoints.stream().mapToDouble(point -> Double.parseDouble(point.getPercentX())).average().orElse(Double.parseDouble(Constants.ZERO_STRING));
                        log.info("无人机告警接收-得到中间点的坐标为：x = " + centerX + ",y = " + centerY);
                        airAlarmLocationReq.setCx(centerX);
                        airAlarmLocationReq.setCy(centerY);
                        airAlarmLocationReq.setU(centerX);
                        airAlarmLocationReq.setV(centerY);
                    }
                }
            }  catch (Exception e) {
                log.error("无人机告警接收-处理告警区域点位时发生异常：{}", e);
            }
        }
        try {
            log.info("无人机告警接收-获取无人机真实经纬度入参：{}", JSON.toJSONString(airAlarmLocationReq));
            double[] latAndLon = AirAlarmLocationUtil.getAirAlarmLocation(airAlarmLocationReq);
            if (latAndLon != null && latAndLon.length == Constants.TWO_NUM) {
                log.info("无人机告警接收-计算得到的真实经纬度：latitude = {}, longitude = {}", latAndLon[0], latAndLon[1]);
                return latAndLon;
            }
        } catch (Exception e) {
            log.error("无人机告警接收-获取无人机真实经纬度时发生异常：{}", e);
        }
        //计算出现问题时，用原始无人机经纬度
        return new double[]{Double.parseDouble(lat), Double.parseDouble(lng)};
    }

    private void setAlarmBody(ReceiveAlarmVO receiveAlarmVO, ReceiveUavAlarmVO receiveUavAlarmVO, List<Map> alarmTypeList) {
        if (!CollectionUtils.isEmpty(alarmTypeList)) {
            alarmTypeList.forEach(item -> {
                if (receiveUavAlarmVO.getAlarmType().equals(item.get(Constants.PARAM_TYPE_VALUE))) {
                    receiveAlarmVO.setAlarmBody(String.valueOf(item.get(Constants.PARAM_TYPE_NAME)));
                }
            });
        }
    }

    private List<ReceiveAlarmVO> batchCreateWarningOrder(List<ReceiveAlarmVO> alarms, ReceiveAlarmDTO receiveAlarmVODB) {
        List<AlarmOriginalPointVO> alarmPointList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(alarms)) {
            log.info("批量保存无人机告警信息入参：{}", JSON.toJSONString(alarms));
            // 创建流程
            List<Map> recordList = warningOrderFlowService.createRecord(alarms);

            // 回填当前环节相关字段
            warningOrderFlowService.setWarningLinkInfo(alarms,recordList);
            receiveAlarmVODB.setAlarmPointList(alarmPointList);
            return alarms;
        }
        return new ArrayList<>();
    }

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

    @Override
    public List<OrderCustomerVO> getCustomers(ReceiveAlarmVO receiveAlarmVO) throws Exception {
        return null;
    }

    @Override
    public void getAddress(ReceiveAlarmVO receiveAlarmVO) {
        log.info("执行方法getAddress");
    }

    @Override
    public String getStrategyName() {
        return AlarmStrategyEnum.UAV_DEVICE_ALARM.getAlarmStrategyBean();
    }
}
