package com.chinatower.message.kafka;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.device.entity.dto.SatelliteInfo;
import com.chinatower.device.entity.dto.UavFlyReq;
import com.chinatower.device.entity.dto.UavFlyTaskRecordReq;
import com.chinatower.device.entity.dto.UavUfcpInfoReq;
import com.chinatower.message.constant.WebSocketConstant;
import com.chinatower.message.hms.service.UavHmsSmsSuppressService;
import com.chinatower.message.station.entity.*;
import com.chinatower.message.station.service.UavAsyncSendMsgService;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.util.DateUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author WLF
 * @description
 * @date 2024/12/2 15:20
 */
@Slf4j
@Component
public class UavMessageKafkaConsumer {
    private String groupId;

    @Resource
    UavAsyncSendMsgService uavAsyncSendMsgService;
    @Resource
    private SystemFeignClient systemFeignClient;

    @Resource
    private UavHmsSmsSuppressService uavHmsSmsSuppressService;

    public static final String PLATFORM_CODE = "platform_code";

    @Bean("uavListenerGroupId")
    public String genGroupId() {
        groupId = UUID.randomUUID().toString();
        return groupId;
    }

    @KafkaListener(topics = {WebSocketConstant.VIDEO_FRAME_MSG}, containerFactory = "kafkaListenerContainerFactoryK6Batch", groupId = "UAV_VIDEO-MESSAGE-TOPIC" + "-" + "#{uavListenerGroupId}")
    public void consumeMessage(ConsumerRecord<?, ?> consumerRecord) {
        log.info("消费消息:{}", JSONObject.toJSON(consumerRecord));
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String) kafkaMessage.get();
                log.info("consumeMessage-KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                UavVideoFrameMsg uavVideoFrameMsg = JSON.parseObject(message, UavVideoFrameMsg.class);
                //判断消息体中的受理节点
                String platformCode = getSelfPlatFormCode();
                log.info("consumeMessage-KafkaListener-消息节点:{},:消费节点{}",uavVideoFrameMsg.getAcceptNode(),platformCode);
                if (platformCode.equals(uavVideoFrameMsg.getAcceptNode())){
                    if (ObjectUtil.isNotNull(uavVideoFrameMsg) && ObjectUtil.isNotEmpty(uavVideoFrameMsg.getCount()) && ObjectUtil.isNotNull(uavVideoFrameMsg.getTotalCount())) {
                        String cacheKey = "uavVideoFrameMsg_" + uavVideoFrameMsg.getId();
                        //进度不满百分百 获取锁 再操作
                        if (uavVideoFrameMsg.getState() == 0) {
                            boolean lock = RedisUtil.setIfAbsent(cacheKey, "lock", 5, TimeUnit.SECONDS, RedisDBType.PRIMARY);
                            if (lock) {
                                //更新数据库任务状态
                                int status = uavAsyncSendMsgService.updateTaskStatus(uavVideoFrameMsg);
                                //socket send
                                if (status>0){
                                    uavAsyncSendMsgService.extractionTaskMsgSupplement(uavVideoFrameMsg);
                                    uavAsyncSendMsgService.sendRadarMsg(uavVideoFrameMsg);
                                }
                            }
                        }
                        //进度已完成或者已取消  直接发送  并且删除锁
                        if(uavVideoFrameMsg.getState()==1 || uavVideoFrameMsg.getState()==2){
                            //更新数据库任务状态
                            int status = uavAsyncSendMsgService.updateTaskStatus(uavVideoFrameMsg);
                            //socket send
                            if (status>0){
                                uavAsyncSendMsgService.extractionTaskMsgSupplement(uavVideoFrameMsg);
                                uavAsyncSendMsgService.sendRadarMsg(uavVideoFrameMsg);
                            }
                            RedisUtil.delete(cacheKey, RedisDBType.PRIMARY);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("视频抽帧任务状态同步消费异常:{}", e);
        }
    }


    /**
     * y遥感数据消费
     */
    @KafkaListener(topics = {"UFCP-UAV-INFO"},containerFactory = "kafkaListenerContainerFactoryK6Batch")
    public void uavUfcpInfo(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                log.info("uavUfcpInfo-KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                UavUfcpInfoReq uavUfcpInfoReq = JSON.parseObject(message, UavUfcpInfoReq.class);
                //判断消息体中的受理节点
                String platformCode = getSelfPlatFormCode();
                log.info("uavUfcpInfo-KafkaListener-消息节点:{},:消费节点{}",uavUfcpInfoReq.getAcceptNode(),platformCode);
                if (platformCode.equals(uavUfcpInfoReq.getAcceptNode())){
                    String uavFlyId = UUID.randomUUID().toString().replace("-", "");
                    UavFlyReq uavFlyReqs =  uavUfcpInfoReq.getFly();
                    uavFlyReqs.setDeviceCode(uavUfcpInfoReq.getDeviceCode());
                    //时间戳转为时间
                    Date date = new Date(Long.valueOf(uavUfcpInfoReq.getTimestamp()));
                    String pioDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                    uavFlyReqs.setTimestamp(pioDate);
                    uavFlyReqs.setUavFlyId(uavFlyId);
                    //存入任务记录信息
                    UavFlyTaskRecordReq uavFlyTaskRecordReq = uavUfcpInfoReq.getTaskRecord();
                    if (ObjectUtil.isNotNull(uavFlyTaskRecordReq)){
                        uavFlyTaskRecordReq.setUavFlyId(uavFlyId);
                        uavFlyReqs.setTaskRecordId(uavFlyTaskRecordReq.getTaskRecordId());
                        uavAsyncSendMsgService.insertUavTaskRecord(uavFlyTaskRecordReq);
                    }
                    // 发送遥测socket
                    uavAsyncSendMsgService.sendUfcpMsg(uavFlyReqs);
                    // 存入遥测信息mongo
                    uavAsyncSendMsgService.insertUavUfcpInfo(uavFlyReqs);
                    // 发送卫星数据socket
                    SatelliteInfo satelliteInfo = uavUfcpInfoReq.getSatelliteInfo();
                    satelliteInfo.setDeviceCode(uavUfcpInfoReq.getDeviceCode());
                    uavAsyncSendMsgService.sendUfcpSatelliteInfo(satelliteInfo);
                }
            }
        }catch (Exception e){
            log.error("uavUfcpInfo 消费异常:{}", e);
        }
    }

    /**
     * y遥感数据大气数据消费
     */
    @KafkaListener(topics = {"UFCP-GAS-INFO"},containerFactory = "kafkaListenerContainerFactoryK6Batch")
    public void uavUfcpGasInfo(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                log.info("uavUfcpGasInfo-KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                //处理大气数据
                AirQualityData airQualityData = JSON.parseObject(message, AirQualityData.class);
                // 定义输入格式
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
                LocalDateTime dateTime = LocalDateTime.parse(airQualityData.getLingxiu().getUtcTime(), inputFormatter);
                // 定义输出格式（按需调整）
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedTime = dateTime.format(outputFormatter);
                airQualityData.getLingxiu().setUtcTime(formattedTime);
                Map<String, Object> airData = airQualityData.getLingxiu().getAirData();
                airQualityData.getLingxiu().setAirDataAsJson(airData);
                if (ObjectUtil.isNotNull(airQualityData)){
                    // socket
                    uavAsyncSendMsgService.sendUfcpGasMsg(airQualityData);
                    //存入遥测信息
                    uavAsyncSendMsgService.insertUavUfcpGasInfo(airQualityData);
                }
            }
        }catch (Exception e){
            log.error("uavUfcpGasInfo 消费异常:{}", e);
        }
    }

