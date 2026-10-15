package com.chinatower.platform.kafka;

import com.chinatower.common.core.dto.BuryPointDTO;
import com.chinatower.common.core.inteceptor.BuryPointKafkaProducer;
import com.google.gson.Gson;
import com.chinatower.platform.elasticsearch.service.BuryPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 埋点日志 Kafka 消费者。替代原 /burypoint/saveOne Feign 接口，消费后落 ES。
 */
@Slf4j
@Component
public class BuryPointKafkaConsumer {

    @Autowired
    private BuryPointService buryPointService;

    // 默认不使用 ES，与 BuryPointController 保持一致
    @Value("${spring.elasticsearch.enabled:}")
    private Boolean esEnabled;

    private static final Gson GSON = new Gson();

    // 专用消费组，不与业务消费组(videoTower等)冲突；bootstrap-servers/反序列化器均继承 public.yml 的 K1
    @KafkaListener(topics = BuryPointKafkaProducer.TOPIC, groupId = "tower-log-burypoint")
    public void consume(String message) {
        log.info("收到埋点日志消息, message:{}", message);
        try {
            if (Objects.isNull(esEnabled) || !esEnabled) {
                log.info("ES 未开启(spring.elasticsearch.enabled={}), 跳过落库", esEnabled);
                return;
            }
            BuryPointDTO buryPointDTO = GSON.fromJson(message, BuryPointDTO.class);
            log.info("埋点日志解析成功, operUrl:{}, title:{}", buryPointDTO.getOperUrl(), buryPointDTO.getTitle());
            buryPointService.save(buryPointDTO);
            log.info("埋点日志落 ES 成功, operUrl:{}", buryPointDTO.getOperUrl());
        } catch (Exception e) {
            log.error("消费埋点日志失败, message:{}", message, e);
        }
    }
}
