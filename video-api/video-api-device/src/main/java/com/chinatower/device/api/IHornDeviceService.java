package com.chinatower.device.api;

import cn.hutool.core.collection.CollectionUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.device.api.deprecated.IHornDeviceServiceMigrate;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.TalkReq;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.VmHornAuditionRequestDTO;
import com.chinatower.device.entity.dto.VmHornContentConfDTO;
import com.chinatower.device.entity.dto.VmHornContentConfRequestListDTO;
import com.chinatower.device.entity.dto.VmHornContentRequestDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Component
public class IHornDeviceService {

    @Autowired
    private IHornDeviceServiceMigrate iHornDeviceServiceMigrate;

    @Autowired
    private IDeviceCommonService iDeviceCommonService;

    /**
     * 查询设备所属的行政区域树形
      * @apiNote 查询设备所属的行政区域树形
      * @apiVersion 3.41
      * @tag 三、设备能力
      * @requestDemo
      * {
      *     "needDevice":true,
      *      "isMonitorFlag":"1"
      * }
      * @responseDemo
      * 返回参数data下字段解析：
      *     code：区域编码
      *     name：区域名称
      *     level：区域等级
      *     num：区域设备数总和
      *     onlineNum：在线设备数总和
      *     list：设备列表
      *         deviceCode：设备编码
      *         deviceName：设备名称
      *         status：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
      *         deviceAlias ：设备别名
      *         code：设备编码
      *         name：设备名称
      *         longitude：经度
      *         latitude：纬度
      *         isMonitor：是否收藏 0未收藏 1已收藏
      *         isMonitorNum：收藏数量
      *     isMonitor：是否收藏 0未收藏 1已收藏
      *     isMonitorNum：收藏数量
      *
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1690855107300,
      *     "data": [
      *         {
      *             "code": "110102011000",
      *             "name": "金融街街道",
      *             "level": "4",
      *             "num": 3,
      *             "onlineNum": 3,
      *             "list": [
      *                 {
      *                     "deviceCode": "31011200831377000003",
      *                      "deviceName": "修改喇叭7",
      *                     "status": "1",
      *                     "deviceAlias": "修改喇叭7",
      *                     "code": "31011200831377000003",
      *                     "name": "铁塔用户又修改设备名称",
      *                     "longitude": "116.372183000",
      *                     "latitude": "39.919775000",
      *                     "isMonitor": "0",
      *                     "isMonitorNum": 0
      *                         }
      *             ],
      *             "isMonitor": "0",
      *             "isMonitorNum": 0
      *         }
      *     ]
      * }
      */
    @PostMapping("/hornDevice/getHornMonitorTree")
    public Result<List<DeviceTreeVO>> getHornMonitorTree(@RequestBody HornDeviceDTO hornDeviceDTO){
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        BeanUtils.copyProperties(hornDeviceDTO,deviceReqDTO);
        deviceReqDTO.setQueryType(5);
        return iDeviceCommonService.getDeviceAreaTree(deviceReqDTO);
    }

