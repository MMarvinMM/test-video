package com.chinatower.platform.service;

import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.common.entity.Result;

public interface WorkbenchService {
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
    public Result getDevTypeYearMonth(StatisticsVO statisticsVO);

    /**
     * 今日事件高发时段
     *
     * @param analysisVO
     * @return Result
     * @throws
     */
    public Result getEventNumByHour(AnalysisVO analysisVO);

    /**
     * 导出事件高发区域
     *
     * @param analysisVO
     * @return analysisVO（provinceId:省份编码 ）
     * @return analysisVO（cityId:市编码）
     * @return analysisVO（countyId:区县编码）
     */
    public Result exportGetEventNumByArea(AnalysisVO analysisVO);



}
