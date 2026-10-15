package com.chinatower.device.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.device.dto.UavCheckNameDTO;
import com.chinatower.device.dto.UavNestDeviceInfoDTO;
import com.chinatower.device.entity.dto.DeviceComprehensiveReq;
import com.chinatower.device.entity.dto.SiteRequestDTO;
import com.chinatower.device.entity.dto.UavDeviceDTO;
import com.chinatower.device.entity.vo.*;
import com.chinatower.industry.vo.IndustryDeviceTree;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 无人机设备信息mapper
 *
 * @ClassName UavDeviceMapper
 * @auther: luo.zhenghong
 * @date: 2023/6/8
 * @version: release_v1.5.95
 */
@Mapper
public interface UavDeviceMapper {

    /**
     * 根据条件查询无人机设备及其通道信息
     *
     * @param param
     * @return
     */
    List<UavDeviceQueryVO> queryUavDeviceInfoList(@Param("param") UavDeviceDTO param);

    List<UavDeviceQueryVO> queryUavNestDeviceInfoList(@Param("param") UavDeviceDTO param);

    /**
     * 根据无人机设备编码查询无人机设备信息
     *
     * @param deviceCode
     * @return
     */
    UavDeviceQueryVO qryByDeviceCode(@Param("deviceCode") String deviceCode, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("userId") String userId);

    /**
     * 查询无人机设备区域树
     *
     * @param param
     * @return
     */
    List<DeviceTreeVO> getUavMonitorTree(@Param("param") UavDeviceDTO param);

    /**
     * 查询设备组织关系
     *
     * @param deviceInfoVO
     * @return
     */
    UavDeviceQueryVO queryOrgInfoList(DeviceInfoVO deviceInfoVO);

    /**
     * 查询摄像机是否存在别名
     *
     * @param param
     * @return
     */
    UavDeviceQueryVO qryAliasByDeviceCode(@Param("param") UavDeviceDTO param);

    /**
     * 修改摄像机别名
     *
     * @param param
     * @return
     */
    Integer updateUavAlias(@Param("param") UavDeviceDTO param);

    /**
     * 新增摄像机别名
     *
     * @param param
     * @return
     */
    Integer addUavAlias(@Param("param") UavDeviceDTO param);

    List<OrganDeviceRelVO> queryOrgUavDevRels(@Param("param") UavDeviceDTO param);

    /**
     * 查询无人机设备距离信息
     *
     * @param param 参数
     * @return 无人机设备距离信息
     */
    List<DeviceDistanceVO> queryUavDeviceDistanceInfo(@Param("param") DeviceDistanceDTO param);

    /***
     * 获取无人机区域树
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getUavMonitorAreaTree(@Param("param") DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getUavMonitorAreaTreeWithoutFormat(@Param("param") DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getUavMonitorAreaTreeWithoutLogin(@Param("param") DeviceReqDTO deviceReqDTO);

    /**
     * 获取无人机组织树
     *
     * @param deviceReqDTO
     * @return
     */
    List<DeviceTreeVO> getUavMonitorOrgTree(@Param("param") DeviceReqDTO deviceReqDTO);

    List<DeviceTreeVO> getUavMonitorOrgTreeWithoutFormat(@Param("param") DeviceReqDTO deviceReqDTO);


    /**
     * 查询无人机设备站址信息
     *
     * @param siteRequestDTO
     * @return
     */
    List<SiteResultVO> queryUavSitesForWE(@Param("param") SiteRequestDTO siteRequestDTO);

    /**
     * 根据站址编码和经纬度查询无人机设备列表
     *
     * @param siteRequestDTO
     * @return
     */
    List<DeviceSiteVO> queryUavDevicesBySiteCode(@Param("param") SiteRequestDTO siteRequestDTO);

    List<IndustryDeviceTree> queryUavSites(@Param("param") SiteRequestDTO siteRequestDTO);

    List<DeviceComprehensiveVO> queryDeviceListByKeyWord(@Param("param") DeviceComprehensiveReq deviceComprehensiveReq);

