package com.chinatower.device.controller;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.dataobject.DeviceInfoDO;
import com.chinatower.device.dto.ScopeDeviceInfoVO;
import com.chinatower.device.dto.ComprehensiveDeviceStatisticsVO;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.DeviceComprehensiveVO;
import com.chinatower.device.dto.QueryRecordPlanCheckDTO;
import com.chinatower.device.dto.RecordPlanCheckDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.repository.IDeviceInfoRepository;
import com.chinatower.device.service.IPolygonPatchService;
import com.chinatower.device.service.IVideoDeviceInfoService;
import com.chinatower.industry.dto.DeviceIndDTO;
import com.chinatower.industry.dto.DeviceLayerReq;
import com.chinatower.industry.dto.IndustryAlgorithmDTO;
import com.chinatower.industry.vo.*;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.QueryStorageDayVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.entity.SnapShotSimpleRes;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * video-device
 * @ClassName VideoDeviceController
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/7 15:22
 * @Version 1.0
 **/
@RestController
@Slf4j
@Api(value = "设备相关信息模块", tags = "设备相关信息模块")
public class VideoDeviceController extends TowerBaseController {

    private static final String DEPRECATED_MSG = "废弃接口调用";

    @Autowired
    private IVideoDeviceInfoService videoDeviceInfoService;

    @Autowired
    private IDeviceInfoRepository deviceInfoRepository;

    @Autowired
    private IPolygonPatchService iPolygonPatchService;

    @Autowired
    private SysAreaRootService sysAreaRootService;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    public static final String LOCALIZATION_FLAG = "localization_flag";

    public static final String LOCALIZATION_FLAG_ERROR = "获取国产化状态：{}";

    /**
     * 视频树查询
     * 视频树查询【前端在用】
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/video/getMonitorTree")
    public Result<List<DeviceTreeVO>> getMonitorTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        Integer queryType = deviceReqDTO.getQueryType();
        List<DeviceTreeVO> list = new ArrayList<>();
        if(StringUtils.isEmpty(deviceReqDTO.getTenantId())){
            SysUser user = SecurityUtils.getLoginUser().getUser();
            deviceReqDTO.setTenantId(user.getTenantId());
            deviceReqDTO.setIndustryCode(user.getIndustryCode());
        }
        if(queryType == null){
            list = videoDeviceInfoService.getMonitorTree(deviceReqDTO);
        }else{
            if (queryType == Constants.ONE_NUM) {
                list = videoDeviceInfoService.getMonitorList(deviceReqDTO);
            }else if (queryType == Constants.TWO_NUM) {
                list = videoDeviceInfoService.getMonitorOrgTree(deviceReqDTO);
            }else if (queryType == Constants.THREE_NUM) {
                list = videoDeviceInfoService.getMonitorLabelTree(deviceReqDTO);
            }else if (queryType == Constants.FOUR_NUM) {
                list = videoDeviceInfoService.getMonitorLabelTreeNew(deviceReqDTO);
            }
        }
        if (!list.isEmpty()) {
            list.get(0).setFirst(1);
        }
        return Result.data(list);
    }

    /**
     * 根据图层id查询设备树
     * @param req
     * @return
     */
    @PostMapping("/video/getMonitorTreeByLayerId")
    public Result<List<DeviceTreeVO>> getMonitorTreeByLayerId(@RequestBody MonitorTreeByLayerIdReq req) {
        log.info("VideoDeviceController-getMonitorTreeByLayerId-req:{}", JSON.toJSONString(req));
        if(CollectionUtils.isEmpty(req.getLayerIds())){
            return Result.data(new ArrayList<>());
        }
        DeviceReqDTO deviceReqDTO = new DeviceReqDTO();
        LoginUser currentUser = SecurityUtils.getLoginUser();
        //根据图层id、租户、行业查询图层关联得设备id
        List<String> deviceCodes = getDeviceCodesByLayerId(req);
        if (CollectionUtils.isEmpty(deviceCodes)){
            return Result.data(new ArrayList<>());
        }
        deviceReqDTO.setDeviceCodes(deviceCodes);
        deviceReqDTO.setTenantId(currentUser.getUser().getTenantId());
        deviceReqDTO.setIndustryCode(currentUser.getUser().getIndustryCode());
        Integer queryType = deviceReqDTO.getQueryType();
        List<DeviceTreeVO> list = videoDeviceInfoService.getMonitorTree(deviceReqDTO);
        if (!list.isEmpty()) {
            list.get(0).setFirst(1);
        }
        log.info("VideoDeviceController-getMonitorTreeByLayerId-resp:{}", JSON.toJSONString(list));
        return Result.data(list);
    }

