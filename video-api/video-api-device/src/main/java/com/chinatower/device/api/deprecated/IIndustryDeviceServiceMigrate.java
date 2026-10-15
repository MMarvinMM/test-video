package com.chinatower.device.api.deprecated;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.entity.dto.DeviceModelDTO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UserDeviceCollectionDTO;
import com.chinatower.device.entity.dto.UserDeviceCollectionQueryDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.api.factory.IndustryDeviceFallbackFactory;
import com.chinatower.industry.dto.*;
import com.chinatower.industry.vo.*;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@FeignClient(contextId = "industryDeviceService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = IndustryDeviceFallbackFactory.class)
public interface IIndustryDeviceServiceMigrate {

    @ApiOperation(value = "查询摄像头信息", notes = "查询摄像头信息")
    @PostMapping("/industry/device/getDeviceChannel")
    @Deprecated
    Result<DeviceIndDTO> getDeviceChannel(@Valid @RequestBody DeviceIndReqDTO deviceIndReq);

    @ApiOperation("查询行业应用摄像机树")
    @PostMapping("/industry/device/queryMonitorIndTree")
    Result<List<IndustryDeviceTree>> queryMonitorIndTree(@RequestBody @Valid MonitorTreeInfReq monitorTreeInfReq);

    @ApiOperation(value = "通过站址信息查询设备", notes = "通过站址信息查询设备")
    @PostMapping("/industry/device/queryDevicesBySite")
    Result<List<DeviceIndDTO>> queryDevicesBySite(@RequestBody DeviceIndReqDTO deviceIndReqDTO);

    @ApiOperation(value = "查询摄像机图层")
    @PostMapping("/industry/device/queryDeviceLayer")
    Result<List<IndustryDeviceTree>> queryDeviceLayer(@RequestBody DeviceLayerReq deviceLayerReq);

    @ApiOperation(value = "查询传感器图层")
    @PostMapping("/industry/device/querySensorLayer")
    Result<List<IndustryDeviceTree>> querySensorLayer(@RequestBody DeviceLayerReq deviceLayerReq);

    @ApiOperation(value = "新增/取消收藏")
    @PostMapping("/industry/device/addOrCancelCollections")
    Result addOrCancelCollections(@RequestBody UserDeviceCollectionDTO userDeviceCollectionDTO);

    @ApiOperation(value = "查询收藏信息")
    @PostMapping("/industry/device/queryCollections")
    Result<List<UserDeviceCollectionQueryVO>> queryCollections(@RequestBody UserDeviceCollectionQueryDTO param);

    @ApiOperation(value = "查询摄像机可视域信息", notes = "查询摄像机可视域信息")
    @PostMapping("/industry/device/getDeviceModelInfo")
    Result<DeviceModelDTO> getDeviceModelInfo(@RequestBody DeviceIndReqDTO deviceInfoReq);

    @ApiOperation(value = "根据站址里列表查询摄像机可视域", notes = "根据站址里列表查询摄像机可视域")
    @PostMapping("/industry/device/getDeviceVisibleBySite")
    Result<List<DeviceModelDTO>> getDeviceVisibleBySite(@RequestBody DeviceIndReqDTO deviceInfoReq);


    @ApiOperation(value = "查询摄像机标定信息", notes = "查询摄像机标定信息")
    @PostMapping("/industry/device/getDeviceCalibration")
    Result<DeviceModelDTO> getDeviceCalibration(@RequestBody DeviceIndReqDTO deviceInfoReq);

    @ApiOperation(value = "查询传感器设备信息", notes = "查询传感器设备信息")
    @PostMapping("/industry/device/pageSensorDevice")
    TableDataInfo pageSensorDevice(@RequestBody PageDeviceVO deviceInfoReq);

    @ApiOperation(value = "查询传物联设备信息", notes = "查询传物联设备信息")
    @PostMapping("/industry/device/pageSensorDeviceInfo")
    TableDataInfo pageSensorDeviceInfo(@RequestBody PageSensorDeviceVO deviceInfoReq);


    @ApiOperation(value = "根据设备类型查询物联设备", notes = "根据设备类型查询物联设备")
    @PostMapping("/industry/device/pageIotDeviceByDeviceType")
    TableDataInfo pageIotDeviceByDeviceType(@RequestBody PageIotDeviceDTO iotDeviceDTO);


    @ApiOperation(value = "根据设备类型查询物联设备所对应的告警类型编码", notes = "查询物联设备所对应的告警类型编码")
    @PostMapping("/industry/device/pageAlarmTypeCodeByDeviceType")
    TableDataInfo pageAlarmTypeCodeByDeviceType(@RequestBody PageIotDeviceDTO iotDeviceDTO);


    @ApiOperation(value = "查询雷达设备信息", notes = "查询雷达设备信息")
    @PostMapping("/industry/device/pageRadarDevice")
    TableDataInfo pageRadarDevice(@RequestBody PageRadarDeviceVO pageRadarDeviceReq);


    @ApiOperation(value = "查询物联设备树", notes = "查询物联设备树")
    @PostMapping("/industry/device/getIotDeviceTree")
    Result<List<DeviceTreeVO>> getIotDeviceTree(@RequestBody IotDeviceVO deviceInfoVO);


    @ApiOperation(value = "查询雷达设备树", notes = "查询雷达设备树")
    @PostMapping("/industry/device/getRadarDeviceTree")
    Result<List<DeviceTreeVO>> getRadarDeviceTree(@RequestBody IotDeviceVO deviceInfoVO);


    @ApiOperation(value = "查询行业传感器设备信息", notes = "查询行业传感器设备信息")
    @PostMapping("/industry/device/pageIndSensorDevice")
    TableDataInfo pageIndSensorDevice(@RequestBody PageDeviceVO deviceInfoReq);

    @ApiOperation(value = "雷达平台查询应用平台获取雷达对应的摄像机列表", notes = "雷达平台查询应用平台获取雷达对应的摄像机列表")
    @PostMapping("/industry/device/getRadarDeviceConfigInfo")
    Result<List<RadarDeviceVO>> getRadarDeviceConfigInfo(@RequestBody RadarDeviceReqDTO radarDeviceReqDTO);

    @ApiOperation(value = "根据组织机构id查询物联设备", notes = "根据组织机构id查询物联设备")
    @PostMapping("/industry/device/getIotDeviceByDept")
    Result<List<IotDeviceEntity>> getIotDeviceByDept(@RequestBody IotDeviceEntity iotDeviceEntity);


    @ApiOperation(value = "根据地域信息按设备类型查询物联设备数据", notes = "根据地域信息按设备类型查询物联设备数据")
    @PostMapping("/industry/device/getIotGroupByDeviceType")
    Result<List<IotDeviceTypeData>> getIotGroupByDeviceType(@RequestBody IotDeviceAreaEntity iotDeviceAreaEntity);


    @ApiOperation(value = "根据客户信息获取物联设备数量", notes = "根据客户信息获取物联设备数量")
    @PostMapping("/industry/device/getSensorDeviceCount")
    Result<SensorDeviceCountVO> getSensorDeviceCount(@RequestBody SensorDeviceCountReqDTO request);


    @ApiOperation(value = "查询物联因子相关信息", notes = "查询物联因子相关信息")
    @PostMapping("/industry/device/pageIotMonitorInfo")
    Result<PageInfo<PageIotMonitorVO>> pageIotMonitorInfo(@RequestBody PageIotMonitorDTO param);


    @ApiOperation(value = "查询指定租户，指定行业，指定经纬度3公里范围全部设备，按照距离从近到远排序")
    @PostMapping("/industry/device/queryDevice3km")
    Result<List<IndustryDeviceTree>> queryDevice3km(@RequestBody DeviceLayerReq deviceLayerReq);

    @ApiOperation(value = "根据关键字查询设备的基本信息接口")

    @PostMapping("/industry/device/queryDeviceList")
    TableDataInfo queryDeviceList(@RequestBody DeviceInfoVO deviceInfoVO);


    @ApiOperation(value = "根据设备编码和类型，获取设备详细信息")
    @PostMapping("/industry/device/queryDeviceDetail")
    Result queryDeviceDetail(@RequestBody DeviceQueryVO deviceInfoVO);


    @ApiOperation(value = "根据设备编码，获取附近指定范围的所有设备")
    @PostMapping("/industry/device/queryDeviceByDistance")
    Result<List<DeviceByDistanceDTO>> queryDeviceByDistance(@RequestBody DeviceQueryVO deviceQueryVO);


    @ApiOperation("根据设备编码查询设备信息")
    @PostMapping("/video/device/queryDeviceForWE")
    Result<DeviceChannelInfoVO> queryDevicesForWE(@RequestBody SiteRequestDTO siteRequestDTO);

    @ApiOperation("根据站址编码查询设备列表")
    @PostMapping("/video/device/queryDevices")
    TableDataInfo queryDevices(@RequestBody SiteRequestDTO siteRequestDTO);


    @PostMapping("/video/device/getDeviceImage")
    Result<List<String>> getDeviceImage(@RequestBody DeviceInfoVO deviceInfoVO);



    @ApiOperation("根据站址编码查询设备聚合列表")
    @PostMapping("/video/device/querySitesForWE")
    Result<Map<String, Object>> querySitesForWE(@RequestBody SiteRequestDTO siteRequestDTO);

    @ApiOperation(value = "根据摄像机对应的标签，部门编码、算法，展示该标签下或者组织机构下（含本组织机构下所有的子节点）或者算法下或者摄像机类型下，所对应的的摄像机信息。")
    @PostMapping("/industry/device/queryDevicesByCondition")
    TableDataInfo queryDevicesByCondition(@RequestBody DevicesByConditionVO devicesByConditionVO);

    @ApiOperation(value = "新增摄像机查询总数接口，根据租户ID或者组织列表，查询该租户下或者所属组织列表下，对应组织的摄像机总数")
    @PostMapping("/industry/device/queryDevicesStatistics")
    Result<DevicesStatisticsResVO> queryDevicesStatistics(@RequestBody DevicesStatisticsReqVO devicesStatisticsReqVO);

    @PostMapping("/video/device/querySiteCodeAndDevices")
    @ApiOperation("功能描述: 大屏设备打点，新接口，返回站址，站址下放设备list")
    Result<List<IndustryDeviceTree>> querySiteCodeAndDevices(@RequestBody SiteRequestDTO siteRequestDTO);

    @PostMapping("/video/device/lookHere")
    @ApiOperation("看这里")
    Result<DetectResultVO> lookHere(@RequestBody DeviceLayerReq deviceLayerReq);


    @PostMapping("/industry/device/radarTargetFollowRelay")
    @ApiOperation("雷达联动接力/取消接力接口")
    Result<RadarTargetFollowResultVO> radarTargetFollowRelay(@RequestBody RadarTargetFollowRelayVO param);


    @PostMapping("/industry/device/radarTargetFollowLookTo")
    @ApiOperation("雷达联动同时随动/取消同时随动接口")
    Result<RadarTargetFollowResultVO> radarTargetFollowLookTo(@RequestBody RadarTargetFollowLookToVO param);


    @GetMapping("/video/device/getFileInfoByDeviceCode")
    @ApiOperation("根据设备编码查询设备附件信息")
    Result<DeviceAttachmentVO> getFileInfoByDeviceCode(@RequestParam("deviceCode") String deviceCode);



    @ApiOperation(value = "雷达联动获取目标设备列表接口")
    @PostMapping("/industry/device/queryRadarTargetList")
    Result<List<RadarDeviceStatusVO>> queryRadarTargetList(@RequestBody RadarTargetDTO radarTargetDTO);


    @ApiOperation(value = "查询指定范围内的设备")
    @PostMapping("/video/warningEvent/querySitesByLocal")
    Result<List<SiteResultVO>> querySitesByLocal(@RequestBody SiteRequestDTO siteRequestDTO);

    @ApiOperation(value = "根据图层ID获取关联的设备")
    @PostMapping("/video/getDevicesByLayerId")
    Result<List<String>> getDevicesByLayerId(@RequestBody MonitorTreeByLayerIdReq req);


    @ApiOperation("查询摄像机俯仰角水平角")
    @PostMapping("/industry/device/getViewPitchAngle")
    Result<JSONObject> getViewPitchAngle(@RequestBody DeviceIndReqDTO deviceInfoReq);

    @ApiOperation("分页查询摄像机图层")
    @PostMapping("/industry/device/pageDeviceLayer")
    Result<PageInfo<IndustryDeviceTree>> pageDeviceLayer(@RequestBody DeviceLayerReq deviceLayerReq);
}
