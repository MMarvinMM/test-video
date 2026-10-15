package com.chinatower.platform.project.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.chinatower.platform.project.system.domain.SysUserRole;

/**
 * 用户与角色关联表 数据层
 *
 * @author chinatower
 */
public interface SysUserRoleMapper
{
    /**
     * 通过用户ID删除用户和角色关联
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserRoleByUserId(String userId);


    /**
     * 通过用户ID查询用户的角色
     *
     * @param userId 用户ID
     * @return 结果
     */
    List<String> selectUserRoleByUserId(String userId);

    /**
     * 批量删除用户和角色关联
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserRole(String[] ids);

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
     * 通过角色ID查询角色使用数量（去除已经删除的用户id）
     *
     * @param roleIdList 角色ID列表
     * @return 结果
     */
    public int countUserRoleByRoleIdListExcludeDeledUser(List<Long> roleIdList);

    /**
     * 批量新增用户角色信息
     *
     * @param userRoleList 用户角色列表
     * @return 结果
     */
    public int batchUserRole(List<SysUserRole> userRoleList);

    /**
     * 删除用户和角色关联信息
     *
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    public int deleteUserRoleInfo(SysUserRole userRole);
    /**
     *
     * @param userId 用户ID
     * @return 结果
     */
    public List<String> selectUserRoleList(@Param("userId")Long userId);
    /**
     * 批量取消授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    public int deleteUserRoleInfos(@Param("roleId") Long roleId, @Param("userIds") String[] userIds);
}
