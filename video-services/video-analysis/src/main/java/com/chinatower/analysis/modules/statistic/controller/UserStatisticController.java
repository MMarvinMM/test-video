package com.chinatower.analysis.modules.statistic.controller;

import com.chinatower.analysis.api.entity.CustomVO;
import com.chinatower.analysis.modules.statistic.service.UserStatisticService;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;

/**
 * video-analysis
 */
@Slf4j
@RestController
@Api(tags = "统计分析-用户统计Controller")
public class UserStatisticController extends TowerBaseController {
    @Resource
    private UserStatisticService userStatisticService;

    /**
     * 用户总览
     * @param customVO
     * @return
     */
    @ApiOperation(value = "用户总览")
    @PostMapping("/statistic/getCustomNum")
    public Result getCustomNum(@RequestBody CustomVO customVO) {
        return userStatisticService.getCustomNum(customVO);
    }

    /**
     * 用户数量按组织排行
     * @param customVO
     * @return
     */
    @ApiOperation(value = "用户数量按组织排行")
    @PostMapping("/statistic/getCustomNumByDept")
    public TableDataInfo getCustomNumByDept(@RequestBody CustomVO customVO) {
        return userStatisticService.getCustomNumByDept(customVO);
    }

    /**
     * 用户登录时间统计
     * @param customVO
     * @return
     */
    @ApiOperation(value = "用户登录时间统计")
    @PostMapping("/statistic/getCustomNumByLoginTime")
    public TableDataInfo getCustomNumByLoginTime(@RequestBody CustomVO customVO) {
        return getDataTable(userStatisticService.getCustomNumByLoginTime(customVO));
    }

    /**
     * 用户登录次数排行
     * @param customVO
     * @return
     */
    @ApiOperation(value = "用户登录次数排行")
    @PostMapping("/statistic/getLoginNumRank")
    public TableDataInfo getLoginNumRank(@RequestBody CustomVO customVO) {
        return userStatisticService.getLoginNumRank(customVO);
    }

    /**
     * 用户登录时长排行
     * @param customVO
     * @return
     */
    @ApiOperation(value = "用户登录时长排行")
    @PostMapping("/statistic/getLoginDurationRank")
    public TableDataInfo getLoginDurationRank(@RequestBody CustomVO customVO) {
        return userStatisticService.getLoginDurationRank(customVO);
    }

    /**
     * 用户登录24小时分布统计
     * @param customVO
     * @return
     */
    @ApiOperation(value = "用户登录24小时分布统计")
    @PostMapping("/statistic/getLoginNumByHour")
    public TableDataInfo getLoginNumByHour(@RequestBody CustomVO customVO) {
        return getDataTable(userStatisticService.getLoginNumByHour(customVO));
    }

    /**
     * PC操作系统
     * @param customVO
     * @return
     */
    @ApiOperation(value = "PC操作系统")
    @PostMapping("/statistic/getLoginTypeByPcOs")
    public TableDataInfo getLoginTypeByPcOs(@RequestBody CustomVO customVO) {
        return userStatisticService.getLoginTypeByPcOs(customVO);
    }

    /**
     * PC浏览器排行
     * @param customVO
     * @return
     */
    @ApiOperation(value = "PC浏览器排行")
    @PostMapping("/statistic/getLoginTypeByPcBrower")
    public TableDataInfo getLoginTypeByPcBrower(@RequestBody CustomVO customVO) {
        return userStatisticService.getLoginTypeByPcBrower(customVO);
    }

    /**
     * APP操作系统
     * @param customVO
     * @return
     */
    @ApiOperation(value = "APP操作系统")
    @PostMapping("/statistic/getLoginTypeByAppOs")
    public TableDataInfo getLoginTypeByAppOs(@RequestBody CustomVO customVO) {
        return userStatisticService.getLoginTypeByAppOs(customVO);
    }

    /**
     * APP手机型号排行
     * @param customVO
     * @return
     */
    @ApiOperation(value = "APP手机型号排行")
    @PostMapping("/statistic/getLoginTypeByPhoneModel")
    public TableDataInfo getLoginTypeByPhoneModel(@RequestBody CustomVO customVO) {
        return userStatisticService.getLoginTypeByPhoneModel(customVO);
    }

    /**
     * APP版本排行
     * @param customVO
     * @return
     */
    @ApiOperation(value = "APP版本排行")
    @PostMapping("/statistic/getLoginTypeByAppVersion")
    public TableDataInfo getLoginTypeByAppVersion(@RequestBody CustomVO customVO) {
        return userStatisticService.getLoginTypeByAppVersion(customVO);
    }

    /**
     * 登录成功率分析
     * @param customVO
     * @return
     */
    @ApiOperation(value = "登录成功率分析")
    @PostMapping("/statistic/getLoginSuccessRate")
    public TableDataInfo getLoginSuccessRate(@RequestBody CustomVO customVO) {
        return getDataTable(userStatisticService.getLoginSuccessRate(customVO));
    }

    /**
     * 登录失败原因分析
     * @param customVO
     * @return
     */
    @ApiOperation(value = "登录失败原因分析")
    @PostMapping("/statistic/getLoginFailureRate")
    public TableDataInfo getLoginFailureRate(@RequestBody CustomVO customVO) {
        return getDataTable(userStatisticService.getLoginFailureRate(customVO));
    }

    /**
     * 用户登录方式分析（提供给APP）
     * @param customVO
     * @return
     */
    @ApiOperation(value = "用户登录方式分析（提供给APP）")
    @PostMapping("/statistic/getCustomNumByLoginStyle")
    public TableDataInfo getCustomNumByLoginStyle(@RequestBody CustomVO customVO) {
        return getDataTable(userStatisticService.getCustomNumByLoginStyle(customVO));
    }

    /**
     * APP端操作系统分析（提供给APP）
     * @param customVO
     * @return
     */
    @ApiOperation(value = "APP端操作系统分析（提供给APP）")
    @PostMapping("/statistic/getAppSystemRate")
    public TableDataInfo getAppSystemRate(@RequestBody CustomVO customVO) {
        return getDataTable(userStatisticService.getAppSystemRate(customVO));
    }
}
