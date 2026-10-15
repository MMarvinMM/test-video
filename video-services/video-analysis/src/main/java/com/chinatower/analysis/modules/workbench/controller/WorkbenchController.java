package com.chinatower.analysis.modules.workbench.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.analysis.api.entity.*;
import com.chinatower.analysis.modules.statistic.service.DeviceStatisticsService;
import com.chinatower.analysis.modules.workbench.service.WorkbenchService;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * video-analysis
 * 工作台统计控制层
 *
 * @author zgq
 * @since 2022-08-03
 */
@Slf4j
@RestController
@Api(tags = "工作台统计Controller")
public class WorkbenchController extends TowerBaseController {
    @Resource
    private WorkbenchService workbenchService;

    @Resource
    DeviceStatisticsService deviceStatisticsService;

    /**
     * 获取事件统计数量
     *
     * @param analysisVO (create_time 创建时间)
     * @return EventNumVO (todayAll 今日事件总数)
     * @return EventNumVO (todayProcess 今日处理中数量)
     * @return EventNumVO (todayComplete 今日已完结数量)
     */
    @ApiOperation(value = "获取事件统计数量")
    @PostMapping("/workbench/getEventNum")
    public Result<EventNumVO> getEventNum(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNum(analysisVO);
    }

    /**
     * 获取事件统计数量
     *
     * @param analysisVO
     */
    @ApiOperation(value = "获取事件统计数量")
    @PostMapping("/workbench/getEventNumApp")
    public Result<EventNumVO> getEventNumApp(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumApp(analysisVO);
    }

    /**
     * 获取事件统计数量
     *
     * @param statisticsVO
     */
    @ApiOperation(value = "获取摄像机下事件统计数量")
    @PostMapping("/workbench/getEventNumDevApp")
    public TableDataInfo getEventNumDevApp(@RequestBody StatisticsVO statisticsVO) {
        return getDataTable(workbenchService.getEventNumDevApp(statisticsVO));
    }

    /**
     * 获取事件统计数量
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "获取事件统计数量(行业)")
    @PostMapping("/industry/workbench/getEventNum")
    public Result<EventNumVO> getIndustryEventNum(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNum(analysisVO);
    }

    /**
     * 统计监控设备数量
     * @param analysisVO (year 年 month 月 day 日)
     *
     * @return deviceCountTotal 摄像机总数
     * @return devCountOnline 摄像机在线数
     * @return devCountOffline 摄像机离线数
     * @return channelCountTotal 通道总数
     * @return channelCountOnline   通道在线数
     * @return channelCountOffline 通道离线数
     */
    @ApiOperation(value = "统计监控设备数量")
    @PostMapping("/workbench/getDeviceStatistics")
    public Result<DeviceStatisticsVO> getDeviceStatistics(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getDeviceStatistics(analysisVO);
    }

