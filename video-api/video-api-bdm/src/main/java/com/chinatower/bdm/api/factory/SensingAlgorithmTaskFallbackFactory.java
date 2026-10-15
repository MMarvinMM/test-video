package com.chinatower.bdm.api.factory;


import com.chinatower.bdm.api.SensingAlgorithmTaskAPI;
import com.chinatower.bdm.api.entity.AlgorithmPicResultVO;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class SensingAlgorithmTaskFallbackFactory implements FallbackFactory<SensingAlgorithmTaskAPI> {


    @Override
    public SensingAlgorithmTaskAPI create(Throwable throwable) {
        return new SensingAlgorithmTaskAPI() {
            @Override
            public Result<Boolean> updateAlgorithmPicResult(AlgorithmPicResultVO algorithmPicResultVO) {
                return Result.fail("遥感算法图片识别异常" + throwable.getMessage());
            }
        };
    }
}
