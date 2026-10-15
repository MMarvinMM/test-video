package com.chinatower.device.api.deprecated;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.SensorDeviceFallbackFactory;
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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(contextId = "sensorDeviceService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = SensorDeviceFallbackFactory.class)
public interface ISensorDeviceServiceMigrate {

    /**
     * 查询物联设备详情
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/queryIotDeviceInfo")
    Result<List<IotDeviceVO>> queryIotDeviceInfo(@RequestBody IotDeviceVO param);

    /**
     * 物联设备组织树
     * @param param 请求参数
     * @return 设备信息
     */
    @Deprecated
    @PostMapping("/iotdevice/getIotMonitorOrgTree")
    Result<List<DeviceTreeVO>> getIotMonitorOrgTree(@RequestBody IotDeviceVO param);

    /**
     * 物联设备区域树
     * @param param 请求参数
     * @return 设备信息
     */
    @Deprecated
    @PostMapping("/iotdevice/getIotMonitorTree")
    Result<List<DeviceTreeVO>> getIotMonitorTree(@RequestBody IotDeviceVO param);

    /**
     *
     * 物联打点详情数据
     * @param param 请求参数
     * @return 设备信息
     */
    @Deprecated
    @PostMapping("/iotdevice/queryIotDeviceBySite")
    Result<List<IotDeviceVO>> queryIotDeviceBySite(@RequestBody IotDeviceVO param);

    /**
     * 物联监测数据
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/getSensorData")
    Result<List<SensorAttrVO>> getSensorData(@RequestBody DeviceReqDTO param);

    /**
     * 物联监测数据
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/getSensorDataBatch")
    Result<List<SensorAttrVO>> getSensorDataBatch(@RequestBody DeviceReqDTO param);

    /**
     *
     * 雷达设备组织树
     * @param param 请求参数
     * @return 设备信息
     */
    @Deprecated
    @PostMapping("/radar/getRadarMonitorOrgTree")
    Result<List<DeviceTreeVO>> getRadarMonitorOrgTree(@RequestBody IotDeviceVO param);

    /**
     *
     * 雷达设备区域
     * @param param 请求参数
     * @return 设备信息
     */
    @Deprecated
    @PostMapping("/radar/getRadarMonitorTree")
    Result<List<DeviceTreeVO>> getRadarMonitorTree(@RequestBody IotDeviceVO param);

    /**
     *
     * 雷达打点详情数据
     * @param param 请求参数
     * @return 设备信息
     */
    @Deprecated
    @PostMapping("/radar/queryRadarDeviceBySite")
    Result<List<IotDeviceVO>> queryRadarDeviceBySite(@RequestBody IotDeviceVO param);

    /**
     *
     * 根据站址查询雷达
     * @param param 请求参数
     * @return 设备信息
     */
    @Deprecated
    @PostMapping("/radar/queryRadarDeviceByArea")
    Result<List<IotDeviceVO>> queryRadarDeviceByArea(@RequestBody IotDeviceVO param);

    /**
     * 查询传感器时间段数据
     *
     * @return
     * @author zheng.yan
     * @date 2022/5/11
     * @version
     */
    @PostMapping("/iotdevice/getSensorPeriodData")
    Result<List<SensorAttrMapVO>> getSensorPeriodData(@RequestBody DeviceReqDTO deviceReqDTO);

    /**
     * 提供给林业行业进行物联设备计算数据使用，该方法不对其他行业开放使用
     */
    @PostMapping("/iotdevice/querySensorPeriodData4Calculate")
    Result<List<IotDeviceSensorCalculateVO>> querySensorPeriodData4Calculate(@RequestBody DeviceDistance4CalculateDTO param);

    /**
     * 查询传感器时间段数据
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/iotdevice/pageSensorPeriodData")
    Result<Page<SensorAttrMapVO>> pageSensorPeriodData(@RequestBody DeviceReqDTO deviceReqDTO);

    /**
     * 查询传感器状态信息
     * @param param 请求参数
     * @return 设备信息
     */
    @PostMapping("/iotdevice/querySensorStatusInfo")
    Result<List<SensorStatusInfoVO>> querySensorStatusInfo(@RequestBody SensorStatusInfoDTO param);


    /**
     * 根据物联设备编码集合获取所包含的传感器和物联因子的关联信息
     *
     * @param deviceReqDTO 物联设备编码集合
     * @return 返回一个Result对象
     */
    @PostMapping("/iotdevice/querySensorMonitorRelInfo")
    Result<List<SensorMonitorRelInfoVo>> querySensorMonitorRelInfo(@RequestBody DeviceReqDTO deviceReqDTO);

    @PostMapping("/iotdevice/qrySensorMonitor")
    Result<PageInfo<SenSorMonitorConfigDO>> qrySensorMonitor(@RequestBody IotDeviceVO iotDeviceVO);

    /**
     * 分页查询物联、雷达摄像机图层
     * @param siteRequestDTO
     * @return
     */
    @PostMapping("/iotdevice/queryIotRadarDeviceSites")
    Result<PageInfo<IndustryDeviceTree>> queryIotRadarDeviceSites(@RequestBody SiteRequestDTO siteRequestDTO);
}
