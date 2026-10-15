package com.chinatower.analysis.modules.workbench.controller;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.modules.workbench.service.CheckpointService;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * video-analysis
 */
@Slf4j
@RestController
@Api(tags = "卡口监控统计Controller")
public class CheckpointController extends TowerBaseController {
    @Resource
    private CheckpointService checkpointService;

    /**
     * 查询卡口摄像机当日识别次数
     *
     * @param checkpointConditionVO
     * @return
     */
    @ApiOperation(value = "查询卡口摄像机当日识别次数")
    @PostMapping("/checkpoint/getCheckpointEventNumByDay")
    public Result<String> getCheckpointEventNumByDay(@Validated @RequestBody CheckpointConditionVO checkpointConditionVO){
        return checkpointService.getCheckpointEventNumByDay(checkpointConditionVO);
    }

    /**
     * 实时抓拍记录查询
     *
     * @param checkpointBatchVO
     * @return
     */
    @ApiOperation(value = "实时抓拍记录查询")
    @PostMapping("/checkpoint/getCaptureRecord")
    public TableDataInfo getCaptureRecord(@Validated @RequestBody CheckpointBatchVO checkpointBatchVO){
        List<CheckpointAlarmVO> list = checkpointService.getCaptureRecord(checkpointBatchVO);
        return getDataTable(list);
    }

    /**
     * 查询车流趋势统计
     *
     * @param checkpointStatisticsConditionVO
     * @return
     */
    @ApiOperation(value = "查询车流趋势统计")
    @PostMapping("/checkpoint/getTrafficTrendStatistics")
    public Result<List<CheckpointTrendDataVO>> getTrafficTrendStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        return checkpointService.getTrafficTrendStatistics(checkpointStatisticsConditionVO);
    }

    /**
     * 查询车流趋势统计
     *
     * @param checkpointStatisticsConditionVO
     * @return
     */
    @ApiOperation(value = "查询车流趋势统计")
    @PostMapping("/checkpoint/getTrafficTrendStatisticsByTime")
    public Result<List<CheckpointTrendDataVO>> getTrafficTrendStatisticsByTime(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        return checkpointService.getTrafficTrendStatisticsByTime(checkpointStatisticsConditionVO);
    }

    /**
     * 查询车流高频时段统计
     *
     * @param checkpointStatisticsConditionVO
     * @return
     */
    @ApiOperation(value = "查询车流高频时段统计")
    @PostMapping("/checkpoint/getHighFrequencyPeriodStatistics")
    public Result<List<CheckpointTrendDataVO>> getHighFrequencyPeriodStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        return checkpointService.getHighFrequencyPeriodStatistics(checkpointStatisticsConditionVO);
    }

    /**
     * 查询车流高频时段统计
     *
     * @param checkpointStatisticsConditionVO
     * @return
     */
    @ApiOperation(value = "查询车流高频时段统计")
    @PostMapping("/checkpoint/getHighFrequencyPeriodStatisticsByTime")
    public Result<List<CheckpointTrendDataVO>> getHighFrequencyPeriodStatisticsByTime(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        return checkpointService.getHighFrequencyPeriodStatisticsByTime(checkpointStatisticsConditionVO);
    }

    /**
     * 查询卡口车流排行统计
     *
     * @param checkpointStatisticsConditionVO
     * @return
     */
    @ApiOperation(value = "查询卡口车流排行统计")
    @PostMapping("/checkpoint/getTrafficFlowRankingStatistics")
    public Result<List<CheckpointDeviceRankingDataVO>> getTrafficFlowRankingStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        return checkpointService.getTrafficFlowRankingStatistics(checkpointStatisticsConditionVO);
    }

    /**
     * 查询卡口摄像机当日识别次数
     *
     * @param checkpointConditionVO
     * @return
     */
    @ApiOperation(value = "查询卡口摄像机当日识别次数")
    @PostMapping("/industry/checkpoint/getCheckpointEventNumByDay")
    public Result<String> getIndustryCheckpointEventNumByDay(@Validated @RequestBody CheckpointConditionVO checkpointConditionVO){
        return checkpointService.getCheckpointEventNumByDay(checkpointConditionVO);
    }

    /**
     * 实时抓拍记录查询
     *
     * @param checkpointBatchVO
     * @return
     */
    @ApiOperation(value = "实时抓拍记录查询")
    @PostMapping("/industry/checkpoint/getCaptureRecord")
    public TableDataInfo getIndustryCaptureRecord(@Validated @RequestBody CheckpointBatchVO checkpointBatchVO){
        List<CheckpointAlarmVO> list = checkpointService.getCaptureRecord(checkpointBatchVO);
        return getDataTable(list);
    }

    /**
     * 查询车流趋势统计
     *
     * @param checkpointStatisticsConditionVO
     * @return
     */
    @ApiOperation(value = "查询车流趋势统计")
    @PostMapping("/industry/checkpoint/getTrafficTrendStatistics")
    public Result<List<CheckpointTrendDataVO>> getIndustryTrafficTrendStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        return checkpointService.getTrafficTrendStatistics(checkpointStatisticsConditionVO);
    }

    /**
     * 查询车流高频时段统计
     *
     * @param checkpointStatisticsConditionVO
     * @return
     */
    @ApiOperation(value = "查询车流高频时段统计")
    @PostMapping("/industry/checkpoint/getHighFrequencyPeriodStatistics")
    public Result<List<CheckpointTrendDataVO>> getIndustryHighFrequencyPeriodStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        return checkpointService.getHighFrequencyPeriodStatistics(checkpointStatisticsConditionVO);
    }

    /**
     * 查询卡口车流排行统计
     *
     * @param checkpointStatisticsConditionVO
     * @return
     */
    @ApiOperation(value = "查询卡口车流排行统计")
    @PostMapping("/industry/checkpoint/getTrafficFlowRankingStatistics")
    public Result<List<CheckpointDeviceRankingDataVO>> getIndustryTrafficFlowRankingStatistics(@Validated @RequestBody CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
        return checkpointService.getTrafficFlowRankingStatistics(checkpointStatisticsConditionVO);
    }
}
