package com.chinatower.device.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.core.dto.SiteInfoDto;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dataobject.DeviceInfoDO;
import com.chinatower.device.dto.QueryRecordPlanCheckDTO;
import com.chinatower.device.dto.RecordPlanCheckDTO;
import com.chinatower.device.dto.ScopeDeviceInfoVO;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.dto.*;
import com.chinatower.industry.vo.*;
import com.chinatower.mongodb.request.Page;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.QueryStorageDayVO;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.video.api.entity.SnapShotSimpleRes;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
public interface IDeviceInfoRepository extends IService<DeviceInfoDO> {
    /**
     * 根据站址信息更新设备中的站址相关信息
     *
     * @param siteInfoDto
     * @return com.chinatower.common.entity.Result
     * @author zheng.yan
     * @date 2022/1/18
     */
    @Deprecated
    void updateDeviceInfoBySiteInfo(SiteInfoDto siteInfoDto);

    /**
     * 获取设备菜单树
     *
     * @param deviceReqDTO
     * @return com.chinatower.common.entity.Result
     */
    List<DeviceTreeVO> getMonitorTree(DeviceReqDTO deviceReqDTO);


    /**
     * 保存上传的附件信息
     * @param hornDeviceDTO
     */
    void saveDeviceAttachmentInfo(HornDeviceDTO hornDeviceDTO);

    /**
     * 根据设备编码获取设备
     *
     * @param deviceCode
     * @return DeviceInfoDO
     * @author zheng.yan
     * @date 2022/1/24
     */
    DeviceInfoDO getDeviceByCode(String deviceCode);


    /**
     * 获取视频组织树
     *
     * @param deviceReqDTO
     * @return com.chinatower.common.entity.Result
     */
    List<DeviceTreeVO> getMonitorOrgTree(DeviceReqDTO deviceReqDTO);


    /**
     * 功能描述: 查询租户管理区域
     *
     * @param deviceReqDTO 请求参数
     * @return: Result
     * @auther: chenshaowu
     * @date: 2022/2/17
     * @version: 1.0
     */
    List<DeviceTreeVO> getTenantMonitorLevel(DeviceReqDTO deviceReqDTO);

    /**
     * 根据行业版本编码查询设备信息
     *
     * @param pageDeviceVO 平台编码
     * @return 设备数据和分页结果
     * @author chenmf
     * @date 2022/1/27
     */
    List<DeviceByPlatformVO> pageDeviceByPlatform(PageDeviceVO pageDeviceVO);

    /**
     * 功能描述: 预警事件-查询站址信息
     *
     * @param: siteRequestDTO
     * @return: List<SiteResultVO>
     * @auther: chenshaowu
     * @date: 2022/3/10
     * @version: 1.0
     */
    List<SiteResultVO> querySitesForWE(SiteRequestDTO siteRequestDTO);

    /**
     * 功能描述: 预警事件-查询摄像头集合
     *
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: chenshaowu
     * @date: 2022/3/11
     * @version: 1.0
     */
    List<DeviceSiteVO> queryDevicesByName(SiteRequestDTO siteRequestDTO);


    /**
     * 功能描述: 预警事件-查询摄像头集合【APP用】
     *
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/11/21
     * @version: 1.5.30
     */
    Map<String,List> queryDevicesApp(SiteRequestDTO siteRequestDTO);


    /**
     * 查询设备含通道信息
     *
     * @param: siteRequestDTO
     * @return: DeviceChannelInfoVO
     * @author: zheng.yan
     * @date: 2022/3/11
     * @version:
     */
    DeviceChannelInfoVO queryDevicesForWE(SiteRequestDTO siteRequestDTO);


    /**
     * 查询设备含通道信息
     *
     * @param: siteRequestDTO
     * @return: DeviceChannelInfoVO
     * @author: zheng.yan
     * @date: 2022/3/11
     * @version:
     */
    List<ChannelInfoScrVO> queryChannnelInfoForApp(SiteRequestDTO siteRequestDTO);

    /**
     * 设备查询，只返回设备名称、编码
     *
     * @param: siteRequestDTO
     * @return: List<SiteResultVO>
     * @author: zheng.yan
     * @date: 2022/3/11
     * @version:
     */
    List<DeviceInfoDO> getDeviceList(SiteRequestDTO siteRequestDTO);

