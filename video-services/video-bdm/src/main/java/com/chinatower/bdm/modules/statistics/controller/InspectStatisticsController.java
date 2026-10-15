package com.chinatower.bdm.modules.statistics.controller;

import com.chinatower.bdm.api.entity.InspectStatisticsVo;
import com.chinatower.bdm.api.entity.InspectionConfStatisticsVO;
import com.chinatower.bdm.modules.statistics.service.InspectStatisticsService;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.controller.TowerBaseController;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * video-bdm
 */
@Api(value = "巡查统计-Api",tags = "巡查统计后端接口")
@Slf4j
@RestController
public class InspectStatisticsController extends TowerBaseController {
    @Resource
    private InspectStatisticsService inspectStatisticsService;
    /**
     * 巡查统计总览
     */
    @ApiOperation(value = "巡查统计总览")
    @PostMapping("/statistics/inspectOverview")
    public Result inspectOverview(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        Map gridTotal = inspectStatisticsService.getInspectOverview(inspectStatisticsVo);
        return Result.data(gridTotal);
    }

    /**
     * 网格层级数量排行
     */
    @ApiOperation(value = "网格层级数量排行")
    @PostMapping("/statistics/getGridLevel")
    public TableDataInfo getGridLevel(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridLevel(inspectStatisticsVo);
    }

    /**
     * 网格性别统计
     */
    @ApiOperation(value = "网格性别统计")
    @PostMapping("/statistics/getGridKeeperGender")
    public Result getGridKeeperGender(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return Result.data(inspectStatisticsService.getGridKeeperGender(inspectStatisticsVo));
    }

    /**
     * 网格员年龄分布
     */
    @ApiOperation(value = "网格员年龄分布")
    @PostMapping("/statistics/getGridKeeperAge")
    public Result getGridKeeperAge(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return Result.data(inspectStatisticsService.getGridKeeperAge(inspectStatisticsVo));
    }

    /**
     * 网格统计
     */
    @ApiOperation(value = "网格统计")
    @PostMapping("/statistics/getGridStatistics")
    public TableDataInfo getGridStatistics(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridStatistics(inspectStatisticsVo);
    }

    /**
     * 网格出勤率排行
     */
    @ApiOperation(value = "网格出勤率排行")
    @PostMapping("/statistics/getGridAttendance")
    public TableDataInfo getGridAttendance(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridAttendance(inspectStatisticsVo);
    }

    /**
     * 网格长/网格员勤率排行
     */
    @ApiOperation(value = "网格长出勤率排行")
    @PostMapping("/statistics/getGridKeeperAttendance")
    public TableDataInfo getGridKeeperAttendance(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridKeeperAttendance(inspectStatisticsVo);
    }

    /**
     * 网格长/网格员勤率排行
     */
    @ApiOperation(value = "网格长出勤率排行")
    @PostMapping("/statistics/getGridKeeperAttendanceByTwo")
    public TableDataInfo getGridKeeperAttendanceByTwo(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridKeeperAttendanceByTwo(inspectStatisticsVo);
    }

    /**
     * 网格打卡状态统计
     */
    @ApiOperation(value = "网格打卡状态统计")
    @PostMapping("/statistics/getGridClockState")
    public TableDataInfo getGridClockState(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridClockState(inspectStatisticsVo);
    }

    /**
     * 网格员打卡状态统计
     */
    @ApiOperation(value = "网格员打卡状态统计")
    @PostMapping("/statistics/getGridKeeperClockState")
    public TableDataInfo getGridKeeperClockState(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridKeeperClockState(inspectStatisticsVo);
    }

    /**
     * 网格巡查任务数量排行
     */
    @ApiOperation(value = "网格巡查任务数量排行")
    @PostMapping("/statistics/getGridInspectTask")
    public TableDataInfo getGridInspectTask(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridInspectTask(inspectStatisticsVo);
    }

