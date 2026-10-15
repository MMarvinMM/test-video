package com.chinatower.video.modules.query.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.dto.PercentPointDto;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.VideoControlAPI;
import com.chinatower.video.api.VideoQueryCustomAPI;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.query.entity.HabitVO;
import com.chinatower.video.modules.query.service.VideoQueryService;
import com.chinatower.video.modules.query.vo.AlgoPresentVo;
import com.chinatower.video.modules.query.vo.QueryPresentPointVo;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 视频查询controller
 * Created by ldl on 2022/1/27.
 */
@Api(value = "视频查询接口-Api",tags = "视频查询请求处理")
@Slf4j
@RestController
public class VideoQueryController implements VideoQueryCustomAPI {
    @Autowired
    VideoQueryService videoQueryService;

    @Autowired
    VideoCommonService videoCommonService;

    @Autowired
    VideoControlAPI videoControlAPI;

    private final String GET_USER_HABIT_ERROR = "获取用户操作习惯失败";

    @ApiOperation(value = "查询通道录像计划",notes = "查询通道录像计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询通道录像计划请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    @Override
    public Result<List<QueryRecordPlanData>> queryRecordPlan(@RequestBody VideoBaseReq videoBaseReq) {
        Result<List<QueryRecordPlanData>> res;
        log.info(VideoConstants.QUERY_RECORD_PLAN_OP + VideoConstants.CONTROLLERREQ + videoBaseReq);
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
    @Override
    public Result queryRecordPlanForPage(VideoChannelVideoPlanVO channelVideoPlanVO) {
        Result<List<QueryRecordPlanData>> res;
        log.info(VideoConstants.QUERY_RECORD_PLAN_FOR_PAGE_OP + VideoConstants.CONTROLLERREQ + channelVideoPlanVO);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_RECORD_PLAN_FOR_PAGE_OP + SecurityUtils.getOperator());
            channelVideoPlanVO.setTenantId(currentUser.getUser().getTenantId());
            channelVideoPlanVO.setIndustryCode(currentUser.getUser().getIndustryCode());
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
    @Override
    public Result<PresetData> queryPtzPosition(@RequestBody VideoBaseReq videoBaseReq) {
        Result<PresetData> res;
        log.info(VideoConstants.QUERY_PTZ_POSITION_OP + VideoConstants.CONTROLLERREQ + videoBaseReq);
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

    @ApiOperation(value = "查询PTZ位置",notes = "查询PTZ位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询PTZ位置请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    public Result<PresetData> queryPtzPosition2(@RequestBody VideoBaseReq videoBaseReq) {
        Result<PresetData> res;
        log.info(VideoConstants.QUERY_PTZ_POSITION_OP + VideoConstants.CONTROLLERREQ + videoBaseReq);
        try{
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,videoBaseReq.getTenantId());
            res = videoQueryService.queryPtzPosition(videoBaseReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_PTZ_POSITION_ERRO));
            log.error(VideoConstants.QUERY_PTZ_POSITION_ERRO , e);
        }
        return res;
    }

    @ApiOperation(value = "视频随动",notes = "视频随动")
    public Result<List<AlgoPresentVo>> getAlgoPresent(@RequestBody List<AlgoPresentVo> list) {
        return Result.data(videoQueryService.getAlgoPresent(list));
    }

    @Override
    public Result<PresetData> queryPtzSdk(VideoBaseReq videoBaseReq) {
        Result<PresetData> res;
        if(StringUtils.isEmpty(videoBaseReq.getTenantId()) || StringUtils.isEmpty(videoBaseReq.getIndustryCode())){
            return res = Result.fail("客户id和行业编码不能为空");
        }
        List<OrderDevicesVO > orderDevices = videoQueryService.getOrderDevices(videoBaseReq);
        if(CollectionUtil.isEmpty(orderDevices)){
           return res = Result.fail("当前客户没有摄像机订购权限");
        }
        res = queryPtzPosition2(videoBaseReq);
        return res;
    }

    @Override
    public Result<PresetData> queryPtzSdkTest(VideoBaseReq videoBaseReq) {
        return videoControlAPI.queryPtzSdk(videoBaseReq);
    }

    @Override
    public Result checkDeviceIsAuth(VideoBaseReq videoBaseReq) {
        Map<String, String> deviceOrderInfo = videoCommonService.checkDeviceAuth(videoBaseReq);
        if (deviceOrderInfo == null) {
            //无权限使用
            return Result.fail("设备无权限");
        }
        return Result.success("设备有权限");
    }

    /**
     * 根据传入的用户习惯信息批量获取用户习惯数据。
     *
     * @param habitVO 包含用户习惯请求信息的对象
     * @return 返回包含用户习惯数据的结果对象，目前实现中返回null
     */
    @Override
    public Result getUserHabitBatch(HabitVO habitVO) {
        Result res;
        LoginUser currentUser = SecurityUtils.getLoginUser();
        String userId = currentUser.getUser().getUserId();
        List<String> habitKeys = habitVO.getHabitKeyList();
        List<String> fullKeys = habitKeys.stream().filter(Objects::nonNull).map(key -> userId + "_" + key).collect(Collectors.toList());
        try{
            List<HabitVO> habitValue = videoQueryService.getUserHabitBatch(fullKeys);
            res = Result.data(habitValue);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(),GET_USER_HABIT_ERROR));
            log.error(GET_USER_HABIT_ERROR , e);
        }
        return res;
    }

    @ApiOperation(value = "查询预置点列表",notes = "查询预置点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询预置点列表请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    @Override
    public Result<List<QueryPresetListData>> queryPresetList(@RequestBody VideoBaseReq videoBaseReq) {
        Result<List<QueryPresetListData>> res;
        log.info(VideoConstants.QUERY_PRESET_LIST_OP + VideoConstants.CONTROLLERREQ + videoBaseReq);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_PRESET_LIST_OP + SecurityUtils.getOperator());
            Map<String,String> deviceOrderInfo = videoCommonService.checkDeviceAuth(videoBaseReq);
            if(deviceOrderInfo == null){
                // 返回错误 无权限使用
                log.info(VideoConstants.QUERY_PRESET_LIST_OP + VideoConstants.RESPONSE_LOG + VideoConstants.NO_AUTH);
                return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.NO_AUTH);
            }else{
                videoBaseReq.setOrderCode(deviceOrderInfo.get(VideoConstants.ORDERCODE));
            }
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            videoBaseReq.setIndustryCode(currentUser.getUser().getIndustryCode());
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
    @Override
    public Result<QueryGuardListData> queryGuardList(@RequestBody VideoBaseReq videoBaseReq) {
        Result<QueryGuardListData> res;
        log.info(VideoConstants.QUERY_GUARD_LIST_OP + VideoConstants.CONTROLLERREQ + videoBaseReq);
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
    @Override
    public Result<List<QueryPatrolData>> queryPatrol(@RequestBody VideoQueryPatrolData videoQueryPatrol) {
        Result<List<QueryPatrolData>> res;
        log.info(VideoConstants.QUERY_PATROL_OP + VideoConstants.CONTROLLERREQ + videoQueryPatrol);
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
            videoQueryPatrol.setIfAppOld(VideoConstants.ONE_STR);
            res = videoQueryService.queryPatrol(videoQueryPatrol);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_PATROL_ERRO));
            log.error(VideoConstants.QUERY_PATROL_ERRO , e);
        }
        return res;
    }

    /**
     * 功能描述: 分页查询巡航计划
     *
     * @param: videoQueryPatrol
     * @return: com.chinatower.common.entity.Result
     * @auther: wanglei
     * @date: 2023/6/12
     * @version: 1.5
     */
    @Override
    public Result<PageInfo<QueryPatrolData>> queryPatrolPage(@RequestBody VideoQueryPatrolData videoQueryPatrol) {
    	Result<PageInfo<QueryPatrolData>> res;
        log.info(VideoConstants.QUERY_PATROL_OP + VideoConstants.CONTROLLERREQ + videoQueryPatrol);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_PATROL_OP + SecurityUtils.getOperator());
            videoQueryPatrol.setIndustryCode(currentUser.getUser().getIndustryCode());
            videoQueryPatrol.setTenantId(currentUser.getUser().getTenantId());
            res = videoQueryService.queryPatrolPage(videoQueryPatrol);
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
    @Override
    public Result selectRoleById(@RequestBody QueryRoleReq queryRoleReq) {
        Result res;
        log.info(VideoConstants.SELECT_ROLE_BY_ID_OP + VideoConstants.CONTROLLERREQ + queryRoleReq);
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
    @Override
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
    @Override
    public Result getUserHabit(HabitVO habitVO) {
        Result res;
        LoginUser currentUser = SecurityUtils.getLoginUser();
        habitVO.setHabitKey(currentUser.getUser().getUserId() + "_" + habitVO.getHabitKey());
        try{
            String habitValue = videoQueryService.getUserHabit(habitVO);
            res = Result.data(habitValue);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(),GET_USER_HABIT_ERROR));
            log.error(GET_USER_HABIT_ERROR , e);
        }
        return res;
    }

    @ApiOperation(value = "获取视频格式字典表",notes = "获取视频格式字典表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryGetEnumInfoReq",value = "获取视频格式字典表请求实体",dataType = "QueryGetEnumInfoReq",required = true,paramType = "query")
    })
    @Override
    public Result getEnumInfo(@RequestBody QueryGetEnumInfoReq queryGetEnumInfoReq) {
        Result res;
        try{
            res = videoQueryService.getEnumInfo(queryGetEnumInfoReq);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.SELECT_ENUM_INFO_ERRO));
            log.error(VideoConstants.SELECT_ENUM_INFO_ERRO ,e);
        }
        return res;
    }


    @Override
    public Result queryPercentPoints(QueryPresentPointVo queryPresentPointVo) {
        Result<List<PercentPointDto>> res;
        log.info(VideoConstants.QUERY_PTZ_POSITION_OP + VideoConstants.CONTROLLERREQ + queryPresentPointVo);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_PTZ_POSITION_OP + SecurityUtils.getOperator());
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            res = videoQueryService.queryPercentPoints(queryPresentPointVo,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_PTZ_POSITION_ERRO));
            log.error(VideoConstants.QUERY_PTZ_POSITION_ERRO , e);
        }
        return res;
    }

    @Override
    public Result updateHistoryPoints() {
        Result<PresetData> res;
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_PTZ_POSITION_OP + SecurityUtils.getOperator());
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            res = videoQueryService.updateHistoryPoints(headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_PTZ_POSITION_ERRO));
            log.error(VideoConstants.QUERY_PTZ_POSITION_ERRO , e);
        }
        return res;

    }

}