    /**
     * 统计无人机设备数量
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计无人机设备数量")
    @PostMapping("/workbench/getUavDeviceStatistics")
    public Result<UavDeviceStatisticsVo> getUavDeviceStatistics(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getUavDeviceStatistics(analysisVO);
    }

    /**
     * 统计无人机设备数量区域排行
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计无人机设备数量区域排行")
    @PostMapping("/workbench/getUavDeviceStatisticsByArea")
    public TableDataInfo getUavDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO) {
        List<Map> statisticsByArea = workbenchService.getUavDeviceStatisticsByArea(analysisVO);
        return  getDataTable(statisticsByArea);
    }

    /**
     * 统计喇叭设备数量
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计喇叭设备数量")
    @PostMapping("/workbench/getHornDeviceStatistics")
    public Result<HornDeviceStatisticsVo> getHornDeviceStatistics(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getHornDeviceStatistics(analysisVO);
    }

    /**
     * 统计喇叭设备数量区域排行
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计喇叭设备数量区域排行")
    @PostMapping("/workbench/getHornDeviceStatisticsByArea")
    public TableDataInfo getHornDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO) {
        List<Map> statisticsByArea = workbenchService.getHornDeviceStatisticsByArea(analysisVO);
        return  getDataTable(statisticsByArea);
    }

    /**
     * 统计雷达设备数量
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计雷达设备数量")
    @PostMapping("/workbench/getRadarDeviceStatistics")
    public Result<RadarDeviceStatisticsVo> getRadarDeviceStatistics(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getRadarDeviceStatistics(analysisVO);
    }

    /**
     * 统计雷达设备数量区域排行
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计雷达设备数量区域排行")
    @PostMapping("/workbench/getRadarDeviceStatisticsByArea")
    public TableDataInfo getRadarDeviceStatisticsArea(@RequestBody AnalysisVO analysisVO) {
        List<Map> statisticsByArea = workbenchService.getRadarDeviceStatisticsByArea(analysisVO);
        return  getDataTable(statisticsByArea);
    }

    /**
     * 统计物联设备数量
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计物联设备数量")
    @PostMapping("/workbench/getIotDeviceStatistics")
    public Result<IotDeviceStatisticsVo> getIotDeviceStatistics(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getIotDeviceStatistics(analysisVO);
    }

    /**
     * 统计物联设备数量区域排行
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计物联设备数量区域排行")
    @PostMapping("/workbench/getIotDeviceStatisticsByArea")
    public TableDataInfo getIotDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO) {
        List<Map> statisticsByArea = workbenchService.getIotDeviceStatisticsByArea(analysisVO);
        return  getDataTable(statisticsByArea);
    }

    /**
     * 统计监控设备数量App
     * @param analysisVO (year 年 month 月 day 日)
     *
     * @return deviceCountTotal 摄像机总数
     * @return devCountOnline 摄像机在线数
     * @return devCountOffline 摄像机离线数
     * @return channelCountTotal 通道总数
     * @return channelCountOnline   通道在线数
     * @return channelCountOffline 通道离线数
     */
    @ApiOperation(value = "统计监控设备数量")
    @PostMapping("/workbench/getDeviceStatisticsApp")
    public Result<DeviceStatisticsVO> getDeviceStatisticsApp(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getDeviceStatistics(analysisVO);
    }

    /**
     * 行业-统计监控设备数量
     *
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计监控设备数量(行业)")
    @PostMapping("/industry/workbench/getDeviceStatistics")
    public Result<DeviceStatisticsVO> getIndustryDeviceStatistics(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getDeviceStatistics(analysisVO);
    }

    /**
     * 近7日事件数量
     *
     * @param analysisVO create_date 创建时间
     * @return eventDate 创建时间
     * @return name 名称
     * @return num 数量
     */
    @ApiOperation(value = "近7日事件数量")
    @PostMapping("/workbench/getEventNumBySource")
    public Result getEventNumBySource(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumBySource(analysisVO);
    }

    /**
     * 事件高发区域
     *
     * @param analysisVO
     * @return analysisVO（provinceId:省份编码 ）
     * @return analysisVO（cityId:市编码）
     * @return analysisVO（countyId:区县编码）
     */
    @ApiOperation(value = "事件高发区域")
    @PostMapping("/workbench/getEventNumByArea")
    public TableDataInfo getEventNumByArea(@RequestBody AnalysisVO analysisVO) {
        return getDataTable(workbenchService.getEventNumByArea(analysisVO));
    }

    /**
     * 告警数量按照区县及告警类型排行
     *
     * @param analysisVO
     */
    @ApiOperation(value = "事件高发区域")
    @PostMapping("/workbench/getEventNumByAreaAndType")
    public TableDataInfo getEventNumByAreaAndType(@RequestBody AnalysisVO analysisVO) {
        return getDataTable(workbenchService.getEventNumByAreaAndType(analysisVO));
    }


    /**
     * 查询告警来源
     * */
    @PostMapping("/workbench/getEventAlarmNumSource")
    public TableDataInfo getEventAlarmNumSource(@RequestBody StatisticsVO statisticsVO) {
        log.info("查询告警来源统计---请求报文------{}", JSON.toJSONString(statisticsVO));
        return getDataTable(workbenchService.getEventAlarmNumSource(statisticsVO));
    }

