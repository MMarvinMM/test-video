package com.chinatower.platform.service;

import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysDept;

/**
 * @author penghaibo
 */
public interface SysAreaRootService {

    /**
     * 获取根区域信息
     *
     * @return 返回根区域信息
     */
    SysArea getRootAreaInfo();

    /**
     * 查询根区域信息
     *
     * @return 返回SysArea对象，表示查询到的根区域信息
     */
    SysArea queryRootAreaInfo();

    /**
     * 创建根节点树状结构视图对象
     *
     * @param target 目标对象
     */
    void createRootTreeVO(Object target);

    /**
     * 获取根组织信息
     *
     * @return 返回根区域信息
     */
    SysDept getRootDeptInfo();

    /**
     * 查询根组织信息
     *
     * @return 返回SysArea对象，表示查询到的根区域信息
     */
    SysDept queryRootDeptInfo();

    /**
     * 创建根组织节点树状结构视图对象
     *
     * @param target 目标对象
     */
    void createRootDeptTreeVO(Object target);
}
