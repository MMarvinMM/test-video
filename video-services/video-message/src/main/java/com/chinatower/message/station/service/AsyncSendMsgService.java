package com.chinatower.message.station.service;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.message.api.model.*;
import com.chinatower.message.constant.WebSocketConstant;
import com.chinatower.message.websocket.config.NettyConfig;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AsyncSendMsgService {

    @Autowired
    SystemFeignClient systemFeignClient;

    @Async("threadPoolTaskExecutor")
    public void senMsg(MsgBody msgBody) {
        List<Channel> channels = NettyConfig.getUserChannelListMap().get(msgBody.getUserId() + WebSocketConstant.LINE + msgBody.getIndustryCode());
        //构建消息实体
        MsgStationReq req = MsgStationReq.builder()
                .uniId(IdWorker.getIdStr())
                .msgId(msgBody.getMsgId())
                .userId(msgBody.getUserId())
                .notifyType(msgBody.getNotifyType())
                .notifyTitle(msgBody.getNotifyTitle())
                .orderStatus(msgBody.getOrderStatus())
                .address(msgBody.getAddress())
                .appVerCode(msgBody.getAppVerCode())
                .alarmFlag(msgBody.getAlarmFlag())
                .showType(msgBody.getShowType())
                .industryFlag(msgBody.getIndustryFlag())
                .industryExtend(msgBody.getIndustryExtend())
                .industryCode(msgBody.getIndustryCode())
                .creator(msgBody.getCreator())
                .soundCloseType(msgBody.getSoundCloseType())
                .soundDuration(msgBody.getSoundDuration())
                .soundUrl(msgBody.getSoundUrl())
                .orderStatusName(msgBody.getOrderStatusName())
                .alarmSource(msgBody.getAlarmSource())
                .deviceCode(msgBody.getDeviceCode())
                .deviceName(msgBody.getDeviceName())
                .build();
        if (!channels.isEmpty()) {
            channels.forEach(channel -> {
                if (null != channel) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(req)));
                }
            });
            log.info("消息发送成功，{}", JSON.toJSONString(req));
        }
    }

    @Async("threadPoolTaskExecutor")
    public void sendRadarMsg(RadarBindUserModel radarBindUserModel) {
        log.info("发送数据:{}", JSON.toJSONString(radarBindUserModel));
        List<Channel> channels = NettyConfig.getUserChannelListMap().get(radarBindUserModel.getUserKey());
        if (!channels.isEmpty()) {
            String msgType = getConfigValue("send_radar_msg_type");
            if ("1".equals(msgType) || StringUtils.isEmpty(msgType)) {
                List<RadarObject> radarObject = radarBindUserModel.getRadarObjectList();
                channels.forEach(channel -> {
                    if (null != channel) {
                        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(radarObject)));
                    }
                });
            } else {
                RadarObject radarObject = radarBindUserModel.getRadarObject();
                channels.forEach(channel -> {
                    if (null != channel) {
                        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(radarObject)));
                    }
                });
            }

        }
    }

    @Async("threadPoolTaskExecutor")
    public void sendPresetArrivalNotice(SendPresetArrivalNoticeVO vo) {
        List<Channel> channels = NettyConfig.getUserChannelListMap().get(vo.getUserId() + WebSocketConstant.LINE + vo.getIndustryCode());
        if (!channels.isEmpty()) {
            channels.forEach(channel -> {
                if (null != channel) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(vo)));
                }
            });
            log.info("sendPresetArrivalNotice-消息发送成功，{}", JSON.toJSONString(vo));
        }
    }

    public String getConfigValue(String configKey) {
        AjaxResult result = systemFeignClient.getConfValue(configKey);
        if (Constants.SUCCESS.toString().equals(String.valueOf(result.get(AjaxResult.CODE_TAG)))) {
            Object data = result.get(AjaxResult.DATA_TAG);
            if (data != null) {
                return JSONObject.parseObject(JSONObject.toJSONString(data), SysConfig.class).getConfigValue();
            }
        }
        return "";
    }

    @Async("threadPoolTaskExecutor")
    public void sendSoundConfigNotice(SoundConfig soundConfig) {
        List<Channel> channels = NettyConfig.getUserChannelListMap().get(soundConfig.getUserId() + WebSocketConstant.LINE + soundConfig.getIndustryCode());
        if (!channels.isEmpty()) {
            channels.forEach(channel -> {
                if (null != channel) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(soundConfig)));
                }
            });
            log.info("sendSoundConfigNotice-消息发送成功，{}", JSON.toJSONString(soundConfig));
        }
    }

    /**
     * AI智能研判通知小塔是否打开弹窗
     *
     * @param operationJudgeMsg 需要通知的用户信息
     */
    @Async("threadPoolTaskExecutor")
    public void sendOperationJudgeRequiredNotice(OperationJudgeMsg operationJudgeMsg) {
        log.info("发送小塔是否打开弹窗通知:{}", JSON.toJSONString(operationJudgeMsg));
        // 根据用户ID和行业编码获取WebSocket连接
        String userKey = operationJudgeMsg.getUserId() + WebSocketConstant.LINE + operationJudgeMsg.getIndustryCode();
        List<Channel> channels = NettyConfig.getUserChannelListMap().get(userKey);
        if (channels != null && !channels.isEmpty()) {
            channels.forEach(channel -> {
                if (null != channel) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(operationJudgeMsg)));
                }
            });
            log.info("小塔是否打开弹窗通知发送成功，用户ID：{}", operationJudgeMsg.getUserId());
        } else {
            log.warn("未找到用户 {} 的WebSocket连接", userKey);
        }
    }

    /**
     * 低空态势服务增量轨迹点位消息通知
     */
    @Async("threadPoolTaskExecutor")
    public void sendLowAltPointNotice(LowAltPointMsg lowAltPointMsg) {
        log.info("发送增量轨迹点位通知:{}", JSON.toJSONString(lowAltPointMsg));
        // 根据用户ID和行业编码获取WebSocket连接
        String userKey = lowAltPointMsg.getUserId() + WebSocketConstant.LINE + lowAltPointMsg.getIndustryCode();
        List<Channel> channels = NettyConfig.getUserChannelListMap().get(userKey);
        if (channels != null && !channels.isEmpty()) {
            channels.forEach(channel -> {
                if (null != channel) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(lowAltPointMsg)));
                }
            });
            log.info("轨迹点位通知发送成功，用户ID：{}", lowAltPointMsg.getUserId());
        } else {
            log.warn("未找到用户 {} 的WebSocket连接", userKey);
        }
    }
}
