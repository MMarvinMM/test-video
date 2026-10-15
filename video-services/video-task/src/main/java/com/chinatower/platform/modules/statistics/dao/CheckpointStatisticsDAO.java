package com.chinatower.platform.modules.statistics.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.statistics.entity.CheckpointDateVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @className: CheckpointStatisticsDAO
 * @description: 车辆卡口统计分析DAO
 * @author: csj
 * @date: 2022/09/28
 * @requestID 需求单号及需求名称
 */
@Mapper
public interface CheckpointStatisticsDAO {

    /**
     * 卡口设备告警统计-小时
     * @param dateVO
     * @return
     */
    int deleteCheckpointEventByHour(CheckpointDateVO dateVO);

    int insertCheckpointEventByHour(List<CheckpointDateVO> list);

    /**
     * 卡口设备告警统计-天
     * @param dateVO
     * @return
     */
    int deleteCheckpointEventByDay(CheckpointDateVO dateVO);

    int insertCheckpointEventByDay(List<CheckpointDateVO> list);

    /**
     * 卡口设备告警统计-月
     * @param dateVO
     * @return
     */
    int deleteCheckpointEventByMonth(CheckpointDateVO dateVO);

    int insertCheckpointEventByMonth(List<CheckpointDateVO> list);

    /**
     * 卡口设备告警统计-年
     * @param dateVO
     * @return
     */
    int deleteCheckpointEventByYear(CheckpointDateVO dateVO);

    int insertCheckpointEventByYear(List<CheckpointDateVO> list);

    /**
     * 获取当前小时的数据
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<CheckpointDateVO> getCheckpointEventByHourList(CheckpointDateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<CheckpointDateVO> getCheckpointEventByHourLists(CheckpointDateVO dateVO);

    /**
     * 获取当前天的数据
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<CheckpointDateVO> getCheckpointEventByDayList(CheckpointDateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<CheckpointDateVO> getCheckpointEventByDayLists(CheckpointDateVO dateVO);

    /**
     * 获取当前月的数据
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<CheckpointDateVO> getCheckpointEventByMonthList(CheckpointDateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<CheckpointDateVO> getCheckpointEventByMonthLists(CheckpointDateVO dateVO);

    /**
     * 获取当前年的数据
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<CheckpointDateVO> getCheckpointEventByYearList(CheckpointDateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<CheckpointDateVO> getCheckpointEventByYearLists(CheckpointDateVO dateVO);

}

