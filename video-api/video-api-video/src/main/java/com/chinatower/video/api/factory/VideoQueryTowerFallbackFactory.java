package com.chinatower.video.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.VideoQueryTowerAPI;
import com.chinatower.video.api.deprecated.VideoQueryAPIMigrate;
import com.chinatower.video.api.entity.*;
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
public class VideoQueryTowerFallbackFactory implements FallbackFactory<VideoQueryTowerAPI> {
private static final Logger log = LoggerFactory.getLogger(VideoQueryTowerFallbackFactory.class);

    @Override
    public VideoQueryTowerAPI create(Throwable throwable) {
        log.error("调用铁塔视角video接口失败:{}", throwable.getMessage());
        return new VideoQueryTowerAPI() {

            @Override
            public Result<List<QueryPresetListData>> queryPresetList(@RequestBody VideoBaseReq videoBaseReq){
                return Result.fail("查询铁塔视角预置位列表:" + throwable.getMessage());
            }
        };
    }
}
