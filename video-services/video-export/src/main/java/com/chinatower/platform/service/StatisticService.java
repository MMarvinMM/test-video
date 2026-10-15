package com.chinatower.platform.service;

import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.api.entity.CustomVO;
import com.chinatower.analysis.api.entity.StatisticsVO;
import com.chinatower.common.entity.Result;

import java.util.List;
import java.util.Map;

public interface StatisticService {


    /**
     * 通道算法任务排行
     */
    List<Map<String,String>> getAllAlgorithmTaskRank(AnalysisVO analysisVO);

    /**
     * 用户登录时长及次数排行
     */
    List<Map> getLoginDurationRankAndNum(CustomVO customVO);

}
