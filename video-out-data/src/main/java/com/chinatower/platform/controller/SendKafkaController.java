package com.chinatower.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.dto.bdm.LayerAlarmCreateDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.provider.VideoProducerService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-out-data
 *
 * @ClassName SendKafkaController
 * @Description 消息推送
 */
@RestController
@Slf4j
@Api(value = "消息推送")
@RequiredArgsConstructor
public class SendKafkaController {
    //卫片告警
    private static final String topic = "receiveRegionAlarm";
    private static final String satellite_topic = "satelliteAlarm";
    private final VideoProducerService videoProducerService;


    /**
     * 推送卫片告警
     *
     * @param dtos
     * @return
     */
    @PostMapping("/send/kafka/layerAlarm")
    public Result layerAlarm(@RequestBody List<LayerAlarmCreateDTO> dtos) {
        videoProducerService.sendChannelMessage(topic, JSON.toJSONString(dtos));
        return Result.success("发送成功");
    }


    @PostMapping("/send/kafka/satelliteAlarm")
    public Result satelliteAlarm(@RequestBody JSONArray receiveSatelliteAlarmVos) {
        videoProducerService.sendChannelMessage(satellite_topic, JSON.toJSONString(receiveSatelliteAlarmVos));
        return Result.success("发送成功");
    }
}
