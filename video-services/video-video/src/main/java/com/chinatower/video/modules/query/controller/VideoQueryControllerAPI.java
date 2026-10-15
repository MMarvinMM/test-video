package com.chinatower.video.modules.query.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.videopreset.dataobject.VideoPresetDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.video.modules.common.entity.ResultEntity;
import com.chinatower.video.modules.common.service.VideoCommonService;
import com.chinatower.video.modules.common.util.VideoCommonUtil;
import com.chinatower.video.modules.player.service.VideoPlayerService;
import com.chinatower.video.modules.query.service.VideoQueryService;
import com.chinatower.video.modules.videocontrol.service.VideoControlService;
import com.chinatower.video.modules.videopatrolplan.service.IVideoPatrolPlanInfoService;
import com.chinatower.video.modules.videopreset.service.IGuardInfoService;
import com.chinatower.video.modules.videopreset.service.IVideoPresetInfoService;
import com.chinatower.video.modules.videorecordplan.service.IVideoRecordPlanInfoService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频查询controller
 * Created by ldl on 2022/1/27.
 */
@Api(value = "视频查询接口-Api",tags = "视频查询请求处理")
@Slf4j
@RestController
public class VideoQueryControllerAPI {

    private static final String X_APP_CODE_NEW = "X-AppCode-new";
    @Autowired
    VideoQueryService videoQueryService;
    @Autowired
    private IVideoPresetInfoService videoPresetInfoService;
    @Autowired
    private IVideoRecordPlanInfoService videoRecordPlanInfoService;
    @Autowired
    private IVideoPatrolPlanInfoService videoPatrolPlanInfoService;
    @Autowired
    private IGuardInfoService guardInfoService;
    @Autowired
    VideoPlayerService videoPlayerService;
    @Autowired
    VideoCommonService videoCommonService;
    @Autowired
    VideoControlService videoControlService;
    @Autowired
    private SystemFeignClient systemFeignClient;

