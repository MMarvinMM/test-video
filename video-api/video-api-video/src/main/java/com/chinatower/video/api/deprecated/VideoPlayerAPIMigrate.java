package com.chinatower.video.api.deprecated;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.api.factory.VideoPlayerFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


/**
 * 不对外暴露此文件的sdk接口，改为对外暴露同名class
 * gitVersion:  1.6.90
 * date:    2024年8月12日
 */
@FeignClient(contextId = "VideoPlayerAPI",
        value = ServiceNameConstants.VIDEO_SERVICE,
        fallbackFactory = VideoPlayerFallbackFactory.class)
@Deprecated
public interface VideoPlayerAPIMigrate {

    @Deprecated
    @PostMapping("/video/player/sdk/queryVideoList")
    Result<QueryVideoData> queryVideoList(@RequestBody VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq);

    @Deprecated
    @PostMapping("/video/player/sdk/getVideoPlaybackUrl")
    Result<GetVideoPlaybackUrlData> getVideoPlaybackUrl(@RequestBody PlayerGetVideoPlayBackUrlReq playerGetVideoPlayBackUrlReq);


    @Deprecated
    @PostMapping("/video/player/sdk/getVideoRealtimeUrl")
    Result<PlayerGetVideoRealtimeUrlData> getVideoRealtimeUrl(@RequestBody PlayerGetVideoRealtimeUrlReq playerGetVideoRealtimeUrlReq);

    @PostMapping("/video/player/sdk/thirdLocation")
    Result thirdLocation(@RequestBody PlayerThirdLocationReq playerThirdLocationReq);

    @Deprecated
    @PostMapping("/video/player/sdk/getAudioChannel")
    Result<GetAudioChannelData> getAudioChannel(@RequestBody PlayerGetAudioChannelReq playerGetAudioChannelReq);

    @PostMapping("/video/player/sdk/snapShot")
    Result snapShot(@RequestBody PlayerSnapShotReq playerSnapShotReq);

    @Deprecated
    @PostMapping("/video/player/sdk/playBackControl")
    Result playBackControl(@RequestBody PlayerPlayBackControlReq playerPlayBackControlReq);


    @PostMapping("/video/player/queryRecordPlanStorageDay")
    Result<ChannelStorageTimeReq> queryRecordPlanStorageDay(@RequestBody ChannelStorageTimeReq channelStorageTimeReq);



    @Deprecated
    @PostMapping("/video/player/downloadVideo")
    Result<List<VideoDownloadDTO>> downloadVideo(@RequestBody List<VideoDownloadVO> list);


    @Deprecated
    @PostMapping("/video/player/stopDownloadVideo")
    Result<List<VideoDownloadDTO>> stopDownloadVideo(@RequestBody List<VideoDownloadVO> list);


    @Deprecated
    @PostMapping("/video/player/downloadProcess")
    Result<Map<String, DownloadProcessDTO>> downloadProcess(@RequestBody List<VideoDownloadVO> list);


    @PostMapping("/video/player/snapShot")
    Result snapShotByNode(@RequestBody PlayerSnapShotReq playerSnapShotReq);

}