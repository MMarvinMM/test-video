package com.chinatower.device.api;

import com.chinatower.common.entity.Result;
import com.chinatower.device.api.deprecated.IReorderDeviceServiceMigrate;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.DeviceCommonInfoVO;
import com.chinatower.device.entity.vo.DeviceSiteVO;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.*;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
public class IReorderDeviceService {

    @Autowired
    private IReorderDeviceServiceMigrate iReorderDeviceServiceMigrate;

    @Autowired
    private IDeviceCommonService iDeviceCommonService;

    /**
      *  查询执法记录仪设备列表
      * @apiNote 查询执法记录仪设备列表
      * @apiVersion 3.52
      * @tag 三、设备能力
      * @requestDemo
     * {
     *   "deviceCode": "",  //设备编码
     *   "deviceName": "",                  //设备名称
     *   "deviceStatus": "",                 //设备状态
     *   "manufacturerCode": "",       //设备厂家
     *   "modelCode": "",         //设备型号
     *   "orderStatus": "0",                //订购状态 0 有效、 1 失效  2 退订
     *   "deptIds": [],                  //所属组织
     *   "provinceCode":"",
     *   "cityCode":"",
     *   "countyCode":"",
     *   "townCode": ""
     *
     * }
      * @responseDemo
     *返回参数data下字段解析：
     *     deviceCode：设备编码
     *     deviceName：设备名称
     *     deviceStatus：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
     *     deviceStatusName：设备状态名称
     *     location：设备地址
     *     longitude：经度
     *     latitude：纬度
     *     areaCode：管理区域
     *     admCode：行政区域
     *     manufacturerCode：厂家编码
     *     manufacturerName：厂家名称
     *     modelCode：型号编码
     *     accessProtocols：接入协议
     *     accessNodeCode：接入节点
     *     siteCode：站址编码
     *     isCascade：级联标识 0-直联---接入方式
     *     collectType：接入类型:0-国标；1-onvif；2-私有协议----执法记录仪目前只支持国标
     *     accessNode：接入节点
     *     devUserName：设备登录用户名
     *     appModFlag：是否自行修改标识
     *     orgName：设备所属组织名称
     *     orgCode：设备所属组织编码
     *     orgRelId：设备所属组织id
     *     orderStatus：订购状态
     *     orderStatusName：订购状态名称
     *     provinceCode：省份编码
     *     provinceName：省份名称
     *     cityCode：地市编码
     *     cityName：地市名称
     *     countyCode：区县名称
     *     countyName：区县名称
     *     townCode：乡镇名称
     *     townName：乡镇名称
     *     isMonitor：是否收藏字段，0未收藏 1已收藏
     *     distance：范围
     *     orgRels: 设备关联组织
     *         id: 组织主键id
     *         tenantId: 客户编码
     *         deptId: 部门编码
     *         deviceCode：设备编码
     *         deleteFlag: 0-未删除 1-已删除
     *         deviceType：设备类型
     *         industryCode: 行业编码
     *         appVerCode：应用版本编码
     *         deptName： 部门名称
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715307392088,
     *     "data": [
     *         {
     *             "creator": "system",
     *             "createTime": "2024-05-07 11:19:48",
     *             "createBy": null,
     *             "updateBy": null,
     *             "modifier": null,
     *             "updateTime": null,
     *             "deleteFlag": null,
     *             "tenantId": null,
     *             "userId": null,
     *             "appVerCode": null,
     *             "industryCode": null,
     *             "platformCode": null,
     *             "dataScope": null,
     *             "criteriaList": null,
     *             "deviceCode": "11010500991327000888",
     *             "deviceName": "三江新区龙头山下11",
     *             "deviceStatus": 0,
     *             "deviceStatusName": "在线",
     *             "location": "北京市朝阳区七棵树创意园北侧河边",
     *             "longitude": "116.4687380000",
     *             "latitude": "39.9578090000",
     *             "areaCode": "110501",
     *             "admCode": "110105",
     *             "manufacturerCode": null,
     *             "manufacturerName": null,
     *             "modelCode": null,
     *             "accessProtocols": null,
     *             "accessNodeCode": null,
     *             "siteCode": "35689",
     *             "isCascade": null,
     *             "collectType": null,
     *             "accessNode": null,
     *             "devUserName": null,
     *             "appModFlag": null,
     *             "orgName": "测试1,测试,吃个桃桃~好凉凉,一省多市",
     *             "orgCode": null,
     *             "orgRelId": null,
     *             "orderStatus": 0,
     *             "orderStatusName": "有效",
     *             "provinceCode": "110000",
     *             "provinceName": "北京市",
     *             "cityCode": "110100",
     *             "cityName": "北京市",
     *             "countyCode": "110101",
     *             "countyName": "东城区",
     *             "townCode": "110101001000",
     *             "townName": "东华门街道",
     *             "isMonitor": null,
     *             "distance": null,
     *             "orgRels": [
     *                 {
     *                     "createBy": null,
     *                     "createTime": null,
     *                     "updateBy": null,
     *                     "updateTime": null,
     *                     "id": "6656853376329943780",
     *                     "tenantId": "122035797",
     *                     "deptId": "122035797-0203",
     *                     "deviceCode": "11010500991327000888",
     *                     "deleteFlag": null,
     *                     "deviceType": 0,
     *                     "industryCode": "000000",
     *                     "appVerCode": null,
     *                     "deptName": "测试1"
     *                 },
     *                 {
     *                     "createBy": null,
     *                     "createTime": null,
     *                     "updateBy": null,
     *                     "updateTime": null,
     *                     "id": "6656853376329943781",
     *                     "tenantId": "122035797",
     *                     "deptId": "122035797-0202",
     *                     "deviceCode": "11010500991327000888",
     *                     "deleteFlag": null,
     *                     "deviceType": 0,
     *                     "industryCode": "000000",
     *                     "appVerCode": null,
     *                     "deptName": "测试"
     *                 },
     *                 {
     *                     "createBy": null,
     *                     "createTime": null,
     *                     "updateBy": null,
     *                     "updateTime": null,
     *                     "id": "6656853376329943782",
     *                     "tenantId": "122035797",
     *                     "deptId": "122035797-0201",
     *                     "deviceCode": "11010500991327000888",
     *                     "deleteFlag": null,
     *                     "deviceType": 0,
     *                     "industryCode": "000000",
     *                     "appVerCode": null,
     *                     "deptName": "吃个桃桃~好凉凉"
     *                 },
     *                 {
     *                     "createBy": null,
     *                     "createTime": null,
     *                     "updateBy": null,
     *                     "updateTime": null,
     *                     "id": "6656853376329943783",
     *                     "tenantId": "122035797",
     *                     "deptId": "122035797-0204",
     *                     "deviceCode": "11010500991327000888",
     *                     "deleteFlag": null,
     *                     "deviceType": 0,
     *                     "industryCode": "000000",
     *                     "appVerCode": null,
     *                     "deptName": "一省多市"
     *                 }
     *             ],
     *             "fileUrlList": null,
     *             "channelList": [
     *                 {
     *                     "parentDeviceCode": "11010500991327000888",
     *                     "channelName": "三江新区龙头山下_1",
     *                     "channelCode": "18113623664705525888",
     *                     "status": "0"
     *                 }
     *             ]
     *         }
     *     ]
     * }
     */
    @PostMapping("/recorder/device/getList")
    public Result<List<RecorderDeviceDTO>> getList(@RequestBody RecorderDeviceVO recorderDeviceVO){
        return iReorderDeviceServiceMigrate.getList(recorderDeviceVO);
    }

