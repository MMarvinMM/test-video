package com.chinatower.platform.service;

import com.chinatower.bdm.api.entity.InspectStatisticsVo;

import javax.servlet.http.HttpServletResponse;

public interface InspectExportWordStatisticsService {

    public void exportWordInspectStatistics(InspectStatisticsVo inspectStatisticsVo, HttpServletResponse response);



}
