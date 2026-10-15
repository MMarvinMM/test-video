package com.chinatower.system.api.feign.factory;

import com.chinatower.system.api.feign.client.SystemFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @auther liuhuanhuan
 * @date 2022/2/15
 */
@Slf4j
@Component
public class SystemFeignClientFallbackFactory implements FallbackFactory<SystemFeignClient> {

    @Override
    public SystemFeignClient create(Throwable throwable) {
        log.info("feigin调用异常信息是:{}", throwable.getMessage());
        SystemFeignClientFallback webServiceFallback = new SystemFeignClientFallback();
        webServiceFallback.setCause(throwable);
        return webServiceFallback;
    }
}
