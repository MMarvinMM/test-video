package com.chinatower.airline.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.airline.dto.request.RoadsNameMilepostReq;
import com.chinatower.airline.dto.request.StatisticalAnalysisReq;
import com.chinatower.airline.dto.request.TaskStatisticsReq;
import com.chinatower.airline.dto.response.AirTaskStatisticsRes;
import com.chinatower.airline.dto.response.ExportStatisticalAnalysisTaskRep;
import com.chinatower.airline.dto.response.ExportStatisticalAnalysisWarningOrderRep;
import com.chinatower.airline.dto.response.TaskStatisticsRsp;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author WLF
 * @description
 * @date 2025/4/15 11:13
 */
@Mapper
public interface AirStatisticsMapper {
    /**
     * 查询任务统计
     * @param taskStatisticsReq
     * @return
     */
    TaskStatisticsRsp queryTaskCount(TaskStatisticsReq taskStatisticsReq);

    /**
     * 查询任务统计（按天、月）
     * @param taskStatisticsReq
     * @return
     */
    List<Map> queryTaskCountDay(TaskStatisticsReq taskStatisticsReq);

    /**
     * 查询任务统计（按天、月）
     * @param taskStatisticsReq
     * @return
     */
    List<Map> queryTaskCountMonth(TaskStatisticsReq taskStatisticsReq);


    /**
     * 查询任务统计列表
     * @param taskStatisticsReq
     * @return
     */
    List<AirTaskStatisticsRes> queryTaskCountList(TaskStatisticsReq taskStatisticsReq);

    /**
     * 统计总览
     * @param statisticalAnalysisReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    Map queryStatisticsOverview(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     * 飞行距离排行top5
     * @param statisticalAnalysisReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<Map> queryFlightDistanceTop(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     * 计划下的无人机飞行次数排行top5
     * @param statisticalAnalysisReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<Map> queryFlightDurationTop(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     * 无人机失败原因排行top5
     * @param statisticalAnalysisReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<String> queryFailCauseTop(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     * 计划下的无人机飞行次数排行top5
     * @param statisticalAnalysisReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<Map> queryPlanUavFlyCountTop(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     * 任务状态统计
     * @param statisticalAnalysisReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    Map queryTaskStatusStatistics(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     * 飞行类别统计
     * @param statisticalAnalysisReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<Map> queryFlyTypeStatistics(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     * 飞行趋势,按周/月/年 统计各个时间段内飞行次数
     * @param statisticalAnalysisReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<Map> queryFlyTrend(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     * 起飞时刻统计,按天/周/月 统计各个时间段内飞行次数
     * @param statisticalAnalysisReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<Map> queryFlyTimeStatistical(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     * 任务详情
     * @param statisticalAnalysisReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<Map> queryTaskDetail(StatisticalAnalysisReq statisticalAnalysisReq);

    /**
     * 获取任务下的所有任务详情
     * @param
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    List<ExportStatisticalAnalysisWarningOrderRep> queryWarningOrder(
            @Param("tableName") String tableName, @Param("taskIdList") List<String> taskIdList,
            @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);
}