    /**
      * 执法记录仪详情
      * @apiNote 执法记录仪详情
      * @apiVersion 3.53
      * @tag 三、设备能力
      * @requestDemo
     * {
     *     "deviceCode": "11010500991327000888"
     * }
      * @responseDemo
     *返回参数data下字段解析：
     *     deviceCode：设备编码
     *     deviceName：设备名称
     *     deviceStatus：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
     *     deviceStatusName：设备状态名称
     *     location：设备地址
     *     longitude：经度
     *     latitude：纬度
     *     areaCode：管理区域
     *     admCode：行政区域
     *     manufacturerCode：厂家编码
     *     manufacturerName：厂家名称
     *     modelCode：型号编码
     *     accessProtocols：接入协议
     *     accessNodeCode：接入节点
     *     siteCode：站址编码
     *     isCascade：级联标识 0-直联---接入方式
     *     collectType：接入类型:0-国标；1-onvif；2-私有协议----执法记录仪目前只支持国标
     *     accessNode：接入节点
     *     devUserName：设备登录用户名
     *     appModFlag：是否自行修改标识
     *     orgName：设备所属组织名称
     *     orgCode：设备所属组织编码
     *     orgRelId：设备所属组织id
     *     orderStatus：订购状态
     *     orderStatusName：订购状态名称
     *     provinceCode：省份编码
     *     provinceName：省份名称
     *     cityCode：地市编码
     *     cityName：地市名称
     *     countyCode：区县名称
     *     countyName：区县名称
     *     townCode：乡镇名称
     *     townName：乡镇名称
     *     isMonitor：是否收藏字段，0未收藏 1已收藏
     *     distance：范围
     *     orgRels: 设备关联组织
     *         id: 组织主键id
     *         tenantId: 客户编码
     *         deptId: 部门编码
     *         deviceCode：设备编码
     *         deleteFlag: 0-未删除 1-已删除
     *         deviceType：设备类型
     *         industryCode: 行业编码
     *         appVerCode：应用版本编码
     *         deptName： 部门名称
     *     fileUrlList：附件url信息列表
     *     channelList：通道信息列表
     *         parentDeviceCode：设备编码
     *         channelName：通道名称
     *         channelCode：通道编码
     *         status 通道状态 0 在线 1 离线
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715307503706,
     *     "data": {
     *         "creator": "system",
     *         "createTime": "2024-05-07 11:19:48",
     *         "createBy": null,
     *         "updateBy": null,
     *         "modifier": null,
     *         "updateTime": null,
     *         "deleteFlag": null,
     *         "tenantId": null,
     *         "userId": null,
     *         "appVerCode": null,
     *         "industryCode": null,
     *         "platformCode": null,
     *         "dataScope": null,
     *         "criteriaList": null,
     *         "deviceCode": "11010500991327000888",
     *         "deviceName": "三江新区龙头山下11",
     *         "deviceStatus": 0,
     *         "deviceStatusName": null,
     *         "location": "北京市朝阳区七棵树创意园北侧河边",
     *         "longitude": "116.4687380000",
     *         "latitude": "39.9578090000",
     *         "areaCode": "110501",
     *         "admCode": "110105",
     *         "manufacturerCode": null,
     *         "manufacturerName": null,
     *         "modelCode": null,
     *         "accessProtocols": null,
     *         "accessNodeCode": null,
     *         "siteCode": "35689",
     *         "isCascade": null,
     *         "collectType": null,
     *         "accessNode": null,
     *         "devUserName": null,
     *         "appModFlag": null,
     *         "orgName": "测试1,测试,吃个桃桃~好凉凉,一省多市",
     *         "orgCode": "122035797-0203,122035797-0202,122035797-0201,122035797-0204",
     *         "orgRelId": "6656853376329943780,6656853376329943781,6656853376329943782,6656853376329943783",
     *         "orderStatus": 0,
     *         "orderStatusName": "有效",
     *         "provinceCode": "110000",
     *         "provinceName": "北京市",
     *         "cityCode": "110100",
     *         "cityName": "北京市",
     *         "countyCode": "110101",
     *         "countyName": "东城区",
     *         "townCode": "110101001000",
     *         "townName": "东华门街道",
     *         "isMonitor": "0",
     *         "distance": null,
     *         "orgRels": null,
     *         "fileUrlList": [
     *             "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/f95319518b86ae9721d856db6c240874/898e83dad8eda3f3474d71165dcbc414/46b3faec6e894d37b1f478e0767738bb.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1715308103&Signature=K/NBQBrxkmd75aMZ/EClw2rcXvc%3D",
     *             "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/f95319518b86ae9721d856db6c240874/0f2683397328b33a9177e41dda1e41d7/7cd69e5f8aa64c07892f84912ee72f5c.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1715308103&Signature=BXITv0myn3wzaun79MuF2MC0SOE%3D",
     *             "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/f95319518b86ae9721d856db6c240874/0f2683397328b33a9177e41dda1e41d7/7d177425b5324bd48fc7736a98366a72.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1715308103&Signature=atVltJBdIn3dm4dd6I38YCRIKZ0%3D"
     *         ],
     *         "channelList": [
     *             {
     *                 "parentDeviceCode": "11010500991327000888",
     *                 "channelName": "三江新区龙头山下_1",
     *                 "channelCode": "18113623664705525888",
     *                 "status": "0"
     *             }
     *         ]
     *     }
     * }
     */
    @PostMapping("/recorder/device/detail")
    public Result<RecorderDeviceDTO> detail(@RequestBody RecorderDeviceVO recorderDeviceVO){
        DeviceCommonInfoVO deviceInfoVO = new DeviceCommonInfoVO();
        BeanUtils.copyProperties(recorderDeviceVO,deviceInfoVO);
        deviceInfoVO.setQueryType(2);
        DeviceCommonInfoVO deviceInfo = iDeviceCommonService.getDeviceInfo(deviceInfoVO).getData();
        RecorderDeviceDTO recorderDeviceInfo = null;
        if (Objects.nonNull(deviceInfo)) {
            recorderDeviceInfo = new RecorderDeviceDTO();
            BeanUtils.copyProperties(deviceInfo,recorderDeviceInfo);
            recorderDeviceInfo.setOrderStatus(Integer.parseInt(deviceInfo.getOrderStatus()));
        }
        return Result.data(recorderDeviceInfo);
    }

