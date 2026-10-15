package com.chinatower.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dataobject.DeviceChannelInfo;
import com.chinatower.device.dto.DevListForAppDTO;
import com.chinatower.device.dto.DevListForAppExtendVO;
import com.chinatower.device.dto.DeviceCustomerOptAuthVO;
import com.chinatower.device.dto.GetStatusReq;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.vo.DeviceAttachmentVO;
import com.chinatower.platform.entity.device.DeviceLabelReq;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.devicemgmt.vo.*;
import com.chinatower.video.api.entity.PlayerGetVideoRealtimeUrlData;
import com.chinatower.video.api.entity.QueryOptAuthEnumResponse;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * 功能描述: 租户视角--摄像机管理Service
 *
 * @auther: zhaozj
 * @date: 2022/8/2
 * @version: release_v1.5
 * @requestID: 资源管理_摄像机管理
 */
public interface IVideoDeviceMgmtInfoService {

    /**
     * 功能描述: 根据条件分页查询设备信息
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    List<DeviceInfoVO> queryDeviceInfoPage(DeviceInfoVO deviceInfoVO);

    /**
     * 功能描述: 查询客户设备下通道信息
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<List<ChannelInfoVO>> listChannelInfoForCustDev(DeviceInfoVO deviceInfoVO);

    /**
     * 功能描述: 查询客户设备下通道信息
     */
    Result<List<ChannelInfoVO>> listChannelInfoForCustomer(DeviceInfoVO deviceInfoVO);

    Result<Set<PlayerGetVideoRealtimeUrlData>> getVideoRealtimeUrlInfo(DeviceInfoVO deviceInfoVO);

    /**
     * 功能描述: 查询设备所属的行政区域树形
     * @param: deviceReqDTO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    List<DeviceAreaTreeVO> queryAreaTree(DeviceReqDTO deviceReqDTO);

    /**
     * 功能描述: 根据条件查询设备信息
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<DeviceInfoVO> getDeviceInfo(DeviceInfoVO deviceInfoVO);

    /**
     * 功能描述: 查询摄像机图片
     * @param: deviceInfoVO
     * @return: Result
     * @auther: wanglei
     * @date: 2023/7/31
     * @version: release_v1.6
     * @requestID: 大屏-AR实景优化
     */
    Result<List<String>> getDeviceImage(DeviceInfoVO deviceInfoVO);

    /**
     * 功能描述: 更新设备相关信息
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<String> updateDeviceInfo(DeviceInfoVO deviceInfoVO);

    /**
     * 功能描述: 设备编辑时校验设备名称是否重复
     * @param: deviceInfoVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<Boolean> checkDeviceName(DeviceInfoVO deviceInfoVO);

    /**
     * 查询客户设备标签
     * @param tenantDeviceLabelVO
     * @return
     */
    List<TenantDeviceLabelVO> queryDeviceLabels(TenantDeviceLabelVO tenantDeviceLabelVO);

    /**
     * 查询租户所属标签
     * @param tenantDeviceLabelVO
     * @return
     */
    List<TenantDeviceLabelVO> queryTenantLabelList(TenantDeviceLabelVO tenantDeviceLabelVO);

    /**
     * 功能描述: 添加客户设备标签
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<String> addDeviceLabel(TenantDeviceLabelVO tenantDeviceLabelVO);

    /**
     * 功能描述: 添加客户设备标签关系
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<String> addDeviceLabelRel(TenantDeviceLabelVO tenantDeviceLabelVO);

    /**
     * 功能描述: 修改客户设备标签
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<String> updateDeviceLabel(TenantDeviceLabelVO tenantDeviceLabelVO);

    /**
     * 功能描述: 解除客户设备标签关系
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<String> relieveDeviceLabelRel(TenantDeviceLabelVO tenantDeviceLabelVO);

    Result<String> relieveDeviceLabelRelBatch(TenantDeviceLabelBatchVO tenantDeviceLabelBatchVO);

    /**
     * 功能描述: 删除客户设备标签
     * @param: tenantDeviceLabelVO
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<String> deleteDeviceLabel(TenantDeviceLabelVO tenantDeviceLabelVO);


    /**
     * 查询收藏摄像机接口（租户视角）
     * @param devListForAppDTO
     * @return
     */
    IPage<DevListForAppExtendVO> getDevListForApp(DevListForAppDTO devListForAppDTO);

    List<ChannelInfoScrVO> queryChannelList8(SiteRequestDTO siteRequestDTO);
    /**
     * @Description: 查询用户权限范围内的所有摄像机信息
     * @return com.chinatower.common.entity.Result<java.util.List<com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO>>
     * @Author: TangJinBo
     * @Date: 2022/12/14 18:55
     */
    List<DeviceInfoVO> selectAllDeviceInfoByUserAuth(DeviceInfoVO deviceInfoVO);

    List<DeviceInfoVO> queryDeviceInfoPageNew(DeviceInfoVO deviceInfoVO);

    /**
     * 获取设备标签类型列表
     * @return 设备标签类型列表
     */
    Result<List<TenantDeviceLabelTypeVO>> listDeviceLabelType();

    /**
     * 新增设备标签类型
     * @param param 标签类型内容
     * @return 结果
     */
    Result<Long> addDeviceLabelType(TenantDeviceLabelTypeVO param);

    /**
     * 更新设备标签类型
     * @param param 标签类型内容
     * @return 结果
     */
    Result<String> updateDeviceLabelType(TenantDeviceLabelTypeVO param);

    /**
     * 删除设备标签类型
     * @param param 标签类型内容
     * @return 结果
     */
    Result<String> deleteDeviceLabelType(TenantDeviceLabelTypeVO param);

