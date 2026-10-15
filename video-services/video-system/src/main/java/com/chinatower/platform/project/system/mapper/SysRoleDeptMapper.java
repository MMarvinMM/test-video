package com.chinatower.platform.project.system.mapper;

import java.util.List;
import com.chinatower.platform.project.system.domain.SysRoleDept;
import com.chinatower.system.api.resp.SysRoleDeptResponse;
/**
 * 角色与部门关联表 数据层
 *
 * @author chinatower
 */
public interface SysRoleDeptMapper
{
    /**
     * 通过角色ID删除角色和部门关联
     *
     * @param roleId 角色ID
     * @return 结果
     */
    public int deleteRoleDeptByRoleId(Long roleId);

    /**
     * 批量删除角色部门关联信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteRoleDept(Long[] ids);

    /**
     * 查询部门使用数量
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public int selectCountRoleDeptByDeptId(String deptId);

    /**
     * 批量新增角色部门信息
     *
     * @param roleDeptList 角色部门列表
     * @return 结果
     */
    public int batchRoleDept(List<SysRoleDept> roleDeptList);

    /**
     * 查询某种类型角色对应的部门
     * @author 李峥
     * @return List<SysRoleDeptResponse>
     */
    List<SysRoleDeptResponse> selectAllRoleDept(String roleType);
}