    private List<String> getDeviceCodesByLayerId(MonitorTreeByLayerIdReq req) {
        if (CollectionUtils.isEmpty(req.getLayerIds())) {
            return new ArrayList<>();
        }
        LoginUser currentUser = SecurityUtils.getLoginUser();
        return iPolygonPatchService.getDeviceCodeByLayerId(
                req.getLayerIds(),
                currentUser.getUser().getIndustryCode(),
                currentUser.getUser().getTenantId()
        );
    }

    /**
     * 根据图层id获取关联的设备（林业-草原资源使用）
     * @param req
     * @return
     */
    @PostMapping("/video/getDevicesByLayerId")
    public Result<List<String>> getDevicesByLayerId(@RequestBody MonitorTreeByLayerIdReq req) {
        log.info("VideoDeviceController-getDevicesByLayerId-req:{}", JSON.toJSONString(req));
        List<String> deviceCodes = getDeviceCodesByLayerId(req);
        return Result.data(deviceCodes);
    }

    /**
     * 废弃接口
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/video/getMonitorList")
    @Deprecated
    public Result<List<DeviceTreeVO>> getMonitorList(@RequestBody DeviceReqDTO deviceReqDTO) {
        log.info(DEPRECATED_MSG);
        List<DeviceTreeVO> list = videoDeviceInfoService.getMonitorList(deviceReqDTO);
        if (!list.isEmpty()) {
            list.get(0).setFirst(1);
        }
        return Result.data(list);
    }

    /**
     * 视频组织树查询
     * 视频组织树查询【前端在用】
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/video/getMonitorOrgTree")
    @Deprecated
    public Result<List<DeviceTreeVO>> getMonitorOrgTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        log.info(DEPRECATED_MSG);
        List<DeviceTreeVO> list = videoDeviceInfoService.getMonitorOrgTree(deviceReqDTO);
        if (!list.isEmpty()) {
            list.get(0).setFirst(1);
        }
        return Result.data(list);
    }


    /**
     * 废弃接口
     * 设备/通道/预置点/监控区域/算法关系推送【订购在用】 该接口已废弃（算法仓删除了该接口）
     * @return
     * @author chenmf
     * @date 2022/2/9
     * @version
     */
    @PostMapping("/video/device/synchDeviceAlgorithms")
    public Result<JSONObject> synchDeviceAlgorithms(@Validated @RequestBody List<AlgorithmParamBodyVO> param) {
        return videoDeviceInfoService.synchDeviceAlgorithms(param);
    }

    /**
     * 废弃接口
     * 批量保存和更新设备信息【应该不用了删了吧】
     * @return
     * @author chenmf
     * @date 2022/2/11
     * @version
     */
    @PostMapping("/video/device/batchSaveOrUpdateDevice")
    public Result<String> batchSaveOrUpdateDevice(@RequestBody List<SaveOrUpdateDeviceDTO> deviceDTOList) {
        videoDeviceInfoService.batchSaveOrUpdateDevice(deviceDTOList);
        return Result.success("批量保存/更新设备信息成功!");
    }

    /**
     * 查询租户管理区域
     * 查询租户管理区域【前端在用】
     * @param: deviceReqDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/2/17
     * @version: 1.0
     */
    @PostMapping("/video/getTenantMonitorLevel")
    public Result<List<DeviceTreeVO>> getTenantMonitorLevel(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = videoDeviceInfoService.getTenantMonitorLevel(deviceReqDTO);
        return Result.data(list);
    }

    /**
     * 废弃接口
     * @param pageDeviceVO
     * @return
     */
    @ApiOperation("根据行业版本编码查询设备信息【应该不用了删了吧】")
    @PostMapping(value = "/video/pageDeviceByPlatform")
    public TableDataInfo pageDeviceByPlatform(@RequestBody PageDeviceVO pageDeviceVO) {
        //查询用户订购订单列表
        List<DeviceByPlatformVO> list = videoDeviceInfoService.pageDeviceByPlatform(pageDeviceVO);
        return getDataTable(list);
    }

    /**
     * 预警事件-查询站址信息
     * 预警事件-查询站址信息【前端在用】
     * @param: siteRequestDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/3/10
     * @version: 1.0
     */
    @PostMapping("/video/device/querySitesForWE")
    Result<Map<String, Object>> querySitesForWE(@RequestBody SiteRequestDTO siteRequestDTO) {
        List<SiteResultVO> siteList = deviceInfoRepository.querySitesForWE(siteRequestDTO);
        Map<String, Object> resultList = new HashMap<>(2);
        resultList.put(Constants.PARAM_LIST, siteList);
        resultList.put(Constants.PARAM_CITY_ID, siteRequestDTO.getCityId());
        return Result.data(resultList);

    }

