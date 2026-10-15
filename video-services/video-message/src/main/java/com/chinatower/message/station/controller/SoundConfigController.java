package com.chinatower.message.station.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.MsgKafkaConsumerModel;
import com.chinatower.message.api.model.SoundConfig;
import com.chinatower.message.compenont.MessageResultCode;
import com.chinatower.message.constant.WebSocketConstant;
import com.chinatower.message.kafka.MessageKafkaProducer;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author LQ
 */
@RestController
@RequestMapping("/SoundConfig")
@Slf4j
public class SoundConfigController {

    @Resource
    MessageKafkaProducer kafkaProducer;

    /**
     * 发送声音配置消息
     */
    @PostMapping(value = "/sendSoundMes")
    public Result sendSoundMes(@RequestBody SoundConfig soundConfig){
        SysUser user = SecurityUtils.getLoginUser().getUser();
        soundConfig.setUserId(user.getUserId());
        soundConfig.setIndustryCode(user.getIndustryCode());
        try {
            MsgKafkaConsumerModel<SoundConfig> msgKafkaConsumerModel = new MsgKafkaConsumerModel<>();
            msgKafkaConsumerModel.setType(4);
            msgKafkaConsumerModel.setT(soundConfig);
            kafkaProducer.sendToKafka(WebSocketConstant.KAFKA_TOPIC_WEBSOCKET_MSG, JSON.toJSONString(msgKafkaConsumerModel));
        } catch (Exception e) {
            log.info("推送kafka报错");
            e.printStackTrace();
        }
        return  Result.success(MessageResultCode.SUCCESS);
    }
}
