package com.chinatower.platform.modules.statistics.service;

import com.chinatower.analysis.api.entity.DateVO;

import java.util.List;
import java.util.Map;

/**
 * @className: UserStatisticsService
 * @description: 用户统计
 * @author: lyy
 * @date: 2023/04/07
 * @requestID 需求单号及需求名称
 */
public interface UserStatisticsService {

    /**
     * 用户登录24小时分布统计
     * @param dateVO
     * @return
     */
    List<Map> getEventUserStatistics(DateVO dateVO);
    Boolean insertEventUserStatistics(DateVO dateVO, List<Map> list);

    /**
     * 用户登录时间统计
     * @param dateVO
     * @return
     */
    List<Map> getEventCustomListForType(DateVO dateVO);
    Boolean addEventCustomListForType(DateVO dateVO, List<Map> list);

    /**
     * 用户登录次数统计
     * @param dateVO
     * @return
     */
    List<Map> getEventCustomLoginCounts(DateVO dateVO);
    Boolean addEventCustomLoginCounts(DateVO dateVO, List<Map> list);

    /**
     * PC操作系统
     * @param dateVO
     * @return
     */
    List<Map> getPcSystemCounts(DateVO dateVO);
    Boolean addPcSystemCounts(DateVO dateVO, List<Map> list);

    /**
     * PC浏览器排行
     * @param dateVO
     * @return
     */
    List<Map> getPcBrowserCounts(DateVO dateVO);
    Boolean addPcBrowserCounts(DateVO dateVO, List<Map> list);

    /**
     * APP操作系统
     * @param dateVO
     * @return
     */
    List<Map> getAppSystemCounts(DateVO dateVO);
    Boolean addAppSystemCounts(DateVO dateVO, List<Map> list);

    /**
     * APP手机型号排行
     * @param dateVO
     * @return
     */
    List<Map> getIphoneModelCounts(DateVO dateVO);
    Boolean addIphoneModelCounts(DateVO dateVO, List<Map> list);

    /**
     * APP版本排行
     * @param dateVO
     * @return
     */
    List<Map> getAppVersionCounts(DateVO dateVO);
    Boolean addAppVersionCounts(DateVO dateVO, List<Map> list);

    /**
     * 用户登录成功率分析
     * @param dateVO
     * @return
     */
    List<Map> getSuccessRateCounts(DateVO dateVO);
    Boolean addSuccessRateCounts(DateVO dateVO, List<Map> list);

    /**
     * 用户登录失败原因分析
     * @param dateVO
     * @return
     */
    List<Map> getFailCauseCounts(DateVO dateVO);
    Boolean addFailCauseCounts(DateVO dateVO, List<Map> list);

    /**
     * 通过redis统计用户登录时长
     *
     * @return 是否成功
     */
    boolean statisticUserLoginDurationByRedis();

}