    /**
     * 查询物联设备列表，只返回编号、名称【下拉筛选】
     * @param siteRequestDTO
     * @return List<IotDeviceVO>
     * @author lyy
     * @date: 2024/01/08
     * @version v1.6.40
     */
    List<IotDeviceVO> getIotDeviceList(SiteRequestDTO siteRequestDTO);

    /**
     * 查询通道录像计划校验（返回冲突的设备通道列表和录像保存天数列表）
     *
     * @param queryRecordPlanCheckDTO
     * @return
     * @author: chenmf
     * @date: 2022/3/13
     */
    RecordPlanCheckDTO queryRecordPlanCheck(QueryRecordPlanCheckDTO queryRecordPlanCheckDTO);

    /**
     * 5.3.故障告警消息报文
     *
     * @param deviceErrorVO
     * @return
     */
    Result insertDeviceError(DeviceErrorVO deviceErrorVO);

    /**
     * 功能描述: 根据网格员定位经纬度查询周边站址（for app）
     *
     * @param siteRequestDTO
     * @return Result
     * @auther: chenshaowu
     * @date: 2022/3/16
     */
    List<SiteResultVO> querySitesByLocal(SiteRequestDTO siteRequestDTO);

    /**
     * 获取视频存储时长
     *
     * @return
     * @author chenmf
     * @date 2022/3/21
     * @version
     */
    List<String> queryRecordPlanStorageDay(QueryStorageDayVO queryStorageDayVO);

    /**
     * 获取设备标签树
     *
     * @param deviceReqDTO 请求参数
     * @return
     * @author trk
     * @date 2022/3/22
     * @version
     */
    List<DeviceTreeVO> getMonitorLabelTree(DeviceReqDTO deviceReqDTO);
    List<DeviceTreeVO> getMonitorLabelTreeNew(DeviceReqDTO deviceReqDTO);

    /**
     * 条件查询租户应用设备
     *
     * @param deviceReq 请求参数
     * @return List<DeviceIndDTO>
     * @author zheng.yan
     * @date 2022/3/22
     * @version
     */
    List<DeviceIndDTO> listDeviceChannelByCond(DeviceIndReqDTO deviceReq);

    /**
     * 功能描述 查询行业应用摄像机树
     *
     * @param monitorTreeInfReq 请求参数
     * @return Result<List < DeviceIndDTO>>
     * @author Chensw
     * @date 2022/3/23
     */
    List<IndustryDeviceTree> queryMonitorIndTree(MonitorTreeInfReq monitorTreeInfReq);

    /**
     * 部门设备树
     * @param: monitorTreeInfReq
     * @return: List<IndustryDeviceTree>
     * @author: zheng.yan
     * @date: 2022/4/27
     * @version:
     */
    List<IndustryDeviceTree> queryMonitorIndDeptTree(MonitorTreeInfReq monitorTreeInfReq);

    /**
     * 功能描述 根据站址编码查询设备列表
     *
     * @param deviceIndReqDTO 请求参数
     * @return List<DeviceIndDTO>
     * @author trk
     * @date 2022/3/23
     */
    List<DeviceIndDTO> queryDevicesBySite(DeviceIndReqDTO deviceIndReqDTO);

    /**
     * 功能描述 查询摄像机图层
     *
     * @param deviceLayerReq 请求参数
     * @return List<IndustryDeviceTree>
     * @author Chensw
     * @date 2022/3/24 10:53
     */
    List<IndustryDeviceTree> queryDeviceLayer(DeviceLayerReq deviceLayerReq);

    PageInfo<IndustryDeviceTree> pageDeviceLayer(DeviceLayerReq deviceLayerReq);


    /**
     * 查看设备属性
     *
     * @param deviceIndReqDTO
     * @return DeviceIndDTO
     * @author trk
     * @date 2022/4/1
     */
    DeviceIndDTO getDeviceBasicInfo(DeviceIndReqDTO deviceIndReqDTO);

    /**
     * 查看通道属性
     *
     * @param deviceIndReqDTO
     * @return DeviceIndDTO
     * @author trk
     * @date 2022/4/2
     */
    DeviceIndDTO getChannelBasicInfo(DeviceIndReqDTO deviceIndReqDTO);

