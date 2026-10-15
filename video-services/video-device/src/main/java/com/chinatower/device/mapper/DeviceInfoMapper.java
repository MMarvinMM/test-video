package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.chinatower.device.dataobject.DeviceChannelInfo;
import com.chinatower.device.dataobject.DeviceInfoDO;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.*;
import com.chinatower.gis.api.entity.AltitudesRes;
import com.chinatower.gis.api.entity.LonLat;
import com.chinatower.industry.dto.*;
import com.chinatower.industry.vo.DeviceAggregationLayerRes;
import com.chinatower.industry.vo.IndustryAlgorithmQueryVo;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.industry.vo.OrderDeviceCategoryRes;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.*;
import com.chinatower.platform.entity.order.OrderDevicesVO;
import com.chinatower.system.api.model.UserRoleVO;
import com.chinatower.video.api.entity.PresetData;
import com.chinatower.video.api.entity.VideoBaseReq;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * 功能描述: 租户视角--摄像机管理 DeviceInfoMapper
 *
 * @auther: zhaozj
 * @date: 2022/8/2
 * @version: release_v1.5
 * @requestID: 资源管理_摄像机管理
 */
public interface DeviceInfoMapper extends BaseMapper<DeviceInfoDO> {

    List<DeviceTreeVO> getMonitorTree(@Param("param") DeviceReqDTO deviceInfoDTO);

