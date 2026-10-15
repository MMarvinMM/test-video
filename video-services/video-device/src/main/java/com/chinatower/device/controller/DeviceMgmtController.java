package com.chinatower.device.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.constants.Constants;
import com.chinatower.device.dataobject.ChannelInfoDO;
import com.chinatower.device.dataobject.DeviceChannelInfo;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.repository.UserDeviceCollectionService;
import com.chinatower.device.utils.RespResult;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.service.IVideoDeviceMgmtInfoService;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.industry.vo.DeviceAttachmentVO;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceLabelReq;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.devicemgmt.vo.*;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.entity.PlayerGetVideoRealtimeUrlData;
import com.chinatower.video.api.entity.QueryOptAuthEnumResponse;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

/**
 *
 * video-device
 *
 * @auther: zhaozj
 * @date: 2022/8/2
 * @version: releae_v1.5
 * @requestID: 资源管理_摄像机管理
 */
@RestController
@Slf4j
@Api(value = "设备相关信息查询模块",tags = "设备相关信息查询模块")
public class DeviceMgmtController extends TowerBaseController {

    private static final String DEPRECATED_MSG = "废弃接口调用";

    @Autowired
    private IVideoDeviceMgmtInfoService videoDeviceMgmtInfoService;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    private DeviceInfoMapper deviceInfoMapper;

    @Autowired
    UserDeviceCollectionService userDeviceCollectionService;

    /**
     * 根据条件分页查询设备信息
     *
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/queryDeviceInfoPage")
    public TableDataInfo queryDeviceInfoPage(@RequestBody DeviceInfoVO deviceInfoVO) {
        List<DeviceInfoVO> list = videoDeviceMgmtInfoService.queryDeviceInfoPage(deviceInfoVO);
        return getDataTable(list);
    }
    /**
     * 查询客户设备下通道信息
     *
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/getChannelInfoForCust")
    @Deprecated
    public Result<List<ChannelInfoVO>> listChannelInfoForCustDev(@RequestBody DeviceInfoVO deviceInfoVO) {
        log.info(DEPRECATED_MSG);
        Result<List<ChannelInfoVO>> list = videoDeviceMgmtInfoService.listChannelInfoForCustDev(deviceInfoVO);
        return list;
    }

    /**
     * 查询通道列表信息
     * 根据设备编码批量查询通道信息
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/getChannelInfoForCustomer")
    public Result<List<ChannelInfoVO>> getChannelInfoForCustomer(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.listChannelInfoForCustomer(deviceInfoVO);
    }

    /**
     * 批量拉取实时视频流
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/getVideoRealtimeUrlInfo")
    public Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlInfo(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.getVideoRealtimeUrlInfo(deviceInfoVO);
    }

    /**
     * 查询设备所属的行政区域树形
     * @param: deviceReqDTO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/queryAreaTree")
    public Result<List<DeviceAreaTreeVO>> queryAreaTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        List<DeviceAreaTreeVO> list = videoDeviceMgmtInfoService.queryAreaTree(deviceReqDTO);
        return Result.data(list);
    }

    /**
     * 根据条件查询设备信息
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/getDeviceInfo")
    @Deprecated
    public Result<DeviceInfoVO> getDeviceInfo(@RequestBody DeviceInfoVO deviceInfoVO) {
        log.info(DEPRECATED_MSG);
        return videoDeviceMgmtInfoService.getDeviceInfo(deviceInfoVO);
    }

    /**
     * 查询摄像机图片
     * @param: deviceInfoVO
     * @return: Result
     * @auther: wanglei
     * @date: 2023/7/31
     * @version: release_v1.6
     * @requestID: 大屏-AR实景优化
     */
    @PostMapping("/video/device/getDeviceImage")
    public Result<List<String>> getDeviceImage(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.getDeviceImage(deviceInfoVO);
    }

