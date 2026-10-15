package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.device.api.factory.AirStatisticsFallbackFactory;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.ExportStatisticalAnalysisReq;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(contextId = "airStatisticsService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = AirStatisticsFallbackFactory.class)
public interface IAirStatisticsService {


    /**
     * 查询统计分析
     *
     * @param statisticalAnalysisReq
     * @return
     */
    @PostMapping("/airStatistics/queryExportStatisticalAnalysis")
    ExportStatisticalAnalysisReq queryExportStatisticalAnalysis(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq);

}
