package com.chinatower.stdu.player.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.stdu.common.service.VideoCommonService;
import com.chinatower.stdu.common.util.VideoCommonUtil;
import com.chinatower.stdu.player.service.VideoPlayerService;
import com.chinatower.video.api.VideoPlayerAPI;
import com.chinatower.video.api.entity.*;
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
import java.util.Map;

/**
 * 视频播放controller
 * Created by ldl on 2022/1/11.
 */
@Api(value = "视频播放接口-Api",tags = "视频播放请求处理")
@Slf4j
@RestController
public class VideoPlayerControllerSDK {

    @Autowired
    VideoPlayerService videoPlayerService;

    @Autowired
    VideoCommonService videoCommonService;

    @Autowired
    private VideoPlayerAPI videoPlayerAPI;


    @ApiOperation(value = "倍速回放",notes = "倍速回放")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "playerPlayBackControlReq",value = "倍速回放请求实体",dataType = "PlayerPlayBackControlReq",required = true,paramType = "query")
    })
    @PostMapping("/video/player/sdk/playBackControl")
    public Result playBackControl(@RequestBody PlayerPlayBackControlReq playerPlayBackControlReq) {
        Result res;
        log.info(VideoConstants.PLAY_BACK_CONTROL_OP + VideoConstants.SDKREQ + playerPlayBackControlReq);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.PLAY_BACK_CONTROL_OP + SecurityUtils.getOperator());
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            headerMap.put(VideoConstants.XORDERCODE,playerPlayBackControlReq.getOrderCode());
            checkAuth(playerPlayBackControlReq);
            res = videoPlayerService.playBackControl(playerPlayBackControlReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.PLAY_BACK_CONTROL_ERRO));
            log.error(VideoConstants.PLAY_BACK_CONTROL_ERRO , e);
        }
        return res;
    }


    @ApiOperation(value = "获取语音对讲通道",notes = "获取语音对讲通道")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "playerGetAudioChannelReq",value = "获取语音对讲通道请求实体",dataType = "PlayerGetAudioChannelReq",required = true,paramType = "query")
    })
    @PostMapping("/video/player/sdk/getAudioChannel")
    public Result<GetAudioChannelData> getAudioChannel(@RequestBody PlayerGetAudioChannelReq playerGetAudioChannelReq) {
        Result<GetAudioChannelData> res;
        log.info(VideoConstants.GET_AUDIO_CHANNEL_OP + VideoConstants.SDKREQ + playerGetAudioChannelReq);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.GET_AUDIO_CHANNEL_OP + SecurityUtils.getOperator());
            checkAuth(playerGetAudioChannelReq);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            headerMap.put(VideoConstants.XPLUGID,playerGetAudioChannelReq.getPlugId());
            res = videoPlayerService.getAudioChannel(playerGetAudioChannelReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.GET_AUDIO_CHANNEL_ERRO));
            log.error(VideoConstants.GET_AUDIO_CHANNEL_ERRO , e);
        }
        return res;
    }


    @ApiOperation(value = "获取通道实时流URL地址",notes = "获取通道实时流URL地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "playerGetVideoRealtimeUrlReq",value = "获取通道实时流URL地址请求实体",dataType = "PlayerGetVideoRealtimeUrlReq",required = true,paramType = "query")
    })
    @PostMapping("/video/player/sdk/getVideoRealtimeUrl")
    public Result<PlayerGetVideoRealtimeUrlData> getVideoRealtimeUrl(@RequestBody PlayerGetVideoRealtimeUrlReq playerGetVideoRealtimeUrlReq) {
        Result<PlayerGetVideoRealtimeUrlData> res;
        log.info(VideoConstants.GET_VIDEO_REAL_TIME_URL_OP + VideoConstants.SDKREQ + playerGetVideoRealtimeUrlReq);
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.GET_VIDEO_REAL_TIME_URL_OP + SecurityUtils.getOperator());
            checkAuth(playerGetVideoRealtimeUrlReq);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            headerMap.put("X-StreamUuid",playerGetVideoRealtimeUrlReq.getStreamUuid());
            headerMap.put(VideoConstants.XPLUGID,playerGetVideoRealtimeUrlReq.getPlugId());
            res = videoPlayerService.getVideoRealtimeUrl(playerGetVideoRealtimeUrlReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.GET_VIDEO_REAL_TIME_URL_ERRO));
            log.error(VideoConstants.GET_VIDEO_REAL_TIME_URL_ERRO , e);
        }
        return res;
    }

    @ApiOperation(value = "查询通道的录像列表",notes = "查询通道的录像列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoPlayerQueryVideoListReq",value = "查询通道的录像列表请求实体",dataType = "VideoPlayerQueryVideoListReq",required = true,paramType = "query")
    })
    @PostMapping("/video/player/sdk/queryVideoList")
    public Result<QueryVideoData> queryVideoList(@RequestBody VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq) {
        Result<QueryVideoData> res;
        log.info(VideoConstants.QUERY_VIDEO_LIST_OP + VideoConstants.SDKREQ + videoPlayerQueryVideoListReq);
        //首先判断传值问题，为空判断在实体中完成，此处只需判断传值的正确与否
        /*if (videoPlayerQueryVideoListReq.getStorageType() != 0 && videoPlayerQueryVideoListReq.getStorageType() != 1) {
            return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.STORAGE_TYPE_ERRO);
        }*/
        if(videoPlayerQueryVideoListReq.getRecordType() != 0 && videoPlayerQueryVideoListReq.getRecordType() != 1 && videoPlayerQueryVideoListReq.getRecordType() != 2) {
            return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.RECORD_TYPE_ERRO);
        }
        try{
            Map<String, String> headerMap = new HashMap<>();
            // 获取当前用户信息
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_VIDEO_LIST_OP + SecurityUtils.getOperator());
            checkAuth(videoPlayerQueryVideoListReq);
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            videoPlayerQueryVideoListReq.setTenantId(currentUser.getUser().getTenantId());
            videoPlayerQueryVideoListReq.setIndustryCode(currentUser.getUser().getIndustryCode());
            res = videoPlayerService.queryVideoList(videoPlayerQueryVideoListReq,headerMap);
        } catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_VIDEO_LIST_ERRO));
            log.error(VideoConstants.QUERY_VIDEO_LIST_ERRO , e);
        }
        log.info(VideoConstants.QUERY_VIDEO_LIST_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(videoPlayerQueryVideoListReq) + JSONObject.toJSONString(res));
        return res;
    }
    @ApiOperation(value = "获取通道的录像回放流URL地址",notes = "获取通道的录像回放流URL地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "playerGetVideoPlayBackUrlReq",value = "获取通道的录像回放流URL地址请求实体",dataType = "PlayerGetVideoPlayBackUrlReq",required = true,paramType = "query")
    })
    @PostMapping("/video/player/sdk/getVideoPlaybackUrl")
    public Result<GetVideoPlaybackUrlData> getVideoPlaybackUrl(@RequestBody PlayerGetVideoPlayBackUrlReq playerGetVideoPlayBackUrlReq) {
        Result<GetVideoPlaybackUrlData> res;
        log.info(VideoConstants.GET_VIDEO_PLAY_BACK_URL_OP + VideoConstants.SDKREQ + playerGetVideoPlayBackUrlReq);
        //校验插件id 在特殊情况下不能为空
        if (6 == playerGetVideoPlayBackUrlReq.getProtocolType() && StringUtils.isEmpty(playerGetVideoPlayBackUrlReq.getPlugId())) {
            return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.NOT_EMPTY_PLUGIN_ID);
        }
        try{
            //获取当前的登陆人
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.GET_VIDEO_PLAY_BACK_URL_OP + SecurityUtils.getOperator());
            checkAuth(playerGetVideoPlayBackUrlReq);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            headerMap.put("X-StreamUuid",playerGetVideoPlayBackUrlReq.getStreamUuid());
            headerMap.put(VideoConstants.XPLUGID,playerGetVideoPlayBackUrlReq.getPlugId());
            headerMap.put(VideoConstants.XFRONTSTREAMUUID,playerGetVideoPlayBackUrlReq.getFrontStreamUuid());
            res = videoPlayerService.getVideoPlaybackUrl(playerGetVideoPlayBackUrlReq,headerMap);
        }catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.GET_VIDEO_PLAY_BACK_URL_ERRO));
            log.error(VideoConstants.GET_VIDEO_PLAY_BACK_URL_ERRO , e);
        }
        return res;
    }
    private void checkAuth(VideoBaseReq videoBaseReq){
        Map<String,String> deviceOrderInfo = videoCommonService.checkDeviceAuth(videoBaseReq);
        if(deviceOrderInfo == null){
            // 返回错误 无权限使用
            log.info(VideoConstants.GET_VIDEO_PLAY_BACK_URL_OP + VideoConstants.RESPONSE_LOG + VideoConstants.NO_AUTH);
            throw new RuntimeException("鉴权失败");
        }else{
            videoBaseReq.setOrderCode(deviceOrderInfo.get(VideoConstants.ORDERCODE));
        }
    }

}
