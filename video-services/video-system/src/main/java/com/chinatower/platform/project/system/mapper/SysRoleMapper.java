package com.chinatower.platform.project.system.mapper;

import java.util.List;
import java.util.Map;

import com.chinatower.bdm.api.entity.GridKeeperVO;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.admin.SysTenantTrade;
import com.chinatower.platform.framework.web.domain.server.Sys;
import com.chinatower.platform.project.system.domain.dto.RoleDTO;
import com.chinatower.platform.project.system.domain.vo.SysRoleQuery;
import com.chinatower.system.api.model.ScreenMenuConfVO;
import com.chinatower.system.api.model.SysRoleScreenVO;
import com.chinatower.system.api.model.UserRoleVO;
import com.chinatower.system.api.request.role.QueryRoleRequest;
import org.apache.ibatis.annotations.Param;

/**
 * 角色表 数据层
 *
 * @author chinatower
 */
public interface SysRoleMapper
{
    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleList(@Param("role") SysRoleQuery role, @Param("user") SysUser user);

    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleListTenant(@Param("role") QueryRoleRequest role);


    /**
     * 查询铁塔角色
     * @param role
     * @return
     */
    public List<SysRole> selectRoleListTower(@Param("role") SysRoleQuery role);

    /**
     * 根据行业查询角色信息
     * @param tenantTrades 行业list
     * @return List<SysRole>
     */
    List<SysRole> selectPublicOrTradeRole(@Param("tenantTrades") List<SysTenantTrade> tenantTrades);

    /**
     * 查询行业版本角色
     * @param tenantTrades 行业
     * @return
     */
    List<SysRole> selectRoleByTrades(@Param("tenantTrades") List<SysTenantTrade> tenantTrades);

    /**
     * 查询铁塔角色和公共角色
     * @return
     */
    List<SysRole> selectTowerAndPublicRole();


    List<SysRole> selectOwnOrTenantRole(@Param("userId") String userId, @Param("tenantId") String tenantId,@Param("industryCode") String industryCode,@Param("appVerCode") String appVerCode);
    /**
     * 根据铁塔用户ID查询铁塔角色
     *
     * @param user 用户ID
     * @return 角色列表
     */
    public List<SysRole> selectRolePermissionByUserId(SysUser user);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    public List<SysRole> selectRoleAll();

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param user 用户ID
     * @return 选中角色ID列表
     */
    public List<SysRole> selectRoleListByUserId(SysUser user);


    /**
     * 根据用户信息获取角色信息
     *
     * @param userRoleVO
     * @return
     */
    public List<SysRole> selectRoleListByUserInfo(UserRoleVO userRoleVO);

    /**
     * 根据标识查询用户下的角色
     *
     * @param user 用户ID
     * @return 选中角色ID列表
     */
    public List<SysRole> selectRoleListByCondition(SysUser user);

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userIds 用户ID集合
     * @param industryCode 行业编码
     * @return 选中角色ID列表
     */
    public List<SysRole> selectRoleListByUserIds(@Param("userIds") List<String> userIds,@Param("industryCode") String industryCode);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public SysRole selectRoleById(@Param("role") RoleDTO roleId);


    /**
     * 通过角色key查询角色
     *
     * @param roleKey 角色key
     * @return 角色对象信息
     */
    public SysRole selectRoleByKey(String roleKey);

    /**
     * 根据用户ID查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    public List<SysRole> selectRolesByUserName(SysUser userName);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色名称
     * @return 角色信息
     */
    public List<SysRole> checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限是否唯一
     *
     * @param roleKey 角色权限
     * @return 角色信息
     */
    public SysRole checkRoleKeyUnique(String roleKey);

    /**
     * 修改角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public int updateRole(SysRole role);

    /**
     * 新增角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public int insertRole(SysRole role);

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteRoleById(Long roleId);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    public int deleteRoleByIds(Long[] roleIds);



    List<SysRole> selectRoleByIds(List<String> roleIds);


    List<SysRole> selectRolePublicList(SysRole role);

    /**
     * 根据行业查询公共角色
     * @param role
     * @return
     */
    List<SysRole> selectRoleIndustryList(SysRole role);

    SysRole getLatelyRole(@Param("userType") String userType,@Param("roleType") String roleType,@Param("key") String key);

    List<SysRole> selectRoleLists(SysUser user);



    /**
     * 查询角色大屏关系
     * @param sysRoleScreenVO
     * @return
     */
    List<SysRoleScreenVO> getRoleScreenRef(SysRoleScreenVO sysRoleScreenVO);


    /**
     * 获取当前用户大屏列表
     * @param sysRoleScreenVO
     * @return
     */
    List<ScreenMenuConfVO> getCurrentRoleScreen(SysRoleScreenVO sysRoleScreenVO);

    /**
     * 获取当前用户大屏列表
     * @param sysRoleScreenVO
     * @return
     */
    List<String> getCurrentRoleScreenRoleId(SysRoleScreenVO sysRoleScreenVO);



    /**
     * 功能说明: 保存角色大屏关系
     * @author liuxt
     * @param sysRoleScreenVOList
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
    int insertRoleScreenRef(List<SysRoleScreenVO> sysRoleScreenVOList);


    /**
     * 功能说明: 物理删除角色大屏关系
     * @author liuxt
     * @param sysRoleScreenVO
     * @return 删除结果
     * @throws
     * @throws
     * @requestID       *
     */
    int deleteRoleScreenRef(SysRoleScreenVO sysRoleScreenVO);


    /**
     * 功能说明: 逻辑删除角色大屏关系
     * @author liuxt
     * @param sysRoleScreenVOList
     * @return 删除结果
     * @throws
     * @throws
     * @requestID       *
     */
    int logicDeleteRoleScreenRef(List<SysRoleScreenVO> sysRoleScreenVOList);


    GridKeeperVO getUserByLoginName(String loginName);


    List<SysTenantTrade> selectTenantTradeByTenantId(@Param("tenantId") String tenantId,@Param("platformCode") String platformCode);


}
