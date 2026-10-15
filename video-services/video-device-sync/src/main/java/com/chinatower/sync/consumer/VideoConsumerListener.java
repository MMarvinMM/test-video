package com.chinatower.sync.consumer;


import com.chinatower.sync.consumer.service.*;
import com.chinatower.sync.consumer.service.HornTaskService;
import com.chinatower.sync.consumer.util.AIotPlatformRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
    private SensorAttributeService sensorAttributeService;
    @Autowired
    private SensorStatusService sensorStatusService;
    @Autowired
    private BroadcastDeviceService broadcastDeviceService;
    @Autowired
    private UavDeviceService uavDeviceService;
    @Autowired
    private RecorderDeviceService recorderDeviceService;

    @Autowired
    private HornContentConfServiceImpl hornContentConfService;
    @Autowired
    private HornTaskService hornTaskService;

    @Autowired
    private AiotEnergyDeviceService aiotEnergyDeviceService;

    @Autowired
    private AIotPlatformRequestUtils aIotUtils;

//    public static final ThreadPoolExecutor poolExecutor =
//            new ThreadPoolExecutor(5,
//                    10,
//                    60,
//                    TimeUnit.SECONDS,
//                    new ArrayBlockingQueue<>(1000),
//                    new ThreadPoolExecutor.CallerRunsPolicy());


    @KafkaListener(topics = {"sensorAttributeReport"},containerFactory = "kafkaListenerContainerFactoryK3Batch")
    public void sensorAttributeConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
