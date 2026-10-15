package com.chinatower.platform.modules.statistics.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.modules.statistics.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @className: StatisticsDAO
 * @description: 告警统计 Dao
 * @author: yongqi.han
 * @date: 2022/6/20
 * @requestID 需求单号及需求名称
 */
@Mapper
public interface StatisticsDAO {
    /**
     * 告警发生区域统计
     * @param dateVO
     * @return
     */
    int deleteEventAreaByTenant(DateVO dateVO);

    int insertEventAreaByTenant(List<Map> list);
    int insertEventAreaByTenantParam(List<Map> list);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventAreaListByTenant(DateVO dateVO);
    @DS(EbikeDataBaseConstant.BASE)
    List<UavFlightTaskVO> getUavFlightTask(DateVO dateVO);


    /**
     * 告警等级统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventLevelListByTenant(DateVO dateVO);

    @DS(EbikeDataBaseConstant.BASE)
    List<SysUser> getAllUser();

    int deleteEventLevelByTenant(DateVO dateVO);

    int insertEventLevelByTenant(List<Map> list);

    /**
     * 今日告警数量统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventTodayNumByTenant(DateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventTodayNumByTenants(DateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventNumByTenant(DateVO dateVO);

    /**
     * 工作台事件数量统计
     * @param dateVO
     * @return
     */
    int deleteEventNumByTenant(DateVO dateVO);

    int deleteEventNumByTenants(DateVO dateVO);

    int insertEventNumByTenant(List<DurationVO> list);

    int insertEventNumByTenants(List<DurationVO> list);

    /**
     * 按照小时统计数据
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumListByHour(DateVO dateVO);

    /**
     * 按照小时统计数据（新）
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumListByHourNew(DateVO dateVO);

    int deleteEventNumByHour(DateVO dateVO);

    int insertEventNumByHour(List<Map> list);

    /**
     * 按照小时统计数据（新）
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumListByHourNews(DateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumListByHourNewNews(DateVO dateVO);

    int deleteEventNumByHourNews(DateVO dateVO);

    int insertEventNumByHourNews(List<Map> list);


    /**
     * 按小时统计压制数据
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumListByInterceptHour(DateVO dateVO);

    int deleteEventNumByInterceptHour(DateVO dateVO);

    int insertEventNumByInterceptHour(List<Map> list);

    /**
     * 按小时统计压制数据（新）
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumListByInterceptHourNew(DateVO dateVO);

    int deleteEventNumByInterceptHourNew(DateVO dateVO);

    int insertEventNumByInterceptHourNew(List<Map> list);

    /**
     * 办结事件处理时长统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventDurationTenant(DateVO dateVO);

    int delEventDurationTenant(DateVO dateVO);

    int addEventDurationTenant(List<DurationVO> list);
    int addEventDurationTenantParam(List<DurationVO> list);

    /**
     * 告警每日维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumDayListForTenant(DateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumDayListForTenants(DateVO dateVO);

    int delEventNumDayTenant(DateVO dateVO);

    int addEventNumDayTenant(List<Map> list);

    int addEventNumDayTenants(List<Map> list);

    /**
     * 告警每日维度统计-人工上报
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumDayListForTenantManual(DateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumDayListForTenantsManual(DateVO dateVO);

    int delEventNumDayTenantManual(DateVO dateVO);

    int addEventNumDayTenantManual(List<Map> list);

    int addEventNumDayTenantsManual(List<Map> list);

    /**
     * 告警月度维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumMonthListForTenant(DateVO dateVO);

    /**
     * 告警标签维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<WarningOrderTagVo> getEventNumTagListForTenant(DateVO dateVO);

    int delEventNumMonthTenant(DateVO dateVO);

    int addEventNumMonthTenant(List<Map> list);

    int addEventNumMonthTenants(List<Map> list);

    /**
     * 告警年度维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventNumYearTenant(DateVO dateVO);

    int delEventNumYearTenant(DateVO dateVO);

    int addEventNumYearTenant(Map map);

    /**
     * 告警类型维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventTypeForTenant(DateVO dateVO);

    int delEventTypeTenant(DateVO dateVO);

    int addEventTypeTenant(List<Map> list);

    /**
     * 方案 A：按 sub_scenes 拆行统计客户下游任务维度
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventSubSceneForTenant(DateVO dateVO);

    int delEventSubSceneTenant(DateVO dateVO);

    int addEventSubSceneTenant(List<Map> list);

    /**
     * 方案 A 扩展：下游 × 小时告警（月表 sub_scenes 拆行）
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventHourSubSceneForTenant(DateVO dateVO);

    int delEventHourSubSceneTenant(DateVO dateVO);

    int addEventHourSubSceneTenant(List<Map> list);

    /**
     * 方案 A 扩展：下游 × 日压制（拦截表 JOIN 月表 sub_scenes）
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getInterceptSubSceneForTenant(DateVO dateVO);

    int delInterceptSubSceneTenant(DateVO dateVO);

    int addInterceptSubSceneTenant(List<Map> list);

    /**
     * 方案 A 扩展：下游 × 小时压制（拦截表 JOIN 月表 sub_scenes）
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getInterceptHourSubSceneForTenant(DateVO dateVO);

    int delInterceptHourSubSceneTenant(DateVO dateVO);

    int addInterceptHourSubSceneTenant(List<Map> list);

    /**
     * 告警区域处理时长维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventAreaHandleForTenant(DateVO dateVO);

    int deleteEventAreaHandleByTenant(DateVO dateVO);

    int insertEventAreaHandleByTenant(List<DurationVO> list);

    /**
     * 设备通道数量统计
     * @param localNode
     * @return
     */
    @DS("base")
    List<Map> getChannelStatisticTenant(String localNode);