    /**
     * 行业-查询告警来源
     * */
    @PostMapping("/workbench/getEventAlarmNumSourceIndustry")
    public TableDataInfo getEventAlarmNumSourceIndustry(@RequestBody AnalysisVO analysisVO) {
        log.info("查询告警来源统计---请求报文------{}", JSON.toJSONString(analysisVO));
        return getDataTable(workbenchService.getEventAlarmNumSourceIndustry(analysisVO));
    }

    /**
     * 行业-查询告警类型
     * */
    @PostMapping("/workbench/getEventAlarmNumTypeIndustry")
    public TableDataInfo getEventAlarmNumTypeIndustry(@RequestBody AnalysisVO analysisVO) {
        log.info("查询告警类型统计---请求报文------{}", JSON.toJSONString(analysisVO));
        return getDataTable(workbenchService.getEventAlarmNumTypeIndustry(analysisVO));
    }



    /**
     * 按年或月查询告警事件类型
     *
     * @param statisticsVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    @ApiOperation(value = "按年或月查询告警事件类型")
    @PostMapping("/workbench/getDevTypeYearMonth")
    public Object getDevTypeYearMonth(@RequestBody StatisticsVO statisticsVO) {
        return workbenchService.getDevTypeYearMonth(statisticsVO);
    }

    /**
     * 按年或月查询告警事件类型-APP
     *
     * @param statisticsVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    @ApiOperation(value = "按年或月查询告警事件类型")
    @PostMapping("/workbench/getDevTypeYearMonthApp")
    public TableDataInfo getDevTypeYearMonthApp(@RequestBody StatisticsVO statisticsVO) {
        return getDataTable(workbenchService.getDevTypeYearMonthApp(statisticsVO));
    }

    /**
     * 查询告警事件类型top10(大屏)
     *
     * @param analysisVO
     * @return analysisVO num
     * @return analysisVO aiNum
     * @return analysisVO judgeNum
     * @return analysisVO realNum
     * @return analysisVO falseNum
     */
    @ApiOperation(value = "查询告警事件类型top10(大屏)")
    @PostMapping("/workbench/getDevTypeYearMonthAppTop")
    public TableDataInfo getDevTypeYearMonthAppTop(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getDevTypeYearMonthAppTop(analysisVO);
    }

    /**
     * 查询告警事件类型(大屏)
     *
     * @param analysisVO
     * @return analysisVO num
     * @return analysisVO aiNum
     * @return analysisVO judgeNum
     * @return analysisVO realNum
     * @return analysisVO falseNum
     */
    @ApiOperation(value = "查询告警事件类型(大屏)")
        @PostMapping("/workbench/getDevTypeYearMonthAppScreen")
    public TableDataInfo getDevTypeYearMonthAppScreen(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getDevTypeYearMonthAppScreen(analysisVO);
    }

    /**
     * 获取事件统计数量-APP
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "获取事件统计数量")
    @PostMapping("/workbench/getEventNumListApp")
    public Result getEventNumListApp(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumListApp(analysisVO);
    }

    /**
     * 获取事件统计数量小时/日期折线图(含告警等级对应数量)
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "获取事件统计数量小时/日期折线图(含告警等级对应数量)")
    @PostMapping("/workbench/getEventNumLevelList")
    public Result getEventNumLevelList(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumLevelList(analysisVO);
    }

    /**
     * 获取事件统计数量小时/日期折线图(含告警类型对应数量)
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "获取事件统计数量小时/日期折线图(含告警类型对应数量)")
    @PostMapping("/workbench/getEventNumTypeList")
    public Result<List<AlarmNumByDateAndTypeVO>> getEventNumTypeList(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumTypeList(analysisVO);
    }

    /**
     * 查询设备未处理最高告警等级
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "查询设备未处理最高告警等级)")
    @PostMapping("/workbench/getDeviceMaxLevelList")
    public Result getDeviceMaxLevelList(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getDeviceMaxLevelList(analysisVO);
    }


    /**
     * 获取事件统计数量(月份)-APP
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "获取事件统计数量(月份)")
    @PostMapping("/workbench/getEventNumListAppMonth")
    public Result getEventNumListAppMonth(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumListAppMonth(analysisVO);
    }

    /**
     * 今日告警等级
     *
     * @param analysisVO  (year 年 month 月 day 日)
     * @return emergency_level 等级
     * @return num 数量
     */
    @ApiOperation(value = "今日告警等级")
    @PostMapping("/workbench/getEventNumByLevel")
    public Result getEventNumByLevel(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumByLevel(analysisVO);
    }

