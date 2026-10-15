package com.chinatower.outdata.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.outdata.api.entity.*;
import com.chinatower.outdata.api.factory.OutdataFallbackFactory;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "OutdataAPI",
        value = ServiceNameConstants.OUTDATA_SERVICE,
        fallbackFactory = OutdataFallbackFactory.class)
public interface OutdataAPI {

    @PostMapping("/outdata/query/queryStatus")
    Result queryStatus(@RequestParam String deviceCodes);


    @PostMapping("/outdata/query/queryAttribute")
    Result<List<Monitor>> queryAttribute(@RequestParam String deviceCodes);

    @PostMapping("/outdata/query/queryAttributeMap")
    Result<List<Map<String, Object>>> queryAttributeMap(@RequestParam String deviceCodes);

    /**
     * 查询传感器设备信息
     * @apiNote 查询传感器设备信息 详细信息
     * @apiVersion 3.36
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "siteCodes":"123",
     *     "siteCodeList":["123","123"],
     *     "provinceId":"111",
     *     "cityId":"111",
     *     "countyId":"111"，
     *     "deviceCode":"111"，
     *     "deviceName":"111"，
     *     "status":"111",
     *     "appVerCode":"11",
     *     "userId":"11",
     *     "industryCode":"22"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     siteCode 站址编码
     *     deviceCode 设备编码
     *     status 设备状态 1:在线 2:离线
     *     lat 纬度
     *     lon 经度
     *     deviceName 设备名称
     *     provinceId 省id
     *     provinceName 省名称
     *     cityId 市id
     *     cityName 市名称
     *     countyId 区县id
     *     countyName 区县名称
     *     location 设备位置
     *     modelCode 设备厂家code
     *     deviceModelCode 物联设备型号编码
     *     deviceType 设备类型
     *     deptInfoList 设备所属部门列表
     *       deviceCode 设备编码
     *       deptId 部门id
     *       deptName 部门名称
     *     isCollect 是否收藏 0-为收藏 1-已收藏
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1736825485555,
     *     "data": [
     *         {
     *             "siteCode": "982d3deb780142448276e06d4390331d",
     *             "deviceCode": "yq-shuibiao-001",
     *             "status": "0",
     *             "lat": "39.939674000",
     *             "lon": "116.335767400",
     *             "deviceName": "超声波流量计",
     *             "provinceId": "110000",
     *             "provinceName": "北京市",
     *             "cityId": "110100",
     *             "cityName": "北京市",
     *             "countyId": "110102",
     *             "countyName": "西城区",
     *             "location": "北京市西城区展览路街道动物园路",
     *             "modelCode": "huawei",
     *             "deviceModelCode": "EPLC- M002FH",
     *             "deviceType": "9002",
     *             "deptInfoList": [
     *                 {
     *                     "deviceCode": "yq-shuibiao-001",
     *                     "deptId": "121062646-11",
     *                     "deptName": "河北广电信息网络集团股份有限公司宁晋分公司_备电-通用平台版本-1.0"
     *                 }
     *             ],
     *             "isCollect": 1
     *         }
     *     ]
     * }
     */
    @PostMapping("/outdata/query/queryStatusFake")
    Result<List<MonitorFake>> queryStatusFake(@RequestBody QueryStatusFakeReq queryStatusFakeReq);

