package com.chinatower.platform.provider;

import com.alibaba.fastjson.JSON;
import com.chinatower.platform.consumer.item.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * video-out-data
 */
@RestController
@Slf4j
@RequestMapping("kafka")
public class VideoKafkaController {

    @Autowired
    private VideoProducerService videoProducerService;

    /**
     * 测试发送kafka
     * @param model
     * @return
     */
    @RequestMapping("sendMsg")
    public String sendMsg(@RequestBody ModelMap model){
        if(model.get(Constants.TOPIC) == null){
            return "999";
        }
        String topic = model.get(Constants.TOPIC).toString();
        String message = model.containsKey(Constants.MESSAGE) ? JSON.toJSONString(model.get(Constants.MESSAGE)): null;
        log.info("测试发送kafka报文{}:{}",topic,message);
        try {
            videoProducerService.sendMsg(topic, message);
            return "000";
        }catch (Exception e){
            e.printStackTrace();
            return "999";
        }
    }


    /**
     * 测试发送kafka消息
     * @param model
     * @return
     */
    @RequestMapping("sendMessage")
    public String sendMessage(@RequestBody ModelMap model){
        if(model.get(Constants.TOPIC) == null){
            return "999";
        }
        String topic = model.get(Constants.TOPIC).toString();
        String message = model.containsKey(Constants.MESSAGE) ? model.get(Constants.MESSAGE).toString(): null;
        log.info("测试发送kafka报文{}:{}",topic,message);
        try {
            videoProducerService.sendChannelMessage(topic, message);
            return "000";
        }catch (Exception e){
            e.printStackTrace();
            return "999";
        }
    }
}