    /**
     * 根据摄像机编码获取型号信息
     * @param: reqCond
     * @return: DeviceModelDTO
     * @author: zheng.yan
     * @date: 2022/4/19
     * @version:
     */
    List<DeviceModelDTO> getDeviceModel(DeviceIndReqDTO reqCond);

    List<DeviceInfoVO> linkageDevice(DeviceReqDTO deviceReqDTO);

    /**
     * 查询摄像机校准信息
     * @param: deviceInfoReq
     * @return: DeviceModelDTO
     * @author: zheng.yan
     * @date: 2022/5/11
     * @version:
     */
    DeviceModelDTO getDeviceCalibration(DeviceIndReqDTO deviceInfoReq);

    DeviceDetailVO queryVideoDetail(DeviceTreeVO deviceTreeVO);

    DeviceDetailVO queryVideoDeviceDetail(DeviceTreeVO deviceTreeVO);

    List<Map<String, String>> getLableByDeviceCode(String deviceCode, String tenantId);

    /**
     * 摄像机设备信息列表
     * @param entity
     * @return
     */
    List<DeviceAttrVO> getVideoDeviceList(BaseIndustryEntity entity);

    /**
     * 雷达设备信息列表
     * @param entity
     * @return
     */
    List<DeviceAttrVO> getRadarDeviceList(BaseIndustryEntity entity);

    /**
     * 物联设备信息列表
     * @param entity
     * @return
     */
    List<DeviceAttrVO> getSensorDeviceList(BaseIndustryEntity entity);

    /**
     * 卫星服务信息列表
     * @param entity
     * @return
     */
    List<ServiceAttrVO> getSatelliteServiceList(BaseIndustryEntity entity);

    /**
     * 功能描述 查询指定租户，指定行业，指定经纬度3公里范围全部设备，按照距离从近到远排序
     *
     * @param deviceLayerReq
     * @return IndustryDeviceTree
     * @author wanglei
     * @date 2022/12/15 10:33
     * @version: release_v1.5
     */
    List<IndustryDeviceTree> queryDevice3km(DeviceLayerReq deviceLayerReq);

    List<IndustryDeviceTree> querySiteCodeAndDevices(SiteRequestDTO siteRequestDTO);

    List<IndustryDeviceTree> querySiteCodeAndDevicesForComponent(SiteRequestDTO siteRequestDTO);

    DetectResultVO lookHere(DeviceLayerReq deviceLayerReq);

    /**
     * 格式化区域设备树
     * @param treeList
     * @return
     */
    List<DeviceTreeVO> formatTreeList(List<DeviceTreeVO> treeList, Integer deviceOrChannel);

    /**
     * 格式化组织设备树
     * @param monitorList
     * @return
     */
    List<DeviceTreeVO> formatOrgTreeList(List<DeviceTreeVO> monitorList);

    List<DeviceTreeVO> formatOrgTreeList4Device(List<DeviceTreeVO> monitorList, Map<String, String> deviceFlightStatusMap);

    /**
     * 根据关键字查询摄像机列表
     * @param deviceComprehensiveReq
     * @return
     */
    Result<PageInfo<DeviceComprehensiveVO>> queryDeviceListByKeyWord(DeviceComprehensiveReq deviceComprehensiveReq);

    /**
     *
     * 查询范围内距离最近的设备，以树的形式返回
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> linkageDeviceTree(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getMonitorTreeWithOutUserId(DeviceReqDTO deviceReqDTO);

    Page<SnapShotSimpleRes> querySnapShotSimpleForAppByChannelCode(SiteRequestDTO siteRequestDTO);


    List<OrderDevicesVO> getOrderDevices(DeviceIndReqDTO deviceIndReqDTO);

    String getConfigByType(String configKey);

    List<DeviceAggregationLayerRes> queryDeviceAggregation(DevicesForGlobalReq devicesForGlobalReq);

    List<OrderDeviceCategoryRes> queryOrderDeviceCategory(SiteRequestDTO siteRequestDTO);

    List<DeviceCommonInfoVO> queryOrderDeviceModel(SiteRequestDTO siteRequestDTO);

    List<ScopeDeviceInfoVO> queryScopeDeviceList(ScopeDeviceInfoVO siteRequestDTO);

    List<ScopeDeviceInfoVO> queryScopeDeviceListTower(ScopeDeviceInfoVO siteRequestDTO);
    List<IndustryAlgorithmDTO> queryAlgorithmByConfig(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);
}
