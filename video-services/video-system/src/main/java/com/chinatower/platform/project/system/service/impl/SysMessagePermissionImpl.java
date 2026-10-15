package com.chinatower.platform.project.system.service.impl;

import com.chinatower.platform.project.system.domain.SysMessagePermission;
import com.chinatower.platform.project.system.mapper.SysMessagePermissionMapper;
import com.chinatower.platform.project.system.service.ISysMessagePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName SysMessagePermissionImpl
 * @Description 用户消息权限服务实现类
 * @Author D
 * @Date 2022/2/28 20:23
 */
@Service
public class SysMessagePermissionImpl  implements ISysMessagePermission {

    @Autowired
    private SysMessagePermissionMapper sysMessagePermissionMapper;

    /**
     * 详情
     * @param userId
     * @return
     */
    @Override
    public int selectPermissionById(String userId) {
        int result = 0; //0关,1开
        //总开关权限
        SysMessagePermission permission = sysMessagePermissionMapper.selectPermission(userId);
        if (null != permission) {
            String mainType = permission.getPermissionType();
            if ("1".equals(mainType)) {
                result = 1;
            }
        }
        //用户配置消息权限
        return result;
    }
}
