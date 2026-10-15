package com.chinatower.platform.controller;

import com.chinatower.common.constant.ProviderUrlConstant;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.platform.dto.LogStatisticsQuery;
import com.chinatower.platform.res.*;
import com.chinatower.platform.service.LogStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * tower-log
 * @desription: 客户登录统计
 * @data 创建时间：2023/3/28 15:59
 * @author chuan hao
 * @version 1.0
 *
 */
@Api(tags = "登录统计")
@RestController
@RequestMapping(ProviderUrlConstant.PROVIDER + "/statistics")
public class LogStatisticsController {

    @Resource
    LogStatisticsService logStatisticsService;

    /**
     * 客户端统计
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "客户端统计")
    @GetMapping("clientTypeRes")
    public Map<String, List<ClientTypeRes>> clientTypeRes(LogStatisticsQuery dto) {
        return logStatisticsService.clientTypeRes(dto);
    }

    /**
     * 用户登录统计
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "用户登录统计")
    @GetMapping("userLoginRes")
    public TableDataInfo userLoginRes(LogStatisticsQuery dto) {
        return logStatisticsService.userLoginRes(dto);
    }

    /**
     * 租户登录次数排行
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "租户登录次数排行")
    @GetMapping("tenantLoginCountRes")
    public TableDataInfo tenantLoginCountRes(LogStatisticsQuery dto) {
        return logStatisticsService.tenantLoginCountRes(dto);
    }

    /**
     * 24小时分布
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "24小时分布")
    @GetMapping("hoursRes")
    public List<HoursRes> hoursRes(LogStatisticsQuery dto) {
        return logStatisticsService.hoursRes(dto);
    }

    /**
     * PC系统统计
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "PC系统统计")
    @GetMapping("pcOsRes")
    public List<PcOsRes> pcOsRes(LogStatisticsQuery dto) {
        return logStatisticsService.pcOsRes(dto);
    }

    /**
     * PC浏览器统计
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "PC浏览器统计")
    @GetMapping("pcBrowserRes")
    public TableDataInfo pcBrowserRes(LogStatisticsQuery dto) {
        return logStatisticsService.pcBrowserRes(dto);
    }

    /**
     * APP系统统计
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "APP系统统计")
    @GetMapping("appOsRes")
    public List<PcOsRes> appOsRes(LogStatisticsQuery dto) {
        return logStatisticsService.appOsRes(dto);
    }

    /**
     * APP型号统计
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "APP型号统计")
    @GetMapping("appModelRes")
    public TableDataInfo appModelRes(LogStatisticsQuery dto) {
        return logStatisticsService.appModelRes(dto);
    }

    /**
     * APP版本统计
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "APP版本统计")
    @GetMapping("appVersionRes")
    public TableDataInfo appVersionRes(LogStatisticsQuery dto) {
        return logStatisticsService.appVersionRes(dto);
    }

    /**
     * 登录状态统计
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "登录状态统计")
    @GetMapping("loginStatusRes")
    public List<LoginStatusRes> loginStatusRes(LogStatisticsQuery dto) {
        return logStatisticsService.loginStatusRes(dto);
    }

    /**
     * 失败原因统计
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "失败原因统计")
    @GetMapping("failureReasonRes")
    public List<FailureReasonRes> failureReasonRes(LogStatisticsQuery dto) {
        return logStatisticsService.failureReasonRes(dto);
    }

    /**
     * 租户成功率排行
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "租户成功率排行")
    @GetMapping("tenantSuccessRatioRes")
    public TableDataInfo tenantSuccessRatioRes(LogStatisticsQuery dto) {
        return logStatisticsService.tenantSuccessRatioRes(dto);
    }
}
