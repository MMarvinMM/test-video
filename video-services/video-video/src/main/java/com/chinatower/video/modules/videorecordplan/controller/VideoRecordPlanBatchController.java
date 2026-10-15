package com.chinatower.video.modules.videorecordplan.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.constants.DeviceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName VideoRecordPlanController
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/7 11:45
 * @Version 1.0
 **/

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.ChannelVideoPlanRequestVO;
import com.chinatower.video.api.entity.SttPatrolReq;
import com.chinatower.video.api.entity.VideoConstants;
import com.chinatower.video.modules.videorecordplan.service.IVideoRecordPlanInfoBatchService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName AlgorithmConfigController
 * @Description 视频通道录像计划
 * @Author chenmf
 * @Date 2022/1/19
 */
@RestController
@Slf4j
@Api(value = "视频通道录像计划模块",tags = "视频通道录像计划模块")
public class VideoRecordPlanBatchController {

    @Autowired
    private IVideoRecordPlanInfoBatchService videoRecordPlanInfoBatchService;


    /**
     * 批量停用/启用通道录像计划 (订单调用)
     *
     * @param channelVideoPlanRequestVO
     * @return
     */
    @ApiOperation(value = "批量停用/启用通道录像计划")
    @PostMapping("/video/batchStartEndRecordPlan")
    public Result<String> batchStartEndRecordPlan(@Validated @RequestBody ChannelVideoPlanRequestVO channelVideoPlanRequestVO) {
        log.info(VideoConstants.BATCH_START_END_PLAN + VideoConstants.CONTROLLERREQ + channelVideoPlanRequestVO);
        channelVideoPlanRequestVO.setUsername(VideoConstants.ADMIN);
        videoRecordPlanInfoBatchService.batchStartEndRecordPlan(channelVideoPlanRequestVO);
        return Result.success(VideoConstants.BATCH_START_END_SUCCESS);
    }

    /**
     * @description: 批量停用/启用巡航路径 （order调用）
     * @param:
     * @return:
     */
    @ApiOperation(value = "批量停用/启用巡航计划", notes = "批量停用/启用巡航计划")
    @PostMapping("/video/patrol/batchStartEndPatrol")
    public Result<String> batchStartEndPatrol(@RequestBody List<SttPatrolReq> patrolDTOList) {
        log.info("batchStartEndPatrol" + VideoConstants.CONTROLLERREQ + JSONObject.toJSONString(patrolDTOList));
        videoRecordPlanInfoBatchService.batchStartEndPatrol(patrolDTOList);
        return Result.success("批量停用/启用巡航计划成功!");
    }

}
