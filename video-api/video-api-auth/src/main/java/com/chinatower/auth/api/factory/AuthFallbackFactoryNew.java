package com.chinatower.auth.api.factory;

import com.chinatower.auth.api.AuthFeignClientNew;
import com.chinatower.auth.api.entity.SdkToApiLoginVo;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class AuthFallbackFactoryNew implements FallbackFactory<AuthFeignClientNew> {
    @Override
    public AuthFeignClientNew create(Throwable throwable) {
        return new AuthFeignClientNew() {
            @Override
            public Result sdkToApiLogin(@RequestBody SdkToApiLoginVo vo){
                return Result.fail("模拟登录报错"+throwable.getMessage());
            }
        };
    }
}
