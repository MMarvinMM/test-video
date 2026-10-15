package com.chinatower.video.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.VideoPlayerStduAPI;
import com.chinatower.video.api.entity.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 视频控制降级处理
 *
 * @Author: zhujun
 * @Date: 2022/1/16
 */
@Component
public class VideoPlayerStduFallbackFactory implements FallbackFactory<VideoPlayerStduAPI> {
    private static final Logger log = LoggerFactory.getLogger(VideoPlayerStduFallbackFactory.class);

    @Override
    public VideoPlayerStduAPI create(Throwable throwable) {
        log.error("调用stdu视频播放接口失败:{}", throwable.getMessage());
        return new VideoPlayerStduAPI() {
            @Override
            public Result<QueryVideoData> queryVideoList(VideoPlayerQueryVideoListReq videoPlayerQueryVideoListReq) {
                return Result.fail("调用视频查询通道的录像列表方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<GetVideoPlaybackUrlData> getVideoPlaybackUrl(PlayerGetVideoPlayBackUrlReq playerGetVideoPlayBackUrlReq) {
                return Result.fail("调用获取通道的录像回放流URL地址方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<PlayerGetVideoRealtimeUrlData> getVideoRealtimeUrl(PlayerGetVideoRealtimeUrlReq playerGetVideoRealtimeUrlReq) {
                return Result.fail("调用获取通道实时流URL地址方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<GetAudioChannelData> getAudioChannel(PlayerGetAudioChannelReq playerGetAudioChannelReq) {
                return Result.fail("调用获取语音对讲通道方法失败:" + throwable.getMessage());
            }

            @Override
            public Result playBackControl(PlayerPlayBackControlReq playerPlayBackControlReq) {
                return Result.fail("调用倍速回放方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<VideoDownloadDTO>> downloadVideo(List<VideoDownloadVO> list) {
                return Result.fail("调用录像批量下载失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<VideoDownloadDTO>> stopDownloadVideo(List<VideoDownloadVO> list) {
                return Result.fail("调用录像批量停止下载失败:" + throwable.getMessage());
            }

            @Override
            public Result<Map<String, DownloadProcessDTO>> downloadProcess(List<VideoDownloadVO> list) {
                return Result.fail("调用录像下载百分比查询失败:" + throwable.getMessage());
            }

        };
    }
}
