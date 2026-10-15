package com.chinatower.platform.project.system.service;

import com.chinatower.platform.entity.admin.SysEnforcementInfo;

import java.util.List;

/**
 * 用户执法信息 业务层
 *
 * @author zhang.xionghui
 */
public interface ISysEnforcementInfoService
{
    /**
     * 根据条件分页查询用户执法信息列表
     *
     * @param enforcementInfo 用户执法信息
     * @return 用户执法信息集合信息
     */
    List<SysEnforcementInfo> selectEnforcementInfoList(SysEnforcementInfo enforcementInfo);



    /**
     * 新增或修改用户执法信息
     *
     * @param enforcementInfo 用户执法信息
     * @return 结果
     */
    int insertOrUpdateEnforcementUser(SysEnforcementInfo enforcementInfo);


    /**
     * 通过用户ID查询用户执法信息
     *
     * @param sysEnforcementInfo 用户执法信息
     * @return 用户执法信息
     */
    SysEnforcementInfo selectEnforcementInfoById(SysEnforcementInfo sysEnforcementInfo);

}