    /**
     *
      * 执法记录仪组织树
      * @apiNote 执法记录仪组织树
      * @apiVersion 3.54
      * @tag 三、设备能力
      * @requestDemo
     * {
     * "isMonitorFlag": "1"   //0 已收藏
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     code：组织编码
     *     isMonitor：该节点下所有设备通道是否都收藏
     *     isMonitorNum：该节点下已收藏的通道数
     *     level：层级
     *     list：子节点
     *          设备层级 根据level判断倒数第二层
     *          code：设备编码
     *          deviceCode：设备编码
     *          deviceName：设备名称
     *          isMonitor：该设备通道是否都收藏
     *          isMonitorNum：该设备下已收藏的设备通道数
     *          level：层级
     *          list：通道信息集合
     *              channelCode：通道编码
     *              channelName：通道名称
     *              code：通道编码
     *              deviceCode：设备编码
     *              deviceName：设备名称
     *              isMonitor：该设备通道是否收藏
     *              isMonitorNum：该设备下已收藏的设备通道数
     *              latitude：纬度
     *              level：层级 通道是最后一个层级
     *              longitude：经度
     *              name：通道名称
     *              num：通道总数
     *              onlineNum：在线通道总数
     *              siteCode：站址编码
     *              status：通道状态 0 在线 1 离线
     *          name：设备名称
     *          num：通道总数
     *          onlineNum：在线通道总数
     *          siteCode：站址编码
     *          status：设备状态 0 在线 1 离线
     *     name：组织名称
     *     num：通道总数
     *     onlineNum：在线通道总数
     *     parentCode：上级组织编码
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715307581806,
     *     "data": [
     *         {
     *             "code": "122035797-02",
     *             "name": "测试客户zbz-通用平台版本-1.0",
     *             "level": "1",
     *             "parentCode": "000000",
     *             "checked": false,
     *             "num": 1,
     *             "onlineNum": 1,
     *             "list": [
     *                 {
     *                     "code": "122035797-0201",
     *                     "name": "吃个桃桃~好凉凉",
     *                     "level": "2",
     *                     "parentCode": "122035797-02",
     *                     "num": 1,
     *                     "onlineNum": 1,
     *                     "list": [
     *                         {
     *                             "deviceCode": "11010500991327000888",
     *                             "deviceName": "三江新区龙头山下11",
     *                             "status": "0",
     *                             "code": "11010500991327000888",
     *                             "name": "三江新区龙头山下11",
     *                             "level": "3",
     *                             "num": 1,
     *                             "onlineNum": 1,
     *                             "list": [
     *                                 {
     *                                     "deviceCode": "11010500991327000888",
     *                                     "deviceName": "三江新区龙头山下11",
     *                                     "channelCode": "18113623664705525888",
     *                                     "channelName": "三江新区龙头山下_1",
     *                                     "status": "0",
     *                                     "code": "18113623664705525888",
     *                                     "name": "三江新区龙头山下_1",
     *                                     "level": "4",
     *                                     "longitude": "116.4687380000",
     *                                     "latitude": "39.9578090000",
     *                                     "checked": false,
     *                                     "num": 0,
     *                                     "onlineNum": 0,
     *                                     "list": [],
     *                                     "isMonitor": "1",
     *                                     "isMonitorNum": 1,
     *                                     "siteCode": "35689",
     *                                     "deviceStatusCount": 0
     *                                 }
     *                             ],
     *                             "isMonitor": "1",
     *                             "isMonitorNum": 1,
     *                             "siteCode": "35689",
     *                             "deviceStatusCount": 0
     *                         }
     *                     ],
     *                     "isMonitor": "1",
     *                     "isMonitorNum": 1,
     *                     "deviceStatusCount": 0
     *                 },
     *                 {
     *                     "code": "122035797-0202",
     *                     "name": "测试",
     *                     "level": "2",
     *                     "parentCode": "122035797-02",
     *                     "num": 1,
     *                     "onlineNum": 1,
     *                     "list": [
     *                         {
     *                             "deviceCode": "11010500991327000888",
     *                             "deviceName": "三江新区龙头山下11",
     *                             "status": "0",
     *                             "code": "11010500991327000888",
     *                             "name": "三江新区龙头山下11",
     *                             "level": "3",
     *                             "num": 1,
     *                             "onlineNum": 1,
     *                             "list": [
     *                                 {
     *                                     "deviceCode": "11010500991327000888",
     *                                     "deviceName": "三江新区龙头山下11",
     *                                     "channelCode": "18113623664705525888",
     *                                     "channelName": "三江新区龙头山下_1",
     *                                     "status": "0",
     *                                     "code": "18113623664705525888",
     *                                     "name": "三江新区龙头山下_1",
     *                                     "level": "4",
     *                                     "longitude": "116.4687380000",
     *                                     "latitude": "39.9578090000",
     *                                     "checked": false,
     *                                     "num": 0,
     *                                     "onlineNum": 0,
     *                                     "list": [],
     *                                     "isMonitor": "1",
     *                                     "isMonitorNum": 1,
     *                                     "siteCode": "35689",
     *                                     "deviceStatusCount": 0
     *                                 }
     *                             ],
     *                             "isMonitor": "1",
     *                             "isMonitorNum": 1,
     *                             "siteCode": "35689",
     *                             "deviceStatusCount": 0
     *                         }
     *                     ],
     *                     "isMonitor": "1",
     *                     "isMonitorNum": 1,
     *                     "deviceStatusCount": 0
     *                 },
     *                 {
     *                     "code": "122035797-0203",
     *                     "name": "测试1",
     *                     "level": "2",
     *                     "parentCode": "122035797-02",
     *                     "num": 1,
     *                     "onlineNum": 1,
     *                     "list": [
     *                         {
     *                             "deviceCode": "11010500991327000888",
     *                             "deviceName": "三江新区龙头山下11",
     *                             "status": "0",
     *                             "code": "11010500991327000888",
     *                             "name": "三江新区龙头山下11",
     *                             "level": "3",
     *                             "num": 1,
     *                             "onlineNum": 1,
     *                             "list": [
     *                                 {
     *                                     "deviceCode": "11010500991327000888",
     *                                     "deviceName": "三江新区龙头山下11",
     *                                     "channelCode": "18113623664705525888",
     *                                     "channelName": "三江新区龙头山下_1",
     *                                     "status": "0",
     *                                     "code": "18113623664705525888",
     *                                     "name": "三江新区龙头山下_1",
     *                                     "level": "4",
     *                                     "longitude": "116.4687380000",
     *                                     "latitude": "39.9578090000",
     *                                     "checked": false,
     *                                     "num": 0,
     *                                     "onlineNum": 0,
     *                                     "list": [],
     *                                     "isMonitor": "1",
     *                                     "isMonitorNum": 1,
     *                                     "siteCode": "35689",
     *                                     "deviceStatusCount": 0
     *                                 }
     *                             ],
     *                             "isMonitor": "1",
     *                             "isMonitorNum": 1,
     *                             "siteCode": "35689",
     *                             "deviceStatusCount": 0
     *                         }
     *                     ],
     *                     "isMonitor": "1",
     *                     "isMonitorNum": 1,
     *                     "deviceStatusCount": 0
     *                 },
     *                 {
     *                     "code": "122035797-0204",
     *                     "name": "一省多市",
     *                     "level": "2",
     *                     "parentCode": "122035797-02",
     *                     "num": 1,
     *                     "onlineNum": 1,
     *                     "list": [
     *                         {
     *                             "deviceCode": "11010500991327000888",
     *                             "deviceName": "三江新区龙头山下11",
     *                             "status": "0",
     *                             "code": "11010500991327000888",
     *                             "name": "三江新区龙头山下11",
     *                             "level": "3",
     *                             "num": 1,
     *                             "onlineNum": 1,
     *                             "list": [
     *                                 {
     *                                     "deviceCode": "11010500991327000888",
     *                                     "deviceName": "三江新区龙头山下11",
     *                                     "channelCode": "18113623664705525888",
     *                                     "channelName": "三江新区龙头山下_1",
     *                                     "status": "0",
     *                                     "code": "18113623664705525888",
     *                                     "name": "三江新区龙头山下_1",
     *                                     "level": "4",
     *                                     "longitude": "116.4687380000",
     *                                     "latitude": "39.9578090000",
     *                                     "checked": false,
     *                                     "num": 0,
     *                                     "onlineNum": 0,
     *                                     "list": [],
     *                                     "isMonitor": "1",
     *                                     "isMonitorNum": 1,
     *                                     "siteCode": "35689",
     *                                     "deviceStatusCount": 0
     *                                 }
     *                             ],
     *                             "isMonitor": "1",
     *                             "isMonitorNum": 1,
     *                             "siteCode": "35689",
     *                             "deviceStatusCount": 0
     *                         }
     *                     ],
     *                     "isMonitor": "1",
     *                     "isMonitorNum": 1,
     *                     "deviceStatusCount": 0
     *                 }
     *             ],
     *             "first": 1,
     *             "isMonitor": "0",
     *             "isMonitorNum": 4,
     *             "deviceStatusCount": 0
     *         }
     *     ]
     * }
     */
    @PostMapping("/recorder/device/getRecorderMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getRecorderMonitorOrgTree(@RequestBody DeviceReqDTO deviceReqDTO){
        deviceReqDTO.setQueryType(2);
        return iDeviceCommonService.getDeviceOrgTree(deviceReqDTO);
    }

