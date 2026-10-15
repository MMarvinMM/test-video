package com.chinatower.sync.consumer.controller;

import com.chinatower.sync.consumer.service.IAlgorithmConfigInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * video-device-sync
 * @ClassName DeviceChannelSyncController
 * @Description 算法任务kafka同步
 * @Author hued
 * @Date 2024年7月10日15:12:22
 */
@RestController
@Slf4j
public class AlgorithmConfigSyncController{

    @Resource
    private IAlgorithmConfigInfoService algorithmConfigInfoService;

    @KafkaListener(topics = {"wh_nanal_channel_relation"},containerFactory = "kafkaListenerContainerFactoryK2Batch")
    public void mathFactoryConsumer(List<ConsumerRecord<?, ?>> consumerRecords) {
        long start = System.currentTimeMillis();
        log.info(start + "mathFactoryFromConsumerKafka=" + consumerRecords.size());
        try {
            List<String> messages = new ArrayList<>();
            if (!consumerRecords.isEmpty()) {
                consumerRecords.forEach(item -> {
                    //判断是否为null
                    Optional<?> kafkaMessage = Optional.ofNullable(item.value());
                    if (kafkaMessage.isPresent()) {
                        String message = (String)kafkaMessage.get();
//                        log.info(start + "接受算法仓kafka消息:"+message);
                        messages.add(message);
                    }
                });
            }
            algorithmConfigInfoService.mathFactoryConsumerList(messages);
        } catch (Exception e) {
            log.error("mathFactoryFromConsumerKafka异常",e);
        }
        log.info(start + "mathFactoryFromConsumerKafka整体耗时=" + (System.currentTimeMillis() - start));
    }
}
