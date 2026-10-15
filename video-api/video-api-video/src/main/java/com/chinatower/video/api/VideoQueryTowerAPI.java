package com.chinatower.video.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.QueryPresetListData;
import com.chinatower.video.api.entity.VideoBaseReq;
import com.chinatower.video.api.factory.VideoQueryTowerFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "VideoQueryTowerAPI",
        value = ServiceNameConstants.TTVIEW_VIDEO_SERVICE,
        fallbackFactory = VideoQueryTowerFallbackFactory.class)
public interface VideoQueryTowerAPI {
    /**
     * 服务内部调用，不对外
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/queryPresetList")
    Result<List<QueryPresetListData>> queryPresetList(@RequestBody VideoBaseReq videoBaseReq);
}
