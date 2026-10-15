package com.chinatower.device.api.factory;


import com.chinatower.common.entity.Result;
import com.chinatower.device.api.deprecated.ISensorDeviceServiceMigrate;
import com.chinatower.device.entity.dto.SenSorMonitorConfigDO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.IotDeviceSensorCalculateVO;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.device.entity.vo.SensorAttrMapVO;
import com.chinatower.device.entity.vo.SensorAttrVO;
import com.chinatower.device.entity.vo.SensorMonitorRelInfoVo;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.entity.device.*;
import com.github.pagehelper.PageInfo;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 设备服务降级处理
 *
 * @author chenmf
 */
@Component
public class SensorDeviceFallbackFactory implements FallbackFactory<ISensorDeviceServiceMigrate> {

    private static final Logger log = LoggerFactory.getLogger(SensorDeviceFallbackFactory.class);

    @Override
    public ISensorDeviceServiceMigrate create(Throwable throwable) {

        return new ISensorDeviceServiceMigrate() {

            @Override
            public Result<List<IotDeviceVO>> queryIotDeviceInfo(IotDeviceVO param) {
                return Result.fail("查询物联设备详情失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getIotMonitorOrgTree(IotDeviceVO iotDeviceVO) {
                return Result.fail("查询物联设备组织树信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getIotMonitorTree(IotDeviceVO deviceInfoVO) {
                return Result.fail("查询物联设备区域树信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IotDeviceVO>> queryIotDeviceBySite(IotDeviceVO iotDeviceVO) {
                return Result.fail("查询物联设备弹窗详情信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<SensorAttrVO>> getSensorData(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询物联设备监测数据信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<SensorAttrVO>> getSensorDataBatch(DeviceReqDTO deviceReqDTO) {
                return Result.fail("批量查询物联设备监测数据信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getRadarMonitorOrgTree(IotDeviceVO iotDeviceVO) {
                return Result.fail("查询雷达设备组织树信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getRadarMonitorTree(IotDeviceVO deviceInfoVO) {
                return Result.fail("查询雷达设备区域树信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IotDeviceVO>> queryRadarDeviceBySite(IotDeviceVO iotDeviceVO) {
                return Result.fail("查询雷达设备弹窗详情信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IotDeviceVO>> queryRadarDeviceByArea(IotDeviceVO iotDeviceVO) {
                return Result.fail("根据站址查询雷达设备信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<SensorAttrMapVO>> getSensorPeriodData(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询物联设备时间段监测数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IotDeviceSensorCalculateVO>> querySensorPeriodData4Calculate(DeviceDistance4CalculateDTO param) {
                return Result.fail("查询物联设备属性计算数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<Page<SensorAttrMapVO>> pageSensorPeriodData(DeviceReqDTO deviceReqDTO) {
                return Result.fail("查询传感器时间段数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<SensorStatusInfoVO>> querySensorStatusInfo(SensorStatusInfoDTO param) {
                return Result.fail("查询传感器状态信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<SensorMonitorRelInfoVo>> querySensorMonitorRelInfo(DeviceReqDTO deviceReqDTO) {
                return Result.fail("传感器和物联因子的关联信息:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<SenSorMonitorConfigDO>> qrySensorMonitor(IotDeviceVO iotDeviceVO) {
                return Result.fail("查询物联因子列表信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<IndustryDeviceTree>> queryIotRadarDeviceSites(SiteRequestDTO siteRequestDTO) {
                return Result.fail("查询物联雷达图层信息失败:" + throwable.getMessage());
            }
        };
    }
}
