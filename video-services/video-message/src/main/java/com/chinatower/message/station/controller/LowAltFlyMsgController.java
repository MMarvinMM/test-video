package com.chinatower.message.station.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.LowAltPointMsg;
import com.chinatower.message.api.model.MsgKafkaConsumerModel;
import com.chinatower.message.constant.WebSocketConstant;
import com.chinatower.message.kafka.MessageKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 低空飞行服务轨迹接口
 */
@RestController
@RequestMapping("/lowalt/flyTrace")
@Slf4j
public class LowAltFlyMsgController extends BaseController {

    @Resource
    MessageKafkaProducer kafkaProducer;

    /**
     * 低空态势服务增量轨迹点位消息通知
     */
    @PostMapping("/lowAltPointNotice")
    public Result<String> lowAltPointNotice(@RequestBody LowAltPointMsg lowAltPointMsg) {
        try {
            // 低空态势服务增量轨迹点位消息通知 接受消息 发送kafka
            MsgKafkaConsumerModel<LowAltPointMsg> msgKafkaConsumerModel = new MsgKafkaConsumerModel<>();
            msgKafkaConsumerModel.setType(WebSocketConstant.LOW_ALT_CONFIG_TYPE);
            msgKafkaConsumerModel.setT(lowAltPointMsg);
            kafkaProducer.sendToKafka(WebSocketConstant.KAFKA_TOPIC_WEBSOCKET_MSG, JSON.toJSONString(msgKafkaConsumerModel));
        } catch (Exception e) {
            log.info("低空态势服务推送kafka报错",e);
        }
        return Result.success("低空态势服务增量轨迹点位消息通知成功");
    }

}