    /**
     *
     * 查询执法记录仪站址下设备
     * @apiNote  查询执法记录仪站址下设备
     * @apiVersion 3.55
     * @tag   三、设备能力
     * @requestDemo
     * {
     * "siteCode": "35689", //站址编码
     * "latitude": "39.9578090000",
     * "longitude": "116.4687380000"
     * }
     * @responseDemo
     *返回参数data下字段解析：
     *     code: 设备编码
     *     name: 名称
     *     level: 等级
     *     parentCode: 父级编码
     *     checked: 是否选中
     *     num: 设备数量
     *     onlineNum: 在线设备数量
     *     deviceCode：设备编码
     *     deviceName: 设备名称
     *     status：设备状态 0 在线 1 离线
     *     longitude：经度
     *     latitude：纬度
     *     channelCode: 通道编码
     *     channelName: 通道名称
     *     isMonitor: 是否监控
     *     isMonitorNum: 是否监控数量
     *     location: 设备地址
     *     siteCode: 站址编码
     *     deviceStatusCount: 设备状态数量
     *     first: 标注作为集合的第一条数据
     *{
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715307660784,
     *     "data": [
     *         {
     *             "code": "122035797-02",
     *             "name": "测试客户zbz-通用平台版本-1.0",
     *             "level": "1",
     *             "parentCode": "000000",
     *             "checked": false,
     *             "num": 1,
     *             "onlineNum": 1,
     *             "list": [
     *                 {
     *                     "code": "122035797-0201",
     *                     "name": "吃个桃桃~好凉凉",
     *                     "level": "2",
     *                     "parentCode": "122035797-02",
     *                     "num": 1,
     *                     "onlineNum": 1,
     *                     "list": [
     *                         {
     *                             "deviceCode": "11010500991327000888",
     *                             "deviceName": "三江新区龙头山下11",
     *                             "status": "0",
     *                             "code": "11010500991327000888",
     *                             "name": "三江新区龙头山下11",
     *                             "level": "3",
     *                             "num": 1,
     *                             "onlineNum": 1,
     *                             "list": [
     *                                 {
     *                                     "deviceCode": "11010500991327000888",
     *                                     "deviceName": "三江新区龙头山下11",
     *                                     "channelCode": "18113623664705525888",
     *                                     "channelName": "三江新区龙头山下_1",
     *                                     "status": "0",
     *                                     "code": "18113623664705525888",
     *                                     "name": "三江新区龙头山下_1",
     *                                     "level": "4",
     *                                     "longitude": "116.4687380000",
     *                                     "latitude": "39.9578090000",
     *                                     "checked": false,
     *                                     "num": 0,
     *                                     "onlineNum": 0,
     *                                     "list": [],
     *                                     "isMonitor": "0",
     *                                     "isMonitorNum": 0,
     *                                     "siteCode": "35689",
     *                                     "deviceStatusCount": 0
     *                                 }
     *                             ],
     *                             "isMonitor": "0",
     *                             "isMonitorNum": 0,
     *                             "siteCode": "35689",
     *                             "deviceStatusCount": 0
     *                         }
     *                     ],
     *                     "isMonitor": "0",
     *                     "isMonitorNum": 0,
     *                     "deviceStatusCount": 0
     *                 },
     *                 {
     *                     "code": "122035797-0202",
     *                     "name": "测试",
     *                     "level": "2",
     *                     "parentCode": "122035797-02",
     *                     "num": 1,
     *                     "onlineNum": 1,
     *                     "list": [
     *                         {
     *                             "deviceCode": "11010500991327000888",
     *                             "deviceName": "三江新区龙头山下11",
     *                             "status": "0",
     *                             "code": "11010500991327000888",
     *                             "name": "三江新区龙头山下11",
     *                             "level": "3",
     *                             "num": 1,
     *                             "onlineNum": 1,
     *                             "list": [
     *                                 {
     *                                     "deviceCode": "11010500991327000888",
     *                                     "deviceName": "三江新区龙头山下11",
     *                                     "channelCode": "18113623664705525888",
     *                                     "channelName": "三江新区龙头山下_1",
     *                                     "status": "0",
     *                                     "code": "18113623664705525888",
     *                                     "name": "三江新区龙头山下_1",
     *                                     "level": "4",
     *                                     "longitude": "116.4687380000",
     *                                     "latitude": "39.9578090000",
     *                                     "checked": false,
     *                                     "num": 0,
     *                                     "onlineNum": 0,
     *                                     "list": [],
     *                                     "isMonitor": "0",
     *                                     "isMonitorNum": 0,
     *                                     "siteCode": "35689",
     *                                     "deviceStatusCount": 0
     *                                 }
     *                             ],
     *                             "isMonitor": "0",
     *                             "isMonitorNum": 0,
     *                             "siteCode": "35689",
     *                             "deviceStatusCount": 0
     *                         }
     *                     ],
     *                     "isMonitor": "0",
     *                     "isMonitorNum": 0,
     *                     "deviceStatusCount": 0
     *                 },
     *                 {
     *                     "code": "122035797-0203",
     *                     "name": "测试1",
     *                     "level": "2",
     *                     "parentCode": "122035797-02",
     *                     "num": 1,
     *                     "onlineNum": 1,
     *                     "list": [
     *                         {
     *                             "deviceCode": "11010500991327000888",
     *                             "deviceName": "三江新区龙头山下11",
     *                             "status": "0",
     *                             "code": "11010500991327000888",
     *                             "name": "三江新区龙头山下11",
     *                             "level": "3",
     *                             "num": 1,
     *                             "onlineNum": 1,
     *                             "list": [
     *                                 {
     *                                     "deviceCode": "11010500991327000888",
     *                                     "deviceName": "三江新区龙头山下11",
     *                                     "channelCode": "18113623664705525888",
     *                                     "channelName": "三江新区龙头山下_1",
     *                                     "status": "0",
     *                                     "code": "18113623664705525888",
     *                                     "name": "三江新区龙头山下_1",
     *                                     "level": "4",
     *                                     "longitude": "116.4687380000",
     *                                     "latitude": "39.9578090000",
     *                                     "checked": false,
     *                                     "num": 0,
     *                                     "onlineNum": 0,
     *                                     "list": [],
     *                                     "isMonitor": "0",
     *                                     "isMonitorNum": 0,
     *                                     "siteCode": "35689",
     *                                     "deviceStatusCount": 0
     *                                 }
     *                             ],
     *                             "isMonitor": "0",
     *                             "isMonitorNum": 0,
     *                             "siteCode": "35689",
     *                             "deviceStatusCount": 0
     *                         }
     *                     ],
     *                     "isMonitor": "0",
     *                     "isMonitorNum": 0,
     *                     "deviceStatusCount": 0
     *                 },
     *                 {
     *                     "code": "122035797-0204",
     *                     "name": "一省多市",
     *                     "level": "2",
     *                     "parentCode": "122035797-02",
     *                     "num": 1,
     *                     "onlineNum": 1,
     *                     "list": [
     *                         {
     *                             "deviceCode": "11010500991327000888",
     *                             "deviceName": "三江新区龙头山下11",
     *                             "status": "0",
     *                             "code": "11010500991327000888",
     *                             "name": "三江新区龙头山下11",
     *                             "level": "3",
     *                             "num": 1,
     *                             "onlineNum": 1,
     *                             "list": [
     *                                 {
     *                                     "deviceCode": "11010500991327000888",
     *                                     "deviceName": "三江新区龙头山下11",
     *                                     "channelCode": "18113623664705525888",
     *                                     "channelName": "三江新区龙头山下_1",
     *                                     "status": "0",
     *                                     "code": "18113623664705525888",
     *                                     "name": "三江新区龙头山下_1",
     *                                     "level": "4",
     *                                     "longitude": "116.4687380000",
     *                                     "latitude": "39.9578090000",
     *                                     "checked": false,
     *                                     "num": 0,
     *                                     "onlineNum": 0,
     *                                     "list": [],
     *                                     "isMonitor": "0",
     *                                     "isMonitorNum": 0,
     *                                     "siteCode": "35689",
     *                                     "deviceStatusCount": 0
     *                                 }
     *                             ],
     *                             "isMonitor": "0",
     *                             "isMonitorNum": 0,
     *                             "siteCode": "35689",
     *                             "deviceStatusCount": 0
     *                         }
     *                     ],
     *                     "isMonitor": "0",
     *                     "isMonitorNum": 0,
     *                     "deviceStatusCount": 0
     *                 }
     *             ],
     *             "first": 1,
     *             "isMonitor": "0",
     *             "isMonitorNum": 0,
     *             "deviceStatusCount": 0
     *         }
     *     ]
     * }
     */
    @PostMapping("/recorder/device/getRecorderDevicesBySiteCode")
    public Result<List<DeviceSiteVO>> getRecorderDevicesBySiteCode(@RequestBody SiteRequestDTO siteRequestDTO){
        siteRequestDTO.setQueryType(2);
        return iDeviceCommonService.getDeviceListBySiteCode(siteRequestDTO);
    }