    /**
     * 分页查询传感器设备信息
     * @apiNote 分页查询传感器设备信息 详细信息
     * @apiVersion 3.37
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "siteCodes": "74f0023ac6914d88bf89874f1a10a43d",
     *     "pageNum":1,
     *     "pageSize":10
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     siteCode 站址编码
     *     deviceCode 设备编码
     *     status 设备状态 1:在线 2:离线
     *     lat 纬度
     *     lon 经度
     *     location 设备位置
     *     modelCode 设备厂家code
     *     deviceModelCode 物联设备型号编码
     *     deviceName 设备名称
     *     provinceId 省id
     *     provinceName 省名称
     *     cityId 市id
     *     cityName 市名称
     *     countyId 区县id
     *     countyName 区县名称
     *     deviceType 设备类型
     *{
     *     "code": 200,
     *     "data": {
     *         "endRow": 2,
     *         "hasNextPage": false,
     *         "hasPreviousPage": false,
     *         "isFirstPage": true,
     *         "isLastPage": true,
     *         "list": [
     *             {
     *                 "siteCode": "680a4ef8b76d4d9f962401729edb06d1",
     *                 "deviceCode": "10113020300107",
     *                 "status": "0",
     *                 "lat": "34.737409",
     *                 "lon": "113.606636",
     *                 "location": "河南省郑州市中原区绿东村街道陇海西路凯旋门",
     *                 "modelCode": "hikvision",
     *                 "deviceModelCode": "12321qW",
     *                 "deviceName": "GNSS监测站7",
     *                 "provinceId": "410000",
     *                 "provinceName": "河南省",
     *                 "cityId": "410100",
     *                 "cityName": "郑州市",
     *                 "countyId": "410102",
     *                 "countyName": "中原区",
     *                 "deviceType":""
     *             },
     *         ],
     *         "nextPage": 0,
     *         "pageNum": 1,
     *         "pageSize": 10,
     *         "pages": 1,
     *         "prePage": 0,
     *         "size": 2,
     *         "startRow": 1,
     *         "total": 2
     *     },
     *     "msg": "请求成功",
     *     "time": 1650365056575
     * }
     */
    @PostMapping("/outdata/query/queryStatusFakePage")
    Result<PageInfo<MonitorFake>> queryStatusFakePage(@RequestBody QueryStatusFakeReq queryStatusFakeReq);

    /**
     * 传感器数量查询
     * @apiNote 传感器数量查询 详细信息
     * @apiVersion 3.38
     * @tag 三、设备能力
     * @requestDemo
     * {
     *  "areaCode":"123"，
     *  "userId":"",
     *  "industryCode":"",
     *  "appVerCode":""
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     data:设备数量
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1673254838741,
     *     "data": 20
     * }
     */
    @PostMapping("/outdata/query/querySensorCount")
    Result<Integer> querySensorCount(@RequestBody QuerySensorCountReq querySensorCountReq);


    /**
     * 气象信息查询
     */
    @PostMapping("/outdata/ecmwf/getEcmwf")
    Result<EcmwfInfoParam> getEcmwf(@RequestBody EcmwfVO vo);

    /**
     * 雷达联动获取正在接力的设备
     * @apiNote   雷达联动获取正在接力的设备
     * @apiVersion 3.39
     * @tag 三、设备能力
     * @requestDemo
     * {
     * "messageId":"1715196337823289344"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     正在接力的设备编码
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1691738964221,
     *     "data": ["11010800011327000006"]
     * }
     */
    @ApiOperation(value = "雷达联动获取正在接力的设备")
    @PostMapping("/radar/target/queryRelayRadarList")
    Result<RelayDeviceInfo> queryRelayRadarList(@RequestBody RadarDeviceStatusVO dto);

    /**
     * 雷达联动目标回放数据查询
     * @apiNote   雷达联动目标回放数据查询
     * @apiVersion 3.40
     * @tag 三、设备能力
     * @requestDemo
     * {
     * "startTime":"1123232",
     * "endTime":"22222222",
     * "area":"POLYGON((109.10369412876939 19.958157908234128,109.12669675328111 19.96267569289037,109.11330716587875 19.93718078523669,109.10369412876939 19.958157908234128))"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     mmsi: 九位码mmsi号
     *     targetId: 目标ID
     *     course: 目标航向
     *     heading: 目标艏向
     *     speed: 目标速度
     *     longitude: 经度
     *     latitude: 维度
     *     sclass: 目标类型
     *     lastTm: 时间戳 目标的时间戳
     *     sources: 目标来源
     * {
     *     "code":200,
     *     "msg":"success",
     *     "data":[
     *         {
     *             "mmsi":"111",
     *             "targetId":"111",
     *             "course":123,
     *             "heading":123,
     *             "speed":123,
     *             "longitude":12312312,
     *             "latitude":21312312,
     *             "sclass":"1212",
     *             "lastTm":112123,
     *             "sources":[
     *                 "312",
     *                 "213"
     *             ]
     *         },
     *         {
     *             "mmsi":"111",
     *             "targetId":"111",
     *             "course":123,
     *             "heading":123,
     *             "speed":123,
     *             "longitude":12312312,
     *             "latitude":21312312,
     *             "sclass":"1212",
     *             "lastTm":112123,
     *             "sources":[
     *                 "312",
     *                 "213"
     *             ]
     *         }
     *     ]
     * }
     */
    @ApiOperation(value = "目标回放数据查询")
    @PostMapping("/radar/target/queryRadarReviewInfoList")
    Result<List<RadarReviewInfo>> queryRadarReviewInfoList(@RequestBody RadarTrackDTO dto);

