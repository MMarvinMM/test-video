package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.IAirTaskService;
import com.chinatower.device.entity.dto.AirTaskReq;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;


/**
 * 降级处理
 */
@Component
public class AirTaskFallbackFactory implements FallbackFactory<IAirTaskService> {

    @Override
    public IAirTaskService create(Throwable throwable) {
        return new IAirTaskService() {
            @Override
            public Result updateTaskAccumulateMileage(AirTaskReq airTaskReq) {
                return Result.fail("修改飞行里程失败:" + throwable.getMessage());
            }
        };
    }
}