    /**
     * 预警事件-查询摄像头集合
     * 预警事件-查询摄像头集合【前端在用】
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: chenshaowu
     * @date: 2022/3/11
     * @version: 1.0
     */
    @PostMapping("/video/device/queryDevices")
    @ApiOperation("查询摄像头集合")
    TableDataInfo queryDevices(@RequestBody SiteRequestDTO siteRequestDTO) {
        String localizationFlag = "0";
        try {
            localizationFlag = deviceInfoMapper.getSysConfig(LOCALIZATION_FLAG);
        } catch (Exception e) {
            log.info(LOCALIZATION_FLAG_ERROR, e);
        }
        if("1".equals(localizationFlag)){
            siteRequestDTO.setLocalizationFlag("1");
        }
        startPage();
        List<DeviceSiteVO> deviceSiteVOS = deviceInfoRepository.queryDevicesByName(siteRequestDTO);
        return getDataTable(deviceSiteVOS);
    }


    /**
     * 预警事件-查询摄像头集合【APP用】
     *
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/11/21
     * @version: 1.5.30
     */
    @PostMapping("/video/device/queryDevicesApp")
    @ApiOperation("APP查询摄像头集合")
    TableDataInfo queryDevicesApp(@RequestBody SiteRequestDTO siteRequestDTO) {
        Map<String,List> map = deviceInfoRepository.queryDevicesApp(siteRequestDTO);
        TableDataInfo indo = getDataTable(map.get(Constants.DEV_INFO_LIST));
        indo.setRows(map.get(Constants.LIST));
        return indo;
    }


    /**
     * 预警事件-根据设备编码查询设备信息【前端在用】
     *
     * @param: siteRequestDTO
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/3/11
     * @version:
     */
    @PostMapping("/video/device/queryDeviceForWE")
    @ApiOperation("根据设备编码查询设备信息")
    public Result<DeviceChannelInfoVO> queryDevicesForWE(@RequestBody SiteRequestDTO siteRequestDTO) {
        String localizationFlag = "0";
        try {
            localizationFlag = deviceInfoMapper.getSysConfig(LOCALIZATION_FLAG);
        } catch (Exception e) {
            log.info(LOCALIZATION_FLAG_ERROR, e);
        }
        if("1".equals(localizationFlag)){
            siteRequestDTO.setLocalizationFlag("1");
        }
        return Result.data(deviceInfoRepository.queryDevicesForWE(siteRequestDTO));
    }


    /**
     * 查询通道列表【App】
     *
     * @param: siteRequestDTO
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/3/11
     * @version:
     */
    @PostMapping("/video/device/queryChannnelInfoForApp")
    @ApiOperation("查询通道列表")
    public TableDataInfo queryChannnelInfoForApp(@RequestBody SiteRequestDTO siteRequestDTO) {
        return getDataTable(deviceInfoRepository.queryChannnelInfoForApp(siteRequestDTO));
    }

    @PostMapping("/video/device/querySnapShotSimpleForAppByChannelCode")
    @ApiOperation("根据通道查询简约模式抓图列表")
    public Result<Page<SnapShotSimpleRes>> querySnapShotSimpleForAppByChannelCode(@RequestBody SiteRequestDTO siteRequestDTO) {
        logger.info("根据通道查询简约模式抓图列表入参：{}", JSONObject.toJSONString(siteRequestDTO) );
        Page<SnapShotSimpleRes> page = deviceInfoRepository.querySnapShotSimpleForAppByChannelCode(siteRequestDTO);
        return Result.data(page);
    }


    /**
     * 查询摄像头列表,只返回设备名称、编码
     * 查询摄像头列表,只返回设备名称、编码【前端在用】
     * @param: siteRequestDTO
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/3/11
     * @version:
     */
    @PostMapping("/video/device/getDeviceList")
    public Result<List<DeviceInfoDO>> getDeviceList(@RequestBody SiteRequestDTO siteRequestDTO) {
        siteRequestDTO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        return Result.data(deviceInfoRepository.getDeviceList(siteRequestDTO));
    }

    /**
     * 查询物联设备列表，只返回编号、名称【下拉筛选】
     * @param siteRequestDTO
     * @return Result
     * @author lyy
     * @date: 2024/01/08
     * @version v1.6.40
     */
    @PostMapping("/video/device/getIotDeviceList")
    public Result<List<IotDeviceVO>> getIotDeviceList(@RequestBody SiteRequestDTO siteRequestDTO) {
        siteRequestDTO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        return Result.data(deviceInfoRepository.getIotDeviceList(siteRequestDTO));
    }

