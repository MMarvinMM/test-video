package com.chinatower.platform.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.event.entity.dto.ReceiveRegionAlarmDto;
import com.chinatower.event.entity.vo.ReceiveSatelliteAlarmApiVo;
import com.chinatower.message.api.model.RadarObject;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.consumer.entity.EpochalGeoVo;
import com.chinatower.platform.consumer.service.*;
import com.chinatower.platform.consumer.utils.AIotPlatformRequestUtils;
import com.chinatower.platform.util.DateUtils;
import com.chinatower.receive.api.IReceiveAlarmNewService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @className: VideoConsumerListener
 * @description: 物联设备消费监听
 * @author: li.xufu
 * @date: 2022/8/4
 * @requestID 需求单号及需求名称
 */
@Component
@Slf4j
public class VideoConsumerListener {

    @Autowired
    private SensorStatusService sensorStatusService;

    @Autowired
    private WarningOrderService warningOrderService;

    @Autowired
    private AlarmOperateService alarmOperateService;

    @Autowired
    private IReceiveAlarmNewService iReceiveAlarmService;

    @Autowired
    private DeviceIndexInfoService deviceIndexInfoService;

    @Resource
    RadarMsgHandler radarMsgHandler;

    @Resource
    OrganDeviceRelService       organDeviceRelService;

    @Resource
    SysDeptService              sysDeptService;

    @Autowired
    WaterLevelService waterLevelService;

    @Autowired
    PresetArrivalNoticeService presetArrivalNoticeService;

    @Autowired
    VmDeviceSyncService VmDeviceSyncService;

    @Autowired
    private AIotPlatformRequestUtils aIotUtils;
    @Autowired
    private EpochalGeoAnalyticsService epochalGeoAnalyticsService;