    public void handleUavNestBindLogic(UavNestInfo uavNestInfo) {
        String uavNestDeviceCode = uavNestInfo.getDeviceCode();
        String uavDeviceCode = uavNestInfo.getUavDeviceCode();
        // 处理无人机和无人机巢绑定关系
        if (StringUtils.isNotEmpty(uavNestDeviceCode)) {
            UavNestInfo.TelemetryData telemetryData = uavNestInfo.getData();
            try {
                String cacheKey = String.format("UAV_NEST_BIND_%s", uavNestDeviceCode);
                // 加缓存，多个地方更新，加缓存
                if (StringUtils.isNotEmpty(uavDeviceCode)) {
                    String cacheUavDeviceCode = RedisUtil.get(cacheKey, String.class, RedisDBType.PRIMARY);
                    // 如果不相同，更新机巢和无人机的绑定关系，并放入缓存
                    if (!StringUtils.equals(cacheUavDeviceCode, uavDeviceCode)) {
                        log.info("handleUavNestBindLogic 更新无人机机巢绑定信息,cacheUavDeviceCode:{} uavDeviceCode:{}, uavNestDeviceCode:{}",
                                cacheUavDeviceCode, uavDeviceCode, uavNestDeviceCode);
                        uavAsyncSendMsgService.updateUavNestCode(uavDeviceCode, uavNestDeviceCode);
                        RedisUtil.set(cacheKey, uavDeviceCode, 10, TimeUnit.MINUTES, RedisDBType.PRIMARY);
                    }
                }
                if (telemetryData != null
                        && (StringUtils.isNotEmpty(telemetryData.getLongitude())
                        || StringUtils.isNotEmpty(telemetryData.getLatitude())
                        || StringUtils.isNotEmpty(telemetryData.getAltitude())
                        || telemetryData.getCalibration() != null)) {

                    UavNestBriefInfo existNestInfo = uavAsyncSendMsgService.selectNestInfoByCode(uavNestDeviceCode);

                    log.info("handleUavNestBindLogic uavNestInfo:{} existNestInfo:{}",
                            JSON.toJSONString(uavNestInfo), JSON.toJSONString(existNestInfo));

                    if (existNestInfo != null && Objects.equals(existNestInfo.getPositionFix(), 0)) {
                        // 更新车载机巢经纬度、挂高、标定
                        String updateLongitude = telemetryData.getLongitude();
                        String updateLatitude = telemetryData.getLatitude();
                        Date latLonUpdateTime = null;
                        // 更新经纬度非空且经纬度有变化
                        if (StringUtils.isNotEmpty(updateLongitude) && StringUtils.isNotEmpty(updateLatitude)
                                && (!StringUtils.equals(updateLongitude, existNestInfo.getLongitude())
                                || !StringUtils.equals(updateLatitude, existNestInfo.getLatitude()))) {
                            latLonUpdateTime = new Date();
                        } else {
                            updateLongitude = null;
                            updateLatitude = null;
                        }

                        uavAsyncSendMsgService.updateUavNestPosition(uavNestDeviceCode,
                                updateLongitude , updateLatitude, telemetryData.getAltitude(),
                                telemetryData.getCalibration(), latLonUpdateTime);
                    }
                }
            } catch (Exception e) {
                log.error("ufcpNestInfo handle nestInfo error:", e);
            }
        }
    }

