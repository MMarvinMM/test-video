package com.chinatower.platform.config;

import com.alibaba.fastjson.JSONArray;
import com.chinatower.platform.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MessageKafkaProducer {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplateK9;

    /**
     * 发送到kafka
     *
     * @return
     * @author chenmf
     * @date 2022/4/1
     * @version
     */
    @Async("threadPoolTaskExecutor")
    public void sendToKafka(String topicName, String jsonStr) {
        log.info("send topic message:" + jsonStr);
        kafkaTemplateK9.send(topicName, jsonStr);
        log.info(Constants.TOPIC_NAME +topicName+"发送成功!");
    }
}
