package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.SysMessagePermission;

/**
 * @Author: wjd
 * @Description: 用户消息权限表 数据层
 * @Date: Created in 20:26 2022/2/28
 * @Modified by:
 */
public interface SysMessagePermissionMapper {


    /**
     * 查询用户消息权限信息
     *
     */
    public SysMessagePermission selectPermission(String userId);
}
