package com.chinatower.video.api;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


/**
 * @ClassName: VideoPlayerCustomAPI
 * @Description: 视频播放相关接口
 * @Author: zhujun
 * @Date: 2022/1/11
 **/
public interface VideoPlayerCustomAPI {

    /**
     * 4.10.查询通道的录像列表
     * @param videoPlayerQueryVideoListReq
     * @return
     */
    @Deprecated
    @PostMapping("/video/player/queryVideoList")
    Result<QueryVideoData> queryVideoList(@RequestBody VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq);
    /**
     * 4.11.获取通道的录像回放流URL地址
     * @param playerGetVideoPlayBackUrlReq
     * @return
     */
    @Deprecated
    @PostMapping("/video/player/getVideoPlaybackUrl")
    Result<GetVideoPlaybackUrlData> getVideoPlaybackUrl(@RequestBody PlayerGetVideoPlayBackUrlReq playerGetVideoPlayBackUrlReq);
    /**
     * 4.12.获取通道实时流URL地址
     * @param playerGetVideoRealtimeUrlReq
     * @return
     */
    @Deprecated
    @PostMapping("/video/player/getVideoRealtimeUrl")
    Result<PlayerGetVideoRealtimeUrlData> getVideoRealtimeUrl(@RequestBody PlayerGetVideoRealtimeUrlReq playerGetVideoRealtimeUrlReq);
    /**
     * 4.51.3D定位
     * @param playerThirdLocationReq
     * @return
     */
    @PostMapping("/video/player/thirdLocation")
    Result thirdLocation(@RequestBody PlayerThirdLocationReq playerThirdLocationReq);
    /**
     * 4.52.获取语音对讲通道
     * @param playerGetAudioChannelReq
     * @return
     */
    @Deprecated
    @PostMapping("/video/player/getAudioChannel")
    Result<GetAudioChannelData> getAudioChannel(@RequestBody PlayerGetAudioChannelReq playerGetAudioChannelReq);
    /**
     * 4.56.抓图
     * @param playerSnapShotReq
     * @return
     */
    @PostMapping("/video/player/snapShot")
    Result snapShot(@RequestBody PlayerSnapShotReq playerSnapShotReq);
    /**
     * 4.60.倍速回放
     * @param playerPlayBackControlReq
     * @return
     */
    @Deprecated
    @PostMapping("/video/player/playBackControl")
    Result playBackControl(@RequestBody PlayerPlayBackControlReq playerPlayBackControlReq);

    /**
     * 功能说明: 视频管理-简约模式，批量查询抓图
     * @param: 通道集合
     * @return: 抓图集合
     * @author: hued
     * @date: 2022年10月10日16:29:42
     * @requestID  R-ZBZB-202210080955525 视频管理-实时视频增加简约模式
     */
    @PostMapping("/video/player/getSnapShotSimple")
    Result<List<SnapShotSimpleRes>> getSnapShotSimple(@RequestBody List<SnapShotSimpleRes> snapShotSimpleReqList);


    /**
     * 功能说明: 视频管理-批量下载
     * @param: 批量List
     * @return: list
     * @author: yy
     * @date: 2024.01.17
     */
    @Deprecated
    @PostMapping("/video/player/downloadVideo")
    Result<List<VideoDownloadDTO>> downloadVideo(@RequestBody List<VideoDownloadVO> list);


    /**
     * 功能说明: 视频管理-批量停止下载
     * @param: 批量List
     * @return: list
     * @author: yy
     * @date: 2024.01.17
     */
    @Deprecated
    @PostMapping("/video/player/stopDownloadVideo")
    Result<List<VideoDownloadDTO>> stopDownloadVideo(@RequestBody List<VideoDownloadVO> list);


    /**
     * 功能说明: 视频管理-录像下载进度百分比查询
     * @param: 批量List batchId
     * @return: list
     * @author: yy
     * @date: 2024.01.17
     */
    @Deprecated
    @PostMapping("/video/player/downloadProcess")
    Result<Map<String, DownloadProcessDTO>> downloadProcess(@RequestBody List<VideoDownloadVO> list);

    /**
     * 功能说明: app一键告警  开始抓图和下载实时录像
     * @param: 批量List
     * @return: list
     * @author: yy
     * @date: 2024.01.17
     */
    @Deprecated
    @PostMapping("/video/player/snapAndDownloadVideo")
    Result<Map<String, Object>> snapAndDownloadVideo(@RequestBody List<VideoDownloadVO> list);

    /**
     * 功能说明: app一键告警  查询抓图返回和录像下载进度百分百
     * @param: 批量List
     * @return: list
     * @author: yy
     * @date: 2024.01.17
     */
    @Deprecated
    @PostMapping("/video/player/snapAndDownloadVideo/process")
    Result<Map<String, Object>> snapAndDownloadVideoProcess(@RequestBody List<VideoDownloadVO> list);

}