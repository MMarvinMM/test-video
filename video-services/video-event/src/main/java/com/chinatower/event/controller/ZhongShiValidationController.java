package com.chinatower.event.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.zhongshi.ZhongShiDateRangeDTO;
import com.chinatower.event.service.ZhongShiScreenStatisticsService;
import com.chinatower.event.vo.zhongshi.FunctionModuleStatsVO;
import com.chinatower.event.vo.zhongshi.LargeModelStatsVO;
import com.chinatower.event.vo.zhongshi.SmallModelStatsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Calendar;
import java.util.Objects;

/**
 * 中试验证统计查询控制层
 */
@RestController
@Slf4j
@RequestMapping("/zhongshi/validation")
public class ZhongShiValidationController {

    @Autowired
    private ZhongShiScreenStatisticsService zhongShiScreenStatisticsService;

    /**
     * 功能模块统计
     */
    @PostMapping("/queryFunctionModuleStats")
    public Result<FunctionModuleStatsVO> queryFunctionModuleStats(@RequestBody(required = false) ZhongShiDateRangeDTO req) {
        req = fillDefaultDateRange(req);
        log.info("查询功能模块统计，入参: {}", req);
        return Result.data(zhongShiScreenStatisticsService.queryFunctionModuleStats(req));
    }

    /**
     * 小模型统计（已完成/进行中）
     */
    @PostMapping("/querySmallModelStats")
    public Result<SmallModelStatsVO> querySmallModelStats(@RequestBody(required = false) ZhongShiDateRangeDTO req) {
        req = fillDefaultDateRange(req);
        log.info("查询小模型统计，入参: {}", req);
        return Result.data(zhongShiScreenStatisticsService.querySmallModelStats(req));
    }

    /**
     * 大模型统计（父子表）
     */
    @PostMapping("/queryLargeModelStats")
    public Result<LargeModelStatsVO> queryLargeModelStats(@RequestBody(required = false) ZhongShiDateRangeDTO req) {
        req = fillDefaultDateRange(req);
        log.info("查询大模型统计，入参: {}", req);
        return Result.data(zhongShiScreenStatisticsService.queryLargeModelStats(req));
    }

    /**
     * 如果日期参数为空，默认查询最近一周
     */
    private ZhongShiDateRangeDTO fillDefaultDateRange(ZhongShiDateRangeDTO req) {
        if (Objects.isNull(req)) {
            req = new ZhongShiDateRangeDTO();
        }
        if (req.getEndDate() == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            req.setEndDate(cal.getTime());
        }
        if (req.getStartDate() == null) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -6);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            req.setStartDate(cal.getTime());
        }
        return req;
    }
}
