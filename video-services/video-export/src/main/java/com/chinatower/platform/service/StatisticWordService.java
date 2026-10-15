package com.chinatower.platform.service;

import com.chinatower.analysis.api.entity.StatisticsVO;

import javax.servlet.http.HttpServletResponse;


public interface StatisticWordService {

    public void exportEventWordStatistics(StatisticsVO statisticsVO, HttpServletResponse response);

}