    /**
     * 雷达数据处理
     * @param consumerRecord
     */
    @KafkaListener(topics = {"unionTargetJs"},containerFactory = "kafkaListenerContainerFactoryK2")
    public void radarReportConsumer(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                log.info("接收到雷达目标：{}  ", message);
                RadarObject radarObject = radarMsgPreHandle(message);

                //持久化操作
                radarMsgHandler.addToPersistenceContainer(radarObject);

                //推送的消息映射信息，key 是 租户id_行业编码
                Map<Object, Object> mapping = radarMsgHandler.needPushMapping(radarObject);
                log.info("mapping:{}", mapping);
                if (MapUtils.isEmpty(mapping)) {
                    return;
                }
                log.info("需要推送的消息映射信息:{}", mapping);
                radarMsgHandler.doPush(mapping);
            }
        } catch (Exception e) {
            log.error("unionTargetJs 消费异常:", e);
        }
    }

    private static RadarObject radarMsgPreHandle(String message) {
        RadarObject radarObject = JSON.parseObject(message, RadarObject.class);
//        if (radarObject.getLatitude() != null && radarObject.getLongitude() != null) {
//            GPS gps = GPSConverterUtils.gps84_To_Gcj02(radarObject.getLatitude(), radarObject.getLongitude());
//            if (gps != null) {
//                radarObject.setLatitude(gps.getLat());
//                radarObject.setLongitude(gps.getLon());
//            }
//        }
        return radarObject;
    }

    /**
     * 雷达告警目标数据处理
     */
    @KafkaListener(topics = {"warningTarget"},containerFactory = "kafkaListenerContainerFactoryK2")
    public void warningTargetConsumer(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                sensorStatusService.dealWarningTargetMsg(message);
            }
        }catch (Exception e){
            log.error("warningTarget 消费异常:", e);
        }
    }

    /**
     * 行业应用支撑：监听工单表，实时同步到mongoDB中
     * @param consumerRecord
     * @author lyy
     * @date 2022/10/19
     */
    @KafkaListener(topics = {"vm_warning_order_t"}, containerFactory = "kafkaListenerContainerFactory")
    public void warningOrderInfoConsumer(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                //log.info("KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                warningOrderService.dealOrderMsg(message);
            }
        }catch (Exception e){
            log.error("vm_warning_order_t 消费异常:{}", e);
        }
    }

    /**
     * 行业应用支撑：监听工单环节流程表，实时同步到mongoDB中
     * @param consumerRecord
     * @author lyy
     * @date 2022/10/19
     */
    @KafkaListener(topics = {"vm_order_flow_record_t"}, containerFactory = "kafkaListenerContainerFactory")
    public void orderFlowRecordInfoConsumer(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                //log.info("KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                warningOrderService.dealRecordMsg(message);
            }
        }catch (Exception e){
            log.error("vm_order_flow_record_t 消费异常:{}", e);
        }
    }

    /**
     * 监听运营研判表，实时同步到mongoDB中
     * @param consumerRecord
     * @author lyy
     * @date 2022/04/03
     * @version v1.5.80
     */
    @KafkaListener(topics = {"vm_alarm_operate_t"}, containerFactory = "kafkaListenerContainerFactory")
    public void alarmOperateConsumer(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                //log.info("KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                //alarmOperateService.dealOperateMsg(message);
            }
        }catch (Exception e){
            log.error("vm_alarm_operate_t 消费异常:{}", e);
        }

    }

    /**
     * 监听告警拦截表，实时同步到mongoDB中
     * @param consumerRecord
     * @author lyy
     * @date 2022/04/03
     * @version v1.5.80
     */
    @KafkaListener(topics = {"vm_warning_intercept_t"}, containerFactory = "kafkaListenerContainerFactory")
    public void warningInterceptConsumer(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                //log.info("KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                //alarmOperateService.dealInterceptMsg(message);
            }
        }catch (Exception e){
            log.error("vm_warning_intercept_t 消费异常:{}", e);
        }

    }

    /**
     * 卫片告警上报消费
     * @param consumerRecord
     * @author songxinfeng
     * @date 2024/05/06
     */
    @KafkaListener(topics = {"receiveRegionAlarm"}, containerFactory = "kafkaListenerContainerFactory")
    public void receiveRegionAlarm(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                recordLog(consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.timestamp(), message);

                JSONArray array = JSON.parseArray(message);
                List<ReceiveRegionAlarmDto> list =array.toJavaList(ReceiveRegionAlarmDto.class);
                iReceiveAlarmService.receiveRegionAlarm(list);
            }
        }catch (Exception e){
            log.error("receiveRegionAlarm 消费异常:{}", e);
        }
    }

    /**
     * 组织与设备关系表数据消费
     * @param consumerRecord
     */
    @KafkaListener(topics = {"vm_organ_device_rel_t"}, containerFactory = "kafkaListenerContainerFactory")
    public void organDeviceRelConsumer(ConsumerRecord<?, ?> consumerRecord){
        try {
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                String message = (String)kafkaMessage.get();
                // log.info("开始消费vm_organ_device_rel_t表dml消息：{}", message);
                organDeviceRelService.dealMsg(message);
            }
        }catch (Exception e){
            log.error("vm_organ_device_rel_t 消费异常:{}", e);
        }
    }

    /**
     * 部门表数据消费
     * @param consumerRecord
     */
    @KafkaListener(topics = {"sys_dept"}, containerFactory = "kafkaListenerContainerFactory")
    public void sysDeptConsumer(ConsumerRecord<?, ?> consumerRecord){
        try {
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                String message = (String)kafkaMessage.get();
                //log.info("开始消费sys_dept表dml消息：{}", message);
                sysDeptService.dealMsg(message);
            }
        }catch (Exception e){
            log.error("sys_dept 消费异常:{}", e);
        }
    }

    /**
     * 传感器文件信息消费
     * @param consumerRecords
     */
    @KafkaListener(topics = {"sensorFileReport"},containerFactory = "kafkaListenerContainerFactoryK3Batch")
    public void sensorFileConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
                log.info("sensorFileConsumer 3.4以上版本不处理");
                return;
            }
            log.info("VideoConsumerListener sensorFileConsumer receive");
            List<String> messages = new ArrayList<>();
            if (!consumerRecords.isEmpty()){
                consumerRecords.forEach(consumerRecord -> {
                    //判断是否为null
                    Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
                    if (kafkaMessage.isPresent()) {
                        //得到Optional实例中的值
                        String message = (String)kafkaMessage.get();
                        messages.add(message);
                    }
                });
            }
            sensorStatusService.dealSensorFileMsg(messages);
        }catch (Exception e){
            log.error("sensorFileReport 消费异常:",e);
        }
    }

    /**
     * 传感器告警信息消费
     * @param consumerRecords
     */
    @KafkaListener(topics = {"sensorEventReport"},containerFactory = "kafkaListenerContainerFactoryK3Batch")
    public void sensorEventConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
                log.info("sensorEventConsumer 3.4以上版本不处理");
                return;
            }
            log.info("VideoConsumerListener sensorEventConsumer receive");
            List<String> messages = new ArrayList<>();
            if (!consumerRecords.isEmpty()){
                consumerRecords.forEach(consumerRecord -> {
                    //判断是否为null
                    Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
                    if (kafkaMessage.isPresent()) {
                        //得到Optional实例中的值
                        String message = (String)kafkaMessage.get();
                        messages.add(message);
                    }
                });
            }
            sensorStatusService.dealSensorEventMsg(messages);
        }catch (Exception e){
            log.error("sensorEventReport 消费异常:",e);
        }
    }

    @KafkaListener(topics = {"sensorFileReportNew"},containerFactory = "kafkaListenerContainerFactoryK12Batch")
    public void sensorFileReportNew(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
                log.info("VideoConsumerListener sensorFileReportNew receive");
                List<String> messages = new ArrayList<>();
                if (!consumerRecords.isEmpty()){
                    consumerRecords.forEach(consumerRecord -> {
                        //判断是否为null
                        Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
                        if (kafkaMessage.isPresent()) {
                            //得到Optional实例中的值
                            String message = (String)kafkaMessage.get();
                            messages.add(message);
                        }
                    });
                }
                sensorStatusService.dealSensorFileMsg(messages);
            }
        }catch (Exception e){
            log.error("sensorAttributeConsumer 消费异常:",e);
        }
    }

    /**
     * 传感器告警信息消费
     * @param consumerRecords
     */
    @KafkaListener(topics = {"sensorEventReportNew"},containerFactory = "kafkaListenerContainerFactoryK12Batch")
    public void sensorEventReportNew(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
                log.info("VideoConsumerListener sensorEventReportNew receive");
                List<String> messages = new ArrayList<>();
                if (!consumerRecords.isEmpty()){
                    consumerRecords.forEach(consumerRecord -> {
                        //判断是否为null
                        Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
                        if (kafkaMessage.isPresent()) {
                            //得到Optional实例中的值
                            String message = (String)kafkaMessage.get();
                            messages.add(message);
                        }
                    });
                }
                sensorStatusService.dealSensorEventMsg(messages);
            }
        }catch (Exception e){
            log.error("sensorEventReportNew 消费异常:",e);
        }
    }

    @KafkaListener(topics = {"VMS-DEVICE-INDEX-INFO"}, containerFactory = "kafkaListenerContainerFactoryK8Batch")
    public void deviceIndexInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {

            List<String> messages = new ArrayList<>();
            if (!consumerRecords.isEmpty()) {
                consumerRecords.forEach(consumerRecord -> {
                    //判断是否为null
                    Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
                    if (kafkaMessage.isPresent()) {
                        String message = (String)kafkaMessage.get();
                        log.info("deviceIndexInfoConsumer接收到的参数为：{}",message);
                        messages.add(message);
                    }
                });
            }
            deviceIndexInfoService.delDeviceIndexInfo(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener deviceIndexInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"VMS-PRESET"}, containerFactory = "kafkaListenerContainerFactoryK9Batch")
    public void presetArrivalNoticeConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            this.commonPresetArrivalNotice(consumerRecords, Constants.ONE);
        } catch (Exception e) {
            log.error("VideoConsumerListener presetArrivalNoticeConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"VIDEO-PLATFORM-VMS-PRESET"}, containerFactory = "kafkaListenerContainerFactoryK3Batch")
    public void presetArrivalNoticeConsumerLocation(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            this.commonPresetArrivalNotice(consumerRecords, Constants.TWO);
        } catch (Exception e) {
            log.error("VideoConsumerListener presetArrivalNoticeConsumerLocation error, e:", e);
        }
    }

    /**
     *
     * @param consumerRecords
     * @param flag 1:中心消息  2:属地消息
     */
    private void commonPresetArrivalNotice(List<ConsumerRecord<?, ?>> consumerRecords,String flag){
        List<String> messages = new ArrayList<>();
        if (!consumerRecords.isEmpty()) {
            consumerRecords.forEach(consumerRecord -> {
                //判断是否为null
                Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
                if (kafkaMessage.isPresent()) {
                    String message = (String)kafkaMessage.get();
                    messages.add(message);
                }
            });
        }
        presetArrivalNoticeService.delPresetArrivalNotice(messages,flag);
    }

    @KafkaListener(topics = {"vm_device_info_topic"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void vmDeviceInfoSync(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            List<String> messages = new ArrayList<>();
            if (!consumerRecords.isEmpty()) {
                consumerRecords.forEach(consumerRecord -> {
                    //判断是否为null
                    Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
                    if (kafkaMessage.isPresent()) {
                        String message = (String)kafkaMessage.get();
                        messages.add(message);
                    }
                });
            }
            VmDeviceSyncService.vmDeviceInfoSync(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener vmDeviceInfoSync error, e:", e);
        }
    }

    @KafkaListener(topics = {"vm_device_model_topic"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void vmDeviceModelInfoSync(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            List<String> messages = new ArrayList<>();
            if (!consumerRecords.isEmpty()) {
                consumerRecords.forEach(consumerRecord -> {
                    //判断是否为null
                    Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
                    if (kafkaMessage.isPresent()) {
                        String message = (String)kafkaMessage.get();
                        messages.add(message);
                    }
                });
            }
            VmDeviceSyncService.vmDeviceModelInfoSync(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener vmDeviceModelInfoSync error, e:", e);
        }
    }

    /**
     * 属地卫星告警
     * @param consumerRecord
     */
    @KafkaListener(topics = {"satelliteAlarm"}, containerFactory = "kafkaListenerContainerFactory")
    public void receiveSatelliteAlarm(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                recordLog(consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.timestamp(), message);

                List<ReceiveSatelliteAlarmApiVo> receiveSatelliteAlarmApiVos = JSON.parseArray(message, ReceiveSatelliteAlarmApiVo.class);
                iReceiveAlarmService.receiveSatelliteAlarmKafka(receiveSatelliteAlarmApiVos);
            }
        }catch (Exception e){
            log.error("receiveSatelliteAlarm 消费异常:{}", e);
        }
    }

    /**
     * 单期遥感解析
     * @param consumerRecord
     */
    @KafkaListener(topics = {"EPOCHAL_GEO_ANALYTICS"}, containerFactory = "kafkaListenerContainerFactory")
    public void epochalGeoAnalytics(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String)kafkaMessage.get();
                recordLog(consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), consumerRecord.timestamp(), message);
                List<EpochalGeoVo> geoVoList = Lists.newArrayList();
                if(message.startsWith("[")) {
                   geoVoList = JSON.parseArray(message, EpochalGeoVo.class);
                } else {
                    geoVoList.add(JSON.parseObject(message,EpochalGeoVo.class));
                }
                epochalGeoAnalyticsService.analytics(geoVoList);
            }
        }catch (Exception e){
            log.error("单期遥感解析 epochalGeoAnalytics 消费异常:{}", e);
        }
    }

    private static void recordLog(String consumerRecord, int consumerRecord1, long consumerRecord2, long consumerRecord3, String message) {
        log.info("KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord, consumerRecord1, consumerRecord2, DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord3)), DateUtils.getTime(), message);
    }
}