    /**
     * 雷达联动目标历史轨迹查询
     * @apiNote   雷达联动目标历史轨迹查询
     * @apiVersion 3.41
     * @tag 三、设备能力
     * @requestDemo
     * {
     *   "startTime":"213213",
     *   "endTime":"231312",
     *   "targetId":"232323"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *   course: 目标航向
     *   heading: 目标艏向
     *   speed: 目标速度
     *   longitude: 经度
     *   latitude: 维度
     *   shipLength: 船舶长度
     *   lastTm: 时间戳 目标的时间戳
     *   vesselName: 船舶名称
     *   sclass: 目标类型
     *   state: 船舶状态
     *   shipLengthMax: 船舶的最大长度
     *
     * {
     *   "code":200,
     *   "msg":"success",
     *   "data":[
     *     {
     *       "mmsi":413871253,
     *        "targetId":"3471436858029252608",
     *        "points":[
     *               {
     *                    "course":183.2,
     *                    "heading":183.2,
     *                    "speed":7.4,
     *                    "longitude":112.92510551,
     *                    "latitude":29.70568032,
     *                    "shipLength":110,
     *                    "lastTm":1679366519513,
     *                    "vesselName":"MIN SHENG HAO",
     *                    "sclass":"AIS_B",
     *                    "state":"1",
     *                    "shipLengthMax":1231
     *                 }
     *             ]
     *        }
     *    ]
     * }
     */
    @ApiOperation(value = "目标历史轨迹查询")
    @PostMapping("/radar/target/queryRadarTrackInfoList")
    Result<List<RadarTrackInfo>> queryRadarTrackInfoList(@RequestBody RadarTrackDTO dto);

    /**
     * 摄像机接入节点信息查询
     * @apiNote   摄像机接入节点信息查询
     * @apiVersion 3.42
     * @tag 三、设备能力
     * @requestDemo {"deviceCodeList":["123","124","125"]}
     * @responseDemo
     * 返回参数data下字段解析：
     *     deviceCode: 设备编码
     *     accessNode: 接入节点编码
     * {
     *     "code":200,
     *     "msg":"success",
     *     "data":[
     *         {
     *             "deviceCode":"123",
     *             "accessNode":"1231"
     *         },
     *         {
     *             "deviceCode":"124",
     *             "accessNode":"1241"
     *         }
     *     ]
     * }
     */
    @ApiOperation(value = "摄像机接入节点信息查询")
    @PostMapping("/radar/target/queryDeviceAccessNodeInfoList")
    Result<List<DeviceAccessNodeVO>> queryDeviceAccessNodeInfoList(@RequestBody DeviceAccessNodeDTO dto);

    /**
     * 雷达告警目标信息查询
     * @apiNote   雷达告警目标信息查询
     * @apiVersion 3.43
     * @tag 三、设备能力
     * @responseDemo
     * 返回参数data下字段解析：
     *     alarmTime: 雷达告警时间
     *     alarmCode: 雷达告警编码
     *     taskId: 任务id
     *     targetId: 目标id
     *     tenantId: 租户id
     * {
     *     "code":200,
     *     "msg":"success",
     *     "data":[
     *         {
     *             "alarmTime":"2023-05-27 01:17:15",
     *             "alarmCode":"faad558792914bb5aadc9029d65a04a8b902936cb1e34b199be05aecce2586a6",
     *             "taskId":"16851152173976idzr",
     *             "targetId":"3520432590662860801",
     *             "tenantId":"120046990"
     *         }
     *     ]
     * }
     */
    @ApiOperation(value = "雷达告警目标信息查询")
    @GetMapping("/radar/target/queryWarningTargetInfoList")
    Result<List<WarningTargetVO>> queryWarningTargetInfoList();

