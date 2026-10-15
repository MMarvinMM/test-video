package com.chinatower.sync.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * K2 的kafka配置
 *
 * @author zhengqian
 */
@Slf4j
@Configuration
@Data
public class K2KafkaConfiguration {

    @Value("${spring.kafka4.bootstrap-servers}")
    private String consumerBootstrapServers;

    @Value("${spring.kafka4.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka4.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka4.consumer.enable-auto-commit}")
    private Boolean enableAutoCommit;

    @Value("${spring.kafka4.bootstrap-servers}")
    private String producerBootstrapServers;


    @Bean
    @Primary
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactoryK2() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryK2());
//        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactoryK2() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsK2());
    }

    @Bean
    public Map<String, Object> consumerConfigsK2() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean
    public Map<String, Object> producerConfigsK2() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryK2() {
        return new DefaultKafkaProducerFactory<>(producerConfigsK2());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateK2() {
        return new KafkaTemplate<>(producerFactoryK2());
    }
}