    /**
     * 更新设备相关信息
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/updateDeviceInfo")
    public Result<String> updateDeviceInfo(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.updateDeviceInfo(deviceInfoVO);
    }

    /**
     * 设备编辑时校验设备名称是否重复
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/checkDeviceName")
    public Result<Boolean> checkDeviceName(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.checkDeviceName(deviceInfoVO);
    }

    /**
     * 查询客户设备标签
     * @param tenantDeviceLabelVO
     * @return
     */
    @PostMapping("/video/device/queryDeviceLabels")
    public Result<List<TenantDeviceLabelVO>> queryDeviceLabels(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO) {
        List<TenantDeviceLabelVO> list = videoDeviceMgmtInfoService.queryDeviceLabels(tenantDeviceLabelVO);
        return Result.data(list);
    }

    /**
     * 查询租户所属标签
     * @param tenantDeviceLabelVO
     * @return
     */
    @PostMapping("/video/device/queryTenantLabelList")
    public Result<List<TenantDeviceLabelVO>> queryTenantLabelList(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO) {
        return Result.data(videoDeviceMgmtInfoService.queryTenantLabelList(tenantDeviceLabelVO));
    }

    /**
     * 添加客户设备标签
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/addDeviceLabel")
    public Result<String> addDeviceLabel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO) {
        return videoDeviceMgmtInfoService.addDeviceLabel(tenantDeviceLabelVO);
    }

    /**
     * 添加客户设备标签关系 - 弃用
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/addDeviceLabelRel")
    public Result<String> addDeviceLabelRel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO) {
        return videoDeviceMgmtInfoService.addDeviceLabelRel(tenantDeviceLabelVO);
    }

    /**
     * 修改客户设备标签
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/updateDeviceLabel")
    public Result<String> updateDeviceLabel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO) {
        return videoDeviceMgmtInfoService.updateDeviceLabel(tenantDeviceLabelVO);
    }

    /**
     * 解除客户设备标签关系
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/relieveDeviceLabelRel")
    public Result<String> relieveDeviceLabelRel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO) {
        return videoDeviceMgmtInfoService.relieveDeviceLabelRel(tenantDeviceLabelVO);
    }

    /**
     * 解除客户设备标签关系-批量-for app
     * @param: tenantDeviceLabelList 绑定的标签
     * @return: Result
     * @auther: hued
     * @date: 2023年3月29日09:30:59
     * @version: release_v1.5.80
     * @requestID: app客户摄像机管理
     */
    @PostMapping("/video/device/relieveDeviceLabelRelBatch")
    public Result<String> relieveDeviceLabelRelBatch(@RequestBody TenantDeviceLabelBatchVO tenantDeviceLabelBatchVO) {
        return videoDeviceMgmtInfoService.relieveDeviceLabelRelBatch(tenantDeviceLabelBatchVO);
    }

    /**
     * 删除客户设备标签
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/deleteDeviceLabel")
    public Result<String> deleteDeviceLabel(@RequestBody TenantDeviceLabelVO tenantDeviceLabelVO) {
        return videoDeviceMgmtInfoService.deleteDeviceLabel(tenantDeviceLabelVO);
    }



    /**
     * 查询收藏摄像机接口（租户视角）
     * @Author：lzt
     * @param devListForAppDTO
     * @return List<DevListForAppVO>
     * @requestID  资源管理_摄像机管理
     */
    @PostMapping("/deviceMgmt/getDevListForApp")
    public com.chinatower.common.core.web.page.TableDataInfo getDevListForApp(@RequestBody DevListForAppDTO devListForAppDTO) {
        IPage<DevListForAppExtendVO> devListForApp = videoDeviceMgmtInfoService.getDevListForApp(devListForAppDTO);
        com.chinatower.common.core.web.page.TableDataInfo tableDataInfo = new com.chinatower.common.core.web.page.TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        tableDataInfo.setMsg("查询成功");
        tableDataInfo.setRows(devListForApp.getRecords());
        tableDataInfo.setTotal(devListForApp.getTotal());
        return tableDataInfo;
    }