    /**
     * 根据设备编码获取设备标签信息
     * @param param
     * @return 设备标签信息
     */
    Result<List<TenantDeviceLabelSelectedVO>> queryDeviceLabelInfoByDeviceCode(TenantDeviceLabelRelVO param);

    /**
     * 根据条件查询设备标签信息
     *
     * @param param 设备标签请求参数
     * @return 返回设备标签信息列表
     */
    Result<List<DeviceLabelInfoVO>> queryDeviceLabelInfoByCondition(DeviceLabelReq param);

    /**
     * 根据设备编码获取设备标签信息【APP使用，需要展示未关联标签的标签类型】
     * @param deviceCode 设备编码
     * @return 设备标签信息
     */
    Result<List<TenantDeviceLabelSelectedVO>> queryDeviceLabelInfoByDeviceCode4App(String deviceCode);

    /**
     * 批量关联设备标签关系
     * @param param 设备标签信息
     * @return 结果
     */
    Result<String> addDeviceLabelRelBatch(TenantDeviceLabelRelVO param);

    /**
     * 新增设备标签
     * @param param 标签内容
     * @return 结果
     */
    Result<Long> addDeviceLabelInfo(TenantDeviceLabelInfoVO param);

    /**
     * 更新设备标签
     * @param param 标签内容
     * @return 结果
     */
    Result<String> updateDeviceLabelInfo(TenantDeviceLabelInfoVO param);

    /**
     * 删除设备标签
     * @param param 标签内容
     * @return 结果
     */
    Result<String> deleteDeviceLabelInfo(TenantDeviceLabelInfoVO param);

    /**
     * 分页查询告警标签信息
     * @param param 查询条件
     * @return 结果
     */
    Result<PageInfo<TenantDeviceLabelDetailVO>> queryDeviceLabelPage(TenantDeviceLabelQueryVO param);

    /**
     * 根据标签类型编码获取标签数量
     * @param labelTypeId 标签类型编码
     * @return 标签数量
     */
    Result<Integer> getLabelCountByLabelTypeId(Long labelTypeId);


    /**
     * 功能描述: 资源管理-摄像机管理-摄像机操控权限列表查询接口
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2023/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Map<String,Object> queryDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO);

    List<DeviceCustomerOptAuthVO> queryDeviceOptAuthNew(DeviceOptAuthVO deviceOptAuthVO);

    /**
     * 功能描述: 资源管理-摄像机管理-新增摄像机操控权限接口
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2023/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<String> addDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO);

    Result<QueryOptAuthEnumResponse> getUserDeviceOptAuthEnum(DeviceOptAuthVO deviceOptAuthVO);

    /**
     * 功能描述: 资源管理-摄像机管理-批量新增摄像机操控权限接口
     * @param: deviceInfoVO
     * @return: List<DeviceInfoVO>
     * @auther: zhaozj
     * @date: 2023/8/2
     * @version: release_v1.5
     * @requestID: 资源管理_摄像机管理
     */
    Result<String> batchAddDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO);

    /**
     * 根据设备编码查询设备附件信息
     * @param deviceCode 设备编码
     * @return 设备附件信息
     */
    Result<DeviceAttachmentVO> getFileInfoByDeviceCode(String deviceCode);

    Result updateAltitude(int len);

    Result<String> updateImageUrl(DeviceInfoVO deviceInfoVO);

    Result<String> updateBatchImageUrl(DeviceInfoVO deviceInfoVO);

    String getVideoImageUrl(DeviceInfoVO deviceInfoVO);

    Result<List<ChannelInfoVO>> getChannelsByDeviceCode(DeviceInfoVO deviceInfoVO);

    /**
     * 新增客户操控权限
     * @param deviceOptAuthVO
     * @return
     */
    Result<String> addCustomerOptAuth(DeviceOptAuthVO deviceOptAuthVO);

    Result<DeviceOptAuthVO> checkDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO);

    Result<String> batchTenantOptAuth(List<DeviceOptAuthVO> deviceOptAuthVOList);

    Result<String> batchCustomerOptAuth(List<DeviceOptAuthVO> deviceOptAuthVOList);

    Result<List<String>> compareDeviceCodeList(DeviceOptAuthVO deviceOptAuthVO);

    Result<String> insertHisUserAuth();

    Result<String> deviceNodeVisible(DeviceInfoVO deviceInfoVO);

    Result<String> keepRegistrationStart(DeviceInfoVO deviceInfoVO);

    Result<GetStatusRes> queryKeepRegistration(GetStatusReq getStatusReq);

    Result<String> registrationStatusBack(DeviceRegistrationVO deviceRegistrationVO);

    DeviceChannelInfo info(DeviceChannelInfo deviceChannelInfo);

    Result saveUserOptDeviceNum(DeviceInfoVO deviceInfoVO);

    Result saveUserViewHistory(DeviceInfoVO deviceInfoVO);

    Result getUserOptDeviceNum(DeviceInfoVO deviceInfoVO);

    Result getUserViewHistory(DeviceInfoVO deviceInfoVO);

    Result<List<DeviceOptAuthVO>> getDeviceOptAuthList(DeviceOptAuthVO deviceOptAuthVO);

    Result<ComprehensiveQueryAIResult> comprehensiveQueryAITower(DeviceAIVO deviceAIVO);
    Result<ComprehensiveQueryAIResult> comprehensiveQueryAITenant(DeviceAIVO deviceAIVO);

    Result<List<ChannelInfoVO>> getAllChannelInfo(DeviceInfoVO deviceInfoVO);

    /**
     * 根据批量的设备编码查询订购摄像机、物联、雷达的用户信息
     * @param deviceInfoVO
     * @return
     */
    List<DeviceInfoVO> queryOrderDeviceByDevices(DeviceInfoVO deviceInfoVO);
}
