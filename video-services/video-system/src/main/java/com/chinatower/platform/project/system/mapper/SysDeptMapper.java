package com.chinatower.platform.project.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.custom.entity.AlarmTypeSyncDataVO;
import com.chinatower.platform.project.custom.entity.OrganAlarmTypeVO;
import com.chinatower.platform.project.custom.entity.ThirdPartyAlarmTypeSyncDataVO;
import com.chinatower.platform.project.system.domain.dto.SysDeptHisMongoDTO;
import com.chinatower.platform.project.system.domain.dto.JPushUserDataDTO;
import com.chinatower.platform.project.system.domain.dto.TreeselectBytenantIdDTO;
import com.chinatower.system.api.model.SysDeptAreaVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 部门管理 数据层
 *
 * @author chinatower
 */
public interface SysDeptMapper
{
    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectDeptList(SysDept dept);

    /**
     * 查询租户组织数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<SysDept> selectTenantDeptList(SysDept dept);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    public List<Integer> selectDeptListByRoleId(String roleId);

    public List<String> selectTennatListByRoleId(String roleId);

    /**
     * 根据租户信息查询租住下的组织与用户数据
     * @param dept
     * @return
     */
    public List<SysDept> getTreeselectByTenantId(TreeselectBytenantIdDTO dept);

    List<SysDept> selectTreeselectByTenantId(TreeselectBytenantIdDTO dept);
    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    public SysDept selectDeptById(@Param("deptId") String deptId,@Param("industryCode") String industryCode,@Param("appVerCode")String appVerCode);

    public SysDept selectDeptOnlyById(@Param("deptId") String deptId);

    public List<String> selectDeptByIds(@Param("deptIds")List<String> deptIds);

    public List<SysDept> selectDeptsByIds(@Param("deptIds")List<String> deptIds);
    /**
     *
     * @param deptId
     * @return
     */
    public SysDept selectDeptByIdAndIndustryCode(@Param("deptIds") String deptId,@Param("industryCode") String industryCode,@Param("appVerCode") String appVerCode);


