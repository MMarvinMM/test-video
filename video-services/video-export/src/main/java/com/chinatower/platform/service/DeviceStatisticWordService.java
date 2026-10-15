package com.chinatower.platform.service;

import com.chinatower.analysis.api.entity.AnalysisVO;

import javax.servlet.http.HttpServletResponse;


public interface DeviceStatisticWordService {

    public void exportDeviceWordStatistics(AnalysisVO analysisVO, HttpServletResponse response);

}
