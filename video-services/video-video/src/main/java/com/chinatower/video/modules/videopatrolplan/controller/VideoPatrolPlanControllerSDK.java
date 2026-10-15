package com.chinatower.video.modules.videopatrolplan.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videopatrolplan.service.IVideoPatrolPlanInfoService;
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
 *
 * @author zheng.yan
 * @date 2022/1/18
 */
@RestController
@Slf4j
@Api(value = "视频巡航路径模块",tags = "视频巡航路径模块")
public class VideoPatrolPlanControllerSDK {

    @Autowired
    private IVideoPatrolPlanInfoService videoPatrolPlanInfoService;

    /**
     * 增加巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "增加巡航路径", notes = "增加巡航路径")
    @PostMapping("/video/patrol/sdk/addCruisePatrol")
    public Result<String> addCruisePatrol(@Validated @RequestBody AddPatrolReq patrolDTO) {
        log.info("addCruisePatrol" + VideoConstants.SDKREQ + patrolDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        patrolDTO.setIndustryCode(currentUser.getIndustryCode());
        patrolDTO.setAppVerCode(currentUser.getAppVerCode());
        patrolDTO.setUserName(SecurityUtils.getOperator());
        patrolDTO.setTenantId(currentUser.getTenantId());
        videoPatrolPlanInfoService.addCruisePatrol(patrolDTO);
        return Result.success("增加巡航路径成功!");
    }

    /**
     * 删除巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "删除巡航路径", notes = "删除巡航路径")
    @PostMapping("/video/patrol/sdk/delPatrol")
    public Result<String> delPatrol(@Validated @RequestBody DelPatrolReq patrolDTO) {
        log.info("delPatrol" + VideoConstants.SDKREQ + patrolDTO);
        videoPatrolPlanInfoService.delPatrol(patrolDTO);
        return Result.success("删除巡航路径成功!");
    }

    /**
     * 批量删除巡航路径
     * @param: patrolDTO
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/2/25
     * @version:
     */
    @ApiOperation(value = "批量删除巡航路径", notes = "批量删除巡航路径")
    @PostMapping("/video/patrol/sdk/batchDelPatrol")
    public Result<String> batchDelPatrol(@Validated @RequestBody BatchDelPatrolReq patrolDTO) {
        log.info("batchDelPatrol" + VideoConstants.SDKREQ + patrolDTO);
        videoPatrolPlanInfoService.batchDelPatrol(patrolDTO);
        return Result.success("批量删除巡航路径成功!");
    }

    /**
     * 修改巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "修改巡航路径", notes = "修改巡航路径")
    @PostMapping("/video/patrol/sdk/updatePatrol")
    public Result<String> updatePatrol(@Validated @RequestBody ModPatrolReq patrolDTO) {
        log.info("updatePatrol" + VideoConstants.SDKREQ + patrolDTO);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        patrolDTO.setIndustryCode(currentUser.getIndustryCode());
        patrolDTO.setAppVerCode(currentUser.getAppVerCode());
        patrolDTO.setUserName(SecurityUtils.getOperator());
        videoPatrolPlanInfoService.updatePatrol(patrolDTO);
        return Result.success("修改巡航路径成功!");
    }

    /**
     * 停用/启用巡航路径
     *
     * @param patrolDTO
     * @return
     */
    @ApiOperation(value = "停用/启用巡航路径", notes = "停用/启用巡航路径")
    @PostMapping("/video/patrol/sdk/doPatrol")
    public Result<String> doPatrol(@Validated @RequestBody SttPatrolReq patrolDTO) {
        log.info("doPatrol" + VideoConstants.SDKREQ + patrolDTO);
        Result<String> result = videoPatrolPlanInfoService.doPatrol(patrolDTO);
        return result;
    }


    /**
     * 查询订单巡航路径
     *
     * @return
     * @author chenmf
     * @date 2022/2/16
     * @version
     */
    @ApiOperation(value = "查询订单巡航路径", notes = "查询订单巡航路径")
    @PostMapping("/video/patrol/sdk/queryOrderPatrolInfo")
    public Result<List<PatrolDTO>> queryOrderPatrolInfo(@RequestBody QueryOrderDeviceSetDTO presetDTO) {
        log.info("queryOrderPatrolInfo" + VideoConstants.SDKREQ + presetDTO);
        if (StringUtils.isEmpty(presetDTO.getIndustryCode())) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser currentUser = loginUser.getUser();
            presetDTO.setIndustryCode(currentUser.getIndustryCode());
        }
        List<PatrolDTO> list = videoPatrolPlanInfoService.queryOrderPatrolInfo(presetDTO);
        return Result.data(list);
    }


    /**
     * 功能描述: 查询巡航路径列表
     *
     * @param queryPatrolReq
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/28
     * @version: 1.0
     */
    @ApiOperation(value = "查询巡航路径列表", notes = "查询巡航路径列表")
    @PostMapping("/video/patrol/sdk/queryPatrol")
    public Result<JSONObject> queryPatrol(@RequestBody VideoQueryPatrolData queryPatrolReq) {
        log.info("queryPatrol" + VideoConstants.SDKREQ + queryPatrolReq);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser currentUser = loginUser.getUser();
        queryPatrolReq.setIndustryCode(currentUser.getIndustryCode());
        queryPatrolReq.setTenantId(currentUser.getTenantId());
        JSONObject json = videoPatrolPlanInfoService.queryPatrol(queryPatrolReq);
        return Result.data(json);
    }

}
