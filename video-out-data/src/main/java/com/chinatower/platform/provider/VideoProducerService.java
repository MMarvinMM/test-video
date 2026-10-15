package com.chinatower.platform.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author wangzz
 * @Date 2019/2/25 15:03
 * @Description
 */
@Service
@Slf4j
public class VideoProducerService {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplateK2;

    //发送消息
    public void sendMsg(String channel, String message) {
        kafkaTemplateK2.send(channel, message);
    }

    //发送通道消息
    public void sendChannelMessage(String channel, String message) {
        kafkaTemplate.send(channel, message);
    }
}