    public final String LOG_FLAG = "废弃接口调用";
    /**
     * 功能说明：查询通道录像计划
     *
     * @author wangyt
     * @param  videoBaseReq
     * @return com.chinatower.common.entity.Result<java.util.List<com.chinatower.video.api.entity.QueryRecordPlanData>>
     * @requestID
     */
    @ApiOperation(value = "查询通道录像计划",notes = "查询通道录像计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询通道录像计划请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    @PostMapping("/plan/devices/{deviceCode}/channels/{channelCode}/queryRecordPlan")
    public ResultEntity<List<QueryRecordPlanData>> queryRecordPlan(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody VideoBaseReq videoBaseReq) {
        Result<List<QueryRecordPlanData>> res;
        videoBaseReq.setDeviceCode(deviceCode);
        videoBaseReq.setChannelCode(channelCode);
        videoBaseReq.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        log.info(VideoConstants.QUERY_RECORD_PLAN_OP + VideoConstants.APIREQ + videoBaseReq);
        try{
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,videoBaseReq.getTenantId());
            res = videoQueryService.queryRecordPlan(videoBaseReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_RECORD_PLAN_ERRO));
            log.error(VideoConstants.QUERY_RECORD_PLAN_ERRO , e);
        }
        return toResultEntity(res);
    }

    /**
     * @Description: 返回值解析
     * @Author: wangyt
     * @Date: 2022/9/21
     * @param result
     * @return com.chinatower.video.modules.common.entity.ResultEntity
     **/
    private ResultEntity toResultEntity(Result result){
        if(result.getCode() == VideoConstants.TWO_HUNDRED){
            return ResultEntity.success(result.getData());
        }else if(result.getCode() == VideoConstants.ERROR_4001){
            String msg = result.getMsg();
            JSONObject errorData = JSON.parseObject(msg);
            return ResultEntity.fail((String) errorData.get(VideoConstants.RESULT_CODE), (String) errorData.get(VideoConstants.RESULT_MSG));
        }else{
            return ResultEntity.fail(String.valueOf(result.getCode()), result.getMsg());
        }
    }

    /**
     * 功能说明：查询通道的录像列表
     *
     * @author wangyt
     * @param  videoPlayerQueryVideoListReq
     * @return com.chinatower.common.entity.Result<com.chinatower.video.api.entity.QueryVideoData>
     * @requestID
     */
    @ApiOperation(value = "查询通道的录像列表",notes = "查询通道的录像列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoPlayerQueryVideoListReq",value = "查询通道的录像列表请求实体",dataType = "VideoPlayerQueryVideoListReq",required = true,paramType = "query")
    })
    @Deprecated
    @PostMapping("/devices/{deviceCode}/channels/{channelCode}/queryVideoList")
    public ResultEntity<QueryVideoData> queryVideoList(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq) {
        log.info(LOG_FLAG);
        Result<QueryVideoData> res;
        videoPlayerQueryVideoListReq.setDeviceCode(deviceCode);
        videoPlayerQueryVideoListReq.setChannelCode(channelCode);
        videoPlayerQueryVideoListReq.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));

        //判断上游是否是video-service-api服务
        String industryCode = request.getHeader(X_APP_CODE_NEW);
        if (StringUtils.isEmpty(industryCode)) {
            industryCode = request.getHeader(VideoConstants.XAPPCODE);
        }
        videoPlayerQueryVideoListReq.setIndustryCode(industryCode);

        String startTime = videoPlayerQueryVideoListReq.getStartTime();
        String endTime = videoPlayerQueryVideoListReq.getEndTime();
        List<Map<String, String>> times = videoPlayerQueryVideoListReq.getTimes();
        // 如果startTime/endTime/times都没值，直接校验失败
        if( (startTime == null || startTime.length() == 0) && (endTime == null || endTime.length() == 0) && (times == null || times.size() == 0) )
        {
        	return ResultEntity.fail(VideoConstants.FAIL_CODE, VideoConstants.ERROR_PRE + VideoConstants.NOT_EMPTY_START_END_TIME);
        }
        // 如果startTime/endTime有值，则优先取startTime/endTime的值
        // 当startTime/endTime没值，times有值，则从times取参数值
        if( (startTime == null || startTime.length() == 0) && (endTime == null || endTime.length() == 0) && (times != null && times.size() > 0) )
        {
	        videoPlayerQueryVideoListReq.setStartTime(times.get(0).get("startTime"));
	        videoPlayerQueryVideoListReq.setEndTime(times.get(0).get("endTime"));
        }
        log.info(VideoConstants.QUERY_VIDEO_LIST_OP + VideoConstants.APIREQ + videoPlayerQueryVideoListReq);
        //首先判断传值问题，为空判断在实体中完成，此处只需判断传值的正确与否
        /*if (videoPlayerQueryVideoListReq.getStorageType() != 0 && videoPlayerQueryVideoListReq.getStorageType() != 1) {
            return ResultEntity.fail(VideoConstants.FAIL_CODE, VideoConstants.ERROR_PRE + VideoConstants.STORAGE_TYPE_ERRO);
        }*/
        if(videoPlayerQueryVideoListReq.getRecordType() != 0 && videoPlayerQueryVideoListReq.getRecordType() != 1 && videoPlayerQueryVideoListReq.getRecordType() != 2) {
            return ResultEntity.fail(VideoConstants.FAIL_CODE, VideoConstants.ERROR_PRE + VideoConstants.RECORD_TYPE_ERRO);
        }
        try{
            // 获取当前用户信息
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,videoPlayerQueryVideoListReq.getTenantId());

            res = videoPlayerService.queryVideoList(videoPlayerQueryVideoListReq,headerMap);

        } catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_VIDEO_LIST_ERRO));
            log.error(VideoConstants.QUERY_VIDEO_LIST_ERRO , e);
        }
        log.info(VideoConstants.QUERY_VIDEO_LIST_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(videoPlayerQueryVideoListReq) + JSONObject.toJSONString(res));
        return toResultEntity(res);
    }

    /**
     * 功能说明：新增通道录像计划
     *
     * @author wangyt
     * @param  channelVideoPlanVO
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "新增通道录像计划")
    @PostMapping("/plan/devices/{deviceCode}/channels/{channelCode}/addRecordPlan")
    public ResultEntity<String> addRecordPlan(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @Validated @RequestBody ChannelVideoPlanVO channelVideoPlanVO) {
        channelVideoPlanVO.setDeviceCode(deviceCode);
        channelVideoPlanVO.setChannelCode(channelCode);
        channelVideoPlanVO.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        channelVideoPlanVO.setIndustryCode(request.getHeader(VideoConstants.XAPPCODE));
        log.info("addRecordPlan" + VideoConstants.APIREQ + channelVideoPlanVO);
        videoRecordPlanInfoService.addRecordPlanSingle(channelVideoPlanVO);
        return ResultEntity.success("新增通道录像计划成功!");
    }

    /**
     * 功能说明：修改通道录像计划
     *
     * @author wangyt
     * @param  channelVideoPlanVO
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "修改通道录像计划")
    @PostMapping("/plan/devices/{deviceCode}/channels/{channelCode}/updateRecordPlan")
    public ResultEntity<String> updateRecordPlan(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @Validated @RequestBody ChannelVideoPlanVO channelVideoPlanVO) {
        channelVideoPlanVO.setDeviceCode(deviceCode);
        channelVideoPlanVO.setChannelCode(channelCode);
        channelVideoPlanVO.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        channelVideoPlanVO.setIndustryCode(request.getHeader(VideoConstants.XAPPCODE));
        log.info("updateRecordPlan" + VideoConstants.APIREQ + channelVideoPlanVO);
        videoRecordPlanInfoService.updateRecordPlan(channelVideoPlanVO);
        return ResultEntity.success("修改通道录像计划成功!");
    }

    /**
     * 功能说明：删除通道录像计划
     *
     * @author wangyt
     * @param  deleteChannelVideoPlanVO
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "删除通道录像计划")
    @PostMapping("/plan/devices/{deviceCode}/channels/{channelCode}/deleteRecordPlan")
    public ResultEntity<String> deleteRecordPlan(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody DeleteChannelVideoPlanVO deleteChannelVideoPlanVO) {
        deleteChannelVideoPlanVO.setDeviceCode(deviceCode);
        deleteChannelVideoPlanVO.setChannelCode(channelCode);
        deleteChannelVideoPlanVO.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        deleteChannelVideoPlanVO.setIndustryCode(request.getHeader(VideoConstants.XAPPCODE));
        log.info("deleteRecordPlan" + VideoConstants.APIREQ + deleteChannelVideoPlanVO);
        videoRecordPlanInfoService.deleteRecordPlan(deleteChannelVideoPlanVO);
        return ResultEntity.success("删除改通道录像计划成功!");
    }

    /**
     * 功能说明：查询全部录像计划
     *
     * @author wangyt
     * @param  channelVideoPlanVO
     * @return com.chinatower.common.entity.Result<>
     * @requestID
     */
    @ApiOperation(value = "查询全部录像计划",notes = "查询全部录像计划")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryRecordPlanForPageReq",value = "查询全部录像计划请求实体",dataType = "QueryRecordPlanForPageReq",required = true,paramType = "query")
    })
    @PostMapping("/plan/devices/{deviceCode}/channels/{channelCode}/queryRecordPlanForPage")
    public ResultEntity<List<QueryRecordPlanData>> queryRecordPlanForPage(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody VideoChannelVideoPlanVO channelVideoPlanVO) {
        Result<List<QueryRecordPlanData>> res;
        channelVideoPlanVO.setDeviceCode(deviceCode);
        channelVideoPlanVO.setChannelCode(channelCode);
        channelVideoPlanVO.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        log.info(VideoConstants.QUERY_RECORD_PLAN_FOR_PAGE_OP + VideoConstants.APIREQ + channelVideoPlanVO);
        try{
            res = videoQueryService.queryRecordPlanForPage(channelVideoPlanVO);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_RECORD_PLAN_ERRO));
            log.error(VideoConstants.QUERY_RECORD_PLAN_ERRO , e);
        }
        return toResultEntity(res);
    }

    /**
     * 功能说明：增加预置点
     *
     * @author wangyt
     * @param  addPresetReq
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "增加预置点", notes = "增加预置点")
    @PostMapping("/preset/devices/{deviceCode}/channels/{channelCode}/preset/addCameraPreset")
    public ResultEntity<String> addCameraPreset(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @Validated @RequestBody AddPresetReq addPresetReq) {
        addPresetReq = assignment(addPresetReq, request, deviceCode, channelCode);
        //判断上游是否是video-service-api服务
        String industryCode = request.getHeader(X_APP_CODE_NEW);
        if (StringUtils.isEmpty(industryCode)) {
            industryCode = request.getHeader(VideoConstants.XAPPCODE);
        }
        addPresetReq.setIndustryCode(industryCode);
        log.info("addCameraPreset" + VideoConstants.APIREQ + addPresetReq);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,addPresetReq.getTenantId());
        long presetId = videoPresetInfoService.addCameraPreset(addPresetReq,headerMap);
        return ResultEntity.success(String.valueOf(presetId));
    }

    /**
     * @Description: 通用数据赋值
     * @Author: wangyt
     * @Date: 2022/8/24
     * @param model
     * @param request
     * @param deviceCode
     * @param channelCode
     * @return T
     **/
    private <T extends DeviceSouthCommonDTO> T assignment(T model, HttpServletRequest request, String deviceCode, String channelCode){
        model.setDeviceCode(deviceCode);
        model.setChannelCode(channelCode);
        model.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        model.setUserCode(request.getHeader(VideoConstants.XUSERCODE));
        model.setTowerCode(request.getHeader(VideoConstants.XTOWERCODE));
        model.setOrderCode(request.getHeader(VideoConstants.XORDERCODE));
        //判断上游是否是video-service-api服务
        String industryCode = request.getHeader(X_APP_CODE_NEW);
        if (StringUtils.isEmpty(industryCode)) {
            industryCode = request.getHeader(VideoConstants.XAPPCODE);
        }
        model.setIndustryCode(industryCode);
        return model;
    }

    /**
     * 功能说明：删除预置点
     *
     * @author wangyt
     * @param  presetDTO
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "删除预置点", notes = "删除预置点")
    @PostMapping("/preset/devices/{deviceCode}/channels/{channelCode}/preset/{presetId}/delCameraPreset")
    public ResultEntity<String> delCameraPreset(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, @PathVariable("presetId") Long presetId, HttpServletRequest request, @RequestBody DelPresetReq presetDTO) {
        presetDTO = assignment(presetDTO, request, deviceCode, channelCode);
        presetDTO.setPresetId(presetId);
        presetDTO.setDeviceCode(deviceCode);
        log.info("delCameraPreset" + VideoConstants.APIREQ + presetDTO);
        videoPresetInfoService.delCameraPreset(presetDTO);
        return ResultEntity.success("删除预置点成功!");
    }

    /**
     * 功能说明：修改预置点
     *
     * @author wangyt
     * @param  modPresetReq
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "修改预置点", notes = "修改预置点")
    @PostMapping("/preset/devices/{deviceCode}/channels/{channelCode}/updatePresetInfo")
    public ResultEntity<String> updatePresetInfo(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @Validated @RequestBody ModPresetReq modPresetReq) {
        modPresetReq = assignment(modPresetReq, request, deviceCode, channelCode);
        log.info("updatePresetInfo" + VideoConstants.APIREQ + modPresetReq);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE,modPresetReq.getTenantId());
        videoPresetInfoService.updatePresetInfo(modPresetReq,headerMap);
        return ResultEntity.success("修改预置点成功!");
    }

    /**
     * 功能说明：查询PTZ位置
     *
     * @author wangyt
     * @param  videoBaseReq
     * @return com.chinatower.common.entity.Result<com.chinatower.video.api.entity.PresetData>
     * @requestID
     */
    @ApiOperation(value = "查询PTZ位置",notes = "查询PTZ位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询PTZ位置请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    @PostMapping("/plat/devices/{deviceCode}/channels/{channelCode}/ptz/queryPtzPosition")
    public ResultEntity<PresetData> queryPtzPosition(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody VideoBaseReq videoBaseReq) {
        Result<PresetData> res;
        videoBaseReq.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        videoBaseReq.setDeviceCode(deviceCode);
        videoBaseReq.setChannelCode(channelCode);
        log.info(VideoConstants.QUERY_PTZ_POSITION_OP + VideoConstants.APIREQ + videoBaseReq);
        try{
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,videoBaseReq.getTenantId());
            res = videoQueryService.queryPtzPosition(videoBaseReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_PTZ_POSITION_ERRO));
            log.error(VideoConstants.QUERY_PTZ_POSITION_ERRO , e);
        }
        return toResultEntity(res);
    }

    /**
     * 功能说明：查询预置点列表
     *
     * @author wangyt
     * @param  videoBaseReq
     * @return com.chinatower.common.entity.Result<java.util.List<com.chinatower.video.api.entity.QueryPresetListData>>
     * @requestID
     */
    @ApiOperation(value = "查询预置点列表",notes = "查询预置点列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询预置点列表请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    @PostMapping("/preset/devices/{deviceCode}/channels/{channelCode}/queryPresetList")
    public ResultEntity<List<QueryPresetListData>> queryPresetList(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody VideoBaseReq videoBaseReq) {
        Result<List<QueryPresetListData>> res;
        videoBaseReq.setDeviceCode(deviceCode);
        videoBaseReq.setChannelCode(channelCode);
        videoBaseReq.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        //判断上游是否是video-service-api服务
        String industryCode = request.getHeader(X_APP_CODE_NEW);
        if (StringUtils.isEmpty(industryCode)) {
            industryCode = request.getHeader(VideoConstants.XAPPCODE);
        }
        videoBaseReq.setIndustryCode(industryCode);
        log.info(VideoConstants.QUERY_PRESET_LIST_OP + VideoConstants.APIREQ + videoBaseReq);
        log.info("X-CustomerCode==={},X-AppCode==={}",request.getHeader(VideoConstants.CUSTOMERCODE), request.getHeader(VideoConstants.XAPPCODE));
        try{
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,videoBaseReq.getTenantId());
            res = videoQueryService.queryPresetList(videoBaseReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_PRESET_LIST_ERRO));
            log.error(VideoConstants.QUERY_PRESET_LIST_ERRO , e);
        }
        return toResultEntity(res);
    }

    /**
     * 功能说明：查询看守位
     *
     * @author wangyt
     * @param  videoBaseReq
     * @return com.chinatower.common.entity.Result<com.chinatower.video.api.entity.QueryGuardListData>
     * @requestID
     */
    @ApiOperation(value = "查询看守位",notes = "查询看守位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoBaseReq",value = "查询看守位请求实体",dataType = "VideoBaseReq",required = true,paramType = "query")
    })
    @PostMapping("/guard/devices/{deviceCode}/channels/{channelCode}/queryGuardList")
    public ResultEntity<QueryGuardListData> queryGuardList(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody VideoBaseReq videoBaseReq) {
        Result<QueryGuardListData> res;
        videoBaseReq.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        videoBaseReq.setUserCode(request.getHeader(VideoConstants.XUSERCODE));
        videoBaseReq.setDeviceCode(deviceCode);
        videoBaseReq.setChannelCode(channelCode);
        log.info(VideoConstants.QUERY_GUARD_LIST_OP + VideoConstants.APIREQ + videoBaseReq);
        try{
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,videoBaseReq.getTenantId());
            //  videoBaseReq.setUserCode(SecurityUtils.getOperator());
            res = videoQueryService.queryGuardList(videoBaseReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_GUARD_LIST_ERRO));
            log.error(VideoConstants.QUERY_GUARD_LIST_ERRO , e);
        }
        return toResultEntity(res);
    }

    /**
     * 功能说明：设置看守卫
     *
     * @author wangyt
     * @param  guardDTO
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "设置看守卫", notes = "设置看守卫")
    @PostMapping("/guard/devices/{deviceCode}/channels/{channelCode}/doSetGuard")
    public ResultEntity<String>  doSetGuard(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @Validated @RequestBody GuardDTO guardDTO) {
        guardDTO = assignment(guardDTO, request, deviceCode, channelCode);
        log.info("doSetGuard" + VideoConstants.APIREQ + guardDTO);
        if (StringUtils.isEmpty(guardDTO.getUserCode()) || StringUtils.isEmpty(guardDTO.getTenantId())) {
            throw new CustomException(DeviceConstants.NO_USER_INFO);
        }
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put(VideoConstants.CUSTOMERCODE, guardDTO.getTenantId());
        long guardId = guardInfoService.doSetGuard(guardDTO,headerMap);
        if(guardId == 0){
            return ResultEntity.fail("", "设置看守卫失败!");
        }else if(guardId == -1){
            return ResultEntity.fail("", "获取云台锁定权限失败，暂时无法设置看守位!");
        }else if(guardId == -2){
            return ResultEntity.fail("", "当前摄像机已有看守位，无法设置。");
        }else {
            return ResultEntity.success("设置看守卫成功!");
        }
    }

    /**
     * 功能说明：增加巡航路径
     *
     * @author wangyt
     * @param  patrolDTO
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "增加巡航路径", notes = "增加巡航路径")
    @PostMapping("/cruise/devices/{deviceCode}/channels/{channelCode}/addCruisePatrol")
    public ResultEntity<String> addCruisePatrol(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @Validated @RequestBody AddPatrolReq patrolDTO) {
        patrolDTO = assignment(patrolDTO, request, deviceCode, channelCode);
        log.info("addCruisePatrol" + VideoConstants.APIREQ + patrolDTO);
        patrolDTO.setIndustryCode(request.getHeader(VideoConstants.XAPPCODE));
        videoPatrolPlanInfoService.addCruisePatrol(patrolDTO);
        return ResultEntity.success("增加巡航路径成功!");
    }

    /**
     * 功能说明：修改巡航路径
     *
     * @author wangyt
     * @param  patrolDTO
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "修改巡航路径", notes = "修改巡航路径")
    @PostMapping("/cruise/devices/{deviceCode}/channels/{channelCode}/updatePatrol")
    public ResultEntity<String> updatePatrol(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @Validated @RequestBody ModPatrolReq patrolDTO) {
        patrolDTO.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        patrolDTO.setIndustryCode(request.getHeader(VideoConstants.XAPPCODE));
        patrolDTO.setDeviceCode(deviceCode);
        patrolDTO.setChannelCode(channelCode);
        log.info("updatePatrol" + VideoConstants.APIREQ + patrolDTO);
        videoPatrolPlanInfoService.updatePatrol(patrolDTO);
        return ResultEntity.success("修改巡航路径成功!");
    }

    /**
     * 功能说明：删除巡航路径
     *
     * @author wangyt
     * @param  patrolDTO
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "删除巡航路径", notes = "删除巡航路径")
    @PostMapping("/cruise/devices/{deviceCode}/channels/{channelCode}/delPatrol")
    public ResultEntity<String> delPatrol(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @Validated @RequestBody DelPatrolReq patrolDTO) {
        patrolDTO = assignment(patrolDTO, request, deviceCode, channelCode);
        log.info("delPatrol" + VideoConstants.APIREQ + patrolDTO);
        videoPatrolPlanInfoService.delPatrol(patrolDTO);
        return ResultEntity.success("删除巡航路径成功!");
    }

    /**
     * 功能说明：查询巡航路径
     *
     * @author wangyt
     * @param  videoQueryPatrol
     * @return com.chinatower.common.entity.Result<java.util.List<com.chinatower.video.api.entity.QueryPatrolData>>
     * @requestID
     */
    @ApiOperation(value = "查询巡航路径",notes = "查询巡航路径")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoQueryPatrol",value = "查询巡航路径请求实体",dataType = "VideoQueryPatrol",required = true,paramType = "query")
    })
    @PostMapping("/cruise/devices/{deviceCode}/channels/{channelCode}/queryPatrol")
    public ResultEntity<List<QueryPatrolData>> queryPatrol(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody VideoQueryPatrolData videoQueryPatrol) {
        Result<List<QueryPatrolData>> res;
        videoQueryPatrol.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
        videoQueryPatrol.setDeviceCode(deviceCode);
        videoQueryPatrol.setChannelCode(channelCode);
        log.info(VideoConstants.QUERY_PATROL_OP + VideoConstants.APIREQ + videoQueryPatrol);
        try{
            res = videoQueryService.queryPatrol(videoQueryPatrol);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_PATROL_ERRO));
            log.error(VideoConstants.QUERY_PATROL_ERRO , e);
        }
        return toResultEntity(res);
    }

    /**
     * 功能说明：停用/启用巡航路径
     *
     * @author wangyt
     * @param  patrolDTO
     * @return com.chinatower.common.entity.Result<java.lang.String>
     * @requestID
     */
    @ApiOperation(value = "停用/启用巡航路径", notes = "停用/启用巡航路径")
    @PostMapping("/cruise/devices/{deviceCode}/channels/{channelCode}/doPatrol")
    public ResultEntity<String> doPatrol(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @Validated @RequestBody SttPatrolReq patrolDTO) {
        patrolDTO = assignment(patrolDTO, request, deviceCode, channelCode);
        log.info("doPatrol" + VideoConstants.APIREQ + patrolDTO);
        Result<String> result = videoPatrolPlanInfoService.doPatrol(patrolDTO);
        return toResultEntity(result);
    }

    /**
     * 功能说明：查询事件告警详情
     *
     * @author wangyt
     * @param  videoRequestDTO
     * @return com.chinatower.common.entity.Result<com.alibaba.fastjson.JSONObject>
     * @requestID
     */
    @ApiOperation(value = "查询事件告警详情", notes = "查询事件告警详情")
    @PostMapping("/video/patrol/api/getAlarmInfo")
    public Result<JSONObject> getAlarmInfo(@Validated @RequestBody VideoRequestDTO videoRequestDTO) {
        log.info("getAlarmInfo" + VideoConstants.APIREQ + videoRequestDTO);
        return guardInfoService.getAlarmInfo(videoRequestDTO);
    }


    /**
     * 功能说明：开始/结束云台转动
     *
     * @author hued
     * @param  doPtzCmdsReq
     * @return com.chinatower.common.entity.Result<com.chinatower.video.api.entity.QueryVideoData>
     * @requestID
     */
    @ApiOperation(value = "开始/结束云台转动",notes = "开始/结束云台转动")
    @PostMapping("/plat/devices/{deviceCode}/channels/{channelCode}/ptz/doPtzCmds")
    public ResultEntity doPtzCmds(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody DoPtzCmdsReq doPtzCmdsReq) {
        Result res;
        log.info(VideoConstants.PTZCMDS_OP + VideoConstants.APIREQ + doPtzCmdsReq);
        try {
            Map<String, String> headerMap = new HashMap<>();
            doPtzCmdsReq.setDeviceCode(deviceCode);
            doPtzCmdsReq.setChannelCode(channelCode);
            doPtzCmdsReq.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));

            //判断上游是否是video-service-api服务
            String industryCode = request.getHeader(X_APP_CODE_NEW);
            if (StringUtils.isEmpty(industryCode)) {
                industryCode = request.getHeader(VideoConstants.XAPPCODE);
            }
            doPtzCmdsReq.setIndustryCode(industryCode);

            doPtzCmdsReq.setUserId(request.getHeader(VideoConstants.XUSERCODE));
            headerMap.put(VideoConstants.CUSTOMERCODE,request.getHeader(VideoConstants.CUSTOMERCODE));
            // 当前接口定制的头
            headerMap.put(VideoConstants.XTOKEN, doPtzCmdsReq.getLockToken());
            headerMap.put(VideoConstants.X_LOCK_TOKEN, doPtzCmdsReq.getLockToken());
            // 调用云台转动方法
            res = videoControlService.doPtzCmds(doPtzCmdsReq,headerMap,false);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.PTZCMDS_ERROR));
            log.error(VideoConstants.PTZCMDS_ERROR , e);
        }
        log.info(VideoConstants.PTZCMDS_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doPtzCmdsReq) + JSON.toJSONString(res));
        return toResultEntity(res);
    }


    /**
     * 功能说明：开始/结束聚焦(focus)/缩放(zoom)
     *
     * @author hued
     * @param  doCameraFocusOrZoomReq
     * @return com.chinatower.common.entity.Result<com.chinatower.video.api.entity.QueryVideoData>
     * @requestID
     */
    @ApiOperation(value = "开始/结束聚焦(focus)/缩放(zoom)",notes = "开始/结束聚焦(focus)/缩放(zoom)")
    @PostMapping("/plat/devices/{deviceCode}/channels/{channelCode}/camera/doCameraFocusOrZoom")
    public ResultEntity doCameraFocusOrZoom(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody DoCameraFocusOrZoomReq doCameraFocusOrZoomReq) {
        Result res;
        log.info(VideoConstants.FOCUSORZOOM_OP + VideoConstants.APIREQ + doCameraFocusOrZoomReq);
        try {
            Map<String, String> headerMap = new HashMap<>();
            doCameraFocusOrZoomReq.setDeviceCode(deviceCode);
            doCameraFocusOrZoomReq.setChannelCode(channelCode);
            doCameraFocusOrZoomReq.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
            doCameraFocusOrZoomReq.setUserId(request.getHeader(VideoConstants.XUSERCODE));
            headerMap.put(VideoConstants.CUSTOMERCODE,request.getHeader(VideoConstants.CUSTOMERCODE));
            // 当前接口定制的头
            headerMap.put(VideoConstants.XTOKEN, doCameraFocusOrZoomReq.getLockToken());
            // 调用云台转动方法
            res = videoControlService.doCameraFocusOrZoom(doCameraFocusOrZoomReq,headerMap,false);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.PTZCMDS_ERROR));
            log.error(VideoConstants.PTZCMDS_ERROR , e);
        }
        log.info(VideoConstants.FOCUSORZOOM_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doCameraFocusOrZoomReq) + JSON.toJSONString(res));
        return toResultEntity(res);
    }

    /**
     * 功能说明：转到预置点
     *
     * @author wangyt
     * @param  doControlPresetReq
     * @return com.chinatower.common.entity.Result<com.chinatower.video.api.entity.QueryVideoData>
     * @requestID
     */
    @ApiOperation(value = "转到预置点")
    @PostMapping("/preset/devices/{deviceCode}/channels/{channelCode}/doControlPreset")
    public ResultEntity doCameraFocusOrZoom(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody DoControlPresetReq doControlPresetReq) {
        Result res;
        log.info(VideoConstants.CONTROLPRESET_OP + VideoConstants.APIREQ + doControlPresetReq);
        try {
            Map<String, String> headerMap = new HashMap<>();
            doControlPresetReq.setDeviceCode(deviceCode);
            doControlPresetReq.setChannelCode(channelCode);
            doControlPresetReq.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
            doControlPresetReq.setUserId(request.getHeader(VideoConstants.XUSERCODE));
            headerMap.put(VideoConstants.CUSTOMERCODE,request.getHeader(VideoConstants.CUSTOMERCODE));
            // 当前接口定制的头
            headerMap.put(VideoConstants.XTOKEN,doControlPresetReq.getLockToken());
            // 调用转到预置点方法
            res = videoControlService.doControlPreset(doControlPresetReq,headerMap,false);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.CONTROLPRESET_ERROR));
            log.error(VideoConstants.CONTROLPRESET_ERROR + e.getMessage());
        }
        log.info(VideoConstants.CONTROLPRESET_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doControlPresetReq) + JSON.toJSONString(res));
        return toResultEntity(res);
    }

    /**
     * 功能说明：转到指定PTZ位置
     *
     * @author wangyt
     * @param  doPtzLocationCmdReq
     * @return com.chinatower.common.entity.Result<com.chinatower.video.api.entity.QueryVideoData>
     * @requestID
     */
    @ApiOperation(value = "转到指定PTZ位置")
    @PostMapping("/plat/devices/{deviceCode}/channels/{channelCode}/ptz/doPtzLocationCmd")
    public Result doPtzLocationCmd(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody DoPtzLocationCmdReq doPtzLocationCmdReq) {
        Result res;
        log.info(VideoConstants.PTZLOCATIONCMD_OP + VideoConstants.APIREQ + doPtzLocationCmdReq);
        try {
            Map<String, String> headerMap = new HashMap<>();
            doPtzLocationCmdReq.setDeviceCode(deviceCode);
            doPtzLocationCmdReq.setChannelCode(channelCode);
            doPtzLocationCmdReq.setTenantId(request.getHeader(VideoConstants.CUSTOMERCODE));
            doPtzLocationCmdReq.setUserId(request.getHeader(VideoConstants.XUSERCODE));
            headerMap.put(VideoConstants.CUSTOMERCODE,doPtzLocationCmdReq.getTenantId());
            // 当前接口定制的头
            headerMap.put(VideoConstants.XTOKEN,doPtzLocationCmdReq.getLockToken());
            // 调用转到指定PTZ位置
            res = videoControlService.doPtzLocationCmd(doPtzLocationCmdReq,headerMap,false);
        } catch (Exception e) {
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.PTZLOCATIONCMD_ERROR));
            log.error(VideoConstants.PTZLOCATIONCMD_ERROR + e.getMessage());
        }
        log.info(VideoConstants.PTZLOCATIONCMD_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(doPtzLocationCmdReq) + JSON.toJSONString(res));
        return res;
    }

    /**
     * 6.49 批量获取通道实时流URL地址
     * @param list
     * @param request
     * @return
     */
    @Deprecated
    @ApiOperation(value = "批量查询实时流地址")
    @PostMapping("/devices/channels/getVideoRealtimeUrls")
    public ResultEntity getVideoRealtimeUrls(@RequestBody List<Map> list, HttpServletRequest request){
        log.info(LOG_FLAG);
        log.info("========getVideoRealtimeUrls请求报文：" + list);
        ResultEntity res;
        try {
            Result<List<SysDictDataResponse>> enableCustomerDictDataresult = systemFeignClient.dictDataType(VideoConstants.bath_VideoRealtimeUrl);
            if (!StringUtils.isEmpty(enableCustomerDictDataresult.getData())
                    &&enableCustomerDictDataresult.getData().size()>0
                    &&enableCustomerDictDataresult.getData().stream().noneMatch(SysDictDataResponse->SysDictDataResponse.getDictValue().equals(request.getHeader(VideoConstants.CUSTOMERCODE)))) {
                return ResultEntity.fail(VideoConstants.FAIL_CODE, "当前客户没有权限");
            }
            Result<List<SysDictDataResponse>> enableChannelSizeDictDataresult = systemFeignClient.dictDataType(VideoConstants.enableChannelSize);
            if ((!StringUtils.isEmpty(enableChannelSizeDictDataresult.getData()))
                    &&(enableChannelSizeDictDataresult.getData().size()>0)
                    &&(list.size() > Integer.parseInt(enableChannelSizeDictDataresult.getData().get(0).getDictValue()))){
                return ResultEntity.fail(VideoConstants.FAIL_CODE, "当前请求通道数量超过允许的最大值");
            }
            res = videoQueryService.getVideoRealtimeUrls(list);
            log.info("========getVideoRealtimeUrls返回报文：" + res);
        }catch(Exception e){
            log.info(VideoConstants.GETVIDEOREALTIMEURLS_INTERFACE+VideoConstants.COMMA+e.getMessage());
            return ResultEntity.fail(VideoConstants.FAIL_CODE,  VideoConstants.GETVIDEOREALTIMEURLS_ERRO);
        }
            return res;
    }



    /**
     * 6.2 获取通道加密视频流API接口
     * @param
     * @param request
     * @return
     */
    @Deprecated
    @ApiOperation(value = "获取通道加密视频流API接口")
    @PostMapping("/devices/{deviceCode}/channels/{channelCode}/getVideoEncryptRealtimeUrl")
    public Result<PlayerGetVideoRealtimeUrlData> getVideoEncryptRealtimeUrl(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody PlayerGetVideoRealtimeUrlReq playerGetVideoRealtimeUrlReq, HttpServletRequest request) {
        log.info(LOG_FLAG);
        Result<PlayerGetVideoRealtimeUrlData> res;
        playerGetVideoRealtimeUrlReq.setDeviceCode(deviceCode);
        playerGetVideoRealtimeUrlReq.setChannelCode(channelCode);
        log.info(VideoConstants.GET_VIDEO_ENCRYPT_REAL_TIME_URL_OP + VideoConstants.SDKREQ + playerGetVideoRealtimeUrlReq);
        if (playerGetVideoRealtimeUrlReq.getProtocolType() != VideoConstants.SIX_NUM) {
            return Result.fail(VideoConstants.GET_VIDEO_ENCRYPT_REAL_PROTOCOL_TYPE);
        }
        if (StringUtils.isEmpty(request.getHeader(VideoConstants.XPLUGID))) {
            return Result.fail(VideoConstants.GET_VIDEO_ENCRYPT_REAL_PLUGID);
        }

        playerGetVideoRealtimeUrlReq.setPlugId(request.getHeader(VideoConstants.XPLUGID));
        playerGetVideoRealtimeUrlReq.setStreamUuid(request.getHeader(VideoConstants.X_StreamUuid));

        try{
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,request.getHeader(VideoConstants.CUSTOMERCODE));
            headerMap.put(VideoConstants.X_StreamUuid,request.getHeader(VideoConstants.X_StreamUuid));
            headerMap.put(VideoConstants.XPLUGID,request.getHeader(VideoConstants.XPLUGID));
            res = videoPlayerService.getVideoRealtimeUrl(playerGetVideoRealtimeUrlReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.GET_VIDEO_ENCRYPT_REAL_ERROR));
            log.error(VideoConstants.GET_VIDEO_ENCRYPT_REAL_ERROR , e);
        }
        return res;
    }


    /**
     * 6.49 批量获取通道实时流URL地址 看这里sdk用
     * @param list
     * @return
     */
    @Deprecated
    @ApiOperation(value = "批量查询实时流地址")
    @PostMapping("/devices/channels/getVideoRealtimeUrlsForLookHere")
    public Result getVideoRealtimeUrlsForLookHere(@RequestBody List<Map> list){
        log.info(LOG_FLAG);
        try {
            ResultEntity res = videoQueryService.getVideoRealtimeUrls(list);
            if("0000".equals(res.getReturnCode())){
                return Result.data(res.getData());
            }else{
                return Result.fail(res.getReturnMsg() + res.getErrorMsg());
            }
        }catch(Exception e){
            log.info(VideoConstants.GETVIDEOREALTIMEURLS_INTERFACE,e);
            return Result.fail(VideoConstants.GETVIDEOREALTIMEURLS_ERRO);
        }
    }

}
