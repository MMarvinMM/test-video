package com.chinatower.bdm.kafka;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.platform.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MessageKafkaProducer {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplateK2;


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
        log.info("send topic message:{}" , jsonStr);
        try {
            SendResult<String, String> result = kafkaTemplate.send(topicName, jsonStr).get();
            log.info("{} 发送成功 {}",topicName,result.getRecordMetadata().offset());
        }catch (Exception e){
            log.error("{} 消息发送失败", topicName, e);
        }
    }

    @Async("threadPoolTaskExecutor")
    public void sendToKafka2(String topicName, String jsonStr) {
        log.info("send topic message:{}" , jsonStr);
        try {
            SendResult<String, String> result = kafkaTemplateK2.send(topicName, jsonStr).get();
            log.info("{} 发送成功 {}",topicName,result.getRecordMetadata().offset());
        }catch (Exception e){
            log.error("{} 消息发送失败", topicName, e);
        }
    }

    /**
     * 发送到kafka
     *
     * @return
     * @author chenmf
     * @date 2022/4/1
     * @version
     */
    @Async("threadPoolTaskExecutor")
    public void sendToKafka(String topicName, JSONArray jsonArray,Integer type) {
        if (!jsonArray.isEmpty()) {
            for(int i=0; i<jsonArray.size(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    jsonObject.put("type",type);
                    String message = jsonObject.toJSONString();
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
