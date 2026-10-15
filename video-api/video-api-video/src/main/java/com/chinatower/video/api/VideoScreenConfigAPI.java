package com.chinatower.video.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.VideoScreenConfigVO;
import com.chinatower.video.api.factory.VideoScreenConfigFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(contextId = "VideoScreenConfigAPI",
        value = ServiceNameConstants.VIDEO_SERVICE,
        fallbackFactory = VideoScreenConfigFallbackFactory.class)
public interface VideoScreenConfigAPI {

    /**
     * 功能描述: 查询租户行业下的水印配置
     */
    @PostMapping("/video/screenConfig/queryWaterMark")
    Result<VideoScreenConfigVO> queryWaterMark(@RequestBody VideoScreenConfigVO videoScreenConfigVO);
}