//            if (aIotUtils.isCallAIot(null)) {
//                log.info("sensorAttributeConsumer 3.4以上版本不处理");
//                return;
//            }
            log.info("VideoConsumerListener sensorAttributeConsumer receive");
            List<String> messages = new ArrayList<>();
            if (!consumerRecords.isEmpty()){
                consumerRecords.forEach(consumerRecord -> {
                    //判断是否为null
                    Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
                    if (kafkaMessage.isPresent()) {
                        //得到Optional实例中的值
                        String message = (String)kafkaMessage.get();
                        //log.info(Constants.KAFKALISTENER_TOPIC_MSG, consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                        messages.add(message);
                    }
                });
            }
            sensorAttributeService.dealMsg(messages, false);
        }catch (Exception e){
           log.error("sensorAttributeConsumer 消费异常:", e);
        }

    }

    @KafkaListener(topics = {"sensorAttributeReportNew"},containerFactory = "kafkaListenerContainerFactoryK12Batch")
    public void sensorAttributeReportNew(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
                log.info("VideoConsumerListener sensorAttributeReportNew receive");
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
                sensorAttributeService.dealMsg(messages, true);
            }
        }catch (Exception e){
            log.error("sensorAttributeReportNew 消费异常:",e);
        }

    }

    @KafkaListener(topics = {"device_net_info"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void deviceNetInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            boolean isAIot = aIotUtils.isCallAIot(null);
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
            sensorStatusService.dealDeviceMsgNew(messages, isAIot);
        } catch (Exception e) {
            log.error("VideoConsumerListener deviceNetInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"SYS_AIOT_DEVICE_OUT_STATUS"}, containerFactory = "kafkaListenerContainerFactoryK11Batch")
    public void sysAiotDeviceOutStatusConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
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
                sensorStatusService.dealAIotDeviceStatusLogic(messages);
            }

        } catch (DuplicateKeyException ee) {
            log.warn("sysAiotDeviceOutStatusConsumer duplicate msg:{}", ee.getMessage());
        }  catch (Exception e) {
            log.error("VideoConsumerListener sysAiotDeviceOutStatusConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"SYS_AIOT_DEVICE_INFO"}, containerFactory = "kafkaListenerContainerFactoryK11Batch")
    public void sysAiotDeviceInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
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
                sensorStatusService.dealAiotDeviceLogic(messages);
            }
        } catch (Exception e) {
            log.error("VideoConsumerListener sysAiotDeviceInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"device_net_sensor_info"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void sensorStatusConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
                log.info("sensorStatusConsumer 3.4以上版本不处理");
                return;
            }
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
            sensorStatusService.dealSensorMsgNew(messages);
        } catch (DuplicateKeyException ee) {
            log.warn("sensorStatusConsumer duplicate msg:{}", ee.getMessage());
        }  catch (Exception e) {
            log.error("VideoConsumerListener sensorStatusConsumer error, e:", e);
        }
    }

    /**
     * 监听无人机设备表，同步到通用数据库
     * @param consumerRecords
     * @author luo.zhenghong
     * @date 2023/06/16
     * @version release_v1.5.95
     */
    @KafkaListener(topics = {"device_uav_info"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void uavDeviceInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            uavDeviceService.dealUavDeviceMsgNew(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener uavDeviceInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"device_nest_info"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void uavNestDeviceInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            uavDeviceService.dealUavNestDeviceMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener uavNestDeviceInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"device_nest_channel"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void uavNestChannelInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            uavDeviceService.dealUavNestChannelMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener uavNestChannelInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"device_broadcast_info"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void deviceBroadcastInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
                log.info("deviceBroadcastInfoConsumer 3.4以上版本不处理");
                return;
            }
            log.info("VideoConsumerListener deviceBroadcastInfoConsumer receive");
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
            broadcastDeviceService.dealDeviceBroadcastMsgNew(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener deviceBroadcastInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"device_aiot_energy_info"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void deviceAiotEnergyInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            aiotEnergyDeviceService.dealAiotEnergyDeviceMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener deviceAiotEnergyInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"echarge_perf_sync"},containerFactory = "kafkaListenerContainerFactoryK10Batch")
    public void echargePerfSyncConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
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
            aiotEnergyDeviceService.dealEChargePerfSyncMsg(messages);
        }catch (Exception e){
            log.error("echargePerfSyncConsumer 消费异常:",e);
        }

    }

    @KafkaListener(topics = {"car_charge_perf_sync"},containerFactory = "kafkaListenerContainerFactoryK10Batch")
    public void carChargePerfSyncConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
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
            aiotEnergyDeviceService.dealCarChargePerfSyncMsg(messages);
        }catch (Exception e){
            log.error("carChargePerfSyncConsumer 消费异常:",e);
        }

    }

    @KafkaListener(topics = {"devAlarm"},containerFactory = "kafkaListenerContainerFactoryK10Batch")
    public void devAlarmConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
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
            aiotEnergyDeviceService.dealDevAlarmMsg(messages);
        }catch (Exception e){
            log.error("devAlarm 消费异常:",e);
        }
    }

    @KafkaListener(topics = {"devAlarmClear-cl"},containerFactory = "kafkaListenerContainerFactoryK10Batch")
    public void devAlarmClearClConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
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
            aiotEnergyDeviceService.dealDevAlarmClearClMsg(messages);
        }catch (Exception e){
            log.error("devAlarmClear-cl 消费异常:",e);
        }
    }


    /**
     * 监听执法记录仪设备表，同步到通用数据库
     * @param consumerRecords
     */
    @KafkaListener(topics = {"device_report_info"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void recorderDeviceInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            recorderDeviceService.dealRecorderDeviceMsgNew(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener recorderDeviceInfoConsumer error, e:", e);
        }
    }

    /**
     * 监听执法记录仪设备通道表，同步到通用数据库
     * @param consumerRecords
     */
    @KafkaListener(topics = {"device_report_channel"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void recorderDeviceChannelInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            recorderDeviceService.dealRecorderDeviceChannelMsgNew(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener recorderDeviceChannelInfoConsumer error, e:", e);
        }
    }

    /**
     * CREATE TABLE `device_uav_camera_dict_data` (
     *   `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
     *   `dict_code` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相机型号id',
     *   `dict_label` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相机型号',
     *   `manufacturer` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生产厂家',
     *   `manufacturer_code` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '厂商code',
     *   `domain` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `type` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `sub_type` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `range_of_pan` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '云台俯仰角范围',
     *   `range_of_yaw` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '云台偏航角范围',
     *   `support_function` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '支持功能',
     *   `create_time` datetime DEFAULT NULL,
     *   `create_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `update_time` datetime DEFAULT NULL,
     *   `update_by` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `del_flag` int DEFAULT '0' COMMENT '删除状态，0：未删除，1：已删除',
     *   PRIMARY KEY (`id`),
     *   KEY `device_uav_camera_dict_data_dict_code_index` (`dict_code`)
     * ) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='无人机相机枚举信息'
     */
    @KafkaListener(topics = {"device_uav_camera_dict_data"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void uavCameraModelInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            List<String> messages = convert2MessageList(consumerRecords);
            uavDeviceService.dealUavCameraModelMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener uavCameraModelInfoConsumer error, e:", e);
        }
    }

    /**
     * CREATE TABLE `device_uav_camera_lens_dict_data` (
     *   `id` bigint NOT NULL AUTO_INCREMENT,
     *   `device_uav_camera_dict_code` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相机型号code，device_uav_camera_dict_data表dict_code',
     *   `lens_name` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '镜头名称',
     *   `spectral_type` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '光谱类型',
     *   `sensor_size` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '传感器尺寸(mm)',
     *   `pixel_size` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '像元大小(mm)',
     *   `focal_length_type` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '焦距类型',
     *   `zoom_conversion_coefficient` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '变焦转化系数',
     *   `actual_focal_length_range` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '实际焦距范围(mm)',
     *   `equivalent_focal_length_range` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '等效焦距范围(mm)',
     *   `optical_zoom_range` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '光学变焦范围',
     *   `digital_zoom_range` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '数码变焦范围',
     *   `color_palette_style` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '调色盘样式',
     *   `supporting_feature` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '支持功能',
     *   PRIMARY KEY (`id`),
     *   KEY `device_uav_camera_lens__dict_code_index` (`device_uav_camera_dict_code`)
     * ) ENGINE=InnoDB AUTO_INCREMENT=170 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='无人机相机镜头信息'
     */
    @KafkaListener(topics = {"device_uav_camera_lens_dict_data"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void uavCameraParamInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            List<String> messages = convert2MessageList(consumerRecords);
            uavDeviceService.dealUavCameraParamMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener uavCameraParamInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"device_uav_mount_model_dict_data"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void uavMountModelInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            List<String> messages = convert2MessageList(consumerRecords);
            uavDeviceService.dealUavMountModelMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener uavMountModelInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"device_uav_mount_model"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void deviceUavMountModelConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            List<String> messages = convert2MessageList(consumerRecords);
            uavDeviceService.dealUavMountModelRelMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener deviceUavMountModelConsumer error, e:", e);
        }
    }


    private List<String> convert2MessageList(List<ConsumerRecord<?, ?>> consumerRecords) {
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
        return messages;
    }

    @KafkaListener(topics = {"sensorStatusReport"},containerFactory = "kafkaListenerContainerFactoryK3Batch")
    public void locationSensorDeviceConsumer(List<ConsumerRecord<?, ?>> consumerRecords){
        try {
            if (aIotUtils.isCallAIot(null)) {
                log.info("locationSensorDeviceConsumer 3.4以上版本不处理");
                return;
            }
            log.info("---------------topic:sensorStatusReport进入消费阶段");
            List<String> messages = new ArrayList<>();
            if (!CollectionUtils.isEmpty(consumerRecords)){
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
            sensorStatusService.dealLocationSensorDeviceMsg(messages);
            log.info("---------------topic:sensorStatusReport消费结束");
        } catch (DuplicateKeyException ee) {
            log.warn("sensorStatusReport duplicate msg:{}", ee.getMessage());
        } catch (Exception e) {
            log.error("VideoConsumerListener locationSensorDeviceConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"radarStatusReport"},containerFactory = "kafkaListenerContainerFactoryK3Batch")
    public void radarDeviceConsumer(List<ConsumerRecord<?, ?>> consumerRecords){
        try {
            List<String> messages = new ArrayList<>();
            if (!CollectionUtils.isEmpty(consumerRecords)){
                consumerRecords.forEach(consumerRecord -> {
                    //判断是否为null
                    Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
                    if (kafkaMessage.isPresent()) {
                        //得到Optional实例中的值
                        String message = (String)kafkaMessage.get();
                        //log.info(Constants.KAFKALISTENER_TOPIC_MSG, consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                        messages.add(message);
                        //log.info(Constants.KAFKALISTENER_TOPIC_MSG, consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                    }
                });
            }
            sensorStatusService.dealRadarDeviceMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener radarDeviceConsumer error, e:", e);
        }
    }

    /**
     * 监听喇叭音频信息上传，同步修改喇叭上传url地址信息
     * @param consumerRecords
     * @author willem
     * @date 2024/10/21
     * @version
     */
    @KafkaListener(topics = {"smartbc_audio_file_status"}, containerFactory = "kafkaListenerContainerFactoryK11Batch")
    public void hornAudioUploadConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            hornContentConfService.updateFileUrlByAudioId(messages);
        } catch (Exception e) {
            log.error("hornAudioUploadConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"smartbc_txt_file_status"}, containerFactory = "kafkaListenerContainerFactoryK11Batch")
    public void hornTxtUploadConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            hornContentConfService.updateFileUrlByTxtId(messages);
        } catch (Exception e) {
            log.error("hornTxtUploadConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"smartbc_device_status"}, containerFactory = "kafkaListenerContainerFactoryK5Batch")
    public void hornDeviceConsumerV2(List<ConsumerRecord<?, ?>> consumerRecords){
        try {
            if (aIotUtils.isCallAIot(null)) {
                log.info("hornDeviceConsumerV2 3.4以上版本不处理");
                return;
            }
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
            sensorStatusService.dealHornDeviceMsgV2(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener hornDeviceConsumerV2 error, e:", e);
        }
    }

    /**
     * Kafka监听器，用于消费"smartbc_task_status"主题的消息。
     * 更新广播任务状态
     *
     * @param consumerRecords 消费者记录列表
     */
    @KafkaListener(topics = {"smartbc_task_status"}, containerFactory = "kafkaListenerContainerFactoryK11Batch")
    public void hornTaskStatusConsumer(List<ConsumerRecord<?, ?>> consumerRecords){
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
            hornTaskService.updateHornTaskStatus(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener hornTaskStatusConsumer error, e:", e);
        }
    }

    /**
     * 从Kafka中消费设备UAV_NEST_NEST_DATA主题的消息，处理机巢型号
     *
     * @param consumerRecords 包含待处理ConsumerRecord对象的列表
     */
    @KafkaListener(topics = {"device_uav_nest_dict_data"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void uavNestModelInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            uavDeviceService.dealUavNestModelMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener uavNestModelInfoConsumer error, e:", e);
        }
    }

    /**
     * 从KAFKA中消费设备DEVICE_UAV_DICT_DATA主题的消息，处理无人机型号
     *
     * @param consumerRecords 包含待处理ConsumerRecord对象的列表
     */
    @KafkaListener(topics = {"device_uav_dict_data"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void uavModelInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            uavDeviceService.dealUavModelMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener uavNestModelInfoConsumer error, e:", e);
        }
    }

    @KafkaListener(topics = {"UFCP-DEVICE-STATUS-ALARM"}, containerFactory = "kafkaListenerContainerFactoryK1Batch")
    public void uavDeviceStatusInfoConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
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
            uavDeviceService.uavDeviceStatusMsg(messages);
        } catch (Exception e) {
            log.error("VideoConsumerListener uavDeviceStatusInfoConsumer error, e:", e);
        }
    }

    /**
     * 广播平台设备中心 设备同步
     * 逻辑 设备同步kafka消费接口，逻辑为先查自己库，没有就舍弃，有就判断版本字段大于库里就走appflag判断更新表里数据
     *
     * @param consumerRecords 消费记录列表
     */
    @KafkaListener(topics = {"SYS_BC_DEVICE_INFO"}, containerFactory = "kafkaListenerContainerFactoryK11Batch")
    public void dealDeviceBroadcastMsgFromAIot(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
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
                broadcastDeviceService.dealDeviceBroadcastMsgFromAIot(messages);
            }
        } catch (Exception e) {
            log.error("VideoConsumerListener dealDeviceBroadcastMsgFromAIot error, e:", e);
        }
    }


    /**
     * 广播平台设备中心 设备状态同步
     * 逻辑 设备状态同步接口，逻辑为先查自己库，没有就调用反查接口入库，有就判断版本字段大于库里就走appflag判断更新表里数据
     *
     * @param consumerRecords 消费记录列表
     */
    @KafkaListener(topics = {"SYS_BC_DEVICE_OUT_STATUS"}, containerFactory = "kafkaListenerContainerFactoryK11Batch")
    public void dealDeviceBroadcastStatusMsgFromAIot(List<ConsumerRecord<?, ?>> consumerRecords) {
        try {
            if (aIotUtils.isCallAIot(null)) {
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
                broadcastDeviceService.dealDeviceBroadcastStatusMsgFromAIot(messages);
            }
        } catch (Exception e) {
            log.error("VideoConsumerListener dealDeviceBroadcastMsgFromAIot error, e:", e);
        }
    }
}