    List<DeviceTreeVO> getMonitorOrgTree(@Param("param") DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getCustOrganInfo(DeviceReqDTO deviceReqDTO);

    /**
     * 查询该设备归属管理区域对应省市区
     *
     * @return
     * @author chenmf
     * @date 2022/2/11
     * @version
     */
    BasicDeviceDTO queryOrderDeviceAreaInfo(@Param("areaCode") String areaCode);

    /**
     * 设备管理-查询设备列表(客户视角)
     *
     * @param deviceInfoVO
     * @return
     */
    List<DeviceInfoVO> queryDeviceInfoList(@Param("param") DeviceInfoVO deviceInfoVO);

    /**
     * 添加组织设备关系信息
     *
     * @param organDeviceRelVOList
     * @return
     */
    int addOrganDeviceRelBatch(List<OrganDeviceRelVO> organDeviceRelVOList);

    /**
     * 批量删除组织设备关系信息
     *
     * @param idList
     * @return
     */
    int deleteOrganDeviceRelBatch(@Param("list") List<String> idList);

    /**
     * 查询设备所属组织信息
     *
     *
     * @param deviceInfoVO
     * @return
     */
    List<Map<String, String>> queryOrgInfoList(DeviceInfoVO deviceInfoVO);

    /**
     * 根据条件查询设备关联的通道信息
     *
     * @param deviceInfoVO
     * @return
     */
    List<ChannelInfoVO> queryChannelInfo(DeviceInfoVO deviceInfoVO);

    List<DeviceAreaTreeVO> getDeviceAreaTree(@Param("param") DeviceReqDTO deviceReqDTO);

    /**
     * 获取区县所属街道
     *
     * @param countryCode
     * @return
     */
    List<Map<String, String>> getStreetList(@Param("countryCode") String countryCode);

    List<DeviceTreeVO> getTenantArea(@Param("param") DeviceReqDTO deviceReqDTO);

    /**
     * 根据行业版本编码查询设备信息
     *
     * @param pageDeviceVO
     * @return 设备数据和分页结果
     * @author chenmf
     * @date 2022/1/27
     */
    List<DeviceByPlatformVO> pageDeviceByPlatform(@Param("param") PageDeviceVO pageDeviceVO);

    /**
     * 获取设备信息 迁移
     *
     * @param siteRequestDTO
     * @return
     */
    List<SiteResultVO> querySitesForWE(@Param("param") SiteRequestDTO siteRequestDTO);

    /**
     * 查询设备 迁移
     *
     * @param: siteRequestDTO
     * @return: List<DeviceSiteVO>
     * @author: zheng.yan
     * @date: 2022/3/11
     * @version:
     */
    List<DeviceSiteVO> queryDevicesByName(@Param("param") SiteRequestDTO siteRequestDTO);

    /**
     * 功能描述: 预警事件-查询摄像头集合【APP用】
     *
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/11/21
     * @version: 1.5.30
     */
    List<DevChanVO> queryDevicesApp(@Param("param") SiteRequestDTO siteRequestDTO);

    /**
     * 功能描述: 预警事件-查询摄像头集合【APP用】
     *
     * @param: siteRequestDTO 查询参数
     * @return: Result
     * @auther: zhaozj
     * @date: 2022/11/21
     * @version: 1.5.30
     */
    List<String> queryDevicesInfoPage(@Param("param") SiteRequestDTO siteRequestDTO);


    /**
     * 查询设备 迁移
     *
     * @param: siteRequestDTO
     * @return: DeviceChannelInfoVO
     * @author: zheng.yan
     * @date: 2022/3/11
     * @version:
     */
    DeviceChannelInfoVO queryDevicesForWE(SiteRequestDTO siteRequestDTO);

    /**
     * 查询设备下通道 迁移
     *
     * @param: siteRequestDTO
     * @return: List<ChannelInfoScrVO>
     * @author: zheng.yan
     * @date: 2022/3/11
     * @version:
     */
    List<ChannelInfoScrVO> queryChannelList(@Param("param") SiteRequestDTO siteRequestDTO);

    List<ChannelInfoScrVO> queryChannelList8(@Param("param") SiteRequestDTO siteRequestDTO);

    /**
     * 设备查询，只返回设备名称、编码
     *
     * @param: siteRequestDTO
     * @return: List<DeviceInfoDO>
     * @author: zheng.yan
     * @date: 2022/3/11
     * @version:
     */
    List<DeviceInfoDO> getDeviceList(@Param("param") SiteRequestDTO siteRequestDTO);


    /**
     * 查询物联设备列表，只返回编号、名称【下拉筛选】
     * @param siteRequestDTO
     * @return List<IotDeviceVO>
     * @author lyy
     * @date: 2024/01/08
     * @version v1.6.40
     */
    List<IotDeviceVO> getIotDeviceList(@Param("param") SiteRequestDTO siteRequestDTO);

    /**
     * 冲突的设备通道列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/13
     * @version
     */
    List<ChannelVideoPlanVO> queryVideoPlan(QueryRecordPlanCheckDTO queryRecordPlanCheckDTO);

    /**
     * 小于入参存储时长的录像最大保存天数列表
     *
     * @return
     * @author chenmf
     * @date 2022/3/13
     * @version
     */
    List<ChannelStorageTimeVO> queryRecordPlanStorageTime(ChannelStorageTimeVO channelStorageTimeVO);

    /**
     * 保存设备故障消息
     *
     * @param deviceErrorVO
     * @return
     */
    int insertDeviceError(DeviceErrorVO deviceErrorVO);

    /**
     * 获取设备订单号
     *
     * @param: channelCode
     * @param: tenantId
     * @return: java.util.List<java.lang.String>
     * @author: zheng.yan
     * @date: 2022/3/16
     * @version:
     */
    List<String> listDeviceOrderCode(@Param("channelCode") String channelCode, @Param("tenantId") String tenantId);

    /**
     * 查询设备的收藏状态
     * @param deviceCode
     * @param userId
     * @return
     */
    Integer queryDeviceCollStatus(@Param("deviceCode") String deviceCode, @Param("userId") String userId);

    Integer queryArTagCollStatus(@Param("deviceCode") String deviceCode, @Param("userId") String userId);

    List<DeviceInfoVO> queryDeviceCollStatusList(DeviceInfoVO qryDevOrg);

    List<SiteResultVO> querySitesByCode(@Param("param") SiteRequestDTO siteRequestDTO);

    List<SiteResultVO> linkageDeviceNew(@Param("param") SiteRequestDTO siteRequestDTO);

    /**
     * 获取视频存储时长
     *
     * @return
     * @author chenmf
     * @date 2022/3/21
     * @version
     */
    List<String> queryRecordPlanStorageDay(QueryStorageDayVO queryStorageDayVO);


    List<DeviceTreeVO> getMonitorLabelTree(DeviceReqDTO deviceReqDTO);
    List<DeviceTreeVO> getMonitorLabelTreeNew(DeviceReqDTO deviceReqDTO);

    /**
     * 条件查询租户应用设备
     *
     * @param: deviceReq
     * @return: List<DeviceIndDTO>
     * @author: zheng.yan
     * @date: 2022/3/22
     * @version:
     */
    List<DeviceIndDTO> listDeviceChannelByCond(DeviceIndReqDTO deviceReq);

    List<IndustryDeviceTree> queryMonitorIndTree(MonitorTreeInfReq monitorTreeInfReq);

    List<IndustryDeviceTree> queryMonitorIndOrdTree(MonitorTreeInfReq monitorTreeInfReq);

    List<IndustryDeviceTree> queryMonitorLabelOrdTree(MonitorTreeInfReq monitorTreeInfReq);

    /**
     * 根据站址编码查询设备列表
     *
     * @param deviceIndReqDTO
     * @author trk
     * @date 2022/3/23
     */
    List<DeviceIndDTO> queryDevicesBySite(DeviceIndReqDTO deviceIndReqDTO);

    /**
     * 功能描述 查询摄像机图层
     *
     * @param deviceLayerReq
     * @return List<IndustryDeviceTree>
     * @author Chensw
     * @date 2022/3/24 10:53
     */
    List<IndustryDeviceTree> queryDeviceLayer(DeviceLayerReq deviceLayerReq);

    List<IndustryDeviceTree> queryDeviceSiteList(DeviceLayerReq deviceLayerReq);

    /**
     * 功能描述 查询指定租户，指定行业，指定经纬度3公里范围全部设备，按照距离从近到远排序
     *
     * @param deviceLayerReq
     * @return List<IndustryDeviceTree>
     * @author wanglei
     * @date 2022/12/15 10:33
     */
    List<IndustryDeviceTree> queryDevice3km(DeviceLayerReq deviceLayerReq);

    /**
     * 功能描述 查询摄像机数量
     *
     * @param indTenantDeviceDTO
     * @return Integer
     * @author Chensw
     * @date 2022/4/1 10:21
     */
    Integer getDeviceNum(IndTenantDeviceDTO indTenantDeviceDTO);

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
     * @date 2022/4/1
     */
    DeviceIndDTO getChannelBasicInfo(DeviceIndReqDTO deviceIndReqDTO);

    List<String> getDeptIds(String orgCode);

    List<IndustryDeviceTree> getIndCustOrganInfo(DeviceReqDTO reqDTO);
    /**
     * 根据摄像机编码获取型号信息
     * @param: deviceCode
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

    DeviceDetailVO queryVideoDetail(String deviceCode,String channelCode);

    DeviceDetailVO queryVideoDeviceDetail(String deviceCode);

    List<Map<String, String>> getLableByDeviceCode(String deviceCode,String tenantId,String industryCode);

    /**
     * 摄像机设备信息列表
     * @param entity
     * @return
     */
    List<DeviceAttrVO> getVideoDeviceList(@Param("param") BaseIndustryEntity entity);

    /**
     * 雷达设备信息列表
     * @param entity
     * @return
     */
    List<DeviceAttrVO> getRadarDeviceList(@Param("param") BaseIndustryEntity entity);

    /**
     * 物联设备信息列表
     * @param entity
     * @return
     */
    List<DeviceAttrVO> getSensorDeviceList(@Param("param") BaseIndustryEntity entity);

    /**
     * 卫星服务信息列表
     * @param entity
     * @return
     */
    List<ServiceAttrVO> getSatelliteServiceList(@Param("param") BaseIndustryEntity entity);

    /**
     * 查询收藏摄像机接口（铁塔视角）
     * @param page
     * @param dto
     * @return
     */
    IPage<DevListForAppExtendVO> getDevListForApp(@Param("page") IPage<DeviceInfoDO> page, @Param("dto") DevListForAppDTO dto);


    /**
     * 查询通道列表(关联vm_user_device_collection_t用户设备收藏表)
     *
     * @param: siteRequestDTO
     * @return: List<ChannelInfoScrVO>
     * @author: liuxt
     * @date: 2022/11/21
     * @version:
     */
    List<ChannelInfoScrVO> queryChannelListCollection(@Param("param") SiteRequestDTO siteRequestDTO);
    /**
     * @Description: 查询用户权限范围内的所有摄像机信息
     * @return com.chinatower.common.entity.Result<java.util.List<com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO>>
     * @Author: TangJinBo
     * @Date: 2022/12/14 18:55
     */
    List<DeviceInfoVO> selectAllDeviceInfoByUserAuth(@Param("param") DeviceInfoVO deviceInfoVO);

    SysUser selectUserOnlyByUserName(String userName);

    List<DeviceInfoVO> queryDeviceInfoListNew(DeviceInfoVO deviceInfoVO);

    List<IndustryDeviceTree> querySiteCodeAndDevices(@Param("param") SiteRequestDTO siteRequestDTO);

    List<IndustryDeviceTree> querySiteCodeAndDevicesForComponent(@Param("param") SiteRequestDTO siteRequestDTO);

    List<DeviceCustomerOptAuthVO> queryDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO);
    List<DeviceOptAuthVO> queryDeviceOptAuthInfo(DeviceOptAuthVO deviceOptAuthVO);