    /**
     * 今日事件高发时段
     *
     * @param analysisVO create_date 当前时间 county_id 市id
     * @return 1-12各个时间段返回值
     */
    @ApiOperation(value = "今日事件高发时段")
    @PostMapping("/workbench/getEventNumByHour")
    public Result getEventNumByHour(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumByHour(analysisVO);
    }

    /**
     * 24小时告警趋势
     *
     * @param analysisVO create_date 当前时间 county_id 市id
     * @return 1-12各个时间段返回值
     */
    @ApiOperation(value = "24小时告警趋势")
    @PostMapping("/workbench/getAlarmEventTrendNumByHour")
    public Result getAlarmEventTrendNumByHour(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getAlarmEventTrendNumByHour(analysisVO);
    }

    /**
     * 今日事件高发时段按类型来源查询
     *
     * @param analysisVO create_date 当前时间 county_id 市id
     * @return 1-12各个时间段返回值
     */
    @ApiOperation(value = "今日事件高发时段")
    @PostMapping("/workbench/getEventNumDetailByHour")
    public Result getEventNumDetailByHour(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumDetailByHour(analysisVO);
    }

    /**
     * 24h告警压制统计
     *
     * @param analysisVO create_date 当前时间 county_id 市id
     * @return 1-12各个时间段返回值
     */
    @ApiOperation(value = "24h告警压制统计")
    @PostMapping("/workbench/getEventNumByInterceptHour")
    public Result getEventNumByInterceptHour(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumByInterceptHour(analysisVO);
    }

    /**
     * 24h告警压制统计按类型来源查询
     *
     * @param analysisVO create_date 当前时间 county_id 市id
     * @return 1-12各个时间段返回值
     */
    @ApiOperation(value = "24h告警压制统计")
    @PostMapping("/workbench/getEventNumDetailByInterceptHour")
    public Result getEventNumDetailByInterceptHour(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumDetailByInterceptHour(analysisVO);
    }

    /**
     * 今日事件高发时段
     *
     * @param analysisVO create_date 当前时间 county_id 市id
     * @return 1-12各个时间段返回值
     */
    @ApiOperation(value = "今日事件高发时段")
    @PostMapping("/workbench/getEventNumByHourApp")
    public Result getEventNumByHourApp(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventNumByHour(analysisVO);
    }

    /**
     * 获取事件数量-APP
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "获取事件数量-APP用")
    @PostMapping("/workbench/getEventNumForApp")
    public Result getEventNumForApp(@RequestBody AnalysisVO analysisVO){
        return workbenchService.getEventNumForApp(analysisVO);
    }

    /**
     * 算法任务数量
     * @param analysisVO
     * @return AlgorithmStatisticsVO(totalNum:算法任务数量)
     * @return AlgorithmStatisticsVO(enableNum:启用算法任务数量)
     */
    @ApiOperation(value = "获取算法任务数量")
    @PostMapping("/workbench/getAlgorithmNum")
    public Result<AlgorithmStatisticsVO> getAlgorithmNum(@RequestBody AnalysisVO analysisVO){
        return workbenchService.getAlgorithmNum(analysisVO);
    }

    /**
     * 按年或月查询告警标签事件
     *
     * @param statisticsVO
     * @return statisticsVO num
     * @return statisticsVO aiNum
     * @return statisticsVO judgeNum
     * @return statisticsVO realNum
     * @return statisticsVO falseNum
     */
    @ApiOperation(value = "按年或月查询告警事件类型")
    @PostMapping("/workbench/getAlarmTagYearMonth")
    public Result getAlarmTagYearMonth(@RequestBody StatisticsVO statisticsVO) {
        return workbenchService.getAlarmTagYearMonth(statisticsVO);
    }

