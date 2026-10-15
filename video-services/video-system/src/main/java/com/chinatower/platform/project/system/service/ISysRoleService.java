package com.chinatower.platform.project.system.service;

import java.util.List;
import java.util.Set;

import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.domain.dto.RoleDTO;
import com.chinatower.platform.project.system.domain.vo.SaveRoleParam;
import com.chinatower.platform.project.system.domain.vo.SysRoleQuery;
import com.chinatower.system.api.model.ScreenMenuConfVO;
import com.chinatower.system.api.model.SysRoleScreenVO;
import com.chinatower.system.api.request.role.QueryRoleRequest;
import com.chinatower.system.api.resp.SysRoleDeptResponse;
import org.apache.ibatis.annotations.Param;

/**
 * 角色业务层
 *
 * @author chinatower
 */
public interface ISysRoleService
{
    /**
     * 根据条件分页查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleList(SysRoleQuery role);


    /**
     * 查询铁塔角色
     * @param role
     * @return
     */
    public List<SysRole> selectRoleListTower(SysRoleQuery role);


    /**
     * 查询租户角色列表
     * @param role
     * @return
     */
    public List<SysRole> selectRoleListTenant(QueryRoleRequest role);

    /**
     * 仅依据租户id筛选角色（告警流程配置使用）
     * 原接口铁塔视角传租户id查询到的role与租户视角不同
     */
    List<SysRole> flowRoleList(SysRoleQuery role);

    /**
     * 查询租户的角色
     * @param tenantId
     * @return
     */
    List<SysRole> selectRoleByTenantId(String tenantId);

    /**
     * 根据用户ID查询角色
     *
     * @param user 用户ID
     * @return 权限列表
     */
    public Set<String> selectRolePermissionByUserId(@Param("user") SysUser user);

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
     * 根据标识查询用户下的角色
     *
     * @param user 用户ID
     * @return 选中角色ID列表
     */
    public List<SysRole> selectRoleListByCondition(SysUser user);

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public SysRole selectRoleById(RoleDTO roleId);


    /**
     * 通过角色KEY查询角色
     *
     * @param roleKey 角色key
     * @return 角色对象信息
     */
    public SysRole selectRoleByKey(String roleKey);

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    public String checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    public String checkRoleKeyUnique(SysRole role);

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    public void checkRoleAllowed(SysRole role);

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int countUserRoleByRoleId(Long roleId);
    
    
    /**
     * 通过角色ID查询角色使用数量（去除已经删除的用户id）
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int countUserRoleByRoleIdExcludeDeledUser(Long roleId);

    /**
     * 新增保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public void insertRole(SaveRoleParam role);

    /**
     * 修改保存角色信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public int updateRole(SaveRoleParam role);

    /**
     * 修改角色状态
     *
     * @param role 角色信息
     * @return 结果
     */
    public int updateRoleStatus(SysRole role);

    /**
     * 修改数据权限信息
     *
     * @param role 角色信息
     * @return 结果
     */
    public void authDataScope(SaveRoleParam role);

    /**
     * 更新角色菜单按钮权限和数据查询权限
     * @param role 角色信息
     * @return 结果
     */
    void updateRolePermission(SaveRoleParam role);

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

    /**
     * 查询某种类型角色对应的部门
     * @author 李峥
     * @return List<SysRoleDeptResponse>
     */
    List<SysRoleDeptResponse> selectAllRoleDept(String roleType);

    List<SysRole> selectRolePublicList(SysRole role);

    List<SysRole> selectRoleLists(SysUser user);


    /**
     * 查询角色大屏关系
     */
    List<SysRoleScreenVO> getRoleScreenRef(SysRoleScreenVO sysRoleScreenVO);




    /**
     * 获取当前用户大屏列表
     */
    SysRoleScreenVO getCurrentRoleScreen(SysRoleScreenVO sysRoleScreenVO);



    /**
     * 功能说明: 保存角色大屏关系
     * @author liuxt
     * @param sysRoleScreenVOList
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
    int saveRoleScreenRef(List<SysRoleScreenVO> sysRoleScreenVOList,Long roleId,String flag);


    /**
     * 功能说明: 逻辑删除角色大屏关系
     * @author liuxt
     * @param sysRoleScreenVOList
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
    int logicDeleteRoleScreenRef(List<SysRoleScreenVO> sysRoleScreenVOList);



}