    /**
     * 机巢实时信息
     */
    @KafkaListener(topics = {"UFCP-NEST-INFO"},containerFactory = "kafkaListenerContainerFactoryK6Batch")
    public void ufcpNestInfo(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                log.info("ufcpNestInfo-KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                UavNestInfo uavNestInfo = JSON.parseObject(message, UavNestInfo.class);
                if (ObjectUtil.isNotNull(uavNestInfo)){

                    // 处理无人机、机巢绑定关系
                    handleUavNestBindLogic(uavNestInfo);

                    UavNestInfo.TelemetryData telemetryData = uavNestInfo.getData();
                    telemetryData.setDeviceCode(uavNestInfo.getDeviceCode());
                    // socket
                    uavAsyncSendMsgService.sendUfcpNestMsg(telemetryData);
                }
            }
        }catch (Exception e){
            log.error("ufcpNestInfo 消费异常:{}", e);
        }
    }

    @KafkaListener(topics = {"UAV-EVENTS-HMS"},containerFactory = "kafkaListenerContainerFactoryK6Batch")
    public void hmsInfo(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                log.info("hmsInfo-KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                UavEventHms uavEventHms = JSON.parseObject(message, UavEventHms.class);
                uavHmsSmsSuppressService.onAlarm(uavEventHms, consumerRecord.timestamp());

            }
        }catch (Exception e){
            log.error("hmsInfo 消费异常", e);
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
}
