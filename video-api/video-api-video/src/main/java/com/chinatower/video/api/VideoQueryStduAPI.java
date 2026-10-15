package com.chinatower.video.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.PlayerGetVideoRealtimeUrlData;
import com.chinatower.video.api.factory.VideoQueryFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;


@FeignClient(contextId = "VideoQueryStduAPI",
        value = ServiceNameConstants.STDU_SERVICE,
        fallbackFactory = VideoQueryFallbackFactory.class)
public interface VideoQueryStduAPI {

    @PostMapping("/devices/channels/getVideoRealtimeUrlsForLookHere")
    Result getVideoRealtimeUrlsForLookHere(@RequestBody List<Map> list);

    /**
     * 全域感知大屏，圈选摄像机，批量拉取实时流
     * @param reqList
     * @return
     */
    @PostMapping("/devices/channels/getVideoRealtimeUrlsForComponent")
    Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlsForComponent(@RequestBody Set<PlayerGetVideoRealtimeUrlData> reqList);
}
