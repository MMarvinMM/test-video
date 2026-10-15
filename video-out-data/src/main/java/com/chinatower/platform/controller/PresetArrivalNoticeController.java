package com.chinatower.platform.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.message.api.model.MsgKafkaConsumerModel;
import com.chinatower.outdata.api.entity.HeartBeatVO;
import com.chinatower.outdata.api.entity.PresetArrivalNotice;
import com.chinatower.platform.config.MessageKafkaProducer;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.consumer.entity.SendPresetNoticeVO;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/presetArrivalNotice")
@Slf4j
public class PresetArrivalNoticeController {

    @Resource
    MessageKafkaProducer kafkaProducer;

    /**
     * 视频画面显示电子围栏，发送心跳请求
     * 客户端每隔5s发送一次心跳，并将开启显示电子围栏的摄像机编码集合传给后台，
     * 后台根据redis的数据对摄像机做添加或续期（30s）
     * @param req 请求体，类型为EyeHeartBeatVO
     * @return Result<String> 返回结果，包含一个String类型的消息
     */
    @PostMapping(value = "/sendHeartBeat")
    public Result<String> sendHeartBeat(@RequestBody HeartBeatVO req){
        log.info("sendHeartBeat入参：{}", JSON.toJSONString(req));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String hashField = this.getHashField(user);
        List<String> deviceCodeList = req.getDeviceCodeList();
        if(CollectionUtil.isNotEmpty(deviceCodeList)){
            long invalidTime = this.getInvalidTime();

            String value = RedisUtil.hGet(Constants.HEART_BEAT_KEY, hashField, RedisDBType.PRIMARY);
            if(StrUtil.isNotBlank(value)){
                List<String> valueList = JSON.parseArray(value, String.class);
                for (int i = valueList.size() - 1; i >= 0; i--) {
                    String s = valueList.get(i);
                    String[] split = s.split(Constants.UNDER_LINE);
                    String deviceCode = split[1];
                    for (int j = deviceCodeList.size() - 1; j >= 0; j--) {
                        String string = deviceCodeList.get(j);
                        if (deviceCode.equals(string)){
                            valueList.remove(s);
                            deviceCodeList.remove(string);
                            valueList.add(invalidTime + Constants.UNDER_LINE + string);
                        }
                    }
                }
                deviceCodeList.replaceAll(s -> invalidTime + Constants.UNDER_LINE + s);
                valueList.addAll(deviceCodeList);
                value = JSON.toJSONString(valueList);
            }else {
                deviceCodeList.replaceAll(s -> invalidTime + Constants.UNDER_LINE + s);
                value = JSON.toJSONString(deviceCodeList);
            }
            RedisUtil.hset(Constants.HEART_BEAT_KEY, hashField, value, RedisDBType.PRIMARY);
            RedisUtil.expire(Constants.HEART_BEAT_KEY, 30, TimeUnit.MINUTES, RedisDBType.PRIMARY);
        }
        return Result.success("发送成功");
    }

    private String getHashField(SysUser user){
        return user.getTenantId() + Constants.FEN_HAO + user.getUserId() + Constants.FEN_HAO + user.getIndustryCode();
    }

    /**
     * 获取30s后的失效时间
     * @return
     */
    private long getInvalidTime(){
        // 获取当前时间
        Instant now = Instant.now();
        // 加上 30 秒
        Instant after30Seconds = now.plusSeconds(30);
        return after30Seconds.toEpochMilli();
    }

    @PostMapping(value = "/sendPresetNotice")
    public Result<String> sendPresetNotice(@RequestBody SendPresetNoticeVO req){
        PresetArrivalNotice infoVO = req.getInfoVO();
        long count = req.getCount();
        for (long l = 0; l < count; l++) {
            try {
                kafkaProducer.sendToKafka("VMS-PRESET", JSON.toJSONString(infoVO));
            } catch (Exception e) {
                log.error("推送kafka报错",e);
            }
        }
        return Result.success("发送成功");
    }
}