    @PostMapping("/video/device/queryChannelList8")
    public Result<List<ChannelInfoScrVO>> queryChannelList8(@RequestBody SiteRequestDTO siteRequestDTO) {
        return Result.data(videoDeviceMgmtInfoService.queryChannelList8(siteRequestDTO));
    }
    /**
     * 查询用户权限范围内的所有摄像机信息
     * @return com.chinatower.common.entity.Result<java.util.List<com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO>>
     * @Author: TangJinBo
     * @Date: 2022/12/14 18:55
     */
    @PostMapping("/video/device/selectAllDeviceInfoByUserAuth")
    public Result<List<DeviceInfoVO>> selectAllDeviceInfoByUserAuth(@RequestBody DeviceInfoVO deviceInfoVO){
        return Result.data(videoDeviceMgmtInfoService.selectAllDeviceInfoByUserAuth(deviceInfoVO));
    }

    /**
     * 根据条件分页查询设备信息
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/queryDeviceInfoPageNew")
    @Deprecated
    public TableDataInfo queryDeviceInfoPageNew(@RequestBody DeviceInfoVO deviceInfoVO) {
        log.info(DEPRECATED_MSG);
        List<DeviceInfoVO> list = videoDeviceMgmtInfoService.queryDeviceInfoPageNew(deviceInfoVO);
        return getDataTable(list);
    }

    /**
     * 获取租户所有标签类型
     * @return
     */
    @PostMapping("/video/device/listDeviceLabelType")
    public Result<List<TenantDeviceLabelTypeVO>> listDeviceLabelType() {
        return videoDeviceMgmtInfoService.listDeviceLabelType();
    }

    /**
     * 新增租户标签类型
     * @param param
     * @return
     */
    @PostMapping("/video/device/addDeviceLabelType")
    public Result<Long> addDeviceLabelType(@RequestBody TenantDeviceLabelTypeVO param) {
        return videoDeviceMgmtInfoService.addDeviceLabelType(param);
    }

    /**
     * 修改租户标签类型
     * @param param
     * @return
     */
    @PostMapping("/video/device/updateDeviceLabelType")
    public Result<String> updateDeviceLabelType(@RequestBody TenantDeviceLabelTypeVO param) {
        return videoDeviceMgmtInfoService.updateDeviceLabelType(param);
    }

    /**
     * 删除租户标签类型
     * @param param
     * @return
     */
    @PostMapping("/video/device/deleteDeviceLabelType")
    public Result<String> deleteDeviceLabelType(@RequestBody TenantDeviceLabelTypeVO param) {
        return videoDeviceMgmtInfoService.deleteDeviceLabelType(param);
    }

    /**
     * 获取设备标签
     * @param param
     * @return
     */
    @PostMapping("/video/device/queryDeviceLabelInfoByDeviceCode")
    public Result<List<TenantDeviceLabelSelectedVO>> queryDeviceLabelInfoByDeviceCode(@RequestBody TenantDeviceLabelRelVO param) {
        if (param == null) {
            throw new CustomException("参数不能为空");
        }
        return videoDeviceMgmtInfoService.queryDeviceLabelInfoByDeviceCode(param);
    }

    @PostMapping("/video/device/queryDeviceLabelInfoByCondition")
    public Result<List<DeviceLabelInfoVO>> queryDeviceLabelInfoByCondition(@RequestBody DeviceLabelReq param) {
        if (param == null || StringUtils.isEmpty(param.getTenantId())
                || StringUtils.isEmpty(param.getIndustryCode())
                || CollectionUtils.isEmpty(param.getDeviceCodeList())) {
            throw new CustomException("必填参数不能为空");
        }
        return videoDeviceMgmtInfoService.queryDeviceLabelInfoByCondition(param);
    }

