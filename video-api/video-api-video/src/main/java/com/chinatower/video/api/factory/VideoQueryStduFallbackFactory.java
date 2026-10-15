package com.chinatower.video.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.VideoQueryStduAPI;
import com.chinatower.video.api.entity.PlayerGetVideoRealtimeUrlData;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 查询功能降级处理
 *
 * @Author: zhujun
 * @Date: 2022/1/16
 */
@Component
public class VideoQueryStduFallbackFactory implements FallbackFactory<VideoQueryStduAPI> {
private static final Logger log = LoggerFactory.getLogger(VideoQueryStduFallbackFactory.class);

    @Override
    public VideoQueryStduAPI create(Throwable throwable) {
        log.error("调用stdu视频播放接口失败:{}", throwable.getMessage());
        return new VideoQueryStduAPI() {

            @Override
            public Result getVideoRealtimeUrlsForLookHere(@RequestBody List< Map > list) {
                return Result.fail("批量获取通道实时流URL地址 看这里sdk用:" + throwable.getMessage());
            }
            @Override
            public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsForComponent(@RequestBody Set<PlayerGetVideoRealtimeUrlData> reqList) {
                return Result.fail("全域感知大屏，圈选摄像机，批量拉取实时流:" + throwable.getMessage());
            }
        };
    }
}
