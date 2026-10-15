package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysRole;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysUserMapper {
    /**
     * 根据条件分页查询用户列表
     *
     * @param sysUser 用户信息
     * @return 用户信息集合信息
     */
    List<SysUser> selectUserList(SysUser sysUser);

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param
     * @return 选中角色ID列表
     */
    public List<SysRole> selectRoleListByUserId(SysUser user);

    /**
     * 查询参数配置信息
     *
     * @param config 参数配置信息
     * @return 参数配置信息
     */
    public SysConfig selectConfig(SysConfig config);

    @DataScope(tenantColumn = "a.tenant_id")
    List<SysUser> selectUser(SysUser sysUser);

    /**
     * 查询租户下所有用户信息
     * @return
     */
    @DS(TableEnum.BASE)
    List<UserInfoDto> queryUserListInfoByUserNameList(List<String> userIdList);
}
