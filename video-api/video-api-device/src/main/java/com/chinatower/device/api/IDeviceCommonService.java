package com.chinatower.device.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.factory.DeviceCommonFallbackFactory;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "deviceCommonService", value = ServiceNameConstants.DEVICE_SERVICE, fallbackFactory = DeviceCommonFallbackFactory.class)
public interface IDeviceCommonService {

    /**
     * 查询设备区域设备树
     * @apiNote 根据设备类型查询对应设备区域树 1-无人机、2-执法记录仪
     * @apiVersion 3.19
     * @tag 三、设备能力
     * @requestDemo
     * {
     *
     *     "isMonitorFlag": "1",
     *     "queryType": 1
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1692327319364,
     *     "data": [
     *         {
     *             "code": "100000",
     *             "name": "中国",
     *             "num": 9,
     *             "onlineNum": 4,
     *             "list": [
     *                 {
     *                     "level": "1",
     *                     "num": 1,
     *                     "onlineNum": 0,
     *                     "list": [
     *                         {
     *                             "level": "2",
     *                             "num": 1,
     *                             "onlineNum": 0,
     *                             "list": [
     *                                 {
     *                                     "level": "3",
     *                                     "num": 1,
     *                                     "onlineNum": 0,
     *                                     "list": [
     *                                         {
     *                                             "deviceCode": "469002008330070000011",
     *                                             "deviceAlias": "琼海牛路岭1-变更",
     *                                             "code": "469002008330070000011",
     *                                             "name": "琼海牛路岭1-变更",
     *                                             "level": "4",
     *                                             "num": 1,
     *                                             "onlineNum": 0,
     *                                             "list": [
     *                                                 {
     *                                                     "deviceCode": "469002008330070000011",
     *                                                     "deviceName": "琼海牛路岭1-变更",
     *                                                     "channelCode": "111111111111111111111",
     *                                                     "channelName": "DJI_test_03",
     *                                                     "status": "1",
     *                                                     "channelAlias": "DJI_test_03",
     *                                                     "code": "111111111111111111111",
     *                                                     "name": "DJI_test_03",
     *                                                     "level": "5",
     *                                                     "longitude": "116.3699518371145",
     *                                                     "latitude": "39.921861623877284",
     *                                                     "checked": false,
     *                                                     "isMonitor": "0",
     *                                                     "isMonitorNum": 0
     *                                                 }
     *                                             ],
     *                                             "isMonitor": "0",
     *                                             "isMonitorNum": 0
     *                                         }
     *                                     ],
     *                                     "isMonitor": "0",
     *                                     "isMonitorNum": 0
     *                                 }
     *                             ],
     *                             "isMonitor": "0",
     *                             "isMonitorNum": 0
     *                         }
     *                     ],
     *                     "isMonitor": "0",
     *                     "isMonitorNum": 0
     *                 }
     *             ],
     *             "first": 1,
     *             "isMonitor": "0",
     *             "isMonitorNum": 0
     *         }
     *     ]
     * }
     * @param    deviceReqDTO
     * @return
     */
    @PostMapping("/deviceCommon/getDeviceAreaTree")
    Result<List<DeviceTreeVO>> getDeviceAreaTree(@RequestBody DeviceReqDTO deviceReqDTO);

    /**
     * 查询设备组织树
     * @apiNote 根据类型查询对应设备的组织树
     * @apiVersion 3.20
     * @tag 三、设备能力
     * @requestDemo
     * {
     *    "isMonitorFlag":"1",
     *    "queryType": 1
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1690967808811,
     *     "data": [
     *         {
     *             "code": "100000010-01",
     *             "name": "中国联合网络通信有限公司乐山市分公司-林业版本-1.0",
     *             "level": "1",
     *             "parentCode": "000000",
     *             "num": 1,
     *             "onlineNum": 0,
     *             "list": [
     *                 {
     *                     "deviceCode": "46900200833007000001",
     *                     "deviceName": "琼海牛路岭1",
     *                     "code": "46900200833007000001",
     *                     "name": "琼海牛路岭1",
     *                     "level": "2",
     *                     "num": 1,
     *                     "onlineNum": 0,
     *                     "list": [
     *                         {
     *                             "deviceCode": "46900200833007000001",
     *                             "deviceName": "琼海牛路岭1",
     *                             "channelCode": "11111111111111111111",
     *                             "channelName": "DJI_test_03",
     *                             "status": "1",
     *                             "code": "11111111111111111111",
     *                             "name": "DJI_test_03",
     *                             "level": "3",
     *                             "longitude": "116.363741000",
     *                             "latitude": "39.920487000",
     *                             "checked": false,
     *                             "isMonitor": "0",
     *                             "isMonitorNum": 0
     *                         }
     *                     ],
     *                     "isMonitor": "0",
     *                     "isMonitorNum": 0
     *                 }
     *             ],
     *             "first": 1,
     *             "isMonitor": "0",
     *             "isMonitorNum": 0
     *         },
     *
     *             "isMonitor": "0",
     *             "isMonitorNum": 0
     *         }
     *     ]
     * }
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/deviceCommon/getDeviceOrgTree")
    Result<List<DeviceTreeVO>> getDeviceOrgTree(@RequestBody DeviceReqDTO deviceReqDTO);

    /**
     *  查询设备图层打点接口
     * @apiNote  根据类型查询对应设备图层打点接口
     * @apiVersion 3.21
     * @tag  三、设备能力
     * @requestDemo
     * {
     * "cityId": "", //市编码
     * "queryType": 1
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715307979841,
     *     "data": [
     *         {
     *             "longitude": "116.4687380000",
     *             "latitude": "39.9578090000",
     *             "siteCode": "35689",
     *             "list": [
     *                 {
     *                     "deviceCode": "11010500991327000888",
     *                     "status": "0"
     *                 }
     *             ]
     *         }
     *     ]
     * }
     * @param   siteRequestDTO
     * @return  Result<List<DeviceTreeVO>>
     */
    @PostMapping("/deviceCommon/getDeviceSites")
    Result<List<IndustryDeviceTree>> getDeviceSites(@RequestBody SiteRequestDTO siteRequestDTO);

