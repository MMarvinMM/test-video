package com.chinatower.industry.api;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.IDeviceCommonService;
import com.chinatower.device.api.deprecated.IIndustryDeviceServiceMigrate;
import com.chinatower.device.entity.dto.DeviceModelDTO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UserDeviceCollectionDTO;
import com.chinatower.device.entity.dto.UserDeviceCollectionQueryDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.dto.*;
import com.chinatower.industry.vo.*;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class IIndustryDeviceService {

    @Autowired
    private IIndustryDeviceServiceMigrate industryDeviceServiceBak;

    @Autowired
    private IDeviceCommonService iDeviceCommonService;


    @ApiOperation(value = "查询摄像头信息", notes = "查询摄像头信息")
    /**
     * 查询摄像头信息
     * @apiNote 3.1查询摄像头信息
     * @apiVersion 3.1
     * @tag 设备能力
     * @requestDemo {"deviceCode":"32120400831327000001"}
     * @responseDemo
     * 返回参数data下字段解析：
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		deviceStatus：设备状态:0在线，1离线
     * 		provinceCode：省份
     * 		provinceName：省份名称
     * 		cityCode：地市
     * 		cityName：地市名称
     * 		countyCode：区县
     * 		countyName：区县名称
     * 		streetCode：街道/乡镇
     * 		streetName：街道名称
     * 		admCode：行政区域
     * 		categoryCode N SIntegerring 设备类型
     * 		location：站址地址
     * 		longitude：经度
     * 		latitude：纬度
     * 		height：设备挂高，单位：米
     * 	    channels：通道列表
     * 		  channelCode：通道编码
     * 		  channelName：通道名称
     * 		  channelStatus：通道状态
     * 		  visualRange：可视范围
     * 		  horizRange：水平辐射角度
     * 		  vertiRange：俯仰辐射
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1648198151649,
     *     "data":{
     *         "deviceCode":"32120400831327000001",
     *         "deviceName":"2",
     *         "deviceStatus":0,
     *         "provinceCode":"320000",
     *         "provinceName":"",
     *         "cityCode":"321200",
     *         "cityName":"",
     *         "countyCode":"321204",
     *         "countyName":"",
     *         "admCode":"321204",
     *         "categoryCode":1,
     *         "location":"1",
     *         "longitude":"120.100736",
     *         "latitude":"32.451762",
     *         "height":"10.0",
     *         "channels":[
     *             {
     *                 "channelCode":"32120400831310000001",
     *                 "channelName":"2_0",
     *                 "channelStatus":0,
     *                 "visualRange":"",
     *                 "horizRange":"",
     *                 "vertiRange":""
     *             }
     *         ]
     *     }
     * }
     */
    @PostMapping("/industry/device/getDeviceChannel")
    public Result<DeviceIndDTO> getDeviceChannel(@Valid @RequestBody DeviceIndReqDTO deviceIndReq){
        SiteRequestDTO siteRequestDTO = new SiteRequestDTO();
        BeanUtils.copyProperties(deviceIndReq, siteRequestDTO);
        Result<DeviceChannelInfoVO> deviceChannelInfoVOResult = industryDeviceServiceBak.queryDevicesForWE(siteRequestDTO);
        DeviceChannelInfoVO data = deviceChannelInfoVOResult.getData();

        DeviceIndDTO result = new DeviceIndDTO();
        if(ObjectUtil.isNotEmpty(data)){
            BeanUtils.copyProperties(data, result);
            List<ChannelInfoScrVO> channels = data.getChannels();
            List<ChannelIndDTO> children = new ArrayList<>();
            for (ChannelInfoScrVO channel : channels) {
                ChannelIndDTO channelInfoVO = new ChannelIndDTO();
                BeanUtils.copyProperties(channel, channelInfoVO);
                children.add(channelInfoVO);
            }
            result.setChannels(children);
        }

        return Result.data(result);
    }

    @ApiOperation("查询行业应用摄像机树")
    /**
     * 查询行业应用摄像机树
     * @apiNote 3.3查询行业应用摄像机树
     * @apiVersion 3.3
     * @tag 设备能力
     * @requestDemo
     * {
     *     "displayMode":1,
     *     "isCollection":"1",
     *     "keyWords":"rr"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		onLineNumber：在线通道数量
     * 		number：全部通道数量
     * 		labelId：设备状态:0在线，1离线
     * 		labelName：设备挂高，单位：米
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		tenantId：租户编码
     * 		channelCode：通道编码
     * 		channelName：通道名称
     * 	    categoryCode： 设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     * 		channelStatus：通道状态
     * 		longitude：经度
     * 		latitude：纬度
     * 		siteCode：站址编码
     * 		deptId：部门编码
     * 		deptName：部门名称
     * 		provinceId：省编码
     * 		provinceName：省名称
     * 		cityId：市编码
     * 		cityName：市编码
     * 		countyId：区编码
     * 		countyName：区名称
     *
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1648085368369,
     *     "data":[
     *         {
     *             "onLineNumber":0,
     *             "number":1,
     *             "labelId":"225",
     *             "labelName":"",
     *             "list":[
     *                 {
     *                     "deviceCode":"36040200831327000005",
     *                     "tenantId":"TEN100001",
     *                     "channelCode":"36040200001130000003",
     *                     "channelName":"36040200001130000003",
     *                     "channelStatus":1,
     *                     "deviceName":"36040200831327000005",
     *                     "longitude":"116.1855550000",
     *                     "latitude":"29.7491660000",
     *                     "siteCode":"360402908000000206",
     *                     "categoryCode":"1"
     *                 }
     *             ]
     *         }
     *     ]
     * }
     */
    @PostMapping("/industry/device/queryMonitorIndTree")
    public Result<List<IndustryDeviceTree>> queryMonitorIndTree(@RequestBody @Valid MonitorTreeInfReq monitorTreeInfReq){
        return industryDeviceServiceBak.queryMonitorIndTree(monitorTreeInfReq);
    }

    @ApiOperation(value = "通过站址信息查询设备", notes = "通过站址信息查询设备")
    /**
     * 通过站址信息查询设备
     * @apiNote 3.2通过站址信息查询设备
     * @apiVersion 3.2
     * @tag 设备能力
     * @requestDemo
     * {
     *     "siteCode":"441625700000223702",
     *     "siteName":"热水东江源"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		deviceStatus：设备状态:0在线，1离线
     * 		height：设备挂高，单位：米
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1648107064634,
     *     "data":[
     *         {
     *             "deviceCode":"100000000123",
     *             "deviceName":"123",
     *             "deviceStatus":0,
     *             "height":"24.1"
     *         }
     *     ]
     * }
     */
    @PostMapping("/industry/device/queryDevicesBySite")
    public Result<List<DeviceIndDTO>> queryDevicesBySite(@RequestBody DeviceIndReqDTO deviceIndReqDTO){
        return industryDeviceServiceBak.queryDevicesBySite(deviceIndReqDTO);
    }

    @ApiOperation(value = "查询摄像机图层")
    /**
     * 查询摄像机图层
     * @apiNote 3.4查询摄像机图层
     * @apiVersion 3.4
     * @tag 设备能力
     * @requestDemo
     * {
     *     "latitude":"122.111",
     *     "longitude":"37.11",
     *     "distance":"10",
     *     "provinceId":"110000",
     *     "cityId":"110100",
     *     "countyId":"110108"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		longitude：经度
     * 		latitude：纬度
     * 		siteCode：站址编码
     * 		siteName：站址名称
     * 		distance：距离
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		deviceStatus：设备状态:0在线，1离线
     * 		height：设备挂高，单位：米
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1648107167647,
     *     "data":[
     *         {
     *             "longitude":"122.111",
     *             "latitude":"37.11",
     *             "siteCode":"100001111111122",
     *             "siteName":"22",
     *             "distance":"10.007935794096117",
     *             "list":[
     *                 {
     *                     "deviceCode":"100000000022",
     *                     "deviceName":"22",
     *                     "height":"24.1",
     *                     "deviceStatus":"0"
     *                 }
     *             ]
     *         }
     *     ]
     * }
     */
    @PostMapping("/industry/device/queryDeviceLayer")
    public Result<List<IndustryDeviceTree>> queryDeviceLayer(@RequestBody DeviceLayerReq deviceLayerReq){
        return industryDeviceServiceBak.queryDeviceLayer(deviceLayerReq);
    }

    /**
     * 查询传感器图层
     * @apiNote 查询传感器图层
     * @apiVersion 3.59
     * @tag 设备能力
     * @requestDemo
     * {
     *     "latitude":"122.111",
     *     "longitude":"37.11",
     *     "distance":"10",
     *     "provinceId":"110000",
     *     "cityId":"110100",
     *     "countyId":"110108"
     * }
     * @responseDemo
     * 返回参数解析：
     * {
     *     code:状态码
     *     msg:请求结果描述
     *     time:时间戳
     *     data:[
     *         {
     *             longitude:经度
     *             latitude:纬度
     *             siteCode:站址编码
     *             siteName:展示名称
     *             deviceCode:设备编码
     *             deviceName:设备名称
     *             height: 高度
     *             deviceStatus:设备状态
     *             location: 设备地址
     *             provinceId: 省份编码
     *             cityId: 地市编码
     *             countyId: 区县编码
     *             creatorDate: 创建时间
     *             modifierDate: 更新时间
     *         }
     *     ]
     * }
     *
     * 返回参数示例：
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1648107167647,
     *     "data":[
     *         {
     *             "longitude":"122.111",
     *             "latitude":"37.11",
     *             "siteCode":"100001111111122",
     *             "siteName":"22",
     *             "distance":"10.007935794096117",
     *             "list":[
     *                 {
     *                     "deviceCode":"100000000022",
     *                     "deviceName":"22",
     *                     "height":"24.1",
     *                     "deviceStatus":"0"
     *                 }
     *             ]
     *         }
     *     ]
     * }
     */
    @ApiOperation(value = "查询传感器图层")
    @PostMapping("/industry/device/querySensorLayer")
    public Result<List<IndustryDeviceTree>> querySensorLayer(@RequestBody DeviceLayerReq deviceLayerReq){
        return industryDeviceServiceBak.querySensorLayer(deviceLayerReq);
    }

    @ApiOperation(value = "新增/取消收藏")
    /**
     * 新增/取消收藏
     * @apiNote 3.5新增/取消收藏
     * @apiVersion 3.5
     * @tag 设备能力
     * @requestDemo
     * {
     *     "collObjType":"6",
     *     "collObjCode":[
     *         "11010500001310000017"
     *     ],
     *     "optType":"1"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"新增收藏成功",
     *     "time":1692241969932
     * }
     */
    @PostMapping("/industry/device/addOrCancelCollections")
    public Result addOrCancelCollections(@RequestBody UserDeviceCollectionDTO userDeviceCollectionDTO){
        return industryDeviceServiceBak.addOrCancelCollections(userDeviceCollectionDTO);
    }

    @ApiOperation(value = "查询收藏信息")
    /**
     * 查询收藏信息
     * @apiNote 3.11查询收藏信息
     * @apiVersion 3.11
     * @tag 设备能力
     * @requestDemo
     * {
     *     "collObjTypeList":[
     *         "1",
     *         "7"
     *     ]
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		userId：用户id
     * 		collObjType：收藏类型
     * 		collObjCode：收藏对象编码
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1684122211613,
     *     "data":[
     *         {
     *             "userId":"0cfedbc6576048d8895c374e8a116926",
     *             "collObjType":"7",
     *             "collObjCode":"11010000001131075044"
     *         },
     *         {
     *             "userId":"0cfedbc6576048d8895c374e8a116926",
     *             "collObjType":"1",
     *             "collObjCode":"11010000831327000043"
     *         },
     *         {
     *             "userId":"0cfedbc6576048d8895c374e8a116926",
     *             "collObjType":"1",
     *             "collObjCode":"11010500831130000031"
     *         }
     *     ]
     * }
     */
    @PostMapping("/industry/device/queryCollections")
    public Result<List<UserDeviceCollectionQueryVO>> queryCollections(@RequestBody UserDeviceCollectionQueryDTO param){
        return industryDeviceServiceBak.queryCollections(param);
    }

    @ApiOperation(value = "查询摄像机可视域信息", notes = "查询摄像机可视域信息")
    /**
     * 查询摄像机可视域信息
     * @apiNote 3.6查询摄像机可视域信息
     * @apiVersion 3.6
     * @tag 设备能力
     * @requestDemo {"deviceCode":"32120400831327000001"}
     * @responseDemo
     * 返回参数data下字段解析：
     * 		deviceCode：设备编码
     * 	    height:设备挂高，单位：米
     * 	    longitude：经度
     * 	    latitude：纬度
     * 	    modelId：设备型号Id
     * 		mfgrCode：摄像机厂家编码
     * 		deviceModelCode：摄像机型号编码
     * 		deviceType：摄像机类型 0-NVR,1-网络摄像机,2-AR摄像机,3-热成像摄像机,4、人脸卡口摄像机，5-车辆卡口摄像机；7-无人机
     * 		picSensorSize：图像传感器尺寸，英寸
     * 		picSensorLength：图像传感器宽度，毫米
     * 		picSensorWidth：图像传感器高度，毫米
     * 		minFocus：最小焦距
     * 		focusRange：焦距范围
     * 		horizViewRange：水平视场角
     * 		vertiViewRange：垂直视场角
     * 		horizRange：水平角范围
     * 		vertiRange：俯仰角范围
     * 		vertiDownNeg：水平向下为负  0否1是.
     * 		visualRange：可视距离，单位米
     *      pan：水平参数p值
     *      tilt：垂直参数t值
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1650702778972,
     *     "data":{
     *         "deviceCode":"11010800831327000008",
     *         "height":"10",
     *         "longitude":"116.28413364028117",
     *         "latitude":"39.95356071120486",
     *         "modelId":9,
     *         "mfgrCode":"dahua",
     *         "deviceModelCode":"DH-SD-8A1440XA-HNR",
     *         "deviceType":"1",
     *         "picSensorSize":"1/1.8",
     *         "picSensorLength":"7.18",
     *         "picSensorWidth":"5.32",
     *         "minFocus":"5.5",
     *         "focusRange":"5.5~220",
     *         "horizViewRange":"17.06",
     *         "vertiViewRange":"12.56",
     *         "horizRange":"86.00",
     *         "vertiRange":"0.90",
     *         "vertiDownNeg":"1",
     *         "remark":"",
     *         "visualRange":"3000",
     *         "pan":1,
     *         "tilt":0
     *     }
     * }
     */
    @PostMapping("/industry/device/getDeviceModelInfo")
    public Result<DeviceModelDTO> getDeviceModelInfo(@RequestBody DeviceIndReqDTO deviceInfoReq){
        return industryDeviceServiceBak.getDeviceModelInfo(deviceInfoReq);
    }

    @ApiOperation(value = "根据站址里列表查询摄像机可视域", notes = "根据站址里列表查询摄像机可视域")
    /**
     * 根据站址里列表查询摄像机可视域
     * @apiNote 3.7根据站址里列表查询摄像机可视域
     * @apiVersion 3.7
     * @tag 设备能力
     * @requestDemo
     * {
     *     "siteCodes":[
     *         "140581908000000420",
     *         "320404908000000121",
     *         "210411908000000407"
     *     ],
     *     "deviceCodes":[
     *         "14058100671327000001",
     *         "32040400011327000001",
     *         "21041100011327000001"
     *     ]
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		deviceCode：设备编码
     * 		longitude：经度
     * 		latitude：纬度
     * 		height：设备挂高，单位：米
     * 	    modelId:设备类型Id
     * 		mfgrCode：摄像机厂家编码
     * 		deviceModelCode：摄像机型号编码
     * 		deviceType：摄像机类型 0-NVR,1-网络摄像机,2-AR摄像机,3-热成像摄像机,4、人脸卡口摄像机，5-车辆卡口摄像机；7-无人机
     * 		picSensorSize：图像传感器尺寸，英寸
     * 		picSensorLength：图像传感器宽度，毫米
     * 		picSensorWidth：图像传感器高度，毫米
     * 		minFocus：最小焦距
     * 		focusRange：焦距范围
     * 		horizViewRange：水平视场角
     * 		vertiViewRange：垂直视场角
     * 		horizRange：水平角范围
     * 		vertiRange：俯仰角范围
     * 		vertiDownNeg：水平向下为负  0否1是.
     * 		visualRange：可视距离，单位米
     *      pan：水平参数p值
     *      tilt：垂直参数t值
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1650702778972,
     *     "data":[
     *         {
     *             "deviceCode":"11010800831327000008",
     *             "height":"10",
     *             "longitude":"116.28413364028117",
     *             "latitude":"39.95356071120486",
     *             "modelId":9,
     *             "mfgrCode":"dahua",
     *             "deviceModelCode":"DH-SD-8A1440XA-HNR",
     *             "deviceType":"1",
     *             "picSensorSize":"1/1.8",
     *             "picSensorLength":"7.18",
     *             "picSensorWidth":"5.32",
     *             "minFocus":"5.5",
     *             "focusRange":"5.5~220",
     *             "horizViewRange":"17.06",
     *             "vertiViewRange":"12.56",
     *             "horizRange":"86.00",
     *             "vertiRange":"0.90",
     *             "vertiDownNeg":"1",
     *             "remark":"",
     *             "visualRange":"3000",
     *             "pan":1,
     *             "tilt":0
     *         }
     *     ]
     * }
     */
    @PostMapping("/industry/device/getDeviceVisibleBySite")
    public Result<List<DeviceModelDTO>> getDeviceVisibleBySite(@RequestBody DeviceIndReqDTO deviceInfoReq){
        return industryDeviceServiceBak.getDeviceVisibleBySite(deviceInfoReq);
    }


    /**
     * 查询摄像机标定信息
     * @apiNote 3.72 查询摄像机标定信息
     * @apiVersion 3.72
     * @tag 设备能力
     * @requestDemo
     * {"deviceCode":"32120400831327000001"}
     * @responseDemo
     * deviceCode：设备编码
     * height：设备挂高，单位：米
     * longitude：经度
     * latitude：纬度
     * pan：水平参数p值
     * tilt：垂直参数t值
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1650702778972,
     *     "data":{
     *         "deviceCode":"11010800831327000008",
     *         "height":"10",
     *         "longitude":"116.28413364028117",
     *         "latitude":"39.95356071120486",
     *         "pan":1,
     *         "tilt":0
     *     }
     * }
     */
    @ApiOperation(value = "查询摄像机标定信息", notes = "查询摄像机标定信息")
    @PostMapping("/industry/device/getDeviceCalibration")
    public Result<DeviceModelDTO> getDeviceCalibration(@RequestBody DeviceIndReqDTO deviceInfoReq){
        return industryDeviceServiceBak.getDeviceCalibration(deviceInfoReq);
    }

    @ApiOperation(value = "查询传感器设备信息", notes = "查询传感器设备信息")
    @PostMapping("/industry/device/pageSensorDevice")
    public TableDataInfo pageSensorDevice(@RequestBody PageDeviceVO deviceInfoReq){
        return industryDeviceServiceBak.pageSensorDevice(deviceInfoReq);
    }

    /**
      * 查询传物联设备信息
      * @apiNote 查询传物联设备信息
      * @apiVersion 3.62
      * @tag 设备能力
      * @requestDemo
      * {
      *     "pageNum": 1,
      *     "pageSize": 10,
      *     "deviceCodeList":["123"]
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *     tenantId: 租户id
      *     iotDeviceId: 设备id
      *     deviceCode: 设备编码
      *     deviceName: 设备名称
      *     deviceStatus: 设备状态:0在线，1离线，2从未连接
      *     provinceId: 省份编码
      *     provinceName: 省份名称
      *     cityId: 地市编码
      *     cityName: 地市名称
      *     countyId: 区县编码
      *     countyName: 区县名称
      *     categoryCode: 设备类型 1-雷达 2-物联设备
      *     deviceType: 设备类型
      *     modelCode: 设备厂家code
      *     location: 设备地址
      *     longitude: 经度
      *     latitude: 纬度
      *
      * {
      *     "total": 3,
      *     "rows": [
      *         {
      *             "tenantId": "122035808",
      *             "iotDeviceId": "1522755695900418199",
      *             "deviceCode": "10010112120029",
      *             "deviceName": "空气微站029",
      *             "deviceStatus": 0,
      *             "provinceId": "210000",
      *             "provinceName": "辽宁省",
      *             "cityId": "210600",
      *             "cityName": "丹东市",
      *             "countyId": "210602",
      *             "countyName": "元宝区",
      *             "categoryCode": 2,
      *             "deviceType": "0002",
      *             "modelCode": "hikvision",
      *             "location": "辽宁省丹东市元宝区七道街道新华街",
      *             "longitude": "124.373769000",
      *             "latitude": "40.147291000"
      *         }
      *     ],
      *     "code": 200
      * }
      */
    @ApiOperation(value = "查询传物联设备信息", notes = "查询传物联设备信息")
    @PostMapping("/industry/device/pageSensorDeviceInfo")
    public TableDataInfo pageSensorDeviceInfo(@RequestBody PageSensorDeviceVO deviceInfoReq){
        return industryDeviceServiceBak.pageSensorDeviceInfo(deviceInfoReq);
    }

    /**
      * 根据设备类型查询物联设备
      * @apiNote    根据设备类型查询物联设备
      * @apiVersion 3.43
      * @tag 设备能力
      * @requestDemo
      * {
      *     "industryCode":"000000",
      *     "tenantId":"120046990",
      *     "userId":"9163651856694185b178d08d762aaf1d",
      *     "deviceTypeList":["0001","0002"],
      *     "pageNum":"1",
      *     "pageSize":"10",
      *     "enablePage": true
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *      deviceCode: 设备编码
      * 	 deviceName: 设备名称
      * 	 longitude: 经度
      * 	 latitude: 纬度
      * 	 deviceStatus: 设备状态:0在线，1离线，2从未连接
      *
      * {
      * 	"total": 5,
      * 	"rows": [
      *                {
      * 			"deviceCode": "10010112120029",
      * 			"deviceName": "空气微站029",
      * 			"longitude": "124.373769000",
      * 			"latitude": "40.147291000",
      * 			"deviceStatus": "0"
      *        },
      *        {
      * 			"deviceCode": "10210010010000",
      * 			"deviceName": "龙威气象1",
      * 			"longitude": "116.295211000",
      * 			"latitude": "39.959135000",
      * 			"deviceStatus": "0"
      *        },
      *        {
      * 			"deviceCode": "rain-frgd-9596-01",
      * 			"deviceName": "城三5G海淀永定路70号院515号楼1ZNR19031652",
      * 			"longitude": "116.26655518400278",
      * 			"latitude": "39.90790496548198",
      * 			"deviceStatus": "0"
      *        },
      *        {
      * 			"deviceCode": "rain-kvmk-9271-02",
      * 			"deviceName": "城三5G海淀百乐宾馆1ZNR19031526",
      * 			"longitude": "116.2666226913809",
      * 			"latitude": "39.927950836673475",
      * 			"deviceStatus": "0"
      *        },
      *        {
      * 			"deviceCode": "air-eghd-3392-02",
      * 			"deviceName": "朝阳北苑住宅小区",
      * 			"longitude": "116.4273030000",
      * 			"latitude": "40.0137610000",
      * 			"deviceStatus": "0"
      *        }
      * 	],
      * 	"code": 200
      * }
      */
    @ApiOperation(value = "根据设备类型查询物联设备", notes = "根据设备类型查询物联设备")
    @PostMapping("/industry/device/pageIotDeviceByDeviceType")
    public TableDataInfo pageIotDeviceByDeviceType(@RequestBody PageIotDeviceDTO iotDeviceDTO){
        return industryDeviceServiceBak.pageIotDeviceByDeviceType(iotDeviceDTO);
    }

    /**
      * 根据设备类型查询物联设备所对应的告警类型编码
      * @apiNote 根据设备类型查询物联设备所对应的告警类型编码
      * @apiVersion 3.44
      * @tag 设备能力
      * @requestDemo
      * {
      *     "industryCode":"000000",
      *     "tenantId":"120046990",
      *     "userId":"9163651856694185b178d08d762aaf1d",
      *     "deviceTypeList":["0001","0002"],
      *     "pageNum":"1",
      *     "pageSize":"10",
      *     "enablePage": true
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *       deviceType: 设备类型
      *       alarmTypeCode: 告警类型编码
      * 	  alarmTypeName: 告警类型名称
      * 	  deviceType: 设备类型
      *
      * {
      * 	"total": 11,
      * 	"rows": [
      *                {
      * 			"alarmTypeCode": null,
      * 			"alarmTypeName": null,
      * 			"deviceType": "0001",
      * 			"list": [
      *                {
      * 					"alarmTypeCode": "WLGJ-JRXCXGJ",
      * 					"alarmTypeName": "浸润线超限告警",
      * 					"deviceType": null,
      * 					"list": null
      *                },
      *                {
      * 					"alarmTypeCode": "WLGJ-SLGJ",
      * 					"alarmTypeName": "渗流告警",
      * 					"deviceType": null,
      * 					"list": null
      *                },
      *                {
      * 					"alarmTypeCode": "WLGJ-YLGJ",
      * 					"alarmTypeName": "雨量告警",
      * 					"deviceType": null,
      * 					"list": null
      *                },
      *                {
      * 					"alarmTypeCode": "WLGJ-SYGJ",
      * 					"alarmTypeName": "渗压告警",
      * 					"deviceType": null,
      * 					"list": null
      *                }
      * 			]
      *        },
      *        {
      * 			"alarmTypeCode": null,
      * 			"alarmTypeName": null,
      * 			"deviceType": "0002",
      * 			"list": [
      *                {
      * 					"alarmTypeCode": "WLGJ-JRXCXGJ",
      * 					"alarmTypeName": "浸润线超限告警",
      * 					"deviceType": null,
      * 					"list": null
      *                },
      *                {
      * 					"alarmTypeCode": "3010100",
      * 					"alarmTypeName": "短时浓度报警",
      * 					"deviceType": null,
      * 					"list": null
      *                },
      *                {
      * 					"alarmTypeCode": "3010101",
      * 					"alarmTypeName": "浓度超标报警",
      * 					"deviceType": null,
      * 					"list": null
      *                },
      *                {
      * 					"alarmTypeCode": "1010106",
      * 					"alarmTypeName": "工业烟尘监控告警",
      * 					"deviceType": null,
      * 					"list": null
      *                }
      * 			]
      *        }
      * 	],
      * 	"code": 200
      * }
      */
    @ApiOperation(value = "根据设备类型查询物联设备所对应的告警类型编码", notes = "查询物联设备所对应的告警类型编码")
    @PostMapping("/industry/device/pageAlarmTypeCodeByDeviceType")
    public TableDataInfo pageAlarmTypeCodeByDeviceType(@RequestBody PageIotDeviceDTO iotDeviceDTO){
        return industryDeviceServiceBak.pageAlarmTypeCodeByDeviceType(iotDeviceDTO);
    }

    /**
      * 查询雷达设备信息
      * @apiNote    查询雷达设备信息
      * @apiVersion 3.45
      * @tag 设备能力
      * @requestDemo
      * {
      *     "tenantId": "122035797",
      *     "pageNo":"1",
      *     "pageSize":"10"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *     tenantId: 租户id,
      * 	iotDeviceId: 设备id
      * 	deviceCode: 设备编码
      * 	deviceName: 设备名称
      * 	deviceStatus: 设备状态:0在线，1离线，2从未连接
      * 	provinceId: 省份编码
      * 	provinceName: 省份名称
      * 	cityId: 地市编码
      * 	cityName: 地市名称
      * 	countyId: 区县编码
      * 	countyName: 区县名称
      * 	categoryCode: 设备类型 1-雷达 2-物联设备
      * 	location: 设备地址
      * 	longitude: 经度
      * 	latitude: 纬度
      * 	wZone: 雷达覆盖范围 单位：米
      *     devicePeriod: 雷达天线扫描周期 单位：秒
      *     parAngle: 相控角度 单位:度
      *
      * {
      * 	"total": 5,
      * 	"rows": [
      *                {
      * 			"tenantId": "122035797",
      * 			"iotDeviceId": "1522755695900418058",
      * 			"deviceCode": "10113020300107",
      * 			"deviceName": "GNSS监测站711",
      * 			"deviceStatus": 0,
      * 			"provinceId": "120000",
      * 			"provinceName": "天津市",
      * 			"cityId": "120100",
      * 			"cityName": "天津市",
      * 			"countyId": "120112",
      * 			"countyName": "津南区",
      * 			"categoryCode": 1,
      * 			"location": "天津市津南区北闸口镇G25长深高速",
      * 			"longitude": "117.399255000",
      * 			"latitude": "38.934781000",
      * 			"wZone": "10.00",
      *             "devicePeriod": "10.00",
      *             "parAngle": "10.00"
      *        }
      * 	],
      * 	"code": 200
      * }
      */
    @ApiOperation(value = "查询雷达设备信息", notes = "查询雷达设备信息")
    @PostMapping("/industry/device/pageRadarDevice")
    public TableDataInfo pageRadarDevice(@RequestBody PageRadarDeviceVO pageRadarDeviceReq){
        return industryDeviceServiceBak.pageRadarDevice(pageRadarDeviceReq);
    }

    /**
     *
      * 查询物联设备树
      * @apiNote   查询物联设备树
      * @apiVersion 3.46
      * @tag 设备能力
      * @requestDemo
      * {
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *      code: 节点编码
      *      name: 节点名称
      *      level: 节点层级
      *      isMonitor：是否收藏 0未收藏 1已收藏
      *      deviceCode: 设备编码
      *      deviceName: 设备名称
      *      status: 设备状态:0在线，1离线，2从未连接
      *      longitude: 经度
      *      latitude: 纬度
      *      modelCode: 设备厂家code
      *      location: 设备地址
      *      isMonitor：是否收藏 0未收藏 1已收藏
      *      wZone: 雷达覆盖范围 单位：米
      *      devicePeriod: 雷达天线扫描周期 单位：秒
      *      parAngle: 相控角度 单位:度
      *
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1686301302577,
      *     "data": [
      *         {
      *             "code": "100000",
      *             "name": "中国",
      *             "list": [
      *                 {
      *                     "code": "110000",
      *                     "name": "北京市",
      *                     "level": "1",
      *                     "list": [
      *                         {
      *                             "code": "110100",
      *                             "name": "北京市",
      *                             "level": "2",
      *                             "list": [
      *                                 {
      *                                     "code": "110108",
      *                                     "name": "海淀区",
      *                                     "level": "3",
      *                                     "list": [
      *                                         {
      *                                             "code": "110108103000",
      *                                             "name": "西北旺镇",
      *                                             "level": "4",
      *                                             "list": [
      *                                                 {
      *                                                     "deviceCode": "10010145160076",
      *                                                     "deviceName": "测试冗余站址字段",
      *                                                     "status": "0",
      *                                                     "level": "5",
      *                                                     "longitude": "116.227",
      *                                                     "latitude": "40.0256",
      *                                                     "modelCode": "dhua",
      *                                                     "location": "北京市海淀区西北旺镇北京龙泉摩托车考试场"
      *                                                 }
      *                                             ]
      *                                         }
      *                                     ]
      *                                 }
      *                             ]
      *                         }
      *                     ]
      *                 }
      *             ]
      *         }
      *     ]
      * }
      */
    @ApiOperation(value = "查询物联设备树", notes = "查询物联设备树")
    @PostMapping("/industry/device/getIotDeviceTree")
    public Result<List<DeviceTreeVO>> getIotDeviceTree(@RequestBody IotDeviceVO deviceInfoVO){
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        BeanUtils.copyProperties(deviceInfoVO,deviceReqDTO);
        deviceReqDTO.setQueryType(4);
        deviceReqDTO.setNeedDevice(true);
        deviceReqDTO.setOrderStatus("1");
        return iDeviceCommonService.getDeviceAreaTree(deviceReqDTO);
    }

    /**
     *
      * 查询雷达设备树
      * @apiNote   查询雷达设备树
      * @apiVersion 3.47
      * @tag 设备能力
      * @requestDemo
      * {
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *      code: 节点编码
      *      name: 节点名称
      *      level: 节点层级
      *      isMonitor：是否收藏 0未收藏 1已收藏
      *      deviceCode: 设备编码
      *      deviceName: 设备名称
      *      status: 设备状态:0在线，1离线，2从未连接
      *      longitude: 经度
      *      latitude: 纬度
      *      modelCode: 设备厂家code
      *      location: 设备地址
      *      isMonitor：是否收藏 0未收藏 1已收藏
      *      wZone: 雷达覆盖范围 单位：米
      *      devicePeriod: 雷达天线扫描周期 单位：秒
      *      parAngle: 相控角度 单位:度
      *
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1686301302577,
      *     "data": [
      *         {
      *             "code": "100000",
      *             "name": "中国",
      *             "list": [
      *                 {
      *                     "code": "110000",
      *                     "name": "北京市",
      *                     "level": "1",
      *                     "list": [
      *                         {
      *                             "code": "110100",
      *                             "name": "北京市",
      *                             "level": "2",
      *                             "list": [
      *                                 {
      *                                     "code": "110108",
      *                                     "name": "海淀区",
      *                                     "level": "3",
      *                                     "list": [
      *                                         {
      *                                             "code": "110108103000",
      *                                             "name": "西北旺镇",
      *                                             "level": "4",
      *                                             "list": [
      *                                                 {
      *                                                     "deviceCode": "10010145160076",
      *                                                     "deviceName": "测试冗余站址字段",
      *                                                     "status": "0",
      *                                                     "level": "5",
      *                                                     "longitude": "116.227",
      *                                                     "latitude": "40.0256",
      *                                                     "modelCode": "dhua",
      *                                                     "location": "北京市海淀区西北旺镇北京龙泉摩托车考试场"
      *                                                      "isMonitor": "0"
      *                                                 }
      *                                             ]
      *                                         }
      *                                     ]
      *                                 }
      *                             ]
      *                         }
      *                     ]
      *                 }
      *             ]
      *         }
      *     ]
      * }
      */
    @ApiOperation(value = "查询雷达设备树", notes = "查询雷达设备树")
    @PostMapping("/industry/device/getRadarDeviceTree")
    public Result<List<DeviceTreeVO>> getRadarDeviceTree(@RequestBody IotDeviceVO deviceInfoVO){
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        BeanUtils.copyProperties(deviceInfoVO,deviceReqDTO);
        deviceReqDTO.setQueryType(3);
        deviceReqDTO.setNeedDevice(true);
        deviceReqDTO.setOrderStatus("1");
        return iDeviceCommonService.getDeviceAreaTree(deviceReqDTO);
    }

    /**
      * 查询行业传感器设备信息
      * @apiNote    查询行业传感器设备信息
      * @apiVersion 3.48
      * @tag 设备能力
      * @requestDemo
      * {
      *     "appVerCode": "1.0",
      *     "pageSize": 5,
      *     "pageNum": 1
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *       iotDeviceId: 设备id
      *       deviceCode: 设备编码
      *       deviceName: 设备名称
      *       deviceStatus: 设备状态:0在线，1离线
      *       categoryCode: 设备类型 1-雷达 2-物联设备
      *       siteCode: 站址编码
      *       siteName: 站址名称
      *       height: 设备挂高，单位：米
      *       location: 设备地址
      *       longitude: 经度
      *       latitude: 纬度
      *       admCode: 行政区域
      *       needDevice: 是否展示摄像机数据
      *       isMonitor: 是否收藏 0未收藏 1已收藏
      *
      * {
      *     "total": 175,
      *     "rows": [
      *         {
      *             "iotDeviceId": "1522755695900418587",
      *             "deviceCode": "fgraph-purq-3919-01",
      *             "deviceName": "fgraph-pur",
      *             "deviceStatus": 0,
      *             "categoryCode": 2,
      *             "siteCode": "110802500010001301",
      *             "siteName": "城三5G海淀永定路70号院515号楼1ZNR19031652",
      *             "height": "10.0",
      *             "location": "北京市海淀区永定路70号院515号楼",
      *             "longitude": "116.26655518400278",
      *             "latitude": "39.90790496548198",
      *             "admCode": "110108",
      *             "needDevice": false,
      *             "isMonitor": "0"
      *         },
      *         {
      *             "iotDeviceId": "1522755695900418588",
      *             "deviceCode": "flow-bong-1382-01",
      *             "deviceName": "flow-bong",
      *             "deviceStatus": 0,
      *             "categoryCode": 2,
      *             "siteCode": "110802500010001301",
      *             "siteName": "城三5G海淀永定路70号院515号楼1ZNR19031652",
      *             "height": "10.0",
      *             "location": "北京市海淀区永定路70号院515号楼",
      *             "longitude": "116.26655518400278",
      *             "latitude": "39.90790496548198",
      *             "admCode": "110108",
      *             "needDevice": false,
      *             "isMonitor": "0"
      *         },
      *         {
      *             "iotDeviceId": "1522755695900418582",
      *             "deviceCode": "gnss-hipw-4643-01",
      *             "deviceName": "琼海牛路岭",
      *             "deviceStatus": 0,
      *             "categoryCode": 2,
      *             "siteCode": "469201000040001581",
      *             "siteName": "琼海牛路岭",
      *             "height": "10.0",
      *             "location": "海南省琼海琼海牛路岭水电站",
      *             "longitude": "110.20379084658899",
      *             "latitude": "19.005471935563268",
      *             "admCode": "469002",
      *             "needDevice": false,
      *             "isMonitor": "0"
      *         },
      *         {
      *             "iotDeviceId": "1522755695900418586",
      *             "deviceCode": "osmometer-dbul-3566-01",
      *             "deviceName": "osmometer-dbul",
      *             "deviceStatus": 0,
      *             "categoryCode": 2,
      *             "siteCode": "110802500010001301",
      *             "siteName": "城三5G海淀永定路70号院515号楼1ZNR19031652",
      *             "height": "10.0",
      *             "location": "北京市海淀区永定路70号院515号楼",
      *             "longitude": "116.26655518400278",
      *             "latitude": "39.90790496548198",
      *             "admCode": "110108",
      *             "needDevice": false,
      *             "isMonitor": "0"
      *         },
      *         {
      *             "iotDeviceId": "1522755695900418517",
      *             "deviceCode": "rain-frgd-9596-01",
      *             "deviceName": "城三5G海淀永定路70号院515号楼1ZNR19031652",
      *             "deviceStatus": 0,
      *             "categoryCode": 2,
      *             "siteCode": "110802500010001301",
      *             "siteName": "城三5G海淀永定路70号院515号楼1ZNR19031652",
      *             "height": "10.0",
      *             "location": "北京市海淀区永定路70号院515号楼",
      *             "longitude": "116.26655518400278",
      *             "latitude": "39.90790496548198",
      *             "admCode": "110108",
      *             "needDevice": false,
      *             "isMonitor": "0"
      *         }
      *     ],
      *     "code": 200
      * }
      */
    @ApiOperation(value = "查询行业传感器设备信息", notes = "查询行业传感器设备信息")
    @PostMapping("/industry/device/pageIndSensorDevice")
    public TableDataInfo pageIndSensorDevice(@RequestBody PageDeviceVO deviceInfoReq){
        return industryDeviceServiceBak.pageIndSensorDevice(deviceInfoReq);
    }

    /**
      * 雷达平台查询应用平台获取雷达对应的摄像机列表
      * @apiNote   雷达平台查询应用平台获取雷达对应的摄像机列表
      * @apiVersion 3.49
      * @tag 设备能力
      * @requestDemo
      * {
      *     "radarIds": ["341"]
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *      radarId: 雷达设备编码
      *      devices: 关联摄像机列表
      *           -deviceCode: 关联摄像机编码
      *           -deviceName: 关联摄像机名称
      *           -longitude: 经度
      *           -latitude: 纬度
      *           -height: 挂高
      *           -channels: 摄像机通道
      *                -channelCode: 通道编码
      *                -channelName: 通道名称
      *                -visualRange: 可视范围
      *                -horizRange: 水平辐射角度
      *                -vertiRange: 俯仰辐射角度
      *
      *
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1692345173591,
      *     "data": [
      *         {
      *             "radarId": "341",
      *             "devices": [
      *                 {
      *                     "deviceCode": "43062300831327000007",
      *                     "deviceName": "华容县江洲无线机房-渔政5km云台",
      *                     "longitude": "112.857788706",
      *                     "latitude": "29.771174915",
      *                     "height": "50",
      *                     "channels": [
      *                         {
      *                             "channelCode": "43062300001130000001",
      *                             "channelName": "华容县江洲无线机房-渔政5km云台_0",
      *                             "visualRange": "501",
      *                             "horizRange": "123",
      *                             "vertiRange": "67"
      *                         },
      *                         {
      *                             "channelCode": "43062300001130000002",
      *                             "channelName": "华容县江洲无线机房-渔政5km云台_1",
      *                             "visualRange": "501",
      *                             "horizRange": "123",
      *                             "vertiRange": "67"
      *                         }
      *                     ]
      *                 },
      *                 {
      *                     "deviceCode": "42282500831327000027",
      *                     "deviceName": "大华3公里",
      *                     "longitude": "109.501748494",
      *                     "latitude": "29.988925319",
      *                     "height": "10",
      *                     "channels": [
      *                         {
      *                             "channelCode": "42282500001130000005",
      *                             "channelName": "大华3公里_0",
      *                             "visualRange": "206",
      *                             "horizRange": "293",
      *                             "vertiRange": "155"
      *                         }
      *                     ]
      *                 }
      *             ]
      *         }
      *     ]
      * }
      */
    @ApiOperation(value = "雷达平台查询应用平台获取雷达对应的摄像机列表", notes = "雷达平台查询应用平台获取雷达对应的摄像机列表")
    @PostMapping("/industry/device/getRadarDeviceConfigInfo")
    public Result<List<RadarDeviceVO>> getRadarDeviceConfigInfo(@RequestBody RadarDeviceReqDTO radarDeviceReqDTO){
        return industryDeviceServiceBak.getRadarDeviceConfigInfo(radarDeviceReqDTO);
    }

    /**
     * 根据组织机构id查询物联设备
     * @apiNote    根据组织机构id查询物联设备
     * @apiVersion 3.50
     * @tag 设备能力
     * @requestDemo
     * {
     *     "deptId": "1616-1161",
     *     "deviceCode": "16419413129414451",
     *     "deviceName": "测试",
     *     "deviceStatus": "0",
     *     "deviceType": "11"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *      deviceCode: 设备编码
     *      deviceType: 设备类型
     *      deviceName: 设备名称
     *      deviceStatus: 设备状态:0在线，1离线，2从未连接
     *      modelCode: 设备厂家code
     *      height: 设备挂高，单位：米
     *      location: 设备地址
     *      longitude: 经度
     *      latitude: 纬度
     *      deviceModelCode: 物联设备型号编码
     *
     * {
     *     code: 状态码
     *     msg: 请求结果描述
     *     time: 时间戳
     *     data: [
     *         {
     *             deviceCode: "10110101000101",
     *             "deviceType": "",
     *             "deviceName": "百望山站点1",
     *             "deviceStatus": 0,
     *             "modelCode": "hikvision",
     *             "height": "10.00",
     *             "location": "北京市东城区东华门街道天安门广场",
     *             "longitude": "116.396999000",
     *             "latitude": "39.904108000",
     *             "deviceModelCode": "123"
     *         }
     *     ]
     * }
     */
    @ApiOperation(value = "根据组织机构id查询物联设备", notes = "根据组织机构id查询物联设备")
    @PostMapping("/industry/device/getIotDeviceByDept")
    public Result<List<IotDeviceEntity>> getIotDeviceByDept(@RequestBody IotDeviceEntity iotDeviceEntity){
        return industryDeviceServiceBak.getIotDeviceByDept(iotDeviceEntity);
    }

    /**
      * 根据地域信息按设备类型查询物联设备数据
      * @apiNote   根据地域信息按设备类型查询物联设备数据
      * @apiVersion 3.51
      * @tag 设备能力
      * @requestDemo
      *{
      *     "provinceId": "100000",
      *     "cityId": "110000",
      *     "countyId": "110100",
      *     "townCode": "110101",
      *     "deviceType": "11"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *     deviceType: 设备类型
      *     iotDevices: 物联设备列表
      *           -deviceCode: 设备编码
      *           -deviceName: 设备类型
      *           -deviceStatus: 设备状态:0在线，1离线，2从未连接
      *           -height: 挂高
      *           -location: 设备地址
      *           -longitude: 经度
      *           -latitude: 纬度
      *           -provinceId: 省份编码
      *           -cityId: 地市编码
      *           -countyId: 区县编码
      *           -townCode: 乡镇编码
      *           -devices:  关联摄像机列表
      *                 -cameraCode: 关联摄像机编码
      *                 -cameraName: 关联摄像机名称
      *                 -cameraStatus: 设备状态:0在线，1离线，2从未连接
      *                 -cameraLongitude: 关联摄像机经度
      *                 -cameraLatitude: 关联摄像机纬度
      *                 -cameraLocation: 关联摄像机设备地址
      *                 -channelCode: 通道编码
      *                 -channelName: 通道名称
      *
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1667994923762,
      *     "data": [
      *         {
      *             "deviceType": "",
      *             "iotDevices": [
      *                 {
      *                     "deviceCode": "10110101000101",
      *                     "deviceName": "百望山站点1",
      *                     "deviceStatus": 0,
      *                     "height": "10.00",
      *                     "location": "北京市东城区东华门街道天安门广场",
      *                     "longitude": "116.396999000",
      *                     "latitude": "39.904108000",
      *                     "provinceId": "110000",
      *                     "cityId": "110100",
      *                     "countyId": "110101",
      *                     "townCode": "110101001000",
      *                     "devices": [
      *                         {
      *                             "cameraCode": "11010800831327000003",
      *                             "cameraName": "铁塔产业园-12号楼角落-大华245",
      *                             "cameraStatus": "0",
      *                             "cameraLongitude": "116.400027000",
      *                             "cameraLatitude": "39.916876000",
      *                             "cameraLocation": "北京市海淀区世纪城远大园六区3号楼",
      *                             "channelCode": "11010800001310000051",
      *                             "channelName": "铁塔产业园-12号楼角落-大华245_0"
      *                         }
      *                     ]
      *                 }
      *             ]
      *         }
      *     ]
      * }
      */
    @ApiOperation(value = "根据地域信息按设备类型查询物联设备数据", notes = "根据地域信息按设备类型查询物联设备数据")
    @PostMapping("/industry/device/getIotGroupByDeviceType")
    public Result<List<IotDeviceTypeData>> getIotGroupByDeviceType(@RequestBody IotDeviceAreaEntity iotDeviceAreaEntity){
        return industryDeviceServiceBak.getIotGroupByDeviceType(iotDeviceAreaEntity);
    }

    /**
      * 根据客户信息获取物联设备数量
      * @apiNote   根据客户信息获取物联设备数量
      * @apiVersion 3.52
      * @tag 设备能力
      * @requestDemo
      * {
      *     "categoryCode": "2"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *     totalCount: 订购的雷达/物联 设备总数
      *     onlineCount: 雷达/物联 在线数量
      *     offlineCount: 雷达/物联 离线数量
      *
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1692344284109,
      *     "data": {
      *         "totalCount": 175,
      *         "onlineCount": 175,
      *         "offlineCount": 0
      *     }
      * }
      */
    @ApiOperation(value = "根据客户信息获取物联设备数量", notes = "根据客户信息获取物联设备数量")
    @PostMapping("/industry/device/getSensorDeviceCount")
    public Result<SensorDeviceCountVO> getSensorDeviceCount(@RequestBody SensorDeviceCountReqDTO request){
        return industryDeviceServiceBak.getSensorDeviceCount(request);
    }

    /**
      *
      * @apiNote   查询物联因子相关信息
      * @apiVersion 3.61
      * @tag 设备能力
      * @requestDemo
      * {
      *     "monitorCode": "pm25",
      *     "alarmType": "3010100",
      *     "deviceCodeList": ["010000A8900016F00016"],
      *     "pageNum": "1",
      *     "pageSize": "10"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *      deviceCode: 设备编码
      *      monitorInfoList: 物联因子信息
      *           -monitorCode: 检测指标编号
      *           -monitorName: 检测指标名称
      *           -monitorUnits: 检测指标单位
      *           -alarmType: 告警类型编码
      *           -alarmTypeName: 告警类型名称
      *
      * {
      *     "code": 200,
      *     "msg": "查询成功",
      *     "time": 1692343885180,
      *     "data": {
      *         "list": [
      *             {
      *                 "deviceCode": "010000A8900016F00016",
      *                 "monitorInfoList": [
      *                     {
      *                         "monitorCode": "pm10",
      *                         "monitorName": "PM₁₀",
      *                         "monitorUnits": "μg/m3",
      *                         "alarmType": "3010101",
      *                         "alarmTypeName": "浓度超标报警"
      *                     },
      *                     {
      *                         "monitorCode": "humidity",
      *                         "monitorName": "湿度",
      *                         "monitorUnits": "%",
      *                         "alarmType": "WLGJ-JRXCXGJ",
      *                         "alarmTypeName": "浸润线超限告警"
      *                     },
      *                     {
      *                         "monitorCode": "pm25",
      *                         "monitorName": "PM₂.₅",
      *                         "monitorUnits": "μg/m3",
      *                         "alarmType": "3010100",
      *                         "alarmTypeName": "短时浓度报警"
      *                     },
      *                     {
      *                         "monitorCode": "pressure",
      *                         "monitorName": "气压",
      *                         "monitorUnits": "千帕",
      *                         "alarmType": "WLGJ-WLLX001",
      *                         "alarmTypeName": null
      *                     },
      *                     {
      *                         "monitorCode": "turbidity",
      *                         "monitorName": "浊度",
      *                         "monitorUnits": "NTU",
      *                         "alarmType": "3010101",
      *                         "alarmTypeName": "浓度超标报警"
      *                     }
      *                 ]
      *             }
      *         ],
      *         "pageNum": 1,
      *         "pageSize": 1,
      *         "size": 1,
      *         "startRow": 0,
      *         "endRow": 0,
      *         "pages": 1,
      *         "prePage": 0,
      *         "nextPage": 0,
      *         "isFirstPage": true,
      *         "isLastPage": true,
      *         "hasPreviousPage": false,
      *         "hasNextPage": false,
      *         "navigatePages": 8,
      *         "navigatepageNums": [
      *             1
      *         ],
      *         "navigateFirstPage": 1,
      *         "navigateLastPage": 1
      *     }
      * }
      */
    @ApiOperation(value = "查询物联因子相关信息", notes = "查询物联因子相关信息")
    @PostMapping("/industry/device/pageIotMonitorInfo")
    public Result<PageInfo<PageIotMonitorVO>> pageIotMonitorInfo(@RequestBody PageIotMonitorDTO param){
        return industryDeviceServiceBak.pageIotMonitorInfo(param);
    }

    /**
     * 查询指定租户，指定行业，指定经纬度3公里范围全部设备，按照距离从近到远排序
     * @apiNote 3.73 查询指定租户，指定行业，指定经纬度3公里范围全部设备，按照距离从近到远排序
     * @apiVersion 3.73
     * @tag 设备能力
     * @requestDemo
     * {
     *     "latitude":"122.111",
     *     "longitude":"37.11",
     *     "tenantId":"TEN100001",
     *     "industryCode":"000000"
     * }
     * @responseDemo
     * longitude：经度
     * latitude：纬度
     * deviceCode：摄像机编码
     * deviceName：摄像机名称
     * distance：距离
     * list 通道列表
     *  channelCode：通道编码
     *  channelName：通道名称
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1648107167647,
     *     "data":[
     *         {
     *             "longitude":"122.111",
     *             "latitude":"37.11",
     *             "deviceCode":"100000000022",
     *             "deviceName":"22",
     *             "distance":"10.007935794096117",
     *             "list":[
     *                 {
     *                     "channelCode":"36040200001130000003",
     *                     "channelName":"36040200001130000003"
     *                 }
     *             ]
     *         }
     *     ]
     * }
     * @param deviceLayerReq
     * @return
     */
    @ApiOperation(value = "查询指定租户，指定行业，指定经纬度3公里范围全部设备，按照距离从近到远排序")
    @PostMapping("/industry/device/queryDevice3km")
    public Result<List<IndustryDeviceTree>> queryDevice3km(@RequestBody DeviceLayerReq deviceLayerReq){
        return industryDeviceServiceBak.queryDevice3km(deviceLayerReq);
    }

    @ApiOperation(value = "根据关键字查询设备的基本信息接口")
    /**
     * 根据关键字查询设备的基本信息接口
     * @apiNote 3.9根据关键字查询设备的基本信息接口
     * @apiVersion 3.9
     * @tag 设备能力
     * @requestDemo
     * {
     *     "deviceCode":"20220006",
     *     "deviceType":"2"
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		time：时间
     * 		data：返回数据
     * 		tenantId：租户编码
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		status：设备状态:0在线，1离线
     * 		provinceCode：省份
     * 		provinceName：省份名称
     * 		cityCode：地市
     * 		cityName：地市名称
     * 		countyCode：区县
     * 		countyName：区县名称
     * 		streetCode：街道/乡镇
     * 		streetName：街道名称
     * 		admCode：行政区域
     * 		categoryCode：设备类型
     * 		location：站址地址
     * 		orderStatus：订购状态
     * 		industryCode：行业编码
     * 		longitude：经度
     * 		latitude：纬度
     * 	    channels：通道列表
     * 	     parentDeviceCode：设备编码
     * 		 channelCode：通道编码
     * 		 channelName：通道名称
     * 		 status：通道状态 0在线 1离线 2未配置设备
     * 		 visualRange：可视范围
     * 		 horizRange：水平辐射角度
     * 		 vertiRange：俯仰辐射
     * 		 tenantId：租户Id
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1679465509488,
     *     "data":{
     *         "tenantId":"122035797",
     *         "industryCode":"000000",
     *         "deviceCode":"11010500831327000149",
     *         "deviceName":"-6-241",
     *         "status":"1",
     *         "location":"",
     *         "orderStatus":"3",
     *         "provinceCode":"430000",
     *         "provinceName":"",
     *         "cityCode":"430500",
     *         "cityName":"",
     *         "countryCode":"430503",
     *         "countryName":"",
     *         "streetCode":"430503010000",
     *         "streetName":"",
     *         "longitude":"111.419616264",
     *         "latitude":"27.140358169",
     *         "channels":[
     *             {
     *                 "parentDeviceCode":"11010500831327000149",
     *                 "channelName":"-6-241_0",
     *                 "channelCode":"11010500831310000054",
     *                 "status":"0",
     *                 "visualRange":"",
     *                 "horizRange":"",
     *                 "vertiRange":"",
     *                 "tenantId":"122035797"
     *             },
     *             {
     *                 "parentDeviceCode":"11010500831327000149",
     *                 "channelName":"-6-241_0",
     *                 "channelCode":"11010500001310000001",
     *                 "status":"1",
     *                 "visualRange":null,
     *                 "horizRange":null,
     *                 "vertiRange":null,
     *                 "tenantId":"122035797"
     *             }
     *         ],
     *         "modelId":"hikvision",
     *         "modelName":"",
     *         "categoryCode":0
     *     }
     * }
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/industry/device/queryDeviceList")
    public TableDataInfo queryDeviceList(@RequestBody DeviceInfoVO deviceInfoVO){
        return industryDeviceServiceBak.queryDeviceList(deviceInfoVO);
    }


    @ApiOperation(value = "根据设备编码和类型，获取设备详细信息")
    /**
     * 根据关键字查询设备的基本信息接口
     * @apiNote 3.8根据关键字查询设备的基本信息接口
     * @apiVersion 3.8
     * @tag 设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010500831327000149",
     *     "pageSize":10,
     *     "pageNum":1
     *     "deviceType"："1"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		deviceType：摄像机类型 1-摄像机  2-物联网
     * 		status：设备状态:0在线，1离线
     * 		longitude：经度
     * 		latitude：纬度
     * 	    channels：通道列表
     * 	     parentDeviceCode：设备编码
     * 		 channelCode：通道编码
     * 		 channelName：通道名称
     * 		 channelStatus：通道状态 0在线1离线
     * 		 visualRange：可视范围
     * 		 horizRange：水平辐射角度
     * 		 vertiRange：俯仰辐射
     * 		 tenantId：租户id
     * {
     *     "total":2,
     *     "rows":[
     *         {
     *             "deviceName":"-6-241",
     *             "deviceCode":"11010500831327000149",
     *             "deviceType":"1",
     *             "status":"1",
     *             "longitude":"111.419616264",
     *             "latitude":"27.140358169",
     *             "channels":[
     *                 {
     *                     "parentDeviceCode":"11010500831327000149",
     *                     "channelName":"-6-241_0",
     *                     "channelCode":"11010500831310000054",
     *                     "status":"0",
     *                     "visualRange":"",
     *                     "horizRange":"",
     *                     "vertiRange":"",
     *                     "tenantId":"122035797"
     *                 },
     *                 {
     *                     "parentDeviceCode":"11010500831327000149",
     *                     "channelName":"-6-241_0",
     *                     "channelCode":"11010500001310000001",
     *                     "status":"1",
     *                     "visualRange":null,
     *                     "horizRange":null,
     *                     "vertiRange":null,
     *                     "tenantId":"122035797"
     *                 }
     *             ]
     *         },
     *         {
     *             "deviceName":"1",
     *             "deviceCode":"14011000831327000001",
     *             "deviceType":"1",
     *             "status":"0",
     *             "longitude":"110.199665910",
     *             "latitude":"19.009824079",
     *             "channels":[
     *                 {
     *                     "parentDeviceCode":"14011000831327000001",
     *                     "channelName":"_0",
     *                     "channelCode":"14011000831310000001",
     *                     "status":"0",
     *                     "visualRange":"",
     *                     "horizRange":"",
     *                     "vertiRange":"",
     *                     "tenantId":"122035797"
     *                 }
     *             ]
     *         }
     *     ],
     *     "code":0
     * }
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/industry/device/queryDeviceDetail")
    public Result queryDeviceDetail(@RequestBody DeviceQueryVO deviceInfoVO){
        return industryDeviceServiceBak.queryDeviceDetail(deviceInfoVO);
    }


    @ApiOperation(value = "根据设备编码，获取附近指定范围的所有设备")
    /**
     * 根据设备编码，获取附近指定范围的所有设备
     * @apiNote 3.10根据设备编码，获取附近指定范围的所有设备
     * @apiVersion 3.10
     * @tag 设备能力
     * @requestDemo
     * {
     *     "deviceCode":"10010123140052",
     *     "deviceType":"2",
     *     "distance":"20000"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		longitude：经度
     * 		latitude：纬度
     * 		distance：距离
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		deviceType：摄像机类型 1-摄像机  2-物联网设备
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1679300063726,
     *     "data":[
     *         {
     *             "deviceCode":"10010123140052",
     *             "deviceName":"11",
     *             "deviceType":"2",
     *             "longitude":"116.454947000",
     *             "latitude":"39.932747000",
     *             "distance":0
     *         },
     *         {
     *             "deviceCode":"142P2250009",
     *             "deviceName":"001",
     *             "deviceType":"2",
     *             "longitude":"116.2780870000",
     *             "latitude":"39.9523280000",
     *             "distance":19786
     *         }
     *     ]
     * }
     * @param deviceQueryVO
     * @return
     */
    @PostMapping("/industry/device/queryDeviceByDistance")
    public Result<List<DeviceByDistanceDTO>> queryDeviceByDistance(@RequestBody DeviceQueryVO deviceQueryVO){
        return industryDeviceServiceBak.queryDeviceByDistance(deviceQueryVO);
    }

    /**
     * 根据设备编码查询设备信息
     * @apiNote 3.74 根据设备编码查询设备信息
     * @apiVersion 3.74
     * @tag 设备能力
     * @requestDemo
     * {"deviceCode":"33060200001327000016"}
     * @responseDemo
     * code枚举
     * 200  操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * 返回实体类（com.chinatower.device.entity.vo.DeviceChannelInfoVO）
     * tenantId租户编码
     * tenantName租户名称
     * industryCode行业编码
     * deviceCode设备编码
     * devName设备名称
     * deviceName设备名称
     * status设备状态
     * deviceStatusName设备状态名称
     * location设备地址
     * orgCode设备所属组织编码
     * orgRelId设备所属组织ID
     * orgName设备所属组织名称
     * orderStatus订购状态
     * orderStatusName订购状态名称
     * provinceCode省份编码
     * provinceName省份名称
     * cityCode地市编码
     * cityName地市名称
     * countryCode区县编码
     * countryName区县名称
     * streetCode乡镇(街道)编码
     * streetName乡镇(街道)名称
     * siteName站址名称
     * longitude经度
     * latitude纬度
     * modelId厂家编码
     * modelName厂家名称
     * categoryCode摄像机类型（0-NVR,1-网络摄像机,2-AR摄像机,3-热成像摄像机,4、人脸卡口摄像机，5-车辆卡口摄像机；7-无人机）
     * isMonitor是否收藏 0否 1是
     * labelNameList标签名称集合
     * channels通道列表
     *    "status":通道状态0 在线1离线
     *    "streamType":码流类型:1-主码流；2-子码流
     *    "maxGuard":最大看守位数:通道允许的最大看守位数量
     *    "maxPreset":最大预置点数:通道允许的最大预置点数量
     *    "maxPatrol":最大巡航路径数量:通道允许的最大巡航路径数量
     *    "channelCode":通道编码
     *    "channelName":通道名称
     *    "deviceCode":设备编码
     *    "facade":外型 1-球机；2-半球；3-枪机
     *    "channelType":通道类型 1-数字信号；2-模拟信号；3-音频信号；4-i/o输入；5-i/o输出；
     *    "visualRange":可视范围
     *    "horizRange":水平辐射角度
     *    "vertiRange":俯仰辐射角度
     *    "channelCodeSet":通道列表l,
     *    "tenantId":租户id,
     *    "isMonitor":是否收藏字段，0未收藏 1已收藏
     *    "longitude":经度
     *    "latitude":纬度
     *    "location":地址信息
     *    "height":挂高
     *    "siteCode":站址code
     *    "modelCode":设备类型code
     *    "modelName":设备类型名称
     *    "rangeMeter":用户离设备距离
     *    "distance":距离
     * siteCode站址编码
     * height挂高
     * deviceModelCode摄像机型号
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1694053193764,
     *     "data":{
     *         "siteCode":"dc28fc97589d4bacb776840e015db290",
     *         "longitude":"116.266750",
     *         "latitude":"39.943157",
     *         "devName":"铁塔产业园-12号楼角落-大华245",
     *         "deviceCode":"11010800831327000003",
     *         "status":"0",
     *         "height":"3",
     *         "location":"四季青镇中国铁塔产业园",
     *         "channels":[
     *             {
     *                 "status":"0",
     *                 "streamType":null,
     *                 "maxGuard":null,
     *                 "maxPreset":null,
     *                 "maxPatrol":null,
     *                 "channelCode":"11010800001310000051",
     *                 "channelName":"铁塔产业园-12号楼角落-大华245_0",
     *                 "deviceCode":"11010800831327000003",
     *                 "facade":null,
     *                 "channelType":null,
     *                 "visualRange":null,
     *                 "horizRange":null,
     *                 "vertiRange":null,
     *                 "channelCodeSet":null,
     *                 "tenantId":null,
     *                 "isMonitor":null,
     *                 "longitude":null,
     *                 "latitude":null,
     *                 "location":null,
     *                 "height":null,
     *                 "siteCode":null,
     *                 "modelCode":null,
     *                 "modelName":null,
     *                 "rangeMeter":0,
     *                 "distance":null
     *             }
     *         ],
     *         "orderCode":"PO12004699020230223768169803776",
     *         "tenantId":"120046990",
     *         "isMonitor":"0",
     *         "modelCode":"dhua",
     *         "modelName":"大华",
     *         "categoryCode":"1",
     *         "deviceModelCode":"DH-SD02-TT",
     *         "labelNameList":[
     *             "1",
     *             "356666556786556887654688764457888643455788",
     *             "123213",
     *             "欠我的期望"
     *         ]
     *     }
     * }
     * @param siteRequestDTO
     * @return
     */
    @ApiOperation("根据设备编码查询设备信息")
    @PostMapping("/video/device/queryDeviceForWE")
    public Result<DeviceChannelInfoVO> queryDevicesForWE(@RequestBody SiteRequestDTO siteRequestDTO){
        return industryDeviceServiceBak.queryDevicesForWE(siteRequestDTO);
    }

    @ApiOperation("根据站址编码查询设备列表")
    /**
     * 根据站址编码查询设备列表
     * @apiNote 3.17根据站址编码查询设备列表
     * @apiVersion 3.17
     * @tag 设备能力
     * @requestDemo
     * {
     *     "townCode":"",
     *     "countyId":"",
     *     "deviceName":"",
     *     "modelCode":"",
     *     "orgId":"",
     *     "orgIdList":[
     *
     *     ],
     *     "modelCodeList":[
     *
     *     ],
     *     "provinceId":"",
     *     "cityId":"",
     *     "status":"",
     *     "latitude":"",
     *     "longitude":"",
     *     "deviceCodeList":[
     *         "21041100011327000001",
     *         "11010800011327000004"
     *     ],
     *     "pageNum":1,
     *     "pageSize":10
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		time：时间
     * 		data：返回数据
     * 		longitude：经度
     * 		latitude：纬度
     * 	    location：地址信息
     * 		deviceName：设备名称
     * 		height：设备挂高，单位：米
     * 		categoryCode：设备类型
     * 		status：设备状态:0在线，1离线
     * 		deviceCode：设备编码
     * 		siteCode：站址编码
     * 		modelName：设备厂家名称
     * 		modelCode：设备厂家编码
     * 	    categoryCode：设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     * {
     *     "total":1,
     *     "rows":[
     *         {
     *             "deviceName":"-6-241",
     *             "deviceCode":"21041100011327000001",
     *             "height":"1",
     *             "status":"1",
     *             "longitude":"111.419616264",
     *             "latitude":"27.140358169",
     *             "location":"11010500831327000149",
     *             "siteCode":"-6-241_0",
     *             "modelCode":"11010500831310000054",
     *             "modelName":"0",
     *             "categoryCode":""
     *         }
     *     ],
     *     "code":200
     * }
     * @param siteRequestDTO
     * @return
     */
    @PostMapping("/video/device/queryDevices")
    public TableDataInfo queryDevices(@RequestBody SiteRequestDTO siteRequestDTO){
        return industryDeviceServiceBak.queryDevices(siteRequestDTO);
    }

    /**
     * 根据设备编码查询设备图片信息
     * @tag 设备能力
     * @requestDemo
     * {"deviceCode":"11010500831327000007"}
     * @responseDemo
     * {
     *  "code": 200,
     *   "msg": "请求成功",
     *   "time": 1718332789549,
     *   "data": [
     *       "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/2.0/bb08f84d9ddf54459ea7b705612bafa1/b07e489c40513087e56e34f48efb229a/65c53b018f18442eb28e4c8f96cdab23.mp4?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1718333389&Signature=kv5AYrLk9NwgoBDNmepxIt0n%2BsQ%3D",
     *       "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/2.0/bb08f84d9ddf54459ea7b705612bafa1/b07e489c40513087e56e34f48efb229a/706bf6f3fc17431fa9dbad6a3f4abe83.mp4?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1718333389&Signature=R/0Fdi0RdTQUxYHy0I/FVy97qLI%3D",
     *       "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/2.0/bb08f84d9ddf54459ea7b705612bafa1/b07e489c40513087e56e34f48efb229a/f4199ef99b5b45afa3e21630d930d95d.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1718333389&Signature=sg4wSUs3dAx9MKtq6bUvAl/AF98%3D"
     *    ]
     * }
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/getDeviceImage")
    public Result<List<String>> getDeviceImage(@RequestBody DeviceInfoVO deviceInfoVO){
        return industryDeviceServiceBak.getDeviceImage(deviceInfoVO);
    }



    /**
     * 根据站址编码查询设备聚合列表
     * @apiNote 3.75 根据站址编码查询设备聚合列表
     * @apiVersion 3.75
     * @tag 设备能力
     * @requestDemo
     * {
     *     "cityId":"110100",
     *     "labelIdList":[
     *         "2222",
     *         "3333"
     *     ]
     * }
     * @responseDemo
     * deviceCode：摄像机编码
     * height：挂高
     * deviceName：摄像机名称
     * longitude：经度
     * latitude：纬度
     * siteCode：站址编码
     * allLine：是否在线 0-在线  1-离线
     * categoryCode:设备类型:1-IPC 0-NVR;2-AR摄像头；3-热成像摄像头
     * {
     *     "list":[
     *         {
     *             "deviceCode":"36040200831327000005",
     *             "height":"39.0",
     *             "deviceName":"36040200831327000005",
     *             "longitude":"116.1855550000",
     *             "latitude":"29.7491660000",
     *             "siteCode":"360402908000000206",
     *             "allLine":"1",
     *             "categoryCode":"1"
     *         }
     *     ]
     * }
     * @param siteRequestDTO
     * @return
     */
    @ApiOperation("根据站址编码查询设备聚合列表")
    @PostMapping("/video/device/querySitesForWE")
    public Result<Map<String, Object>> querySitesForWE(@RequestBody SiteRequestDTO siteRequestDTO){
        return industryDeviceServiceBak.querySitesForWE(siteRequestDTO);
    }

    /**
     * 根据摄像机标签、部门编码、算法，查询摄像机信息
     * @apiNote 根据摄像机标签、部门编码、算法，查询摄像机信息
     * @apiVersion
     * @tag 设备能力
     * @requestDemo
     * {
     *     "labelIdList":[
     *         "425",
     *         "426"
     *     ],
     *     "algorithmCodeList":[
     *         "ZGTT-1010100001"
     *     ],
     *     "deviceTypeList":[
     *         "2"
     *     ],
     *     "deptId":"122035797-01",
     *     "pageNum":1,
     *     "pageSize":2
     * }
     * @responseDemo
     * 返回参数解析：
     * 		code：返回编码
     * 		msg：返回信息
     * 		time：时间
     * 		data：返回数据
     * 		longitude：经度
     * 		latitude：纬度
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		deviceStatus：设备状态:0在线，1离线
     * 		deviceType：摄像机类型
     * 		isMonitor：是否收藏
     *
     * {
     *     "total":3,
     *     "rows":[
     *         {
     *             "deviceCode":"11010800011327000002",
     *             "deviceName":"-22",
     *             "latitude":"40.245917344437586",
     *             "longitude":"116.85979678675973",
     *             "deviceStatus":"0",
     *             "deviceType":"1",
     *             "isMonitor":"0"
     *         },
     *         {
     *             "deviceCode":"11010800831327000008",
     *             "deviceName":"-11-243",
     *             "latitude":"39.959658",
     *             "longitude":"116.268613",
     *             "deviceStatus":"0",
     *             "deviceType":"1",
     *             "isMonitor":"0"
     *         }
     *     ],
     *     "code":0
     * }
     * @param devicesByConditionVO
     * @return
     */
    @ApiOperation(value = "根据摄像机对应的标签，部门编码、算法，展示该标签下或者组织机构下（含本组织机构下所有的子节点）或者算法下或者摄像机类型下，所对应的的摄像机信息。")
    @PostMapping("/industry/device/queryDevicesByCondition")
    public TableDataInfo queryDevicesByCondition(@RequestBody DevicesByConditionVO devicesByConditionVO){
        return industryDeviceServiceBak.queryDevicesByCondition(devicesByConditionVO);
    }

    /**
     * 大摄像机查询总数接口
     * @apiNote 3.22摄像机查询总数接口
     * @apiVersion 3.22
     * @tag 设备能力
     * @requestDemo
     * {
     *     "deptList":[
     *         "122035807-0102",
     *         "122035807-010201"
     *     ]
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * 		totalCnt：摄像机总数
     * 		onlineCnt：摄像机在线数量
     * 		deptList：组织集合
     * 		deptId：组织编码
     * 		deviceCode：设备编码
     * 		isOnline：在线状态 0在线，1离线
     *
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1685353012491,
     *     "data":{
     *         "totalCnt":"26",
     *         "onlineCnt":"23",
     *         "deptList":[
     *             {
     *                 "devicesList":[
     *                     {
     *                         "deviceCode":"51152400831327000001",
     *                         "isOnline":"0"
     *                     },
     *                     {
     *                         "deviceCode":"51150200831327000044",
     *                         "isOnline":"0"
     *                     }
     *                 ],
     *                 "deptId":"122035807-0102"
     *             },
     *             {
     *                 "devicesList":[
     *                     {
     *                         "deviceCode":"11010800011327000002",
     *                         "isOnline":"0"
     *                     }
     *                 ],
     *                 "deptId":"122035807-010201"
     *             }
     *         ]
     *     }
     * }
     * @param devicesStatisticsReqVO
     * @return
     */
    @ApiOperation(value = "新增摄像机查询总数接口，根据租户ID或者组织列表，查询该租户下或者所属组织列表下，对应组织的摄像机总数")
    @PostMapping("/industry/device/queryDevicesStatistics")
    public Result<DevicesStatisticsResVO> queryDevicesStatistics(@RequestBody DevicesStatisticsReqVO devicesStatisticsReqVO){
        return industryDeviceServiceBak.queryDevicesStatistics(devicesStatisticsReqVO);
    }

    /**
     * 大屏设备打点，新接口，返回站址，站址下放设备list
     * @apiNote 3.15大屏设备打点，新接口，返回站址，站址下放设备list
     * @apiVersion 3.15
     * @tag 设备能力
     * @requestDemo
     * {}
     * @responseDemo
     * 返回参数data下字段解析：
     * 		longitude：经度
     * 		latitude：纬度
     * 		siteCode：站址编码
     * 		deviceCode：设备编码
     * 		height：设备挂高，单位：米
     * 		status：设备状态:0在线，1离线
     * 		categoryCode：设备类型 0-NVR,1-网络摄像机,2-AR摄像机,3-热成像摄像机,4、人脸卡口摄像机，5-车辆卡口摄像机；7-无人机
     *
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1690940521609,
     *     "data":[
     *         {
     *             "longitude":"121.515718",
     *             "latitude":"38.846279",
     *             "siteCode":"110801700000214496",
     *             "list":[
     *                 {
     *                     "deviceCode":"11010800031327000002",
     *                     "height":"39.0",
     *                     "status":"0",
     *                     "categoryCode":"1"
     *                 }
     *             ]
     *         }
     *     ]
     * }
     * @param siteRequestDTO
     * @return
     */
    @PostMapping("/video/device/querySiteCodeAndDevices")
    @ApiOperation("功能描述: 大屏设备打点，新接口，返回站址，站址下放设备list")
    public Result<List<IndustryDeviceTree>> querySiteCodeAndDevices(@RequestBody SiteRequestDTO siteRequestDTO){
        return industryDeviceServiceBak.querySiteCodeAndDevices(siteRequestDTO);
    }

    /**
     * 看这里
     * @apiNote 3.16看这里
     * @apiVersion 3.16
     * @tag 设备能力
     * @requestDemo
     * {
     *     "longitude":103.747971,
     *     "latitude":28.819229,
     *     "urlNum":5,
     *     "distance":"3000"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * siteList：站址集合
     * 		longitude：经度
     * 		latitude：纬度
     * 		siteCode：站址编码
     * 		siteName：站址名称
     * 		status：设备状态:0在线，1离线
     * urlList：播放url集合
     * 		deviceCode：设备编码
     * 		channelCode：通道编码
     * 		longitude：经度
     * 		latitude：纬度
     * 		streamUrl：流地址
     *
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1690794131281,
     *     "data":{
     *         "siteList":[
     *             {
     *                 "longitude":"103.738872790",
     *                 "latitude":"28.820582714",
     *                 "siteCode":"de8b4dbb208545c6b6295acf18db72c5",
     *                 "siteName":"",
     *                 "status":"0"
     *             },
     *             {
     *                 "longitude":"103.738872790",
     *                 "latitude":"28.820582714",
     *                 "siteCode":"de8b4dbb208545c6b6295acf18db72c5",
     *                 "siteName":"",
     *                 "status":"0"
     *             }
     *         ],
     *         "urlList":[
     *             {
     *                 "deviceCode":"51150200831327000256+51150200831187000005",
     *                 "channelCode":"51150200001180000007",
     *                 "longitude":"103.738872790",
     *                 "latitude":"28.820582714",
     *                 "streamUrl":"wss://slsptest.tower0788.cn:9535/live/51150200831187000005_0_0_968fa8d7bff248dba8208d1b52a53d64.flv"
     *             }
     *         ]
     *     }
     * }
     * @param deviceLayerReq
     * @return
     */
    @PostMapping("/video/device/lookHere")
    @ApiOperation("看这里")
    public Result<DetectResultVO> lookHere(@RequestBody DeviceLayerReq deviceLayerReq){
        return industryDeviceServiceBak.lookHere(deviceLayerReq);
    }

    /**
     * 雷达联动接力/取消接力接口
     * @apiNote 雷达联动接力/取消接力接口
     * @apiVersion 3.63
     * @tag 设备能力
     * @requestDemo {
     *     "deviceCodeList":[
     *         "123",
     *         "124"
     *     ],
     *     "targetId":"123",
     *     "type":"start"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     messageId: 消息ID - 用来查询正在随动的相机
     *     failDeviceCodeList: 处理失败的设备编码列表
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "data":{
     *         "failDeviceCodeList":[
     *             "123",
     *             "124"
     *         ]
     *     }
     * }
     */
    @PostMapping("/industry/device/radarTargetFollowRelay")
    @ApiOperation("雷达联动接力/取消接力接口")
    public Result<RadarTargetFollowResultVO> radarTargetFollowRelay(@RequestBody RadarTargetFollowRelayVO param){
        return industryDeviceServiceBak.radarTargetFollowRelay(param);
    }

    /**
     * 雷达联动同时随动/取消同时随动接口
     * @apiNote 雷达联动同时随动/取消同时随动接口
     * @apiVersion 3.64
     * @tag 设备能力
     * @requestDemo {
     *     "deviceCodeList":[
     *         "123",
     *         "124"
     *     ],
     *     "targetId":"123",
     *     "type":"start"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     failDeviceCodeList: 处理失败的设备编码列表
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "data":{
     *         "failDeviceCodeList":[
     *             "123",
     *             "124"
     *         ]
     *     }
     * }
     */
    @PostMapping("/industry/device/radarTargetFollowLookTo")
    @ApiOperation("雷达联动同时随动/取消同时随动接口")
    public Result<RadarTargetFollowResultVO> radarTargetFollowLookTo(@RequestBody RadarTargetFollowLookToVO param){
        return industryDeviceServiceBak.radarTargetFollowLookTo(param);
    }

    /**
      * 根据设备编码查询设备附件信息
      * @apiNote   根据设备编码查询设备附件信息
      * @apiVersion 3.65
      * @tag 设备能力
      * @requestDemo
      * ?deviceCode=xxx
      * @responseDemo
      * 返回参数data下字段解析：
      *     mediaFileUrlList：视频附件地址列表
      *     imgFileUrlList：图像附件地址列表
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "data":
      *         {
      *             "mediaFileUrlList": ["https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/2.0/8cf576079fbbddce05679af789ab183d/1cc9e1d4fb9e15d0a6c158820c4eb113/b3b4825eae5f47ddbd2ca22b53675de0.MP4?AccessKeyId=FIFYCLGVA3XZR4XRFLII&Expires=1691377193&Signature=VXCl26MUbldJ%2B3Xb0AfhWehv2xM%3D"],
      *             "imgFileUrlList": ["https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/2.0/ec7c3b0bc5f9b10ff3b4839d88332a78/ee2ce2fd812e4368fef64b6a3d7be546/b05060f7030e46bb8654007b1e73064a.PNG?AccessKeyId=FIFYCLGVA3XZR4XRFLII&Expires=1690770272&Signature=GbvThJq7u5NtJIL%2BaF7LHOM5GQU%3D"]
      *         }
      * }
      */
    @GetMapping("/video/device/getFileInfoByDeviceCode")
    @ApiOperation("根据设备编码查询设备附件信息")
    public Result<DeviceAttachmentVO> getFileInfoByDeviceCode(@RequestParam("deviceCode")String deviceCode){
        return industryDeviceServiceBak.getFileInfoByDeviceCode(deviceCode);
    }


    /**
     * 雷达联动获取目标设备列表接口
     * @apiNote   雷达联动获取目标设备列表接口
     * @apiVersion 3.66
     * @tag 设备能力
     * @requestDemo
     * {
     * "deviceCodeList":["1234","1234"]
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     deviceCode：设备编码
     *     status：联动状态 1-正在多目标联动 2-正在接力
     *     targetId：目标id
     *     messageId：消息id
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1691738964221,
     *     "data": [
     *          {
     *             "deviceCode":"1234",
     *             "status":1,
     *             "targetId":"1232131",
     *             "messageId":"111111111"
     *          },
     *          {
     *             "deviceCode":"1234",
     *             "status":1,
     *             "targetId":"1232131",
     *             "messageId":"111111111"
     *          }
     *      ]
     * }
     */
    @ApiOperation(value = "雷达联动获取目标设备列表接口")
    @PostMapping("/industry/device/queryRadarTargetList")
    public Result<List<RadarDeviceStatusVO>> queryRadarTargetList(@RequestBody RadarTargetDTO radarTargetDTO){
        return industryDeviceServiceBak.queryRadarTargetList(radarTargetDTO);
    }

    /**
     * 查询指定范围内设备
     * @apiNote 3.74 查询指定范围内设备
     * @apiVersion 3.74
     * @tag 设备能力
     * @requestDemo
     * {
     *     "latitude":"122.111",
     *     "longitude":"37.11",
     *     "queryType":"",// 1，2
     *     "deviceName":"",
     *     "deviceCode":"",
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1648107167647,
     *     "data":[
     *         {
     *             "longitude":"122.111",
     *             "latitude":"37.11",
     *             "deviceCode":"100000000022",
     *             "deviceName":"22",
     *             "distance":"10.007935794096117",
     *             "list":[
     *                 {
     *                     "channelCode":"36040200001130000003",
     *                     "channelName":"36040200001130000003"
     *                 }
     *             ]
     *         }
     *     ]
     * }
     * @param siteRequestDTO
     * @return
     */
    @ApiOperation(value = "查询指定范围内的设备")
    @PostMapping("/video/warningEvent/querySitesByLocal")
    public Result<List<SiteResultVO>> querySitesByLocal(@RequestBody SiteRequestDTO siteRequestDTO){
        return industryDeviceServiceBak.querySitesByLocal(siteRequestDTO);
    }

    /**
     * 分页查询摄像机图层
     * @apiNote 3.75 分页查询摄像机图层
     * @apiVersion 3.75
     * @tag 设备能力
     * @requestDemo
     * {
     *     "provinceId": "110000",
     *    "cityId": "110100",
     *    "countyId": "110101",
     *     "pageNum": 1,
     *   "pageSize": 15,
     *   "keyWord": "23123"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1648107167647,
     *     "data":[
     *         {
     *             "longitude":"122.111",
     *             "latitude":"37.11",
     *             "deviceCode":"100000000022"
     *         }
     *     ]
     * }
     * @param deviceLayerReq
     * @return
     */
    @ApiOperation("分页查询摄像机图层")
    @PostMapping("/industry/device/pageDeviceLayer")
    public Result<PageInfo<IndustryDeviceTree>> pageDeviceLayer(@RequestBody DeviceLayerReq deviceLayerReq) {
        return industryDeviceServiceBak.pageDeviceLayer(deviceLayerReq);
    }

}
