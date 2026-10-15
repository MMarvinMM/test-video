package com.chinatower.video.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.VideoScreenConfigAPI;
import com.chinatower.video.api.entity.VideoScreenConfigVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class VideoScreenConfigFallbackFactory implements FallbackFactory<VideoScreenConfigAPI> {
    @Override
    public VideoScreenConfigAPI create(Throwable throwable) {
        return new VideoScreenConfigAPI() {

            @Override
            public Result<VideoScreenConfigVO> queryWaterMark(VideoScreenConfigVO videoScreenConfigVO) {
                return Result.fail("获取水印配置报错:" + throwable.getMessage());
            }
        };
    }
}
