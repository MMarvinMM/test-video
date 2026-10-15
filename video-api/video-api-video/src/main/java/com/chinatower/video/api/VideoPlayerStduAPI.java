package com.chinatower.video.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.api.factory.VideoPlayerStduFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


@FeignClient(contextId = "VideoPlayerStduAPI",
        value = ServiceNameConstants.STDU_SERVICE,
        fallbackFactory = VideoPlayerStduFallbackFactory.class)
public interface VideoPlayerStduAPI {

    @PostMapping("/video/player/sdk/queryVideoList")
    Result<QueryVideoData> queryVideoList(@RequestBody VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq);

    @PostMapping("/video/player/sdk/getVideoPlaybackUrl")
    Result<GetVideoPlaybackUrlData> getVideoPlaybackUrl(@RequestBody PlayerGetVideoPlayBackUrlReq playerGetVideoPlayBackUrlReq);

    @PostMapping("/video/player/sdk/getVideoRealtimeUrl")
    Result<PlayerGetVideoRealtimeUrlData> getVideoRealtimeUrl(@RequestBody PlayerGetVideoRealtimeUrlReq playerGetVideoRealtimeUrlReq);

    @PostMapping("/video/player/sdk/getAudioChannel")
    Result<GetAudioChannelData> getAudioChannel(@RequestBody PlayerGetAudioChannelReq playerGetAudioChannelReq);


    @PostMapping("/video/player/sdk/playBackControl")
    Result playBackControl(@RequestBody PlayerPlayBackControlReq playerPlayBackControlReq);


    @PostMapping("/video/player/downloadVideo")
    Result<List<VideoDownloadDTO>> downloadVideo(@RequestBody List<VideoDownloadVO> list);


    @PostMapping("/video/player/stopDownloadVideo")
    Result<List<VideoDownloadDTO>> stopDownloadVideo(@RequestBody List<VideoDownloadVO> list);


    @PostMapping("/video/player/downloadProcess")
    Result<Map<String, DownloadProcessDTO>> downloadProcess(@RequestBody List<VideoDownloadVO> list);

}