    /**
     * 设备数量统计
     * @param localNode
     * @return
     */
    @DS("base")
    List<Map> getDevStatisticTenant(String localNode);

    int deleteDeviceStatisticTenant(DateVO dateVO);

    /**
     * 按照租户沉淀数据
     * @return
     */
    int insertDeviceStatisticTenant(List<Map> list);

    /**
     * 按告警来源沉淀数据
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<EventSourceVo> getEventSourceListForTenant(DateVO dateVO);
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<EventSourceVo> getEventSourceListForTower(DateVO dateVO);

    int delEventSourceTenant(DateVO dateVO);

    int addEventSourceTenant(List<EventSourceVo> list);

    int addEventSourceTenantForParams(List<EventSourceVo> list);

    int delEventSourceTower(DateVO dateVO);

    int addEventSourceTower(List<EventSourceVo> list);

    int addEventSourceTowerForParams(List<EventSourceVo> list);

    /**
     * 获取告警数量-按照行业版本与告警状态维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<OrderNumVO> getEventNumListNew(DateVO dateVO);

    int deleteEventNumNew();

    int insertEventNumNew(List<OrderNumVO> list);

    /**
     * 告警每日维度统计(新)
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumDayListForTenantNew(DateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<EventSourceVo> getEventNumDayListForTenantNews(DateVO dateVO);

    int delEventNumDayTenantNew(DateVO dateVO);

    int addEventNumDayTenantNew(List<Map> list);

    int addEventNumDayTenantNews(List<EventSourceVo> list);

    /**
     * 告警标签维度统计
     * @param dateVO
     * @return
     */
    int delEventTagTenant(DateVO dateVO);

    int addEventTagTenant(List<WarningOrderTagVo> list);

    int delEventTagTenantTower(DateVO dateVO);

    int addEventTagTenantTower(List<WarningOrderTagVo> list);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<WarningOrderTagVo> getEventNumTagListForTenantTower(DateVO dateVO);

    /**
     * 根据id查询告警标签
     * @param x
     * @return
     */
    @DS("base")
    List<String> queryTagId(WarningOrderTagVo x);

    /**
     * 根据id查询告警标签-铁塔视角
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> queryTagIdTT(DateVO dateVO);

    /**
     * 告警压制事件统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getInterceptNum(DateVO dateVO);

    int deleteInterceptNum(DateVO dateVO);

    int insertInterceptNum(List<Map> list);

    /**
     * 告警压制事件统计(新)
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getInterceptNumNew(DateVO dateVO);

    int deleteInterceptNumNew(DateVO dateVO);

    int insertInterceptNumNew(List<Map> list);


    /**
     * 查询告警类型
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<Map<String,String>> getAlarmType();

    /**
     * 下游任务编码→名称（sys_scene_alarm_rel），供铁塔 *_tt 沉淀 typeName。
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<Map<String, String>> getSubSceneAlarmTypeNames();

    /**
     * 大模型场景告警类型编码（sys_scene_config.type_value，与工单 warning_type_id 同命名空间）。
     * 铁塔类型/算法沉淀须剔除，避免准确率/算法统计展示场景维度。
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<String> getSceneIds();

    /**
     * 下游任务编码（sys_scene_alarm_rel.alarm_type）。
     * 与场景 type_value 可重叠，剔除场景时须保留这些编码，避免下游统计被误删。
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<String> getDownstreamAlarmTypeIds();


    @DS(EbikeDataBaseConstant.BASE)
    List<Map<String,String>> getAlarmTypeByValue(List<String> valueList);


    /**
     * 昨日告警每日维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumYesterdayListForTenant(DateVO dateVO);

    int delEventNumYesterdayTenant(DateVO dateVO);

    int addEventNumYesterdayTenant(List<Map> list);

    /**
     * 摄像机维度告警事件统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getDeviceNum(DateVO dateVO);

    int deleteDeviceNum(DateVO dateVO);

    int insertDeviceNum(List<Map> list);

    /**
     * 用户数量按组织关系统计
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<Map> getUserNumByDept();

    int deleteUserNumByDept();

    int insertUserNumByDept(List<Map> list);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<StatisticalAnalysisWarningOrderVO> queryMonthAlarmData(@Param("tableName") String tableName,
                                                                @Param("taskIdList") List<String> taskIdList);
}