    /**
     * 雷达-AIS目标搜索
     * @apiNote 雷达-AIS目标搜索
     * @apiVersion 3.44
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "keyword":"4444"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     mmsi: 船舶MMSI 船舶9位码
     *     vesselName: 船名: AIS船名
     *     shipName: 船名
     *     terminal: 终端
     *     callSign: 呼号: AIS呼号
     *     imo: IMO编号
     *     dataType: 数据类型
     *     isOnline: 是否在线
     * {
     *     "code":0,
     *     "msg":"success",
     *     "data":[
     *         {
     *             "mmsi":224444000,
     *             "vesselName":null,
     *             "shipName":"克努森",
     *             "terminal":null,
     *             "callSign":"EARD ",
     *             "imo":"9721724",
     *             "dataType":"AIS",
     *             "isOnline":1
     *         },
     *         {
     *             "mmsi":140604444,
     *             "vesselName":null,
     *             "shipName":null,
     *             "terminal":null,
     *             "callSign":null,
     *             "imo":null,
     *             "dataType":"AIS",
     *             "isOnline":2
     *         }
     *     ]
     * }
     */
    @ApiOperation(value = "雷达-AIS目标搜索")
    @PostMapping("/external/target/search")
    Result<List<RadarReviewInfo>> queryAisSearch(@RequestBody RadarTrackDTO dto);

    /**
     * 雷达-目标信息查询
     * @apiNote 雷达-目标信息查询
     * @apiVersion 3.45
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "mmsi":"412334344",
     *     "id":"123213"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     course: 目标航向: 单位为°
     *     displacement: 移动距离
     *     displayId: 短编号
     *     duration: 持续时长
     *     heading: 目标艏向：单位为°
     *     id: 目标ID：目标唯一id
     *     idLink: 接续之前的id
     *     lastTm: 时间戳：目标的时间戳
     *     latitude: 纬度：单位为度
     *     len: 目标长度：单位为米
     *     longitude: 经度：单位为度
     *     maxLen: 最大长度：单位为米
     *     minLen: 最小长度：单位为米
     *     period: 更新周期：单位为秒
     *     province: 省份：雷达站点所属省份
     *     quality: 数据质量：可不用
     *     receiveTime: 接收时间：同一批次数据接收时间
     *     s_class: 目标类型
     *     provider: 数据提供方
     *     ids: ID列表
     *     type: 目标类别
     *     speed: 目标速度：单位为节
     *     state: 目标状态：1代表正常 2代表预测 3代表丢失
     *     status: 数据状态：新增、删除和更新
     * {
     *     "code":0,
     *     "msg":"success",
     *     "data":{
     *         "course":298,
     *         "displacement":13,
     *         "displayId":27385,
     *         "duration":464829,
     *         "heading":298,
     *         "id":"3648267705351938048",
     *         "idLink":"3648267705351938048",
     *         "lastTm":1699949924533,
     *         "latitude":29.14849447,
     *         "len":3,
     *         "longitude":112.25203527,
     *         "maxLen":4,
     *         "minLen":0,
     *         "period":1,
     *         "province":"JiangXi",
     *         "quality":100,
     *         "receiveTime":1699949927976,
     *         "s_class":"RADAR",
     *         "sources":[
     *             {
     *                 "provider":"YJ",
     *                 "ids":[
     *                     "390"
     *                 ],
     *                 "type":"RADAR"
     *             }
     *         ],
     *         "speed":0,
     *         "state":1,
     *         "status":"UPDATE"
     *     }
     * }
     */
    @ApiOperation(value = "雷达-目标信息查询")
    @PostMapping("/external/target/getDetails")
    Result<AisDetailsInfo> queryAisDetails(@RequestBody RadarTrackDTO dto);

