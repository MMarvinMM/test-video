package com.chinatower.sync.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.sync.consumer.entity.YgDeviceChannelDO;
import com.chinatower.sync.consumer.item.DeviceSyncConstants;
import com.chinatower.sync.consumer.service.IDeviceInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * video-device-sync
 * @ClassName DeviceChannelSyncController
 * @Description 设备kafka同步
 * @Author hued
 * @Date 2024年7月10日15:12:22
 */
@RestController
@Slf4j
public class DeviceChannelSyncController {
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    IDeviceInfoRepository deviceInfoRepositoryImpl;

    @KafkaListener(topics = {"yg_device_channel"},containerFactory = "kafkaListenerContainerFactoryK2Device")
    public void ygDeviceConsumer(ConsumerRecord<?, ?> consumerRecord) {
        log.info("ygDeviceConsumerkafka");
        //判断是否为null
        try {
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                String message = (String)kafkaMessage.get();
                //现阶段只关注 ChannelCode  redis暂存数据 只存ChannelCode 方便提高定时执行速度
                YgDeviceChannelDO ygDeviceChannelDO = JSON.parseObject(message, YgDeviceChannelDO.class);
                // 新增修改list
                List<String> list = new ArrayList<>();
            /*
                2022-11-28 修改删除同步逻辑  该处理被放弃
                为避免出现 删除 新增顺序出错
                比如 先推删除  后推新增 ->  但是定时器执行时  新增在前，删除在后。那么会出现删除正常数据的情况。考虑到删除数据量不到，直接同步删除。
                故删除设备不走定时器批量删除逻辑 走实时删除逻辑
             */
                for (YgDeviceChannelDO.DataDTO dataDTO:ygDeviceChannelDO.getData()){
                    list.add(dataDTO.getChannelCode());
                }
                if (0 < list.size()) {
                    //去重
                    List<String> collect = list.stream().distinct().collect(Collectors.toList());
                    Long addCount = redisTemplate.opsForSet().add(DeviceSyncConstants.VIDEO_DEVICE_CHANNEL_CODE_LIST_KEY, list.toArray());
                    log.info("{}条channelCode存入通道新增修改 set redis:{}", addCount, collect);
                }
            }
        } catch (Exception e) {
            log.error("ygDeviceConsumerKafka异常", e);
        }

    }

    /**
     * 视频质量诊断结果【VMS_VQDS_RESULT Kafka】 数据对接文档
     */
    @KafkaListener(topics = {"VMS_VQDS_RESULT"},containerFactory = "kafkaListenerContainerFactoryK9Batch")
    public void vmsVodResult(List<ConsumerRecord<?, ?>> consumerRecords) {
        long start = System.currentTimeMillis();
        try {
            if (!CollectionUtils.isEmpty(consumerRecords)) {
                List<String> messages = new ArrayList<>();
                consumerRecords.forEach(item -> {
                    //判断是否为null
                    Optional<?> kafkaMessage = Optional.ofNullable(item.value());
                    if (kafkaMessage.isPresent()) {
                        String message = (String)kafkaMessage.get();
                        messages.add(message);
                    }
                });
                deviceInfoRepositoryImpl.dealVmsVodResult(messages);
            }
        } catch (Exception e) {
            log.error("vmsVodResult",e);
        }
        log.info(start + "vmsVodResult整体耗时=" + (System.currentTimeMillis() - start));
    }

}
