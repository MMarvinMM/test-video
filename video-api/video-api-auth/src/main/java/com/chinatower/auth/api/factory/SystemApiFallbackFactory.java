package com.chinatower.auth.api.factory;

import com.chinatower.auth.api.SystemApiFeignClient;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SystemApiFallbackFactory implements FallbackFactory<SystemApiFeignClient> {

    @Override
    public SystemApiFeignClient create(Throwable throwable) {
        return new SystemApiFeignClient() {
            @Override
            public Result selectUserOnlyByUserName(String userName, String Authorization) {
                return null;
            }

        };
    };
}