    /**
     * 雷达-轨迹查询
     * @apiNote 雷达-轨迹查询
     * @apiVersion 3.46
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "startDate":"2023-10-01 10:00:41",
     *     "endDate":"2023-11-30 10:00:41",
     *     "targetId":"3617041102430814228",
     *     "mmsi":412334344
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     trackLine: 轨迹线
     *     id: 目标唯一标识ID
     *     lastTm: 最后更新时间戳，单位为毫秒
     *     speed: 目标速度，单位为节
     *     longitude: 目标经度，单位为度
     *     latitude: 目标纬度，单位为度
     *     mmsi: 船舶MMSI码，用于唯一标识船舶
     *     len: 目标长度，单位为米（可能为空）
     *     course: 目标航向，单位为度
     *     lastDt: 最后更新时间
     *     staticState: 静态状态
     *     terminal: 终端
     *     sclass: 目标类别
     *     durationMinutes: 持续时间，单位为分钟
     * {
     *     "code":0,
     *     "msg":"success",
     *     "data":[
     *         {
     *             "trackLine":"LINESTRING (108.6516101 19.4300872, 108.6516101 19.4300872, 108.6516101 19.4300872)",
     *             "pointInfo":{
     *                 "id":"7521481460000451623",
     *                 "lastTm":1698804253467,
     *                 "speed":7,
     *                 "longitude":108.6516101,
     *                 "latitude":19.4300872,
     *                 "mmsi":140604444,
     *                 "len":null,
     *                 "course":260.4,
     *                 "lastDt":"2023-11-01 10:04:13",
     *                 "staticState":"4",
     *                 "terminal":null,
     *                 "sclass":"AIS_B"
     *             },
     *             "pointFirstInfo":{
     *                 "id":"7521481460000451623",
     *                 "lastTm":1698804127934,
     *                 "speed":7,
     *                 "longitude":108.6516101,
     *                 "latitude":19.4300872,
     *                 "mmsi":140604444,
     *                 "len":null,
     *                 "course":260.4,
     *                 "lastDt":"2023-11-01 10:02:07",
     *                 "staticState":"0",
     *                 "terminal":null,
     *                 "sclass":"AIS_B"
     *             },
     *             "sailStatePointList":[
     *                 {
     *                     "id":"7521481460000451623",
     *                     "lastTm":1698804127934,
     *                     "speed":7,
     *                     "longitude":108.6516101,
     *                     "latitude":19.4300872,
     *                     "mmsi":140604444,
     *                     "len":null,
     *                     "course":260.4,
     *                     "lastDt":"2023-11-01 10:02:07",
     *                     "staticState":"0",
     *                     "terminal":null,
     *                     "durationMinutes":0,
     *                     "sclass":"AIS_B"
     *                 },
     *                 {
     *                     "id":"7521481460000451623",
     *                     "lastTm":1698804253467,
     *                     "speed":7,
     *                     "longitude":108.6516101,
     *                     "latitude":19.4300872,
     *                     "mmsi":140604444,
     *                     "len":null,
     *                     "course":260.4,
     *                     "lastDt":"2023-11-01 10:04:13",
     *                     "staticState":"4",
     *                     "terminal":null,
     *                     "durationMinutes":0,
     *                     "sclass":"AIS_B"
     *                 }
     *             ],
     *             "spicePointList":[
     *
     *             ]
     *         }
     *     ]
     * }
     */
    @ApiOperation(value = "雷达-轨迹查询")
    @PostMapping("/external/trajectory/track")
    Result<List<TrackAllInfo>> queryTrajectoryTrack(@RequestBody RadarTrackDTO dto);

