package com.chinatower.airline.service;

import com.chinatower.airline.dto.response.ExportStatisticalAnalysisReq;
import com.chinatower.airline.dto.request.StatisticalAnalysisReq;
import com.chinatower.airline.dto.request.TaskStatisticsReq;
import com.chinatower.common.entity.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author WLF
 * @description
 * @date 2025/4/15 10:57
 */
public interface AirStatisticsService {

    /**
     *
     * @param taskStatisticsReq
     * @return
     */
    Result queryTaskCount(TaskStatisticsReq taskStatisticsReq);

    /**
     *
     * @param taskStatisticsReq
     * @return
     */
    Result queryTaskCountDayMonth(TaskStatisticsReq taskStatisticsReq);

    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    Result queryStatisticsOverview(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    Result queryFlightDistanceTop(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    Result queryFlightDurationTop(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    Result queryFailCauseTop(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    Result queryPlanUavFlyCountTop(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    Result queryTaskStatusStatistics(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    Result queryFlyTypeStatistics(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    Result queryFlyTrend(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    Result queryFlyTimeStatistical(StatisticalAnalysisReq statisticalAnalysisReq);


    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    List<Map> queryTaskDetail(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     *
     * @param statisticalAnalysisReq
     * @return
     */
    ExportStatisticalAnalysisReq queryExportStatisticalAnalysis(StatisticalAnalysisReq statisticalAnalysisReq);

    void exportStatisticalAnalysis(StatisticalAnalysisReq req, HttpServletResponse response);
}