    /**
     * 获取设备标签--APP
     * @param param
     * @return
     */
    @Deprecated
    @PostMapping("/video/device/queryDeviceLabelInfoByDeviceCode4App")
    public Result<List<TenantDeviceLabelSelectedVO>> queryDeviceLabelInfoByDeviceCode4App(@RequestBody TenantDeviceLabelRelVO param) {
        if (param == null) {
            throw new CustomException("参数不能为空");
        }
        return videoDeviceMgmtInfoService.queryDeviceLabelInfoByDeviceCode4App(param.getDeviceCode());
    }

    /**
     * 批量新增设备标签
     * @param param
     * @return
     */
    @PostMapping("/video/device/addDeviceLabelRelBatch")
    public Result<String> addDeviceLabelRelBatch(@RequestBody TenantDeviceLabelRelVO param) {
        return videoDeviceMgmtInfoService.addDeviceLabelRelBatch(param);
    }

    /**
     * 新增设备标签
     * @param param
     * @return
     */
    @PostMapping("/video/device/addDeviceLabelInfo")
    public Result<Long> addDeviceLabelInfo(@RequestBody TenantDeviceLabelInfoVO param) {
        return videoDeviceMgmtInfoService.addDeviceLabelInfo(param);
    }

    /**
     * 修改设备标签
     * @param param
     * @return
     */
    @PostMapping("/video/device/updateDeviceLabelInfo")
    public Result<String> updateDeviceLabelInfo(@RequestBody TenantDeviceLabelInfoVO param) {
        return videoDeviceMgmtInfoService.updateDeviceLabelInfo(param);
    }

    /**
     * 删除设备标签
     * @param param
     * @return
     */
    @PostMapping("/video/device/deleteDeviceLabelInfo")
    public Result<String> deleteDeviceLabelInfo(@RequestBody TenantDeviceLabelInfoVO param) {
        return videoDeviceMgmtInfoService.deleteDeviceLabelInfo(param);
    }

    /**
     * 分页查询设备标签
     * @param param
     * @return
     */
    @PostMapping("/video/device/queryDeviceLabelPage")
    public Result<PageInfo<TenantDeviceLabelDetailVO>> queryDeviceLabelPage(@RequestBody TenantDeviceLabelQueryVO param) {
        return videoDeviceMgmtInfoService.queryDeviceLabelPage(param);
    }

    /**
     * 查询标签数量
     * @param labelTypeId
     * @return
     */
    @GetMapping("/video/device/getLabelCountByLabelTypeId")
    public Result<Integer> getLabelCountByLabelTypeId(Long labelTypeId) {
        return videoDeviceMgmtInfoService.getLabelCountByLabelTypeId(labelTypeId);
    }


    /**
     * 资源管理-摄像机管理-摄像机操控权限列表查询接口
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2023/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    @PostMapping("/video/device/queryDeviceOptAuthOld")
    public TableDataInfo queryDeviceOptAuthOld(@RequestBody DeviceOptAuthVO deviceOptAuthVO) {
        Map<String,Object> map = videoDeviceMgmtInfoService.queryDeviceOptAuth(deviceOptAuthVO);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows((List<?>)map.get("list"));
        rspData.setTotal(Long.parseLong(map.get("total").toString()));
        return rspData;
    }

    /**
     * 根据客户的摄像机操控权限优先级获取其用户的优先级
     * @param: deviceOptAuthVO
     * @return: Result<List<QueryGetEnumInfoReq>>
     * @auther: wanglei
     * @date: 2024/1/18
     * @version: release_v1.6.40
     */
    @PostMapping("/video/device/getUserDeviceOptAuthEnum")
    public Result<QueryOptAuthEnumResponse> getUserDeviceOptAuthEnum(@RequestBody DeviceOptAuthVO deviceOptAuthVO) {
        return videoDeviceMgmtInfoService.getUserDeviceOptAuthEnum(deviceOptAuthVO);
    }

    /**
     * 资源管理-摄像机管理-新增编辑摄像机操控权限接口
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2023/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */

