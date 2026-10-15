package com.chinatower.receive.kafka;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
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

    public static final String TOPICNAME = "topicName:";
    
    
    /**
     * 发送到kafka
     *
     * @return
     * @author chenmf
     * @date 2022/4/1
     * @version
     */
    public void sendToKafka(String topicName, String jsonStr) {
        kafkaTemplate.send(topicName, jsonStr);
        log.info("topicMessage:" + jsonStr);
        log.info(TOPICNAME+topicName+"发送成功!");
    }
    
    /**
     * 发送到kafka
     *
     * @return
     * @author chenmf
     * @date 2022/4/1
     * @version
     */
    public void sendToKafka(String topicName,JSONArray jsonArray) {
        if (!jsonArray.isEmpty()) {
            for(int i=0; i<jsonArray.size(); i++) {
                try {
                    String message = jsonArray.getJSONObject(i).toJSONString();
                    kafkaTemplate.send(topicName, message);
                    log.info("topicMessage:" + message);
                    log.info(TOPICNAME+topicName+"发送成功!");
                } catch(Exception e) {
                    log.error(TOPICNAME+topicName+"发送失败!", e);
                }
            }
        }
    }
}
