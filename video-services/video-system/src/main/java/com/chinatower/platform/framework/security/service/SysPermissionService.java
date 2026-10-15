package com.chinatower.platform.framework.security.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.hutool.core.collection.CollectionUtil;
import com.chinatower.common.core.constant.Constants;
import com.chinatower.platform.common.utils.UserTypeEnum;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceOptAuthVO;
import com.chinatower.platform.project.system.mapper.DeviceInfoAuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.service.ISysMenuService;
import com.chinatower.platform.project.system.service.ISysRoleService;

/**
 * 用户权限处理
 *
 * @author iwhalecloud
 */
@Component
public class    SysPermissionService
{
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private DeviceInfoAuthMapper deviceInfoMapper;


    /**
     * 获取角色数据权限
     *
     * @param user 用户信息
     * @return 角色权限信息
     */
    public Set<String> getRolePermission(SysUser user)
    {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        user.setPageNum(null);
        user.setPageSize(null);
        if (user.isAdminNew())
        {
            roles.add("admin");
        }else if(user.getUserType().equalsIgnoreCase(UserTypeEnum.TANENT.getType())){
            roles.addAll(roleService.selectRolePermissionByUserId(user));
        } else if(user.getUserType().equalsIgnoreCase(UserTypeEnum.TOWER.getType()))
        {
            roles.addAll(roleService.selectRolePermissionByUserId(user));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user)
    {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            roles.add("*:*:*");
        }
        else
        {
            roles.addAll(menuService.selectMenuPermsByUserId(user));
        }
        return roles;
    }

    public DeviceOptAuthVO checkDeviceOptAuth(SysUser user) {
        DeviceOptAuthVO vo = new DeviceOptAuthVO();
        try{
            // 查看历史数据是否有权限
            List<String> userHisAuth = deviceInfoMapper.queryHisAuthList(user.getUserId());
            if(CollectionUtil.isNotEmpty(userHisAuth)){
                if (userHisAuth.contains(Constants.ONE)) {
                    vo.setCameraStatus(Constants.ONE);
                } else {
                    vo.setCameraStatus(Constants.ZERO);
                }
                if (userHisAuth.contains(Constants.TWO)) {
                    vo.setPresetStatus(Constants.ONE);
                } else {
                    vo.setPresetStatus(Constants.ZERO);
                }
                if (userHisAuth.contains(Constants.THREE)) {
                    vo.setPatrolStatus(Constants.ONE);
                } else {
                    vo.setPatrolStatus(Constants.ZERO);
                }
                if (userHisAuth.contains(Constants.FOUR)) {
                    vo.setGuardStatus(Constants.ONE);
                } else {
                    vo.setGuardStatus(Constants.ZERO);
                }
            }else{
                vo.setCameraStatus(Constants.ZERO);
                vo.setPresetStatus(Constants.ZERO);
                vo.setPatrolStatus(Constants.ZERO);
                vo.setGuardStatus(Constants.ZERO);
            }
        }catch (Exception e){
            vo.setCameraStatus(Constants.ZERO);
            vo.setPresetStatus(Constants.ZERO);
            vo.setPatrolStatus(Constants.ZERO);
            vo.setGuardStatus(Constants.ZERO);
        }
        return vo;
    }

}