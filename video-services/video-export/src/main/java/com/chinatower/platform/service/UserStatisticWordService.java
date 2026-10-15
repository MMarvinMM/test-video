package com.chinatower.platform.service;

import com.chinatower.analysis.api.entity.CustomVO;

import javax.servlet.http.HttpServletResponse;


public interface UserStatisticWordService {

    void exportUserWordStatistics(CustomVO customVO, HttpServletResponse response);

}
