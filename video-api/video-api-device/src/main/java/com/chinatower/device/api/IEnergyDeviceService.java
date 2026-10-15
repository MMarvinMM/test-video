package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.factory.AirLineFallbackFactory;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.EnergyDeviceAlarmStatisticsVO;
import com.chinatower.device.entity.vo.EnergyDeviceStatusStatisticsVO;
import com.chinatower.device.entity.vo.EnergyDeviceTreeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "energyDeviceService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = AirLineFallbackFactory.class)
public interface IEnergyDeviceService {

    @PostMapping(value = "/energy/device/queryStationList")
    TableDataInfo queryStationList(@RequestBody EnergyDeviceStationQueryReq req);

    @PostMapping(value = "/energy/device/queryDeviceList")
    TableDataInfo queryDeviceList(@RequestBody EnergyDeviceQueryReq req);

    @PostMapping(value = "/energy/device/queryDeviceTree")
    Result<List<EnergyDeviceTreeVO>> queryDeviceTree(@RequestBody EnergyDeviceTreeQueryReq req);

    @PostMapping(value = "/energy/device/queryMonitorDataList")
    TableDataInfo queryMonitorDataList(@RequestBody EnergyDeviceMonitorQueryReq req);

    @PostMapping(value = "/energy/device/queryDeviceAlarmList")
    TableDataInfo queryDeviceAlarmList(@RequestBody EnergyDeviceAlarmQueryReq req);

    @PostMapping(value = "/energy/device/queryDeviceStatusStatisticsByStation")
    Result<List<EnergyDeviceStatusStatisticsVO>> queryDeviceStatusStatisticsByStation(@RequestBody EnergyDeviceStatusStatisticsQueryReq req);

    @PostMapping(value = "/energy/device/queryDeviceStatusStatistics")
    Result<EnergyDeviceStatusStatisticsVO> queryDeviceStatusStatistics(@RequestBody EnergyDeviceStatusStatisticsQueryReq req);

    @PostMapping(value = "/energy/device/queryDeviceAlarmStatistics")
    Result<EnergyDeviceAlarmStatisticsVO> queryDeviceAlarmStatistics(@RequestBody EnergyDeviceAlarmStatisticsQueryReq req);
}
