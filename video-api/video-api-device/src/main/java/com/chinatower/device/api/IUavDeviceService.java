package com.chinatower.device.api;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.deprecated.IUavDeviceServiceMigrate;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.UavSecureStrategyInfoVO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Objects;

@Component
public class IUavDeviceService {

    @Autowired
    private IDeviceCommonService iDeviceCommonService;

    @Autowired
    private IUavDeviceServiceMigrate iUavDeviceServiceMigrate;

    private int UAV_QUERY_TYPE = 1;


    /**
      * 根据设备编码查询无人机设备信息
      * @apiNote 根据设备编码查询无人机设备信息
      * @apiVersion 3.61
      * @tag 三、设备能力
      * @responseDemo
      * 返回参数data下字段解析：
      *      deviceCode: 设备编码
      *      deviceName: 设备名称
      *      deviceStatus: 设备状态:0在线，1离线
      *      manufacturerCode: 厂家编码
      *      modelCode: 型号编码
      *      location: 设备地址
      *      manufacturerName: 设备厂家名称
      *      modelName: 设备型号名称
      *      provinceCode: 省份编码
      *      cityCode: 地市编码
      *      countyCode: 区县编码
      *      townCode: 乡镇编码
      *      longitude: 精度
      *      latitude: 纬度
      *      areaCode: 行政编码
      *      admCode: 管理区域
      *      deviceType: 设备类型编码
      *      provinceName: 省份编码
      *      cityName: 地市编码
      *      countyName: 乡镇编码
      *      createTime: 创建时间
      *      modifier: 修改人
      *      creator: 创建人
      *      orderStatus: 订购状态
      *      tenantName: 租户名称
      *      orderStatusName: 订购状态名称
      *      deviceTypeName: 设备类型名称
      *      channelList: 通道列表
      *      orgCode: 所属组织编码
      *      orgRelId: 所属组织ID
      *      orgName: 所属组织名称
      *      townName: 乡镇名称
      *      "orgRels": 所属组织集合
      *  {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1690796917230,
      *     "data": {
      *         "deviceCode": "51150200833007000001",
      *         "deviceName": "三江新区白莲",
      *         "deviceStatus": 0,
      *         "manufacturerCode": "dji",
      *         "modelCode": "303",
      *         "location": "设备地址",
      *         "manufacturerName": "大疆",
      *         "modelName": "DJI Mavic3行业系列",
      *         "provinceCode": "120000",
      *         "cityCode": "120100",
      *         "countyCode": "120102",
      *         "townCode": "",
      *         "longitude": "116.396908000",
      *         "latitude": "39.908607000",
      *         "areaCode": "469002",
      *         "admCode": "469002",
      *         "deviceType": "100",
      *         "provinceName": "天津市",
      *         "cityName": "天津市",
      *         "countyName": "河东区",
      *         "createTime": "2023-06-12 13:55:57",
      *         "updateTime": "2023-06-26 14:16:33",
      *         "modifierName": null,
      *         "creatorName": null,
      *         "modifier": "weichuang001",
      *         "creator": "oper",
      *         "orderStatus": null,
      *         "tenantName": null,
      *         "orderStatusName": null,
      *         "deviceTypeName": "无人固定翼飞机",
      *         "channelList": null,
      *         "orgCode": "120046990-0107,120046990-0104,120046990-0105,120046990-010501,120046990-0106,120046990-010601,120046990-0108,120046990-0102,120046990-01,120046990-01060101,120046990-0109,120046990-0101",
      *         "orgRelId": "6656853376329929745,6656853376329929833,6656853376329929834,6656853376329929835,6656853376329929836,6656853376329929837,6656853376329929839,6656853376329931095,6656853376329931837,6656853376329933266,6656853376329933551,6656853376329933558",
      *         "orgName": "无人机权限,海淀（没权限）,uuu,哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈,测试组,动物园,大喇叭权限,西城区（没权限）,测试客户0010-通用平台版本-1.0,长度测试长度测长度测试长度长度测试试,测试,海淀区（大华243）",
      *         "townName": null,
      *         "orgRels": null
      *     }
      * }
      */
    @PostMapping("/uav/device/qryUavDeviceInfo/{deviceCode}")
    public Result<UavDeviceQueryVO> qryUavDeviceInfo(@PathVariable("deviceCode") String deviceCode) {
        DeviceCommonInfoVO deviceInfoVO = new DeviceCommonInfoVO();
        deviceInfoVO.setDeviceCode(deviceCode);
        deviceInfoVO.setQueryType(UAV_QUERY_TYPE);
        Result<DeviceCommonInfoVO> result = iDeviceCommonService.getDeviceInfo(deviceInfoVO);
        DeviceCommonInfoVO infoVO = result.getData();
        UavDeviceQueryVO deviceQueryVO = null;
        if (Objects.nonNull(infoVO)) {
            deviceQueryVO = new UavDeviceQueryVO();
            BeanUtils.copyProperties(infoVO, deviceQueryVO);
        }
        return Result.data(deviceQueryVO);
    }

