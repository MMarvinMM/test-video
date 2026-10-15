package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.core.dto.AlgorithmInfoDTO;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.*;
import com.chinatower.event.entity.dto.AlarmOrderByDeviceDTO;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.entity.dto.SysUserDTO;
import com.chinatower.event.entity.dto.TenantDeviceRelDTO;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.event.entity.vo.TenantDeviceEventRelVO;
import com.chinatower.event.vo.*;
import com.chinatower.event.vo.entity.DeviceFeatureVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: DeviceMapper.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/28 09:56
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface DeviceMapper {

    /**
     * 通过设备编码获取租户
     * @param deviceCode
     * @return
     */
    List<OrderCustomerVO> getCustomersByDevice(@Param("deviceCode")String deviceCode, @Param("applicationCode")String applicationCode, @Param("param") ReceiveAlarmVO receiveAlarmVO);

    /**
     * 通过设备编码和算法编码获取租户
     * @param deviceCode
     * @param algorithmCode
     * @return
     */
    List<OrderCustomerVO> getCustomersList(@Param("deviceCode") String deviceCode, @Param("algorithmCode") String algorithmCode, @Param("taskId")String taskId,@Param("alarmSource")String alarmSource,@Param("localNode") String localNode);

    /**
     * 获取符合条件的铁塔运营研判人员
     * @param list
     * @return
     */
    List<String> getUserForOperation(List<String> list);

    /**
     * 根据角色ID查询角色用户
     * @param roleIdList
     * @param tenantId
     * @return
     */
    List<SysUser> getUserInfoByRoleIds(@Param("roleIdList")List<String> roleIdList, @Param("tenantId")String tenantId);

    /**
     * 根据角色ID查询角色用户
     * @param roleIdList
     * @param tenantId
     * @return
     */
    List<SysUser> getUserInfoByLongRoleIds(@Param("roleIdList")List<Long> roleIdList, @Param("tenantId")String tenantId);

    List<SysUser> getUserInfoByLongRoleIdsAndIndustryCode(@Param("roleIdList")List<Long> roleIdList, @Param("tenantId")String tenantId,@Param("industryCode")String industryCode);

    /**
     * 查询客户管理员信息
     * @param tenantId
     * @return
     */
    List<SysUserVO> getTenantAdminUser(@Param("tenantId") String tenantId);

    /**
     * 根据用户ID查询用户信息
     * @param userIdList
     * @return
     */
    List<SysUserVO> getUserInfoByUserIds(@Param("userIdList")List<String> userIdList);

    /**
     * 根据用户ID查询用户信息
     * @param userIdList
     * @return
     */
    List<SysUserVO> getUserInfoByUserIdsAndIndustryCode(@Param("userIdList")List<String> userIdList , @Param("industryCode") String industryCode);

    /**
     * 查询设备别名
     * @param tenantDeviceRelDTO
     * @return
     */
    List<TenantDeviceEventRelVO> getDeviceRel(TenantDeviceRelDTO tenantDeviceRelDTO);

    /**
     * 通过网格id获取网格员
     * @param gridId
     * @return
     */
    String getGridKeeperByGridId(String gridId);

    /**
     * 通过网格id获取网格员
     * @param gridId
     * @return
     */
    List<Map> getGridKeeperByGridIds(String gridId);

    /**
     * 通过网格id获取网格员对象
     * @param gridId
     * @return
     */
    String getGridUserByGridId(String gridId);

    /**
     * 查询摄像机信息
     * @param deviceCode
     * @return
     */
    DeviceSiteVO queryDevice(@Param("deviceCode") String deviceCode,@Param("channelCode") String channelCode);

    /**
     * 查询告警类型
     * @param warningTypeId
     * @return
     */
    AlarmTypeInfoDTO getAlarmTypeInfoById(String warningTypeId);

    /**
     * 根据部门ID查询部门信息
     * @param deptIdList
     * @return
     */
    List<SysDept> getSysDeptByDeptIds(@Param("deptIdList") List<String> deptIdList);

    /**
     *  查询区域
     * @return
     */
    List<Map> getAreaInfo();

    /**
     * 获取告警类型
     * @param
     * @return
     */
    List<Map> getAlarmType();

    /**
     * 通过用户id获取用户相关信息
     * @param userId
     * @return
     */
    Map<String, String> getUserInfoById(String userId);

    /**
     * 获取设备别名
     *
     * @param list
     * @param tenantId
     * @return
     */
    List<Map> getDeviceRelList(@Param("list") List<OrderInfoVO> list, @Param("tenantId") String tenantId);

    /**
     * 获取通道别名
     *
     * @param list
     * @param tenantId
     * @return
     */
    List<Map> getChannelRel(@Param("list") List<OrderInfoVO> list, @Param("tenantId") String tenantId);

    /**
     * 查询设备相关信息
     *
     * @return
     * @author chenmf
     * @date 2022/3/10
     * @version
     */
    List<AlarmOrderByDeviceDTO> queryAlarmOrderByDevice(@Param("param") AlarmOrderDTO dto);

    /**
     * 查询物联设备相关信息
     *
     * @return
     * @author chenmf
     * @date 2022/3/10
     * @version
     */
    List<AlarmOrderByDeviceDTO> getAlarmOrderByIotDevice(@Param("param") AlarmOrderDTO dto);

    /**
     * 查询领导批示数据
     * @param dto
     * @return
     */
    List<SysUserVO> getLeaderByTenantId(@Param("param") SysUserDTO dto);

    /**
     * 查询领导批示数据(带组织id)
     * @param dto
     * @return
     */
    List<SysInfo> getLeaderDeptByTenantId(@Param("param") SysUserDTO dto);

    /**
     * 查询摄像头集合
     * @param siteRequestVO
     * @return
     */
    List<SiteRequestVO> getDeviceList(@Param("param") SiteRequestVO siteRequestVO);

    /**
     * 根据条件查询省份数据
     * @param screenDTO
     * @return
     */
    List<LatitudeVO> getNumByProvinceTT(@Param("param") ScreenDTO screenDTO);

    /**
     * 根据条件查询地市数据
     * @param screenDTO
     * @return
     */
    List<LatitudeVO> getNumByCityTT(@Param("param") ScreenDTO screenDTO);

    /**
     * 根据编码查询下级区域数据
     * @param parentCode
     * @return
     */
    List<String> getSubLevelCode(@Param("parentCode") List<String> parentCode);

    /**
     * 查询设备参数信息
     * @param deviceCode
     * @return
     */
    SysLntlagConfigVO getLntlagConfig(String deviceCode);

    List<DeviceInfoVO> getDeviceInfoList(DeviceInfoReq deviceInfoReq);

    /**
     * 通过传感器设备编码获取租户
     *
     * @return
     * @author chenmf
     * @date 2022/5/10
     * @version
     */
    List<OrderCustomerVO> getCustomersBySensorDevice(@Param("deviceCode")String[] deviceCode, @Param("applicationCode")String applicationCode,@Param("categoryCode") int categoryCode,@Param("taskId") String taskId,@Param("alarmSource") String alarmSource,@Param("localNode") String localNode);

    /**
     * 通过taskId查询雷达告警模板
     */
    int getRadarTemplateCount(@Param("taskId")String taskId);

    /**
     * 通过批量传感器设备编码获取租户
     *
     * @return
     * @author chenmf
     * @date 2022/5/10
     * @version
     */
    List<OrderCustomerVO> getCustomersBySensorDeviceList(@Param("list") List<String> deviceCodeList);

    /**
     * 查询物联网设备信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/19
     * @version
     */
    DeviceSiteVO queryIotDevice(@Param("deviceCode") String deviceCode);

    /**
     * 查询雷达设备信息
     *
     * @return
     * @author daiyong
     * @date 2022/9/15
     * @version
     */
    DeviceSiteVO queryRadarDevice(@Param("tenantId") String tenantId,@Param("industryCode") String industryCode,@Param("deviceCode") String deviceCode);

    /**
     * 查询物联网设备/雷达设备信息
     *
     * @return
     * @author luo.zhenghong
     * @date 2022/8/10
     * @version
     */
    DeviceSiteVO querySensorDevice(@Param("deviceCode") String deviceCode,@Param("categoryCode") int categoryCode);

    /**
     *  通过设备编码获取有效参考点
     *
     * @return
     * @author chenmf
     * @date 2022/5/23
     * @version
     */
    List<DeviceRefPointVO> getDevicePointsValid(@Param("deviceCode") String deviceCode);

    List<DeviceRefPointVO> getDevicePointsValidBatch(@Param("deviceCodeList") List<String> deviceCodeList);

    /**
     * 查询摄像机信息
     * @param deviceCodeList
     * @return
     */
    List<DeviceSiteVO> queryDeviceList(@Param("deviceCodeList") List<String> deviceCodeList);

    /**
     * @description: 查询特征点
     * @author: yongqi.han
     * @date: 2022/7/4
     * @param featureVO
     * @return: List<DeviceFeatureVO>
     */
    List<DeviceFeatureVO> getFeatureListByDevice(DeviceFeatureVO featureVO);


    String getDeviceCodeCollectByDeviceCodeAndObjType(@Param("collObjCode") String collObjCode, @Param("collObjType") String collObjType, @Param("userId") String userId);

    /**
     * 查询设备收藏表
     */
    List<String> getDeviceCodeCollectByDeviceCodes(@Param("collObjCodeList") List<String> collObjCodeList, @Param("collObjType") String collObjType, @Param("userId") String userId);

    /**
     * 批量查询物联网设备信息
     *
     * @return
     * @author luo.zhenghong
     * @date 2022/8/4
     * @version
     */
    List<DeviceSiteVO> queryIotDeviceList(@Param("list") List<String> deviceCodeList);

    /**
     * 根据任务id查询预置位名称
     * @param taskId
     * @return
     */
    List<String> getPresetNameByTaskId (String taskId);

    /**
     * 查询租户注册信息
     * @param customerCodeList
     * @return
     */
    List<AuthRegisterPush> getAuthRegister(List<String> customerCodeList);

    /**
     * 新增调用日志
     * @return
     */
    int insertLogs(Map map);

    @DS(EbikeDataBaseConstant.BASE)
    List<DeviceInfoByPush> getDevInfo(List<String> deviceList);

    List<String> getValidDev(String customerCode);

    List<String> getValidChn(String customerCode);

    List<String> getAlarmOperateByRole(@Param("provinceId")String provinceId,@Param("cityId")String cityId);


    List<OrderCustomerVO> queryRealNowVersion(List<OrderCustomerVO> customers);

    String getPlatformNameByPlatformCode(@Param("platformCode") String platformCode);

    @DS(EbikeDataBaseConstant.BASE)
    List<String> getAllDownPlatformCode();

    /**
     * 根据算法编码获取算法名称
     * @param algorithmCode
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    String getAlgorithmName(@Param("algorithmCode") String algorithmCode);
    @DS(EbikeDataBaseConstant.BASE)
    List<AlgorithmInfoDTO> getAlgorithmNameList(@Param("list") List<String> algorithmCodeList);
}
