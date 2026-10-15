package com.chinatower.analysis.modules.statistic.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticDAO {
    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getAlarmType(StatisticsVO statisticsVO);

    @DS(EbikeDataBaseConstant.SLAVE)
    List<Map> getTags(StatisticsVO statisticsVO);

    List<Map> getEventStatisticalAnalysis(StatisticsVO statisticsVO);

    List<Map> getEventAnalysisByTag(StatisticsVO statisticsVO);

    Map getEventAlarmDayNum(StatisticsVO statisticsVO);

    Map getEventAlarmYesterdayNum(StatisticsVO statisticsVO);

    List<Map> getEventAlarmNumYearMonth(StatisticsVO statisticsVO);

    Map getEventHandleTimeNum(StatisticsVO statisticsVO);

    List<Map> getEventNumByHandle(AnalysisVO analysisVO);
    @DS(EbikeDataBaseConstant.SLAVE)
    String getAreaNameById(String areaId);
    @DS(EbikeDataBaseConstant.SLAVE)
    List<String> getAreaCodeByPCode(String parentCode);
}