    @PostMapping("/video/device/addDeviceOptAuth")
    public Result<String> addDeviceOptAuth(@RequestBody DeviceOptAuthVO deviceOptAuthVO) {
        return videoDeviceMgmtInfoService.addDeviceOptAuth(deviceOptAuthVO);
    }

    /**
     * 资源管理-摄像机管理-批量新增编辑摄像机操控权限接口
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2023/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */

    @PostMapping("/video/device/batchAddDeviceOptAuth")
    public Result<String> batchAddDeviceOptAuth(@RequestBody DeviceOptAuthVO deviceOptAuthVO) {
        return videoDeviceMgmtInfoService.batchAddDeviceOptAuth(deviceOptAuthVO);
    }

    /**
     * 根据设备编码查询设备附件信息
     * @param deviceCode 设备编码
     * @return 设备附件信息
     */
    @GetMapping("/video/device/getFileInfoByDeviceCode")
    public Result<DeviceAttachmentVO> getFileInfoByDeviceCode(String deviceCode) {
        return videoDeviceMgmtInfoService.getFileInfoByDeviceCode(deviceCode);
    }

    /**
     * 修复历史数据海拔字段（临时）
     * @param len
     * @return
     */
    @GetMapping("/video/device/updateAltitude")
    public Result updateAltitude(@RequestParam("len")int len){
        return videoDeviceMgmtInfoService.updateAltitude(len);
    }

    /**
     * 自定义摄像机图标
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/updateImageUrl")
    @Deprecated
    public Result updateImageUrl(@RequestBody DeviceInfoVO deviceInfoVO) {
        log.info(DEPRECATED_MSG);
        return videoDeviceMgmtInfoService.updateImageUrl(deviceInfoVO);
    }

    /**
     * 批量自定义摄像机图标
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/updateBatchImageUrl")
    public Result updateBatchImageUrl(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.updateBatchImageUrl(deviceInfoVO);
    }

    /**
     * 查询摄像机图标路径
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/getVideoImageUrl")
    public Result getVideoImageUrl(@RequestBody DeviceInfoVO deviceInfoVO) {
        String imageUrl = videoDeviceMgmtInfoService.getVideoImageUrl(deviceInfoVO);
        if(StringUtils.isNotEmpty(imageUrl)){
            imageUrl =  fileUrlEncryptUtil.dealObsFileUrl(imageUrl);
        }
        return Result.data(imageUrl);
    }

    /**
     * 根据摄像机编码查询有效通道
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/getChannelsByDeviceCode")
    @Deprecated
    public Result<List<ChannelInfoVO>> getChannelsByDeviceCode(@RequestBody DeviceInfoVO deviceInfoVO) {
        log.info(DEPRECATED_MSG);
        return videoDeviceMgmtInfoService.getChannelsByDeviceCode(deviceInfoVO);
    }


    /**
     * 新增用户配置权限保存接口
     * @param deviceOptAuthVO
     * @return
     */
    @PostMapping("/video/device/addCustomerOptAuth")
    public Result<String> addCustomerOptAuth(@RequestBody DeviceOptAuthVO deviceOptAuthVO) {
        return videoDeviceMgmtInfoService.addCustomerOptAuth(deviceOptAuthVO);
    }

    /**
     * 新增权限检验接口
     * @param deviceOptAuthVO
     * @return
     */
    @PostMapping("/video/device/checkDeviceOptAuth")
    public Result<DeviceOptAuthVO> checkDeviceOptAuth(@RequestBody DeviceOptAuthVO deviceOptAuthVO) {
        return videoDeviceMgmtInfoService.checkDeviceOptAuth(deviceOptAuthVO);
    }

