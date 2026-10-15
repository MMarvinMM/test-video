package com.chinatower.platform.modules.statistics.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.DateVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.statistics.entity.DurationVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @className: TowerStatisticsDAO
 * @description: 告警统计（铁塔）
 * @author: csj
 * @date: 2022/6/20
 * @requestID 需求单号及需求名称
 */
@Mapper
public interface TowerStatisticsDAO {
    /**
     * 通道数量统计
     * @return
     */
    @DS("base")
    List<Map> getChannelStatistic();

    /**
     * 设备数量统计
     * @return
     */
    @DS("base")
    List<Map> getDevStatistic();

    /**
     * 今日告警数量统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventTodayNumTower(DateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventTodayNumTowers(DateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventNumTower(DateVO dateVO);

    /**
     * 告警数量批量新增
     * @param dateVO
     * @return
     */
    int deleteEventNum(DateVO dateVO);
    int insertEventNum(List<DurationVO> list);

    int deleteEventNums(DateVO dateVO);
    int insertEventNums(List<DurationVO> list);

    /**
     * 设备通道数量统计
     * @param dateVO
     * @return
     */
    int deleteDeviceStatistic(DateVO dateVO);

    int insertDeviceStatistic(List<Map> list);

    /**
     * 告警发生区域统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventArea(DateVO dateVO);

    int deleteEventArea(DateVO dateVO);

    int insertEventArea(List<Map> list);
    int insertEventAreaParam(List<Map> list);
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventDurationTower(DateVO dateVO);

    /**
     * 办结事件处理时长统计
     * @param dateVO
     * @return
     */
    int delEventDurationTower(DateVO dateVO);

    int addEventDurationTower(List<DurationVO> list);
    int addEventDurationTowerParam(List<DurationVO> list);
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventAreaHandle(DateVO dateVO);

    int deleteEventAreaHandle(DateVO dateVO);

    int insertEventAreaHandle(List<DurationVO> list);

    /**
     * 按小时统计告警数据
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventByHour(DateVO dateVO);
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventByHourNew(DateVO dateVO);

    int deleteEventByHour(DateVO dateVO);

    int insertEventByHour(List<Map> list);

    /**
     * 按小时统计告警数据（新）
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventByHourNews(DateVO dateVO);
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventByHourNewNews(DateVO dateVO);

    int deleteEventByHourNews(DateVO dateVO);

    int insertEventByHourNews(List<Map> list);

    /**
     * 按照小时统计压制数据
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumListByInterceptHour(DateVO dateVO);

    int deleteEventNumByInterceptHour(DateVO dateVO);

    int insertEventNumByInterceptHour(List<Map> list);

    /**
     * 按照小时统计压制数据（新）
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumListByInterceptHourNew(DateVO dateVO);

    int deleteEventNumByInterceptHourNew(DateVO dateVO);

    int insertEventNumByInterceptHourNew(List<Map> list);

    /**
     * 告警等级统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventLevel(DateVO dateVO);

    int deleteEventLevel(DateVO dateVO);

    int insertEventLevel(List<Map> list);

    /**
     * 告警每日维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumDay(DateVO dateVO);

    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumDays(DateVO dateVO);

    int delEventNumDay(DateVO dateVO);

    int insertEventNumDay(List<Map> list);

    int insertEventNumDays(List<Map> list);

    /**
     * 告警月度维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumMonth(DateVO dateVO);

    int delEventNumMonth(DateVO dateVO);

    int insertEventNumMonth(List<Map> list);

    int insertEventNumMonths(List<Map> list);

    /**
     * 告警年度维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<DurationVO> getEventNumYear(DateVO dateVO);

    int delEventNumYear(DateVO dateVO);

    int addEventNumYear(Map map);

    /**
     * 告警类型维度统计
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventType(DateVO dateVO);
    int delEventType(DateVO dateVO);
    int addEventType(List<Map> list);

    /**
     * 算法编码维度统计-铁塔视角
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventAlgorithm(DateVO dateVO);
    int delEventAlgorithm();
    int insertEventAlgorithm(List<Map> list);

    /**
     * 算法编码维度统计-铁塔视角（按入参日期刷历史）
     * <p>与日更 Job 分离：按 DateVO.handleDate（yyyy-MM / yyyy-MM-dd）过滤，支持月表 sub_scenes 拆行。</p>
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventAlgorithmByDate(DateVO dateVO);

    int delEventAlgorithmByDate(DateVO dateVO);

    int insertEventAlgorithmByDate(List<Map> list);

    /**
     * 告警压制数量查询、新增、删除
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getInterceptNum(DateVO dateVO);
    int deleteInterceptNum(DateVO dateVO);
    int insertInterceptNum(List<Map> list);

    /**
     * 查询设备数量
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getDeviceNum(DateVO dateVO);

    /**
     * 设备数量统计新增/删除
     * @param dateVO
     * @return
     */
    int deleteDeviceNum(DateVO dateVO);
    int insertDeviceNum(List<Map> list);

    /**
     * 查询租户信息
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<Map<String,String>> getTenants();

    /**
     * 查询网格等级
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<Map<String,String>> getGrids();

    /**
     * 查询昨日告警数量（铁塔）
     * @param dateVO
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<Map> getEventNumYesterdayListForTower(DateVO dateVO);

    /**
     * 昨日告警数量统计新增/删除
     * @param dateVO
     * @return
     */
    int delEventNumYesterdayTower(DateVO dateVO);
    int addEventNumYesterdayTower(List<Map> list);
}