    /**
      * 查询指定经纬度附近的执法记录仪设备
      * @apiNote 查询指定经纬度附近的执法记录仪设备
      * @apiVersion 3.56
      * @tag  三、设备能力
      * @requestDemo
     * {
     * "latitude": "39.9578090000",
     * "longitude": "116.4687380000",
     * "radius":10000
     * }
      * @responseDemo
     *返回参数data下字段解析：
     *     deviceCode：设备编码
     *     name：设备名称
     *     location：设备地址
     *     distance：指定点位到设备距离
     *     longitude：经度
     *     latitude：纬度
     *     deviceStatus：设备状态
     *     height：挂高
     *     modelCode：型号编码
     *     modelName：型号名称
     *     deviceType：设备类型
     *     deviceTypeName：设备类型名称
     *     isMonitor：是否收藏
     *     deviceOriginalStatus：设备原始状态
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
     */
    @Deprecated
    @PostMapping("/recorder/device/getRecorderDeviceDistanceInfo")
    public Result<List<DeviceDistanceVO>> getRecorderDeviceDistanceInfo(@RequestBody DeviceDistanceDTO param){
        param.setQueryType(2);
        return  iDeviceCommonService.getDeviceDistanceInfo(param);
    }

    /**
      * 查询执法记录仪设备区域树
      * @apiNote  查询执法记录仪设备区域树
      * @apiVersion 3.57
      * @tag  三、设备能力
      * @requestDemo
     * {
     * "isMonitorFlag": "1"   //0 已收藏
     * }
      * @responseDemo
     * 返回参数data下字段解析：
     *     code：中国根节点
     *     name：中国
     *     num：全国通道总数
     *     onlineNum：全国在线通道总数
     *     list：区域下子区域列表
     *         code：区域编码
     *         name：区域名称
     *         level：层级 123 为省市区
     *         num：区域下通道总数
     *         onlineNum：区域下在线通道总数
     *         list：区域下子区域列表
     *             设备层级
     *             code：设备编码
     *             deviceCode：设备编码
     *             deviceName：设备名称
     *             isMonitor：该设备通道是否都收藏
     *             isMonitorNum：该设备下已收藏的设备通道数
     *             level：4
     *             list：通道信息集合
     *                 channelCode：通道编码
     *                 channelName：通道名称
     *                 code：通道编码
     *                 deviceCode：设备编码
     *                 deviceName：设备名称
     *                 isMonitor：该设备通道是否收藏
     *                 isMonitorNum：该设备下已收藏的设备通道数
     *                 latitude：纬度
     *                 level：层级 5
     *                 longitude：经度
     *                 name：通道名称
     *                 num：通道总数
     *                 onlineNum：在线通道总数
     *                 siteCode：站址编码
     *                 status：通道状态 0 在线 1 离线
     *         isMonitor 该节点下是否全收藏 0未收藏 1已收藏
     *         isMonitorNum 该节点下收藏的通道总数
     *     first:标注为第一层级
     *     isMonitor 该节点下是否全收藏 0未收藏 1已收藏
     *     isMonitorNum 该节点下收藏的通道总数
     *
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715307865754,
     *     "data": [
     *         {
     *             "code": "100000",
     *             "name": "中国",
     *             "num": 1,
     *             "onlineNum": 1,
     *             "list": [
     *                 {
     *                     "code": "110000",
     *                     "name": "北京市",
     *                     "level": "1",
     *                     "num": 1,
     *                     "onlineNum": 1,
     *                     "list": [
     *                         {
     *                             "code": "110100",
     *                             "name": "北京市",
     *                             "level": "2",
     *                             "num": 1,
     *                             "onlineNum": 1,
     *                             "list": [
     *                                 {
     *                                     "code": "110101",
     *                                     "name": "东城区",
     *                                     "level": "3",
     *                                     "num": 1,
     *                                     "onlineNum": 1,
     *                                     "list": [
     *                                         {
     *                                             "townCode": "110101001000",
     *                                             "townName": "东华门街道",
     *                                             "code": "110101001000",
     *                                             "name": "东华门街道",
     *                                             "level": "6",
     *                                             "num": 1,
     *                                             "onlineNum": 1,
     *                                             "list": [
     *                                                 {
     *                                                     "deviceCode": "11010500991327000888",
     *                                                     "status": "0",
     *                                                     "townCode": "110101001000",
     *                                                     "townName": "东华门街道",
     *                                                     "deviceAlias": "三江新区龙头山下11",
     *                                                     "code": "11010500991327000888",
     *                                                     "name": "三江新区龙头山下11",
     *                                                     "level": "4",
     *                                                     "num": 1,
     *                                                     "onlineNum": 1,
     *                                                     "list": [
     *                                                         {
     *                                                             "deviceCode": "11010500991327000888",
     *                                                             "deviceName": "三江新区龙头山下",
     *                                                             "channelCode": "18113623664705525888",
     *                                                             "channelName": "三江新区龙头山下_1",
     *                                                             "status": "0",
     *                                                             "channelAlias": "三江新区龙头山下_1",
     *                                                             "code": "18113623664705525888",
     *                                                             "name": "三江新区龙头山下_1",
     *                                                             "level": "5",
     *                                                             "longitude": "116.4687380000",
     *                                                             "latitude": "39.9578090000",
     *                                                             "checked": false,
     *                                                             "num": 0,
     *                                                             "onlineNum": 0,
     *                                                             "list": [],
     *                                                             "isMonitor": "0",
     *                                                             "isMonitorNum": 0,
     *                                                             "siteCode": "35689",
     *                                                             "deviceStatusCount": 0
     *                                                         }
     *                                                     ],
     *                                                     "isMonitor": "0",
     *                                                     "isMonitorNum": 0,
     *                                                     "siteCode": "35689",
     *                                                     "deviceStatusCount": 0
     *                                                 }
     *                                             ],
     *                                             "isMonitor": "0",
     *                                             "isMonitorNum": 0,
     *                                             "deviceStatusCount": 0
     *                                         }
     *                                     ],
     *                                     "isMonitor": "0",
     *                                     "isMonitorNum": 0,
     *                                     "deviceStatusCount": 0
     *                                 }
     *                             ],
     *                             "isMonitor": "0",
     *                             "isMonitorNum": 0,
     *                             "deviceStatusCount": 0
     *                         }
     *                     ],
     *                     "isMonitor": "0",
     *                     "isMonitorNum": 0,
     *                     "deviceStatusCount": 0
     *                 }
     *             ],
     *             "first": 1,
     *             "isMonitor": "0",
     *             "isMonitorNum": 0,
     *             "deviceStatusCount": 0
     *         }
     *     ]
     * }
     */
    @PostMapping("/recorder/device/getRecorderMonitorAreaTree")
    public Result<List<DeviceTreeVO>> getRecorderMonitorAreaTree(@RequestBody DeviceReqDTO deviceReqDTO){
        deviceReqDTO.setQueryType(2);
        deviceReqDTO.setNeedDevice(true);
        return iDeviceCommonService.getDeviceAreaTree(deviceReqDTO);
    }

