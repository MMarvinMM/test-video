package com.chinatower.event.api.feign.factory;

import com.chinatower.event.api.feign.client.AlarmFlowDeployFeign;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlarmFlowDeployFeignFallbackFactory implements FallbackFactory<AlarmFlowDeployFeign> {
    @Override
    public AlarmFlowDeployFeign create(Throwable throwable) {
        log.info("feigin调用异常信息是:{}", throwable.getMessage());
        AlarmFlowDeployFeignFallback webServiceFallback = new AlarmFlowDeployFeignFallback();
        webServiceFallback.setCause(throwable);
        return webServiceFallback;
    }
}
