package com.chinatower.platform.controller;

import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.CustomVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.bdm.api.entity.InspectStatisticsVo;
import com.chinatower.platform.service.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;


/**
 * video-export
 * 统计类相关接口
 */
@RestController
@Slf4j
public class StatisticsExportController {
    @Resource
    private ExportStatisticsService exportStatistics;
    @Resource
    private StatisticWordService statisticWordService;
    @Resource
    private UserStatisticWordService userStatisticWordService;
    @Resource
    private DeviceStatisticWordService deviceStatisticWordService;
    @Resource
    private InspectExportStatisticsService inspectExportStatisticsService;
    @Resource
    private InspectExportWordStatisticsService inspectExportWordStatisticsService;
    /**
     * 导出告警统计报表
     *
     * @param statisticsVO
     * @param response
     * @return
     */
    @ApiOperation(value = "导出告警统计分析")
    @PostMapping("/statistic/exportEventStatistics")
    public void exportEventStatistics(@RequestBody StatisticsVO statisticsVO, HttpServletResponse response) {
        exportStatistics.exportEventStatistics(statisticsVO,response);
    }

    /**
     * 导出设备统计报表
     *
     * @param analysisVO
     * @param response
     * @return
     */
    @ApiOperation(value = "导出设备统计分析")
    @PostMapping("/statistic/exportDeviceStatistics")
    public void exportDeviceStatistics(@RequestBody AnalysisVO analysisVO, HttpServletResponse response) {
        exportStatistics.exportDeviceStatistics(analysisVO,response);
    }

    /**
     * 导出用户统计报表
     *
     * @param customVO
     * @param response
     * @return
     */
    @ApiOperation(value = "导出用户统计分析")
    @PostMapping("/statistic/exportUserStatistics")
    public void exportUserStatistics(@RequestBody CustomVO customVO,HttpServletResponse response) {
        exportStatistics.exportUserStatistics(customVO,response);
    }

    /**
     * 导出巡查统计报表
     *
     * @param inspectStatisticsVo
     * @param response
     * @return
     */
    @ApiOperation(value = "导出巡查统计报表")
    @PostMapping("/statistic/exportInspectStatistics")
    public void exportInspectStatistics(@RequestBody InspectStatisticsVo inspectStatisticsVo, HttpServletResponse response) {
        inspectExportStatisticsService.exportInspectStatistics(inspectStatisticsVo,response);
    }


    /**
     * 导出告警统计分析报告
     *
     * @param statisticsVO
     * @param response
     * @return
     */
    @ApiOperation(value = "导出告警统计分析报告")
    @PostMapping("/statistic/exportEventWordStatistics")
    public void exportEventWordStatistics(@RequestBody StatisticsVO statisticsVO, HttpServletResponse response) {
        statisticWordService.exportEventWordStatistics(statisticsVO,response);
    }

    /**
     * 导出设备统计分析报告
     *
     * @param analysisVO
     * @param response
     * @return
     */
    @ApiOperation(value = "导出设备统计分析报告")
    @PostMapping("/statistic/exportDeviceWordStatistics")
    public void exportDeviceWordStatistics(@RequestBody AnalysisVO analysisVO, HttpServletResponse response) {
        deviceStatisticWordService.exportDeviceWordStatistics(analysisVO,response);
    }


    /**
     * 导出用户统计分析报告
     * @param customVO
     * @param response
     */
    @ApiOperation(value = "导出用户统计分析报告")
    @PostMapping("/statistic/exportUserWordStatistics")
    public void exportUserWordStatistics(@RequestBody CustomVO customVO, HttpServletResponse response) {
        userStatisticWordService.exportUserWordStatistics(customVO,response);
    }

    /**
     * 导出巡查统计报告
     *
     * @param inspectStatisticsVo
     * @param response
     * @return
     */
    @ApiOperation(value = "导出巡查统计报告")
    @PostMapping("/statistic/exportWordInspectStatistics")
    public void exportWordInspectStatistics(@RequestBody InspectStatisticsVo inspectStatisticsVo, HttpServletResponse response) {
        inspectExportWordStatisticsService.exportWordInspectStatistics(inspectStatisticsVo,response);
    }
}
