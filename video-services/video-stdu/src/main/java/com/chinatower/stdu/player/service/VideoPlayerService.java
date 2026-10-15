package com.chinatower.stdu.player.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.utils.*;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.api.IVideoDeviceService;
import com.chinatower.event.api.feign.client.ExportFeignClient;
import com.chinatower.event.entity.dto.DownloadVideoByUrlReq;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.file.feign.client.FileFeignClient;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.mongodb.request.UpdateEntity;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceVideoDTO;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.stdu.common.entity.ResultEntity;
import com.chinatower.stdu.common.service.IRecordDownloadService;
import com.chinatower.stdu.common.service.VideoCommonService;
import com.chinatower.stdu.common.util.BASE64DecodedMultipartFile;
import com.chinatower.stdu.common.util.VideoCommonUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import com.chinatower.video.api.ICasCadeInfoService;
import com.chinatower.video.api.VideoPlayerAPI;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.chinatower.video.api.entity.*;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 视频播放服务类
 * Created by ldl on 2022/1/14.
 */
@Service
@Slf4j
public class VideoPlayerService {

    @Autowired
    private VideoCommonService videoCommonService;
    @Autowired
    private IVideoDeviceService iVideoDeviceService;
    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    private FileFeignClient fileFeignClient;
    @Autowired
    private FileUrlEncryptUtil fileUrlEncryptUtil;
    @Autowired
    private NetConnectUtils connectUtils;
    @Autowired
    private VideoPlayerAPI videoPlayerAPI;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private IRecordDownloadService iRecordDownloadService;
    @Autowired
    private ICasCadeInfoService iCasCadeInfoService;

    private final String SUCCESS_CODE = "0000";

    private final String CENTER_NODE = "10000000001";

    @Resource
    private ExportFeignClient exportFeignClient;

