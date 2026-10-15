package com.chinatower.platform.service;

import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.dto.LogStatisticsQuery;
import com.chinatower.platform.res.*;

import java.util.List;
import java.util.Map;

public interface LogStatisticsService {
    /**
     * 客户端统计
     */
    Map<String, List<ClientTypeRes>> clientTypeRes(LogStatisticsQuery dto);

    /**
     * 用户登录统计
     */
    TableDataInfo userLoginRes(LogStatisticsQuery dto);


    /**
     * 租户登录次数排行
     */
    TableDataInfo tenantLoginCountRes(LogStatisticsQuery dto);

    /**
     * 24小时登录统计
     */
    List<HoursRes> hoursRes(LogStatisticsQuery dto);

    /**
     * PC系统统计
     */
    List<PcOsRes> pcOsRes(LogStatisticsQuery dto);
    /**
     * PC浏览器统计
     */
    TableDataInfo pcBrowserRes(LogStatisticsQuery dto);

    /**
     * APP系统统计
     */
    List<PcOsRes> appOsRes(LogStatisticsQuery dto);
    /**
     * APP型号统计
     */
    TableDataInfo appModelRes(LogStatisticsQuery dto);
    /**
     * APP版本统计
     */
    TableDataInfo appVersionRes(LogStatisticsQuery dto);

    /**
     * 登录状态统计
     */
    List<LoginStatusRes> loginStatusRes(LogStatisticsQuery dto);
    /**
     * 失败原因统计
     */
    List<FailureReasonRes> failureReasonRes(LogStatisticsQuery dto);

    /**
     * 租户成功率排行
     */
    TableDataInfo tenantSuccessRatioRes(LogStatisticsQuery dto);
}
