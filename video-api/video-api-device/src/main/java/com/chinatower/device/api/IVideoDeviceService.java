package com.chinatower.device.api;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.deprecated.IVideoDeviceServiceMigrate;
import com.chinatower.device.entity.dto.IotDeviceMsgDTO;
import com.chinatower.device.entity.vo.DeviceDiyTreeResultVO;
import com.chinatower.device.entity.vo.DeviceDiyTreeVO;
import com.chinatower.device.entity.vo.SensorAttrMapVO;
import com.chinatower.device.entity.vo.TenantIotDeviceConfVO;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.platform.entity.device.*;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;


@Component
public class IVideoDeviceService {

    @Resource
    IVideoDeviceServiceMigrate iVideoDeviceServiceMigrate;

    @Resource
    IDeviceCommonService iDeviceCommonService;

    /**
     * 视频树查询
     * @apiNote 视频树查询
     * @apiVersion 3.81
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "customerCode":"122049393",
     *     "isMonitorFlag":"1"
     * }
     * @responseDemo
     * 		code：省市区编码
     * 		name：省市区名称
     * 		level：级别
     * 		num：通道数量
     * 		onlineNum：通道在线数量
     * 		longitude：经度
     * 		latitude：纬度
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		channelCode：通道编码
     * 		channelName：通道名称
     * 		status：通道状态:0在线，1离线
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1692255036355,
     *     "data":[
     *         {
     *             "code":"100000",
     *             "name":"中国",
     *             "num":1215,
     *             "onlineNum":917,
     *             "list":[
     *                 {
     *                     "code":"110000",
     *                     "name":"北京市",
     *                     "level":"1",
     *                     "num":22,
     *                     "onlineNum":15,
     *                     "list":[
     *                         {
     *                             "code":"110100",
     *                             "name":"北京市",
     *                             "level":"2",
     *                             "num":22,
     *                             "onlineNum":15,
     *                             "list":[
     *                                 {
     *                                     "code":"110105",
     *                                     "name":"朝阳区",
     *                                     "level":"3",
     *                                     "num":1,
     *                                     "onlineNum":1,
     *                                     "list":[
     *                                         {
     *                                             "townCode":"110105025000",
     *                                             "townName":"大屯街道",
     *                                             "code":"110105025000",
     *                                             "name":"大屯街道",
     *                                             "level":"6",
     *                                             "num":1,
     *                                             "onlineNum":1,
     *                                             "list":[
     *                                                 {
     *                                                     "deviceCode":"11010500831327000168",
     *                                                     "townCode":"110105025000",
     *                                                     "townName":"大屯街道",
     *                                                     "deviceAlias":"铁塔产业园-6号楼-海康241",
     *                                                     "code":"11010500831327000168",
     *                                                     "name":"铁塔产业园-6号楼-海康241",
     *                                                     "level":"4",
     *                                                     "num":1,
     *                                                     "onlineNum":1,
     *                                                     "list":[
     *                                                         {
     *                                                             "deviceCode":"11010500831327000168",
     *                                                             "deviceName":"铁塔产业园-6号楼-海康241",
     *                                                             "channelCode":"11010500001310000017",
     *                                                             "channelName":"铁塔产业园-6号楼-海康241_0_0",
     *                                                             "status":"0",
     *                                                             "channelAlias":"铁塔产业园-6号楼-海康241_0_0",
     *                                                             "code":"11010500001310000017",
     *                                                             "name":"铁塔产业园-6号楼-海康241_0_0",
     *                                                             "level":"5",
     *                                                             "longitude":"116.268215000",
     *                                                             "latitude":"39.959448000",
     *                                                             "checked":false,
     *                                                             "isMonitor":"0",
     *                                                             "isMonitorNum":0,
     *                                                             "facade":"1"
     *                                                         }
     *                                                     ],
     *                                                     "isMonitor":"0",
     *                                                     "isMonitorNum":0,
     *                                                     "categoryCode":"1"
     *                                                 }
     *                                             ],
     *                                             "isMonitor":"0",
     *                                             "isMonitorNum":0
     *                                         }
     *                                     ],
     *                                     "isMonitor":"0",
     *                                     "isMonitorNum":0
     *                                 }
     *                             ]
     *                         }
     *                     ]
     *                 }
     *             ]
     *         }
     *     ]
     * }
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/video/getMonitorTree")
    public Result<List<DeviceTreeVO>> getMonitorTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        return iVideoDeviceServiceMigrate.getMonitorTree(deviceReqDTO);
    }

    @PostMapping("/video/getMonitorList")
    public Result<List<DeviceTreeVO>> getMonitorList(@RequestBody DeviceReqDTO deviceReqDTO) {
        deviceReqDTO.setQueryType(1);
        return iVideoDeviceServiceMigrate.getMonitorTree(deviceReqDTO);
    }

    /**
     * 视频组织树查询
     * @apiNote 视频组织树查询
     * @apiVersion 3.82
     * @tag 三、设备能力
     * @requestDemo {"isMonitorFlag":"1"}
     * @responseDemo
     * 返回参数data下字段解析：
     * 		code：组织编码
     * 		name：组织名称
     * 		level：级别
     * 		num：通道数量
     * 		onlineNum：通道在线数量
     * 		longitude：经度
     * 		latitude：纬度
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		channelCode：通道编码
     * 		channelName：通道名称
     * 		status：通道状态:0在线，1离线
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1692255033059,
     *     "data":[
     *         {
     *             "code":"122049393-01",
     *             "name":"智联总部演示",
     *             "level":"1",
     *             "parentCode":"000000",
     *             "num":1215,
     *             "onlineNum":917,
     *             "list":[
     *                 {
     *                     "deviceCode":"11010500831327000168",
     *                     "deviceName":"铁塔产业园-6号楼-海康241",
     *                     "code":"11010500831327000168",
     *                     "name":"铁塔产业园-6号楼-海康241",
     *                     "level":"2",
     *                     "num":1,
     *                     "onlineNum":1,
     *                     "list":[
     *                         {
     *                             "deviceCode":"11010500831327000168",
     *                             "deviceName":"铁塔产业园-6号楼-海康241",
     *                             "channelCode":"11010500001310000017",
     *                             "channelName":"铁塔产业园-6号楼-海康241_0_0",
     *                             "status":"0",
     *                             "code":"11010500001310000017",
     *                             "name":"铁塔产业园-6号楼-海康241_0_0",
     *                             "level":"3",
     *                             "longitude":"116.268215000",
     *                             "latitude":"39.959448000",
     *                             "checked":false,
     *                             "isMonitor":"0",
     *                             "isMonitorNum":0,
     *                             "facade":"1"
     *                         }
     *                     ],
     *                     "isMonitor":"0",
     *                     "isMonitorNum":0,
     *                     "categoryCode":"1"
     *                 }
     *             ]
     *         }
     *     ]
     * }
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/video/getMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getMonitorOrgTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        deviceReqDTO.setQueryType(2);
        return iVideoDeviceServiceMigrate.getMonitorTree(deviceReqDTO);
    }


    /**
     * 设备/通道/预置点/监控区域/算法关系推送
     * @apiNote 设备/通道/预置点/监控区域/算法关系推送
     * @apiVersion 3.83
     * @tag 三、设备能力
     * @requestDemo
     * [
     *     {
     *         "deviceCode":"设备编码",
     *         "channelCode":"通道编码",
     *         "deviceName":"设备名称",
     *         "channelName":"通道名称",
     *         "optType":1,
     *         "extractionType":1,
     *         "thresh":0.8,
     *         "alarmInterval":180,
     *         "modelConfig":"",
     *         "preset":{
     *             "presetId":1232,
     *             "pan":12.3,
     *             "tilt":52.6,
     *             "zoom":122
     *         },
     *         "algorithmCode":"XWKJ-1012100001",
     *         "taskId":"qwwqwrwe4fwereeet43dfgdgd",
     *         "monitorArea":[
     *             {
     *                 "monitorAreaId":"sfwgfgsfetghdfgfgfdge",
     *                 "moniPointList":[
     *                     {
     *                         "moniPointId":1,
     *                         "percentX":0.65,
     *                         "percentY":0.98
     *                     },
     *                     {
     *                         "moniPointId":2,
     *                         "percentX":0.95,
     *                         "percentY":"0.121"
     *                     },
     *                     {
     *                         "moniPointId":3,
     *                         "percentX":0.12,
     *                         "percentY":0.121
     *                     }
     *                 ]
     *             },
     *             {
     *                 "monitorAreaId":"sfwgfgsfetghdfgfjkjhj",
     *                 "moniPointList":[
     *                     {
     *                         "moniPointId":1,
     *                         "percentX":0.65,
     *                         "percentY":0.98
     *                     },
     *                     {
     *                         "moniPointId":2,
     *                         "percentX":0.95,
     *                         "percentY":"0.121"
     *                     },
     *                     {
     *                         "moniPointId":3,
     *                         "percentX":0.12,
     *                         "percentY":0.121
     *                     }
     *                 ]
     *             }
     *         ]
     *     }
     * ]
     * @responseDemo
     * {
     *     "returnCode":"0000",
     *     "returnMsg":"ok",
     *     "errorCode":"",
     *     "errorMsg":"",
     *     "data":{
     *
     *     }
     * }
     */
    @PostMapping("/video/device/synchDeviceAlgorithms")
    public Result<JSONObject> synchDeviceAlgorithms(@Validated @RequestBody List<AlgorithmParamBodyVO> param) {
        return iVideoDeviceServiceMigrate.synchDeviceAlgorithms(param);
    }