    /**
     * 批量查询摄像机权限接口
     * @param deviceOptAuthVO
     * @return
     */
    @PostMapping("/video/device/getDeviceOptAuthList")
    public Result<List<DeviceOptAuthVO>> getDeviceOptAuthList(@RequestBody DeviceOptAuthVO deviceOptAuthVO) {
        return videoDeviceMgmtInfoService.getDeviceOptAuthList(deviceOptAuthVO);
    }

    /**
     * 铁塔批量新增用户配置权限保存接口
     * @param deviceOptAuthVOList
     * @return
     */
    @PostMapping("/video/device/batchTenantOptAuth")
    public Result<String> batchTenantOptAuth(@RequestBody List<DeviceOptAuthVO> deviceOptAuthVOList) {
        return videoDeviceMgmtInfoService.batchTenantOptAuth(deviceOptAuthVOList);
    }

    /**
     * 客户批量新增用户配置权限保存接口
     * @param deviceOptAuthVOList
     * @return
     */
    @PostMapping("/video/device/batchCustomerOptAuth")
    public Result<String> batchCustomerOptAuth(@RequestBody List<DeviceOptAuthVO> deviceOptAuthVOList) {
        return videoDeviceMgmtInfoService.batchCustomerOptAuth(deviceOptAuthVOList);
    }

    /**
     * 新增组织勾选取消摄像机回显接口
     * @param deviceOptAuthVO
     * @return
     */
    @PostMapping("/video/device/compareDeviceCodeList")
    public Result<List<String>> compareDeviceCodeList(@RequestBody DeviceOptAuthVO deviceOptAuthVO) {
        return videoDeviceMgmtInfoService.compareDeviceCodeList(deviceOptAuthVO);
    }

    /**
     * 历史用户配置权限数据处理初始化
     * @return
     */
    @PostMapping("/video/device/insertHisUserAuth")
    public Result<String> insertHisUserAuth() {
        return videoDeviceMgmtInfoService.insertHisUserAuth();
    }
    @PostMapping("/video/device/deviceNodeVisible")
    public Result<String> deviceNodeVisible(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.deviceNodeVisible(deviceInfoVO);
    }

    /**
     * 视图融合-请求配准
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/keepRegistrationStart")
    public Result<String> keepRegistrationStart(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.keepRegistrationStart(deviceInfoVO);
    }

    /**
     * 视图融合-配准状态查询
     * @param getStatusReq
     * @return
     */
    @PostMapping("/video/device/queryKeepRegistration")
    public Result<GetStatusRes> queryKeepRegistration(@RequestBody GetStatusReq getStatusReq) {
        return videoDeviceMgmtInfoService.queryKeepRegistration(getStatusReq);
    }

    /**
     * 摄像机配准状态回传 API
     * @param deviceRegistrationVO
     * @return
     */
    @PostMapping("/video/device/registrationStatusBack")
    public RespResult registrationStatusBack(@RequestBody DeviceRegistrationVO deviceRegistrationVO) {
        Result<String> result = videoDeviceMgmtInfoService.registrationStatusBack(deviceRegistrationVO);
        return RespResult.parseRespResultNew(result);
    }