    /**
      * 查询行政组织喇叭设备树
      * @apiNote 查询行政组织喇叭设备树
      * @apiVersion 3.42
      * @tag 三、设备能力
      * @requestDemo
      *   {
      *    "isMonitorFlag":"1"
      *  }
      * @responseDemo
      * 返回参数data下字段解析：
      *     code：组织编码
      *     name：组织名称
      *     level：组织等级
      *     parentCode：上级组织编码
      *     num：区域设备数总和
      *     onlineNum：在线设备数总和
      *     list：设备列表
      *         deviceCode：设备编码
      *         deviceName：设备名称
      *         status：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
      *         code：设备编码
      *         name：设备名称
      *         level：层级（设备层级是最大的）
      *         longitude：经度
      *         latitude：纬度
      *         isMonitor：是否收藏 0未收藏 1已收藏
      *         isMonitorNum：收藏数量
      *     isMonitor：是否收藏 0未收藏 1已收藏
      *     isMonitorNum：收藏数量
      *
      *  {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1690855123519,
      *     "data": [
      *         {
      *             "code": "121119862-010101",
      *             "name": "测试",
      *             "level": "3",
      *             "parentCode": "121119862-0101",
      *             "num": 1,
      *             "onlineNum": 1,
      *             "list": [
      *                 {
      *                     "deviceCode": "31011200831377000003",
      *                     "deviceName": "修改喇叭7",
      *                     "status": "1",
      *                     "code": "31011200831377000003",
      *                     "name": "修改喇叭7",
      *                     "level": "4",
      *                     "longitude": "116.372183000",
      *                     "latitude": "39.919775000",
      *                     "isMonitor": "0",
      *                     "isMonitorNum": 0
      *                         }
      *             ],
      *             "isMonitor": "0",
      *             "isMonitorNum": 0
      *         }
      *     ]
      * }
      */
    @PostMapping("/hornDevice/getHornMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getHornMonitorOrgTree(@RequestBody HornDeviceDTO hornDeviceDTO){
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        BeanUtils.copyProperties(hornDeviceDTO,deviceReqDTO);
        deviceReqDTO.setQueryType(5);
        return iDeviceCommonService.getDeviceOrgTree(deviceReqDTO);
    }

    @PostMapping("/hornDevice/startTalk")
    public Result<LockResultVo> startTalk(@RequestBody TalkReq talkReq){
        return iHornDeviceServiceMigrate.startTalk(talkReq);
    }

    @PostMapping("/hornDevice/stopTalk")
    public Result<String> stopTalk(@RequestBody TalkReq talkReq){
        return iHornDeviceServiceMigrate.stopTalk(talkReq);
    }


    /**
      * 根据id查询喇叭详情
      * @apiNote 根据id查询喇叭详情
      * @apiVersion 3.43
      * @tag 三、设备能力
      * @requestDemo
      * {
      *    "deviceCode": "31011200831377000003"
      * }
      * @responseDemo
      *
      *返回参数data下字段解析：
      *     tenantId：客户id
      *     id：设备主键id
      *     deviceCode：设备编码
      *     deviceName：设备名称
      *     deviceStatus：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
      *     manufacturerCode：厂家编码
      *     modelCode：型号编码
      *     deviceType：设备类型
      *     orderStatus：订购状态
      *     provinceCode：省份编码
      *     provinceName：省份名称
      *     cityCode：地市编码
      *     cityName：地市名称
      *     countyCode：区县名称
      *     countyName：区县名称
      *     townCode：乡镇名称
      *     townName：乡镇名称
      *     height：设备挂高，单位：米
      *     longitude：经度
      *     latitude：纬度
      *     location：设备地址
      *     isMonitor：是否收藏 0未收藏 1已收藏
      *     manufacturerName：厂家名称
      *
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
      */
    @PostMapping("/hornDevice/queryHornDeviceDetail")
    public Result<HornDeviceVo> queryHornDeviceDetail(@RequestBody HornDeviceDTO hornDeviceDTO){
        DeviceCommonInfoVO deviceInfoVO = new DeviceCommonInfoVO();
        BeanUtils.copyProperties(hornDeviceDTO,deviceInfoVO);
        deviceInfoVO.setQueryType(5);
        DeviceCommonInfoVO deviceInfo = iDeviceCommonService.getDeviceInfo(deviceInfoVO).getData();
        HornDeviceVo hornDeviceVo = new HornDeviceVo();
        BeanUtils.copyProperties(deviceInfo,hornDeviceVo);
        hornDeviceVo.setDeviceStatus(String.valueOf(deviceInfo.getDeviceStatus()));
        return Result.data(hornDeviceVo);
    }