    @PostMapping("/uav/device/queryUavDetail")
    public Result<UavDetailVO> queryUavDetail(@RequestBody UavDeviceDTO param) {
        return iUavDeviceServiceMigrate.queryUavDetail(param);
    }

    /**
      * 查询无人机区域设备树
      * @apiNote 查询无人机区域设备树
      * @apiVersion 3.62
      * @tag 三、设备能力
      * @requestDemo
      * {
      *     "isMonitorFlag": "1"
      * }
      * @responseDemo
     * 返回参数data下字段解析：
     *       code: 节点编码 （第一层中国节点）
     *       name: 节点名称
     *       num: 设备数量
     *       onlineNum: 在线数量
     *       level: 节点层级
     *       townCode: 乡镇编码
     *       townName: 乡镇名称
     *       deviceCode: 设备编码
     *       status: 设备状态 0 在线 1 离线
     *       townCode: 乡镇编码
     *       townName: 乡镇名称
     *       deviceAlias: 设备别名
     *       code: 节点编码（设备编码）
     *       name: 节点名称
     *       longitude: 精度
     *       latitude: 纬度
     *       num: 设备数量
     *       onlineNum: 在线数量
     *       isMonitor: 是否收藏 0未收藏 1已收藏
     *       isMonitorNum: 收藏数量
     *       siteCode: 站址编码
     *       accessNode: 接入节点编码
     *       altitude：海拔
     * {
     *   "code": 200,
     *   "msg": "请求成功",
     *   "time": 1736214684103,
     *   "data": [
     *     {
     *       "code": "100000",
     *       "name": "中国",
     *       "num": 4,
     *       "onlineNum": 2,
     *       "videoNum": 0,
     *       "list": [
     *         {
     *           "code": "130000",
     *           "name": "河北省",
     *           "level": "1",
     *           "num": 1,
     *           "onlineNum": 0,
     *           "videoNum": 1,
     *           "list": [
     *             {
     *               "code": "130600",
     *               "name": "保定市",
     *               "level": "2",
     *               "num": 1,
     *               "onlineNum": 0,
     *               "videoNum": 1,
     *               "list": [
     *                 {
     *                   "code": "130683",
     *                   "name": "安国市",
     *                   "level": "3",
     *                   "num": 1,
     *                   "onlineNum": 0,
     *                   "videoNum": 1,
     *                   "list": [
     *                     {
     *                       "townCode": "130683103000",
     *                       "townName": "郑章镇",
     *                       "code": "130683103000",
     *                       "name": "郑章镇",
     *                       "level": "6",
     *                       "num": 1,
     *                       "onlineNum": 0,
     *                       "videoNum": 1,
     *                       "list": [
     *                         {
     *                           "deviceCode": "11090300003007000001",
     *                           "status": "1",
     *                           "townCode": "130683103000",
     *                           "townName": "郑章镇",
     *                           "deviceAlias": "永兴社区-5G铁塔无人机",
     *                           "code": "11090300003007000001",
     *                           "name": "永兴社区-5G铁塔无人机",
     *                           "level": "4",
     *                           "longitude": "115.240020847",
     *                           "latitude": "38.457918253",
     *                           "num": 1,
     *                           "onlineNum": 0,
     *                           "videoNum": 1,
     *                           "list": [],
     *                           "isMonitor": "0",
     *                           "isMonitorNum": 0,
     *                           "siteCode": "50b37d94faf14eff856d454909c86b04",
     *                           "deviceStatusCount": 0,
     *                           "accessNode": "10000000001"
     *                         }
     *                       ],
     *                       "isMonitor": "0",
     *                       "isMonitorNum": 0,
     *                       "deviceStatusCount": 0
     *                     }
     *                   ],
     *                   "isMonitor": "0",
     *                   "isMonitorNum": 0,
     *                   "deviceStatusCount": 0
     *                 }
     *               ],
     *               "isMonitor": "0",
     *               "isMonitorNum": 0,
     *               "deviceStatusCount": 0
     *             }
     *           ],
     *           "isMonitor": "0",
     *           "isMonitorNum": 0,
     *           "deviceStatusCount": 0
     *         },
     *         {
     *           "code": "140000",
     *           "name": "山西省",
     *           "level": "1",
     *           "num": 1,
     *           "onlineNum": 0,
     *           "videoNum": 1,
     *           "list": [
     *             {
     *               "code": "140100",
     *               "name": "太原市",
     *               "level": "2",
     *               "num": 1,
     *               "onlineNum": 0,
     *               "videoNum": 1,
     *               "list": [
     *                 {
     *                   "code": "140109",
     *                   "name": "万柏林区",
     *                   "level": "3",
     *                   "num": 1,
     *                   "onlineNum": 0,
     *                   "videoNum": 1,
     *                   "list": [
     *                     {
     *                       "townCode": "140109002000",
     *                       "townName": "下元街道",
     *                       "code": "140109002000",
     *                       "name": "下元街道",
     *                       "level": "6",
     *                       "num": 1,
     *                       "onlineNum": 0,
     *                       "videoNum": 1,
     *                       "list": [
     *                         {
     *                           "deviceCode": "11080200003007000001",
     *                           "status": "1",
     *                           "townCode": "140109002000",
     *                           "townName": "下元街道",
     *                           "deviceAlias": "相公庄邮政支局/视频监控设备1",
     *                           "code": "11080200003007000001",
     *                           "name": "相公庄邮政支局/视频监控设备1",
     *                           "level": "4",
     *                           "longitude": "112.505088000",
     *                           "latitude": "37.849567000",
     *                           "num": 1,
     *                           "onlineNum": 0,
     *                           "videoNum": 1,
     *                           "list": [],
     *                           "isMonitor": "1",
     *                           "isMonitorNum": 1,
     *                           "siteCode": "5d0adf34268e40e6b2eb38e0d8fc3e3b",
     *                           "deviceStatusCount": 0,
     *                           "accessNode": "10000000001"
     *                         }
     *                       ],
     *                       "isMonitor": "1",
     *                       "isMonitorNum": 1,
     *                       "deviceStatusCount": 0
     *                     }
     *                   ],
     *                   "isMonitor": "1",
     *                   "isMonitorNum": 1,
     *                   "deviceStatusCount": 0
     *                 }
     *               ],
     *               "isMonitor": "1",
     *               "isMonitorNum": 1,
     *               "deviceStatusCount": 0
     *             }
     *           ],
     *           "isMonitor": "1",
     *           "isMonitorNum": 1,
     *           "deviceStatusCount": 0
     *         },
     *         {
     *           "code": "430000",
     *           "name": "湖南省",
     *           "level": "1",
     *           "num": 2,
     *           "onlineNum": 2,
     *           "videoNum": 2,
     *           "list": [
     *             {
     *               "code": "430600",
     *               "name": "岳阳市",
     *               "level": "2",
     *               "num": 1,
     *               "onlineNum": 1,
     *               "videoNum": 1,
     *               "list": [
     *                 {
     *                   "code": "430602",
     *                   "name": "岳阳楼区",
     *                   "level": "3",
     *                   "num": 1,
     *                   "onlineNum": 1,
     *                   "videoNum": 1,
     *                   "list": [
     *                     {
     *                       "townCode": "430602010000",
     *                       "townName": "奇家岭街道",
     *                       "code": "430602010000",
     *                       "name": "奇家岭街道",
     *                       "level": "6",
     *                       "num": 1,
     *                       "onlineNum": 1,
     *                       "videoNum": 1,
     *                       "list": [
     *                         {
     *                           "deviceCode": "43060200833007000001",
     *                           "status": "0",
     *                           "townCode": "430602010000",
     *                           "townName": "奇家岭街道",
     *                           "deviceAlias": "北京现代4S店",
     *                           "code": "43060200833007000001",
     *                           "name": "北京现代4S店",
     *                           "level": "4",
     *                           "longitude": "113.140999",
     *                           "latitude": "29.316111",
     *                           "num": 1,
     *                           "onlineNum": 1,
     *                           "videoNum": 1,
     *                           "list": [],
     *                           "isMonitor": "1",
     *                           "isMonitorNum": 1,
     *                           "siteCode": "9a7f270983ff4b8aaac1be65b577fe79",
     *                           "deviceStatusCount": 0,
     *                           "accessNode": "10000000001"
     *                         }
     *                       ],
     *                       "isMonitor": "1",
     *                       "isMonitorNum": 1,
     *                       "deviceStatusCount": 0
     *                     }
     *                   ],
     *                   "isMonitor": "1",
     *                   "isMonitorNum": 1,
     *                   "deviceStatusCount": 0
     *                 }
     *               ],
     *               "isMonitor": "1",
     *               "isMonitorNum": 1,
     *               "deviceStatusCount": 0
     *             },
     *             {
     *               "code": "431000",
     *               "name": "郴州市",
     *               "level": "2",
     *               "num": 1,
     *               "onlineNum": 1,
     *               "videoNum": 1,
     *               "list": [
     *                 {
     *                   "code": "431003",
     *                   "name": "苏仙区",
     *                   "level": "3",
     *                   "num": 1,
     *                   "onlineNum": 1,
     *                   "videoNum": 1,
     *                   "list": [
     *                     {
     *                       "townCode": "431003005000",
     *                       "townName": "卜里坪街道",
     *                       "code": "431003005000",
     *                       "name": "卜里坪街道",
     *                       "level": "6",
     *                       "num": 1,
     *                       "onlineNum": 1,
     *                       "videoNum": 1,
     *                       "list": [
     *                         {
     *                           "deviceCode": "43102500003007000001",
     *                           "uavNestCode": "43102500003027000001",
     *                           "uavNestName": "郴州临武县文体中心",
     *                           "uavNestStatus": "0",
     *                           "status": "1",
     *                           "townCode": "431003005000",
     *                           "townName": "卜里坪街道",
     *                           "deviceAlias": "郴州临武县文体中心",
     *                           "code": "43102500003007000001",
     *                           "name": "郴州临武县文体中心",
     *                           "level": "4",
     *                           "longitude": "113.022896",
     *                           "latitude": "25.837799",
     *                           "num": 1,
     *                           "onlineNum": 1,
     *                           "videoNum": 1,
     *                           "list": [],
     *                           "isMonitor": "0",
     *                           "isMonitorNum": 0,
     *                           "siteCode": "819ec2d0c1be43bb9344b9a65d7134c3",
     *                           "deviceStatusCount": 0,
     *                           "accessNode": "10000000001",
     *                           "altitude": "167.25"
     *                         }
     *                       ],
     *                       "isMonitor": "0",
     *                       "isMonitorNum": 0,
     *                       "deviceStatusCount": 0
     *                     }
     *                   ],
     *                   "isMonitor": "0",
     *                   "isMonitorNum": 0,
     *                   "deviceStatusCount": 0
     *                 }
     *               ],
     *               "isMonitor": "0",
     *               "isMonitorNum": 0,
     *               "deviceStatusCount": 0
     *             }
     *           ],
     *           "isMonitor": "0",
     *           "isMonitorNum": 1,
     *           "deviceStatusCount": 0
     *         }
     *       ],
     *       "first": 1,
     *       "isMonitor": "0",
     *       "isMonitorNum": 0,
     *       "deviceStatusCount": 0
     *     }
     *   ]
     * }
     */
    @PostMapping("/uav/device/getUavMonitorAreaTree")
    public Result<List<DeviceTreeVO>> getUavMonitorAreaTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        deviceReqDTO.setNeedDevice(true);
        deviceReqDTO.setQueryType(UAV_QUERY_TYPE);
        return iDeviceCommonService.getDeviceAreaTree(deviceReqDTO);
    }

    /**
      * 查询无人机组织设备树
      * @apiNote 查询无人机组织设备树
      * @apiVersion 3.63
      * @tag 三、设备能力
      * @requestDemo
      * {
      *    "isMonitorFlag":"1"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *     code: 节点编码
      *     name: 节点名称
      *     level: 节点层级
      *     parentCode: 父节点编码
      *     num: 设备数量
      *     onlineNum: 在线设备数量
      *     deviceCode: 设备编码
      *     deviceName: 设备名称
      *     channelCode: 通道编码
      *     channelName: 通道名称
      *     status: 状态
      *     longitude: 经度
      *     latitude: 维度
      *     checked: 前台使用，默认 false
      *     isMonitor: 是否收藏 0未收藏 1已收藏
      *     isMonitorNum: 收藏数量
      *     first: 标注作为集合的第一条数据
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
      */
    @PostMapping("/uav/device/getUavMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getUavMonitorOrgTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        deviceReqDTO.setQueryType(UAV_QUERY_TYPE);
        return iDeviceCommonService.getDeviceOrgTree(deviceReqDTO);
    }

    /**
      * 查询无人机分布情况
      * @apiNote  查询无人机分布情况
      * @apiVersion 3.64
      * @tag 三、设备能力
      * @requestDemo
      *  {
      *      "cityId": "12345"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *     siteCode: 站址编码
      *     longitude: 经度
      *     latitude: 纬度
      *     height: 高度
      *     allLine: 是否在线 0-在线  1-离线
      *     deviceCode: 设备编码
      *
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1690968021542,
      *     "data": [
      *         {
      *             "siteCode": null,
      *             "longitude": "112.936295000",
      *             "latitude": "27.905101000",
      *             "height": null,
      *             "allLine": "1",
      *             "deviceCode": "43030200833007000001",
      *             "categoryCode": null
      *         },
      *         {
      *             "siteCode": null,
      *             "longitude": "116.396908000",
      *             "latitude": "39.908607000",
      *             "height": null,
      *             "allLine": "0",
      *             "deviceCode": "51150200833007000001",
      *             "categoryCode": null
      *         },
      *         {
      *             "siteCode": null,
      *             "longitude": "116.363741000",
      *             "latitude": "39.920487000",
      *             "height": null,
      *             "allLine": "0",
      *             "deviceCode": "46900200833007000001",
      *             "categoryCode": null
      *         },
      *         {
      *             "siteCode": null,
      *             "longitude": "116.698686000",
      *             "latitude": "39.575920000",
      *             "height": null,
      *             "allLine": "0",
      *             "deviceCode": "11010500833007000001",
      *             "categoryCode": null
      *         },
      *         {
      *             "siteCode": null,
      *             "longitude": "116.3699518371145",
      *             "latitude": "39.921861623877284",
      *             "height": null,
      *             "allLine": "0",
      *             "deviceCode": "469002008330070000011",
      *             "categoryCode": null
      *         },
      *         {
      *             "siteCode": null,
      *             "longitude": "116.3699518371145",
      *             "latitude": "39.921861623877284",
      *             "height": null,
      *             "allLine": "0",
      *             "deviceCode": "110105008330070000011",
      *             "categoryCode": null
      *         }
      *     ]
      * }
      */
    @PostMapping("/uav/device/queryUavSitesForWE")
    public Result<List<SiteResultVO>> queryUavSitesForWE(@RequestBody SiteRequestDTO siteRequestDTO) {
        siteRequestDTO.setQueryType(UAV_QUERY_TYPE);
        Result<List<DeviceSiteVO>> result = iDeviceCommonService.getDeviceListBySiteCode(siteRequestDTO);
        List<SiteResultVO> list = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(result.getData())) {
            result.getData().forEach(item -> {
                SiteResultVO siteResultVO = new SiteResultVO();
                BeanUtils.copyProperties(item, siteResultVO);
                list.add(siteResultVO);
            });
        }
        return Result.data(list);
    }

    /**
     *
     * 无人机地图打点
     * @param siteRequestDTO
     * @return
     */
    @PostMapping("/uav/device/queryUavSites")
    public Result<List<SiteResultVO>> queryUavSites(@RequestBody SiteRequestDTO siteRequestDTO) {
        siteRequestDTO.setQueryType(UAV_QUERY_TYPE);
        Result<List<DeviceSiteVO>> result = iDeviceCommonService.getDeviceListBySiteCode(siteRequestDTO);
        List<SiteResultVO> list = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(result.getData())) {
            result.getData().forEach(item -> {
                SiteResultVO siteResultVO = new SiteResultVO();
                BeanUtils.copyProperties(item, siteResultVO);
                list.add(siteResultVO);
            });
        }
        return Result.data(list);
    }

    /**
     *
      * 根据站址查询无人机列表
      * @apiNote  根据站址查询无人机列表
      * @apiVersion 3.65
      * @tag 三、设备能力
      * @requestDemo
      * {
      *     "siteCode": "10001",
      *     "longitude": "116.396908000",
      *     "latitude": "39.908607000"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *      deviceName: 设备名称
      *      deviceCode: 设备编码
      *      status: 设备状态 0-在线，1-离线
      *      longitude: 经度
      *      latitude: 纬度
      *      location: 设备地址
      *      height: 挂高
      *      siteCode: 站址编码
      *      modelCode: 设备型号
      *      modelName: 设备型号名称
      *      focusRange: 焦距范围
      *      vertiDownNeg: 水平向下为负  0否1是
      *
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
      */
    @PostMapping("/uav/device/queryUavDevicesBySiteCode")
    public Result<List<DeviceSiteVO>> queryUavDevicesBySiteCode(@RequestBody SiteRequestDTO siteRequestDTO) {
        siteRequestDTO.setQueryType(UAV_QUERY_TYPE);
        return iDeviceCommonService.getDeviceListBySiteCode(siteRequestDTO);
    }

    /**
     * 获取安全策略详情
     */
    @PostMapping("/uav/device/queryUavSecureStrategyDetail")
    public Result<UavSecureStrategyInfoVO> queryUavSecureStrategyDetail(@RequestBody SiteRequestDTO siteRequestDTO) {
        return iUavDeviceServiceMigrate.queryUavSecureStrategyDetail(siteRequestDTO);
    }

    /**
     *
     * 同步更新当前无人机巢的信息
     * @apiNote  同步更新当前无人机巢的信息
     * @apiVersion 3.66
     * @tag 三、设备能力
     * @requestDemo
     * {"uavNestCode":"11080200003027000007"}
     * @responseDemo
     * 返回参数data下字段解析：
     *      deviceCode: 设备编码
     *      longitude: 经度
     *      latitude: 纬度
     *      address: 设备地址
     *      provinceCode: 省份编码
     *      cityCode: 地市编码
     *      countyCode: 区县编码
     *      townCode: 乡镇编码
     *      altitude: 海拔
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1741771708628,
     *     "data": {
     *         "deviceCode": "11080200003027000007",
     *         "latitude": "39.946853",
     *         "longitude": "116.227326",
     *         "provinceCode": "110000",
     *         "cityCode": "110100",
     *         "countyCode": "110108",
     *         "townCode": "110108102000",
     *         "address": "北京市海淀区四季青镇西平庄路",
     *         "altitude": "12029282"
     *     }
     * }
     */
    @PostMapping("/uav/device/syncUavNestLocationInfo")
    public Result<UavNestLocationVO> getUavNestLocationInfo(@RequestBody UavDeviceDTO param) {
        return iUavDeviceServiceMigrate.getUavNestLocationInfo(param);
    }
}
