package com.chinatower.airline.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.airline.dto.response.ExportStatisticalAnalysisReq;
import com.chinatower.airline.dto.request.StatisticalAnalysisReq;
import com.chinatower.airline.dto.request.TaskStatisticsReq;
import com.chinatower.airline.service.AirStatisticsService;
import com.chinatower.common.entity.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author WLF
 * @description
 * @date 2025/4/15 10:36
 */
@RestController
@Api(value = "飞行统计模块", tags = "飞行统计模块")
@Slf4j
@RequestMapping("/airStatistics")
public class AirStatisticsController {
    @Resource
    private AirStatisticsService airStatisticsService;

    /**
     * 统计飞行任务数量以及列表
     *
     * @param taskStatisticsReq
     * @return
     */
    @PostMapping("/queryTaskCount")
    public Result queryTaskCount(@RequestBody TaskStatisticsReq taskStatisticsReq) {
        log.info("airStatistics-queryTaskCount-req:{}", JSON.toJSONString(taskStatisticsReq));
        return airStatisticsService.queryTaskCount(taskStatisticsReq);
    }

    /**
     * 统计飞行任务数量(统计维度 1日/7日/30日)
     *
     * @param taskStatisticsReq
     * @return
     */
    @PostMapping("/queryTaskCountDayMonth")
    public Result queryTaskCountDayMonth(@RequestBody TaskStatisticsReq taskStatisticsReq) {
        log.info("airStatistics-queryTaskCountDayMonth-req:{}", JSON.toJSONString(taskStatisticsReq));
        return airStatisticsService.queryTaskCountDayMonth(taskStatisticsReq);
    }


    /**
     * 统计总览
     *
     * @param statisticalAnalysisReq
     * @return
     */
    @PostMapping("/queryStatisticsOverview")
    public Result queryStatisticsOverview(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryStatisticsOverview-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryStatisticsOverview(statisticalAnalysisReq);
    }

    /**
     * 飞行距离排名top5
     *
     * @param statisticalAnalysisReq
     * @return
     */
    @PostMapping("/queryFlightDistanceTop")
    public Result queryFlightDistanceTop(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryFlightDistanceTop-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryFlightDistanceTop(statisticalAnalysisReq);
    }

    /**
     * 飞行时长排名top5
     *
     * @param statisticalAnalysisReq
     * @return
     */
    @PostMapping("/queryFlightDurationTop")
    public Result queryFlightDurationTop(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryFlightDurationTop-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryFlightDurationTop(statisticalAnalysisReq);
    }

    /**
     * 失败原因排行top5
     *
     * @param statisticalAnalysisReq
     * @return
     */
    @PostMapping("/queryFailCauseTop")
    public Result queryFailCauseTop(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryFailCauseTop-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryFailCauseTop(statisticalAnalysisReq);
    }

    /**
     * 计划下的无人机飞行次数排行top5
     *
     * @param statisticalAnalysisReq
     * @return
     */
    @PostMapping("/queryPlanUavFlyCountTop")
    public Result queryPlanUavFlyCountTop(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryPlanUavFlyCountTop-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryPlanUavFlyCountTop(statisticalAnalysisReq);
    }

    /**
     * 任务状态统计
     *
     * @param statisticalAnalysisReq
     * @return
     */
    @PostMapping("/queryTaskStatusStatistics")
    public Result queryTaskStatusStatistics(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryTaskStatusStatistics-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryTaskStatusStatistics(statisticalAnalysisReq);
    }

    /**
     * 飞行类别统计
     *
     * @param statisticalAnalysisReq
     * @return
     */
    @PostMapping("/queryFlyTypeStatistics")
    public Result queryFlyTypeStatistics(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryFlyTypeStatistics-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryFlyTypeStatistics(statisticalAnalysisReq);
    }

    /**
     * 飞行趋势图
     *
     * @param statisticalAnalysisReq
     * @return
     */
    @PostMapping("/queryFlyTrend")
    public Result queryFlyTrend(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryFlyTrend-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryFlyTrend(statisticalAnalysisReq);
    }

    /**
     *  起飞时刻统计
     *
     * @param statisticalAnalysisReq
     * @return
     */
    @PostMapping("/queryFlyTimeStatistical")
    public Result queryFlyTimeStatistical(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryFlyTimeStatistical-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryFlyTimeStatistical(statisticalAnalysisReq);
    }

    /**
     *  查询任务清单
     *
     * @param statisticalAnalysisReq
     * @return
     */

    @PostMapping("/queryTaskDetail")
    public List<Map> queryTaskDetail(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryTaskDetail-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryTaskDetail(statisticalAnalysisReq);
    }


    @PostMapping("/queryExportStatisticalAnalysis")
    public ExportStatisticalAnalysisReq queryExportStatisticalAnalysis(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq) {
        log.info("airStatistics-queryExportStatisticalAnalysis-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        return airStatisticsService.queryExportStatisticalAnalysis(statisticalAnalysisReq);
    }

    @PostMapping("/exportStatisticalAnalysis")
    public void exportStatisticalAnalysis(@RequestBody StatisticalAnalysisReq statisticalAnalysisReq,
                                          HttpServletResponse response) {
        log.info("airStatistics-exportStatisticalAnalysis-req:{}", JSON.toJSONString(statisticalAnalysisReq));
        airStatisticsService.exportStatisticalAnalysis(statisticalAnalysisReq, response);
    }


}
