package com.chinatower.message.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.message.api.model.*;
import com.chinatower.message.constant.WebSocketConstant;
import com.chinatower.message.station.service.AsyncSendMsgService;
import com.chinatower.message.websocket.config.NettyConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.UUID;

/**
 * kafka listener
 */
@Slf4j
@Component
public class MessageKafkaConsumer {

    @Resource
    AsyncSendMsgService sendMsgService;

    private String groupId;

    @Bean("listenerGroupId")
    public String genGroupId() {
        groupId = UUID.randomUUID().toString();
        return groupId;
    }

    @KafkaListener(topics = {WebSocketConstant.KAFKA_TOPIC_WEBSOCKET_MSG}, containerFactory = "kafkaListenerContainerFactory", groupId = "VIDEO-MESSAGE-TOPIC" + "-" + "#{listenerGroupId}")
    public void mathFactoryConsumer(ConsumerRecord<?, ?> consumerRecords) {
        try {
            //判断是否为null
            Optional.ofNullable(consumerRecords.value()).ifPresent(kafkaMessage -> {
                String record = (String) kafkaMessage;
                JSONObject jsonObject = JSON.parseObject(record);
                if (null != jsonObject) {
                    Integer type = (Integer) jsonObject.get("type");
                    String message = JSON.toJSONString(jsonObject.get("t"));
                    if (WebSocketConstant.MSG_KAFKA_TYPE.equals(type)) {
                        //告警消息
                        MsgBody msgBody = JSON.parseObject(message, MsgBody.class);
                        //uid : userId-industry
                        String uid = msgBody.getUserId() + WebSocketConstant.LINE + msgBody.getIndustryCode();
                        log.info("告警消息uid:{}", uid);
                        if (StringUtils.isNotEmpty(uid)) {
                            /*
                             * 判断当前节点是否有该channel
                             * 有 ->  。。。
                             * 无 -> 忽略
                             */
                            if (NettyConfig.getUserChannelListMap().containsKey(uid)) {
                                log.info("{}-用户通道存在该netty中，发送消息:{}", groupId, JSON.toJSONString(msgBody));
                                sendMsgService.senMsg(msgBody);
                            }
                        }
                    } else if (WebSocketConstant.MSG_RADAR_TYPE.equals(type)) {

                        //雷达消息
                        RadarBindUserModel radarBindUserModel = JSON.parseObject(message, RadarBindUserModel.class);
                        log.info("处理雷达消息:{}", JSON.toJSONString(radarBindUserModel));
                        String uid = radarBindUserModel.getUserKey();
                        if (StringUtils.isNotEmpty(uid)) {
                            if (NettyConfig.getUserChannelListMap().containsKey(uid)) {
                                log.info("{}-用户通道存在该netty中，发送消息:{}", groupId, JSON.toJSONString(radarBindUserModel));
                                sendMsgService.sendRadarMsg(radarBindUserModel);
                            }
                        }
                    } else if (WebSocketConstant.MSG_MARK_TYPE.equals(type)) {
                        //预置位到达通知
                        SendPresetArrivalNoticeVO vo = JSON.parseObject(message, SendPresetArrivalNoticeVO.class);
                        vo.setType(type);

                        log.info("webSocket接收数据：{}", JSON.toJSONString(vo));
                        String uid = vo.getUserId() + WebSocketConstant.LINE + vo.getIndustryCode();
                        if (StringUtils.isNotEmpty(uid)) {
                            /*
                             * 判断当前节点是否有该channel
                             * 有 ->  。。。
                             * 无 -> 忽略
                             */
                            if (NettyConfig.getUserChannelListMap().containsKey(uid)) {
                                log.info("{}-用户通道存在该netty中，uid:{},发送消息:{}", groupId, uid, JSON.toJSONString(vo));
                                sendMsgService.sendPresetArrivalNotice(vo);
                            }
                        }
                    }else if (WebSocketConstant.SOUND_CONFIG_TYPE.equals(type)) {
                        //告警声音开关
                        SoundConfig soundConfig = JSON.parseObject(message, SoundConfig.class);
                        soundConfig.setType(type);
                        String uid = soundConfig.getUserId() + WebSocketConstant.LINE + soundConfig.getIndustryCode();
                        log.info("告警声音开关uid:{}", uid);
                        if (StringUtils.isNotEmpty(uid)) {
                            /*
                             * 判断当前节点是否有该channel
                             * 有 ->  。。。
                             * 无 -> 忽略
                             */
                            if (NettyConfig.getUserChannelListMap().containsKey(uid)) {
                                log.info("{}-用户通道存在该netty中，uid:{},发送消息:{}", groupId, uid, JSON.toJSONString(soundConfig));
                                sendMsgService.sendSoundConfigNotice(soundConfig);
                            }
                        }
                    }else if (WebSocketConstant.LOW_ALT_CONFIG_TYPE.equals(type)) {
                        //低空轨迹消息推送
                        LowAltPointMsg lowAltPointMsg = JSON.parseObject(message, LowAltPointMsg.class);
                        String uid = lowAltPointMsg.getUserId() + WebSocketConstant.LINE + lowAltPointMsg.getIndustryCode();
                        log.info("低空轨迹消息推送uid:{}", uid);
                        if (StringUtils.isNotEmpty(uid)) {
                            /*
                             * 判断当前节点是否有该channel
                             * 有 ->  。。。
                             * 无 -> 忽略
                             */
                            if (NettyConfig.getUserChannelListMap().containsKey(uid)) {
                                log.info("lowalt-消费组{}-用户通道存在该netty中，uid:{},发送消息:{}", groupId, uid, JSON.toJSONString(lowAltPointMsg));
                                sendMsgService.sendLowAltPointNotice(lowAltPointMsg);
                            }
                        }
                    }else if(WebSocketConstant.OPERATION_JUDGE_TYPE.equals(type)){
                        // AI运营研判，产生新告警根据规则唤起小塔
                        OperationJudgeMsg operationJudgeMsg = JSON.parseObject(message, OperationJudgeMsg.class);
                        String uid = operationJudgeMsg.getUserId() + WebSocketConstant.LINE + operationJudgeMsg.getIndustryCode();
                        log.info("AI运营研判uid:{}", uid);
                        if (StringUtils.isNotEmpty(uid)) {
                            /*
                             * 判断当前节点是否有该channel
                             * 有 ->  。。。
                             * 无 -> 忽略
                             */
                            if (NettyConfig.getUserChannelListMap().containsKey(uid)) {
                                log.info("智能研判-消费组{}-用户通道存在该netty中，uid:{},发送消息:{}", groupId, uid, JSON.toJSONString(operationJudgeMsg));
                                sendMsgService.sendOperationJudgeRequiredNotice(operationJudgeMsg);
                            }
                        }

                    }
                }
            });
        } catch (Exception e) {
            log.info("websocketMessage异常", e);
        }

    }
}
