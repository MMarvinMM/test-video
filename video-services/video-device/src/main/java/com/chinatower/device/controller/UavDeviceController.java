package com.chinatower.device.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UavPointToFlyQueryReqDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.repository.UavDeviceService;
import com.chinatower.device.entity.vo.UavDetailVO;
import com.chinatower.device.repository.UavNestCombineService;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * video-device
 * @ClassName UavDeviceController
 * @Deacription 无人设备控制层
 * @Author luo.zhenghong
 * @Date 2023/6/10 15:00
 * @Version release_v1.5.95
 **/
@RestController
@Api(value = "无人机设备控制层")
@RequestMapping("/uav/device")
@Slf4j
public class UavDeviceController extends BaseController {

    @Autowired
    private UavDeviceService uavDeviceService;

    @Autowired
    private UavNestCombineService uavNestCombineService;

    /**
     * 查询无人机设备列表
     * @param uavDeviceDTO 请求参数
     * @return TableDataInfo
     */
    @PostMapping("/qryUavDeviceListPage")
    public TableDataInfo qryUavDeviceListPage(@RequestBody UavDeviceDTO uavDeviceDTO) {
        log.info("无人机设备查询---请求报文------{}", JSON.toJSONString(uavDeviceDTO));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        uavDeviceDTO.setUserId(loginUser.getUser().getUserId());
        uavDeviceDTO.setTenantId(loginUser.getUser().getTenantId());
        uavDeviceDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
        startPage();
        return getDataTable(uavDeviceService.queryUavDeviceInfoList(uavDeviceDTO));
    }

    /**
     * 根据设备编码查询无人机设备信息 合并到/deviceCommon/getDeviceInfo
     * @param deviceCode 设备编码
     * @return
     */
    @Deprecated
    @PostMapping("/qryUavDeviceInfo/{deviceCode}")
    public Result<UavDeviceQueryVO> qryUavDeviceInfo(@PathVariable("deviceCode") String deviceCode) {
        return Result.data(uavDeviceService.qryByDeviceCode(deviceCode));
    }

    /**
     * 修改无人机设备信息
     * @param uavDeviceDTO
     * @return
     */
    @PostMapping("/updateUavDeviceInfo")
    @TowerLog(title = "资源管理-无人机设备信息修改", businessType = BusinessType.UPDATE)
    public Result updateUavDeviceInfo(@RequestBody @Valid UavDeviceDTO uavDeviceDTO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        uavDeviceDTO.setModifier(loginUser.getUser().getUserName());
        uavDeviceDTO.setTenantId(loginUser.getUser().getTenantId());
        uavDeviceDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
        uavDeviceDTO.setAppVerCode(loginUser.getUser().getAppVerCode());
        uavDeviceDTO.setCreator(loginUser.getUser().getUserName());
        uavDeviceService.updateUavDeviceInfo(uavDeviceDTO);
        return Result.success("更新成功");
    }

    /**
     * 查询无人机设备所属的行政区域树形
     * @param
     * @return
     */
    @ApiOperation(value = "查询无人机设备所属的行政区域树形")
    @PostMapping("/getUavMonitorTree")
    public Result<List<DeviceTreeVO>> getUavMonitorTree(@RequestBody UavDeviceDTO uavDeviceDTO) {
        return Result.data(uavDeviceService.getUavMonitorTree(uavDeviceDTO));
    }

    /**
     * 查询无人机设备距离信息
     * @param param
     * @return
     */
    @Deprecated
    @ApiOperation(value = "查询无人机设备距离信息")
    @PostMapping("/queryUavDeviceDistanceInfo")
    public Result<List<DeviceDistanceVO>> queryUavDeviceDistanceInfo(@RequestBody DeviceDistanceDTO param) {
        return Result.data(uavDeviceService.queryUavDeviceDistanceInfo(param));
    }

    /**
     * APP查询无人机设备距离信息
     * @param param
     * @return
     */
    @Deprecated
    @ApiOperation(value = "APP查询无人机设备距离信息")
    @PostMapping("/queryUavDeviceDistanceInfo4App")
    public Result<List<DeviceDistanceVO>> queryUavDeviceDistanceInfo4App(@RequestBody DeviceDistanceDTO param) {
        return Result.data(uavDeviceService.queryUavDeviceDistanceInfo4App(param));
    }

