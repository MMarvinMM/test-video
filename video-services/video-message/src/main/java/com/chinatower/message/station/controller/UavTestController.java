package com.chinatower.message.station.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.MsgKafkaConsumerModel;
import com.chinatower.message.api.model.RadarBindUserModel;
import com.chinatower.message.api.model.RadarObject;
import com.chinatower.message.constant.WebSocketConstant;
import com.chinatower.message.kafka.MessageKafkaProducer;
import com.chinatower.message.kafka.UavMessageKafkaConsumer;
import com.chinatower.message.station.entity.AirQualityData;
import com.chinatower.message.station.entity.UavEventHms;
import com.chinatower.message.station.entity.UavNestInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author WLF
 * @description
 * @date 2025/5/30 10:08
 */
@Slf4j
@Validated
@RestController
public class UavTestController {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplateK6;

    @Autowired
    private UavMessageKafkaConsumer uavMessageKafkaConsumer;

    /**
     * 发送雷达消息
     * @param
     * @return
     */
    @PostMapping("/msgUav/sendUavToKafka")
    Result<Boolean> sendToKafka(@RequestBody AirQualityData airQualityData) {
        try {
            log.info("sendUavToKafka入参{}", JSON.toJSONString(airQualityData));
            try {
                kafkaTemplateK6.send("UFCP-GAS-INFO", JSON.toJSONString(airQualityData));
            } catch (Exception e) {
                log.error("发送消息到Kafka异常", e);
                return Result.fail();
            }
        } catch (Exception e) {
            log.error("sendRadarMsg2Client 异常", e);
            return Result.fail();
        }
        return Result.success("ok");
    }

    @PostMapping("/test/uavBind")
    public Result<String> uavBind(@RequestBody UavNestInfo uavNestInfo) {
        uavMessageKafkaConsumer.handleUavNestBindLogic(uavNestInfo);
        return Result.success("success");
    }

    @PostMapping("/test/hms")
    public Result<String> uavHms(@RequestBody UavEventHms uavEventHms) {
        kafkaTemplateK6.send("UAV-EVENTS-HMS", JSON.toJSONString(uavEventHms));
        return Result.success("success");
    }



}