    /**
     * 查询通道录像计划校验
     * 查询通道录像计划校验（返回冲突的设备通道列表和录像保存天数列表）【前端在用】
     * @param queryRecordPlanCheckDTO
     * @return
     * @author: chenmf
     * @date: 2022/3/13
     */
    @PostMapping("/video/queryRecordPlanCheck")
    public Result<RecordPlanCheckDTO> queryRecordPlanCheck(@RequestBody QueryRecordPlanCheckDTO queryRecordPlanCheckDTO) {
        return Result.data(deviceInfoRepository.queryRecordPlanCheck(queryRecordPlanCheckDTO));
    }

    /**
     * 故障告警消息报文【告警逻辑在用】
     * @param deviceErrorVO
     * @return
     * @author: sutn
     * @date: 2022/3/15
     */
    @PostMapping("/video/message/insertDeviceError")
    public Result insertDeviceError(@RequestBody DeviceErrorVO deviceErrorVO) {
        return deviceInfoRepository.insertDeviceError(deviceErrorVO);
    }

    /**
     * 根据网格员定位经纬度查询周边站址
     * 根据网格员定位经纬度查询周边站址（for app）【app在用】
     * @param siteRequestDTO
     * @return Result
     * @auther: chenshaowu
     * @date: 2022/3/16
     * @version: 1.0
     */
    @PostMapping("/video/warningEvent/querySitesByLocal")
    public Result<List<SiteResultVO>> querySitesByLocal(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(deviceInfoRepository.querySitesByLocal(siteRequestDTO));
    }

    /**
     * 获取视频存储时长
     * @param queryStorageDayVO
     * @return
     */
    @ApiOperation(value = "获取视频存储时长", notes = "获取视频存储时长【前端在用】")
    @PostMapping("/video/player/queryRecordPlanStorageDay")
    public Result<String> queryRecordPlanStorageDay(@RequestBody QueryStorageDayVO queryStorageDayVO) {
        String storageTime = videoDeviceInfoService.queryRecordPlanStorageDay(queryStorageDayVO);
        return Result.data(storageTime);
    }

