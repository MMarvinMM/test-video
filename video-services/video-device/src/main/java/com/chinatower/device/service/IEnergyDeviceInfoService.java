package com.chinatower.device.service;

import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.mongodb.request.Page;

import java.util.List;

public interface IEnergyDeviceInfoService {

    List<EnergyDeviceStationVO> queryStationList(EnergyDeviceStationQueryReq req);

    List<EnergyDeviceVO> queryDeviceList(EnergyDeviceQueryReq req);

    List<EnergyDeviceTreeVO> queryDeviceTree(EnergyDeviceTreeQueryReq req);

    Page<EnergyMonitorDataMapVO> queryMonitorDataList(EnergyDeviceMonitorQueryReq req);

    Page<EnergyDeviceAlarmVO> queryDeviceAlarmList(EnergyDeviceAlarmQueryReq req);

    List<EnergyDeviceStatusStatisticsVO> queryDeviceStatusStatisticsByStation(EnergyDeviceStatusStatisticsQueryReq req);

    EnergyDeviceStatusStatisticsVO queryDeviceStatusStatistics(EnergyDeviceStatusStatisticsQueryReq req);

    EnergyDeviceAlarmStatisticsVO queryDeviceAlarmStatistics(EnergyDeviceAlarmStatisticsQueryReq req);

    void refreshStationRegion();

    void handleHistoryData();
}
