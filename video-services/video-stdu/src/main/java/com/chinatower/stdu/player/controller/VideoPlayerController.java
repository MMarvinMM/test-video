package com.chinatower.stdu.player.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.stdu.common.service.VideoCommonService;
import com.chinatower.stdu.common.util.VideoCommonUtil;
import com.chinatower.stdu.player.service.VideoPlayerService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.VideoPlayerAPI;
import com.chinatower.video.api.entity.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 视频播放controller
 * Created by ldl on 2022/1/11.
 */
@Api(value = "视频播放接口-Api",tags = "视频播放请求处理")
@Slf4j
@RestController
public class VideoPlayerController {

    @Autowired
    VideoPlayerService videoPlayerService;

    @Autowired
    VideoCommonService videoCommonService;

    @Autowired
    private VideoPlayerAPI videoPlayerAPI;

    @Autowired
    private SystemFeignClient systemFeignClient;

    public final String REDIS_APP_ALARM_IMG_BATCH_ID_PRE = "app_alarm_img_batch_id_";


    @ApiOperation(value = "倍速回放",notes = "倍速回放")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "playerPlayBackControlReq",value = "倍速回放请求实体",dataType = "PlayerPlayBackControlReq",required = true,paramType = "query")
    })
    @PostMapping("/video/player/playBackControl")
    public Result playBackControl(@RequestBody PlayerPlayBackControlReq playerPlayBackControlReq) {
        Result res;
        log.info(VideoConstants.PLAY_BACK_CONTROL_OP + VideoConstants.CONTROLLERREQ + playerPlayBackControlReq);
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
    @PostMapping("/video/player/getAudioChannel")
    public Result<GetAudioChannelData> getAudioChannel(@RequestBody PlayerGetAudioChannelReq playerGetAudioChannelReq) {
        Result<GetAudioChannelData> res;
        log.info(VideoConstants.GET_AUDIO_CHANNEL_OP + VideoConstants.CONTROLLERREQ + playerGetAudioChannelReq);
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
    @PostMapping("/video/player/getVideoRealtimeUrl")
    public Result<PlayerGetVideoRealtimeUrlData> getVideoRealtimeUrl(@RequestBody PlayerGetVideoRealtimeUrlReq playerGetVideoRealtimeUrlReq) {
        Result<PlayerGetVideoRealtimeUrlData> res;
        log.info(VideoConstants.GET_VIDEO_REAL_TIME_URL_OP + VideoConstants.CONTROLLERREQ + JSONObject.toJSONString(playerGetVideoRealtimeUrlReq));
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.GET_VIDEO_REAL_TIME_URL_OP + SecurityUtils.getOperator());
            if(StringUtils.isEmpty(playerGetVideoRealtimeUrlReq.getDeviceCode()) || StringUtils.isEmpty(playerGetVideoRealtimeUrlReq.getChannelCode())){
                res = Result.fail("设备编码和通道编码不能为空");
                return res;
            }
            //此接口只校验订购，不需要校验用户权限，由上游页面控制
            checkNoAuth(playerGetVideoRealtimeUrlReq);

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            headerMap.put(VideoConstants.X_StreamUuid,playerGetVideoRealtimeUrlReq.getStreamUuid());
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
    @PostMapping("/video/player/queryVideoList")
    public Result<QueryVideoData> queryVideoList(@RequestBody VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq) {
        Result<QueryVideoData> res;
        log.info(VideoConstants.QUERY_VIDEO_LIST_OP + VideoConstants.CONTROLLERREQ + videoPlayerQueryVideoListReq);
        //首先判断传值问题，为空判断在实体中完成，此处只需判断传值的正确与否
      /*  if (videoPlayerQueryVideoListReq.getStorageType() != 0 && videoPlayerQueryVideoListReq.getStorageType() != 1) {
            return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.STORAGE_TYPE_ERRO);
        }*/
        if(videoPlayerQueryVideoListReq.getRecordType() != 0 && videoPlayerQueryVideoListReq.getRecordType() != 1 && videoPlayerQueryVideoListReq.getRecordType() != 2) {
            return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.RECORD_TYPE_ERRO);
        }
        try{
            // 获取当前用户信息
            LoginUser currentUser = SecurityUtils.getLoginUser();
            log.info(VideoConstants.QUERY_VIDEO_LIST_OP + SecurityUtils.getOperator());
            checkAuth(videoPlayerQueryVideoListReq);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put(VideoConstants.CUSTOMERCODE,currentUser.getUser().getTenantId());
            videoPlayerQueryVideoListReq.setTenantId(currentUser.getUser().getTenantId());
            videoPlayerQueryVideoListReq.setIndustryCode(currentUser.getUser().getIndustryCode());
            // 1-中心存储
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
    @PostMapping("/video/player/getVideoPlaybackUrl")
    public Result<GetVideoPlaybackUrlData> getVideoPlaybackUrl(@RequestBody PlayerGetVideoPlayBackUrlReq playerGetVideoPlayBackUrlReq) {
        Result<GetVideoPlaybackUrlData> res;
        log.info(VideoConstants.GET_VIDEO_PLAY_BACK_URL_OP + VideoConstants.CONTROLLERREQ + playerGetVideoPlayBackUrlReq);
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
            headerMap.put(VideoConstants.X_StreamUuid,playerGetVideoPlayBackUrlReq.getStreamUuid());
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

    private void checkNoAuth(VideoBaseReq videoBaseReq){
        Map<String,String> deviceOrderInfo = videoCommonService.checkDeviceNoAuth(videoBaseReq);
        if(deviceOrderInfo == null){
            // 返回错误 无权限使用
            log.info(VideoConstants.GET_VIDEO_PLAY_BACK_URL_OP + VideoConstants.RESPONSE_LOG + VideoConstants.NO_AUTH);
            throw new RuntimeException("鉴权失败");
        }else{
            videoBaseReq.setOrderCode(deviceOrderInfo.get(VideoConstants.ORDERCODE));
        }
    }



    /**
     * 功能说明: 视频管理-批量下载
     * @param: 批量List
     * @return: list
     * @author: yy
     * @date: 2024.01.17
     */
    @PostMapping("/video/player/downloadVideo")
    public Result<List<VideoDownloadDTO>> downloadVideo(@RequestBody List<VideoDownloadVO> list) {
        log.info(VideoConstants.DOWNLOAD_VIDEO + VideoConstants.CONTROLLERREQ + JSON.toJSONString(list));
        return videoPlayerService.downloadVideo(list);
    }

    /**
     * 功能说明: 视频管理-批量停止下载
     * @param: 批量List
     * @return: list
     * @author: yy
     * @date: 2024.01.17
     */
    @PostMapping("/video/player/stopDownloadVideo")
    public Result<List<VideoDownloadDTO>> stopDownloadVideo(@RequestBody List<VideoDownloadVO> list) {
        log.info(VideoConstants.STOP_DOWNLOAD_VIDEO + VideoConstants.CONTROLLERREQ + JSON.toJSONString(list));
        return videoPlayerService.stopDownloadVideo(list);
    }

    /**
     * 功能说明: 视频管理-录像下载进度百分比查询
     * @param: 批量List batchId
     * @return: list
     * @author: yy
     * @date: 2024.01.17
     */
    @PostMapping("/video/player/downloadProcess")
    public Result<Map<String, DownloadProcessDTO>> downloadProcess(@RequestBody List<VideoDownloadVO> list) {
        log.info(VideoConstants.DOWNLOAD_VIDEO_PROCESS + VideoConstants.CONTROLLERREQ + JSON.toJSONString(list));
        return videoPlayerService.downloadProcess(list);
    }


    /**
     * 功能说明: app一键告警  开始抓图和下载实时录像
     * @param: 批量List
     * @return: list
     * @author: yy
     * @date: 2024.01.17
     */
    @PostMapping("/video/player/snapAndDownloadVideo")
    public Result<Map<String, Object>> snapAndDownloadVideo(@RequestBody List<VideoDownloadVO> list) {
        log.info(VideoConstants.SNAP_AND_DOWNLOAD_VIDEO_START + VideoConstants.CONTROLLERREQ + JSON.toJSONString(list));

        Map<String, Object> res = new HashMap<>();
        Result<List<SysDictDataResponse>> dictResult = systemFeignClient.dictDataType("app_alarm_snap_shot_back_url");
        if (dictResult.getData() != null && !dictResult.getData().isEmpty()) {
            List<Map<String, Object>> snapResult = ThreadParallelUtil.run(list, item -> {
                Map<String, Object> map = new HashMap<>();
                map.put("batchId", item.getBatchId());

                //抓图
                PlayerSnapShotReq snapShotReq = new PlayerSnapShotReq();
                SecureRandom rd = new SecureRandom();
                int a = rd.nextInt(10);
                int b = rd.nextInt(10);
                /**
                 * 图片名称
                 *  有预置位：设备编码_通道编码_预置位id_2位序号；
                 *  无预置位：设备编码_通道编码_2位序号；
                 * */
                String imgName = item.getDeviceCode() + item.getChannelCode() + a + b;
                snapShotReq.setDeviceCode(item.getDeviceCode());
                snapShotReq.setChannelCode(item.getChannelCode());
                String appAlarmSnapCallBackUrl = dictResult.getData().get(0).getDictValue() + item.getBatchId();
                snapShotReq.setImageUpUrl(appAlarmSnapCallBackUrl);
                snapShotReq.setImageName(imgName);
                snapShotReq.setStreamType(1);
                log.info("4.56.抓图请求参数：" + JSON.toJSONString(snapShotReq));
                Result result = videoPlayerAPI.snapShotByNode(snapShotReq);
                map.put("data", result.getData());
                map.put("code", result.getCode());
                map.put("msg", result.getMsg());
                return map;
            }, ThreadParallelUtil.ConfigFactory.DOWNLOAD_VIDEO);

            if (CollectionUtils.isNotEmpty(snapResult)) {
                res.put("snap", snapResult);
            }
        } else {
            log.info("未配置app一键告警抓图回调");
        }

        //开始录像下载
        Result<List<VideoDownloadDTO>> listResult = downloadVideo(list);
        if (CollectionUtils.isNotEmpty(listResult.getData())) {
            res.put("video", listResult.getData());
        }
        return Result.data(res);
    }

    /**
     * 功能说明: app一键告警  查询抓图返回和录像下载进度百分百
     * @param: 批量List
     * @return: list
     * @author: yy
     * @date: 2024.01.17
     */
    @PostMapping("/video/player/snapAndDownloadVideo/process")
    public Result<Map<String, Object>> snapAndDownloadVideoProcess(@RequestBody List<VideoDownloadVO> list) {
        log.info(VideoConstants.SNAP_AND_DOWNLOAD_VIDEO_PROCESS + VideoConstants.CONTROLLERREQ + JSON.toJSONString(list));
        Map<String, Object> result = new HashMap<>();
        //查看抓图
        Map<String, String> snapResult = new HashMap<>();
        list.forEach(item -> {
            if (RedisUtil.hasKey(REDIS_APP_ALARM_IMG_BATCH_ID_PRE + item.getBatchId(), RedisDBType.PRIMARY)) {
                snapResult.put(item.getBatchId(), RedisUtil.get(REDIS_APP_ALARM_IMG_BATCH_ID_PRE + item.getBatchId(), RedisDBType.PRIMARY));
            }
        });
        //查看录像百分比
        Result<Map<String, DownloadProcessDTO>> videoResult = downloadProcess(list);
        if (!snapResult.isEmpty()) {
            result.put("snap", snapResult);
        }
        if (!videoResult.getData().isEmpty()) {
            result.put("video", videoResult.getData());
        }
        return Result.data(result);
    }

}
