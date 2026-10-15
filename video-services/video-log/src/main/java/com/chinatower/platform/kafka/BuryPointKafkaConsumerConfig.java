package com.chinatower.platform.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 埋点日志消费者专用 Kafka 配置。
 *
 * <p>自建 ConsumerFactory，只放必要项（bootstrap/group/反序列化器/offset/auto-commit），
 * <b>不继承全局 spring.kafka.consumer.*</b>——test 环境那套全局配置里 heartbeat/session 值不匹配，
 * 走 Boot 自动装配会导致 "Heartbeat must be set lower than the session timeout" 启动失败。
 * 与平台其它消费服务(video-bdm 等)的做法一致。
 *
 * <p>Bean 名用默认 {@code kafkaListenerContainerFactory}，Boot 的自动装配 @ConditionalOnMissingBean
 * 会退避；tower-log 无其它 @KafkaListener，作为唯一容器工厂。
 */
@Configuration
public class BuryPointKafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, String> buryPointConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "tower-log-burypoint");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(buryPointConsumerFactory());
        // topic 未建好也不阻断 tower-log 启动
        factory.getContainerProperties().setMissingTopicsFatal(false);
        return factory;
    }
}
