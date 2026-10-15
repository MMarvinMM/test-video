package com.chinatower.common.core.inteceptor;

import com.chinatower.common.core.dto.BuryPointDTO;
import com.google.gson.Gson;
import com.chinatower.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * 埋点日志 Kafka 生产者。所有服务统一走 Kafka 上报埋点，替代原 Feign 调用。
 *
 * <p>由 {@link GlobalEmbeddedPointConfig} 的 ImportSelector 注册为 Bean（不用 @Component，
 * 与拦截器/过滤器保持一致的注册方式）。
 *
 * <p><b>为什么内部 KafkaTemplate 用 {@code new} 构建、而不是像业务 KafkaConfiguration 那样
 * 声明成 {@code @Bean}（务必保持，勿改）：</b>
 * <ol>
 *   <li>Bean 名冲突：video-bdm 等已定义 {@code @Bean kafkaTemplate()/producerFactory()}，
 *       common-core 再定义会与之覆盖/歧义（叠加 allow-bean-definition-overriding 更危险）。</li>
 *   <li>自动装配劫持：Spring Boot {@code KafkaAutoConfiguration.kafkaTemplate} 带
 *       {@code @ConditionalOnMissingBean(KafkaTemplate.class)}。只要这里注册任意 KafkaTemplate Bean，
 *       Boot 就不再创建默认 kafkaTemplate，导致 video-order 这类「@Autowired KafkaTemplate 靠自动装配」
 *       的服务按类型误注入到埋点 template，把业务消息发错集群。</li>
 * </ol>
 * 因此这里用 {@code new} 构建、不入 Spring 容器，与业务 KafkaTemplate 物理隔离：
 * 「已用 Kafka」的服务不冲突，「未用 Kafka」的服务也开箱即用。
 *
 * <p>集群地址直接复用平台默认 K1 集群 {@code spring.kafka.bootstrap-servers}（各服务都从
 * public.yml 继承），与 tower-log 消费端同集群，无需额外配置。未取到时优雅降级为不上报，
 * 绝不阻断服务启动。
 */
@Slf4j
public class BuryPointKafkaProducer {

    /** 埋点日志 topic，生产者与 tower-log 消费者共用 */
    public static final String TOPIC = "video-bury-point";

    private static final Gson GSON = new Gson();

    // 复用平台默认 K1 集群，与 tower-log 消费端同集群
    @Value("${spring.kafka.bootstrap-servers:}")
    private String bootstrapServers;

    private KafkaTemplate<String, String> template;

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(bootstrapServers)) {
            log.warn("未配置 bury.point.kafka.bootstrap-servers, 埋点日志将不上报 Kafka");
            return;
        }
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 埋点日志允许丢失，绝不阻塞业务：不重试、最长阻塞 2s、只等 leader 确认
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 2000);
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        this.template = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
        log.info("BuryPointKafkaProducer 初始化完成, bootstrap-servers={}", bootstrapServers);
    }

    public void send(BuryPointDTO dto) {
        if (template == null) {
            return;
        }
        try {
            template.send(TOPIC, GSON.toJson(dto));
        } catch (Exception e) {
            // 埋点日志发送失败不影响业务
            log.warn("埋点日志发送 Kafka 失败, 已忽略: {}", e.getMessage());
        }
    }
}