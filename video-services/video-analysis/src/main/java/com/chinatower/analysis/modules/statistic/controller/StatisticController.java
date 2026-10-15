package com.chinatower.analysis.modules.statistic.controller;

import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.analysis.modules.statistic.service.StatisticService;
import com.chinatower.common.entity.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * video-analysis
 */
@Slf4j
@RestController
@Api(tags = "统计分析Controller")
public class StatisticController {
    @Resource
    private StatisticService statisticService;

    /**
     * 事件统计分析统计
     *
     * @param statisticsVO
     * @return Result
     * @throws
     */
    @ApiOperation(value = "事件统计分析统计")
    @PostMapping("/statistic/getEventStatisticalAnalysis")
    public Result getEventStatisticalAnalysis(@RequestBody StatisticsVO statisticsVO) {
        return statisticService.getEventStatisticalAnalysis(statisticsVO);
    }


    /**
     * 事件统计分析统计（告警标签维度）
     *
     * @param statisticsVO
     * @return Result
     * @throws
     */
    @ApiOperation(value = "事件统计分析统计（告警标签维度）")
    @PostMapping("/statistic/getEventAnalysisByTag")
    public Result getEventAnalysisByTag(@RequestBody StatisticsVO statisticsVO) {
        return statisticService.getEventAnalysisByTag(statisticsVO);
    }

    /**
     * 查询告警事件准确率统计
     *
     * @param statisticsVO
     * @return Result
     * @throws
     */
    @ApiOperation(value = "告警事件准确率统计")
    @PostMapping("/statistic/getEventRealNum")
    public Result getEventRealNum(@RequestBody StatisticsVO statisticsVO) {
        return statisticService.getEventRealNum(statisticsVO);
    }

    /**
     * 按年或月查询告警事件统计
     *
     * @param statisticsVO
     * @return Result
     * @throws
     */
    @ApiOperation(value = "按年或月查询告警事件统计")
    @PostMapping("/statistic/getEventAlarmNumYearMonth")
    public Result getEventAlarmNumYearMonth(@RequestBody StatisticsVO statisticsVO) {
        return statisticService.getEventAlarmNumYearMonth(statisticsVO);
    }

    /**
     * 办结事件处理时长统计
     *
     * @param statisticsVO
     * @return Result
     * @throws
     */
    @ApiOperation(value = "办结事件处理时长统计")
    @PostMapping("/statistic/getEventHandleTimeNum")
    public Result getEventHandleTimeNum(@RequestBody StatisticsVO statisticsVO) {
        return statisticService.getEventHandleTimeNum(statisticsVO);
    }

    /**
     * 告警事件统计-告警区域处理时长
     *
     * @param analysisVO
     * @return Result
     * @throws
     */
    @ApiOperation(value = "告警区域处理时长")
    @PostMapping("/statistic/getEventNumByHandle")
    public Result getEventNumByHandle(@RequestBody AnalysisVO analysisVO) {
        return statisticService.getEventNumByHandle(analysisVO);
    }

}
