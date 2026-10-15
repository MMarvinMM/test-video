package com.chinatower.analysis.modules.workbench.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CheckpointDAO {

    /**
     * 查询卡口摄像机当日识别次数
     * @param checkpointConditionVO
     * @return
     */
    String getCheckpointEventNumByDay(CheckpointConditionVO checkpointConditionVO);

    /**
     * 实时抓拍记录查询
     * @param checkpointBatchVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    List<CheckpointAlarmVO> getCaptureRecord(CheckpointBatchVO checkpointBatchVO);

    /**
     * 查询车流趋势统计-当年（月数据）
     * @param checkpointStatisticsConditionVO
     * @return
     */
    List<CheckpointTrendDataVO> getTrafficTrendStatisticsForYear(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询车流趋势统计-当年（月数据）
     * @param checkpointStatisticsConditionVO
     * @return
     */
    List<CheckpointTrendDataVO> getTrafficTrendStatisticsForYearByTime(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询车流趋势统计-当月（日数据）
     * @param checkpointStatisticsConditionVO
     * @return
     */
    List<CheckpointTrendDataVO> getTrafficTrendStatisticsForMonth(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询车流趋势统计-当月（日数据）
     * @param checkpointStatisticsConditionVO
     * @return
     */
    List<CheckpointTrendDataVO> getTrafficTrendStatisticsForMonthByTime(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询车流趋势统计-当日（小时数据）
     * @param checkpointStatisticsConditionVO
     * @return
     */
    List<CheckpointTrendDataVO> getTrafficTrendStatisticsForDayByTime(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);


    /**
     * 查询车流高频时段统计
     * @param checkpointStatisticsConditionVO
     * @return
     */
    List<CheckpointTrendDataVO> getHighFrequencyPeriodStatistics(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询车流高频时段统计
     * @param checkpointStatisticsConditionVO
     * @return
     */
    List<CheckpointTrendDataVO> getHighFrequencyPeriodStatisticsByTime(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);


    /**
     * 查询卡口车流排行统计-当年
     * @param checkpointStatisticsConditionVO
     * @return
     */
    List<CheckpointDeviceRankingDataVO> getTrafficFlowRankingStatisticsForDay(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询卡口车流排行统计-当月
     * @param checkpointStatisticsConditionVO
     * @return
     */
    List<CheckpointDeviceRankingDataVO> getTrafficFlowRankingStatisticsForMonth(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 查询卡口车流排行统计-当日
     * @param checkpointStatisticsConditionVO
     * @return
     */
    List<CheckpointDeviceRankingDataVO> getTrafficFlowRankingStatisticsForYear(CheckpointStatisticsConditionVO checkpointStatisticsConditionVO);

    /**
     * 获取当前租户已配置的卡口设备
     *
     * @param tenantId
     * @return
     */
    @DS(EbikeDataBaseConstant.SLAVE)
    List<String> getCheckpointDeviceList(String tenantId);

}