    /**
     * 批量保存和更新设备信息【应该不用了删了吧】
     *
     * @return
     * @author chenmf
     * @date 2022/2/11
     * @version
     */
    @PostMapping("/video/device/batchSaveOrUpdateDevice")
    public Result<String> batchSaveOrUpdateDevice(@RequestBody List<SaveOrUpdateDeviceDTO> deviceDTOList) {
        return iVideoDeviceServiceMigrate.batchSaveOrUpdateDevice(deviceDTOList);
    }

    /**
     * 批量保存和更新物联设备信息
     * @param: iotDevices
     * @return: Result<String>
     * @author: zheng.yan
     * @date: 2022/5/10
     * @version:
     */
    @PostMapping("/iotdevice/batchSaveOrUpdateIotDevice")
    public Result<String> batchSaveOrUpdateIotDevice(@RequestBody List<IotDeviceMsgDTO> iotDevices) {
        return iVideoDeviceServiceMigrate.batchSaveOrUpdateIotDevice(iotDevices);
    }

    /**
     * 查询物联设备联动配置
     * @param: qryCond
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/5/7
     * @version:
     */
    @PostMapping("/iotdevice/getTenantIotDevConf")
    public Result<TenantIotDeviceConfVO> getTenantIotDevConf(@Valid @RequestBody TenantIotDeviceConfVO qryCond) {
        return iVideoDeviceServiceMigrate.getTenantIotDevConf(qryCond);
    }