    List<UavNestDeviceInfoDTO> queryUavNestInfoByCodeList(@Param("list") List<String> list,
                                                          @Param("tenantId") String tenantId,
                                                          @Param("industryCode") String industryCode);

    List<String> queryUavNestCodeByCondition(@Param("param") UavDeviceDTO param);

    int countDeviceName(@Param("param") UavCheckNameDTO param);

    int countAliasName(@Param("param") UavCheckNameDTO param);

    int countNestDeviceName(@Param("param") UavCheckNameDTO param);

    int countNestAliasName(@Param("param") UavCheckNameDTO param);

    String queryCameraModelCodeByDeviceCode(@Param("deviceCode") String deviceCode);

    /**
     * 查询无人机设备详情信息
     * @param deviceCode
     * @return
     */
    UavDeviceDTO queryUavDetailInfo(@Param("deviceCode") String deviceCode);

    UavDeviceDTO queryUavByNestInfo(@Param("param")UavDeviceDTO param);

    List<DeviceCameraModelVO> queryUavCameraModelInfo(@Param("cameraModelCodeList") List<String> cameraModelCodeList);

    /**
     * 无人机机场型号列表
     * @return
     */
    List<UavNestModelVO> queryUavNestModelList();

    /**
     * 无人机型号列表
     * @return
     */
    List<UavModelVO> queryUavModelList();

    /**
     * 修改无人机经纬度位置信息
     */
    Integer updateUavDeviceLocationInfo(@Param("param") UavNestLocationVO param, @Param("uavDeviceCodeList") List<String> uavDeviceCodeList);

    /**
     * 修改无人机机巢位置信息
     */
    Integer updateUavNestDeviceLocationInfo(@Param("param") UavNestLocationVO param);

    /**
     * 通过机巢编码查询关联无人机
     */
    List<UavDeviceDTO> queryUavDeviceCodesByUavNestDeviceCode(@Param("uavNestCode") String uavNestCode, @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 获取无人机卡片列表
     * @param reqDTO
     * @return
     */
    @DS("base")
    List<UavCardQueryVO> queryUavInfoList(@Param("param") DeviceReqDTO reqDTO);

    /**
     * 查询每个设备的最新任务状态
     * @param deviceCodes
     * @param currentTime
     * @return
     */
    List<Map<String, Object>> getLatestTaskStatus(@Param("deviceCodes") List<String> deviceCodes, @Param("currentTime") Date currentTime);

    /**
     * 查询每个设备的未来计划任务
     * @param deviceCodes
     * @param currentTime
     * @param endTime
     * @return
     */
    List<Map<String, Object>> getFuturePlanTasks(@Param("deviceCodes") List<String> deviceCodes,
                                                 @Param("currentTime") Date currentTime,
                                                 @Param("endTime") Date endTime);


    /**
     * 根据飞行任务查询告警
     * @param tableName
     * @param planTaskId
     * @param tenantId
     * @param industryCode
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int getAlarmOrderCount(@Param("tableName") String tableName, @Param("planTaskId") String planTaskId,
                           @Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    /**
     * 根据挂载型号编码列表查询挂载型号信息
     * @param mountModelCodeList 挂载型号编码列表
     * @return 挂载型号信息列表
     */
    List<UavMountModelInfoVO> queryUavMountModelInfo(@Param("mountModelCodeList") List<String> mountModelCodeList);

    /**
     * 根据条件查询无人机基本信息
     * @param param
     * @return
     */
    List<UavDeviceQueryVO> queryUavListByCondition(@Param("param") UavDeviceDTO param);

    /**
     * 统计当前租户当前行业下正在飞行中的无人机设备数量
     * @param param 查询参数（包含 dataScope）
     * @param currentTime 当前时间
     * @return 正在飞行中的无人机设备数量
     */
    int countFlyingUavDevices(@Param("param") UavDeviceDTO param, 
                               @Param("currentTime") Date currentTime);
}
