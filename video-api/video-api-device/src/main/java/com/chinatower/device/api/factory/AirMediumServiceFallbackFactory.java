package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirMediumService;
import com.chinatower.device.entity.dto.AirTaskStatusProcessDto;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author cuibaoluo
 * @date 2025/12/17
 * @description：
 */
@Component
public class AirMediumServiceFallbackFactory implements FallbackFactory<IAirMediumService> {
    @Override
    public IAirMediumService create(Throwable throwable) {
        return new IAirMediumService() {
            @Override
            public Result taskStatusProcess(AirTaskStatusProcessDto dto) {
                return Result.fail("喇叭任务调用失败:" + throwable.getMessage());
            }
        };
    }
}