    /**
     * 【行业调用sdk不要使用此接口，使用v2，此接口没有过滤登录权限，是给微创告警上报，通用内部使用】
     * 批量查询物联设备联动配置
     */
    @PostMapping("/iotdevice/getTenantIotDevConfBatch")
    public Result<List<TenantIotDeviceConfVO>> getTenantIotDevConfBatch(@RequestBody TenantIotDeviceConfVO qryCond){
        return iVideoDeviceServiceMigrate.getTenantIotDevConfBatch(qryCond);
    }

    /**
     * 批量查询物联设备联动配置，行业sdk可使用
     */
    @PostMapping("/iotdevice/getTenantIotDevConfBatchV2")
    public Result<List<TenantIotDeviceConfVO>> getTenantIotDevConfBatchV2(@RequestBody TenantIotDeviceConfVO qryCond){
        return iVideoDeviceServiceMigrate.getTenantIotDevConfBatchV2(qryCond);
    }

    /**
     * 查询租户管理区域
     * @apiNote 查询租户管理区域
     * @apiVersion 3.84
     * @tag 三、设备能力
     * @requestDemo
     * {}
     * @responseDemo
     * provinceId：省份编码
     * cityId：地市编码
     * countyId：区县编码
     *
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1648107167647,
     *     "data":[
     *         {
     *             "provinceId":"110000",
     *             "list":[
     *                 {
     *                     "cityId":"110100",
     *                     "list":[
     *                         {
     *                             "countyId":"110108"
     *                         }
     *                     ]
     *                 }
     *             ]
     *         }
     *     ]
     * }
     */
    @PostMapping("/video/getTenantMonitorLevel")
    public Result<List<DeviceTreeVO>> getTenantMonitorLevel(@RequestBody DeviceReqDTO deviceReqDTO) {
        return iVideoDeviceServiceMigrate.getTenantMonitorLevel(deviceReqDTO);
    }

    /**
     * 根据用户编号查询用户订购的某个平台下的设备编号【应该不用了删了吧】
     * @author chenmf
     * @date 2022/1/27
     * @param pageDeviceVO
     * @return 设备编号列表
     */
    @ApiOperation("根据平台编码查询设备信息")
    @PostMapping(value = "/video/pageDeviceByPlatform")
    public TableDataInfo pageDeviceByPlatform(@RequestBody PageDeviceVO pageDeviceVO) {
        return iVideoDeviceServiceMigrate.pageDeviceByPlatform(pageDeviceVO);
    }

