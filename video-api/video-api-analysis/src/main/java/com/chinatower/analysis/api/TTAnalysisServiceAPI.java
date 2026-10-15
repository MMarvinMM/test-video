package com.chinatower.analysis.api;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.api.factory.TTAnalysisServiceFallbackFactory;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "TTAnalysisServiceAPI",
        value = ServiceNameConstants.TTANALYSIS_SERVICE,
        fallbackFactory = TTAnalysisServiceFallbackFactory.class)
public interface TTAnalysisServiceAPI {

    /**
     * 查询属地算法统计数据
     */
    @PostMapping("/statistic/getEventJudgeAlarm")
    Result getEventJudgeAlarm(@RequestBody DateVO dateVO);
}
