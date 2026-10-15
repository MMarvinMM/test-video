package com.chinatower.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.order.RecorderDeviceTrackDTO;
import com.chinatower.order.service.IRecorderDeviceTrackService;
import com.chinatower.platform.entity.order.UavDeviceTrackParam;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * video-order
 */
@Api(tags = "执法记录仪遥测信息")
@RestController
@Slf4j
public class RecorderDeviceController {

    @Autowired
    private IRecorderDeviceTrackService recorderDeviceTrackService;

    /**
     * 接收执法记录仪轨迹上报信息接口
     *
     * @param req 请求体，包含遥测信息
     * @return 处理结果
     */
    @PostMapping("/device/recorder/track")
    public Result receiveRecorderTrakTrack(@RequestBody String req) {
        log.info("/device/recorder/track 执法记录仪遥测信息入参: {}", req);
        RecorderDeviceTrackDTO param = JSONObject.parseObject(req, RecorderDeviceTrackDTO.class);
        if (StringUtils.isBlank(param.getDeviceCode()) || Objects.isNull(param.getLatitude())
            || Objects.isNull(param.getLongitude())) {
            log.debug("/device/recorder/track 同步失败，缺少必要参数:{}", req);
            return Result.fail("处理失败,缺少必要参数！");
        }
        return recorderDeviceTrackService.receiveRecorderTrack(param);
    }


    /**
     * 查询执法记录仪轨迹信息接口
     * @param param
     * @return
     */
    @PostMapping("/device/recorder/getRecorderDeviceTrackList")
    public Result<List<RecorderDeviceTrackDTO>> getRecorderDeviceTrackList(@RequestBody UavDeviceTrackParam param) {
        return recorderDeviceTrackService.getRecorderDeviceTrackList(param);
    }
}