    @ApiOperation(value = "根据标签查询标签下告警时间数并增加返回网格字段")
    @PostMapping("/workbench/getAlarmNumByLabel")
    public TableDataInfo getAlarmNumByLabel(@RequestBody StatisticsVO statisticsVO) {
        return getDataTable(workbenchService.getAlarmNumByLabel(statisticsVO));
    }


    /**
     * 告警统计总览
     *
     * @param statisticsVO
     * @author cuisj
     */
    @ApiOperation(value = "查询告警统计总览")
    @PostMapping("/workbench/getNumAll")
    public Result getNumAll(@RequestBody StatisticsVO statisticsVO) {
        return workbenchService.getNumAll(statisticsVO);
    }

    /**
     * 点位告警统计
     * @param statisticsVO
     * @return
     */
    @ApiOperation(value = "点位告警统计")
    @PostMapping("/workbench/getPointAlarmNum")
    public Result getPointAlarmNum(@RequestBody StatisticsVO statisticsVO) {
        Map<String, Object> result = workbenchService.getPointAlarmNum(statisticsVO);
        return Result.data(result);
    }

    /**
     * 摄像机告警统计总览
     *
     * @param statisticsVO
     * @author cuisj
     */
    @ApiOperation(value = "查询告警统计总览")
    @PostMapping("/workbench/getDeviceNumAll")
    public TableDataInfo getDeviceNumAll(@RequestBody StatisticsVO statisticsVO) {
        return workbenchService.getDeviceNumAll(statisticsVO);
    }

    /**
     * 物联告警统计总览
     *
     * @param statisticsVO
     * @author cuisj
     */
    @ApiOperation(value = "查询物联告警统计总览")
    @PostMapping("/workbench/getIotDeviceNumAll")
    public TableDataInfo getIotDeviceNumAll(@RequestBody StatisticsVO statisticsVO) {
        return workbenchService.getIotDeviceNumAll(statisticsVO);
    }


    /**
     * 告警进度处理统计
     *
     * @param statisticsVO 查询参数
     * @author lisy
     */
    @ApiOperation(value = "告警进度处理统计")
    @PostMapping("/workbench/getAlarmStatisticsProcessingNum")
    public Result getAlarmStatisticsProcessingNum(@RequestBody StatisticsVO statisticsVO) {
        return workbenchService.getAlarmStatisticsProcessingNum(statisticsVO);
    }

    /**
     * 查询摄像机设备区域分布排行
     *
     * @param statisticsVO 查询参数
     * @author lisy
     */
    @ApiOperation(value = "查询摄像机设备告警数量排行")
    @PostMapping("/workbench/getCamaraDeviceAreaDistributionNum")
    public TableDataInfo getCamaraDeviceAreaDistributionNum(@RequestBody StatisticsVO statisticsVO) {
        Integer pageNum = statisticsVO.getPageNum();
        Integer pageSize = statisticsVO.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return getDataTable(workbenchService.getCamaraDeviceAreaDistributionNum(statisticsVO));
    }

    /**
     * 查询无人机设备区域分布排行
     *
     * @param statisticsVO 查询参数
     * @author lisy
     */
    @ApiOperation(value = "查询无人机设备区域分布排行入参")
    @PostMapping("/workbench/getUavAreaDistributionNum")
    public TableDataInfo getUavAreaDistributionNum(@RequestBody StatisticsVO statisticsVO) {
        log.info("查询无人机设备区域分布排行入参{}", JSONObject.toJSONString(statisticsVO));
        Integer pageNum = statisticsVO.getPageNum();
        Integer pageSize = statisticsVO.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return getDataTable(workbenchService.getUavAreaDistributionNum(statisticsVO));
    }

    /**
     * 查询IOT设备区域分布排行
     *
     * @param statisticsVO 查询参数
     * @author lisy
     */
    @ApiOperation(value = "查询IOT物联设备告警数量排行")
    @PostMapping("/workbench/getIotDeviceAreaDistributionNum")
    public TableDataInfo getIotDeviceAreaDistributionNum(@RequestBody StatisticsVO statisticsVO) {
        Integer pageNum = statisticsVO.getPageNum();
        Integer pageSize = statisticsVO.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return getDataTable(workbenchService.getIotDeviceAreaDistributionNum(statisticsVO));
    }


