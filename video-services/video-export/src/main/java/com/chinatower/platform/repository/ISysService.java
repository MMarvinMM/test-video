package com.chinatower.platform.repository;

import com.chinatower.platform.entity.admin.*;
import java.util.List;

/**
 * 基础信息业务接口
 * @author chmod
 * @version 1.0
 * @date 2022/3/04 15:20
 */
public interface ISysService {

    /**
     * 根据组织查询用户
     * @param deptId
     * @return
     */
    List<SysUser> selectUserByDeptId(String deptId);

    /**
     * 获取当前登录用户id(切换后取对应账号的id)
     * @return
     */
    String getLoginUserId();
}
