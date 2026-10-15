package com.chinatower.order.mapper;

import com.chinatower.mapper.TowerBaseMapper;
import com.chinatower.order.dto.SysTenantInfoHisMongoDTO;
import com.chinatower.order.dto.SysUserHisMongoDTO;
import com.chinatower.order.dto.SysUserRole;
import com.chinatower.order.dto.UserRoleDTO;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysTenantTrade;
import com.chinatower.platform.entity.admin.SysUser;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SysUserMapper extends TowerBaseMapper<SysUser> {

    /**
     * 新增用户数据
     *
     * @return int
     * @param: user
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    int insertUser(SysUser user);

    /**
     * 批量新增用户角色信息
     *
     * @return void
     * @param: list
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    void batchUserRole(List<SysUserRole> list);

    /**
     * 查询行业版本角色
     *
     * @return java.util.List<com.chinatower.platform.entity.admin.SysRole>
     * @param: tenantTrades
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    List<SysRole> selectRoleByTrades(List<SysTenantTrade> tenantTrades);

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    int checkUserNameUnique(@Param("userName") String userName);

    /**
     * 查询userId
     *
     * @param userName 用户名称
     * @return 结果
     */
    String findUserIdByUserName(@Param("userName") String userName);

    /**
     * 根据用户id更新组织id
     * @param userId 用户id
     * @param deptId 组织id
     * @return 影响条数
     */
    int updateDeptByUserId(@Param("userId")String userId, @Param("deptId")String deptId, @Param("password") String password);

    /**
     * 根据租户id查询用户表中是否已存在该租户管理员账号
     *
     * @param tenantId 租户id
     * @return
     */
    int checkTenantAdminUnique(@Param("tenantId") String tenantId);

    /**
     * 根据行业及行业编码查询角色
     *
     * @param industryCode
     * @param appVerCode
     * @return
     */
    List<Long> getRoleByIndustryCode(@Param("industryCode") String industryCode, @Param("appVerCode") String appVerCode);

    List<UserRoleDTO> getRoleByIndustryCodeBatch(@Param("industryCodeList") List<String> industryCodeList,
                                                 @Param("appVerCodeList") List<String> appVerCodeList);

    List<Long> getRoleByUserId (@Param("userId") String userId);

    int deleteByTenantId(String tenantId);

    SysUser getAdminByTenantId(String tenantId);

    List<SysUser> selectByTenantId(@Param("tenantId")String tenantId);

    SysUser selectByUserId(@Param("userId") String userId);

    int countAdminUserNameByTenantId(@Param("tenantId") String tenantId);

    String queryNodeAlias(@Param("nodeCode") String nodeCode);

    /**
     * 查询参数配置信息
     *
     * @param config 参数配置信息
     * @return 参数配置信息
     */
    SysConfig selectConfig(SysConfig config);

    List<SysUserHisMongoDTO> selectByUserIdList(@Param("userIdList")List<String> userIdList);

    int deletePhysicalByUserIdList(@Param("userIdList")List<String> userIdList);

}