    /**
     * 查询设备昼夜数量(物联设备)
     *
     * @param statisticsVO
     * @author cuisj
     */
    @ApiOperation(value = "查询设备昼夜数量(物联设备)")
    @PostMapping("/workbench/getDeviceDayAndNightNum")
    public TableDataInfo getDeviceDayAndNightNum(@RequestBody StatisticsVO statisticsVO) {
        return getDataTable(workbenchService.getDeviceDayAndNightNum(statisticsVO));
    }

    /**
     * 获取压制事件统计数量
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "获取压制事件统计数量")
    @PostMapping("/workbench/getEventInterceptNumApp")
    public Result getEventInterceptNumApp(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventInterceptNumApp(analysisVO);
    }

    /**
     * 获取压制事件统计数量
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "获取压制事件统计数量")
    @PostMapping("/workbench/getEventDetailInterceptNumApp")
    public Result getEventDetailInterceptNumApp(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEventDetailInterceptNumApp(analysisVO);
    }

    /**
     * 告警准确率分析
     * */
    @ApiOperation(value = "告警准确率分析")
    @PostMapping("/workbench/getEvenAccuracy")
    public Result getEvenAccuracy(@RequestBody StatisticsVO statisticsVO) {
        return workbenchService.getEvenAccuracy(statisticsVO);
    }

    /**
     * 告警区域准确率排行
     * */
    @ApiOperation(value = "告警区域准确率排行")
    @PostMapping("/workbench/getEvenAreaAccuracy")
    public TableDataInfo getEvenAreaAccuracy(@RequestBody AnalysisVO analysisVO) {
        return getDataTable(workbenchService.getEvenAreaAccuracy(analysisVO));
    }

    /**
     * 告警类型准确率排行
     * */
    @ApiOperation(value = "告警类型准确率排行")
    @PostMapping("/workbench/getEvenTypeAccuracy")
    public TableDataInfo getEvenTypeAccuracy(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEvenTypeAccuracy(analysisVO);
    }

    /**
     * 告警来源准确率排行
     * */
    @ApiOperation(value = "告警来源准确率排行")
    @PostMapping("/workbench/getEvenSourceAccuracy")
    public TableDataInfo getEvenSourceAccuracy(@RequestBody AnalysisVO analysisVO) {
        return getDataTable(workbenchService.getEvenSourceAccuracy(analysisVO));
    }

    /**
     * 网格完结率统计
     * */
    @ApiOperation(value = "网格完结率统计")
    @PostMapping("/workbench/getEvenGridCompletion")
    public TableDataInfo getEvenGridCompletion(@RequestBody AnalysisVO analysisVO) {
        return getDataTable(workbenchService.getEvenGridCompletion(analysisVO));
    }

    /**
     * 网格完结率统计
     * */
    @ApiOperation(value = "网格完结率统计")
    @PostMapping("/workbench/getEvenGridCompletionApp")
    public TableDataInfo getEvenGridCompletionApp(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEvenGridCompletionApp(analysisVO);
    }

    /**
     * 行政区域完结率统计
     * */
    @ApiOperation(value = "行政区域完结率统计")
    @PostMapping("/workbench/getEvenAreaCompletion")
    public TableDataInfo getEvenAreaCompletion(@RequestBody AnalysisVO analysisVO) {
        return getDataTable(workbenchService.getEvenAreaCompletion(analysisVO));
    }

    /**
     * 行政区域完结率统计行业
     * */
    @ApiOperation(value = "行政区域完结率统计行业")
    @PostMapping("/workbench/getEvenAreaCompletionIndustry")
    public TableDataInfo getEvenAreaCompletionIndustry(@RequestBody AnalysisVO analysisVO) {
        return getDataTable(workbenchService.getEvenAreaCompletionIndustry(analysisVO));
    }

    /**
     * app告警来源详情
     * */
    @ApiOperation(value = "app告警来源详情")
    @PostMapping("/workbench/getEventDetailBySource")
    public TableDataInfo getEventDetailBySource(@RequestBody AnalysisVO analysisVO) {
        return getDataTable(workbenchService.getEventDetailBySource(analysisVO));
    }

