package com.chinatower.platform.project.pushsys.service;

import com.chinatower.common.entity.Result;

import java.util.Map;

/**
 * 推送变更服务实现类
 *
 * @author zl
 * @since 2022-02-14 12:03:37
 */
public interface IPushSysService {
     Result userPush(Map map);
     Result customer(Map map);
     Result updateManagementArea(Map map);
     Result organInfoPush(Map map);
}
