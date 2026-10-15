package com.chinatower.industry.api.factory;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.entity.dto.DeviceModelDTO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UserDeviceCollectionDTO;
import com.chinatower.device.entity.dto.UserDeviceCollectionQueryDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.api.deprecated.IIndustryDeviceServiceMigrate;
import com.chinatower.industry.dto.*;
import com.chinatower.industry.vo.*;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.github.pagehelper.PageInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @Name IndustryDeviceFallbackFactory
 * @Description 行业应用设备服务降级处理
 * @Author Chensw
 * @Date 2022/03/2022/3/24 09:40
 */
@Component
public class IndustryDeviceFallbackFactory implements FallbackFactory<IIndustryDeviceServiceMigrate> {
    @Override
    public IIndustryDeviceServiceMigrate create(Throwable throwable) {
        return new IIndustryDeviceServiceMigrate()
        {
            @Override
            public Result<DeviceIndDTO> getDeviceChannel(DeviceIndReqDTO deviceIndReq) {
                return Result.fail("调用查询摄像头信息方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IndustryDeviceTree>> queryMonitorIndTree(MonitorTreeInfReq monitorTreeInfReq) {
                return Result.fail("调用查询行业应用摄像机树方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceIndDTO>> queryDevicesBySite(DeviceIndReqDTO deviceIndReqDTO) {
                return Result.fail("调用通过站址信息查询设备方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IndustryDeviceTree>> queryDeviceLayer(DeviceLayerReq deviceLayerReq) {
                return Result.fail("调用查询摄像机图层方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IndustryDeviceTree>> querySensorLayer(@RequestBody DeviceLayerReq deviceLayerReq) {
                return Result.fail("调用查询传感器方法失败:" + throwable.getMessage());
            }

            @Override
            public Result addOrCancelCollections(UserDeviceCollectionDTO userDeviceCollectionDTO) {
                return Result.fail("调用新增/取消收藏方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<UserDeviceCollectionQueryVO>> queryCollections(UserDeviceCollectionQueryDTO param) {
                return Result.fail("调用查询收藏方法失败:" + throwable.getMessage());
            }

            @Override
            public Result<DeviceModelDTO> getDeviceModelInfo(DeviceIndReqDTO deviceInfoReq){
                return Result.fail("调用查询摄像机可视域信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceModelDTO>> getDeviceVisibleBySite(@RequestBody DeviceIndReqDTO deviceInfoReq) {
                return Result.fail("调用根据站址里列表查询摄像机可视域失败:" + throwable.getMessage());
            }

            @Override
            public Result<DeviceModelDTO> getDeviceCalibration(DeviceIndReqDTO deviceInfoReq) {
                return Result.fail("调用查询摄像机标定信息失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo pageSensorDevice(@RequestBody PageDeviceVO deviceInfoReq) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public TableDataInfo pageSensorDeviceInfo(PageSensorDeviceVO deviceInfoReq) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public TableDataInfo pageIotDeviceByDeviceType(PageIotDeviceDTO iotDeviceDTO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public TableDataInfo pageAlarmTypeCodeByDeviceType(PageIotDeviceDTO iotDeviceDTO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public TableDataInfo pageRadarDevice(@RequestBody PageRadarDeviceVO pageRadarDeviceReq) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public Result<List<DeviceTreeVO>> getIotDeviceTree(IotDeviceVO deviceInfoVO) {
                return Result.fail("查询物联设备树失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceTreeVO>> getRadarDeviceTree(@RequestBody IotDeviceVO deviceInfoVO) {
                return Result.fail("查询雷达设备树失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo pageIndSensorDevice(@RequestBody PageDeviceVO deviceInfoReq) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public Result<List<RadarDeviceVO>> getRadarDeviceConfigInfo(RadarDeviceReqDTO radarDeviceReqDTO) {
                return Result.fail("雷达平台查询应用平台获取雷达对应的摄像机列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IotDeviceEntity>> getIotDeviceByDept(@RequestBody IotDeviceEntity iotDeviceEntity) {
                return Result.fail("根据组织机构id查询物联设备失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<IotDeviceTypeData>> getIotGroupByDeviceType(@RequestBody IotDeviceAreaEntity iotDeviceAreaEntity) {
                return Result.fail("根据地域信息按设备类型查询物联设备数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<SensorDeviceCountVO> getSensorDeviceCount(SensorDeviceCountReqDTO request) {
                return Result.fail("根据客户息查询物联设备数量失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<PageIotMonitorVO>> pageIotMonitorInfo(PageIotMonitorDTO param) {
                return Result.fail("查询物联因子相关信息失败:" + throwable.getMessage());
            }

            @Override
			public Result<List<IndustryDeviceTree>> queryDevice3km(DeviceLayerReq deviceLayerReq) {
				return Result.fail("调用查询指定租户，指定行业，指定经纬度3公里范围全部设备，按照距离从近到远排序方法失败:" + throwable.getMessage());
			}

            @Override
            public TableDataInfo queryDeviceList(DeviceInfoVO deviceInfoVO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public Result queryDeviceDetail(DeviceQueryVO deviceInfoVO) {
                return Result.fail("调用失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<DeviceByDistanceDTO>> queryDeviceByDistance(DeviceQueryVO deviceQueryVO) {
                return Result.fail("调用查询指定租户，指定行业，某设备几公里范围全部设备，按照距离从近到远排序方法失败:" + throwable.getMessage());
            }
            @Override
            public Result<DeviceChannelInfoVO> queryDevicesForWE(@RequestBody SiteRequestDTO siteRequestDTO){
                return Result.fail("根据设备编码查询设备信息失败:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryDevices(SiteRequestDTO siteRequestDTO) {
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(HttpStatus.ERROR);
                return rspData;
            }

            @Override
            public Result<List<String>> getDeviceImage(DeviceInfoVO deviceInfoVO) {
                return Result.fail("根据设备编码查询设备图片信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<Map<String, Object>> querySitesForWE(SiteRequestDTO siteRequestDTO) {
                return Result.fail("根据站址编码查询设备聚合列表:" + throwable.getMessage());
            }

            @Override
            public TableDataInfo queryDevicesByCondition(DevicesByConditionVO devicesByConditionVO) {
                TableDataInfo response = new TableDataInfo();
                response.setCode(HttpStatus.ERROR);
                return response;
            }

            @Override
            public Result<DevicesStatisticsResVO> queryDevicesStatistics(@RequestBody DevicesStatisticsReqVO devicesStatisticsReqVO) {
                TableDataInfo response = new TableDataInfo();
                response.setCode(HttpStatus.ERROR);
                return Result.fail("queryDevicesStatistics:" + throwable.getMessage());
            }

            @Override
            public Result<List<IndustryDeviceTree>> querySiteCodeAndDevices(@RequestBody SiteRequestDTO siteRequestDTO) {
                TableDataInfo response = new TableDataInfo();
                response.setCode(HttpStatus.ERROR);
                return Result.fail("querySiteCodeAndDevices:" + throwable.getMessage());
            }

            @Override
            public Result<DetectResultVO> lookHere(@RequestBody DeviceLayerReq deviceLayerReq) {
                TableDataInfo response = new TableDataInfo();
                response.setCode(HttpStatus.ERROR);
                return Result.fail("lookHere:" + throwable.getMessage());
            }

            @Override
            public Result<RadarTargetFollowResultVO> radarTargetFollowRelay(RadarTargetFollowRelayVO param) {
                return Result.fail("雷达联动接力/取消接力失败:" + throwable.getMessage());
            }

            @Override
            public Result<RadarTargetFollowResultVO> radarTargetFollowLookTo(RadarTargetFollowLookToVO param) {
                return Result.fail("达联动同时随动/取消同时随动失败:" + throwable.getMessage());
            }

            @Override
            public Result<DeviceAttachmentVO> getFileInfoByDeviceCode(String deviceCode) {
                return Result.fail("根据设备编码查询设备附件信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<RadarDeviceStatusVO>> queryRadarTargetList(RadarTargetDTO radarTargetDTO) {
                return Result.fail("获取目标设备列表失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<SiteResultVO>> querySitesByLocal(SiteRequestDTO siteRequestDTO) {
                return Result.fail("查询指定范围内的设备:" + throwable.getMessage());
            }

            @Override
            public Result<List<String>> getDevicesByLayerId(MonitorTreeByLayerIdReq req) {
                return Result.fail("根据图层ID获取关联的设备:" + throwable.getMessage());
            }

            @Override
            public Result<JSONObject> getViewPitchAngle(DeviceIndReqDTO deviceInfoReq) {
                return Result.fail("查询摄像机俯仰角水平角失败:" + throwable.getMessage());
            }

            @Override
            public Result<PageInfo<IndustryDeviceTree>> pageDeviceLayer(DeviceLayerReq deviceLayerReq) {
                return Result.fail("分页查询摄像机图层失败:" + throwable.getMessage());
            }
        };
    }
}
