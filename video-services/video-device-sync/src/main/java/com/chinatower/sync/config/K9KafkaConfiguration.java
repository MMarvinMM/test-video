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
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * K9 的kafka配置
 *
 * @author fanzhiwei
 */
@Slf4j
@Configuration
@Data
public class K9KafkaConfiguration {

    @Value("${spring.kafka9.bootstrap-servers}")
    private String consumerBootstrapServers;

    @Value("${spring.kafka9.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka9.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Value("${spring.kafka9.consumer.enable-auto-commit}")
    private Boolean enableAutoCommit;

    @Value("${spring.kafka9.bootstrap-servers}")
    private String producerBootstrapServers;

    @Value("${spring.kafka9.consumer.max-poll-records}")
    private String maxPollRecords;

    @Value("${spring.kafka9.consumer.max-partition-fetch-bytes}")
    private String maxPartitionFetchBytes;

    @Bean
    @Primary
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactoryK9() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryK9());
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean("kafkaListenerContainerFactoryK9Batch")
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>> kafkaListenerContainerFactoryK9Batch() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryK9Batch());
        factory.setBatchListener(true);
        factory.getContainerProperties().setPollTimeout(3000);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactoryK9() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsK9());
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactoryK9Batch() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsK9Batch());
    }

    @Bean
    public Map<String, Object> consumerConfigsK9() {
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
    public Map<String, Object> consumerConfigsK9Batch() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytes);
        return props;
    }

    @Bean
    public Map<String, Object> producerConfigsK9() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryK9() {
        return new DefaultKafkaProducerFactory<>(producerConfigsK9());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplateK9() {
        return new KafkaTemplate<>(producerFactoryK9());
    }
}
