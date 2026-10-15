package com.chinatower.order.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.order.api.IRecorderDeviceApiService;
import com.chinatower.platform.entity.order.RecorderDeviceTrackDTO;
import com.chinatower.platform.entity.order.UavDeviceTrackParam;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RecorderDeviceApiFallbackFactory implements FallbackFactory<IRecorderDeviceApiService> {

    private static final Logger log = LoggerFactory.getLogger(RecorderDeviceApiFallbackFactory.class);
    @Override
    public IRecorderDeviceApiService create(Throwable cause) {
        log.error("执法记录仪服务调用失败:{}", cause.getMessage());
        return new IRecorderDeviceApiService() {

            @Override
            public Result receiveRecorderTrack(String req) {
                return Result.fail("执法记录仪轨迹上报失败:" + cause.getMessage());
            }

            @Override
            public Result<List<RecorderDeviceTrackDTO>> getRecorderDeviceTrackList(UavDeviceTrackParam param) {
                return Result.fail("查询执法记录仪轨迹失败:" + cause.getMessage());
            }
        };
    }
}
