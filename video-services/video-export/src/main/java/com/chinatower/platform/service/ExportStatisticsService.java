package com.chinatower.platform.service;

import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.CustomVO;
import com.chinatower.analysis.api.entity.StatisticsVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ExportStatisticsService {

    public void exportEventStatistics(StatisticsVO statisticsVO, HttpServletResponse response);

    public void exportDeviceStatistics(AnalysisVO analysisVO, HttpServletResponse response);

    public void exportUserStatistics(CustomVO customVO, HttpServletResponse response);

    public void handleName(List<Map> mapList);



}
