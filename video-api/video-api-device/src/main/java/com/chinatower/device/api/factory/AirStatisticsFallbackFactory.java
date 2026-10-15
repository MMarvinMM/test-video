package com.chinatower.device.api.factory;

import com.chinatower.device.api.IAirStatisticsService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 降级处理
 */
@Component
public class AirStatisticsFallbackFactory implements FallbackFactory<IAirStatisticsService> {

    @Override
    public IAirStatisticsService create(Throwable throwable) {
        return null;
    }
}