    /**
     * 雷达-轨迹点信息
     * @apiNote 雷达-轨迹点信息
     * @apiVersion 3.47
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "startDate":"2023-11-28 10:00:41",
     *     "endDate":"2023-11-28 18:11:41",
     *     "zoom":12,
     *     "targetId":"3658716922696441856",
     *     "mmsi":666666660
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     id: 目标唯一标识ID
     *     lastTm: 最后更新时间戳，单位为毫秒
     *     speed: 目标速度，单位为节
     *     longitude: 目标的经度，单位为度
     *     latitude: 目标的纬度，单位为度
     *     mmsi: 船舶的MMSI码，用于唯一标识船舶
     *     len: 目标的长度，单位为米
     *     course: 目标的航向，单位为度
     *     lastDt: 最后更新时间
     *     staticState: 静态状态
     *     terminal: 终端
     *     sclass: 目标类别
     * {
     *     "code":0,
     *     "msg":"success",
     *     "data":{
     *         "trackPoints":[
     *             {
     *                 "id":"7527961360010986630",
     *                 "lastTm":1699531072258,
     *                 "speed":9.9,
     *                 "longitude":109.80391678,
     *                 "latitude":20.21921596,
     *                 "mmsi":413273910,
     *                 "len":65,
     *                 "course":295.4,
     *                 "lastDt":"2023-11-09 19:57:52",
     *                 "staticState":"1",
     *                 "terminal":null,
     *                 "sclass":"RADAR_AIS_A"
     *             },
     *             {
     *                 "id":"7527961360010986630",
     *                 "lastTm":1699534063079,
     *                 "speed":9.5,
     *                 "longitude":109.67476308,
     *                 "latitude":20.27837617,
     *                 "mmsi":413273910,
     *                 "len":65,
     *                 "course":300,
     *                 "lastDt":"2023-11-09 20:47:43",
     *                 "staticState":"1",
     *                 "terminal":null,
     *                 "sclass":"RADAR_AIS_A"
     *             }
     *         ]
     *     }
     * }
     */
    @ApiOperation(value = "雷达-轨迹点信息")
    @PostMapping("/external/trajectory/trackPoint")
    Result<TrackAllInfo> queryTrajectoryTrackPoint(@RequestBody RadarTrackDTO dto);

    /**
     * 雷达-轨迹分析
     * @apiNote 雷达-轨迹分析
     * @apiVersion 3.48
     * @tag 三、设备能力
     * @requestDemo
     * [
     *     {
     *         "startDate":"2023-10-01 10:00:41",
     *         "endDate":"2023-11-01 10:00:41",
     *         "targetId":"3617041102430814228",
     *         "mmsi":412334344
     *     }
     * ]
     * @responseDemo
     * 返回参数data下字段解析：
     *     id: 目标唯一标识ID
     *     lastTm: 最后更新时间戳，单位为毫秒
     *     speed: 目标速度，单位为节
     *     longitude: 目标的经度，单位为度
     *     latitude: 目标的纬度，单位为度
     *     mmsi: 船舶的MMSI码，用于唯一标识船舶
     *     len: 目标的长度，单位为米
     *     course: 目标的航向，单位为度
     *     lastDt: 最后更新时间
     *     staticState: 静态状态
     *     terminal: 终端信息
     *     sclass: 目标类别
     *     averageLen: 平均长度，单位为米
     *     averageCourse: 平均航向，单位为度
     *     averageSpeed: 平均速度，单位为节
     *     totalTime: 总时间，单位为毫秒
     *     totalDistance: 总距离，单位为千米
     * {
     *     "code":0,
     *     "msg":"success",
     *     "data":[
     *         {
     *             "trackPoints":[
     *                 {
     *                     "id":"7527451910002242590",
     *                     "lastTm":1699401246817,
     *                     "speed":0,
     *                     "longitude":109.05114167,
     *                     "latitude":19.821505,
     *                     "mmsi":40674444,
     *                     "len":null,
     *                     "course":49.8,
     *                     "lastDt":"2023-11-08 07:54:06",
     *                     "staticState":"0",
     *                     "terminal":null,
     *                     "sclass":"AIS_B"
     *                 },
     *                 {
     *                     "id":"7527451910002242590",
     *                     "lastTm":1699401782694,
     *                     "speed":0,
     *                     "longitude":109.05114167,
     *                     "latitude":19.821505,
     *                     "mmsi":40674444,
     *                     "len":null,
     *                     "course":49.8,
     *                     "lastDt":"2023-11-08 08:03:02",
     *                     "staticState":"2",
     *                     "terminal":null,
     *                     "sclass":"AIS_B"
     *                 }
     *             ],
     *             "averageLen":0,
     *             "averageCourse":202.90509033203125,
     *             "averageSpeed":0.8986957669258118,
     *             "totalTime":51391065,
     *             "totalDistance":10.63830381850702
     *         }
     *     ]
     * }
     */
    @ApiOperation(value = "雷达-轨迹分析")
    @PostMapping("/external/trajectory/trackPointChartQuery")
    Result<List<TrackAllInfo>> trackPointChartQuery(@RequestBody List<RadarTrackDTO> dtoList);

