package com.chinatower.device.api.deprecated;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.platform.entity.device.*;
import com.chinatower.device.api.factory.VideoDeviceFallbackFactory;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;


@FeignClient(contextId = "videoDeviceService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = VideoDeviceFallbackFactory.class)
public interface IVideoDeviceServiceMigrate {

    @PostMapping("/video/getMonitorTree")
    Result<List<DeviceTreeVO>> getMonitorTree(@RequestBody DeviceReqDTO deviceReqDTO);

    @PostMapping("/video/getMonitorList")
    @Deprecated
    Result<List<DeviceTreeVO>> getMonitorList(@RequestBody DeviceReqDTO deviceReqDTO);

    @PostMapping("/video/getMonitorOrgTree")
    @Deprecated
    Result<List<DeviceTreeVO>> getMonitorOrgTree(@RequestBody DeviceReqDTO deviceReqDTO);


    @PostMapping("/video/device/synchDeviceAlgorithms")
    Result<JSONObject> synchDeviceAlgorithms(@Validated @RequestBody List<AlgorithmParamBodyVO> param);

    @PostMapping("/video/device/batchSaveOrUpdateDevice")
    Result<String> batchSaveOrUpdateDevice(@RequestBody List<SaveOrUpdateDeviceDTO> deviceDTOList);

    @PostMapping("/iotdevice/batchSaveOrUpdateIotDevice")
    Result<String> batchSaveOrUpdateIotDevice(@RequestBody List<IotDeviceMsgDTO> iotDevices);

    @PostMapping("/iotdevice/getTenantIotDevConf")
    Result<TenantIotDeviceConfVO> getTenantIotDevConf(@Valid @RequestBody TenantIotDeviceConfVO qryCond);

    @PostMapping("/video/getTenantMonitorLevel")
    Result<List<DeviceTreeVO>> getTenantMonitorLevel(@RequestBody DeviceReqDTO deviceReqDTO);

    @ApiOperation("根据平台编码查询设备信息")
    @PostMapping(value = "/video/pageDeviceByPlatform")
    TableDataInfo pageDeviceByPlatform(@RequestBody PageDeviceVO pageDeviceVO);

    @ApiOperation(value = "获取标签设备树",notes = "获取标签设备树")
    @PostMapping("/video/getMonitorLabelTree")
    @Deprecated
     Result<List<DeviceTreeVO>> getMonitorLabelTree(@RequestBody DeviceReqDTO deviceReqDTO);

    @ApiOperation(value = "获取标签设备树",notes = "获取标签设备树")
    @PostMapping("/video/getMonitorLabelTreeNew")
    @Deprecated
     Result<List<DeviceTreeVO>> getMonitorLabelTreeNew(@RequestBody DeviceReqDTO deviceReqDTO);

    @ApiOperation(value = "更新设备用户数缓存",notes = "更新设备用户数缓存")
    @PostMapping("/video/updateDeviceUserNum")
    Result<String> updateDeviceUserNum(@RequestBody DeviceVideoDTO deviceVideoDTO);

    /**
      * 查询无人机设备距离信息
      * @requestDemo
      * {
      *     "longitude": "116.3699518371145",
      *     "latitude": "39.921861623877284",
      *     "radius": "3000"
      * }
      * @responseDemo
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1692340232313,
      *     "data": [
      *         {
      *             "deviceCode": "51150200833007000001",
      *             "name": "三江新区白莲",
      *             "location": "设备地址",
      *             "distance": "2730.8431587270625",
      *             "longitude": "116.396908000",
      *             "latitude": "39.908607000"
      *         },
      *         {
      *             "deviceCode": "46900200833007000001",
      *             "name": "琼海牛路岭2",
      *             "location": "北京市西城区金融街街道太平桥大街15号全国政协",
      *             "distance": "551.2642300474115",
      *             "longitude": "116.363741000",
      *             "latitude": "39.920487000"
      *         },
      *         {
      *             "deviceCode": "469002008330070000011",
      *             "name": "琼海牛路岭1-变更",
      *             "location": "2222",
      *             "distance": "0.0",
      *             "longitude": "116.3699518371145",
      *             "latitude": "39.921861623877284"
      *         },
      *         {
      *             "deviceCode": "110105008330070000011",
      *             "name": "测试ccc-变更",
      *             "location": "2222",
      *             "distance": "0.0",
      *             "longitude": "116.3699518371145",
      *             "latitude": "39.921861623877284"
      *         },
      *         {
      *             "deviceCode": "110101016000",
      *             "name": "天坛街道777",
      *             "location": "北京市东城区东华门街道天安门",
      *             "distance": "2730.8431587270625",
      *             "longitude": "116.396908000",
      *             "latitude": "39.908607000"
      *         }
      *     ]
      * }
      * @param       param
      * @return
      */
    @Deprecated
    @ApiOperation(value = "查询无人机设备距离信息",notes = "查询无人机设备距离信息")
    @PostMapping("/uav/device/queryUavDeviceDistanceInfo")
    Result<List<DeviceDistanceVO>> queryUavDeviceDistanceInfo(@RequestBody DeviceDistanceDTO param);

    /**
     *
      * 查询喇叭设备距离信息
      * @apiNote  查询喇叭设备距离信息
      * @apiVersion 3.3
      * @tag 三、设备能力
      * @requestDemo
      * {
      *     "longitude": "116.3699518371145",
      *     "latitude": "39.921861623877284",
      *     "radius": "3000"
      * }
      * @responseDemo
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1692340232313,
      *     "data": [
      *         {
      *             "deviceCode": "51150200833007000001",
      *             "name": "三江新区白莲",
      *             "location": "设备地址",
      *             "distance": "2730.8431587270625",
      *             "longitude": "116.396908000",
      *             "latitude": "39.908607000"
      *         },
      *         {
      *             "deviceCode": "46900200833007000001",
      *             "name": "琼海牛路岭2",
      *             "location": "北京市西城区金融街街道太平桥大街15号全国政协",
      *             "distance": "551.2642300474115",
      *             "longitude": "116.363741000",
      *             "latitude": "39.920487000"
      *         },
      *         {
      *             "deviceCode": "469002008330070000011",
      *             "name": "琼海牛路岭1-变更",
      *             "location": "2222",
      *             "distance": "0.0",
      *             "longitude": "116.3699518371145",
      *             "latitude": "39.921861623877284"
      *         },
      *         {
      *             "deviceCode": "110105008330070000011",
      *             "name": "测试ccc-变更",
      *             "location": "2222",
      *             "distance": "0.0",
      *             "longitude": "116.3699518371145",
      *             "latitude": "39.921861623877284"
      *         },
      *         {
      *             "deviceCode": "110101016000",
      *             "name": "天坛街道777",
      *             "location": "北京市东城区东华门街道天安门",
      *             "distance": "2730.8431587270625",
      *             "longitude": "116.396908000",
      *             "latitude": "39.908607000"
      *         }
      *     ]
      * }
      * @param     param
      * @return
      */
    @ApiOperation(value = "查询喇叭设备距离信息",notes = "查询喇叭设备距离信息")
    @PostMapping("/hornDevice/queryHornDeviceDistanceInfo")
    Result<List<DeviceDistanceVO>> queryHornDeviceDistanceInfo(@RequestBody DeviceDistanceDTO param);

    /**
      * 查询传感器时间段数据
      * @apiNote 查询传感器时间段数据
      * @apiVersion 3.4
      * @tag 三、设备能力
      * @requestDemo
      * {
      *   "startDate": "2023-05-24 00:00:00",
      *   "endDate": "2023-05-24 23:59:59",
      *   "deviceCode": "10010151853624"
      * }
      * @responseDemo
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "data": [
      *         {
      *             "no2": "1232",
      *             "monitorTime": "2023-05-24 15:00:00",
      *             "o3": "123213",
      *             "pm25": "2132132",
      *             "so2": "43133",
      *             "pm10": "43243",
      *             "deviceCode": "10010151853624",
      *             "pressure": "3333",
      *             "co": "1222"
      *         },
      *         {
      *             "no2": "1",
      *             "monitorTime": "2023-05-24 00:00:00",
      *             "o3": "1",
      *             "pm25": "1",
      *             "so2": "1",
      *             "pm10": "1",
      *             "deviceCode": "10010151853624",
      *             "pressure": "1",
      *             "co": "1"
      *         }
      *     ]
      * }
      * @param   deviceReqDTO
      * @return
      */
    @ApiOperation(value = "查询传感器时间段数据",notes = "查询传感器时间段数据")
    @PostMapping("/iotdevice/getSensorPeriodData")
    Result<List<SensorAttrMapVO>> getSensorPeriodData(@RequestBody DeviceReqDTO deviceReqDTO);

    /**
      * 查询雷达距离信息
      * {
      *     "longitude": "116.3699518371145",
      *     "latitude": "39.921861623877284",
      *     "radius": "3000"
      * }
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1692340232313,
      *     "data": [
      *         {
      *             "deviceCode": "51150200833007000001",
      *             "name": "三江新区白莲",
      *             "location": "设备地址",
      *             "distance": "2730.8431587270625",
      *             "longitude": "116.396908000",
      *             "latitude": "39.908607000"
      *         },
      *         {
      *             "deviceCode": "46900200833007000001",
      *             "name": "琼海牛路岭2",
      *             "location": "北京市西城区金融街街道太平桥大街15号全国政协",
      *             "distance": "551.2642300474115",
      *             "longitude": "116.363741000",
      *             "latitude": "39.920487000"
      *         },
      *         {
      *             "deviceCode": "469002008330070000011",
      *             "name": "琼海牛路岭1-变更",
      *             "location": "2222",
      *             "distance": "0.0",
      *             "longitude": "116.3699518371145",
      *             "latitude": "39.921861623877284"
      *         },
      *         {
      *             "deviceCode": "110105008330070000011",
      *             "name": "测试ccc-变更",
      *             "location": "2222",
      *             "distance": "0.0",
      *             "longitude": "116.3699518371145",
      *             "latitude": "39.921861623877284"
      *         },
      *         {
      *             "deviceCode": "110101016000",
      *             "name": "天坛街道777",
      *             "location": "北京市东城区东华门街道天安门",
      *             "distance": "2730.8431587270625",
      *             "longitude": "116.396908000",
      *             "latitude": "39.908607000"
      *         }
      *     ]
      * }
      * @param    param
      * @return
      */
    @Deprecated
    @ApiOperation(value = "查询雷达距离信息",notes = "查询喇叭设备距离信息")
    @PostMapping("/radar/queryRadarDevDistanceInfo")
    Result<List<DeviceDistanceVO>> queryRadarDevDistanceInfo(@RequestBody DeviceDistanceDTO param);

    /**
      * 查询物联设备距离信息
      * @requestDemo
      * {
      *     "longitude": "116.3699518371145",
      *     "latitude": "39.921861623877284",
      *     "radius": "3000"
      * }
      * @responseDemo
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1692340232313,
      *     "data": [
      *         {
      *             "deviceCode": "51150200833007000001",
      *             "name": "三江新区白莲",
      *             "location": "设备地址",
      *             "distance": "2730.8431587270625",
      *             "longitude": "116.396908000",
      *             "latitude": "39.908607000"
      *         },
      *         {
      *             "deviceCode": "46900200833007000001",
      *             "name": "琼海牛路岭2",
      *             "location": "北京市西城区金融街街道太平桥大街15号全国政协",
      *             "distance": "551.2642300474115",
      *             "longitude": "116.363741000",
      *             "latitude": "39.920487000"
      *         },
      *         {
      *             "deviceCode": "469002008330070000011",
      *             "name": "琼海牛路岭1-变更",
      *             "location": "2222",
      *             "distance": "0.0",
      *             "longitude": "116.3699518371145",
      *             "latitude": "39.921861623877284"
      *         },
      *         {
      *             "deviceCode": "110105008330070000011",
      *             "name": "测试ccc-变更",
      *             "location": "2222",
      *             "distance": "0.0",
      *             "longitude": "116.3699518371145",
      *             "latitude": "39.921861623877284"
      *         },
      *         {
      *             "deviceCode": "110101016000",
      *             "name": "天坛街道777",
      *             "location": "北京市东城区东华门街道天安门",
      *             "distance": "2730.8431587270625",
      *             "longitude": "116.396908000",
      *             "latitude": "39.908607000"
      *         }
      *     ]
      * }
      * @param      param
      * @return
      */
    @Deprecated
    @ApiOperation(value = "查询物联设备距离信息",notes = "查询喇叭设备距离信息")
    @PostMapping("/iotdevice/queryIotDevDistanceInfo")
    Result<List<DeviceDistanceVO>> queryIotDevDistanceInfo(@RequestBody DeviceDistanceDTO param);


    @PostMapping("/deviceDiy/getList")
    Result<List<DeviceDiyTreeVO>> getList(@RequestBody DeviceDiyTreeVO deviceInfoVO);

    @PostMapping("/deviceDiy/getDiyTree")
    Result<List<DeviceDiyTreeResultVO>> getDiyTree(@RequestBody DeviceDiyTreeResultVO deviceInfoVO);

    @PostMapping("/video/device/getDeviceListByCodes")
    Result<List<DeviceIndDTO>> getDeviceListByCodes(@RequestBody List<String> deviceCodes);

    @PostMapping("/video/device/getDeviceTreeForestry")
    Result<List<TreeSelect>> getDeviceTreeForestry(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    @PostMapping("/video/device/getWoodProcessingPlantDeivceInfo")
    Result<List<WoodProcessingPlantDeivceVO>> getWoodProcessingPlantDeivceInfo(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    @PostMapping("/video/device/getAlarmNumByDevice")
    Result<List<WoodProcessingPlantDeivceVO>> getAlarmNumByDevice(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    @PostMapping("/iotdevice/getTenantIotDevConfBatch")
    Result<List<TenantIotDeviceConfVO>> getTenantIotDevConfBatch(@RequestBody TenantIotDeviceConfVO qryCond);

    @PostMapping("/iotdevice/getTenantIotDevConfBatchV2")
    Result<List<TenantIotDeviceConfVO>> getTenantIotDevConfBatchV2(@RequestBody TenantIotDeviceConfVO qryCond);

    @PostMapping("/video/device/getWoodProcessingPlantDeivceInfoPage")
    Result<PageInfo<WoodProcessingPlantDeivceVO>> getWoodProcessingPlantDeivceInfoPage(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    @PostMapping("/video/device/queryDeviceComprehensive")
    Result<PageInfo<DeviceComprehensiveVO>> queryDeviceComprehensive(@RequestBody @Validated DeviceComprehensiveReq deviceComprehensiveReq);
}
