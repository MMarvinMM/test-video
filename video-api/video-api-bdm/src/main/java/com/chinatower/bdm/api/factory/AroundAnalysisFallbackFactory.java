package com.chinatower.bdm.api.factory;


import com.chinatower.bdm.api.AroundAnalysisAPI;
import com.chinatower.bdm.api.entity.AroundAnalysisParamVO;
import com.chinatower.bdm.api.entity.AroundAnalysisResultVO;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AroundAnalysisFallbackFactory implements FallbackFactory<AroundAnalysisAPI> {

    @Override
    public AroundAnalysisAPI create(Throwable throwable) {
        return new AroundAnalysisAPI() {
            @Override
            public Result<List<AroundAnalysisResultVO>> getAroundAnalysisList(AroundAnalysisParamVO aroundAnalysisParamVO) {
                return Result.fail("周边分析调用失败"+throwable.getMessage());
            }

            @Override
            public Result<List<AroundAnalysisResultVO>> getAroundAnalysisOwnList(AroundAnalysisParamVO aroundAnalysisParamVO) {
                return Result.fail("周边分析调用失败"+throwable.getMessage());
            }

        };
    }
}
