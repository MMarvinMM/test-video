package com.chinatower.platform.project.system.service;

import com.chinatower.platform.project.system.domain.SysMessagePermission;

/**
 * @Author: wjd
 * @Description: 用户消息权限服务接口
 * @Date: Created in 20:21 2022/2/28
 * @Modified by:
 */
public interface ISysMessagePermission {
    /**
     * 详情
     * @param userId
     * @return
     */
    public int selectPermissionById(String userId);
}
