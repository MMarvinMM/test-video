package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.*;
import com.chinatower.platform.model.vo.ScreenDataConfigCountVO;
import com.chinatower.platform.model.vo.ScreenDataConfigParticipationVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Auther: yuexuebin
 * @Date: 2021/10/29 15:06
 * @Description:
 *  鉴权基础服务，需要实现鉴权的服务都需要继承此接口
 */
@Component
public interface AuthBaseMapper {
    /**
     * 获取铁塔用户的管理区域编码
     * @param: userId
     * @return: java.util.List<java.lang.String>
     * @author: zheng.yan
     * @date: 2022/3/5
     * @version:
     */
    List<String> getTowerUserAreas(String userId);

    /**
     * 根据用户ID+area_level获取区域列表
     * @param userId
     * @param areaLevel
     * @return
     */
    List<String> getTowerByAreaLevel(@Param("userId") String userId,@Param("areaLevel") Integer areaLevel);

    /**
     * 根据用户ID，查询租户列表
     * @param deptId
     * @return
     */
    List<String> getTenantByTowerDeptId(@Param("deptIds") List<SysDept> deptId);

    /**
     * 查询下级部门
     * @param sysDept
     * @return
     */
    List<SysDept> getDeptByCurrentDeptId(SysDept sysDept);
    /**
     * 查询管理区域路径
     * @param: areaCodes
     * @return: java.util.List<java.lang.String>
     * @author: zheng.yan
     * @date: 2022/3/5
     * @version:
     */
    List<String> getTowerUserAreaPath(@Param("areaCodes") List<String> areaCodes);
    /**
     * 查询areaPath下所有区域
     * @param: areaPaths
     * @return: java.util.List<java.lang.String>
     * @author: zheng.yan
     * @date: 2022/3/8
     * @version:
     */
    List<String> getTowerUserMgmtAreas(@Param("areaPaths") List<String> areaPaths);
    /**
     *
     * 功能描述:
     * 获取组织下的摄像头权限
     * @param:
     * @return:
     * @auther: yuexuebin
     * @date: 2021/11/10 18:04
     */
    List<String> getOrgDevices(@Param("deptIds") Set<String> deptIds, @Param("alarmSource")String alarmSource, @Param("industryCode")String industryCode);

    /**
     * 查询自定义组织
     * @param user
     * @return
     */
    List<String> selectCustomTanentDept(SysUser user);

    /**
     * 查询租户下所有组织
     * @param user
     * @return
     */
    List<String> selectAllTanentDept(SysUser user);

    /**
     * 查询当前组织及下级组织
     * @param deptId
     * @return
     */
    List<String> selectTenantChildDept(@Param("deptId") String deptId);

    SysUser selectUserById(String userId);

    List<SysRole> selectRoleListByUserId(String userId);

//    List<String> getPermissionsForString(String userId, String dept);

    /**
     * 查询租户根节点组织
     * @param tenantId
     * @return
     */
    String getParentDeptId(String tenantId);

    /**
     *
     * @param deptId
     * @return
     */
    SysDept getDeptById(@Param("deptId") String deptId);

//    List<String> getDevideCodes(String appVerCode, String tenantId, String deptIdStr);
//
//    List<String> getSensorCodes(String appVerCode, String tenantId, String deptIdStr);
//
//    List<String> getSatelliteCodes(String appVerCode, String tenantId, String deptIdStr);
//
//    List<String> getPersonCodes(String appVerCode, String tenantId, String deptIdStr);

    List<String> getRootOrgsByTenantId(@Param("tenantId") String tenantId,@Param("orgId") String orgId);

    List<OrganStreetVO> getOrgnStreetVO(@Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds);

    List<OrganStreetVO> getStreetByRadarVO(@Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds);

    List<String> getUserIdsByDeptId(@Param("deptId") String deptId);


    ScreenDataConfigCountVO selectScreenDataConfigCount(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,
                                                        @Param("appVerCode") String appVerCode, @Param("screenId") String screenId, @Param("screenType") String screenType);

    List<String> selectScreenDataConfigCameraByConfigId(Long id);

    List<String> selectScreenDataConfigIotByConfigId(Long id);

    List<String> selectScreenDataConfigRadarByConfigId(Long id);

    List<String> selectScreenDataConfigSpeakerByConfigId(Long id);

    List<String> selectScreenDataConfigUavByConfigId(Long id);

    List<String> selectScreenDataConfigUavNestByConfigId(SysUser user);

    List<String> selectScreenDataConfigRecorderByConfigId(Long id);

    List<String> selectScreenDataConfigGridByConfigId(Long id);

    List<String> selectScreenDataConfigGridOperatorByConfigId(Long id);

    List<String> selectScreenDataConfigResourceByConfigId(Long id);

    List<ScreenDataConfigParticipationVO> selectScreenDataConfigAlarmTypeByConfigId(Long id);

    Integer devByTenantIdCount(ScreenDataConfigCountVO body);

    Integer uavByTenantIdCount(ScreenDataConfigCountVO body);

    Integer iotDevByTenantIdCount(ScreenDataConfigCountVO body);

    List<ScreenDataConfigParticipationVO> findAlarmTypeByOrder(ScreenDataConfigCountVO body);

    List<ScreenDataConfigParticipationVO> findAlarmTypeBySource(ScreenDataConfigCountVO body);

    List<ScreenDataConfigParticipationVO> getThirdPartyAlarmTypes(ScreenDataConfigCountVO body);

    List<ScreenDataConfigParticipationVO> selectDeptAlarmTypeConfigList(String tenantId, String industryCode, Set<String> deptIdSet);

    List<String> getOrgLayers(@Param("deptIds") Set<String> deptIds, @Param("industryCode")String industryCode);

    SysArea getRootAreaInfo();

    SysDept getRootDeptInfo();

    @DS("base")
    String getConfigByType(String customDataScope);
}