    /**
     * 获取标签设备树
     * @apiNote 查询设备标签树
     * @apiVersion 3.85
     * @tag 三、设备能力
     * @requestDemo {"isMonitorFlag":"1"}
     * @responseDemo
     * 返回参数data下字段解析：
     * 		code：标签编码
     * 		name：标签名称
     * 		level：级别
     * 		num：通道数量
     * 		onlineNum：通道在线数量
     * 		longitude：经度
     * 		latitude：纬度
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		channelCode：通道编码
     * 		channelName：通道名称
     * 		status：通道状态:0在线，1离线
     *
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1692255723533,
     *     "data":[
     *         {
     *             "code":"999999",
     *             "name":"全部标签",
     *             "num":5,
     *             "onlineNum":5,
     *             "list":[
     *                 {
     *                     "code":"67",
     *                     "name":"测试",
     *                     "level":"1",
     *                     "num":3,
     *                     "onlineNum":3,
     *                     "list":[
     *                         {
     *                             "code":"993",
     *                             "name":"测试标签",
     *                             "level":"2",
     *                             "num":2,
     *                             "onlineNum":2,
     *                             "list":[
     *                                 {
     *                                     "deviceCode":"21021100831327000028",
     *                                     "deviceName":"周水子派出所",
     *                                     "code":"21021100831327000028",
     *                                     "name":"周水子派出所",
     *                                     "level":"3",
     *                                     "num":1,
     *                                     "onlineNum":1,
     *                                     "list":[
     *                                         {
     *                                             "deviceCode":"21021100831327000028",
     *                                             "deviceName":"周水子派出所",
     *                                             "channelCode":"21021100001310000011",
     *                                             "channelName":"周水子派出所_0",
     *                                             "status":"0",
     *                                             "code":"21021100001310000011",
     *                                             "name":"周水子派出所_0",
     *                                             "level":"4",
     *                                             "longitude":"121.566715126",
     *                                             "latitude":"38.956039054",
     *                                             "checked":false,
     *                                             "isMonitor":"0",
     *                                             "isMonitorNum":0,
     *                                             "facade":"1"
     *                                         }
     *                                     ],
     *                                     "isMonitor":"0",
     *                                     "isMonitorNum":0,
     *                                     "categoryCode":"1"
     *                                 }
     *                             ],
     *                             "labelId":993,
     *                             "labelName":"测试标签",
     *                             "isMonitor":"0",
     *                             "isMonitorNum":0
     *                         }
     *                     ],
     *                     "labelTypeId":67,
     *                     "labelTypeName":"测试",
     *                     "isMonitor":"0",
     *                     "isMonitorNum":0
     *                 }
     *             ],
     *             "first":1,
     *             "isMonitor":"0",
     *             "isMonitorNum":0
     *         }
     *     ]
     * }
     * @param deviceReqDTO
     * @return
     */
    @ApiOperation(value = "获取标签设备树",notes = "获取标签设备树")
    @PostMapping("/video/getMonitorLabelTree")
     public Result<List<DeviceTreeVO>> getMonitorLabelTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        deviceReqDTO.setQueryType(3);
        return iVideoDeviceServiceMigrate.getMonitorTree(deviceReqDTO);
    }

    /**
     * 查询设备标签树NEW-增加标签类型层级
     * @apiNote 查询设备标签树NEW-增加标签类型层级
     * @apiVersion 3.86
     * @tag 三、设备能力
     * @requestDemo {"isMonitorFlag":"1"}
     * @responseDemo
     * 返回参数data下字段解析：
     * 		code：标签编码
     * 		name：标签名称
     * 		level：级别
     * 		num：通道数量
     * 		onlineNum：通道在线数量
     * 		longitude：经度
     * 		latitude：纬度
     * 		deviceCode：设备编码
     * 		deviceName：设备名称
     * 		channelCode：通道编码
     * 		channelName：通道名称
     * 		status：通道状态:0在线，1离线
     *
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1692255723533,
     *     "data":[
     *         {
     *             "code":"999999",
     *             "name":"全部标签",
     *             "num":5,
     *             "onlineNum":5,
     *             "list":[
     *                 {
     *                     "code":"67",
     *                     "name":"测试",
     *                     "level":"1",
     *                     "num":3,
     *                     "onlineNum":3,
     *                     "list":[
     *                         {
     *                             "code":"993",
     *                             "name":"测试标签",
     *                             "level":"2",
     *                             "num":2,
     *                             "onlineNum":2,
     *                             "list":[
     *                                 {
     *                                     "deviceCode":"21021100831327000028",
     *                                     "deviceName":"周水子派出所",
     *                                     "code":"21021100831327000028",
     *                                     "name":"周水子派出所",
     *                                     "level":"3",
     *                                     "num":1,
     *                                     "onlineNum":1,
     *                                     "list":[
     *                                         {
     *                                             "deviceCode":"21021100831327000028",
     *                                             "deviceName":"周水子派出所",
     *                                             "channelCode":"21021100001310000011",
     *                                             "channelName":"周水子派出所_0",
     *                                             "status":"0",
     *                                             "code":"21021100001310000011",
     *                                             "name":"周水子派出所_0",
     *                                             "level":"4",
     *                                             "longitude":"121.566715126",
     *                                             "latitude":"38.956039054",
     *                                             "checked":false,
     *                                             "isMonitor":"0",
     *                                             "isMonitorNum":0,
     *                                             "facade":"1"
     *                                         }
     *                                     ],
     *                                     "isMonitor":"0",
     *                                     "isMonitorNum":0,
     *                                     "categoryCode":"1"
     *                                 }
     *                             ],
     *                             "labelId":993,
     *                             "labelName":"测试标签",
     *                             "isMonitor":"0",
     *                             "isMonitorNum":0
     *                         }
     *                     ],
     *                     "labelTypeId":67,
     *                     "labelTypeName":"测试",
     *                     "isMonitor":"0",
     *                     "isMonitorNum":0
     *                 }
     *             ],
     *             "first":1,
     *             "isMonitor":"0",
     *             "isMonitorNum":0
     *         }
     *     ]
     * }
     * @param deviceReqDTO
     * @return
     */
    @ApiOperation(value = "获取标签设备树",notes = "获取标签设备树")
    @PostMapping("/video/getMonitorLabelTreeNew")
     public Result<List<DeviceTreeVO>> getMonitorLabelTreeNew(@RequestBody DeviceReqDTO deviceReqDTO) {
        deviceReqDTO.setQueryType(4);
        return iVideoDeviceServiceMigrate.getMonitorTree(deviceReqDTO);
    }

    /**
     * 更新设备用户数缓存
     * @apiNote 更新设备用户数缓存
     * @apiVersion 3.87
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010800831327000008",
     *     "videoType":"1"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1694677677822
     * }
     */
    @ApiOperation(value = "更新设备用户数缓存",notes = "更新设备用户数缓存")
    @PostMapping("/video/updateDeviceUserNum")
    public Result<String> updateDeviceUserNum(@RequestBody DeviceVideoDTO deviceVideoDTO) {
        return iVideoDeviceServiceMigrate.updateDeviceUserNum(deviceVideoDTO);
    }

    /**
      * 查询无人机设备距离信息
      * @apiNote 查询无人机设备距离信息
      * @apiVersion 3.88
      * @tag 三、设备能力
      * @requestDemo
      * {
      *     "longitude": "116.3699518371145",
      *     "latitude": "39.921861623877284",
      *     "radius": "3000"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *      deviceCode：设备编码
      *      name: 设备名称
      *      location: 设备地址
      *      distance: 距离
      *      longitude: 经度
      *      latitude: 纬度
      *      modelCode: 设备厂家编码
      *      modelName: 设备厂家名称
      *      deviceStatus: 设备状态:0在线，1离线
      *
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
      */
    @Deprecated
    @ApiOperation(value = "查询无人机设备距离信息",notes = "查询无人机设备距离信息")
    @PostMapping("/uav/device/queryUavDeviceDistanceInfo")
    public Result<List<DeviceDistanceVO>> queryUavDeviceDistanceInfo(@RequestBody DeviceDistanceDTO param) {
        return iVideoDeviceServiceMigrate.queryUavDeviceDistanceInfo(param);
    }

    /**
      * 查询喇叭设备距离信息
      * @apiNote 查询喇叭设备距离信息
      * @apiVersion 3.89
      * @tag 三、设备能力
      * @requestDemo
      * {
      *     "longitude": "116.3699518371145",
      *     "latitude": "39.921861623877284",
      *     "radius": "3000"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *      deviceCode：设备编码
      *      name: 设备名称
      *      location: 设备地址
      *      distance: 距离
      *      longitude: 经度
      *      latitude: 纬度
      *      modelCode: 设备厂家编码
      *      modelName: 设备厂家名称
      *      deviceType: 设备类型
      *      deviceTypeName: 设备类型名称
      *      height: 挂高
      *      deviceStatus: 设备状态:0在线，1离线
      *
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
      */
    @ApiOperation(value = "查询喇叭设备距离信息",notes = "查询喇叭设备距离信息")
    @PostMapping("/hornDevice/queryHornDeviceDistanceInfo")
    public Result<List<DeviceDistanceVO>> queryHornDeviceDistanceInfo(@RequestBody DeviceDistanceDTO param){
        param.setQueryType(5);
        return iDeviceCommonService.getDeviceDistanceInfo(param);
    }

    /**
      * 查询传感器时间段数据
      * @apiNote 查询传感器时间段数据
      * @apiVersion 3.90
      * @tag 三、设备能力
      * @requestDemo
      * {
      *   "startDate": "2023-05-24 00:00:00",
      *   "endDate": "2023-05-24 23:59:59",
      *   "deviceCode": "10010151853624"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *       no2: 二氧化氮
      *       monitorTime: 监测时间
      *       o3: 臭氧
      *       pm25: pm2.5
      *       so2: 二氧化硫
      *       pm10: pm10
      *       deviceCode: 设备编码
      *       pressure: 气压
      *       co: 一氧化碳
      *
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
      */
    @ApiOperation(value = "查询传感器时间段数据",notes = "查询传感器时间段数据")
    @PostMapping("/iotdevice/getSensorPeriodData")
    public Result<List<SensorAttrMapVO>> getSensorPeriodData(@RequestBody DeviceReqDTO deviceReqDTO){
        return iVideoDeviceServiceMigrate.getSensorPeriodData(deviceReqDTO);
    }

    /**
      * 查询雷达距离信息
      * @apiNote 查询雷达距离信息
      * @apiVersion 3.91
      * @tag 三、设备能力
      * @requestDemo
      * {
      *     "longitude": "116.3699518371145",
      *     "latitude": "39.921861623877284",
      *     "radius": "3000"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *      deviceCode：设备编码
      *      name: 设备名称
      *      location: 设备地址
      *      distance: 距离
      *      longitude: 经度
      *      latitude: 纬度
      *      wZone: 雷达覆盖范围 单位：米
      *      height: 挂高
      *      modelCode: 设备厂家编码
      *      modelName: 设备厂家名称
      *      deviceStatus: 设备状态:0在线，1离线
      *
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
      */
    @ApiOperation(value = "查询雷达距离信息",notes = "查询喇叭设备距离信息")
    @PostMapping("/radar/queryRadarDevDistanceInfo")
    public Result<List<DeviceDistanceVO>> queryRadarDevDistanceInfo(@RequestBody DeviceDistanceDTO param){
        param.setQueryType(3);
        return iDeviceCommonService.getDeviceDistanceInfo(param);
    }

    /**
      * 查询物联设备距离信息
      * @apiNote  查询物联设备距离信息
      * @apiVersion 3.92
      * @tag 三、设备能力
      * @requestDemo
      * {
      *     "longitude": "116.3699518371145",
      *     "latitude": "39.921861623877284",
      *     "radius": "3000"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *      deviceCode：设备编码
      *      name: 设备名称
      *      location: 设备地址
      *      distance: 距离
      *      longitude: 经度
      *      latitude: 纬度
      *      wZone: 雷达覆盖范围 单位：米
      *      height: 挂高
      *      deviceType: 设备类型
      *      modelCode: 设备厂家编码
      *      deviceTypeName: 设备类型名称
      *      modelName: 设备厂家名称
      *      deviceStatus: 设备状态:0在线，1离线
      *
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
      */
    @ApiOperation(value = "查询物联设备距离信息",notes = "查询喇叭设备距离信息")
    @PostMapping("/iotdevice/queryIotDevDistanceInfo")
    public Result<List<DeviceDistanceVO>> queryIotDevDistanceInfo(@RequestBody DeviceDistanceDTO param){
        param.setQueryType(4);
        return iDeviceCommonService.getDeviceDistanceInfo(param);
    }


    /**
     * 自定义树下拉框查询
     * @apiNote 自定义树下拉框查询
     * @apiVersion 3.93
     * @tag 三、设备能力
     * @requestDemo
     * {
     *  "treeStatus": "0"
     * }
     * @responseDemo
     * treeId：自定义树编码
     * treeName：自定义树名称
     * {
     *  "code": 200,
     *  "msg": "请求成功",
     *  "time": 1705478654421,
     *  "data": [
     *      {
     *      "treeId": "5438e478aa5442be9bdf781be00a2af9",
     *      "treeName": "hued测试1"
     *      }
     *  ]
     * }
     * @param      deviceInfoVO
     * @return
     */
    @PostMapping("/deviceDiy/getList")
    public Result<List<DeviceDiyTreeVO>> getList(@RequestBody DeviceDiyTreeVO deviceInfoVO){
        return iVideoDeviceServiceMigrate.getList(deviceInfoVO);
    }

    /**
     * 自定义树查询
     * @apiNote 自定义树查询
     * @apiVersion 3.94
     * @tag 三、设备能力
     * @requestDemo
     * {
     *  "treeId": "5438e478aa5442be9bdf781be00a2af9",
     *  "isMonitorFlag": "1"
     * }
     * @responseDemo
     * nodeType：节点类型 1文本 2设备 3通道
     * code：节点编码
     * name：节点名称
     * level：节点级别
     * parentCode：节点父级
     * num：通道数量
     * onlineNum：在线通道数量
     * isMonitor：是否收藏 0未收藏 1已收藏
     * isMonitorNum：收藏数量
     * list：子节点
     * deviceCode：摄像机编码
     * deviceName：摄像机名称
     * channelCode：通道编码
     * channelName：通道名称
     * status：通道状态 0在线 1离线
     * deviceStatus：摄像机状态 0在线 1离线
     * deviceAlias：摄像机别名
     * channelAlias：通道别名
     * longitude：经度
     * latitude：纬度
     * categoryCode：0-NVR,1-网络摄像机,2-AR摄像机,3-热成像摄像机,4、人脸卡口摄像机，5-车辆卡口摄像机；7-无人机
     * facade：外型 1-球机；2-半球；3-枪机
     * siteCode：站址编码
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1705472896045,
     *     "data": [
     *         {
     *             "nodeType": "1",
     *             "code": "001",
     *             "name": "辽宁",
     *             "level": "1",
     *             "parentCode": "0",
     *             "num": 4,
     *             "onlineNum": 2,
     *             "isMonitor": "0",
     *             "isMonitorNum": 0,
     *             "list": [
     *                 {
     *                     "nodeType": "1",
     *                     "code": "002",
     *                     "name": "沈阳",
     *                     "level": "2",
     *                     "parentCode": "001",
     *                     "num": 3,
     *                     "onlineNum": 1,
     *                     "isMonitor": "0",
     *                     "isMonitorNum": 0,
     *                     "list": [
     *                         {
     *                             "nodeType": "2",
     *                             "deviceCode": "51150200831327000044",
     *                             "deviceName": "三江新区沙坪街道诚志立交",
     *                             "deviceStatus": "0",
     *                             "deviceAlias": "三江新区沙坪街道诚志立交",
     *                             "code": "005",
     *                             "name": "三江新区沙坪街道诚志立交",
     *                             "level": "3",
     *                             "parentCode": "002",
     *                             "longitude": "104.644439357",
     *                             "latitude": "28.814613429",
     *                             "num": 3,
     *                             "onlineNum": 1,
     *                             "isMonitor": "0",
     *                             "isMonitorNum": 1,
     *                             "categoryCode": "1",
     *                             "siteCode": "1cecd705549f4643897ce087026cd599",
     *                             "list": [
     *                                 {
     *                                     "nodeType": "2",
     *                                     "deviceCode": "11010800831327000003",
     *                                     "deviceName": "中国铁塔产业园-12号楼角落-大华245",
     *                                     "deviceStatus": "1",
     *                                     "deviceAlias": "中国铁塔产业园-12号楼角落-大华245",
     *                                     "code": "006",
     *                                     "name": "中国铁塔产业园-12号楼角落-大华245",
     *                                     "level": "4",
     *                                     "parentCode": "005",
     *                                     "longitude": "116.235924703",
     *                                     "latitude": "28.585300570",
     *                                     "num": 2,
     *                                     "onlineNum": 0,
     *                                     "isMonitor": "1",
     *                                     "isMonitorNum": 2,
     *                                     "categoryCode": "1",
     *                                     "siteCode": "d70de523c992404fbecd5202fa36c197",
     *                                     "list": [
     *                                         {
     *                                             "nodeType": "3",
     *                                             "deviceCode": "11010800831327000003",
     *                                             "deviceName": "中国铁塔产业园-12号楼角落-大华245",
     *                                             "channelCode": "11010800831310000002",
     *                                             "channelName": "铁塔产业园区-12#_1",
     *                                             "status": "1",
     *                                             "deviceStatus": "1",
     *                                             "deviceAlias": "铁塔产业园-12号楼角落-大华245",
     *                                             "channelAlias": "铁塔产业园区-12#_0",
     *                                             "code": "008",
     *                                             "name": "铁塔产业园区-12#_1",
     *                                             "level": "5",
     *                                             "parentCode": "006",
     *                                             "longitude": "116.235924703",
     *                                             "latitude": "28.585300570",
     *                                             "num": 0,
     *                                             "onlineNum": 0,
     *                                             "isMonitor": "1",
     *                                             "isMonitorNum": 0,
     *                                             "categoryCode": "1",
     *                                             "facade": "1",
     *                                             "siteCode": "d70de523c992404fbecd5202fa36c197",
     *                                             "list": [
     *
     *                                             ]
     *                                         },
     *                                         {
     *                                             "nodeType": "3",
     *                                             "deviceCode": "11010800831327000003",
     *                                             "deviceName": "中国铁塔产业园-12号楼角落-大华245",
     *                                             "channelCode": "11010800001310000051",
     *                                             "channelName": "铁塔产业园-12号楼角落-大华245_1",
     *                                             "status": "1",
     *                                             "deviceStatus": "1",
     *                                             "deviceAlias": "铁塔产业园-12号楼角落-大华245",
     *                                             "channelAlias": "铁塔产业园-12号楼角落-大华245_0",
     *                                             "code": "009",
     *                                             "name": "铁塔产业园-12号楼角落-大华245_1",
     *                                             "level": "5",
     *                                             "parentCode": "006",
     *                                             "longitude": "116.235924703",
     *                                             "latitude": "28.585300570",
     *                                             "num": 0,
     *                                             "onlineNum": 0,
     *                                             "isMonitor": "1",
     *                                             "isMonitorNum": 0,
     *                                             "categoryCode": "1",
     *                                             "siteCode": "d70de523c992404fbecd5202fa36c197",
     *                                             "list": [
     *
     *                                             ]
     *                                         }
     *                                     ]
     *                                 },
     *                                 {
     *                                     "nodeType": "3",
     *                                     "deviceCode": "51150200831327000044",
     *                                     "deviceName": "三江新区沙坪街道诚志立交",
     *                                     "channelCode": "51150200001310000001",
     *                                     "channelName": "三江新区沙坪街道诚志立交_0",
     *                                     "status": "0",
     *                                     "deviceStatus": "0",
     *                                     "deviceAlias": "三江新区沙坪街道诚志立交",
     *                                     "channelAlias": "三江新区沙坪街道诚志立交_0",
     *                                     "code": "007",
     *                                     "name": "三江新区沙坪街道诚志立交_0",
     *                                     "level": "4",
     *                                     "parentCode": "005",
     *                                     "longitude": "104.644439357",
     *                                     "latitude": "28.814613429",
     *                                     "num": 0,
     *                                     "onlineNum": 0,
     *                                     "isMonitor": "0",
     *                                     "isMonitorNum": 0,
     *                                     "categoryCode": "1",
     *                                     "facade": "1",
     *                                     "siteCode": "1cecd705549f4643897ce087026cd599",
     *                                     "list": [
     *
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
     * @param      deviceInfoVO
     * @return
     */
    @PostMapping("/deviceDiy/getDiyTree")
    public Result<List<DeviceDiyTreeResultVO>> getDiyTree(@RequestBody DeviceDiyTreeResultVO deviceInfoVO){
        return iVideoDeviceServiceMigrate.getDiyTree(deviceInfoVO);
    }

    @PostMapping("/video/device/getDeviceListByCodes")
    public Result<List<DeviceIndDTO>> getDeviceListByCodes(@RequestBody List<String> deviceCodes){
        return iVideoDeviceServiceMigrate.getDeviceListByCodes(deviceCodes);
    }

    @PostMapping("/video/device/getDeviceTreeForestry")
    public Result<List<TreeSelect>> getDeviceTreeForestry(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO){
        return iVideoDeviceServiceMigrate.getDeviceTreeForestry(woodProcessingPlantDeivceVO);
    }

    @PostMapping("/video/device/getWoodProcessingPlantDeivceInfo")
    public Result<List<WoodProcessingPlantDeivceVO>> getWoodProcessingPlantDeivceInfo(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO){
        return iVideoDeviceServiceMigrate.getWoodProcessingPlantDeivceInfo(woodProcessingPlantDeivceVO);
    }

    @PostMapping("/video/device/getWoodProcessingPlantDeivceInfoPage")
    public Result<PageInfo<WoodProcessingPlantDeivceVO>> getWoodProcessingPlantDeivceInfoPage(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO){
        return iVideoDeviceServiceMigrate.getWoodProcessingPlantDeivceInfoPage(woodProcessingPlantDeivceVO);
    }

    @PostMapping("/video/device/getAlarmNumByDevice")
    public Result<List<WoodProcessingPlantDeivceVO>> getAlarmNumByDevice(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO){
        return iVideoDeviceServiceMigrate.getAlarmNumByDevice(woodProcessingPlantDeivceVO);
    }

}
