package com.chinatower.message.station.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.MsgKafkaConsumerModel;
import com.chinatower.message.api.model.RadarBindUserModel;
import com.chinatower.message.api.model.RadarObject;
import com.chinatower.message.constant.WebSocketConstant;
import com.chinatower.message.kafka.MessageKafkaProducer;
import com.chinatower.message.station.service.AsyncSendMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * video-message
 */
@Slf4j
@Validated
@RestController
public class RadarMsgServiceController {

    @Resource
    MessageKafkaProducer kafkaProducer;
    @Autowired
    AsyncSendMsgService asyncSendMsgService;

    private final Lock batchLock = new ReentrantLock();   // 专门守护 startTime、count、map 的锁
    private volatile Instant startTime; // 记录第一个数据进入的时间

    // 存储用户key和对应的RadarBindUserModel列表
    private volatile ConcurrentHashMap<String,RadarBindUserModel> map = new ConcurrentHashMap<>();
    // 计数器
    private volatile AtomicLong count = new AtomicLong(0);

    private long totalNum = 1000L;

    private long totalTime= 1L;



    /**
     * 发送雷达消息
     * @param model
     * @return
     */
    @PostMapping("/msgApi/sendRadarMsg2Client")
    Result<Boolean> sendRadarMsg2Client(@RequestBody RadarBindUserModel model) {
        try {
            log.info("sendRadarMsg2Client 入参  model = {}", JSON.toJSONString(model));
            String[] split = model.getUserKey().split(":");
            String userId = split[1];
            String industryCode = split[2];

            RadarObject radarObject = model.getRadarObject();
            radarObject.setIndustryCode(industryCode);
            radarObject.setS_class(radarObject.getSClass());

            String key = userId + "-" + industryCode;
            model.setUserKey(key);

            String msgType = asyncSendMsgService.getConfigValue("send_radar_msg_type");

            if ("1".equals(msgType) || StringUtils.isEmpty(msgType)) {
                batchLock.lock();
                try {
                    if (startTime == null) startTime = Instant.now();

                    String msgTime = asyncSendMsgService.getConfigValue("send_radar_msg_time");
                    String msgNum  = asyncSendMsgService.getConfigValue("send_radar_msg_num");
                    // 添加到map中
                    map.computeIfAbsent(key, k -> new RadarBindUserModel(key, new ArrayList<>()))
                            .getRadarObjectList().add(model.getRadarObject());
                    // 增加计数器
                    long currentCount = count.incrementAndGet();
                    log.info("当前发送消息数量:{}", currentCount);

                    if (StringUtils.isNotEmpty(msgNum))  totalNum  = Long.valueOf(msgNum);
                    if (StringUtils.isNotEmpty(msgTime)) totalTime = Long.valueOf(msgTime);

                    if (currentCount >= totalNum ||
                            Instant.now().isAfter(startTime.plusSeconds(totalTime))) {

                        List<Map.Entry<String, RadarBindUserModel>> entries =
                                new ArrayList<>(map.entrySet());
                        for (Map.Entry<String, RadarBindUserModel> entry : entries) {
                            RadarBindUserModel radarBindUserModel = entry.getValue();
                            MsgKafkaConsumerModel<RadarBindUserModel> msgKafkaConsumerModel =
                                    new MsgKafkaConsumerModel<>();
                            msgKafkaConsumerModel.setType(2);
                            msgKafkaConsumerModel.setT(radarBindUserModel);
                            kafkaProducer.sendToKafka(WebSocketConstant.KAFKA_TOPIC_WEBSOCKET_MSG,
                                    JSON.toJSONString(msgKafkaConsumerModel));
                            map.remove(entry.getKey());
                        }
                        // 清空计数器,时间
                        count.set(0);
                        startTime = null;
                    }
                } finally {
                    batchLock.unlock();
                }
            } else {
                MsgKafkaConsumerModel<RadarBindUserModel> msgKafkaConsumerModel =
                        new MsgKafkaConsumerModel<>();
                msgKafkaConsumerModel.setType(2);
                msgKafkaConsumerModel.setT(model);
                kafkaProducer.sendToKafka(WebSocketConstant.KAFKA_TOPIC_WEBSOCKET_MSG,
                        JSON.toJSONString(msgKafkaConsumerModel));
            }
        } catch (Exception e) {
            log.error("sendRadarMsg2Client 异常", e);
            return Result.fail();
        }
        return Result.success("ok");
    }

}
