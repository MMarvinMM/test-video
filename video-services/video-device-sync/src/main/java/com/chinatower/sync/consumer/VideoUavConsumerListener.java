package com.chinatower.sync.consumer;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.util.DateUtils;
import com.chinatower.sync.consumer.entity.*;
import com.chinatower.sync.consumer.service.UavReceiveService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 无人机业务模块kafka消费
 * @Author: songxinfeng
 * @Date: 2024/11/28 17:22
 */

@Component
@Slf4j
public class VideoUavConsumerListener {
    @Autowired
    private UavReceiveService uavReceiveService;
    @Autowired
    private SystemFeignClient systemFeignClient;
    public static final String PLATFORM_CODE = "platform_code";
    @Resource
    private GisMapApi gisMapApi;
    @Value("${sms.templateCode.uavUnConnectAlarm}")
    private String uavUnConnectAlarmCode;
    @Value("${sms.templateCode.uavConnectAlarm}")
    private String uavConnectAlarmCode;

    /**
     * 无人机航线任务计划任务状态变更
     * @param consumerRecord
     */
    @KafkaListener(topics = {"UFCP-TASK-STATUS-PROCESS"},containerFactory = "kafkaListenerContainerFactoryK6Batch")
    public void receiveAirLineTaskStatus(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                log.info("receiveAirLineTaskStatus-KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                UavStatusRequest uavStatusRequest = JSON.parseObject(message,UavStatusRequest.class);
                //判断消息体中的受理节点
                String platformCode = getSelfPlatFormCode();
                if (ObjectUtil.isNotNull(uavStatusRequest.getBreakPoint())) {
                    uavStatusRequest.setBreakPointReqJson(JSON.toJSONString(uavStatusRequest.getBreakPoint()));
                }
                log.info("receiveAirLineTaskStatus-KafkaListener-消息节点:{},:消费节点{}",uavStatusRequest.getAcceptNode(),platformCode);
                if (platformCode.equals(uavStatusRequest.getAcceptNode())){
                    uavReceiveService.receiveAirLineTaskStatus(uavStatusRequest);
                }
            }
        }catch (Exception e){
            log.error("receiveRegionAlarm-receiveAirLineTaskStatus-消费异常:{}", e);
        }
    }

    /**
     * 无人机算法任务状态同步
     * @param consumerRecord
     */
    @KafkaListener(topics = {"wh_algorithm_task_status"},containerFactory = "kafkaListenerContainerFactoryK7Batch")
    public void editAirTaskStatus(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                log.info("editAirTaskStatus-KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                UavAlgorithmRequest uavAlgorithmRequest = JSON.parseObject(message,UavAlgorithmRequest.class);
                //判断消息体中的受理节点
                String platformCode = getSelfPlatFormCode();
                log.info("editAirTaskStatus-KafkaListener-消息节点:{},:消费节点{}",uavAlgorithmRequest.getAcceptNode(),platformCode);
                if (platformCode.equals(uavAlgorithmRequest.getAcceptNode())){
                    uavReceiveService.editAirTaskStatus(uavAlgorithmRequest);
                }
            }
        }catch (Exception e){
            log.error("receiveRegionAlarm-editAirTaskStatus-消费异常:{}", e);
        }
    }

    /**
     * 无人机失联信息同步
     * @param consumerRecord
     */
    @KafkaListener(topics = {"UFCP-UAV-LOST-CONTROL-ALARM"},containerFactory = "kafkaListenerContainerFactoryK6Batch")
    public void uavStatus(ConsumerRecord<?, ?> consumerRecord) {
        try {
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                log.info("uavStatus-KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                AIrUavConnectionInfo aIrUavConnectionInfo = JSON.parseObject(message,AIrUavConnectionInfo.class);
                //判断消息体中的受理节点
                String platformCode = getSelfPlatFormCode();
                log.info("editAirTaskStatus-KafkaListener-消息节点:{},:消费节点{}",aIrUavConnectionInfo.getAcceptNode(),platformCode);
                if (!platformCode.equals(aIrUavConnectionInfo.getAcceptNode())){
                    return;
                }
                if (ObjectUtil.isNotNull(aIrUavConnectionInfo)){
                    if (ObjectUtil.isNotNull(aIrUavConnectionInfo.getLatitude()) && ObjectUtil.isNotNull(aIrUavConnectionInfo.getLongitude())){
                        String latitude = this.formatToSixDecimalPlaces(aIrUavConnectionInfo.getLatitude());
                        String longitude = this.formatToSixDecimalPlaces(aIrUavConnectionInfo.getLongitude());
                        aIrUavConnectionInfo.setLatitude(latitude);
                        aIrUavConnectionInfo.setLongitude(longitude);
                    }else {
                        return;
                    }
                    //时间戳转为时间
                    Date date = new Date(Long.valueOf(aIrUavConnectionInfo.getTimestamp()));
                    String pioDate = new SimpleDateFormat(Constants.DATE_YMDHMS).format(date);
                    aIrUavConnectionInfo.setTimestampStr(pioDate);
                    //根据deviceCode查询无人机信息
                    AirUavStatus airUavStatus = uavReceiveService.queryUavByDeviceCode(aIrUavConnectionInfo.getDeviceCode());
                    if(ObjectUtil.isNull(airUavStatus)){
                        airUavStatus = uavReceiveService.queryUavByUavCode(aIrUavConnectionInfo.getDeviceCode());
                    }
                    aIrUavConnectionInfo.setUavCode(airUavStatus.getDeviceCode());
                    aIrUavConnectionInfo.setUavName(airUavStatus.getDeviceName());
                    List<String> list = new ArrayList();
                    list.add(airUavStatus.getDeviceCode());
                    //存在失联信息 查询此设备下的所有无人机告警配置信息
                    List<UavAlarmStrategyVO> uavAlarmStrategyVOS = uavReceiveService.selectUavAlarmStrategyInfo(list);
                    PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
                    req.setLocation(String.format(Constants.FORMAT_S, aIrUavConnectionInfo.getLongitude(), aIrUavConnectionInfo.getLatitude()));
                    String address = "";
                    try {
                        Result<List<PoiContraryGeocodingQueryRes>> result = gisMapApi.poiContraryGeocodingQuery(req);
                        log.info("getGisRegion gisMapApi poiContraryGeocodingQuery param:{}, result:{}", JSON.toJSONString(req), JSON.toJSONString(result));
                        if (result != null && ResultCode.SUCCESS.getCode() == result.getCode() && org.apache.commons.collections4.CollectionUtils.isNotEmpty(result.getData())) {
                            PoiContraryGeocodingQueryRes data = result.getData().get(0);
                            address = data.getAddress();
                        }
                    }catch (Exception e){
                        log.error("getGisRegion error");
                    }
                    aIrUavConnectionInfo.setAddress(address);
                    if("OUT_OF_CONTACT".equals(aIrUavConnectionInfo.getLostContact())){
                        if (CollectionUtils.isNotEmpty(uavAlarmStrategyVOS)){
                            // 筛选 实时发送短信的租户
                            List<UavAlarmStrategyVO> realTimeList = uavAlarmStrategyVOS.stream()
                                    .filter(uavAlarmStrategyVO -> Constants.ZERO.equals(uavAlarmStrategyVO.getDelayOpen()))
                                    .collect(Collectors.toList());
                            uavReceiveService.sendSms(realTimeList,aIrUavConnectionInfo,uavUnConnectAlarmCode,Constants.ONE);
                            // 筛选 延时发送短信的租户
                            List<UavAlarmStrategyVO> delayList = uavAlarmStrategyVOS.stream()
                                    .filter(uavAlarmStrategyVO -> Constants.ONE.equals(uavAlarmStrategyVO.getDelayOpen()))
                                    .collect(Collectors.toList());
                            uavReceiveService.insertUavAlarmInfo(delayList,aIrUavConnectionInfo);
                        }
                    }
                    if ("RECOVERY".equals(aIrUavConnectionInfo.getLostContact())){
                        //解除失联告警 删除设备下无人机失联告警表中的数据
                        uavReceiveService.deleteUavAlarmInfo(aIrUavConnectionInfo.getDeviceCode());
                        //发送恢复短信
                        uavReceiveService.sendSms(uavAlarmStrategyVOS,aIrUavConnectionInfo,uavConnectAlarmCode,Constants.ZERO);
                    }
                }
            }
        }catch (Exception e){
            log.error("receiveRegionAlarm-uavStatus-消费异常:{}", e);
        }
    }
    /**
     * 获取当前节点
     * @return
     */
    public String getSelfPlatFormCode(){
        String configValue ="";
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey(PLATFORM_CODE);
        AjaxResult result = systemFeignClient.getConfigInfo(sysConfig);
        if(result != null && 200 == Integer.valueOf(String.valueOf(result.get("code")))){
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result.get("data")),SysConfig.class);
            if(sysConfigInfo != null){
                configValue = sysConfigInfo.getConfigValue();
            }else{
                log.info("getSelfPlatFormCode-获取当前节点失败:{}",JSON.toJSONString(result));
            }
        }else {
            log.info("getSelfPlatFormCode-获取当前节点失败:{}",JSON.toJSONString(result));
        }
        return configValue;
    }

    /**
     * 截取小数点6位
     * @param input
     * @return
     */
    private String formatToSixDecimalPlaces(String input) {
        try {
            double number = Double.parseDouble(input);
            return String.format("%.6f", number);
        } catch (NumberFormatException e) {
            return input;
        }
    }
}
