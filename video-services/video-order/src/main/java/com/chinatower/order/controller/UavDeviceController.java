package com.chinatower.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.order.service.IUavDeviceTrackService;
import com.chinatower.platform.entity.order.UavDeviceTrackParam;
import com.chinatower.order.vo.UavDeviceSyncVO;
import com.chinatower.platform.entity.order.UavDeviceTrackVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-order
 */
@Api(tags = "无人机遥测信息")
@RestController
@Slf4j
public class UavDeviceController {

    @Autowired
    private IUavDeviceTrackService uavDeviceTrackService;
    /**
     * 接受无人机轨迹上报信息接口
     * @param req
     * @return
     */
    @PostMapping("/device/uav/track")
    public Result receiveUavTrack(@RequestBody String req) {
        log.info("/device/uav/track 无人机遥测信息入参: {}", req);
        UavDeviceSyncVO param = JSONObject.parseObject(req, UavDeviceSyncVO.class);
        if (StringUtils.isBlank(param.getDeviceCode()) || null == param.getData()
                || StringUtils.isBlank(param.getData().getLng()) || StringUtils.isBlank(param.getData().getLat()) || null == param.getTimestamp()) {
            log.debug("/device/uav/track 同步失败，缺少必要参数:{}", req);
            return Result.fail("处理失败,缺少必要参数！");
        }
        return uavDeviceTrackService.receiveUavTrack(param);
    }

    /**
     * 查询无人机轨迹信息接口
     * @return
     */
    @PostMapping("/device/uav/qryUavDeviceTrackList")
    public Result<List<UavDeviceTrackVO>> qryUavDeviceTrackList(@RequestBody @Validated UavDeviceTrackParam param) {
        return uavDeviceTrackService.qryUavDeviceTrackList(param);
    }
}
