package com.chinatower.stdu.query.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.stdu.common.entity.ResultEntity;
import com.chinatower.stdu.common.service.VideoCommonService;
import com.chinatower.stdu.common.util.VideoCommonUtil;
import com.chinatower.stdu.player.service.VideoPlayerService;
import com.chinatower.stdu.query.service.VideoQueryService;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.entity.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    VideoPlayerService videoPlayerService;
    @Autowired
    VideoCommonService videoCommonService;
    @Autowired
    private SystemFeignClient systemFeignClient;




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
    @PostMapping("/devices/{deviceCode}/channels/{channelCode}/queryVideoList")
    public ResultEntity<QueryVideoData> queryVideoList(@PathVariable("deviceCode") String deviceCode, @PathVariable("channelCode") String channelCode, HttpServletRequest request, @RequestBody VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq) {
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

            // 1-中心存储
            res = videoPlayerService.queryVideoList(videoPlayerQueryVideoListReq,headerMap);

        } catch (Exception e){
            res = Result.fail(VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(e.getMessage(), VideoConstants.QUERY_VIDEO_LIST_ERRO));
            log.error(VideoConstants.QUERY_VIDEO_LIST_ERRO , e);
        }
        log.info(VideoConstants.QUERY_VIDEO_LIST_OP + VideoConstants.RESPONSE_LOG + VideoCommonUtil.getLogData(videoPlayerQueryVideoListReq) + JSONObject.toJSONString(res));
        return toResultEntity(res);
    }


    /**
     * 6.49 批量获取通道实时流URL地址
     * @param list
     * @param request
     * @return
     */
    @PostMapping("/devices/channels/getVideoRealtimeUrls")
    public ResultEntity getVideoRealtimeUrls(@RequestBody List<Map> list, HttpServletRequest request){
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
    @ApiOperation(value = "获取通道加密视频流API接口")
    @PostMapping("/devices/{deviceCode}/channels/{channelCode}/getVideoEncryptRealtimeUrl")
    public Result<PlayerGetVideoRealtimeUrlData> getVideoEncryptRealtimeUrl(@PathVariable String deviceCode, @PathVariable String channelCode, @RequestBody PlayerGetVideoRealtimeUrlReq playerGetVideoRealtimeUrlReq, HttpServletRequest request) {
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
    @ApiOperation(value = "批量查询实时流地址")
    @PostMapping("/devices/channels/getVideoRealtimeUrlsForLookHere")
    public Result getVideoRealtimeUrlsForLookHere(@RequestBody List<Map> list){
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