    /**
     * app告警类型详情
     * */
    @ApiOperation(value = "app告警类型详情")
    @PostMapping("/workbench/getEventDetailByType")
    public TableDataInfo getEventDetailByType(@RequestBody AnalysisVO analysisVO) {
        return getDataTable(workbenchService.getEventDetailByType(analysisVO));
    }

    /**
     * 行政区域完结率统计Pc
     * */
    @ApiOperation(value = "行政区域完结率统计")
    @PostMapping("/workbench/getEvenAreaCompletionPc")
    public TableDataInfo getEvenAreaCompletionPc(@RequestBody AnalysisVO analysisVO) {
        return getDataTable(workbenchService.getEvenAreaCompletionPc(analysisVO));
    }

    /**
     * 组织完结率统计
     * */
    @ApiOperation(value = "组织完结率统计")
    @PostMapping("/workbench/getEvenDeptCompletion")
    public TableDataInfo getEvenDeptCompletion(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getEvenDeptCompletion(analysisVO);
    }

    /**
     * 统计执法记录仪设备数量区域排行
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计执法记录仪设备数量区域排行")
    @PostMapping("/workbench/getRecorderDeviceStatisticsByArea")
    public TableDataInfo getRecorderDeviceStatisticsByArea(@RequestBody AnalysisVO analysisVO) {
        List<Map> statisticsByArea = workbenchService.getRecorderDeviceStatisticsByArea(analysisVO);
        return getDataTable(statisticsByArea);
    }

    /**
     * 统计执法记录仪数量
     * @param analysisVO
     * @return
     */
    @ApiOperation(value = "统计执法记录仪数量")
    @PostMapping("/workbench/getRecorderDeviceStatistics")
    public Result<DeviceStatisticsVO> getRecorderDeviceStatistics(@RequestBody AnalysisVO analysisVO) {
        return workbenchService.getRecorderDeviceStatistics(analysisVO);
    }

    /**
     * 设备区域排行
     * @param deviceAnalysisVO
     * @return
     */
    @PostMapping("/workbench/getDeviceAreaRanking")
    public TableDataInfo getDeviceAreaRanking(@RequestBody @Validated DeviceAnalysisVO deviceAnalysisVO) {
        TableDataInfo tableDataInfo = null;
        AnalysisVO analysisVO = new AnalysisVO();
        org.springframework.beans.BeanUtils.copyProperties(deviceAnalysisVO, analysisVO);
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        analysisVO.setTenantId(sysUser.getTenantId());
        analysisVO.setUserId(sysUser.getUserId());
        analysisVO.setIndustryCode(sysUser.getIndustryCode());
        analysisVO.setAppVerCode(sysUser.getAppVerCode());
        List<Map> statisticsByArea;
        switch (deviceAnalysisVO.getQueryDeviceLabel()) {
            //摄像机
            case 1:
                List<Map<String, String>> deviceStatisticsVOS = deviceStatisticsService.getCameraNumByArea(analysisVO);
                tableDataInfo = getDataTable(deviceStatisticsVOS);
                break;
            //物联设备
            case 2:
                statisticsByArea = workbenchService.getIotDeviceStatisticsByArea(analysisVO);
                tableDataInfo =  getDataTable(statisticsByArea);
                break;
            //无人机
            case 3:
                statisticsByArea = workbenchService.getUavDeviceStatisticsByArea(analysisVO);
                tableDataInfo = getDataTable(statisticsByArea);
                break;
            //喇叭
            case 4:
                statisticsByArea = workbenchService.getHornDeviceStatisticsByArea(analysisVO);
                tableDataInfo = getDataTable(statisticsByArea);
                break;
            //执法记录仪
            case 5:
                statisticsByArea = workbenchService.getRecorderDeviceStatisticsByArea(analysisVO);
                tableDataInfo = getDataTable(statisticsByArea);
                break;
            //雷达
            case 8:
                statisticsByArea = workbenchService.getRadarDeviceStatisticsByArea(analysisVO);
                tableDataInfo = getDataTable(statisticsByArea);
                break;
            default:
                break;
        }

        return tableDataInfo;
    }

}
