package com.chinatower.platform.modules.alarm.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 告警分级 Kafka 生产者
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class AiJudgeKafkaProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("告警分级Kafka发送成功, topic={}, message={}", topic, message);
    }
}