    /**
     * 获取标签设备树
     * @param deviceReqDTO
     * @return
     */
    @ApiOperation(value = "获取标签设备树", notes = "获取标签设备树【前端在用】")
    @PostMapping("/video/getMonitorLabelTree")
    @Deprecated
    public Result<List<DeviceTreeVO>> getMonitorLabelTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        log.info(DEPRECATED_MSG);
        List<DeviceTreeVO> list = videoDeviceInfoService.getMonitorLabelTree(deviceReqDTO);
        if (!list.isEmpty()) {
            list.get(0).setFirst(1);
        }
        return Result.data(list);
    }

    /**
     * 获取标签设备树,增加标签类型
     * @param deviceReqDTO
     * @return
     */
    @ApiOperation(value = "获取标签设备树", notes = "获取标签设备树NEW-增加标签类型")
    @PostMapping("/video/getMonitorLabelTreeNew")
    @Deprecated
    public Result<List<DeviceTreeVO>> getMonitorLabelTreeNew(@RequestBody DeviceReqDTO deviceReqDTO) {
        log.info(DEPRECATED_MSG);
        List<DeviceTreeVO> list = videoDeviceInfoService.getMonitorLabelTreeNew(deviceReqDTO);
        if (!list.isEmpty()) {
            list.get(0).setFirst(1);
        }
        return Result.data(list);
    }

    /**
     * 查询联动周边指定范围内的设备
     * 查询联动周边指定范围内的设备【前端在用】
     * @param deviceReqDTO
     * @return Result
     * @author Chensw
     * @date 2022/5/7 14:09
     */
    @ApiOperation("查询联动周边指定范围内的设备")
    @PostMapping("/video/device/linkageDeviceList")
    @Deprecated
    public Result linkageDeviceList(@RequestBody DeviceReqDTO deviceReqDTO) {
        log.info(DEPRECATED_MSG);
        return result(videoDeviceInfoService.linkageDevice(deviceReqDTO));
    }

    /**
     * 废弃接口
     */
    @PostMapping("/video/queryVideoDetail")
    @Deprecated
    public Result<DeviceDetailVO> queryVideoDetail(@RequestBody DeviceTreeVO deviceTreeVO) {
        log.info(DEPRECATED_MSG);
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail("无法获取当前登录人");
        }
        return Result.data(videoDeviceInfoService.queryVideoDetail(deviceTreeVO,loginUser));
    }


    /**
     * 根据设备编码查询设备详情接口
     * @param deviceReqDTO
     * @return Result
     * @author Chensw
     * @date 2022/5/7 14:09
     */
    @ApiOperation("根据设备编码查询设备详情接口")
    @PostMapping("/video/device/queryDeviceInfo")
    @Deprecated
    public Result queryDeviceInfo(@RequestBody DeviceReqDTO deviceReqDTO) {
        log.info(DEPRECATED_MSG);
        return Result.data(videoDeviceInfoService.queryDeviceInfo(deviceReqDTO));
    }

    /**
     * 查询站址集合
     * 大屏设备打点，新接口，返回站址，站址下放设备list
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: hued
     * @date: 2023年8月7日11:46:50
     * @version: release_v1.6.10
     */
    @PostMapping("/video/device/querySiteCodeAndDevices")
    @ApiOperation("查询站址集合")
    public Result<List<IndustryDeviceTree>> querySiteCodeAndDevices(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(deviceInfoRepository.querySiteCodeAndDevices(siteRequestDTO));
    }

    /**
     * 查询站址集合（for 组件大屏）
     * 大屏设备打点，新接口，返回站址，站址下放设备list （for 组件大屏）
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: hued
     * @date: 2023年8月7日11:46:50
     * @version: release_v1.6.10
     */
    @PostMapping("/video/device/querySiteCodeAndDevicesForComponent")
    @ApiOperation("查询站址集合")
    public Result<List<IndustryDeviceTree>> querySiteCodeAndDevicesForComponent(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(deviceInfoRepository.querySiteCodeAndDevicesForComponent(siteRequestDTO));
    }


    /**
     * 看这里
     * 看这里返回目标点位半径r内的设备，并自动播放n路视频
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: hued
     * @date: 2023年8月7日11:46:50
     * @version: release_v1.6.10
     */
    @PostMapping("/video/device/lookHere")
    @ApiOperation("看这里")
    public Result<DetectResultVO> lookHere(@RequestBody DeviceLayerReq deviceLayerReq) {
        return Result.data(deviceInfoRepository.lookHere(deviceLayerReq));
    }

    /**
     * 设备综合查询
     * 设备综合查询,根据设备编码精确查询或根据设备名称模糊查询
     *
     * @param:
     * @return: Result
     * @auther: luo.zhenghong
     * @date: 2024年4月2日11:46:50
     * @version: release_v1.6.50
     */
    @PostMapping("/video/device/queryDeviceComprehensive")
    @ApiOperation("设备综合查询")
    public Result<PageInfo<DeviceComprehensiveVO>> queryDeviceComprehensive(@RequestBody @Validated DeviceComprehensiveReq deviceComprehensiveReq) {
        return videoDeviceInfoService.queryDeviceComprehensive(deviceComprehensiveReq);
    }


    /**
     * 查询范围内距离最近的设备，以树的形式返回
     * @param deviceReqDTO
     * @return
     */
    @ApiOperation("查询范围内距离最近的设备，以树的形式返回")
    @PostMapping("/video/device/linkageDeviceTree")
    public Result<List<DeviceTreeVO>> linkageDeviceTree(@RequestBody DeviceReqDTO deviceReqDTO) {

        List<DeviceTreeVO> monitorTree = videoDeviceInfoService.linkageDeviceTree(deviceReqDTO);
        // 补充中国根节点
        DeviceTreeVO root = new DeviceTreeVO();
        sysAreaRootService.createRootTreeVO(root);
        root.setList(monitorTree);
        root.setOnlineNum(root.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
        root.setNum(root.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if( monitorTree != null && monitorTree.size() > DeviceConstants.ZERO_NUM )
        {
            rootTree.add(root);
        }
        return Result.data(rootTree);
    }

    /**
     * 查询摄像机基本信息
     * @param deviceCodes
     * @return
     */
    @ApiOperation("根据deviceCode数组查询device基本信息")
    @PostMapping("/video/device/getDeviceListByCodes")
    public Result<List<DeviceIndDTO>> getDeviceListByCodes(@RequestBody List<String> deviceCodes) {
        List<DeviceIndDTO> devices = videoDeviceInfoService.getDeviceListByCodes(deviceCodes);
        return Result.data(devices);
    }

    /**
     * 林业根据用户获取可查阅的摄像机树
     * @return
     */
    @PostMapping("/video/device/getDeviceTreeForestry")
    Result<List<TreeSelect>> getDeviceTreeForestry(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
        log.info("林业调用，请求加工厂树入参：{}", woodProcessingPlantDeivceVO);
        return videoDeviceInfoService.getDeviceTreeForestry(woodProcessingPlantDeivceVO);
    }

    /**
     * 林业查询木材加工厂摄像机
     * @param woodProcessingPlantDeivceVO
     * @return
     */
    @PostMapping("/video/device/getWoodProcessingPlantDeivceInfo")
    Result<List<WoodProcessingPlantDeivceVO>> getWoodProcessingPlantDeivceInfo (@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
        log.info("林业调用，请求加工厂摄像机数组入参：{}", woodProcessingPlantDeivceVO);
        try {
            List<WoodProcessingPlantDeivceVO> List = videoDeviceInfoService.getWoodProcessingPlantDeivceInfo(woodProcessingPlantDeivceVO);
            return Result.data(List);
        } catch (Exception e) {
            return Result.fail("查询出错。");
        }
    }
    @PostMapping("/video/device/getWoodProcessingPlantDeivceInfoPage")
    Result<PageInfo<WoodProcessingPlantDeivceVO>> getWoodProcessingPlantDeivceInfoPage(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
        log.info("林业调用，请求加工厂列表入参：{}", woodProcessingPlantDeivceVO);
        try {
            PageInfo<WoodProcessingPlantDeivceVO> List = videoDeviceInfoService.getWoodProcessingPlantDeivceInfoPage(woodProcessingPlantDeivceVO);
            return Result.data(List);
        } catch (Exception e) {
            return Result.fail("查询出错。");
        }
    }

    /**
     * 查询设备告警数量
     * 根据deviceCode请求设备告警数量
     * @param woodProcessingPlantDeivceVO
     * @return
     */
    @PostMapping("/video/device/getAlarmNumByDevice")
    Result<List<WoodProcessingPlantDeivceVO>> getAlarmNumByDevice(@RequestBody WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO) {
        log.info("根据deviceCode请求设备告警数量：{}", woodProcessingPlantDeivceVO);
        return Result.data(videoDeviceInfoService.getAlarmNumByDevice(woodProcessingPlantDeivceVO));
    }

    /**
     * 视频树查询根据组织id
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/video/getMonitorTreeByDept")
    public Result<List<DeviceTreeVO>> getMonitorTreeByDept(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(deviceReqDTO.getDeptIds())){
            List<String> deviceCodeList = videoDeviceInfoService.queryDeviceCodeList(deviceReqDTO);
            deviceReqDTO.setDeviceCodes(deviceCodeList);
            list = videoDeviceInfoService.getMonitorTree(deviceReqDTO);
        }else{
            return Result.fail("部门编码不能为空！");
        }
        if (!list.isEmpty()) {
            list.get(0).setFirst(1);
        }
        return Result.data(list);
    }

    /**
     * 摄像机行政区划树查询，第一步：查询行政区划
     * 由于济南属地摄像机行政区划树、摄像机标签树页面渲染过慢的原因，新增了getMonitorTreeStepOne接口，采用分步查询方案，处理摄像机行政区划树、摄像机标签树加载慢问题，对其他属地及功能没有影响
     */
    @PostMapping("/video/getMonitorTreeStepOne")
    public Result<List<DeviceTreeVO>> getMonitorTreeStepOne(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = videoDeviceInfoService.getMonitorTreeStepOne(deviceReqDTO);
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if (!list.isEmpty()) {
            // 补充根节点
            String code = DeviceConstants.TOWER_ROOT_AREA;
            String name = DeviceConstants.TOWER_ROOT_AREA_NAME;
            SysArea sysArea = deviceInfoMapper.getAreaRootInfo();
            if(!ObjectUtils.isEmpty(sysArea)){
                code = sysArea.getAreaCode();
                name = sysArea.getAreaName();
            }
            DeviceTreeVO root = new DeviceTreeVO();
            root.setCode(code);
            root.setName(name);
            root.setList(list);
            root.setOnlineNum(root.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
            root.setNum(root.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
            root.setVideoNum(root.getList().stream().mapToInt(DeviceTreeVO::getVideoNum).sum());
            rootTree.add(root);
            list.get(0).setFirst(1);
        }
        return Result.data(rootTree);
    }

    /**
     * 摄像机行政区划树查询，第二步：查询摄像机
     * 由于济南属地摄像机行政区划树、摄像机标签树页面渲染过慢的原因，新增了getMonitorTreeStepTwo接口，采用分步查询方案，处理摄像机行政区划树、摄像机标签树加载慢问题，对其他属地及功能没有影响
     */
    @PostMapping("/video/getMonitorTreeStepTwo")
    public Result<List<DeviceTreeVO>> getMonitorTreeStepTwo(@RequestBody DeviceTreeVO deviceTreeVO) {
        List<DeviceTreeVO> list = videoDeviceInfoService.queryDevicesByAreaParam(deviceTreeVO);
        return Result.data(list);
    }

    /**
     * 摄像机标签树查询，第一步：查询标签
     * 由于济南属地摄像机行政区划树、摄像机标签树页面渲染过慢的原因，新增了getMonitorLabelTreeStepOne接口，采用分步查询方案，处理摄像机行政区划树、摄像机标签树加载慢问题，对其他属地及功能没有影响
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/video/getMonitorLabelTreeStepOne")
    Result<List<DeviceTreeVO>> getMonitorLabelTreeStepOne(@RequestBody DeviceReqDTO deviceReqDTO) {
        return Result.data(videoDeviceInfoService.getLabelTree(deviceReqDTO));
    }

    /**
     * 摄像机标签树查询，第二步：查询摄像机
     * 由于济南属地摄像机行政区划树、摄像机标签树页面渲染过慢的原因，新增了getMonitorLabelTreeStepTwo接口，采用分步查询方案，处理摄像机行政区划树、摄像机标签树加载慢问题，对其他属地及功能没有影响
     * @param deviceTreeVO
     * @return
     */
    @PostMapping("/video/getMonitorLabelTreeStepTwo")
    public Result<List<DeviceTreeVO>> queryDevicesByLabelParam(@RequestBody DeviceTreeVO deviceTreeVO) {
        List<DeviceTreeVO> list = videoDeviceInfoService.queryDevicesByLabelParam(deviceTreeVO);
        return Result.data(list);
    }

    /**
     * 全域2.0，区域+标签过滤设备
     * @param devicesForGlobalReq
     * @return
     */
    @PostMapping("/video/device/queryDevicesForGlobal")
    public Result<List<IndustryDeviceTree>> queryDevicesForGlobal(@RequestBody DevicesForGlobalReq devicesForGlobalReq) {
        return Result.data(videoDeviceInfoService.queryDevicesForGlobal(devicesForGlobalReq));
    }

    /**
     * 全域2.0，地图搜索
     * @param devicesForGlobalPoiReq
     * @return
     */
    @PostMapping("/video/device/queryDevicesForGlobalPoi")
    public Result<List<DevicesForGlobalPoiRes>> queryDevicesForGlobalPoi(@RequestBody DevicesForGlobalPoiReq devicesForGlobalPoiReq) {
        return Result.data(videoDeviceInfoService.queryDevicesForGlobalPoi(devicesForGlobalPoiReq));
    }

    /**
     * 全域2.0，搜索周边摄像机(站址-摄像机，树形结构))
     * @param devicesForGlobalSurroundReq
     * @return
     */
    @PostMapping("/video/device/queryDevicesForGlobalSurround")
    public Result<List<IndustryDeviceTree>> queryDevicesForGlobalSurround(@RequestBody DevicesForGlobalSurroundReq devicesForGlobalSurroundReq) {
        return Result.data(videoDeviceInfoService.queryDevicesForGlobalSurround(devicesForGlobalSurroundReq));
    }

    /**
     * 全域2.0，搜索周边摄像机（通道列表）
     * @param devicesForGlobalSurroundReq
     * @return
     */
    @PostMapping("/video/device/queryDevicesForGlobalSurroundList")
    public TableDataInfo queryDevicesForGlobalSurroundList(@RequestBody DevicesForGlobalSurroundReq devicesForGlobalSurroundReq) {
        return getDataTable(videoDeviceInfoService.queryDevicesForGlobalSurroundList(devicesForGlobalSurroundReq));
    }

    /**
     * 全域2.0，搜索框-历史记录查询
     * @return
     */
    @PostMapping("/video/device/queryHisForGlobalPoi")
    public Result<List<DevicesForGlobalPoiRes>> queryHisForGlobalPoi() {
        return Result.data(videoDeviceInfoService.queryHisForGlobalPoi());
    }

    /**
     * 全域2.0，搜索框-历史记录新增
     * @return
     */
    @PostMapping("/video/device/addHisForGlobalPoi")
    public Result<String> addHisForGlobalPoi(@RequestBody DevicesForGlobalPoiRes devicesForGlobalPoiRes) {
        return Result.success(videoDeviceInfoService.addHisForGlobalPoi(devicesForGlobalPoiRes));
    }

    /**
     * 全域2.0，搜索框-历史记录删除
     * @return
     */
    @PostMapping("/video/device/delHisForGlobalPoi")
    public Result<String> delHisForGlobalPoi(@RequestBody DevicesForGlobalPoiRes devicesForGlobalPoiRes) {
        return Result.success(videoDeviceInfoService.delHisForGlobalPoi(devicesForGlobalPoiRes));
    }

    /**
     * 图层摄像机打点聚合
     * @param devicesForGlobalReq
     * @return
     */
    @PostMapping("/video/device/queryDeviceAggregation")
    public Result<List<DeviceAggregationLayerRes>> queryDeviceAggregation(@RequestBody DevicesForGlobalReq devicesForGlobalReq) {
        return Result.data(deviceInfoRepository.queryDeviceAggregation(devicesForGlobalReq));
    }

    /**
     * 查询订购的摄像机类型
     * @return
     */
    @PostMapping("/video/device/queryOrderDeviceCategory")
    public Result<List<OrderDeviceCategoryRes>> queryOrderDeviceCategory(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(deviceInfoRepository.queryOrderDeviceCategory(siteRequestDTO));
    }

    /**
     * 查询订购的摄像机厂家
     * @return
     */
    @PostMapping("/video/device/queryOrderDeviceModel")
    public Result<List<DeviceCommonInfoVO>> queryOrderDeviceModel(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(deviceInfoRepository.queryOrderDeviceModel(siteRequestDTO));
    }

    /**
     * 全域2.0，指点飞行，分页搜索周边摄像机(摄像机-通道，树形结构)
     * @param devicesForGlobalSurroundReq
     * @return
     */
    @PostMapping("/video/device/queryDevicesForGlobalUav")
    public TableDataInfo queryDevicesForGlobalUav(@RequestBody DevicesForGlobalSurroundReq devicesForGlobalSurroundReq) {
        return getDataTable(videoDeviceInfoService.queryDevicesForGlobalUav(devicesForGlobalSurroundReq));
    }

    /**
     * 综合设备统计接口、摄像机、卫星影像、机场总数、无人机执行任务中的数量
     * @return
     */
    @GetMapping("/video/device/queryComprehensiveDeviceStatistics")
    public Result<ComprehensiveDeviceStatisticsVO> queryComprehensiveDeviceStatistics() {
        return Result.data(videoDeviceInfoService.queryComprehensiveDeviceStatistics());
    }

    /**
     * 查询权限范围内的摄像机列表
     * @param siteRequestDTO
     * @return
     */
    @PostMapping("/video/device/queryScopeDeviceList")
    Result<List<ScopeDeviceInfoVO>> queryScopeDeviceList(@RequestBody ScopeDeviceInfoVO siteRequestDTO) {
        String localizationFlag = "0";
        try {
            localizationFlag = deviceInfoMapper.getSysConfig(LOCALIZATION_FLAG);
        } catch (Exception e) {
            log.info(LOCALIZATION_FLAG_ERROR, e);
        }
        if("1".equals(localizationFlag)){
            siteRequestDTO.setLocalizationFlag("1");
        }
        if (StringUtils.isEmpty(siteRequestDTO.getTenantId())) {
            return Result.data(deviceInfoRepository.queryScopeDeviceList(siteRequestDTO));
        }else{
            return Result.data(deviceInfoRepository.queryScopeDeviceListTower(siteRequestDTO));
        }
    }

    /**
     * 摄像机自身绑定的后端算法
     */
    @PostMapping("/video/device/queryAlgorithmByConfig")
    public Result<List<IndustryAlgorithmDTO>> queryAlgorithmByConfig(@RequestBody IndustryAlgorithmQueryVo industryAlgorithmQueryVo) {
        return Result.data(deviceInfoRepository.queryAlgorithmByConfig(industryAlgorithmQueryVo));
    }

    /**
     * 摄像机组织树查询，第一步：查询组织
     */
    @PostMapping("/video/getMonitorDeptTreeStepOne")
    public Result<List<DeviceTreeVO>> getMonitorDeptTreeStepOne(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = videoDeviceInfoService.getMonitorDeptTreeStepOne(deviceReqDTO);
        return Result.data(list);
    }

    /**
     * 摄像机组织树查询，第二步：查询摄像机
     */
    @PostMapping("/video/getMonitorDeptTreeStepTwo")
    public Result<List<DeviceTreeVO>> getMonitorDeptTreeStepTwo(@RequestBody DeviceTreeVO deviceTreeVO) {
        List<DeviceTreeVO> list = videoDeviceInfoService.getMonitorDeptTreeStepTwo(deviceTreeVO);
        return Result.data(list);
    }

    /**
     * 摄像机行政区划树查询，第二步：查询摄像机（分页）
     * 由于济南属地摄像机行政区划树、摄像机标签树页面渲染过慢的原因，新增了getMonitorTreeStepTwo接口，采用分步查询方案，处理摄像机行政区划树、摄像机标签树加载慢问题，对其他属地及功能没有影响
     */
    @PostMapping("/video/getMonitorTreeStepTwoPage")
    public TableDataInfo getMonitorTreeStepTwoPage(@RequestBody DeviceTreeVO deviceTreeVO) {
        List<DeviceTreeVO> list = videoDeviceInfoService.getMonitorTreeStepTwoPage(deviceTreeVO);
        return getDataTable(list);
    }
}
