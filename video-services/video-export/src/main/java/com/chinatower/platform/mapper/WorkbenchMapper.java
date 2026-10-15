package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.platform.entity.*;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface WorkbenchMapper {
    /**
     * 事件高发区域
     *
     * @param analysisVO
     * @return analysisVO（provinceId:省份编码 ）
     * @return analysisVO（cityId:市编码）
     * @return analysisVO（countyId:区县编码）
     */
    @DS(TableEnum.STATISTIC)
    List<Map> getEventNumByArea(AnalysisVO analysisVO);
    /**
     * 按年或月查询告警事件类型
     *
     * @param statisticsVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    @DS(TableEnum.STATISTIC)
    List<Map> getInitDevTypeYearMonth(StatisticsVO statisticsVO);

    List<Map> getAlarmType();

    String getAlarmTypeById(String typeId);

    String getIndustryNameById(String industryCode);

    /**
     * 今日事件高发时段
     *
     * @param analysisVO
     * @return
     */
    @DS(TableEnum.STATISTIC)
    List<Map> getEventNumByHour(AnalysisVO analysisVO);


    String getAlarmByCode(String areaCode);


    String getAlarmByLevel(String areaCode);
}