    /**
     * 雷达-热力图
     * @apiNote 雷达-热力图
     * @apiVersion 3.49
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "mmsi":412334344,
     *     "startDate":"2023-10-01",
     *     "endDate":"2023-11-01"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     mmsi: 船舶的MMSI码
     *     day: 记录的时间
     *     geoHashStr: 地理哈希字符串
     *     cnt: 计数器
     *     longitude: 经度
     *     latitude: 维度
     * {
     *     "code": 0,
     *     "msg": "success",
     *     "data":  [
     *             {
     *                 "mmsi": 413801784,
     *                 "day": "2023-11-12T16:00:00.000+0000",
     *                 "geoHashStr": "wt68gz",
     *                 "cnt": 2,
     *                 "longitude": 116.18535667,
     *                 "latitude": 29.70218
     *             },
     *             {
     *                 "mmsi": 413801784,
     *                 "day": "2023-11-12T16:00:00.000+0000",
     *                 "geoHashStr": "wt69k8",
     *                 "cnt": 2,
     *                 "longitude": 116.22258667,
     *                 "latitude": 29.75492333
     *             },
     *             {
     *                 "mmsi": 413801784,
     *                 "day": "2023-11-12T16:00:00.000+0000",
     *                 "geoHashStr": "wt69qp",
     *                 "cnt": 2,
     *                 "longitude": 116.28355833,
     *                 "latitude": 29.79376
     *             }
     *         ]
     * }
     */
    @ApiOperation(value = "雷达-热力图")
    @PostMapping("/external/trajectory/track/aisDaily/thermalMap")
    Result<List<ThermalMapInfo>> queryTrajectoryThermalMap(@RequestBody RadarTrackDTO dto);

    /**
     * 获取固定目标列表
     *
     * @param dto 包含查询条件的固定目标数据传输对象
     * @return 包含固定目标记录的结果对象
     */
    @ApiOperation(value = "获取固定目标列表")
    @PostMapping("/external/target/fixedTarget")
    Result<FixedTargetRecord> queryFixedTarget(@RequestBody FixedTargetDTO dto);

    /**
     * 修改固定目标状态
     *
     * @param dto 包含固定目标更新信息的数据传输对象
     * @return 返回一个包含更新结果的Result对象，其中数据部分为更新成功的固定目标ID列表
     */
    @ApiOperation(value = "修改固定目标状态")
    @PostMapping("/external/target/updateFixedTarget")
    Result<UpdateFixedTargetIdList> updateFixedTarget(@RequestBody FixedTargetDTO dto);

    /**
     * 获取全局轨迹
     *
     * @param dto 包含获取全局轨迹所需参数的数据传输对象
     * @return 返回一个包含全局轨迹信息的结果对象
     */
    @ApiOperation(value = "获取全局轨迹")
    @PostMapping("/external/trajectory/globalTrack")
    Result<GlobalTrackTrackMap> globalTrack(@RequestBody GlobalTrackDTO dto);

    /**
     * 获取海图瓦片接口
     *
     * @param node     节点信息
     * @param userName 用户名
     * @param level    瓦片级别
     * @param col      列号
     * @param row      行号
     * @return 返回包含海图瓦片数据的结果对象
     */
    @ApiOperation(value = "获取海图瓦片接口")
    @GetMapping("/radar/target/chart/fishArea/getTile")
    Result<byte[]> getTile(@RequestParam("node") String node,
                           @RequestParam("userName")String userName,
                           @RequestParam("level")Integer level,
                           @RequestParam("col")Integer col,
                           @RequestParam("row")Integer row,
                           @RequestParam("skipCode")String skipCode);

    /**
     * 获取渔区瓦片接口
     *
     * @param tileType 瓦片类型
     * @param userName 用户名
     * @param level    瓦片层级
     * @param col      列号
     * @param row      行号
     * @return 返回包含渔区瓦片数据的结果对象
     */
    @ApiOperation(value = "获取渔区瓦片接口")
    @GetMapping("/radar/target/chart/fishArea/getExtendTile")
    Result<byte[]> getExtendTile(@RequestParam("tileType") String tileType,
                                 @RequestParam("userName")String userName,
                                 @RequestParam("level")Integer level,
                                 @RequestParam("col")Integer col,
                                 @RequestParam("row")Integer row);
}