    /**
     *
     * 无人机区域设备树--给大屏展示用
     * @param deviceReqDTO
     * @return
     */
    @Deprecated
    @PostMapping("/getUavMonitorAreaTree")
    public Result<List<DeviceTreeVO>> getUavMonitorAreaTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = uavDeviceService.getUavMonitorAreaTree(deviceReqDTO);
        if (!CollectionUtils.isEmpty(list)) {
            list.get(0).setFirst(1);
        }
        return Result.data(list);
    }

    /**
     *
     * 无人机组织区域树
     *
     * @param deviceReqDTO
     * @return
     */
    @Deprecated
    @PostMapping("/getUavMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getUavMonitorOrgTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceTreeVO> list = uavDeviceService.getUavMonitorOrgTree(deviceReqDTO);
        if (!CollectionUtils.isEmpty(list)) {
            list.get(0).setFirst(1);
        }
        return Result.data(list);
    }

    /**
     * 功能描述: 查询无人机分布情况
     *
     * @param: siteRequestDTO
     * @return: com.chinatower.common.entity.Result
     * @auther: luo.zhenghong
     * @date: 2023/8/2
     */
    @PostMapping("/queryUavSitesForWE")
    public Result<List<SiteResultVO>> queryUavSitesForWE(@RequestBody SiteRequestDTO siteRequestDTO) {
        List<SiteResultVO> siteList = uavDeviceService.queryUavSitesForWE(siteRequestDTO);
        return Result.data(siteList);
    }

    /**
     * 查询无人机站址集合
     * @param siteRequestDTO
     * @return
     */
    @PostMapping("/queryUavSites")
    @ApiOperation("查询站址集合")
    @Deprecated
    Result<List<IndustryDeviceTree>> queryUavSites(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(uavDeviceService.queryUavSites(siteRequestDTO));
    }

    /**
     *
     * 功能描述: 根据站址查询摄像机列表信息
     *
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: luo.zhenghong
     * @date: 2023/8/2
     */
    @Deprecated
    @PostMapping("/queryUavDevicesBySiteCode")
    public Result<List<DeviceSiteVO>> queryUavDevicesBySiteCode(@RequestBody SiteRequestDTO siteRequestDTO) {
        List<DeviceSiteVO> deviceSiteVOS = uavDeviceService.queryUavDevicesBySiteCode(siteRequestDTO);
        return Result.data(deviceSiteVOS);
    }

    /**
     * 查询无人机相机设备信号信息
     */
    @PostMapping("/queryUavCameraModelInfo")
    public Result<List<DeviceCameraModelVO>> queryUavCameraModelInfo(@RequestBody UavDeviceDTO param) {
        List<DeviceCameraModelVO> resultList = uavDeviceService.queryUavCameraModelInfo(param);
        return Result.data(resultList);
    }

    /**
     * 查询无人机详情
     */
    @PostMapping("/queryUavDetail")
    public Result<UavDetailVO> queryUavDetail(@RequestBody UavDeviceDTO param) {
        return Result.data(uavDeviceService.queryUavDetail(param));
    }

    /**
     * 根据机巢查询无人机信息
     * @param param 参数
     * @return 返回值
     */
    @PostMapping("/queryUavByNestInfo")
    public Result<UavDeviceDTO> queryUavByNestInfo(@RequestBody UavDeviceDTO param) {
        return Result.data(uavDeviceService.queryUavByNestInfo(param));
    }

    /**
     * 同步更新当前无人机巢的信息
     *
     * @param param 机巢编码
     * @return 无人机巢的地址信息
     */
    @PostMapping("/syncUavNestLocationInfo")
    public Result<UavNestLocationVO> getUavNestLocationInfo(@RequestBody UavDeviceDTO param) {
        return Result.data(uavDeviceService.getUavNestLocationInfo(param));
    }

    /**
     * 分页查询无人机卡片列表
     * @param deviceReqDTO
     * @return
     */
    @PostMapping("/queryUavInfoList")
    public TableDataInfo queryUavInfoList(@RequestBody DeviceReqDTO deviceReqDTO) {
        log.info("无人机卡片列表查询---请求报文------{}", JSON.toJSONString(deviceReqDTO));
        return getDataTable(uavDeviceService.queryUavInfoList(deviceReqDTO));
    }

    /**
     * 查询指点飞行可用无人机列表
     * 推荐图标：
     * 1. 无飞行计划：该无人机3小时内执行计划下无飞行任务
     * 2. 空闲：该无人机当前飞行状态为"空闲"
     * 3. 电量充足：根据目的地距离与当前电量确保该无人机可完成本次指点飞行
     * 4. 都满足的无人机按照距离由近到远排序
     *
     * @param queryReqDTO 查询请求参数（包含目的地经纬度、高度、距离、设备编码列表）
     * @return 符合条件的无人机卡片列表
     */
    @PostMapping("/queryUavPointToFlyList")
    @ApiOperation(value = "查询指点飞行可用无人机列表")
    public Result<List<UavPointToFlyCardVO>> queryUavPointToFlyList(@RequestBody UavPointToFlyQueryReqDTO queryReqDTO) {
        log.info("查询指点飞行可用无人机列表---请求报文------{}", JSON.toJSONString(queryReqDTO));
        List<UavPointToFlyCardVO> resultList = uavDeviceService.queryUavPointToFlyList(queryReqDTO);
        return Result.data(resultList);
    }

    /**
     * 获取当前租户当前行业下的正在飞行中的无人机设备数量
     * @return 正在飞行中的无人机设备数量
     */
    @PostMapping("/getFlyingUavDeviceCount")
    @ApiOperation(value = "获取当前租户当前行业下的正在飞行中的无人机设备数量")
    public Result<Integer> getFlyingUavDeviceCount(@RequestBody UavDeviceDTO param) {
        log.info("获取当前租户当前行业下的正在飞行中的无人机设备数量");
        int count = uavDeviceService.getFlyingUavDeviceCount(param);
        return Result.data(count);
    }

    /**
     * 获取设备区域树结构
     *
     * @param param 设备请求参数对象，包含获取设备区域树所需的信息
     * @return 包含设备树结构列表的结果对象
     */
    @PostMapping("/getDeviceAreaTree")
    public Result<List<DeviceTreeVO>> getDeviceAreaTree(@RequestBody DeviceReqDTO param) {
        List<DeviceTreeVO> list = uavNestCombineService.getUavMonitorAreaTreeWithNest(param);
        return Result.data(list);
    }

    /**
     * 获取设备组织树结构
     *
     * @param param 设备请求参数对象，包含获取设备组织树所需的信息
     * @return 包含设备组织树结构的Result对象，数据部分为DeviceTreeVO列表
     */
    @PostMapping("/getDeviceOrgTree")
    public Result<List<DeviceTreeVO>> getDeviceOrgTree(@RequestBody DeviceReqDTO param) {
        List<DeviceTreeVO> list = uavNestCombineService.getUavMonitorOrgTreeWithNest(param);
        return Result.data(list);
    }

    @PostMapping("/getDeviceListBySiteCode")
    public Result<List<DeviceSiteVO>> getDeviceListBySiteCode(@RequestBody SiteRequestDTO siteRequestDTO) {
        List<DeviceSiteVO> deviceSiteList = uavNestCombineService.getDeviceListBySiteCodeWithNest(siteRequestDTO);
        return Result.data(deviceSiteList);
    }

    @PostMapping("/getDeviceSites")
    Result<List<IndustryDeviceTree>> getDeviceSites(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(uavNestCombineService.getDeviceSitesWithNest(siteRequestDTO));
    }

    @PostMapping("/qryUavNestDeviceListPage")
    public TableDataInfo qryUavDeviceListPageWithNest(@RequestBody UavDeviceDTO uavDeviceDTO) {
        log.info("无人机、机巢设备查询---请求报文------{}", JSON.toJSONString(uavDeviceDTO));
        LoginUser loginUser = SecurityUtils.getLoginUser();
        uavDeviceDTO.setUserId(loginUser.getUser().getUserId());
        uavDeviceDTO.setTenantId(loginUser.getUser().getTenantId());
        uavDeviceDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
        startPage();
        return getDataTable(uavDeviceService.queryUavNestDeviceInfoList(uavDeviceDTO));
    }
}
