package com.chinatower.order.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.order.api.IUavDeviceApiService;
import com.chinatower.platform.entity.order.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 无人机降级处理
 *
 *
 */
@Component
public class UavDeviceApiFallbackFactory implements FallbackFactory<IUavDeviceApiService>
{

    private static final Logger log = LoggerFactory.getLogger(UavDeviceApiFallbackFactory.class);

    @Override
    public IUavDeviceApiService create(Throwable throwable) {
        log.error("无人机服务调用失败:{}", throwable.getMessage());
        return new IUavDeviceApiService()
        {

            @Override
            public Result<List<UavDeviceTrackVO>> qryUavDeviceTrackList(UavDeviceTrackParam param) {
                return Result.fail("查询无人机轨迹失败:" + throwable.getMessage());
            }

            @Override
            public Result receiveUavTrack(String req) {
                return Result.fail("无人机轨迹上报失败:" + throwable.getMessage());
            }
        };
    }
}