    /**
     * 根据站址查询设备列表
     * @apiNote  根据类型查询站址下对应设备的列表
     * @apiVersion 3.22
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "siteCode": "10001",
     *     "longitude": "116.396908000",
     *     "latitude": "39.908607000",
     *     "queryType": 1
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1690968281236,
     *     "data": [
     *         {
     *             "deviceName": "三江新区白莲",
     *             "deviceCode": "51150200833007000001",
     *             "status": "0",
     *             "longitude": "116.396908000",
     *             "latitude": "39.908607000",
     *             "location": "设备地址",
     *             "height": "10.0",
     *             "siteCode": "10001",
     *             "modelCode": "303",
     *             "modelName": "DJI Mavic3行业系列",
     *             "modelId": null,
     *             "focusRange": null,
     *             "vertiDownNeg": null,
     *             "categoryCode": null
     *         }
     *     ]
     * }
     * @param   siteRequestDTO
     * @return
     */
    @PostMapping("/deviceCommon/getDeviceListBySiteCode")
    Result<List<DeviceSiteVO>> getDeviceListBySiteCode(@RequestBody SiteRequestDTO siteRequestDTO);

    /**
     * 查询指定经纬度附近的设备
     * @apiNote  查询指定经纬度附近的设备
     * @apiVersion 3.23
     * @tag    三、设备能力
     * @requestDemo
     * {
     * "latitude": "39.9578090000",
     * "longitude": "116.4687380000",
     * "radius":10000
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715307782308,
     *     "data": [
     *         {
     *             "deviceCode": "11010500991327000888",
     *             "name": "三江新区龙头山下11",
     *             "location": "北京市朝阳区七棵树创意园北侧河边",
     *             "distance": "0.0",
     *             "longitude": "116.4687380000",
     *             "latitude": "39.9578090000",
     *             "deviceStatus": "0",
     *             "height": null,
     *             "modelCode": null,
     *             "modelName": null,
     *             "deviceType": null,
     *             "deviceTypeName": null,
     *             "isMonitor": null,
     *             "deviceOriginalStatus": null,
     *             "wzone": null
     *         }
     *     ]
     * }
     * @param   deviceDistanceDTO
     * @return   Result<List<DeviceDistanceVO>>
     */
    @PostMapping("/deviceCommon/getDeviceDistanceInfo")
    Result<List<DeviceDistanceVO>> getDeviceDistanceInfo(@RequestBody DeviceDistanceDTO deviceDistanceDTO);

    /**
     * 根据设备编码查询设备详情信息
     * @apiNote  根据设备编码查询设备详情信息
     * @apiVersion 3.24
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode": "10001",
     *     "queryType": 5
     * }
     * @responseDemo
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1690857479026,
     *     "data": {
     *         "tenantId": "121119862",
     *         "id": 10113020300165,
     *         "deviceCode": "31011200831377000003",
     *         "deviceName": "修改喇叭7",
     *         "deviceStatus": "1",
     *         "manufacturerCode": "talian",
     *         "modelCode": "",
     *         "deviceType": "4001",
     *         "orderStatus": "1",
     *         "provinceCode": "110000",
     *         "provinceName": "北京市",
     *         "cityCode": "110100",
     *         "cityName": "北京市",
     *         "countyCode": "110102",
     *         "countyName": "西城区",
     *         "townCode": "110102011000",
     *         "townName": "金融街街道",
     *         "height": "50.0",
     *         "longitude": "116.372183000",
     *         "latitude": "39.919775000",
     *         "location": "北京市西城区金融街街道朱苇箔胡同中国地质调查所旧址",
     *         "isMonitor": "0",
     *         "manufacturerName": "安徽塔联"
     *     }
     * }
     * @param   deviceCommonInfoVO
     * @return
     */
    @PostMapping("/deviceCommon/getDeviceInfo")
    Result<DeviceCommonInfoVO> getDeviceInfo(@RequestBody DeviceCommonInfoVO deviceCommonInfoVO);


    /**
     * 获取当前租户当前行业下的正在飞行中的无人机设备数量
     * @return 正在飞行中的无人机设备数量
     */
    @PostMapping("/uav/device/getFlyingUavDeviceCount")
    Result<Integer> getFlyingUavDeviceCount(@RequestBody UavDeviceDTO param);
}