    /**
      *  查询执法记录仪分布信息-以站址聚合打点
      * @apiNote  查询执法记录仪分布信息-以站址聚合打点
      * @apiVersion 3.58
      * @tag  三、设备能力
      * @requestDemo
     * {
     * "cityId": "" //市编码
     * }
      * @responseDemo
     * 返回参数data下字段解析：
     *    longitude: 经度
     *    latitude: 纬度
     *    siteCode: 站点编码
     *    list: 执法记录仪列表
     *        deviceCode: 执法记录仪编码
     *        status: 执法记录仪状态 0 在线 1 离线
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
     */
    @PostMapping("/recorder/device/getRecorderSites")
    public Result<List<DeviceTreeVO>> getRecorderSites(@RequestBody SiteRequestDTO siteRequestDTO){
        siteRequestDTO.setQueryType(2);
        List<IndustryDeviceTree> industryDeviceTrees = iDeviceCommonService.getDeviceSites(siteRequestDTO).getData();
        List<DeviceTreeVO> deviceTreeVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(industryDeviceTrees)) {
            for (IndustryDeviceTree industryDeviceTree : industryDeviceTrees){
                DeviceTreeVO deviceTreeVO = new DeviceTreeVO();
                BeanUtils.copyProperties(industryDeviceTree,deviceTreeVO);
                if (!CollectionUtils.isEmpty(industryDeviceTree.getList())){
                    List<DeviceTreeVO> childList = new ArrayList<>();
                    industryDeviceTree.getList().stream().forEach(item -> {
                        DeviceTreeVO child = new DeviceTreeVO();
                        BeanUtils.copyProperties(item,child);
                        childList.add(child);
                    });
                    deviceTreeVO.setList(childList);
                }
                deviceTreeVOList.add(deviceTreeVO);
            }
        }
        return Result.data(deviceTreeVOList);
    }

    /**
     *  分页查询执法记录仪设备列表
     * @apiNote  分页查询执法记录仪设备列表
     * @apiVersion  3.59
     * @tag  三、设备能力
     * @requestDemo
     * {
     *   "deviceCode": "",  //设备编码
     *   "deviceName": "",                  //设备名称
     *   "deviceStatus": "",                 //设备状态
     *   "manufacturerCode": "",       //设备厂家
     *   "modelCode": "",         //设备型号
     *   "orderStatus": "0",                //订购状态 0 有效、 1 失效  2 退订
     *   "deptIds": [],                  //所属组织
     *   "provinceCode":"",
     *   "cityCode":"",
     *   "countyCode":"",
     *   "townCode": ""
     *   "deviceCodeList":[]
     *
     * }
     * @responseDemo
     *返回参数data下字段解析：
     *     deviceCode：设备编码
     *     deviceName：设备名称
     *     deviceStatus：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
     *     deviceStatusName：设备状态名称
     *     location：设备地址
     *     longitude：经度
     *     latitude：纬度
     *     areaCode：管理区域
     *     admCode：行政区域
     *     manufacturerCode：厂家编码
     *     manufacturerName：厂家名称
     *     modelCode：型号编码
     *     accessProtocols：接入协议
     *     accessNodeCode：接入节点
     *     siteCode：站址编码
     *     isCascade：级联标识 0-直联---接入方式
     *     collectType：接入类型:0-国标；1-onvif；2-私有协议----执法记录仪目前只支持国标
     *     accessNode：接入节点
     *     devUserName：设备登录用户名
     *     appModFlag：是否自行修改标识
     *     orgName：设备所属组织名称
     *     orgCode：设备所属组织编码
     *     orgRelId：设备所属组织id
     *     orderStatus：订购状态
     *     orderStatusName：订购状态名称
     *     provinceCode：省份编码
     *     provinceName：省份名称
     *     cityCode：地市编码
     *     cityName：地市名称
     *     countyCode：区县名称
     *     countyName：区县名称
     *     townCode：乡镇名称
     *     townName：乡镇名称
     *     isMonitor：是否收藏字段，0未收藏 1已收藏
     *     distance：范围
     *     orgRels: 设备关联组织
     *         id: 组织主键id
     *         tenantId: 客户编码
     *         deptId: 部门编码
     *         deviceCode：设备编码
     *         deleteFlag: 0-未删除 1-已删除
     *         deviceType：设备类型
     *         industryCode: 行业编码
     *         appVerCode：应用版本编码
     *         deptName： 部门名称
     * {
     *   "code": 200,
     *   "msg": "查询成功",
     *   "time": 1744965931373,
     *   "data": {
     *     "total": 1,
     *     "list": [
     *       {
     *         "creator": "zhaozhiq",
     *         "createTime": "2024-11-05 16:10:10",
     *         "createBy": null,
     *         "updateBy": null,
     *         "modifier": null,
     *         "updateTime": "2024-11-05 16:10:10",
     *         "deleteFlag": null,
     *         "tenantId": null,
     *         "userId": null,
     *         "appVerCode": null,
     *         "industryCode": null,
     *         "platformCode": null,
     *         "dataScope": null,
     *         "criteriaList": null,
     *         "screenId": null,
     *         "screenType": null,
     *         "dataPermissions": null,
     *         "deviceCode": "11080200003017000009",
     *         "deviceName": "颐和金茂府1",
     *         "deviceStatus": 1,
     *         "deviceStatusName": "离线",
     *         "location": "颐和金茂府北京市海淀区瑞府路附近",
     *         "longitude": "116.21417",
     *         "latitude": "39.952032",
     *         "areaCode": "110108",
     *         "admCode": "5110108",
     *         "manufacturerCode": "hikvision",
     *         "manufacturerName": "海康威视",
     *         "modelCode": null,
     *         "accessProtocols": null,
     *         "accessNodeCode": null,
     *         "siteCode": "2681486",
     *         "isCascade": null,
     *         "collectType": null,
     *         "accessNode": null,
     *         "devUserName": null,
     *         "appModFlag": null,
     *         "orgName": "组织1-1,图层1-全量,河北广电信息网络集团股份有限公司宁晋分公司_备电-通用平台版本-1.0",
     *         "orgCode": null,
     *         "orgRelId": null,
     *         "orderStatus": 0,
     *         "orderStatusName": "有效",
     *         "provinceCode": "110000",
     *         "provinceName": "北京市",
     *         "cityCode": "110100",
     *         "cityName": "北京市",
     *         "countyCode": "110108",
     *         "countyName": "海淀区",
     *         "townCode": "110108102000",
     *         "townName": "四季青镇",
     *         "isMonitor": "0",
     *         "distance": null,
     *         "orgRels": [
     *           {
     *             "createBy": null,
     *             "createTime": null,
     *             "updateBy": null,
     *             "updateTime": null,
     *             "id": "6656853376331303022",
     *             "tenantId": "121062646",
     *             "deptId": "121062646-110101",
     *             "deviceCode": "11080200003017000009",
     *             "deleteFlag": null,
     *             "deviceType": 0,
     *             "industryCode": "000000",
     *             "appVerCode": null,
     *             "deptName": "组织1-1"
     *           },
     *           {
     *             "createBy": null,
     *             "createTime": null,
     *             "updateBy": null,
     *             "updateTime": null,
     *             "id": "6656853376331363386",
     *             "tenantId": "121062646",
     *             "deptId": "121062646-1108",
     *             "deviceCode": "11080200003017000009",
     *             "deleteFlag": null,
     *             "deviceType": 0,
     *             "industryCode": "000000",
     *             "appVerCode": null,
     *             "deptName": "图层1-全量"
     *           },
     *           {
     *             "createBy": null,
     *             "createTime": null,
     *             "updateBy": null,
     *             "updateTime": null,
     *             "id": "6656853376331364044",
     *             "tenantId": "121062646",
     *             "deptId": "121062646-11",
     *             "deviceCode": "11080200003017000009",
     *             "deleteFlag": null,
     *             "deviceType": 0,
     *             "industryCode": "000000",
     *             "appVerCode": null,
     *             "deptName": "河北广电信息网络集团股份有限公司宁晋分公司_备电-通用平台版本-1.0"
     *           }
     *         ],
     *         "fileUrlList": null,
     *         "channelList": [
     *           {
     *             "parentDeviceCode": "11080200003017000009",
     *             "channelName": "颐和金茂府_0",
     *             "channelCode": "110802100000000011130000024",
     *             "status": "1"
     *           }
     *         ]
     *       }
     *     ],
     *     "pageNum": 1,
     *     "pageSize": 10,
     *     "size": 1,
     *     "startRow": 1,
     *     "endRow": 1,
     *     "pages": 1,
     *     "prePage": 0,
     *     "nextPage": 0,
     *     "isFirstPage": true,
     *     "isLastPage": true,
     *     "hasPreviousPage": false,
     *     "hasNextPage": false,
     *     "navigatePages": 8,
     *     "navigatepageNums": [
     *       1
     *     ],
     *     "navigateFirstPage": 1,
     *     "navigateLastPage": 1
     *   }
     * }
     */
    @PostMapping("/recorder/device/getRecorderDeviceListPage")
    public Result<PageInfo<RecorderDeviceDTO>> getRecorderDeviceListPage(@RequestBody RecorderDeviceVO recorderDeviceVO){
        return iReorderDeviceServiceMigrate.getRecorderDeviceListPage(recorderDeviceVO);
    }
}
