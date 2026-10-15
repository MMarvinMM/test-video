package com.chinatower.device.controller;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.service.IEnergyDeviceInfoService;
import com.chinatower.mongodb.request.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * video-device
 */
@RestController
@RequestMapping("/energy/device")
@Slf4j
public class EnergyDeviceController extends BaseController {

    @Autowired
    private IEnergyDeviceInfoService energyDeviceInfoService;

    /**
     * 查询能源设备站址列表
     * @param req 参数
     * @return 返回值
     */
    @PostMapping(value = "/queryStationList")
    public TableDataInfo queryStationList(@RequestBody EnergyDeviceStationQueryReq req) {
        return getDataTable(energyDeviceInfoService.queryStationList(req));
    }

    /**
     * 查询能源设备列表
     * @param req 参数
     * @return 返回值
     */
    @PostMapping(value = "/queryDeviceList")
    public TableDataInfo queryDeviceList(@RequestBody EnergyDeviceQueryReq req) {
        return getDataTable(energyDeviceInfoService.queryDeviceList(req));
    }

    /**
     * 查询能源设备树
     * @param req 参数
     * @return 返回值
     */
    @PostMapping(value = "/queryDeviceTree")
    public Result<List<EnergyDeviceTreeVO>> queryDeviceTree(@RequestBody EnergyDeviceTreeQueryReq req) {
        List<EnergyDeviceTreeVO> list = energyDeviceInfoService.queryDeviceTree(req);
        return Result.data(list);
    }

    /**
     * 查询能源设备监测数据列表
     * @param req 参数
     * @return 返回值
     */
    @PostMapping(value = "/queryMonitorDataList")
    public TableDataInfo queryMonitorDataList(@RequestBody EnergyDeviceMonitorQueryReq req) {
        Page<EnergyMonitorDataMapVO> resultPage = energyDeviceInfoService.queryMonitorDataList(req);
        TableDataInfo resultData = new TableDataInfo();
        resultData.setCode(HttpStatus.SUCCESS);
        resultData.setRows(resultPage.getList());
        resultData.setMsg("查询成功");
        resultData.setTotal(resultPage.getCount());
        return resultData;
    }

    /**
     * 查询能源设备告警列表
     * @param req 参数
     * @return 返回值
     */
    @PostMapping(value = "/queryDeviceAlarmList")
    public TableDataInfo queryDeviceAlarmList(@RequestBody EnergyDeviceAlarmQueryReq req) {
        Page<EnergyDeviceAlarmVO> resultPage = energyDeviceInfoService.queryDeviceAlarmList(req);
        TableDataInfo resultData = new TableDataInfo();
        resultData.setCode(HttpStatus.SUCCESS);
        resultData.setRows(resultPage.getList());
        resultData.setMsg("查询成功");
        resultData.setTotal(resultPage.getCount());
        return resultData;
    }

    /**
     * 根据站址查询能源设备状态统计信息
     * @param req 参数
     * @return 返回值
     */
    @PostMapping(value = "/queryDeviceStatusStatisticsByStation")
    public Result<List<EnergyDeviceStatusStatisticsVO>> queryDeviceStatusStatisticsByStation(@RequestBody EnergyDeviceStatusStatisticsQueryReq req) {
        return Result.data(energyDeviceInfoService.queryDeviceStatusStatisticsByStation(req));
    }

    /**
     * 查询能源设备状态统计信息
     * @param req 参数
     * @return 返回值
     */
    @PostMapping(value = "/queryDeviceStatusStatistics")
    public Result<EnergyDeviceStatusStatisticsVO> queryDeviceStatusStatistics(@RequestBody EnergyDeviceStatusStatisticsQueryReq req) {
        return Result.data(energyDeviceInfoService.queryDeviceStatusStatistics(req));
    }

    /**
     * 查询能源设备告警统计信息
     * @param req 参数
     * @return 返回值
     */
    @PostMapping(value = "/queryDeviceAlarmStatistics")
    public Result<EnergyDeviceAlarmStatisticsVO> queryDeviceAlarmStatistics(@RequestBody EnergyDeviceAlarmStatisticsQueryReq req) {
        return Result.data(energyDeviceInfoService.queryDeviceAlarmStatistics(req));
    }

    /**
     * 刷新站址区域信息
     * @return 返回值
     */
    @PostMapping(value = "/refreshStationRegion")
    public Result<String> refreshStationRegion() {
        energyDeviceInfoService.refreshStationRegion();
        return Result.data("处理成功");
    }

    /**
     * 处理能源设备历史数据
     * @return 返回值
     */
    @PostMapping(value = "/handleHistoryData")
    public Result<String> handleHistoryData() {
        energyDeviceInfoService.handleHistoryData();
        return Result.data("处理成功");
    }

}