    /**
      * 根据区域编码查询喇叭列表
      * @apiNote  根据区域编码查询喇叭列表
      * @apiVersion 3.44
      * @tag 三、设备能力
      * @requestDemo
      * {
      *     "provinceCode":"",
      *    "cityCode":"",
      *    "countyCode":"",
      *    "townCode":"",
      *    "deptId":"121119862-0101"
      * }
      * @responseDemo
      *返回参数data下字段解析：
      *     deviceCode：设备编码
      *     deviceName：设备名称
      *     deviceStatus：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
      *     provinceCode：省份编码
      *     cityCode：地市编码
      *     countyCode：区县名称
      *     townCode：乡镇名称
      *     height：设备挂高，单位：米
      *     longitude：经度
      *     latitude：纬度
      *     isMonitor：是否收藏 0未收藏 1已收藏
      *
      *  {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1690878468738,
      *     "data": [
      *         {
      *             "deviceCode": "170288054232",
      *             "deviceName": "0616测试1",
      *             "deviceStatus": "2",
      *             "provinceCode": "460000",
      *             "cityCode": "469000",
      *             "countyCode": "469006",
      *             "townCode": "530524101000",
      *             "height": "20.0",
      *             "longitude": "87.88",
      *             "latitude": "23.55",
      *             "isMonitor": "0"
      *         }
      *     ]
      * }
      */
    @PostMapping("/hornDevice/queryHornForArea")
    public Result<List<HornDeviceVo>> queryHornForArea(@RequestBody HornDeviceDTO hornDeviceDTO){
        SiteRequestDTO siteRequestDTO = new SiteRequestDTO();
        BeanUtils.copyProperties(hornDeviceDTO,siteRequestDTO);
        siteRequestDTO.setProvinceId(hornDeviceDTO.getProvinceCode());
        siteRequestDTO.setCityId(hornDeviceDTO.getCityCode());
        siteRequestDTO.setCountyId(hornDeviceDTO.getCountyCode());
        siteRequestDTO.setQueryType(5);
        siteRequestDTO.setQueryFlag(1);
        List<DeviceSiteVO> deviceListBySiteCode = iDeviceCommonService.getDeviceListBySiteCode(siteRequestDTO).getData();
        List<HornDeviceVo> hornDeviceList = new ArrayList<>();
        for (DeviceSiteVO deviceSiteVO : deviceListBySiteCode){
            HornDeviceVo hornDeviceVo = new HornDeviceVo();
            BeanUtils.copyProperties(deviceSiteVO,hornDeviceVo);
            hornDeviceList.add(hornDeviceVo);
        }
        return Result.data(hornDeviceList);
    }

