package com.chinatower.order.kafka;

import com.alibaba.fastjson.JSONArray;
import com.chinatower.platform.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

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
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    
    /**
     * 发送到kafka
     *
     * @return
     * @author chenmf
     * @date 2022/4/1
     * @version
     */
    @Async("asyncOrderExecutor")
    public void sendToKafka(String topicName, String jsonStr) {
        log.info("topicMessage:" + jsonStr);
        kafkaTemplate.send(topicName, jsonStr);
        log.info(Constants.TOPIC_NAME +topicName+"发送成功!");
    }
    
    /**
     * 发送到kafka
     *
     * @return
     * @author chenmf
     * @date 2022/4/1
     * @version
     */
    @Async("asyncOrderExecutor")
    public void sendToKafka(String topicName,JSONArray jsonArray) {
        if (!jsonArray.isEmpty()) {
            for(int i=0; i<jsonArray.size(); i++) {
                try {
                    String message = jsonArray.getJSONObject(i).toJSONString();
                    log.info("topicMessage:" + message);
                    kafkaTemplate.send(topicName, message);
                    log.info(Constants.TOPIC_NAME+topicName+"发送成功!");
                } catch(Exception e) {
                    log.error(Constants.TOPIC_NAME+topicName+"发送失败!", e);
                }
            }
        }
    }
}
