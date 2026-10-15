package com.chinatower.stdu.query.controller;


import com.chinatower.common.entity.Result;

import com.chinatower.stdu.query.service.VideoQueryService;
import com.chinatower.video.api.entity.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.Set;

/**
 * 视频查询controller
 * Created by ldl on 2022/1/27.
 */
@Api(value = "视频查询接口-Api",tags = "视频查询请求处理")
@Slf4j
@RestController
public class VideoQueryControllerSDK {

    @Autowired
    VideoQueryService videoQueryService;

    /**
     * 全域感知大屏，圈选摄像机，批量拉取实时流
     * @param reqList
     * @return
     */
    @ApiOperation(value = "全域感知大屏，圈选摄像机，批量拉取实时流")
    @PostMapping("/devices/channels/getVideoRealtimeUrlsForComponent")
    public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsForComponent(@RequestBody Set<PlayerGetVideoRealtimeUrlData> reqList){
        return videoQueryService.getVideoRealtimeUrlsForComponent(reqList);
    }


    @ApiOperation(value = "批量拉取实时流")
    @PostMapping("/devices/channels/getVideoRealtimeUrlsCustomSet")
    public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsCustomSet(@RequestBody Set<PlayerGetVideoRealtimeUrlData> reqList){
        return videoQueryService.getVideoRealtimeUrlsCustomSet(reqList);
    }

}