    /**
     * 网格任务完成率排行
     */
    @ApiOperation(value = "网格任务完成率排行")
    @PostMapping("/statistics/getGridInspectTaskCompletionRate")
    public TableDataInfo getGridInspectTaskCompletionRate(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridInspectTaskCompletionRate(inspectStatisticsVo);
    }

    /**
     * 根据网格员类型统计网格任务完成率
     */
    @ApiOperation(value = "根据网格员类型统计网格任务完成率")
    @PostMapping("/statistics/getGridTaskCompletionRateByType")
    public Result getGridTaskCompletionRateByType(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return Result.data(inspectStatisticsService.getGridTaskCompletionRateByType(inspectStatisticsVo));
    }

    /**
     * 根据网格员类型分页统计网格任务完成率
     */
    @ApiOperation(value = "根据网格员类型分页统计网格任务完成率")
    @PostMapping("/statistics/getGridTaskCompletionRateToPage")
    public TableDataInfo getGridTaskCompletionRateToPage(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridTaskCompletionRateToPage(inspectStatisticsVo);
    }

    /**
     * 巡查任务反馈端统计
     */
    @ApiOperation(value = "巡查任务反馈端统计")
    @PostMapping("/statistics/getGridInspectTaskBack")
    public TableDataInfo getGridInspectTaskBack(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridInspectTaskBack(inspectStatisticsVo);
    }

    /**
     * 巡查反馈24小时分布
     */
    @ApiOperation(value = "巡查反馈24小时分布")
    @PostMapping("/statistics/getGridInspectTaskBackByHour")
    public TableDataInfo getGridInspectTaskBackByHour(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridInspectTaskBackByHour(inspectStatisticsVo);
    }

    /**
     * 查询单个巡查反馈24小时分布
     */
    @ApiOperation(value = "查询单个巡查反馈24小时分布")
    @PostMapping("/statistics/getGridInspectTaskBackByHourById")
    public TableDataInfo getGridInspectTaskBackByHourById(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridInspectTaskBackByHourById(inspectStatisticsVo);
    }

    /**
     * 巡查任务按日期统计
     */
    @ApiOperation(value = "巡查任务按日期统计")
    @PostMapping("/statistics/getGridInspectTaskByDay")
    public TableDataInfo getGridInspectTaskByDay(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridInspectTaskByDay(inspectStatisticsVo);
    }

    /**
     * 查询单个巡查任务按日期统计
     */
    @ApiOperation(value = "查询单个巡查任务按日期统计")
    @PostMapping("/statistics/getGridInspectTaskByDayById")
    public TableDataInfo getGridInspectTaskByDayById(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridInspectTaskByDayById(inspectStatisticsVo);
    }

    /**
     * 巡查人任务数量排行
     */
    @ApiOperation(value = "巡查人任务数量排行")
    @PostMapping("/statistics/getGridInspectTaskByPerson")
    public TableDataInfo getGridInspectTaskByPerson(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridInspectTaskByPerson(inspectStatisticsVo);
    }

    /**
     * 巡查人任务完成率排行
     */
    @ApiOperation(value = "巡查人任务完成率排行")
    @PostMapping("/statistics/getGridInspectTaskRateByPerson")
    public TableDataInfo getGridInspectTaskRateByPerson(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return inspectStatisticsService.getGridInspectTaskRateByPerson(inspectStatisticsVo);
    }


    /**
     * 查询网格员信息
     */
    @ApiOperation(value = "查询网格员信息")
    @PostMapping("/statistics/getGridKeeperById")
    public Result getGridKeeperById(@RequestBody InspectStatisticsVo inspectStatisticsVo) {
        return Result.data(inspectStatisticsService.getGridKeeperById(inspectStatisticsVo));
    }

    @PostMapping("/statistics/getInspectionConf")
    public Result<InspectionConfStatisticsVO> getInspectionConf(@RequestBody InspectionConfStatisticsVO inspectionConfParam){
        return Result.data(inspectStatisticsService.getInspectionConf(inspectionConfParam));
    }

}
