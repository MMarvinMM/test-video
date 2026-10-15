package com.chinatower.receive.kafka.consumer;


import cn.hutool.json.JSONUtil;
import com.chinatower.platform.util.DateUtils;
import com.chinatower.receive.constant.AlarmConstants;
import com.chinatower.receive.model.ReceiveAlarmVOReq;
import com.chinatower.receive.service.ReceiveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;


@Component
@Slf4j
public class KafkaConsumer {

    @Autowired
    private ReceiveService receiveService;

    /**
     * 大模型告警消费
     *
     * @param consumerRecord
     */
    @KafkaListener(topics = {AlarmConstants.LARGE_MODEL_SCENE_ALARM_TOPIC}, containerFactory = "kafkaListenerContainerFactory")
    public void largeModelSceneAlarmConsumer(ConsumerRecord<?, ?> consumerRecord) {
        try {
            //判断是否为null
            Optional<?> kafkaMessage = Optional.ofNullable(consumerRecord.value());
            if (kafkaMessage.isPresent()) {
                //得到Optional实例中的值
                String message = (String) kafkaMessage.get();
                log.info("KafkaListener topic: {}, partition: {}, offset: {}, kafkaTimestamp: {}, systemTimestamp: {}, msg:{}", consumerRecord.topic(), consumerRecord.partition(), consumerRecord.offset(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date(consumerRecord.timestamp())), DateUtils.getTime(), message);
                ReceiveAlarmVOReq receiveAlarmVOReq = JSONUtil.toBean(message, ReceiveAlarmVOReq.class);
                receiveService.receiveAlarm(receiveAlarmVOReq);
            }
        } catch (Exception e) {
            log.error("largeModelSceneAlarm 消费异常:{}", e);
        }
    }
}
