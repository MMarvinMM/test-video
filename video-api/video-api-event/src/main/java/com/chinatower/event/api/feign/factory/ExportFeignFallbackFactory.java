package com.chinatower.event.api.feign.factory;

import com.chinatower.event.api.feign.client.ExportFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExportFeignFallbackFactory implements FallbackFactory<ExportFeignClient> {
    @Override
    public ExportFeignClient create(Throwable throwable) {
        log.info("feigin调用异常信息是:{}", throwable.getMessage());
        ExportFeignFallback fallback = new ExportFeignFallback();
        fallback.setCause(throwable);
        return fallback;
    }
}

