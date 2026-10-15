package com.chinatower.video.modules.videorecordplan.controller;

/**
 * @ClassName VideoRecordPlanController
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/7 11:45
 * @Version 1.0
 **/

import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videorecordplan.service.IVideoRecordPlanInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName AlgorithmConfigController
 * @Description 视频通道录像计划
 * @Author chenmf
 * @Date 2022/1/19
 */
@RestController
@Slf4j
@Api(value = "视频通道录像计划模块",tags = "视频通道录像计划模块")
public class VideoRecordPlanControllerSDK {

    @Autowired
    private IVideoRecordPlanInfoService videoRecordPlanInfoService;


    /**
     * 新增通道录像计划
     *
     * @param channelVideoPlanRequestVO
     * @return
     */
    @ApiOperation(value = "新增通道录像计划")
    @PostMapping("/video/sdk/addRecordPlan")
    public Result<String> addRecordPlan(@Validated @RequestBody ChannelVideoPlanRequestVO channelVideoPlanRequestVO) {
        log.info("addRecordPlan" + VideoConstants.SDKREQ + channelVideoPlanRequestVO);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        channelVideoPlanRequestVO.setIndustryCode(user.getIndustryCode());
        channelVideoPlanRequestVO.setAppVerCode(user.getAppVerCode());
        channelVideoPlanRequestVO.setUsername(SecurityUtils.getOperator());
        List<ChannelVideoPlanVO> recordPlanList = channelVideoPlanRequestVO.getRecordPlanList();
        for (ChannelVideoPlanVO channelVideoPlanVO : recordPlanList) {
            channelVideoPlanVO.setTenantId(user.getTenantId());
        }
        videoRecordPlanInfoService.addRecordPlan(channelVideoPlanRequestVO);
        return Result.success("新增通道录像计划成功!");
    }

    /**
     * 修改通道录像计划
     *
     * @param channelVideoPlanVO
     * @return
     */
    @ApiOperation(value = "修改通道录像计划")
    @PostMapping("/video/sdk/updateRecordPlan")
    public Result<String> updateRecordPlan(@Validated @RequestBody ChannelVideoPlanVO channelVideoPlanVO) {
        log.info("updateRecordPlan" + VideoConstants.SDKREQ + channelVideoPlanVO);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        channelVideoPlanVO.setUsername(SecurityUtils.getOperator());
        channelVideoPlanVO.setIndustryCode(user.getIndustryCode());
        channelVideoPlanVO.setAppVerCode(user.getAppVerCode());
        videoRecordPlanInfoService.updateRecordPlan(channelVideoPlanVO);
        return Result.success("新修改通道录像计划成功!");
    }

    /**
     * 删除通道录像计划
     * @param deleteChannelVideoPlanVO
     * @return
     */
    @ApiOperation(value = "删除通道录像计划")
    @PostMapping("/video/sdk/deleteRecordPlan")
    public Result<String> deleteRecordPlan(@Validated @RequestBody DeleteChannelVideoPlanVO deleteChannelVideoPlanVO) {
        log.info("deleteRecordPlan" + VideoConstants.SDKREQ + deleteChannelVideoPlanVO);
        SysUser user = SecurityUtils.getLoginUser().getUser();
        deleteChannelVideoPlanVO.setIndustryCode(user.getIndustryCode());
        videoRecordPlanInfoService.deleteRecordPlan(deleteChannelVideoPlanVO);
        return Result.success("删除改通道录像计划成功!");
    }

    /**
     * @description: 批量删除通道录像计划
     * @param: channelVideoPlanVO
     * @return:
     */
    @ApiOperation(value = "批量删除通道录像计划")
    @PostMapping("/video/sdk/batchDeleteRecordPlan")
    public Result<String> batchDeleteRecordPlan(@Validated @RequestBody BatchDeleteChannelVideoPlanVO batchDeleteChannelVideoPlanVO) {
        log.info("batchDeleteRecordPlan" + VideoConstants.SDKREQ + batchDeleteChannelVideoPlanVO);
        return videoRecordPlanInfoService.batchDeleteRecordPlan(batchDeleteChannelVideoPlanVO);
    }

    /**
     * @description: 批量停用/启用通道录像计划
     * @param:
     * @return:
     */
    @ApiOperation(value = "批量停用/启用通道录像计划")
    @PostMapping("/video/sdk/batchStartOrEndRecordPlan")
    public Result<String> batchStartOrEndRecordPlan(@Validated @RequestBody BatchStartOrEndRecordPlanVO batchStartOrEndRecordPlanVO) {
        log.info("sdk_batchStartOrEndRecordPlan" + VideoConstants.SDKREQ + batchStartOrEndRecordPlanVO);
        return videoRecordPlanInfoService.batchStartOrEndRecordPlan(batchStartOrEndRecordPlanVO);
    }

    /**
     * 查询订单通道录像计划
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    @PostMapping("/video/sdk/queryOrderRecordPlanInfo")
    public Result<List<ChannelVideoPlanVO>> queryOrderRecordPlanInfo(@RequestBody QueryOrderDeviceSetDTO queryOrderDeviceSetDTO) {
        log.info("queryOrderRecordPlanInfo" + VideoConstants.SDKREQ + queryOrderDeviceSetDTO);
        List<ChannelVideoPlanVO> list = videoRecordPlanInfoService.queryOrderRecordPlanInfo(queryOrderDeviceSetDTO);
        return Result.data(list);
    }

}
