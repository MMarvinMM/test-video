package com.chinatower.platform.service;

import com.chinatower.bdm.api.entity.InspectStatisticsVo;

import javax.servlet.http.HttpServletResponse;

public interface InspectExportStatisticsService {

    public void exportInspectStatistics(InspectStatisticsVo inspectStatisticsVo, HttpServletResponse response);



}