    //倍速回放playBackControl
    public Result playBackControl(PlayerPlayBackControlReq playerPlayBackControlReq,Map<String, String> headerMap) {
        Result res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.PLAY_BACK_CONTROL_ERRO);
        try{
            NetConnectUtils.RequestParamter<PlayerPlayBackControlReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(playerPlayBackControlReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.PLAY_BACK_CONTROL);
            // 接口路径参数
            path.setDeviceCode(playerPlayBackControlReq.getDeviceCode());
            path.setChannelCode(playerPlayBackControlReq.getChannelCode());
            paramter.setPath(path);
            //属地化数据统一改造 add by liuxt 20240116
            Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceCode(playerPlayBackControlReq.getDeviceCode());
            log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
            CasCadeVO casCadeVO = new CasCadeVO();
            if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
                casCadeVO = casCadeVOResult.getData();
            }
            // 调用统一网关接口
            log.info(VideoConstants.PLAY_BACK_CONTROL_OP + VideoConstants.RESPONSE_INTERFACE_LOG + paramter);
            PlayerPlayBackControlReq.PlayerPlayBackControlReqA cr = connectUtils.post(paramter, PlayerPlayBackControlReq.PlayerPlayBackControlReqA.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_OrderCode.of(headerMap.get(VideoConstants.XORDERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.GET_VIDEO_REAL_TIME_URL_OP + VideoConstants.RESPONSE_LOG + cr);
            // 倍速回放成功
            if (VideoCommonUtil.crIsSuccess(cr)) {
                res = Result.success(VideoConstants.SUCCESS_MSG);
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.PLAY_BACK_CONTROL_ERRO));
            }
        } catch (Exception e) {
            log.error(VideoConstants.PLAY_BACK_CONTROL_ERRO + VideoCommonUtil.getLogData(playerPlayBackControlReq),e);
        }
        return res;
    }

    //获取语音对讲通道getAudioChannel
    public Result<GetAudioChannelData> getAudioChannel(PlayerGetAudioChannelReq playerGetAudioChannelReq,Map<String, String> headerMap) {
        Result<GetAudioChannelData> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.GET_AUDIO_CHANNEL_ERRO);
        try{
            NetConnectUtils.RequestParamter<PlayerGetAudioChannelReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(playerGetAudioChannelReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_AUDIO_CHANNEL);
            // 接口路径参数
            path.setDeviceCode(playerGetAudioChannelReq.getDeviceCode());
            path.setChannelCode(playerGetAudioChannelReq.getChannelCode());
            paramter.setPath(path);
            //属地化数据统一改造 add by liuxt 20240116
            Integer deviceType = StringUtils.isEmpty(playerGetAudioChannelReq.getDeviceType()) ? com.chinatower.common.core.constant.Constants.DEVICE_TYPE_CAMERA : Integer.valueOf(playerGetAudioChannelReq.getDeviceType());
            Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceInfo(playerGetAudioChannelReq.getDeviceCode(), deviceType);
            log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
            CasCadeVO casCadeVO = new CasCadeVO();
            if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
                casCadeVO = casCadeVOResult.getData();
            }
            // 调用统一网关接口
            log.info(VideoConstants.GET_AUDIO_CHANNEL_OP + VideoConstants.REQUEST_LOG + paramter);
            GetAudioChannelData.PGetAudioChannelDataEntity cr = connectUtils.post(paramter, GetAudioChannelData.PGetAudioChannelDataEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_PlugId.of(headerMap.get(VideoConstants.XPLUGID)),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.GET_AUDIO_CHANNEL_OP + VideoConstants.RESPONSE_LOG + cr);

            // 获取语音对讲通道成功
            if (VideoCommonUtil.crIsSuccess(cr)) {
                res = Result.data(cr.getData());
                this.operatorCount(VideoConstants.OPERATOR_TYPE,playerGetAudioChannelReq.getDeviceCode());
            } else {
            	String crJson = JSON.toJSONString(cr);
            	ResultEntity crResultEntity = JSON.parseObject(crJson, ResultEntity.class);
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(crResultEntity, VideoConstants.GET_AUDIO_CHANNEL_ERRO));
            }
        } catch (Exception e) {
            log.error(VideoConstants.GET_AUDIO_CHANNEL_ERRO + VideoCommonUtil.getLogData(playerGetAudioChannelReq),e);
        }
        return res;
    }


    //获取通道实时流URL地址getVideoRealtimeUrl
    public Result<PlayerGetVideoRealtimeUrlData> getVideoRealtimeUrl(PlayerGetVideoRealtimeUrlReq playerGetVideoRealtimeUrlReq,Map<String, String> headerMap) {
        if (6 == playerGetVideoRealtimeUrlReq.getProtocolType() && StringUtils.isEmpty(playerGetVideoRealtimeUrlReq.getPlugId())) {
            return Result.fail(VideoConstants.ERROR_PRE + VideoConstants.NOT_EMPTY_PLUGIN_ID);
        }
        Result<PlayerGetVideoRealtimeUrlData> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.GET_VIDEO_REAL_TIME_URL_ERRO);
        //码流类型  1-主码流；2-子码流
        boolean streamFlag = false;
        // 业务参数
        // 未指定主子码流，则优先使用子码流获取，成功可切换主子码流，失败则再使用主码流获取，并置不可切换主子码流
        if (playerGetVideoRealtimeUrlReq.getStreamType() != 1 && playerGetVideoRealtimeUrlReq.getStreamType() != 2) {
            playerGetVideoRealtimeUrlReq.setStreamType(2);
            streamFlag = true;
        }

        // 网域类型字段
        try {
            if (StringUtils.isEmpty(playerGetVideoRealtimeUrlReq.getNetType())) {
                AjaxResult ajaxResult = systemFeignClient.getConfValue(VideoConstants.NETTYPE_PARAM);
                if (VideoConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                    SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                    if(!ObjectUtils.isEmpty(sysConfig)){
                        playerGetVideoRealtimeUrlReq.setNetType(sysConfig.getConfigValue());
                    }else {
                        return Result.fail(VideoConstants.NETTYPE_PARAM_NOT_CONFIG);
                    }
                }else {
                    return Result.fail(VideoConstants.NETTYPE_PARAM_NOT_CONFIG);
                }
            }
        } catch (NumberFormatException e) {
            return Result.fail(VideoConstants.NETTYPE_PARAM_ERROR);
        }

        try{
            NetConnectUtils.RequestParamter<PlayerGetVideoRealtimeUrlReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(playerGetVideoRealtimeUrlReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_VIDEO_REALTIME_URL);
            // 接口路径参数
            path.setDeviceCode(playerGetVideoRealtimeUrlReq.getDeviceCode());
            path.setChannelCode(playerGetVideoRealtimeUrlReq.getChannelCode());
            paramter.setPath(path);

            Integer deviceType = StringUtils.isEmpty(playerGetVideoRealtimeUrlReq.getDeviceType()) ? com.chinatower.common.core.constant.Constants.DEVICE_TYPE_CAMERA : Integer.valueOf(playerGetVideoRealtimeUrlReq.getDeviceType());
            //属地化数据统一改造 add by liuxt 20240116
            Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceInfo(playerGetVideoRealtimeUrlReq.getDeviceCode(), deviceType);
            log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
            CasCadeVO casCadeVO = new CasCadeVO();
            if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
                casCadeVO = casCadeVOResult.getData();
            }
            //CasCadeVO casCadeVO = iCasCadeQueryService.getCasCadeByDeviceCode(playerGetVideoRealtimeUrlReq.getDeviceCode());
            // 调用统一网关接口
            log.info(VideoConstants.GET_VIDEO_REAL_TIME_URL_OP + VideoConstants.REQUEST_LOG + paramter);
            PlayerGetVideoRealtimeUrlData.PlayerGetVideoRealtimeUrlDataA cr = connectUtils.post(paramter, PlayerGetVideoRealtimeUrlData.PlayerGetVideoRealtimeUrlDataA.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_StreamUuid.of(headerMap.get(VideoConstants.X_StreamUuid)),
                        NetConnectUtils.HeaderKeyEnum.X_PlugId.of(headerMap.get(VideoConstants.XPLUGID)),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.GET_VIDEO_REAL_TIME_URL_OP + VideoConstants.RESPONSE_LOG + cr);

            // 获取通道实时流URL地址成功
            if (VideoCommonUtil.crIsSuccess(cr)) {
                // 获取通道实时流URL地址成功
                PlayerGetVideoRealtimeUrlData resObj = cr.getData();
                resObj.setStreamType(playerGetVideoRealtimeUrlReq.getStreamType());
                resObj.setStreamSwitchFlag(1);
                res = Result.data(resObj);
                this.operatorCount(VideoConstants.PLAY_TYPE,playerGetVideoRealtimeUrlReq.getDeviceCode());
            } else if (streamFlag) {
                // 调用统一网关接口
                log.info(VideoConstants.GET_VIDEO_REAL_TIME_URL_OP + VideoConstants.REQUEST_LOG + paramter);
                cr = connectUtils.post(paramter,PlayerGetVideoRealtimeUrlData.PlayerGetVideoRealtimeUrlDataA.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_StreamUuid.of(headerMap.get(VideoConstants.X_StreamUuid)),
                        NetConnectUtils.HeaderKeyEnum.X_PlugId.of(headerMap.get(VideoConstants.XPLUGID)),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info(VideoConstants.GET_VIDEO_REAL_TIME_URL_OP + VideoConstants.RESPONSE_LOG + cr);
                if (VideoCommonUtil.crIsSuccess(cr)) {
                    // 获取通道实时流URL地址成功
                    PlayerGetVideoRealtimeUrlData resObj = cr.getData();
                    resObj.setStreamType(playerGetVideoRealtimeUrlReq.getStreamType());
                    resObj.setStreamSwitchFlag(2);
                    res = Result.data(resObj);
                } else {
                    res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.GET_VIDEO_REAL_TIME_URL_ERRO));
                }
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.GET_VIDEO_REAL_TIME_URL_ERRO));
            }
        } catch (Exception e) {
            log.error(VideoConstants.GET_VIDEO_REAL_TIME_URL_ERRO + VideoCommonUtil.getLogData(playerGetVideoRealtimeUrlReq) , e);
        }
        return res;
    }


    //查询通道录像列表queryVideoList
    public Result<QueryVideoData> queryVideoList(VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq,Map<String, String> headerMap) {
        Result<QueryVideoData> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.QUERY_VIDEO_LIST_ERRO);
        try{
            List<Map<String, String>> timsList = new ArrayList<>();
            // 查询条件开始时间
            String startTime =  videoPlayerQueryVideoListReq.getStartTime().substring(0,10);
            String startTimeHms =  videoPlayerQueryVideoListReq.getStartTime().substring(10,19);
            // 查询条件结束时间
            String endTime = videoPlayerQueryVideoListReq.getEndTime().substring(0,10);
            String endTimeHms = videoPlayerQueryVideoListReq.getEndTime().substring(10,19);
            LocalDate startTimeLocalDate = LocalDate.parse(startTime, DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
            LocalDate endTimeLocalDate = LocalDate.parse(endTime, DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
            List datesList = this.getDatesBetween(startTimeLocalDate, endTimeLocalDate);
            // 获取录像回放起止日期和录像计划起止日期的交集
            if( datesList == null || datesList.size() == 0 ) {
            	return Result.data(new QueryVideoData());
            }
            // 交集的开始日期
            String startTimeNew = datesList.get(0).toString();
            if( startTimeNew.equals(startTime) )
            {
            	// 如果交集的开始日期等于查询条件的开始日期，则取查询条件开始日期的时分秒
            	startTimeNew += startTimeHms;
            }
            else
            {
            	startTimeNew += " 00:00:00";
            }
            // 交集的结束日期
            String endTimeNew = datesList.get(datesList.size() - 1).toString();
            if( endTimeNew.equals(endTime) )
            {
            	// 如果交集的结束日期等于查询条件的结束日期，则取查询条件结束日期的时分秒
            	endTimeNew += endTimeHms;
            }
            else
            {
            	endTimeNew += " 23:59:59";
            }
            videoPlayerQueryVideoListReq.setStartTime(startTimeNew);
            videoPlayerQueryVideoListReq.setEndTime(endTimeNew);
            int pageSize = videoPlayerQueryVideoListReq.getPageSize();
            videoPlayerQueryVideoListReq.setPageSize(86400);
            int currentPage = videoPlayerQueryVideoListReq.getCurrentPage();
            videoPlayerQueryVideoListReq.setCurrentPage(1);
            for(int i=0; i<datesList.size(); i++) {   //获取某一天
            String dateTmp = datesList.get(i).toString();
                Map<String, String> timeMap = new HashMap<>();
                timeMap.put("startTime", dateTmp +" " + "00:00:00");
                timeMap.put("endTime", dateTmp + " " + "23:59:59");
                timsList.add(timeMap);
            }
            if( timsList != null && timsList.size() > 0 ) {
	            videoPlayerQueryVideoListReq.setTimes(timsList);
                NetConnectUtils.RequestParamter<VideoPlayerQueryVideoListReq> paramter = new NetConnectUtils.RequestParamter<>();
                // 请求参数
                paramter.setBody(videoPlayerQueryVideoListReq);
                NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
                // 获取接口地址
                path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.QUERY_VIDEO_LIST);
                // 接口路径参数
                path.setDeviceCode(videoPlayerQueryVideoListReq.getDeviceCode());
                path.setChannelCode(videoPlayerQueryVideoListReq.getChannelCode());
                paramter.setPath(path);

                //属地化数据统一改造 add by liuxt 20240116
                Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceCode(videoPlayerQueryVideoListReq.getDeviceCode());
                log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
                CasCadeVO casCadeVO = new CasCadeVO();
                if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
                    casCadeVO = casCadeVOResult.getData();
                }
                // 调用统一网关接口
                log.info(VideoConstants.QUERY_VIDEO_LIST_OP + VideoConstants.REQUEST_LOG + paramter);
                QueryVideoData.QueryVideoDataEntity cr = connectUtils.post(paramter, QueryVideoData.QueryVideoDataEntity.class,
                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
                log.info(VideoConstants.QUERY_VIDEO_LIST_OP + VideoConstants.RESPONSE_LOG + cr);

	            // 查询通道录像列表成功
	            if (VideoCommonUtil.crIsSuccess(cr)) {
	            	QueryVideoData queryVideoDataNew = new QueryVideoData();
	            	QueryVideoData queryVideoData = cr.getData();
	            	if( queryVideoData != null )
	            	{
	            		List<QueryVideoListData> videosList = queryVideoData.getVideos();
	            		if( videosList != null && videosList.size() > 0 )
	            		{
	            			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
	            			List<QueryVideoListData> videosListNew = new ArrayList<QueryVideoListData>();
	            			for( QueryVideoListData queryVideoListData : videosList )
	            			{
	            				Date startTimeDate1 = simpleDateFormat.parse(queryVideoListData.getStartTime());
	            				Date endTimeDate1 = simpleDateFormat.parse(queryVideoListData.getEndTime());
	            				for( Map<String, String> mapTemp : timsList )
	            				{
	            					Date startTimeDate2 = simpleDateFormat.parse(mapTemp.get(VideoConstants.START_TIME));
		            				Date endTimeDate2 = simpleDateFormat.parse(mapTemp.get(VideoConstants.END_TIME));
		            				if( (startTimeDate1.getTime() >= startTimeDate2.getTime()) && (endTimeDate1.getTime() <= endTimeDate2.getTime()) )
		            				{
		            					videosListNew.add(queryVideoListData);
		            					break;
		            				}
		            				else if( (startTimeDate1.getTime() <= endTimeDate2.getTime()) && (endTimeDate1.getTime() >= startTimeDate2.getTime()) )
		            				{
		            					// 如果两个时间段有交集，则把基础返回的时间段拆成两个时间段
		            					QueryVideoListData queryVideoListDataCopy = new QueryVideoListData();
		            					BeanUtils.copyProperties(queryVideoListData, queryVideoListDataCopy);
		            					// 录像的开始时间小于查询的开始时间,就取查询的开始时间
		            					if( startTimeDate1.getTime() < startTimeDate2.getTime() )
		                    			{
		            						queryVideoListDataCopy.setStartTime(mapTemp.get(VideoConstants.START_TIME));
		                    			}
		            					// 录像的结束时间大于查询的结束时间,就取查询的结束时间
		            					if( endTimeDate1.getTime() > endTimeDate2.getTime() )
		                    			{
		            						queryVideoListDataCopy.setEndTime(mapTemp.get(VideoConstants.END_TIME));
		                    			}
		            					videosListNew.add(queryVideoListDataCopy);
		            				}
	            				}
	            			}
	            			int totalPage = (videosListNew.size() + pageSize - 1) / pageSize;
	            			queryVideoDataNew.setTotalPage(totalPage);
	            			queryVideoDataNew.setTotalNum(videosListNew.size());
	            			List<QueryVideoListData> videosListNewPage =
	            					videosListNew.stream().skip((currentPage - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
	            			queryVideoDataNew.setVideos(videosListNewPage);
	            		}
	            	}
	                res = Result.data(queryVideoDataNew);
	            } else {
	            	String crJson = JSON.toJSONString(cr);
	            	ResultEntity crResultEntity = JSON.parseObject(crJson, ResultEntity.class);
	                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(crResultEntity, VideoConstants.QUERY_VIDEO_LIST_ERRO));
	            }
            }
            else {
            	res = Result.data(new QueryVideoData());
            }
        } catch (Exception e) {
        	log.error(VideoConstants.QUERY_VIDEO_LIST_ERRO, e);
        }
        return res;
    }


    //获取通道的录像回放流URL地址getVideoPlaybackUrl
    public Result<GetVideoPlaybackUrlData> getVideoPlaybackUrl(PlayerGetVideoPlayBackUrlReq playerGetVideoPlayBackUrlReq,Map<String, String> headerMap) {
        Result<GetVideoPlaybackUrlData> res = Result.fail(VideoConstants.ERROR_PRE + VideoConstants.GET_VIDEO_PLAY_BACK_URL_ERRO);

        // 网域类型字段
        try {
            if (StringUtils.isEmpty(playerGetVideoPlayBackUrlReq.getNetType())) {
                AjaxResult ajaxResult = systemFeignClient.getConfValue(VideoConstants.NETTYPE_PARAM);
                if (VideoConstants.STRING_200.equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                    SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                    if(!ObjectUtils.isEmpty(sysConfig)){
                        playerGetVideoPlayBackUrlReq.setNetType(sysConfig.getConfigValue());
                    }else {
                        return Result.fail(VideoConstants.NETTYPE_PARAM_NOT_CONFIG);
                    }
                }else {
                    return Result.fail(VideoConstants.NETTYPE_PARAM_NOT_CONFIG);
                }
            }
        } catch (NumberFormatException e) {
            return Result.fail(VideoConstants.NETTYPE_PARAM_ERROR);
        }

        try{
            NetConnectUtils.RequestParamter<PlayerGetVideoPlayBackUrlReq> paramter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            paramter.setBody(playerGetVideoPlayBackUrlReq);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.GET_VIDEO_PLAYBACK_URL);
            // 接口路径参数
            path.setDeviceCode(playerGetVideoPlayBackUrlReq.getDeviceCode());
            path.setChannelCode(playerGetVideoPlayBackUrlReq.getChannelCode());
            paramter.setPath(path);
            //属地化数据统一改造 add by liuxt 20240116
            Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceCode(playerGetVideoPlayBackUrlReq.getDeviceCode());
            log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
            CasCadeVO casCadeVO = new CasCadeVO();
            if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
                casCadeVO = casCadeVOResult.getData();
            }
            // 调用统一网关接口
            log.info(VideoConstants.GET_VIDEO_PLAY_BACK_URL_OP + VideoConstants.REQUEST_LOG + paramter);
            GetVideoPlaybackUrlData.GetVideoPlaybackUrlDataEntity cr = connectUtils.post(paramter, GetVideoPlaybackUrlData.GetVideoPlaybackUrlDataEntity.class,
                    NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(headerMap.get(VideoConstants.CUSTOMERCODE)),
                    NetConnectUtils.HeaderKeyEnum.X_StreamUuid.of(headerMap.get(VideoConstants.X_StreamUuid)),
                    NetConnectUtils.HeaderKeyEnum.X_PlugId.of(headerMap.get(VideoConstants.XPLUGID)),
                    NetConnectUtils.HeaderKeyEnum.X_FrontStreamUuid.of(headerMap.get(VideoConstants.XFRONTSTREAMUUID)),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeVO.getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(casCadeVO.getCascadeToCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(paramter)));
            log.info(VideoConstants.GET_VIDEO_PLAY_BACK_URL_OP + VideoConstants.RESPONSE_LOG + cr);

            // 获取通道的录像回放流URL地址接口成功
            if (VideoCommonUtil.crIsSuccess(cr)) {
                res = Result.data(cr.getData());
            } else {
                res = Result.fail(HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.GET_VIDEO_PLAY_BACK_URL_ERRO));
            }
        } catch (Exception e) {
            log.error(VideoConstants.GET_VIDEO_PLAY_BACK_URL_ERRO + VideoCommonUtil.getLogData(playerGetVideoPlayBackUrlReq),e);
        }
        return res;
    }

    public static List getDatesBetween(LocalDate startDate, LocalDate endDate) {
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate)+1;
        return IntStream.iterate(0, i -> i + 1) .limit(numOfDaysBetween) .mapToObj(i -> startDate.plusDays(i)) .collect(Collectors.toList());
    }


    public Result<List<VideoDownloadDTO>> downloadVideo(List<VideoDownloadVO> list) {
        List<VideoDownloadDTO> results = ThreadParallelUtil.run(list, videoDownload -> {
            VideoDownloadDTO msg = new VideoDownloadDTO();
            msg.setBatchId(videoDownload.getBatchId());
            msg.setDeviceCode(videoDownload.getDeviceCode());
            msg.setChannelCode(videoDownload.getChannelCode());
            try{
                NetConnectUtils.RequestParamter<VideoDownloadVO> parameter = videoDownloadSetParameter(videoDownload, NetConnectUtils.RequestParamter.URLMappingEnum.DOWNLOAD_VIDEO);
                if (StringUtils.isEmpty(videoDownload.getTenantId()) && StringUtils.isEmpty(videoDownload.getIndustryCode())) {
                    SysUser user = SecurityUtils.getLoginUser().getUser();
                    videoDownload.setTenantId(user.getTenantId());
                    videoDownload.setIndustryCode(user.getIndustryCode());
                    videoDownload.setUserName(user.getUserName());
                }

                // 调用统一网关接口
                log.info(VideoConstants.DOWNLOAD_VIDEO + VideoConstants.REQUEST_LOG + parameter);
                MoreToNodeResponse cr = connectUtils.post(parameter, MoreToNodeResponse.class,
//                        NetConnectUtils.HeaderKeyEnum.X_CustomerCode.of(videoDownload.getTenantId()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(videoDownload.getAcceptNode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(videoDownload.getAccessNode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(parameter)));
                log.info(VideoConstants.DOWNLOAD_VIDEO + VideoConstants.RESPONSE_LOG + cr);

                msg.setReturnCode(cr.getReturnCode());
                msg.setReturnMsg(cr.getReturnMsg());
                // 下载成功
                if (!VideoCommonUtil.crIsSuccess(cr)) {
                    log.info(VideoConstants.DOUBLE_BRACKET,HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.DOWNLOAD_ERROR));
                    msg.setReturnMsg(videoCommonService.getErrorMsg(cr, VideoConstants.DOWNLOAD_ERROR));
                }
            } catch (Exception e) {
                msg.setReturnMsg(e.getMessage());
                log.error(VideoConstants.DOWNLOAD_ERROR + JSON.toJSONString(list), e);
            }
            return msg;
        }, ThreadParallelUtil.ConfigFactory.DOWNLOAD_VIDEO);
        //下载开始成功的数据存表
        Map<String, VideoDownloadVO> batchIdDownloadMap = list.stream().collect(Collectors.toMap(VideoDownloadVO::getBatchId, b -> b));
        List<RecordDownloadDTO> addDownloadList = new ArrayList<>();
        Date nowDate = DateUtils.getNowDate();
        for (VideoDownloadDTO result : results) {
            VideoDownloadVO videoDownloadVO = batchIdDownloadMap.get(result.getBatchId());
            RecordDownloadDTO recordDownload = new RecordDownloadDTO();
            recordDownload.setId(IdUtils.randomUUID().replace("-", ""));
            recordDownload.setBatchId(videoDownloadVO.getBatchId());
            recordDownload.setDeviceCode(videoDownloadVO.getDeviceCode());
            recordDownload.setChannelCode(videoDownloadVO.getChannelCode());
            recordDownload.setStartTime(videoDownloadVO.getStartTime());
            recordDownload.setEndTime(videoDownloadVO.getEndTime());
            recordDownload.setDownloadStatus("0000".equals(result.getReturnCode()) ? 0 : 2);
            recordDownload.setCreator(videoDownloadVO.getUserName());
            recordDownload.setTenantId(videoDownloadVO.getTenantId());
            recordDownload.setIndustryCode(videoDownloadVO.getIndustryCode());
            recordDownload.setWarningOrderId(videoDownloadVO.getWarningOrderId());
            recordDownload.setTableType(videoDownloadVO.getTableType());
            recordDownload.setMemoryCycle(videoDownloadVO.getMemoryCycle());
            recordDownload.setOperateSourceType(videoDownloadVO.getOperateSourceType());
            recordDownload.setDeleteFlag("0");
            recordDownload.setCreateTime(nowDate);
            addDownloadList.add(recordDownload);
        }
        if (CollectionUtil.isNotEmpty(addDownloadList)) {
            iRecordDownloadService.batchSave(addDownloadList);
        }
        return Result.data(results);
    }


    public Result<List<VideoDownloadDTO>> stopDownloadVideo(List<VideoDownloadVO> list) {
        List<VideoDownloadDTO> results = ThreadParallelUtil.run(list, videoDownload -> {
            VideoDownloadDTO msg = new VideoDownloadDTO();
            msg.setBatchId(videoDownload.getBatchId());
            msg.setDeviceCode(videoDownload.getDeviceCode());
            msg.setChannelCode(videoDownload.getChannelCode());
            try{
                NetConnectUtils.RequestParamter<VideoDownloadVO> parameter = videoDownloadSetParameter(videoDownload, NetConnectUtils.RequestParamter.URLMappingEnum.STOP_DOWNLOAD_VIDEO);

                // 调用统一网关接口
                log.info(VideoConstants.STOP_DOWNLOAD_VIDEO + VideoConstants.REQUEST_LOG + parameter);
                MoreToNodeResponse cr = connectUtils.post(parameter, MoreToNodeResponse.class,
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(videoDownload.getAcceptNode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(videoDownload.getAccessNode()),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                        NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(parameter)));
                log.info(VideoConstants.STOP_DOWNLOAD_VIDEO + VideoConstants.RESPONSE_LOG + cr);

                msg.setReturnCode(cr.getReturnCode());
                msg.setReturnMsg(cr.getReturnMsg());
                // 下载成功
                if (!VideoCommonUtil.crIsSuccess(cr)) {
                    log.info(VideoConstants.DOUBLE_BRACKET,HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.STOP_DOWNLOAD_ERROR));
                }
            } catch (Exception e) {
                msg.setReturnMsg(e.getMessage());
                log.error(VideoConstants.STOP_DOWNLOAD_ERROR + JSON.toJSONString(list), e);
            }
            return msg;
        }, ThreadParallelUtil.ConfigFactory.DOWNLOAD_VIDEO);
        List<String> deleteList = new ArrayList<>();
        results.forEach(item -> {
            if (StringUtil.isNotEmpty(item.getReturnCode()) && SUCCESS_CODE.equals(item.getReturnCode())) {
                deleteList.add(item.getBatchId());
            }
        });
        if (!deleteList.isEmpty()) {
            iRecordDownloadService.batchDelete(deleteList);
        }
        return Result.data(results);
    }


    /**
     * 录像下载相关 设置请求南向接口的 parameter
     * @param videoDownload videoDownload
     * @return NetConnectUtils.RequestParamter
     */
    private NetConnectUtils.RequestParamter<VideoDownloadVO> videoDownloadSetParameter(VideoDownloadVO videoDownload, NetConnectUtils.RequestParamter.URLMappingEnum url) {
        Result<CasCadeVO> casCadeVOResult = iCasCadeInfoService.getCasCadeByDeviceCode(videoDownload.getDeviceCode());
        log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeVOResult);
        CasCadeVO casCadeVO = new CasCadeVO();
        if(casCadeVOResult.getCode() == VideoConstants.TWO_HUNDRED){
            casCadeVO = casCadeVOResult.getData();
        }
        videoDownload.setAcceptNode(casCadeVO.getPlatformCode());
        videoDownload.setAccessNode(casCadeVO.getCascadeToCode());
        NetConnectUtils.RequestParamter<VideoDownloadVO> parameter = new NetConnectUtils.RequestParamter<>();
        // 请求参数
        parameter.setBody(videoDownload);
        NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
        // 获取接口地址
        path.setUrl(url);
        // 接口路径参数
        path.setDeviceCode(videoDownload.getDeviceCode());
        path.setChannelCode(videoDownload.getChannelCode());
        parameter.setPath(path);
        return parameter;
    }

    public Result<Map<String, DownloadProcessDTO>> downloadProcess(List<VideoDownloadVO> list) {

        //入库存表
        List<RecordDownloadDTO> recordDownloadList = new ArrayList<>();

        List<String> deleteList = new ArrayList<>();
        //请求入参
        List<DownloadProcessReq> reqs = new ArrayList<>();

        //{batchId:{RecordDownloadDTO}}
        Map<String, DownloadProcessDTO> batchIdDownloadMap = new HashMap<>();

        Date nowDate = DateUtils.getNowDate();


        list.forEach(item -> {
            //请求入参
            reqs.add(new DownloadProcessReq(item.getBatchId()));
            //构建map
            DownloadProcessDTO recordDownload = new DownloadProcessDTO();
            recordDownload.setDeviceCode(item.getDeviceCode());
            recordDownload.setChannelCode(item.getChannelCode());
            recordDownload.setBatchId(item.getBatchId());
            recordDownload.setStartTime(item.getStartTime());
            recordDownload.setEndTime(item.getEndTime());
            batchIdDownloadMap.put(item.getBatchId(), recordDownload);
        });

        try{
            NetConnectUtils.RequestParamter<List<DownloadProcessReq>> parameter = new NetConnectUtils.RequestParamter<>();
            // 请求参数
            parameter.setBody(reqs);
            NetConnectUtils.RequestParamter.Path path = new NetConnectUtils.RequestParamter.Path();
            // 获取接口地址
            path.setUrl(NetConnectUtils.RequestParamter.URLMappingEnum.DOWNLOAD_PROCESS);
            // 接口路径参数
            parameter.setPath(path);
            // 调用统一网关接口
            log.info(VideoConstants.DOWNLOAD_PROCESS_OP + VideoConstants.REQUEST_LOG + parameter);

            List<String> deviceCodeList = list.stream().map(VideoDownloadVO::getDeviceCode).collect(Collectors.toList());
            Result<List<CasCadeVO>> casCadeByDeviceCode = iCasCadeInfoService.getCasCadeByDeviceCodeBatch(deviceCodeList);
            log.info(VideoConstants.DO_SELECT_FROM_TO_OP + VideoConstants.RESPONSE_LOG + casCadeByDeviceCode);
            List<CasCadeVO> casCadeByDeviceCodeBatch = new ArrayList<>();
            if(casCadeByDeviceCode.getCode() == VideoConstants.TWO_HUNDRED){
                casCadeByDeviceCodeBatch = casCadeByDeviceCode.getData();
            }
            String toCode = casCadeByDeviceCodeBatch.stream()
                    .filter(distinctByKey(CasCadeVO::getCascadeToCode))
                    .map(CasCadeVO::getCascadeToCode).collect(Collectors.joining(","));
            MoreToNodeResponse cr = connectUtils.post(parameter, MoreToNodeResponse.class,
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_From_Code.of(casCadeByDeviceCodeBatch.get(0).getPlatformCode()),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Code.of(toCode),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_App.of(VideoConstants.TO_APP_VIDEO),
                    NetConnectUtils.HeaderKeyEnum.X_Cascade_To_Path.of(connectUtils.getRealUrl(parameter)));
            log.info(VideoConstants.DOWNLOAD_PROCESS_OP + VideoConstants.RESPONSE_LOG + cr);

            // 下载成功
            if (VideoCommonUtil.crIsSuccess(cr)) {

                if (StringUtil.isNotEmpty(String.valueOf(cr.getData()))) {
                    JSONArray array = JSONArray.parseArray(cr.getData());

                    //增加node url
                    //0-私有化  1-云化 2-下沉节点
                    boolean correctLocationFlag = true;
                    AjaxResult ajaxResult = systemFeignClient.getConfValue("dependence_manage");
                    if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                        SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                        if (sysConfig.getConfigValue().equals(Constants.ZERO)){
                            correctLocationFlag = false;
                        }
                    }
                    log.info("correctLocationFlag=" + correctLocationFlag);
                    for (Object download : array) {
                        JSONObject obj = (JSONObject) download;
                        String batchId = (String) obj.get("batchId");
                        if (batchIdDownloadMap.containsKey(batchId)) {
                            DownloadProcessDTO downloadProcess = batchIdDownloadMap.get(batchId);
                            String process = (String) obj.get("process");
                            String processCode = (String) obj.get("processCode");
                            int fileSize = (int) obj.get("fileSize");
                            String fileName = (String) obj.get("fileName");
                            int order = (Integer) obj.get("order");
                            String downLoadUrl = (String) obj.get("downLoadUrl");
                            String accessNode = (String) obj.get("accessNode");
                            //设置返回
                            downloadProcess.setProcess(process);
                            downloadProcess.setProcessCode(processCode);
                            downloadProcess.setFileSize(fileSize);
                            downloadProcess.setDownloadUrl(downLoadUrl);
                            downloadProcess.setFileName(fileName);
                            downloadProcess.setOrder(order);
                            downloadProcess.setAccessNode(accessNode);

                            //上传图片
                            String addNodeUrl = AssembleUrlUtil.getAssembleUrl(accessNode, downLoadUrl, correctLocationFlag);
                            downloadProcess.setAddNodeUrl(addNodeUrl);

                            try {
                                if(Constants.ONE.equals(list.get(Constants.ZERO_NUM).getIsWaterMark()) && !StringUtils.isEmpty(downLoadUrl)){
                                    DownloadVideoByUrlReq req = new DownloadVideoByUrlReq();
                                    req.setUrl(downLoadUrl);
                                    log.info("录像文件加水印，入参，{}",JSONObject.toJSONString(req));
                                    Result<String> stringResult = exportFeignClient.downloadVideoByUrl(req);
                                    log.info("录像文件加水印，出参，{}",JSONObject.toJSONString(stringResult));
                                    if (stringResult != null && stringResult.getCode() == 200 && stringResult.getData() != null){
                                        downloadProcess.setDownloadUrl(stringResult.getData());
                                    }
                                }
                            }catch (Exception e){
                                log.error("录像文件加水印失败：", e);
                            }

                            if (!StringUtils.isEmpty(process) && "1000".equals(processCode)) {
                                if ("100%".equals(process)) {
                                    RecordDownloadDTO recordDownload = new RecordDownloadDTO();
                                    recordDownload.setBatchId(batchId);
                                    recordDownload.setDeviceCode(downloadProcess.getDeviceCode());
                                    recordDownload.setChannelCode(downloadProcess.getChannelCode());
                                    recordDownload.setStartTime(downloadProcess.getStartTime());
                                    recordDownload.setEndTime(downloadProcess.getEndTime());
                                    recordDownload.setDownloadStatus(1);
                                    recordDownload.setFileSize(fileSize);
                                    recordDownload.setFileName(fileName);
                                    recordDownload.setOrder(order);
                                    recordDownload.setLocalUrl(downLoadUrl);
                                    recordDownload.setUpdateTime(nowDate);
                                    recordDownload.setAccessNode(accessNode);
                                    recordDownloadList.add(recordDownload);
                                }
                            } else {
                                if (!"0".equals(processCode)) {
                                    //失败处理
                                    deleteList.add(batchId);
                                }
                            }
                        }
                    }
                }

            } else {
                log.info(VideoConstants.DOUBLE_BRACKET,HttpStatus.ERROR_4001, VideoConstants.ERROR_PRE + videoCommonService.getErrorMsg(cr, VideoConstants.DOWNLOAD_PROCESS_ERROR));

            }
        } catch (Exception e) {
            log.error(VideoConstants.DOWNLOAD_PROCESS_ERROR + JSON.toJSONString(list), e);
        }

        //有数据代表有完成，可保存
        if (CollectionUtil.isNotEmpty(recordDownloadList)) {
            //查询该条记录是否已经更新为完成
            Set<String> existBatchIds = new HashSet<>(iRecordDownloadService.existDownloadFinishByBatchIds(recordDownloadList));
            recordDownloadList.removeIf(record -> existBatchIds.contains(record.getBatchId()));

            if (CollectionUtil.isNotEmpty(recordDownloadList)) {
                iRecordDownloadService.batchUpdate(recordDownloadList);
            }
        }
        //修改下载状态为下载失败
        if (CollectionUtil.isNotEmpty(deleteList)) {
            iRecordDownloadService.batchUpdateDownLoadFail(deleteList);
        }

        return Result.data(batchIdDownloadMap);
    }


    public void operatorCount(String type,String deviceCode){
        DeviceVideoDTO deviceVideoDTO = new DeviceVideoDTO();
        if ("1".equals(type)){
            deviceVideoDTO.setVideoType(DeviceConstants.VIDEO_OPERATOR_NUM);
        }else {
            deviceVideoDTO.setVideoType(DeviceConstants.VIDEO_PLAY_NUM);
        }
        deviceVideoDTO.setDeviceCode(deviceCode);
        log.info(VideoConstants.OPERATORCOUNT + deviceVideoDTO.toString());
        iVideoDeviceService.updateDeviceUserNum(deviceVideoDTO);
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
