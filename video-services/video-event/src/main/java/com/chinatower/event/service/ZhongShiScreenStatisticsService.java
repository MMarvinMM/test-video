package com.chinatower.event.service;

import com.chinatower.event.dto.zhongshi.ZhongShiDateRangeDTO;
import com.chinatower.event.vo.zhongshi.FunctionModuleStatsVO;
import com.chinatower.event.vo.zhongshi.LargeModelStatsVO;
import com.chinatower.event.vo.zhongshi.SmallModelStatsVO;

/**
 * 中试大屏验证统计服务
 */
public interface ZhongShiScreenStatisticsService {

    /**
     * 查询功能模块验证统计
     */
    FunctionModuleStatsVO queryFunctionModuleStats(ZhongShiDateRangeDTO req);

    /**
     * 查询小模型验证统计
     */
    SmallModelStatsVO querySmallModelStats(ZhongShiDateRangeDTO req);

    /**
     * 查询大模型验证统计
     */
    LargeModelStatsVO queryLargeModelStats(ZhongShiDateRangeDTO req);
}
