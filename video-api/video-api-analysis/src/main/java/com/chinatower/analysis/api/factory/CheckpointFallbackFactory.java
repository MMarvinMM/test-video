package com.chinatower.analysis.api.factory;

import com.chinatower.analysis.api.CheckpointAPI;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckpointFallbackFactory implements FallbackFactory<CheckpointAPI> {
    @Override
    public CheckpointAPI create(Throwable throwable) {
        return new CheckpointAPI(){

            /**
             * 查询卡口摄像机当日识别次数
             *
             * @param checkpointConditionVO
             * @return
             */
            @Override
            public Result<String> getCheckpointEventNumByDay(CheckpointConditionVO checkpointConditionVO){
                return Result.fail("查询卡口摄像机当日识别次数方法失败:" + throwable.getMessage());
            }
            /**
             * 查询卡口摄像机当日识别次数
             *
             * @param checkpointConditionVO
             * @return
             */
            @Override
            public Result<String> getIndustryCheckpointEventNumByDay(CheckpointConditionVO checkpointConditionVO){
                return Result.fail("查询卡口摄像机当日识别次数方法失败:" + throwable.getMessage());
            }

            /**
             * 实时抓拍记录查询
             *
             * @param checkpointBatchVO
             * @return
             */
            @Override
            public TableDataInfo getCaptureRecord(CheckpointBatchVO checkpointBatchVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            /**
             * 实时抓拍记录查询
             *
             * @param checkpointBatchVO
             * @return
             */
            @Override
            public TableDataInfo getIndustryCaptureRecord(CheckpointBatchVO checkpointBatchVO){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            /**
             * 查询车流趋势统计
             *
             * @param checkpointStatisticsConditionVO
             * @return
             */
            @Override
            public Result<List<CheckpointTrendDataVO>> getTrafficTrendStatistics(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
                return Result.fail("查询车流趋势统计方法失败:" + throwable.getMessage());
            }

            /**
             * 查询车流趋势统计
             *
             * @param checkpointStatisticsConditionVO
             * @return
             */
            @Override
            public Result<List<CheckpointTrendDataVO>> getIndustryTrafficTrendStatistics(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
                return Result.fail("查询车流趋势统计方法失败:" + throwable.getMessage());
            }

            /**
             * 查询车流高频时段统计
             *
             * @param checkpointStatisticsConditionVO
             * @return
             */
            @Override
            public Result<List<CheckpointTrendDataVO>> getHighFrequencyPeriodStatistics(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
                return Result.fail("查询车流高频时段统计方法失败:" + throwable.getMessage());
            }

            /**
             * 查询车流高频时段统计
             *
             * @param checkpointStatisticsConditionVO
             * @return
             */
            @Override
            public Result<List<CheckpointTrendDataVO>> getIndustryHighFrequencyPeriodStatistics(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
                return Result.fail("查询车流高频时段统计方法失败:" + throwable.getMessage());
            }

            /**
             * 查询卡口车流排行统计
             *
             * @param checkpointStatisticsConditionVO
             * @return
             */
            @Override
            public Result<List<CheckpointDeviceRankingDataVO>> getTrafficFlowRankingStatistics(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
                return Result.fail("查询车流高频时段统计old失败:" + throwable.getMessage());
            }

            /**
             * 查询卡口车流排行统计
             *
             * @param checkpointStatisticsConditionVO
             * @return
             */
            @Override
            public Result<List<CheckpointDeviceRankingDataVO>> getIndustryTrafficFlowRankingStatistics(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
                return Result.fail("查询车流高频时段统计old失败:" + throwable.getMessage());
            }

            /**
             * 查询卡口车流排行统计
             *
             * @param checkpointStatisticsConditionVO
             * @return
             */
            @Override
            public Result<List<CheckpointTrendDataVO>> getTrafficTrendStatisticsByTime(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
                return Result.fail("查询车流趋势统计new失败:" + throwable.getMessage());
            }

            /**
             * 查询卡口车流排行统计
             *
             * @param checkpointStatisticsConditionVO
             * @return
             */
            @Override
            public Result<List<CheckpointTrendDataVO>> getHighFrequencyPeriodStatisticsByTime(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO){
                return Result.fail("查询车流高频时段统计new失败:" + throwable.getMessage());
            }
        };
    }
}