    /**
     * 根据ID查询所有子部门
     *
     * @param deptId 部门ID
     * @return 部门列表
     */
    public List<SysDept> selectChildrenDeptById(String deptId);

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int hasChildByDeptId(String deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int checkDeptExistUser(String deptId);

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 父部门ID
     * @return 结果
     */
    public Long checkDeptNameUnique(SysDept dept);

    /**
     * 新增部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int insertDept(SysDept dept);

    /**
     * 修改部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int updateDept(SysDept dept);

    /**
     * 修改所在部门的父级部门状态
     *
     * @param dept 部门
     */
    public void updateDeptStatus(SysDept dept);

    /**
     * 修改子元素关系
     *
     * @param depts 子元素
     * @return 结果
     */
    public int updateDeptChildren(@Param("depts") List<SysDept> depts);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int deleteDeptById(String deptId);


    /**
     * 查询两个部门间是否存在关联
     * @param deptId
     * @param parentId
     * @return
     */
    int checkIsRelation(@Param("deptId") String deptId,@Param("parentId") String parentId);

    List<SysDept> selectALLDeptListByDeptId(String deptId);

    /**
     * 查询铁塔的下级部门
     * @param deptId
     * @param level
     * @return
     */
    List<SysDept> selectTowerDeptByLevel(@Param("deptId") String deptId,@Param("level") Integer level);

    /**
     * 根据租户与行业查询所有的租户部门
     * @param user
     * @return
     */
    List<SysDept> selectAllTanentDept(SysUser user);

    /**
     * 查询租户自定义的部门数据
     * @param user
     * @return
     */
    List<SysDept> selectCustomTanentDept(SysUser user);


    /**
     * 查询租户下级部门
     * @param deptId
     * @return
     */
    List<SysDept> selectTenantChildDept(@Param("deptId") String deptId);
    /**
     * 查询租户本部门信息
     * @param user
     * @return
     */
    List<SysDept> selectTenantOwnerDept(SysUser user);
    /**
     * 查询租户的数据权限
     * @param sysUser
     * @return
     */
    List<String> selectTanentDataScop(SysUser sysUser);

    /**
     * 根据部门id,状态获取子部门集合
     * @param dept
     * @author 李峥
     * @return List<SysDept>
     */
    List<SysDept> selectChildrenByDeptId(SysDept dept);

    /**
     * 根据主键id,获取部门集合
     * @param id
     * @author 李峥
     * @return
     */
    SysDept selectById(Long id);

    List<SysDept> selectTenantTop(SysDept dept);

    List<SysDept> selectByParentId(@Param("parentId")String parentId);

    /**
     * 根据租户id更新sys_dept表中的租户名称
     * @param dept
     */
    void updateByTenantId(SysDept dept);


    /**
     * 查询租户部门顶级结构信息
     * @author 李峥
     * @param dept 部门信息
     * @return 返回平级结构信息集合
     */
    List<SysDept> selectTenantTop2Video(SysDept dept);

    List<SysDept> selectDeptAndUserTree(@Param("tenantId") String tenantId,@Param("deptId") String deptId,@Param("roleId") Long roleId,@Param("industryCode")String industryCod,@Param("appVerCode") String appVerCode);

    Integer countNormalDept(@Param("parentId") String parentId);

    List<SysDeptAreaVO> selectDeptAndAreaByLevel(@Param("deptId") String deptId,@Param("level") Integer level);

    SysDeptAreaVO selectDeptAreaOnlyById(@Param("deptId") String deptId);

    List<SysDeptAreaVO> selectAreaByDeptManage(@Param("deptAreaCodes") List<String> areaCodeList);

    List<String> selectDeptAreaCodeByProvince(String deptId);

    List<String> selectDeptAreaCodeByCity(String deptId);

    List<String> selectDeptAreaCodeByCounty(String deptId);

    List<String> selectDirectCityAreaCodeByProvince(String deptId);

    List<String> selectDirectCityAreaCodeByCity(String deptId);

    /**
     * 根据管理区域编码和部门等级查询区域信息
     * @param deptId 管理区域编码
     * @param deptLevel 部门等级
     * @return
     */
    List<SysDeptAreaVO> selectAreaByDeptAndLevel(@Param("deptId") String deptId, @Param("deptLevel") Integer deptLevel);

    /**
     *
     * @param tenantId
     * @param industryCode
     * @return 查询租户行业下的用户
     */
    List<JPushUserDataDTO> selectTenantUserList(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode);
    Integer selectTenantUserListCount(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode);



    /**
     * 批量新增组织与告警类型关系
     * @param list
     * @return
     */
    int insertOrganAlarmTypeBatch(List<OrganAlarmTypeVO> list);


    /**
     * 批量删除组织与告警类型关系
     * @param list
     * @return
     */
    int delOrganAlarmTypeBatch(List<OrganAlarmTypeVO> list);



    /**
     * 查询全部租户组织数据
     *
     * @param
     * @return 部门信息集合
     */
    public List<OrganAlarmTypeVO> selectAllDeptList();


    List<OrganAlarmTypeVO> devByTenantIdCount(List<OrganAlarmTypeVO> list);

    List<OrganAlarmTypeVO> uavByTenantIdCount(List<OrganAlarmTypeVO> list);

    List<OrganAlarmTypeVO> iotDevByTenantIdCount(@Param("list") List<OrganAlarmTypeVO> list,@Param("categoryCode") String categoryCode);


    /**
     * 根据订购算法查询告警类型
     * @param list
     * @return
     */
    List<OrganAlarmTypeVO> findAlarmTypeByOrder(List<OrganAlarmTypeVO> list);


    /**
     * 根据告警来源查询告警类型(铁塔)
     * @param list
     * @return
     */
    List<OrganAlarmTypeVO> findAlarmTypeBySourceTower(@Param("list") List<OrganAlarmTypeVO> list);

    /**
     * 根据告警来源查询告警类型(客户)
     * @param list
     * @return
     */
    List<OrganAlarmTypeVO> findAlarmTypeBySourceTenant(List<OrganAlarmTypeVO> list);

    /**
     * 查询第三方告警类型
     * @param list
     * @return
     */
    @DS("platform")
    List<OrganAlarmTypeVO> getThirdPartyAlarmTypes(List<OrganAlarmTypeVO> list);


    List<AlarmTypeSyncDataVO> getAlarmTypeList(List<AlarmTypeSyncDataVO> list);


    List<OrganAlarmTypeVO> selectOrganList(List<AlarmTypeSyncDataVO> list);

    @DS("platform")
    List<ThirdPartyAlarmTypeSyncDataVO> getThirdPartyAlarmTypeList(List<ThirdPartyAlarmTypeSyncDataVO> list);

    List<OrganAlarmTypeVO> getOrganAlarmTypeList(List<OrganAlarmTypeVO> list);

    List<String> getOrganListByAlarmType(@Param("tenantId") String tenantId,@Param("industryCode") String industryCode,
                                         @Param("warningSource") String warningSource,@Param("typeValue") String typeValue,
                                         @Param("list") Set<String> set);

    List<String> getDeptIdList(SysDept dept);

    List<String> getDeptByCurrentDeptId(SysDept sysDept);

    List<String> getTenantByTowerDeptId(List<String> deptIds);

    List<SysDeptHisMongoDTO> selectByDeptId(@Param("deptId") String deptId);

    int physicalDeleteByDeptId(@Param("deptId") String deptId);
}
