package com.chinatower.message.station.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.MsgBody;
import com.chinatower.message.api.model.MsgKafkaConsumerModel;
import com.chinatower.message.api.model.OperationJudgeMsg;
import com.chinatower.message.api.model.OperationJudgeMsgReq;
import com.chinatower.message.constant.WebSocketConstant;
import com.chinatower.message.kafka.MessageKafkaProducer;
import com.chinatower.message.station.service.AsyncSendMsgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * AI智能研判通知小塔打开弹窗
 */
@RestController
@RequestMapping("/msg/operation-judge")
@Api(value = "AI智能研判通知小塔打开弹窗", tags = "AI智能研判通知小塔打开弹窗")
@Slf4j
public class OperationJudgeMsgController extends BaseController {

    @Resource
    MessageKafkaProducer kafkaProducer;


    /**
     * AI智能研判通知小塔打开弹窗
     */
    @PostMapping("/send-notice")
    @ApiOperation(value = "AI智能研判通知小塔打开弹窗", notes = "AI智能研判通知小塔打开弹窗")
    public Result<String> sendNotice(@RequestBody OperationJudgeMsg operationJudgeMsgReq) {
        try {
            MsgKafkaConsumerModel<OperationJudgeMsg> msgKafkaConsumerModel = new MsgKafkaConsumerModel<>();
            msgKafkaConsumerModel.setType(WebSocketConstant.OPERATION_JUDGE_TYPE);
            msgKafkaConsumerModel.setT(operationJudgeMsgReq);
            kafkaProducer.sendToKafka(WebSocketConstant.KAFKA_TOPIC_WEBSOCKET_MSG, JSON.toJSONString(msgKafkaConsumerModel));
            return Result.success("发送小塔打开弹窗通知成功");
        } catch (Exception e) {
            log.error("发送小塔打开弹窗通知异常", e);
            return Result.fail("发送小塔打开弹窗通知异常：" + e.getMessage());
        }
    }


}
