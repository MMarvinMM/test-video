package com.chinatower.analysis.modules.statistic.controller;

import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.modules.statistic.service.BigScreenService;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * video-analysis
 * 大屏统计分析
 */
@Slf4j
@RestController
@Api(tags = "大屏统计分析Controller")
public class BigScreenController {
    @Resource
    private BigScreenService bigScreenService;

    /**
     * 获取设备统计
     * @param vo
     * @return
     */
    @ApiOperation(value = "获取设备统计")
    @PostMapping("/statistic/getDevicesStatistics")
    public Result<List<DevicesDataVO>> getDevicesStatistics(@RequestBody QueryParamVO vo){
        return bigScreenService.getDevicesStatistics(vo);
    }

    /**
     * 行业-获取设备统计
     * @param vo
     * @return
     */
    @ApiOperation(value = "获取设备统计")
    @PostMapping("/industry/statistic/getDevicesStatistics")
    public Result<List<DevicesDataVO>> getIndustryDevicesStatistics(@RequestBody QueryParamVO vo){
        return bigScreenService.getDevicesStatistics(vo);
    }

    /**
     * 获取告警统计
     * @param vo
     * @return
     */
    @ApiOperation(value = "获取告警统计")
    @PostMapping("/statistic/getEventStatistics")
    public Result<List<EventDataVO>> getEventStatistics(@RequestBody QueryParamVO vo){
        return bigScreenService.getEventStatistics(vo);
    }

    /**
     * 行业-获取告警统计
     * @param vo
     * @return
     */
    @ApiOperation(value = "获取告警统计")
    @PostMapping("/industry/statistic/getEventStatistics")
    public Result<List<EventDataVO>> getIndustryEventStatistics(@RequestBody QueryParamVO vo){
        return bigScreenService.getEventStatistics(vo);
    }

    /**
     * 获取告警看板统计
     * 查询当月数据
     * @param vo
     * @return
     */
    @ApiOperation(value = "获取告警看板统计")
    @PostMapping("/statistic/getEventBoardStatistics")
    public Result<List<EventBoardDataVO>> getEventBoardStatistics(@RequestBody EventBoardParamVO vo){
        return bigScreenService.getEventBoardStatistics(vo);
    }

    /**
     * 行业-获取告警看板统计
     * 查询当月数据
     * @param vo
     * @return
     */
    @ApiOperation(value = "获取告警看板统计")
    @PostMapping("/industry/statistic/getEventBoardStatistics")
    public Result<List<EventBoardDataVO>> getIndustryEventBoardStatistics(@RequestBody EventBoardParamVO vo){
        return bigScreenService.getEventBoardStatistics(vo);
    }

    /**
     * 站点维度top10
     * @param vo
     * @return
     */
    @ApiOperation(value = "站点维度top10")
    @PostMapping("/statistic/getDeviceTopTen")
    public Result<List<DeviceTopTenDataVO>> getDeviceTopTen(@RequestBody @Valid DeviceTopTenParamVO vo){
        return bigScreenService.getDeviceTopTen(vo);
    }

    /**
     * 行业-站点维度top10
     * @param vo
     * @return
     */
    @ApiOperation(value = "站点维度top10")
    @PostMapping("/industry/statistic/getDeviceTopTen")
    public Result<List<DeviceTopTenDataVO>> getIndustryDeviceTopTen(@RequestBody @Valid DeviceTopTenParamVO vo){
        return bigScreenService.getDeviceTopTen(vo);
    }

    /**
     * 告警类型统计
     * @param vo
     * @return
     */
    @ApiOperation(value = "告警类型统计")
    @PostMapping("/statistic/getEventTypeStatistics")
    public Result<List<EventStatisticDataVO>> getEventTypeStatistics(@RequestBody @Valid EventStatisticParamVO vo){
        return bigScreenService.getEventTypeStatistics(vo);
    }

    /**
     * 国土SDK：查询告警地块面积
     * @param paramVO
     * @return
     */
    @ApiOperation(value = "告警地块面积统计")
    @PostMapping("/statistic/getEventPlotAreaStatistics")
    Result<List<Map<String, Object>>> getEventPlotAreaStatistics(@RequestBody EventBoardParamVO paramVO){
        return bigScreenService.getEventPlotAreaStatistics(paramVO);
    }
}
