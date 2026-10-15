package com.chinatower.video.modules.query.controller;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.query.entity.HabitVO;
import com.chinatower.video.modules.query.service.VideoQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    VideoCommonService videoCommonService;

    @ApiOperation(value = "查询通道录像计划",notes = "查询通道录像计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询通道录像计划请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    @PostMapping("/video/query/sdk/queryRecordPlan")
    public Result<List<QueryRecordPlanData>> queryRecordPlan(@RequestBody VideoBaseReq videoBaseReq) {
        Result<List<QueryRecordPlanData>> res;
        log.info(VideoConstants.QUERY_RECORD_PLAN_OP + VideoConstants.SDKREQ + videoBaseReq);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_RECORD_PLAN_OP + SecurityUtils.getOperator());
            Map<String,String> deviceOrderInfo = videoCommonService.checkDeviceAuth(videoBaseReq);
            if(deviceOrderInfo == null){
                // 返回错误 无权限使用
                log.info(VideoConstants.QUERY_RECORD_PLAN_OP + VideoConstants.RESPONSE_LOG + VideoConstants.NO_AUTH);
                return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.NO_AUTH);
            }else{
                videoBaseReq.setOrderCode(deviceOrderInfo.get(VideoConstants.ORDERCODE));
            }
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            res = videoQueryService.queryRecordPlan(videoBaseReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_RECORD_PLAN_ERRO));
            log.error(VideoConstants.QUERY_RECORD_PLAN_ERRO , e);
        }
        return res;
    }

    @ApiOperation(value = "查询全部录像计划",notes = "查询全部录像计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryRecordPlanForPageReq",value = "查询全部录像计划请求实体",dataType = "QueryRecordPlanForPageReq",required = true,paramType = "query")
    })
    @PostMapping("/video/query/sdk/queryRecordPlanForPage")
    public Result queryRecordPlanForPage(VideoChannelVideoPlanVO channelVideoPlanVO) {
        Result<List<QueryRecordPlanData>> res;
        log.info(VideoConstants.QUERY_RECORD_PLAN_FOR_PAGE_OP + VideoConstants.SDKREQ + channelVideoPlanVO);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_RECORD_PLAN_FOR_PAGE_OP + SecurityUtils.getOperator());
            Map<String,String> deviceOrderInfo = videoCommonService.checkDeviceAuth(channelVideoPlanVO);
            if(deviceOrderInfo == null){
                // 返回错误 无权限使用
                log.info(VideoConstants.QUERY_RECORD_PLAN_FOR_PAGE_OP + VideoConstants.RESPONSE_LOG + VideoConstants.NO_AUTH);
                return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.NO_AUTH);
            }else{
                channelVideoPlanVO.setOrderCode(deviceOrderInfo.get(VideoConstants.ORDERCODE));
            }
            channelVideoPlanVO.setTenantId(currentUser.getUser().getTenantId());
            res = videoQueryService.queryRecordPlanForPage(channelVideoPlanVO);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_RECORD_PLAN_ERRO));
            log.error(VideoConstants.QUERY_RECORD_PLAN_ERRO , e);
        }
        return res;
    }

    @ApiOperation(value = "查询PTZ位置",notes = "查询PTZ位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询PTZ位置请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    @PostMapping("/video/query/sdk/queryPtzPosition")
    public Result<PresetData> queryPtzPosition(@RequestBody VideoBaseReq videoBaseReq) {
        Result<PresetData> res;
        log.info(VideoConstants.QUERY_PTZ_POSITION_OP + VideoConstants.SDKREQ + videoBaseReq);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_PTZ_POSITION_OP + SecurityUtils.getOperator());
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            res = videoQueryService.queryPtzPosition(videoBaseReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_PTZ_POSITION_ERRO));
            log.error(VideoConstants.QUERY_PTZ_POSITION_ERRO , e);
        }
        return res;
    }
    @ApiOperation(value = "查询预置点列表",notes = "查询预置点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询预置点列表请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    @PostMapping("/video/query/sdk/queryPresetList")
    public Result<List<QueryPresetListData>> queryPresetList(@RequestBody VideoBaseReq videoBaseReq) {
        Result<List<QueryPresetListData>> res;
        log.info(VideoConstants.QUERY_PRESET_LIST_OP + VideoConstants.SDKREQ + videoBaseReq);
        try{
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String industryCode;
            String tenantId;
            if(ObjectUtil.isEmpty(loginUser)){
                industryCode = videoBaseReq.getIndustryCode();
                tenantId = videoBaseReq.getTenantId();
            }else{
                SysUser user = loginUser.getUser();
                industryCode = user.getIndustryCode();
                tenantId = user.getTenantId();
                log.info(VideoConstants.QUERY_PRESET_LIST_OP + SecurityUtils.getOperator());
            }

            Map<String,String> deviceOrderInfo = videoCommonService.checkDeviceAuth(videoBaseReq);
            if(deviceOrderInfo == null){
                // 返回错误 无权限使用
                log.info(VideoConstants.QUERY_PRESET_LIST_OP + VideoConstants.RESPONSE_LOG + VideoConstants.NO_AUTH);
                return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.NO_AUTH);
            }else{
                videoBaseReq.setOrderCode(deviceOrderInfo.get(VideoConstants.ORDERCODE));
            }
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,tenantId);
            videoBaseReq.setIndustryCode(industryCode);
            res = videoQueryService.queryPresetList(videoBaseReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_PRESET_LIST_ERRO));
            log.error(VideoConstants.QUERY_PRESET_LIST_ERRO , e);
        }
        return res;
    }
    @ApiOperation(value = "查询看守位",notes = "查询看守位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询看守位请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    @PostMapping("/video/query/sdk/queryGuardList")
    public Result<QueryGuardListData> queryGuardList(@RequestBody VideoBaseReq videoBaseReq) {
        Result<QueryGuardListData> res;
        log.info(VideoConstants.QUERY_GUARD_LIST_OP + VideoConstants.SDKREQ + videoBaseReq);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_GUARD_LIST_OP + SecurityUtils.getOperator());
            Map<String,String> deviceOrderInfo = videoCommonService.checkDeviceAuth(videoBaseReq);
            if(deviceOrderInfo == null){
                // 返回错误 无权限使用
                log.info(VideoConstants.QUERY_GUARD_LIST_OP + VideoConstants.RESPONSE_LOG + VideoConstants.NO_AUTH);
                return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.NO_AUTH);
            }else{
                videoBaseReq.setOrderCode(deviceOrderInfo.get(VideoConstants.ORDERCODE));
            }
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            videoBaseReq.setUserCode(currentUser.getUser().getUserId());
            res = videoQueryService.queryGuardList(videoBaseReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_GUARD_LIST_ERRO));
            log.error(VideoConstants.QUERY_GUARD_LIST_ERRO , e);
        }
        return res;
    }
    @ApiOperation(value = "查询巡航路径",notes = "查询巡航路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoQueryPatrol",value = "查询巡航路径请求实体",dataType = "VideoQueryPatrol",required = true,paramType = "query")
    })
    @PostMapping("/video/query/sdk/queryPatrol")
    public Result<List<QueryPatrolData>> queryPatrol(@RequestBody VideoQueryPatrolData videoQueryPatrol) {
        Result<List<QueryPatrolData>> res;
        log.info(VideoConstants.QUERY_PATROL_OP + VideoConstants.SDKREQ + videoQueryPatrol);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_PATROL_OP + SecurityUtils.getOperator());
            Map<String,String> deviceOrderInfo = videoCommonService.checkDeviceAuth(videoQueryPatrol);
            if(deviceOrderInfo == null){
                // 返回错误 无权限使用
                log.info(VideoConstants.QUERY_PATROL_OP + VideoConstants.RESPONSE_LOG + VideoConstants.NO_AUTH);
                return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.NO_AUTH);
            }else{
                videoQueryPatrol.setOrderCode(deviceOrderInfo.get(VideoConstants.ORDERCODE));
            }
            videoQueryPatrol.setIndustryCode(currentUser.getUser().getIndustryCode());
            videoQueryPatrol.setTenantId(currentUser.getUser().getTenantId());
            res = videoQueryService.queryPatrol(videoQueryPatrol);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_PATROL_ERRO));
            log.error(VideoConstants.QUERY_PATROL_ERRO , e);
        }
        return res;
    }

    @ApiOperation(value = "获取租户权限",notes = "获取租户权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryRoleReq",value = "获取租户权限请求实体",dataType = "QueryRoleReq",required = true,paramType = "query")
    })
    @PostMapping("/video/query/sdk/selectRoleById")
    public Result selectRoleById(@RequestBody QueryRoleReq queryRoleReq) {
        Result res;
        log.info(VideoConstants.SELECT_ROLE_BY_ID_OP + VideoConstants.SDKREQ + queryRoleReq);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.SELECT_ROLE_BY_ID_OP + SecurityUtils.getOperator());
            res = videoQueryService.selectRoleById(queryRoleReq,currentUser);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.SELECT_ROLE_BY_ID_ERRO));
            log.error(VideoConstants.SELECT_ROLE_BY_ID_ERRO , e);
        }
        return res;
    }

    @ApiOperation(value = "设置用户操作习惯",notes = "设置用户操作习惯")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "setUserHabit",value = "设置用户操作习惯请求实体",dataType = "HabitVO",required = true,paramType = "query")
    })
    @PostMapping("/video/query/sdk/setUserHabit")
    public Result setUserHabit(HabitVO habitVO) {
        Result res = Result.success("设置用户操作习惯成功");
        LoginUser currentUser = SecurityUtils.getLoginUser();
        habitVO.setHabitKey(currentUser.getUser().getUserId() + "_" + habitVO.getHabitKey());
        try{
            videoQueryService.setUserHabit(habitVO);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(),"设置用户操作习惯失败"));
            log.error("设置用户操作习惯失败" , e);
        }
        return res;
    }

    @ApiOperation(value = "获取用户操作习惯",notes = "获取用户操作习惯")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "getUserHabit",value = "获取用户操作习惯请求实体",dataType = "HabitVO",required = true,paramType = "query")
    })
    @PostMapping("/video/query/sdk/getUserHabit")
    public Result getUserHabit(HabitVO habitVO) {
        Result res;
        LoginUser currentUser = SecurityUtils.getLoginUser();
        habitVO.setHabitKey(currentUser.getUser().getUserId() + "_" + habitVO.getHabitKey());
        try{
            String habitValue = videoQueryService.getUserHabit(habitVO);
            res = Result.data(habitValue);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(),"获取用户操作习惯失败"));
            log.error("获取用户操作习惯失败" , e);
        }
        return res;
    }

    @ApiOperation(value = "获取视频格式字典表",notes = "获取视频格式字典表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryGetEnumInfoReq",value = "获取视频格式字典表请求实体",dataType = "QueryGetEnumInfoReq",required = true,paramType = "query")
    })
    @PostMapping("/video/query/sdk/getEnumInfo")
    public Result getEnumInfo(@RequestBody QueryGetEnumInfoReq queryGetEnumInfoReq) {
        Result res;
        try{
            res = videoQueryService.getEnumInfo(queryGetEnumInfoReq);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.SELECT_ENUM_INFO_ERRO));
            log.error(VideoConstants.SELECT_ENUM_INFO_ERRO , e);
        }
        return res;
    }

    /**
     * 全域感知大屏，圈选摄像机，批量拉取实时流
     * @param reqList
     * @return
     */
    @Deprecated
    @ApiOperation(value = "全域感知大屏，圈选摄像机，批量拉取实时流")
    @PostMapping("/devices/channels/getVideoRealtimeUrlsForComponent")
    public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsForComponent(@RequestBody Set<PlayerGetVideoRealtimeUrlData> reqList){
        log.info("废弃接口调用");
        return videoQueryService.getVideoRealtimeUrlsForComponent(reqList);
    }

}
