package com.chinatower.device.entity.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExportStatisticalAnalysisReq {

    private List<Map> statisticsOverviewList;//统计总览
    private List<Map> flightDistanceTopList;//飞行距离统计排名
    private List<Map> flightDurationTopList;//飞行时长统计排名
    private List<Map> failCauseTopList;//失败原因排行
    private List<Map> planUavFlyCountTopList;//计划统计排名
    private List<Map> taskStatusStatisticsTopList;//任务状态统计
    private List<Map> flyTypeStatisticsList;//飞行类别统计
    private List<Map> flyTrendList;//飞行趋势统计
    private List<Map> flyTimeStatisticalList;//起飞时刻统计
    private List<Map> taskDetailList;//飞行任务明细
}
