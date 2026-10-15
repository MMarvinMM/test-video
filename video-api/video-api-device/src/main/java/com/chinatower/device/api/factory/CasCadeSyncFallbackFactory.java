package com.chinatower.device.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.ICasCadeSyncService;
import com.chinatower.platform.entity.device.CasCadeSyncVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 级联平台信息同步
 */
@Component
public class CasCadeSyncFallbackFactory implements FallbackFactory<ICasCadeSyncService> {


    @Override
    public ICasCadeSyncService create(Throwable throwable) {

        return new ICasCadeSyncService() {

            @Override
            public Result<String> synDelCascadeByOrder(CasCadeSyncVO req) {
                return Result.fail("订购变更后需要重新同步级联平台信息失败:" + throwable.getMessage());
            }
        };
    }
}
