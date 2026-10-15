package com.chinatower.platform.project.tool.kafka;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName KafkaProducer
 * @Deacription kafka生产者
 * @Author chenmf
 * @Date 2022/4/1 9:34
 * @Version 1.0
 **/
@Component
@Slf4j
public class KafkaProducer {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    
    
    /**
     * 发送到kafka
     *
     * @return
     * @author chenmf
     * @date 2022/4/1
     * @version
     */
    public void sendChannelMessage(String channel, String message) {
        kafkaTemplate.send(channel, message);
    }
}
