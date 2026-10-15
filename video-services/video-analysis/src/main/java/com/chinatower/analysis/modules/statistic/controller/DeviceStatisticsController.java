package com.chinatower.analysis.modules.statistic.controller;

import com.chinatower.analysis.api.entity.AnalysisVO;
import com.chinatower.analysis.modules.statistic.service.DeviceStatisticsService;
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
import java.util.List;
import java.util.Map;


/**
 * video-analysis
 * 设备统计入口
 * @author fzw
 * @version 1.0
 * @date 2023/4/8 19:45
 */
@Slf4j
@RestController
@Api(tags = "设备统计Controller")
public class DeviceStatisticsController extends TowerBaseController {

    @Resource
    DeviceStatisticsService deviceStatisticsService;


    /**
     * 摄像机数量区域排行(租户)
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "摄像机数量区域排行(租户)")
    @PostMapping("/device/getCameraNumByArea")
    public TableDataInfo getCameraNumByArea(@RequestBody AnalysisVO analysisVO) {
        List<Map<String,String>> deviceStatisticsVOS = deviceStatisticsService.getCameraNumByArea(analysisVO);
        return getDataTable(deviceStatisticsVOS);
    }

    /**
     * 摄像机订购算法排行
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "摄像机订购算法排行")
    @PostMapping("/device/getOrderAlgorithmRank")
    public TableDataInfo getOrderAlgorithmRank(@RequestBody AnalysisVO analysisVO) {
        List<Map<String,String>> deviceStatisticsVOS = deviceStatisticsService.getOrderAlgorithmRank(analysisVO);
        return getDataTable(deviceStatisticsVOS);
    }

    /**
     * 通道配置前端算法任务排行
     * 统计查询条件下用户权限范围内通道配置的前端算法数量。
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "通道配置前端算法任务排行")
    @PostMapping("/device/getForeAlgorithmTaskRank")
    public TableDataInfo getForeAlgorithmTaskRank(@RequestBody AnalysisVO analysisVO) {
        List<Map<String,String>> deviceStatisticsVOS = deviceStatisticsService.getForeAlgorithmTaskRank(analysisVO);
        return getDataTable(deviceStatisticsVOS);
    }

    /**
     * 通道配置后端算法任务排行
     * 统计查询条件下用户权限范围内通道配置的后端算法数量。
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "通道配置后端算法任务排行")
    @PostMapping("/device/getAfterAlgorithmTaskRank")
    public TableDataInfo getAfterAlgorithmTaskRank(@RequestBody AnalysisVO analysisVO) {
        List<Map<String,String>> deviceStatisticsVOS = deviceStatisticsService.getAfterAlgorithmTaskRank(analysisVO);
        return getDataTable(deviceStatisticsVOS);
    }

    /**
     * 通道启用前端算法任务排行
     * 统计查询条件下用户权限范围内通道启用前端算法任务数量。
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "通道启用前端算法任务排行")
    @PostMapping("/device/getStartForeAlgorithmTaskRank")
    public TableDataInfo getStartForeAlgorithmTaskRank(@RequestBody AnalysisVO analysisVO) {
        List<Map<String,String>> deviceStatisticsVOS = deviceStatisticsService.getStartForeAlgorithmTaskRank(analysisVO);
        return getDataTable(deviceStatisticsVOS);
    }

    /**
     * 通道启用后端算法任务排行
     * 统计查询条件下用户权限范围内通道启用的后端算法任务数量。
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "通道启用后端算法任务排行")
    @PostMapping("/device/getStartAfterAlgorithmTaskRank")
    public TableDataInfo getStartAfterAlgorithmTaskRank(@RequestBody AnalysisVO analysisVO) {
        List<Map<String,String>> deviceStatisticsVOS = deviceStatisticsService.getStartAfterAlgorithmTaskRank(analysisVO);
        return getDataTable(deviceStatisticsVOS);
    }

    /**
     * 算法任务数量统计
     * 统计启用前端算法任务总数量,通道启用后端算法任务总数量及后端算法任务总数量
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "算法任务数量统计")
    @PostMapping("/device/getAlgorithmTaskNum")
    public Result getAlgorithmTaskNum(@RequestBody AnalysisVO analysisVO) {
        return Result.data(deviceStatisticsService.getAlgorithmTaskNum(analysisVO));
    }

    /**
     * 算法名称列表
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "算法名称列表")
    @PostMapping("/device/getAlgorithmTaskList")
    public TableDataInfo getAlgorithmTaskList(@RequestBody AnalysisVO analysisVO) {
        List<Map<String, String>> algorithmTaskList = deviceStatisticsService.getAlgorithmTaskList(analysisVO);
        return getDataTable(algorithmTaskList);
    }

    /**
     * 告警事件中拥有订购摄像机数量
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "告警事件中拥有订购摄像机数量")
    @PostMapping("/device/getDeviceNumInEvent")
    public TableDataInfo getDeviceNumInEvent(@RequestBody AnalysisVO analysisVO) {
        List<Map> deviceStatisticsVOS = deviceStatisticsService.getDeviceNumInEvent(analysisVO);
        return getDataTable(deviceStatisticsVOS);
    }


}