    int addDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO);
    int uptDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO);
    int batchAddDeviceOptAuth(List<DeviceOptAuthVO> list);
    int batchUptDeviceOptAuth(List<DeviceOptAuthVO> list);
    int batchUptDeviceOptAuthUser(DeviceOptAuthVO deviceOptAuthVO);

    int batchUptDeviceOptAuthUserList(List<DeviceOptAuthVO> list);

    List<String> queryLabelNameList(@Param("param") SiteRequestDTO siteRequestDTO);

    List<LonLat> queryNullAltitudeList();
    void updateDeviceInfoBatch(List<AltitudesRes> list);

    List<DeviceIndDTO> getDeviceBasicInfoBatch(List<AlgorithmListVO> list);

    /**
     * 根据关键字查询物联设备列表
     *
     * @param deviceComprehensiveReq
     * @return List<DeviceComprehensiveVO>
     */
    List<DeviceComprehensiveVO> queryDeviceListByKeyWord(@Param("param") DeviceComprehensiveReq deviceComprehensiveReq);


    void updateImageUrl(DeviceInfoVO deviceInfoVO);

    void updateBatchImageUrl(DeviceInfoVO deviceInfoVO);


    /**
     * 查询范围内距离最近的设备，以树的形式返回
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> linkageDeviceTree(DeviceReqDTO deviceReqDTO);


    String getVideoImageUrl(DeviceInfoVO deviceInfoVO);

    List<ChannelInfoVO> getChannelsByDeviceCode(DeviceInfoVO deviceInfoVO);

    List<DeviceIndDTO> getDeviceListByCodes(List<String> deviceCodes);

    List<DeviceTreeForestryVO> getDeviceTreeForestry(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    List<DeviceTreeForestryVO> selectSysArea(@Param("areaCodes") List<String> areaCodes);

    List<WoodProcessingPlantDeivceVO> getWoodProcessingPlantDeivceInfo(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    List<WoodProcessingPlantDeivceVO> getWoodProcessingPlantDeivceInfoPage(WoodProcessingPlantDeivceVO woodProcessingPlantDeivceVO);

    DeviceOptAuthVO checkDeviceOptAuth(DeviceOptAuthVO deviceOptAuthVO);

    List<String> getRoleIdList(String userId);
    List<String> queryDeviceCodeList(DeviceReqDTO deviceInfoVO);

    List<String> queryDeptIdsList(DeviceOptAuthVO deviceOptAuthVO);

    List<RoleDataRelVO> queryRoleDataRelList(DeviceOptAuthVO DeviceOptAuthVO);

    List<SysUser> queryUserRoleList(@Param("roleIds") List<Long> roleIds,@Param("tenantId") String tenantId);

    List<SysUser> queryCustomizeRoleList(@Param("roleIds") List<Long> roleIds);

    List<String> selectAllTanentDept(SysUser user);

    List<String> selectCustomTanentDept(SysUser user);

    List<String> selectTenantChildDept(SysUser user);

    List<String> queryRolesList(String userId);

    List<UserRoleVO> batchQueryRolesList(@Param("userIds") List<String> userIds);

    List<SysRole> querySysRoleList(@Param("roles") List<String> roles,@Param("tenantId")String tenantId,@Param("industryCode")String industryCode,@Param("appVerCode")String appVerCode);

    List<SysRole> batchQuerySysRoleList(@Param("roleIds") List<String> roleIds, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("appVerCode") String appVerCode);

    List<String> queryHisUserAuth(String menuId);

    String querySysMenuId(String menuName);

    void insertNotUserAuth(List<String> userIdList ,String type);

    List<String> queryHisAuthList(String userId);

    Map<String, List<String>> queryUserRolesList(List<String> userList);

    List<SysRole> querySysRoleDetails(Set<String> roleIds, String tenantId, String industryCode, String appVerCode);

    String queryDeviceNodeVisible(DeviceInfoVO deviceInfoVO);

    PresetData findPreset(@Param("presetId")Long presetId, @Param("deviceCode")String deviceCode, @Param("channelCode")String channelCode);

    List<DeviceRegistrationVO> queryRegistrationInfo(String deviceCode);

    int updateDeviceRegistration(DeviceRegistrationVO deviceRegistrationVO);

    int updateDeviceRegistrationByPreset(DeviceRegistrationVO deviceRegistrationVO);

    SysArea getAreaRootInfo();

    List<DeviceTreeVO> getIsMonitorDeviceTree(@Param("param") DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getDeviceTree(@Param("param") DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getDevicCollInfo(@Param("param") DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> queryDevicesByAreaParam(@Param("param") DeviceTreeVO deviceTreeVO);

    List<DeviceTreeVO> getLabelTree(DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> queryDevicesByLabelParam(@Param("param") DeviceTreeVO deviceTreeVO);

    @DataScope(tenantColumn = "ord.tenant_id", acceptColumn="ord.accept_node", industryCode = "ord.industry_code", deviceColumn = "ord.device_code")
    List<DeviceTreeVO> getDevChannelInfo(UserDeviceCollectionJNDTO userDeviceCollectionDTO);

    List<DeviceTreeVO> getDeptDevChannelInfo(UserDeviceCollectionJNDTO userDeviceCollectionDTO);

    @DataScope(tenantColumn = "ord.tenant_id", acceptColumn="ord.accept_node", industryCode = "ord.industry_code", deviceColumn = "ord.device_code")
    List<DeviceTreeVO> getLabelChannelInfo(UserDeviceCollectionJNDTO userDeviceCollectionDTO);

    DeviceChannelInfo findDeviceChannel(DeviceChannelInfo deviceChannelInfo);

    List<OrderDevicesVO> getOrderDevices(DeviceIndReqDTO deviceIndReqDTO);

    String getConfigByType(String configKey);

    Integer getVideoPreloaderNum(DeviceInfoVO deviceInfoVO);

    List<DeviceOptAuthVO> getDeviceOptAuthBatch(DeviceOptAuthVO deviceOptAuthVO);

    String getAreaCodeByName(String areaName, String areaLevel);

    AreaInfoNew getAreaInfoByCode(String areaCode);

    List<DeviceTreeNewVO> comprehensiveQueryAI(DeviceAIVO deviceAIVO);

    int comprehensiveQueryAICount(DeviceAIVO deviceAIVO);

    List<DeviceTreeNewVO> queryChannelListNew(DeviceAIVO deviceAIVO);

    List<IndustryDeviceTree> queryDevicesForGlobal(@Param("param") DevicesForGlobalReq devicesForGlobalReq);

    List<IndustryDeviceTree> queryDevicesForGlobalSurround(DevicesForGlobalSurroundReq devicesForGlobalSurroundReq);

    List<IndustryDeviceTree> queryDevicesForGlobalSurroundList(DevicesForGlobalSurroundReq devicesForGlobalSurroundReq);

    List<DeviceAggregationLayerRes> queryDeviceAggregation(@Param("param") DevicesForGlobalReq devicesForGlobalReq);

    List<OrderDeviceCategoryRes> queryOrderDeviceCategory(SiteRequestDTO siteRequestDTO);

    List<DeviceCommonInfoVO> queryOrderDeviceModel(SiteRequestDTO siteRequestDTO);

    List<BasicDeviceDTO> queryAreaInfo(List<String> areaCodes);

    List<IndustryDeviceTree> queryDevicesForGlobalUav(DevicesForGlobalSurroundReq devicesForGlobalSurroundReq);

    String getSysConfig(String configKey);

    List<ScopeDeviceInfoVO> queryScopeDeviceList(@Param("param") ScopeDeviceInfoVO siteRequestDTO);

    List<IndustryAlgorithmDTO> queryAlgorithmByConfig(IndustryAlgorithmQueryVo industryAlgorithmQueryVo);

    List<DeviceTreeVO> getIsMonitorDeviceGroup(@Param("param") DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getDeviceGroup(@Param("param") DeviceReqDTO deviceReqDTO);

    /**
     * 根据设备编码查询订购摄像机的客户
     * @param deviceInfoVO
     * @return
     */
    List<DeviceInfoVO> queryOrderDeviceByDevices(DeviceInfoVO deviceInfoVO);

    /**
     * 根据设备编码查询订购物联/雷达的客户
     * @param deviceInfoVO
     * @return
     */
    List<DeviceInfoVO> queryOrderIotDeviceByDevices(DeviceInfoVO deviceInfoVO);

    List<DeviceTreeVO> getMonitorDeptTreeStepOne(@Param("param") DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getDeviceCollInfoByDept(@Param("param") DeviceReqDTO deviceReqDTO);
    List<DeviceTreeVO> getIsMonitorDeviceDeptTree(@Param("param") DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getMonitorDeptTreeStepTwo(@Param("param") DeviceTreeVO deviceTreeVO);

    List<DeviceTreeVO> getMonitorTreeStepTwoPage(@Param("param") DeviceTreeVO deviceTreeVO);

    List<DeviceTreeVO> getChannelListForTwoPage(List<String> deviceCodeList,String userId);

    List<DeviceTreeVO> getDevChaAlias(List<DeviceTreeVO> list, String tenantId, String industryCode);
}