    /**
      * 根据站址编码查询喇叭列表
      * @apiNote 根据站址编码查询喇叭列表
      * @apiVersion 3.45
      * @tag 三、设备能力
      * @requestDemo
      * {
      *     "deviceCode": "51101100831377000001",
      *     "siteCode": "1234"
      * }
      * @responseDemo
      *返回参数data下字段解析：
      *     tenantId：客户id
      *     deviceCode：设备编码
      *     deviceName：设备名称
      *     deviceStatus：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
      *     provinceCode：省份编码
      *     cityCode：地市编码
      *     countyCode：区县名称
      *     townCode：乡镇名称
      *     height：设备挂高，单位：米
      *     longitude：经度
      *     latitude：纬度
      *     location：设备地址
      *     isMonitor：是否收藏 0未收藏 1已收藏
      *     orderCode：订单编号
      *
      * {
      *     "code": 200,
      *     "msg": "请求成功",
      *     "time": 1690859578643,
      *     "data": [
      *         {
      *             "tenantId": "121119862",
      *             "deviceCode": "51101100831377000001",
      *             "deviceName": "永永福2",
      *             "deviceStatus": "1",
      *             "provinceCode": "110000",
      *             "cityCode": "110100",
      *             "countyCode": "110102",
      *             "townCode": "110102011000",
      *             "height": "10.0",
      *             "longitude": "116.369951837",
      *             "latitude": "39.921861624",
      *             "location": "北京市西城区金融街街道砖塔胡同68号砖塔胡同关帝庙",
      *             "isMonitor": "0",
      *             "orderCode": "PO12111986220230621099488239616"
      *         }
      *     ]
      * }
      */
    @PostMapping("/hornDevice/queryHornForSite")
    public Result<List<HornDeviceVo>> queryHornForSite(@RequestBody HornDeviceDTO hornDeviceDTO){
        SiteRequestDTO siteRequestDTO = new SiteRequestDTO();
        BeanUtils.copyProperties(hornDeviceDTO,siteRequestDTO);
        siteRequestDTO.setQueryType(5);
        List<DeviceSiteVO> list = iDeviceCommonService.getDeviceListBySiteCode(siteRequestDTO).getData();
        List<HornDeviceVo> resultList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(list)) {
            for (DeviceSiteVO deviceSiteVO : list){
                HornDeviceVo hornDeviceVo = new HornDeviceVo();
                BeanUtils.copyProperties(deviceSiteVO,hornDeviceVo);
                resultList.add(hornDeviceVo);
            }
        }
        return Result.data(resultList);
    }

    /**
     * 喇叭地图打点
     * @param hornDeviceDTO
     * @return
     */
    @PostMapping("/hornDevice/querySiteDevTree")
    public Result<List<HornDeviceVo>> querySiteDevTree(@RequestBody HornDeviceDTO hornDeviceDTO){
        SiteRequestDTO siteRequestDTO = new SiteRequestDTO();
        BeanUtils.copyProperties(hornDeviceDTO,siteRequestDTO);
        siteRequestDTO.setQueryType(5);
        List<IndustryDeviceTree> list = iDeviceCommonService.getDeviceSites(siteRequestDTO).getData();
        List<HornDeviceVo> resultList = new ArrayList<>();
        for (IndustryDeviceTree industryDeviceTree : list){
            HornDeviceVo hornDeviceVo = new HornDeviceVo();
            hornDeviceVo.setSiteCode(industryDeviceTree.getSiteCode());
            hornDeviceVo.setLatitude(industryDeviceTree.getLatitude());
            hornDeviceVo.setLongitude(industryDeviceTree.getLongitude());
            hornDeviceVo.setList(industryDeviceTree.getList());
            resultList.add(hornDeviceVo);
        }
        return Result.data(resultList);
    }

    /**
     * 查询喇叭管理列表
     * @apiNote 查询喇叭管理列表
     * @apiVersion 3.46
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"",
     *     "deviceName":"",
     *     "deviceStatus":"",
     *     "manufacturerCode":"",
     *     "modelCode":"",
     *     "deptIds":[],
     *     "orderStatus":"1",
     *     "deviceType":"",
     *     "pageNum":1,
     *     "pageSize":10,
     *     "total":2
     * }
     * @responseDemo
     *返回参数data下字段解析：
     *     tenantId：客户id
     *     deviceCode：设备编码
     *     deviceName：设备名称
     *     deviceStatus：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
     *     deviceType：设备类型
     *     deviceTypeName：设备类型名称
     *     provinceCode：省份编码
     *     provinceName：省份名称
     *     cityCode：地市编码
     *     cityName：地市名称
     *     countyCode：区县名称
     *     countyName：区县名称
     *     townCode：乡镇名称
     *     townName：乡镇名称
     *     height：设备挂高，单位：米
     *     longitude：经度
     *     latitude：纬度
     *     location：设备地址
     *     isMonitor：是否收藏 0未收藏 1已收藏
     *     orgName：设备所属组织名称
     *
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1690859578643,
     *     "data": {
     *         "endRow":2,
     *         "hasNextPage":false,
     *         "list":[{
     *          "tenantId": "121119862",
     *          "deviceCode": "51101100831377000001",
     *          "deviceName": "永永福2",
     *          "deviceStatus": "1",
     *          "deviceType": "4002",
     *          "deviceTypeName": "喇叭",
     *          "provinceCode": "110000",
     *          "provinceName": "北京市",
     *          "cityCode": "110100",
     *          "cityName": "北京市",
     *          "countyCode": "110102",
     *          "countyName": "西城区",
     *          "townCode": "110102011000",
     *          "townName": "金融街街道",
     *          "height": "10.0",
     *          "isMonitor": "0",
     *          "longitude": "116.369951837",
     *          "latitude": "39.921861624",
     *          "location": "北京市西城区金融街街道砖塔胡同68号砖塔胡同关帝庙",
     *          "orgName": "测试客户zbz-通用平台版本-1.0",
     *         },{}],
     *         "pageNum":1,
     *         "pageSize":10,
     *         "total":2
     *     }
     * }
     */
    @PostMapping("/hornDevice/queryHornDevicePage")
    public Result<PageInfo<HornDeviceVo>> queryHornDevicePage(@RequestBody HornDeviceDTO hornDeviceDTO){
        return iHornDeviceServiceMigrate.queryHornDevicePage(hornDeviceDTO);
    }

    /**
     * 查询无人机管理列表
     * @apiNote 查询无人机管理列表
     * @apiVersion 3.47
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"",
     *     "deviceName":"",
     *     "deviceStatus":"",
     *     "manufacturerCode":"",
     *     "modelCode":"",
     *     "deviceType":"",
     *     "tenantId":"",
     *     "deptProvinceCity":[],
     *     "locationCode":"",
     *     "orderStatus":"0",
     *     "needDevice":false,
     *     "deptIds":[],
     *     "pageNum":1,
     *     "pageSize":10,
     *     "total":0
     * }
     * @responseDemo
     *返回参数data下字段解析：
     *     channelList：通道集合
     *     deviceCode：设备编码
     *     deviceName：设备名称
     *     deviceStatus：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
     *     deviceType：设备类型
     *     deviceTypeName：设备类型名称
     *     isMonitor：是否收藏 0未收藏 1已收藏
     *     longitude：经度
     *     latitude：纬度
     *     location：设备地址
     *     manufacturerCode：厂家编码
     *     manufacturerName：厂家名称
     *     modelCode：型号编码
     *     modelName：型号名称
     *     orderStatus：订购状态
     *     orderStatusName：订购状态名称
     *     orgName：设备所属组织名称
     *     provinceCode：省份编码
     *     provinceName：省份名称
     *     cityCode：地市编码
     *     cityName：地市名称
     *     countyCode：区县名称
     *     countyName：区县名称
     *     townCode：乡镇名称
     *     townName：乡镇名称
     * {
     *     "code": 200,
     *     "msg": "查询成功",
     *     "rows": [{
     *          "channelList":[{parentDeviceCode:"46900200833007000001","channelCode":"11111111111111111111","channelName":"DJI_test_03","status":"1"}],
     *          "deviceCode": "46900200833007000001",
     *          "deviceName": "琼海牛路岭12",
     *          "deviceStatus": "1",
     *          "deviceType": "100",
     *          "deviceTypeName": "无人固定翼飞机",
     *          "isMonitor":0,
     *          "latitude":40.119801164,
     *          "longitude":116.498667573,
     *          "location":"辽宁省大连市甘井子区七贤岭街道大连腾飞园区园区广场中心点飞来飞去看看风景",
     *          "manufacturerCode":"dji",
     *          "manufacturerName":"大疆",
     *          "modelCode":"300",
     *          "modelName":"Matrice 350 RTK",
     *          "orderStatus":0,
     *          "orderStatusName":"有效",
     *          "orgName":"一省多市",
     *          "provinceCode":"110000",
     *          "provinceName":"北京市",
     *          "cityCode":"110100",
     *          "cityName":"北京市",
     *          "countyCode":"110113",
     *          "countyName":"顺义区",
     *          "townCode":"110113101000",
     *          "townName":"高丽营镇"
     *     },{}]
     *     "total":3
     * }
     */
    @PostMapping("/uav/device/qryUavDeviceListPage")
    public TableDataInfo qryUavDeviceListPage(@RequestBody UavDeviceDTO uavDeviceDTO){
        return iHornDeviceServiceMigrate.qryUavDeviceListPage(uavDeviceDTO);
    }

    /**
     * 查询物联设备管理列表
     * @apiNote 查询物联设备管理列表
     * @apiVersion 3.48
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"",
     *     "deviceName":"",
     *     "deviceStatus":"",
     *     "modelCode":"",
     *     "deptId":"",
     *     "deptIds":[],
     *     "orderStatus":"1",
     *     "hasLinkConf":"",
     *     "custCode":"",
     *     "deviceType":"",
     *     "pageNum":1,
     *     "pageSize":10,
     *     "total":0
     * }
     * @responseDemo
     *返回参数data下字段解析：
     *     tenantId：客户id
     *     categoryCode：设备类型
     *     deviceCode：设备编码
     *     deviceName：设备名称
     *     deviceStatus：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
     *     hasLinkConf：是否配置了算法 1是0否
     *     deviceType：设备类型
     *     deviceTypeName：设备类型名称
     *     isMonitor：是否收藏 0未收藏 1已收藏
     *     longitude：经度
     *     latitude：纬度
     *     location：设备地址
     *     modelCode：型号编码
     *     modelName：型号名称
     *     orderStatus：订购状态
     *     orgName：设备所属组织名称
     *     provinceCode：省份编码
     *     provinceName：省份名称
     *     cityCode：地市编码
     *     cityName：地市名称
     *     countyCode：区县名称
     *     countyName：区县名称
     *     townCode：乡镇名称
     *     townName：乡镇名称
     * {
     *     "code": 200,
     *     "rows": [{
     *          "tenantId":"122035797",
     *          "categoryCode":"10010123140052",
     *          "deviceCode": "10010123140052",
     *          "deviceName": "动物园站1",
     *          "deviceStatus": 1,
     *          "hasLinkConf":"1",
     *          "deviceType": "401000020001",
     *          "deviceTypeName": "无人固定翼飞机",
     *          "isMonitor":0,
     *          "latitude":"0.000000",
     *          "longitude":"0.000000",
     *          "location":"北京市朝阳区三里屯街道想我么(通盈中心店)北京三里屯通盈中心洲际酒店",
     *          "modelCode":"dahua",
     *          "modelName":"大华",
     *          "orderStatus":"0",
     *          "orgName":"测试1",
     *          "provinceCode":"110000",
     *          "provinceName":"北京市",
     *          "cityCode":"110100",
     *          "cityName":"北京市",
     *          "countyCode":"110113",
     *          "countyName":"顺义区",
     *          "townCode":"110113101000",
     *          "townName":"高丽营镇"
     *     },{}]
     *     "total":3
     * }
     */
    @PostMapping("/iotdevice/queryIotDevicePage")
    public TableDataInfo queryIotDevicePage(@RequestBody IotDeviceVO deviceInfoVO){
        return iHornDeviceServiceMigrate.queryIotDevicePage(deviceInfoVO);
    }

    /**
     * 查询物联设备管理列表
     * @apiNote 查询物联设备管理列表
     * @apiVersion 3.48
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"",
     *     "deviceName":"",
     *     "deviceStatus":"",
     *     "modelCode":"",
     *     "deptId":"",
     *     "deptIds":[],
     *     "orderStatus":"1",
     *     "hasLinkConf":"",
     *     "custCode":"",
     *     "deviceType":"",
     *     "pageNum":1,
     *     "pageSize":10,
     *     "total":0
     * }
     * @responseDemo
     *返回参数data下字段解析：
     *     tenantId：客户id
     *     categoryCode：设备类型
     *     deviceCode：设备编码
     *     deviceName：设备名称
     *     deviceType：设备类型
     *     deviceTypeName：设备类型名称
     *     isMonitor：是否收藏 0未收藏 1已收藏
     *     longitude：经度
     *     latitude：纬度
     *     location：设备地址
     *     modelCode：型号编码
     *     modelName：型号名称
     *     orderStatus：订购状态
     *     orgName：设备所属组织名称
     *     provinceCode：省份编码
     *     provinceName：省份名称
     *     cityCode：地市编码
     *     cityName：地市名称
     *     countyCode：区县名称
     *     countyName：区县名称
     *     townCode：乡镇名称
     *     townName：乡镇名称
     * {
     *     "code": 200,
     *     "data": [{
     *          "tenantId":"122035797",
     *          "categoryCode":"10010123140052",
     *          "deviceCode": "10010123140052",
     *          "deviceName": "动物园站1",
     *          "deviceStatus": 1,
     *          "hasLinkConf":"1",
     *          "deviceType": "401000020001",
     *          "deviceTypeName": "无人固定翼飞机",
     *          "isMonitor":0,
     *          "latitude":"0.000000",
     *          "longitude":"0.000000",
     *          "location":"北京市朝阳区三里屯街道想我么(通盈中心店)北京三里屯通盈中心洲际酒店",
     *          "modelCode":"dahua",
     *          "modelName":"大华",
     *          "orderStatus":"0",
     *          "orgName":"测试1",
     *          "provinceCode":"110000",
     *          "provinceName":"北京市",
     *          "cityCode":"110100",
     *          "cityName":"北京市",
     *          "countyCode":"110113",
     *          "countyName":"顺义区",
     *          "townCode":"110113101000",
     *          "townName":"高丽营镇"
     *     },{}]
     *     "total":3
     * }
     */
    @PostMapping("/iotdevice/queryIotRadarDeviceList")
    public Result<List<IotDeviceVO> > queryIotRadarDeviceList(@RequestBody IotDeviceVO deviceInfoVO) {
        return iHornDeviceServiceMigrate.queryIotRadarDeviceList(deviceInfoVO);
    }

    /**
     * 查询雷达管理列表
     * @apiNote 查询雷达管理列表
     * @apiVersion 3.49
     * @tag 三、设备能力
     * @requestDemo
     * {
     *     "deviceCode":"",
     *     "deviceName":"",
     *     "deviceStatus":"",
     *     "modelCode":"",
     *     "deptId":"",
     *     "deptIds":[],
     *     "orderStatus":"",
     *     "hasLinkConf":"",
     *     "custCode":"",
     *     "pageNum":1,
     *     "pageSize":10,
     *     "total":0
     * }
     * @responseDemo
     *返回参数data下字段解析：
     *     tenantId：客户id
     *     tenantName：客户名称
     *     categoryCode：设备类型
     *     deviceCode：设备编码
     *     deviceModelCode：设备型号编码
     *     deviceName：设备名称
     *     deviceStatus：设备状态:0-离线、1-空闲、2-工作中、3-故障、4-从未连接
     *     isMonitor：是否收藏 0未收藏 1已收藏
     *     height：设备挂高，单位：米
     *     longitude：经度
     *     latitude：纬度
     *     location：设备地址
     *     modelCode：型号编码
     *     modelName：型号名称
     *     orderStatus：订购状态
     *     orgName：设备所属组织名称
     *     provinceId：省份编码
     *     provinceName：省份名称
     *     cityId：地市编码
     *     cityName：地市名称
     *     countyId：区县名称
     *     countyName：区县名称
     *     townCode：乡镇名称
     *     townName：乡镇名称
     *
     * {
     *     "code": 200,
     *     "rows": [{
     *          "tenantId":"122035797",
     *          "tenantName":"测试客户zbz",
     *          "categoryCode":1,
     *          "deviceCode": "10113020300111",
     *          "deviceModelCode":"12321qW",
     *          "deviceName": "GNSS监测站11",
     *          "deviceStatus": 0,
     *          "height": "45.00",
     *          "isMonitor":"0",
     *          "latitude":"39.659076000",
     *          "longitude":"119.072425000",
     *          "location":"河北省秦皇岛市昌黎县龙家店镇致远航空",
     *          "modelCode":"hikvision",
     *          "modelName":"海康威视",
     *          "orderStatus":"3",
     *          "orgName":"测试1",
     *          "provinceId":"130000",
     *          "provinceName":"河北省",
     *          "cityId":"130300",
     *          "cityName":"秦皇岛市",
     *          "countyId":"130322",
     *          "countyName":"昌黎县",
     *          "townCode":"130322103000",
     *          "townName":"龙家店镇"
     *     },{}]
     *     "total":1
     * }
     */
    @PostMapping("/radar/queryRadarListPage")
    public TableDataInfo queryRadarListPage(@RequestBody IotDeviceVO deviceInfoVO){
        return iHornDeviceServiceMigrate.queryRadarListPage(deviceInfoVO);
    }

    @PostMapping("/hornTask/device/callHornTaskPlay")
    public Result<String> callHornTaskPlay(@RequestBody List<String> fileIdList) {
        return iHornDeviceServiceMigrate.callHornTaskPlay(fileIdList);
    }

    @PostMapping("/hornTask/device/syncTaskInfoToAlgorithmMongo")
    public Result<String> syncTaskInfoToAlgorithmMongo(@RequestBody Long taskId) {
        return iHornDeviceServiceMigrate.syncTaskInfoToAlgorithmMongo(taskId);
    }

    @PostMapping("/horn/content/add")
    public Result<String> addHornContent(@RequestBody VmHornContentVO param) {
        return iHornDeviceServiceMigrate.addHornContent(param);
    }

    @PostMapping("/horn/content/update")
    public Result<String> updateHornContent(@RequestBody VmHornContentVO param) {
        return iHornDeviceServiceMigrate.updateHornContent(param);
    }

    @PostMapping("/horn/content/audit")
    public Result<String> auditHornContent(@RequestBody VmHornContentVO param) {
        return iHornDeviceServiceMigrate.auditHornContent(param);
    }

    @PostMapping("/horn/content/delete")
    public Result<String> deleteHornContent(@RequestBody List<String> ids) {
        return iHornDeviceServiceMigrate.deleteHornContent(ids);
    }

    @PostMapping("/hornTask/device/addHornTask")
    public Result<String> addHornTask(@RequestBody HornTaskConfVO param) {
        return iHornDeviceServiceMigrate.addHornTask(param);
    }

    @PostMapping("/hornTask/device/addAndStartHornTask")
    public Result<String> addAndStartHornTask(@RequestBody HornTaskConfVO param) {
        return iHornDeviceServiceMigrate.addAndStartHornTask(param);
    }

    @PostMapping("/hornTask/device/addHornTaskV2")
    public Result<String> addHornTaskV2(@RequestBody HornTaskConfVO param) {
        return iHornDeviceServiceMigrate.addHornTaskV2(param);
    }

    @PostMapping("/hornTask/device/deleteHornTask/v2")
    public Result<String> deleteHornTaskV2(@RequestBody HornTaskConfVO param) {
        return iHornDeviceServiceMigrate.deleteHornTaskV2(param);
    }

    @PostMapping("/horn/content/list")
    public Result<PageInfo<VmHornContentConfDTO>> getContentList(@RequestBody VmHornContentConfRequestListDTO param) {
        return iHornDeviceServiceMigrate.list(param);
    }

    @PostMapping("/horn/content/audition")
    public Result<HornAuditionResultVO> audition(@RequestBody VmHornAuditionRequestDTO param) {
        return iHornDeviceServiceMigrate.audition(param);
    }

    @PostMapping("/hornTask/device/list")
    public TableDataInfo getTaskList(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return iHornDeviceServiceMigrate.getList(hornTaskConfVO);
    }

    @PostMapping("/hornTask/device/startHornTask")
    public Result<String> startTask(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return iHornDeviceServiceMigrate.startTask(hornTaskConfVO);
    }

    @PostMapping("/hornTask/device/playRightNow")
    public Result<String> playRightNow(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return iHornDeviceServiceMigrate.playRightNow(hornTaskConfVO);
    }

    @PostMapping("/hornTask/device/stopHornTask")
    public Result<String> stopHornTask(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return iHornDeviceServiceMigrate.stopHornTask(hornTaskConfVO);
    }

    @PostMapping("/hornTask/device/deleteHornTask")
    public Result<String> deleteHornTask(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return iHornDeviceServiceMigrate.deleteHornTask(hornTaskConfVO);
    }

    @PostMapping("/horn/content/getListByContentType")
    Result<PageInfo<VmHornContentConfDTO>> getListByContentType(@RequestBody VmHornContentRequestDTO param) {
        return iHornDeviceServiceMigrate.getListByContentType(param);
    }
}
