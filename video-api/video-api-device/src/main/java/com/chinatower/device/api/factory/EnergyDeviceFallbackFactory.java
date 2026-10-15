package com.chinatower.device.api.factory;

import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.IEnergyDeviceService;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.EnergyDeviceAlarmStatisticsVO;
import com.chinatower.device.entity.vo.EnergyDeviceStatusStatisticsVO;
import com.chinatower.device.entity.vo.EnergyDeviceTreeVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 降级处理
 */
@Component
public class EnergyDeviceFallbackFactory implements FallbackFactory<IEnergyDeviceService> {
    @Override
    public IEnergyDeviceService create(Throwable throwable) {
        return new IEnergyDeviceService() {

            @Override
            public TableDataInfo queryStationList(EnergyDeviceStationQueryReq req) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo queryDeviceList(EnergyDeviceQueryReq req) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<List<EnergyDeviceTreeVO>> queryDeviceTree(EnergyDeviceTreeQueryReq req) {
                return Result.fail("查询能源设备区域树失败" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryMonitorDataList(EnergyDeviceMonitorQueryReq req) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public TableDataInfo queryDeviceAlarmList(EnergyDeviceAlarmQueryReq req) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                rspData.setRows(null);
                rspData.setTotal(0);
                return rspData;
            }

            @Override
            public Result<List<EnergyDeviceStatusStatisticsVO>> queryDeviceStatusStatisticsByStation(EnergyDeviceStatusStatisticsQueryReq req) {
                return Result.fail("查询能源设备站点统计失败" + throwable.getMessage());
            }

            @Override
            public Result<EnergyDeviceStatusStatisticsVO> queryDeviceStatusStatistics(EnergyDeviceStatusStatisticsQueryReq req) {
                return Result.fail("查询能源设备统计失败" + throwable.getMessage());
            }

            @Override
            public Result<EnergyDeviceAlarmStatisticsVO> queryDeviceAlarmStatistics(EnergyDeviceAlarmStatisticsQueryReq req) {
                return Result.fail("查询能源设备告警统计失败" + throwable.getMessage());
            }
        };
    }
}