    /**
     * 新增收藏或取消收藏区域，标签摄像机树
     * 由于济南属地摄像机行政区划树、摄像机标签树页面渲染过慢的原因，新增了addOrCancelCollections接口，采用分步查询方案，处理摄像机行政区划树、摄像机标签树加载慢问题，对其他属地及功能没有影响
     * @param vo
     */
    @PostMapping("/video/device/addOrCancelCollections")
    public Result<String> addOrCancelCollections(@RequestBody @Valid UserDeviceCollectionJNDTO vo) {
        //收藏之前先根据省市区乡镇收藏时，先查询通道，再收藏
        if (Constants.YES_FLAG.equals(vo.getTreeType()) && (!org.apache.commons.lang3.StringUtils.isEmpty(vo.getProvinceId())
                || !org.apache.commons.lang3.StringUtils.isEmpty(vo.getCityId())  || !org.apache.commons.lang3.StringUtils.isEmpty(vo.getCountyId())
                || !org.apache.commons.lang3.StringUtils.isEmpty(vo.getTownCode()))) {
            // 处理区域树收藏逻辑
            return handleAreaTreeCollectionLogic(vo);
        } else if (Constants.TWO_FLAG.equals(vo.getTreeType()) && (!org.apache.commons.lang3.StringUtils.isEmpty(vo.getParentCode())
                || !org.apache.commons.lang3.StringUtils.isEmpty(vo.getCode()))) {
            // 处理组织树收藏逻辑
            return handleDeptTreeCollectionLogic(vo);
        } else if (Constants.THREE_FLAG.equals(vo.getTreeType()) && (!CollectionUtils.isEmpty(vo.getLabelIdList()) || !CollectionUtils.isEmpty(vo.getLabelTypeIdList()))) {
            // 处理标签树收藏逻辑
            return handleLabelTreeCollectionLogic(vo);
        }else {
            if (vo.getOptType() == Constants.ONE_NUM) {
                userDeviceCollectionService.addJNCollections(vo.getCollObjCode(), vo.getCollObjType());
                return Result.success(Constants.ADD_COLLECT_SUCCESS);
            } else {
                userDeviceCollectionService.cancelJNCollections(vo.getCollObjCode(), vo.getCollObjType());
                return Result.success(Constants.CANCEL_COLLECT_SUCCESS);
            }
        }
    }

    private Result<String> handleAreaTreeCollectionLogic(UserDeviceCollectionJNDTO vo) {
        List<DeviceTreeVO> devChanList;
        // 摄像机设备
        String collObjType = Constants.FIVE_FLAG;
        //获取设备
        devChanList = deviceInfoMapper.getDevChannelInfo(vo);
        return devCollect(vo, devChanList, collObjType);
    }
    private Result<String> handleDeptTreeCollectionLogic(UserDeviceCollectionJNDTO vo) {
        List<DeviceTreeVO> devChanList;
        // 摄像机设备
        String collObjType = Constants.FIVE_FLAG;
        //获取设备
        devChanList = deviceInfoMapper.getDeptDevChannelInfo(vo);
        return devCollect(vo, devChanList, collObjType);
    }

    private Result<String> handleLabelTreeCollectionLogic(UserDeviceCollectionJNDTO vo) {
        List<DeviceTreeVO> devChanList;
        // 摄像机设备
        String collObjType = Constants.FIVE_FLAG;
        //获取设备
        devChanList = deviceInfoMapper.getLabelChannelInfo(vo);
        return devCollect(vo, devChanList, collObjType);
    }

