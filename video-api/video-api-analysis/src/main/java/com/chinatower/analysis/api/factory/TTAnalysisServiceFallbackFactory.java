package com.chinatower.analysis.api.factory;

import com.chinatower.analysis.api.TTAnalysisServiceAPI;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.common.entity.Result;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;


@Component
public class TTAnalysisServiceFallbackFactory implements FallbackFactory<TTAnalysisServiceAPI> {
    @Override
    public TTAnalysisServiceAPI create(Throwable throwable) {
        return new TTAnalysisServiceAPI(){
            @Override
            public Result getEventJudgeAlarm(DateVO dateVO) {
                return Result.fail("查询属地算法统计数据失败:" + throwable.getMessage());
            }

        };
    }
}
