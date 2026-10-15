package com.chinatower.event.repository;

import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.dto.*;
import com.chinatower.event.dto.leadertree.LeaderByTenantIdTreeReq;
import com.chinatower.event.entity.dto.AlarmOrderByDeviceDTO;
import com.chinatower.event.entity.dto.SysInfo;
import com.chinatower.event.entity.dto.SysUserDTO;
import com.chinatower.event.entity.dto.TenantDeviceRelDTO;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.event.entity.vo.TenantDeviceEventRelVO;
import com.chinatower.event.vo.*;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.model.vo.DeviceRefPointVO;

import java.util.List;
import java.util.Map;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: IDeviceInfoRepository.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/28 09:33
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public interface IDeviceRepository {

    /**
     * 通过设备编码获取租户
     * @param deviceCode
     * @return
     */
    List<OrderCustomerVO> getCustomersByDevice(String deviceCode, String applicationCode, ReceiveAlarmVO receiveAlarmVO);

    /**
     * 通过设备编码和算法编码获取租户
     * @param deviceCode
     * @param algorithmCode
     * @return
     */
    List<OrderCustomerVO> getCustomersList(String deviceCode, String algorithmCode, String taskId,String alarmSource,String localNode);

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
    List<SysUser> getUserInfoByRoleIds(List<String> roleIdList, String tenantId);

    /**
     * 根据角色ID查询角色用户
     * @param roleIdList
     * @param tenantId
     * @return
     */
    List<SysUser> getUserInfoByLongRoleIds(List<Long> roleIdList, String tenantId);

    /**
     * 根据角色ID&行业查询角色用户
     * @param roleIdList
     * @param tenantId
     * @return
     */
    List<SysUser> getUserInfoByLongRoleIdsAndIndustryCode(List<Long> roleIdList, String tenantId, String industryCode);


    /**
     * 查询客户管理员信息
     * @param tenantId
     * @return
     */
    List<SysUserVO> getTenantAdminUser(String tenantId);

    /**
     * 根据用户ID查询用户信息
     * @param userIdList
     * @return
     */
    List<SysUserVO> getUserInfoByUserIds(List<String> userIdList);

    /**
     * 根据用户ID查询用户信息
     * @param userIdList,industryCode
     * @return
     */
    List<SysUserVO> getUserInfoByUserIdsAndIndustryCode(List<String> userIdList, String industryCode);

    /**
     * 查询设备别名
     * @param tenantDeviceRelDTOList
     * @return
     */
    List<TenantDeviceEventRelVO> getDeviceRel(List<TenantDeviceRelDTO> tenantDeviceRelDTOList);

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
     *
     * @param deviceCode
     * @return
     */
    DeviceSiteVO queryDevice(String deviceCode, String channelCode);


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
    List<SysDept> getSysDeptByDeptIds(List<String> deptIdList);

    /**
     * 获取行政区划列表
     * @return
     */
    List<Map> getAreaInfoList();

    /**
     * 获取缓存行政区划名称
     * @param areaCodes
     * @return
     */
    Map<String, String> getAreaNameCaffeineMap(List<String> areaCodes);

    /**
     * 获取告警类型
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
    List<Map> getDeviceRel(List<OrderInfoVO> list, String tenantId);

    /**
     * 获取通道别名
     * @param list
     * @param tenantId
     * @return
     */
    List<Map> getChannelRel(List<OrderInfoVO> list, String tenantId);

    /**
     * 查询设备相关信息
     *
     * @return
     * @author chenmf
     * @date 2022/3/10
     * @version
     */
    List<AlarmOrderByDeviceDTO> queryAlarmOrderByDevice(AlarmOrderDTO dto);

    /**
     * 查询物联设备相关信息
     *
     * @return
     * @author lyy
     * @date 2024/3/10
     * @version
     */
    List<AlarmOrderByDeviceDTO> getAlarmOrderByIotDevice(AlarmOrderDTO dto);

    /**
     * 查询领导批示数据
     * @param dto
     * @return
     */
    List<SysUserVO> getLeaderByTenantId(SysUserDTO dto);


    /**
     * 查询领导批示数据（根据组织节点展示树状数据）
     * @param dto
     * @return
     */
    List<SysInfo> getLeaderByTenantIdShowTree(LeaderByTenantIdTreeReq dto);

    /**
     * 查询摄像头集合
     * @param siteRequestVO
     * @return
     */
    List<SiteRequestVO> getDeviceList(SiteRequestVO siteRequestVO);

    /**
     * 查询设备参数信息
     * @param deviceCode
     * @return
     */
    SysLntlagConfigVO getLntlagConfig(String deviceCode);

    /**
     * 功能描述 通过 厂商 查询摄像机
     *
     * @param deviceInfoReq
     * @return List<DeviceInfoVO>
     * @author Chensw
     * @date 2022/4/26 15:58
     */
    List<DeviceInfoVO> getDeviceInfoList(DeviceInfoReq deviceInfoReq);

    /**
     * 通过传感器设备编码获取租户
     *
     * @return
     * @author chenmf
     * @date 2022/5/10
     * @version
     */
    List<OrderCustomerVO> getCustomersBySensorDevice(String[] deviceCode, String applicationCode, int categoryCode,String taskId, String alarmSource,String localNode);

    /**
     * 通过taskId查询雷达告警模板
     */
    int getRadarTemplateCount(String taskId);

    /**
     * 通过批量传感器设备编码获取租户
     *
     * @return
     * @author luo.zhenghong
     * @date 2022/8/4
     * @version
     */
    List<OrderCustomerVO> getCustomersBySensorDeviceList(List<String> deviceCodeList);

    /**
     * 查询物联网设备信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/19
     * @version
     */
    DeviceSiteVO queryIotDevice(String deviceCode);

    /**
     * 查询雷达设备信息
     *
     * @return
     * @author daiyong
     * @date 2022/9/15
     * @version
     */
    DeviceSiteVO queryRadarDevice(String tenantId, String industryCode, String deviceCode);

    /**
     * 查询物联网设备/雷达设备信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/19
     * @version
     */
    DeviceSiteVO querySensorDevice(String deviceCode, int categoryCode);

    /**
     *  通过设备编码获取有效参考点
     *
     * @return
     * @author chenmf
     * @date 2022/5/23
     * @version
     */
    List<DeviceRefPointVO> getDevicePointsValid(String deviceCode);

    /**
     * 批量查询有效参考点
     */
    List<DeviceRefPointVO> getDevicePointsValidBatch(List<String> deviceCodeList);

    /**
     * 查询摄像机信息
     * @param deviceCodeList
     * @return
     */
    List<DeviceSiteVO> queryDeviceList(List<String> deviceCodeList);

    /**
     * @description: 特征点处理
     * @author: yongqi.han
     * @date: 2022/7/4
     * @param translnglatRep
     * @return: TranslnglatVO
     */
    TranslnglatVO handlerFeaturePT(TranslnglatVO translnglatRep,String deviceCode,String alarmCode);

    String getDeviceCodeCollectByDeviceCodeAndObjType(String collObjCode, String collObjType, String userId);

    /**
     * 通过物联设备编码集合批量查询物联设备信息
     * @param deviceCodeList
     * @return
     */
    List<DeviceSiteVO> queryIotDeviceList(List<String> deviceCodeList);

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

    List<DeviceInfoByPush> getDevInfo(List<String> deviceList);

    List<String> getValidDev(String customerCode);

    List<String> getValidChn(String customerCode);

    List<String> getAlarmOperateByRole(String provinceId,String cityId);



    /**
     * 通过租户和行业查询订购表中当前正式生效的版本  若查询到多条 为非正常场景  按照创建时间倒序查询第一个即可
     * @param customers
     * @return
     */
    List<OrderCustomerVO> queryRealNowVersion(List<OrderCustomerVO> customers);

    String getPlatformNameByPlatformCode(String platformCode);

    List<String> getAllDownPlatformCode();

    /**
     * 查询租户管理员
     * @param tenantId
     * @return
     */
    SysInfo queryAdminUser(String tenantId);

}
