package com.chinatower.device.api;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.api.deprecated.IIndustryDeviceServiceMigrate;
import com.chinatower.device.api.deprecated.IVideoDeviceMgmtServiceMigrate;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.ChannelInfoScrVO;
import com.chinatower.device.entity.vo.DeviceChannelInfoVO;
import com.chinatower.platform.entity.device.DeviceLabelReq;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.devicemgmt.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


@Component
public class IVideoDeviceMgmtService {

    @Resource
    IIndustryDeviceServiceMigrate industryDeviceServiceMigrate;

    @Resource
    IVideoDeviceMgmtServiceMigrate iVideoDeviceMgmtServiceMigrate;

    /**
     * 根据条件分页查询设备信息
     * @apiNote 根据条件分页查询设备信息
     * @apiVersion 3.67
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "operType":"0",
     *     "deviceCode":"",
     *     "devName":"",
     *     "status":"",
     *     "orgCode":"",
     *     "orgId":[
     *
     *     ],
     *     "orderStatus":"",
     *     "provinceCode":,
     *     "cityCode":,
     *     "countryCode":"",
     *     "channelName":"",
     *     "channelCode":"",
     *     "custCode":"",
     *     "pageNum":1,
     *     "pageSize":10,
     *     "total":926,
     *     "modelId":"",
     *     "source":"1",
     *     "tenantId":""
     * }
     *
     * @responseDemo
     * code枚举
     * 200   操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * 返回实体类（com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO）
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
     * children通道列表（com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO）
     *  parentDeviceCode设备编码
     *  channelName通道名称
     *  channelCode通道编码
     *  status设备状态 0在线1离线
     *  visualRange可视范围
     *  horizRange水平辐射角度
     *  vertiRange俯仰辐射角度
     *  tenantId租户Id
     * {
     *     "total":1,
     *     "rows":[
     *         {
     *             "tenantId":"122049393",
     *             "industryCode":"000000",
     *             "deviceCode":"11010500831327000168",
     *             "devName":"铁塔产业园-6号楼-海康241",
     *             "status":"0",
     *             "location":"北京市海淀区四季青镇常青路中国铁塔产业园",
     *             "orgCode":"122049393-01,122049393-0102",
     *             "orgRelId":"5864256894058066435,5864256894058078173",
     *             "orgName":"智联总部演示,摄像机北京市组织",
     *             "orderStatus":"0",
     *             "provinceCode":"110000",
     *             "provinceName":"北京市",
     *             "cityCode":"110100",
     *             "cityName":"北京市",
     *             "countryCode":"110105",
     *             "countryName":"朝阳区",
     *             "streetCode":"110105025000",
     *             "streetName":"大屯街道",
     *             "longitude":"116.268215000",
     *             "latitude":"39.959448000",
     *             "children":[
     *                 {
     *                     "parentDeviceCode":"11010500831327000168",
     *                     "channelName":"铁塔产业园-6号楼-海康241_0_0",
     *                     "channelCode":"11010500001310000017",
     *                     "status":"0",
     *                     "visualRange":null,
     *                     "horizRange":null,
     *                     "vertiRange":null,
     *                     "tenantId":"122049393"
     *                 }
     *             ],
     *             "modelId":"hikvision",
     *             "modelName":"海康",
     *             "categoryCode":0,
     *             "isMonitor":"0",
     *             "labelNameList":[
     *                 "tower"
     *             ]
     *         }
     *     ],
     *     "code":200
     * }
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/queryDeviceInfoPage")
    public TableDataInfo queryDeviceInfoPage(@RequestBody DeviceInfoVO deviceInfoVO) {
        return iVideoDeviceMgmtServiceMigrate.queryDeviceInfoPage(deviceInfoVO);
    }

    @PostMapping("/video/device/getChannelInfoForCustomer")
    public Result<List<ChannelInfoVO>> listChannelInfoForCustDev(@RequestBody DeviceInfoVO deviceInfoVO){
        return iVideoDeviceMgmtServiceMigrate.listChannelInfoForCustDev(deviceInfoVO);
    }

    /**
     * 查询设备所属的行政区域树形
     * @apiNote 查询设备所属的行政区域树形
     * @apiVersion 3.68
     * @tag 三、设备能力
     * @requestDemo
     * {}
     * @responseDemo
     * code枚举
     * 200   操作成功
     * 400	业务异常
     * 401	未认证
     * 404	服务未找到
     * 500	服务异常
     * 返回实体类
     * provinceId省份编码
     * provinceName省份名称
     * proLevel省份级别
     * children市级
     *  cityId市级编码
     *  cityName市级名称
     *  cityLevel市级级别
     *   children乡镇级
     *    countyId乡镇级编码
     *    countyName乡镇名称
     *    countryLevel乡镇级别
     *
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1648107167647,
     *     "data":[
     *         {
     *             "provinceId":"110000",
     *             "provinceName":"北京市",
     *             "proLevel":"1",
     *             "children":[
     *                 {
     *                     "cityId":"110100",
     *                     "cityName":"西城区",
     *                     "cityLevel":"2",
     *                     "children":[
     *                         {
     *                             "countyId":"110108",
     *                             "countyName":"西城区",
     *                             "countryLevel":"3"
     *                         }
     *                     ]
     *                 }
     *             ]
     *         }
     *     ]
     * }
     */
    @PostMapping("/video/device/queryAreaTree")
    public Result<List<DeviceAreaTreeVO>> queryAreaTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        return iVideoDeviceMgmtServiceMigrate.queryAreaTree(deviceReqDTO);
    }

    /**
     * 根据条件查询设备信息
     * @apiNote 根据条件查询设备信息
     * @apiVersion 3.69
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010800831327000008",
     *     "tenantId":"122035807"
     * }
     * @responseDemo
     * 返回参数解析：
     * tenantId：租户编码
     * industryCode：行业编码
     * deviceCode：设备编码
     * deviceName：设备名称
     * status：设备状态:0在线，1离线
     * location：站址地址
     * orgCode：设备所属组织编码
     * orgId：设备所属组织ID
     * orgName：所属组织名称
     * orderStatus：订购状态0-变更 1-新增订购 2-订单续订、启用 3-退订 4-停用、失效'
     * provinceCode：省份
     * provinceName：省份名称
     * cityCode：地市
     * cityName：地市名称
     * countyCode：区县
     * countyName：区县名称
     * streetCode：街道/乡镇
     * streetName：街道名称
     * longitude：经度
     * latitude：纬度
     * children通道列表
     *  parentDeviceCode设备编码
     *  channelName通道名称
     *  channelCode通道编码
     *  status设备状态0在线1离线
     *  visualRange可视范围
     *  horizRange水平辐射角度
     *  vertiRange俯仰辐射角度
     *  tenantId租户Id
     * modelId厂家编码
     * modelName厂家名称
     * categoryCode摄像机类型（0-NVR,1-网络摄像机,2-AR摄像机,3-热成像摄像机,4、人脸卡口摄像机，5-车辆卡口摄像机；7-无人机）
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1686280398196,
     *     "data":{
     *         "tenantId":"122035793",
     *         "industryCode":"000000",
     *         "deviceCode":"11010800831327000008",
     *         "devName":"-11-243",
     *         "status":"0",
     *         "location":"",
     *         "orgCode":"122035793-01,122035793-010101,122035793-010102",
     *         "orgRelId":"6656853376329921158,6656853376329928931,6656853376329928942",
     *         "orgName":"20220322014--1.0,2,3",
     *         "orderStatus":"2",
     *         "provinceCode":"110000",
     *         "provinceName":"",
     *         "cityCode":"110100",
     *         "cityName":"",
     *         "countryCode":"110108",
     *         "countryName":"",
     *         "streetCode":"110108006000",
     *         "streetName":"",
     *         "longitude":"116.308281000",
     *         "latitude":"39.958971000",
     *         "children":[
     *             {
     *                 "parentDeviceCode":"11010800831327000008",
     *                 "channelName":"-11-243_0",
     *                 "channelCode":"11010800831310000008",
     *                 "status":"0",
     *                 "visualRange":"0",
     *                 "horizRange":"0",
     *                 "vertiRange":"0",
     *                 "tenantId":"122035793"
     *             }
     *         ],
     *         "modelId":"dhua",
     *         "modelName":"",
     *         "categoryCode":0
     *     }
     * }
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/getDeviceInfo")
    public Result<DeviceInfoVO> getDeviceInfo(@RequestBody DeviceInfoVO deviceInfoVO) {
        SiteRequestDTO siteRequestDTO = new SiteRequestDTO();
        BeanUtils.copyProperties(deviceInfoVO, siteRequestDTO);
        Result<DeviceChannelInfoVO> deviceChannelInfoVOResult = industryDeviceServiceMigrate.queryDevicesForWE(siteRequestDTO);
        DeviceChannelInfoVO data = deviceChannelInfoVOResult.getData();

        DeviceInfoVO result = new DeviceInfoVO();
        if(ObjectUtil.isNotEmpty(data)){
            BeanUtils.copyProperties(data, result);
            List<ChannelInfoScrVO> channels = data.getChannels();
            List<ChannelInfoVO> children = new ArrayList<>();
            for (ChannelInfoScrVO channel : channels) {
                ChannelInfoVO channelInfoVO = new ChannelInfoVO();
                BeanUtils.copyProperties(channel, channelInfoVO);
                children.add(channelInfoVO);
            }
            result.setChildren(children);
        }
        return Result.data(result);
    }

    /**
     * 更新设备相关信息
     * @apiNote 更新设备相关信息
     * @apiVersion 3.70
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010500011327000005",
     *     "devName":"朝阳区东坝乡水管站西侧林地",
     *     "streetCode":"",
     *     "streetName":"",
     *     "categoryCode":4,
     *     "channelInfoList":[
     *         {
     *             "parentDeviceCode":"11010500011327000005",
     *             "channelName":"朝阳区东坝乡水管站西侧林地_1",
     *             "channelCode":"72380455491098853311",
     *             "status":"0",
     *             "visualRange":null,
     *             "horizRange":null,
     *             "vertiRange":null,
     *             "tenantId":"122035807"
     *         }
     *     ],
     *     "oldOrgCode":"122035807-0106,122035807-0109,122035807-0108,122035807-0105,122035807-0104,122035807-01,122035807-0113",
     *     "orgCode":"122035807-0106,122035807-0109,122035807-0108,122035807-0105,122035807-0104,122035807-01,122035807-0113",
     *     "tenantId":"122035807",
     *     "fileUrlList":[
     *         "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/2.0/8cf576079fbbddce05679af789ab183d/1cc9e1d4fb9e15d0a6c158820c4eb113/576366c3041449a29949907a64148a5e.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1694749878&Signature=8Z00G7mRDQkZnxgFuyy%2B9KFiY%2B0%3D"
     *     ],
     *     "orgRelId":"6656853376329931901,6656853376329932048,6656853376329932122,6656853376329932191,6656853376329932263,6656853376329934987,6656853376329935188"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"新设备相关信息成功!",
     *     "time":1694677677822
     * }
     */
    @PostMapping("/video/device/updateDeviceInfo")
    public Result<String> updateDeviceInfo(@RequestBody DeviceInfoVO deviceInfoVO) {
        return iVideoDeviceMgmtServiceMigrate.updateDeviceInfo(deviceInfoVO);
    }

    /**
     * 查询设备下的客户信息列表
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/getDeviceCustList")
    public Result<List<DeviceTenantInfoVO>> getDeviceCustList(@RequestBody DeviceInfoVO deviceInfoVO){
        return iVideoDeviceMgmtServiceMigrate.getDeviceCustList(deviceInfoVO);
    }

    /**
     * 设备编辑时校验设备名称是否重复
     * @apiNote 设备编辑时校验设备名称是否重复
     * @apiVersion 3.71
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "devName":"朝阳区东坝乡水管站西侧林地",
     *     "oldDevName":"朝阳区东坝乡水管站西侧林地",
     *     "tenantId":"122035807"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1694749283887,
     *     "data":true
     * }
     */
    @PostMapping("/video/device/checkDeviceName")
    public Result<Boolean> checkDeviceName(@RequestBody DeviceInfoVO deviceInfoVO){
        return iVideoDeviceMgmtServiceMigrate.checkDeviceName(deviceInfoVO);
    }

    /**
     * 查询客户设备标签
     * @apiNote 查询客户设备标签
     * @apiVersion 3.72
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010800831327000008",
     *     "tenantId":"122035807"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     * creator：创建人
     * createTime：创建时间
     * createBy：创建人
     * updateBy:更新人
     * modifier：更新人
     * updateTime：更新时间
     * deleteFlag：删除标识 0 正常 1删除
     * userId：用户id
     * tenantId：租户编码
     * appVerCode:版本标识
     * industryCode：行业编码
     * deviceCode：设备编码
     * labelId：标签id
     * labelRelId：标签关联编码
     * labelName：标签名称
     * labelStatus：标签状态 0不存在 1存在
     * flag：标识
     * {
     *     "code":200,
     *     "msg":"",
     *     "time":1686276207912,
     *     "data":[
     *         {
     *             "creator":null,
     *             "createTime":null,
     *             "createBy":null,
     *             "updateBy":null,
     *             "modifier":null,
     *             "updateTime":null,
     *             "deleteFlag":null,
     *             "tenantId":null,
     *             "userId":null,
     *             "appVerCode":"1.0",
     *             "industryCode":"000000",
     *             "labelId":516,
     *             "labelRelId":null,
     *             "deviceCode":null,
     *             "labelName":"222",
     *             "labelStatus":0,
     *             "flag":null
     *         },
     *         {
     *             "creator":null,
     *             "createTime":null,
     *             "createBy":null,
     *             "updateBy":null,
     *             "modifier":null,
     *             "updateTime":null,
     *             "deleteFlag":null,
     *             "tenantId":null,
     *             "userId":null,
     *             "appVerCode":"1.0",
     *             "industryCode":"000000",
     *             "labelId":545,
     *             "labelRelId":504,
     *             "deviceCode":null,
     *             "labelName":"",
     *             "labelStatus":1,
     *             "flag":null
     *         }
     *     ]
     * }
     * @param tenantDeviceLabelVO
     * @return
     */
    @PostMapping("/video/device/queryDeviceLabels")
    public Result<List<TenantDeviceLabelVO>> queryDeviceLabels(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO){
        return iVideoDeviceMgmtServiceMigrate.queryDeviceLabels(tenantDeviceLabelVO);
    }

    /**
     * 查询租户所属标签
     * @apiNote 查询租户所属标签
     * @apiVersion 3.73
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "industryCode":"000000",
     *     "tenantId":"122035807"
     * }
     * @responseDemo
     * labelId便签id
     * labelName标签名称
     * {
     *     "code":200,
     *     "msg":"查询成功",
     *     "time":1694751384055,
     *     "data":[
     *         {
     *             "labelId":55959,
     *             "labelName":"6628288499599163773839494995499494928717727273774"
     *         },
     *         {
     *             "labelId":55942,
     *             "labelName":"有什么事名字英语"
     *         }
     *     ]
     * }
     */
    @PostMapping("/video/device/queryTenantLabelList")
    public Result<List<TenantDeviceLabelVO>> queryTenantLabelList(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO){
        return iVideoDeviceMgmtServiceMigrate.queryTenantLabelList(tenantDeviceLabelVO);
    }

    /**
     * 添加客户设备标签
     * @apiNote 添加客户设备标签
     * @apiVersion 3.74
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010800831327000008",
     *     "labelName":"11122222",
     *     "tenantId":"122035807"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"添加客户设备标签成功!",
     *     "time":1694677677822
     * }
     */
    @PostMapping("/video/device/addDeviceLabel")
    public Result<String> addDeviceLabel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO){
        return iVideoDeviceMgmtServiceMigrate.addDeviceLabel(tenantDeviceLabelVO);
    }

    /**
     * 添加客户设备标签关系
     * @apiNote 添加客户设备标签关系
     * @apiVersion 3.75
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010800831327000008",
     *     "labelId":"55955",
     *     "tenantId":"122035807"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"添加客户设备标签关系成功!",
     *     "time":1694677677822
     * }
     */
    @PostMapping("/video/device/addDeviceLabelRel")
    public Result<String> addDeviceLabelRel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO){
        return iVideoDeviceMgmtServiceMigrate.addDeviceLabelRel(tenantDeviceLabelVO);
    }

    /**
     * 修改客户设备标签
     * @apiNote 修改客户设备标签
     * @apiVersion 3.76
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "labelRelId":"24523452",
     *     "labelName":"11122222",
     *     "labelId":"55955"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"修改客户设备标签成功!",
     *     "time":1694677677822
     * }
     */
    @PostMapping("/video/device/updateDeviceLabel")
    public Result<String> updateDeviceLabel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO){
        return iVideoDeviceMgmtServiceMigrate.updateDeviceLabel(tenantDeviceLabelVO);
    }

    /**
     * 解除客户设备标签关系
     * @apiNote 解除客户设备标签关系
     * @apiVersion 3.77
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010800831327000008",
     *     "labelName":"11122222",
     *     "labelRelId":"2452452",
     *     "tenantId":"122035807",
     *     "labelId":"55955"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"解除客户设备标签关系成功!",
     *     "time":1694677677822
     * }
     */
    @PostMapping("/video/device/relieveDeviceLabelRel")
    public Result<String> relieveDeviceLabelRel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO){
        return iVideoDeviceMgmtServiceMigrate.relieveDeviceLabelRel(tenantDeviceLabelVO);
    }

    /**
     * 删除客户设备标签
     * @apiNote 删除客户设备标签
     * @apiVersion 3.78
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"11010800831327000008",
     *     "labelRelId":"2345234",
     *     "tenantId":"122035807",
     * 	   "labelId":"55955"
     * }
     * @responseDemo
     * {
     *     "code":200,
     *     "msg":"删除客户设备标签成功!",
     *     "time":1694677677822
     * }
     */
    @PostMapping("/video/device/deleteDeviceLabel")
    public Result<String> deleteDeviceLabel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO){
        return iVideoDeviceMgmtServiceMigrate.deleteDeviceLabel(tenantDeviceLabelVO);
    }


    /**
      *
      * @apiNote 查询设备所有标签
      * @apiVersion 3.79
      * @tag 三、设备能力
      * @requestDemo
     * {
     *   "deviceCode": "11010800831327000003"
     * }
      * @responseDemo
     * 返回参数data下字段解析：
     *     labelTypeId：标签类型id
     *     labelTypeName：标签类型名称
     *     labelCount：标签数量
     *     labelInfoList：标签信息列表
     *     labelId：标签id
     *     labelName：标签名称
     *     isRel：是否关联 0未关联 1已关联
     * {
     *   "code": 200,
     *   "msg": "请求成功",
     *   "time": 1701134823954,
     *   "data": [
     *     {
     *       "labelTypeId": 298,
     *       "labelTypeName": "213",
     *       "labelCount": 30,
     *       "labelInfoList": [
     *         {
     *           "labelId": 55962,
     *           "labelName": "123",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55963,
     *           "labelName": "1233",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55970,
     *           "labelName": "测试标签1",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55979,
     *           "labelName": "测试标签10",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55980,
     *           "labelName": "测试标签11",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55981,
     *           "labelName": "测试标签12",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55982,
     *           "labelName": "测试标签13",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55983,
     *           "labelName": "测试标签14",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55984,
     *           "labelName": "测试标签15",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55985,
     *           "labelName": "测试标签16",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55986,
     *           "labelName": "测试标签17",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55987,
     *           "labelName": "测试标签18",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55988,
     *           "labelName": "测试标签19",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55971,
     *           "labelName": "测试标签2",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55989,
     *           "labelName": "测试标签20",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55990,
     *           "labelName": "测试标签21",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55991,
     *           "labelName": "测试标签22",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55992,
     *           "labelName": "测试标签23",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55993,
     *           "labelName": "测试标签24",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55994,
     *           "labelName": "测试标签25",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55995,
     *           "labelName": "测试标签26",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55996,
     *           "labelName": "测试标签27",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55997,
     *           "labelName": "测试标签28",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55972,
     *           "labelName": "测试标签3",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55973,
     *           "labelName": "测试标签4",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55974,
     *           "labelName": "测试标签5",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55975,
     *           "labelName": "测试标签6",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55976,
     *           "labelName": "测试标签7",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55977,
     *           "labelName": "测试标签8",
     *           "isRel": 0
     *         },
     *         {
     *           "labelId": 55978,
     *           "labelName": "测试标签9",
     *           "isRel": 0
     *         }
     *       ]
     *     }
     *   ]
     * }
      */
    @PostMapping("/video/device/queryDeviceLabelInfoByDeviceCode")
    public Result<List<TenantDeviceLabelSelectedVO>> queryDeviceLabelInfoByDeviceCode(@RequestBody TenantDeviceLabelRelVO param){
        return iVideoDeviceMgmtServiceMigrate.queryDeviceLabelInfoByDeviceCode(param);
    }

    /**
     * 查询摄像机标签信息-通用内部使用，不对行业提供
     * @param param 参数条件
     * @return 摄像机标签信息
     */
    public Result<List<DeviceLabelInfoVO>> queryDeviceLabelInfoByCondition(@RequestBody DeviceLabelReq param) {
        return iVideoDeviceMgmtServiceMigrate.queryDeviceLabelInfoByCondition(param);
    }

    /**
     * @apiNote 查询摄像机图片
     * @apiVersion  3.80
     * @tag 三、设备能力
     * @requestDemo
     * {
     *   "deviceCode": "14052100831327000004"
     * }
     * @responseDemo
     * {
     *   "code": 200,
     *   "msg": "请求成功",
     *   "time": 1701140697107,
     *   "data": [
     *     "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/84c11123b1c93fd78fa51be4d31454ce/0d95ed9f44b14043a0857e8bd3713014.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1701141297&Signature=Aljr8UeUfTidKGZGuD1BtTz5d78%3D",
     *     "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/84c11123b1c93fd78fa51be4d31454ce/a43262e7374f45d29b525acb39707797.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1701141297&Signature=LHxDYacA7vHm7suQNF48QgAaHU8%3D",
     *     "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/84c11123b1c93fd78fa51be4d31454ce/34de4841c67348a1ba01594fe68d9839.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1701141297&Signature=Qm/ylwseRbHVHGYyZV%2BSYfGPPbE%3D",
     *     "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/84c11123b1c93fd78fa51be4d31454ce/6202b4cf1cce4a88bc40e7e2525788aa.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1701141297&Signature=YcjBK09aJ6Jv/pJYp0uDFXhsHgw%3D",
     *     "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/84c11123b1c93fd78fa51be4d31454ce/d5b4d16fb6ec4fccb943690f6cad412a.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1701141297&Signature=AIBBbFP2GjKUf9OflyIdPlPBWAY%3D",
     *     "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/84c11123b1c93fd78fa51be4d31454ce/3dab4ae8f893485bb0cffea0cf5551ec.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1701141297&Signature=ncgwE6MRq3zO7wM4NGanQWSM3OY%3D",
     *     "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/84c11123b1c93fd78fa51be4d31454ce/edc6524d62184c34b7c79f2d59afc197.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1701141297&Signature=SZYLHLWZ3FcP5YQjPf3zD95U3a4%3D",
     *     "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/84c11123b1c93fd78fa51be4d31454ce/6139776da12c4a3eaefe9bdf842742a7.jpg?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1701141297&Signature=MadOEKa1OHfkHYb4sXETa1arbrQ%3D",
     *     "https://video-platform.obs.cn-north-4.myhuaweicloud.com/slyy2/dev/1.0/ca3ede80c21cd96bba96774974b67185/84c11123b1c93fd78fa51be4d31454ce/97bd92e6727f448f8c5c9fdd3230b781.png?AccessKeyId=CRFYWADDOJHXLKROOIXX&Expires=1701141297&Signature=BMvZzKvEW5dagVy9X1eaFWtTgkU%3D"
     *   ]
     * }
     */
    @PostMapping("/video/device/getDeviceImage")
    public Result<List<String>> getDeviceImage(@RequestBody DeviceInfoVO deviceInfoVO){
        return iVideoDeviceMgmtServiceMigrate.getDeviceImage(deviceInfoVO);
    }

    /**
     * @apiNote 根据设备编码查询订购设备的客户
     * @apiVersion  3.81
     * @tag 三、设备能力
     * @requestDemo
     * {
     *   "deviceCodeList": ["14052100831327000004"]
     * }
     * @responseDemo
     * {
     *   "code": 200,
     *   "msg": "请求成功",
     *   "time": 1701140697107,
     *   "data": [
     *    {
     *        "tenantId": "123113",
     *        "industryCode": "100000",
     *        "deviceCode": "143242235234523"
     *    }
     *   ]
     * }
     */
    @PostMapping("/video/device/queryOrderDeviceByDevices")
    public Result<List<DeviceInfoVO>> queryOrderDeviceByDevices(@RequestBody DeviceInfoVO deviceInfoVO) {
        return iVideoDeviceMgmtServiceMigrate.queryOrderDeviceByDevices(deviceInfoVO);
    }
}
