package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.dto.AlarmType;
import com.chinatower.event.dto.StatisticsDTO;
import com.chinatower.event.dto.TodayAlarmRealtimeQueryDTO;
import com.chinatower.event.entity.vo.EventAlarmNumberVO;
import com.chinatower.event.entity.vo.TodayAlarmRealtimeStatVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dto.zhongshi.AlgorithmDto;
import com.chinatower.event.dto.zhongshi.StatisticsDto;

/**
 * @ClassName StatisticsMapper
 * @Description 统计分析
 * @Author chenshaowu
 * @Date 2022/2/16
 **/
@Mapper
@DS(EbikeDataBaseConstant.STATISTIC)
public interface StatisticsMapper {

    EventAlarmNumberVO getEventAlarmDayNum(@Param("param") StatisticsDTO statisticsDTO);

    EventAlarmNumberVO getEventAlarmYesterdayNum(@Param("param") StatisticsDTO statisticsDTO);

    EventAlarmNumberVO getEventAlarmNumYearMonth(@Param("param") StatisticsDTO statisticsDTO);

    List<EventAlarmNumberVO> getInitDevTypeYearMonth(@Param("param") StatisticsDTO statisticsDTO);

    List<Map> getAllEventAlarmNumYearMonth(@Param("param") StatisticsDTO statisticsDTO);

    List<EventAlarmNumberVO> getEventTrendYear(@Param("param") StatisticsDTO statisticsDTO);

    List<EventAlarmNumberVO> getEventTrendMonth(@Param("param") StatisticsDTO statisticsDTO);

    List<EventAlarmNumberVO> getEventTrendDay(@Param("param") StatisticsDTO statisticsDTO);

    @DS(EbikeDataBaseConstant.BASE)
    List<AlarmType> getTypeName(@Param("ids") List<String> ids);

    List<EventAlarmNumberVO> getThermDiagramNum(@Param("param") StatisticsDTO statisticsDTO);

    /**
     * 实时统计今日告警、今日待研判、今日真警（均限制 alarm_time 为今日）
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    TodayAlarmRealtimeStatVO countTodayAlarmStatRealtime(@Param("param") TodayAlarmRealtimeQueryDTO query);
}