    /**
     * 一级组织、二级组织不同状态的数据数量合并
     * 功能描述: 将同一一级组织、二级组织 不同状态的数据数量合并后，得到 chanMap
     * @param:
     * @return:
     */
    private Result<String> devCollect(UserDeviceCollectionJNDTO vo, List<DeviceTreeVO> devChanList, String collObjType) {
        List<String> devList = new ArrayList<>();
        List<String> chanList = new ArrayList<>();
        for (DeviceTreeVO dVO : devChanList) {
            if (!devList.contains(dVO.getDeviceCode())) {
                devList.add(dVO.getDeviceCode());
            }
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(dVO.getChannelCode())) {
                chanList.add(dVO.getChannelCode());
            }
        }
        String channelCollObjType = "";
        // 根据设备类型确认通道类型，如果设备类型为11，则取无人机通道类型9，否则取摄像机通道类型6，其他设备没有通道信息
        if (collObjType.equals(Constants.TWELVE_FLAG)) {
            channelCollObjType = String.valueOf(Constants.THIRTEEN_NUM);
        }
        else {
            channelCollObjType = Constants.SIX_FLAG;
        }
        if (vo.getOptType() == Constants.ONE_NUM) {
            if (!CollectionUtils.isEmpty(devList)) {
                userDeviceCollectionService.addJNCollections(devList, collObjType);
            }
            if (!CollectionUtils.isEmpty(chanList)) {
                userDeviceCollectionService.addJNCollections(chanList, channelCollObjType);
            }
            return Result.success(Constants.ADD_COLLECT_SUCCESS);
        } else {
            if (!CollectionUtils.isEmpty(devList)) {
                userDeviceCollectionService.cancelJNCollections(devList, collObjType);
            }
            if (!CollectionUtils.isEmpty(chanList)) {
                userDeviceCollectionService.cancelJNCollections(chanList, channelCollObjType);
            }
            return Result.success(Constants.CANCEL_COLLECT_SUCCESS);
        }
    }

    @PostMapping("/video/channel/info")
    public Result<DeviceChannelInfo> info(@RequestBody DeviceChannelInfo deviceChannelInfo) {
        DeviceChannelInfo info = videoDeviceMgmtInfoService.info(deviceChannelInfo);
        return Result.data(info);
    }

    /**
     * 保存用户常用设备记录
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/saveUserOptDeviceNum")
    public Result saveUserOptDeviceNum(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.saveUserOptDeviceNum(deviceInfoVO);
    }

    /**
     * 保存用户设备浏览记录
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/saveUserViewHistory")
    public Result saveUserViewHistory(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.saveUserViewHistory(deviceInfoVO);
    }

    @PostMapping("/video/device/getUserViewHistory")
    public Result getUserViewHistory(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.getUserViewHistory(deviceInfoVO);
    }
    @PostMapping("/video/device/getUserOptDeviceNum")
    public Result getUserOptDeviceNum(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.getUserOptDeviceNum(deviceInfoVO);
    }

    /**
     * 综合查询摄像机列表 from 1.7.85 综合查询基础版优化
     * @param deviceAIVO
     * @return
     */
    @PostMapping("/device/comprehensiveQueryMcp")
    public Result<ComprehensiveQueryAIResult> comprehensiveQueryMcp(@RequestBody DeviceAIVO deviceAIVO) {
        log.info("comprehensiveQueryMcp入参：{}", JSON.toJSONString(deviceAIVO));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //1：铁塔视角 2：客户视角
        String flag = com.chinatower.platform.constants.Constants.TWO;
        if (VideoSystemEnums.UserType.TOWER_USER.getIndex().equals(user.getUserType()) && !SecurityUtils.isSwitch()) {
            flag = com.chinatower.platform.constants.Constants.ONE;
        }
        if(com.chinatower.platform.constants.Constants.ONE.equals(flag)){
            log.info("进入铁塔分支");
            return videoDeviceMgmtInfoService.comprehensiveQueryAITower(deviceAIVO);
        }else{
            log.info("进入客户分支");
            return videoDeviceMgmtInfoService.comprehensiveQueryAITenant(deviceAIVO);
        }
    }

    /**
     * 查询权限范围内的全部通道列表信息
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/getAllChannelInfo")
    public Result<List<ChannelInfoVO>> getAllChannelInfo(@RequestBody DeviceInfoVO deviceInfoVO) {
        return videoDeviceMgmtInfoService.getAllChannelInfo(deviceInfoVO);
    }


    @PostMapping("/video/device/queryDeviceOptAuth")
    public TableDataInfo queryDeviceOptAuthNew(@RequestBody DeviceOptAuthVO deviceOptAuthVO) {
        List<DeviceCustomerOptAuthVO> list = videoDeviceMgmtInfoService.queryDeviceOptAuthNew(deviceOptAuthVO);
        return getDataTable(list);
    }

    /**
     * 根据设备编码查询订购摄像机、物联、雷达的客户
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/device/queryOrderDeviceByDevices")
    public Result<List<DeviceInfoVO>> queryOrderDeviceByDevices(@RequestBody DeviceInfoVO deviceInfoVO) {
        return Result.data(videoDeviceMgmtInfoService.queryOrderDeviceByDevices(deviceInfoVO));
    }
}
