package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.entity.admin.SysEnforcementInfo;

import java.util.List;

/**
 * 用户执法信息表 数据层
 *
 * @author zhang.xionghui
 */
public interface SysEnforcementInfoMapper {
    /**
     * 根据条件分页查询用户执法信息列表
     *
     * @param enforcementInfo 用户执法信息
     * @return 用户执法信息集合信息
     */
    List<SysEnforcementInfo> selectEnforcementInfoList(SysEnforcementInfo enforcementInfo);


    /**
     * 新增用户执法信息
     *
     * @param enforcementInfo 用户执法信息
     * @return 结果
     */
    int insertEnforcementInfo(SysEnforcementInfo enforcementInfo);

    /**
     * 修改用户执法信息
     *
     * @param enforcementInfo 用户执法信息
     * @return 结果
     */
    int updateEnforcementInfo(SysEnforcementInfo enforcementInfo);


    /**
     * 通过用户ID获取详细信息
     *
     * @param enforcementInfo 用户ID
     * @return 用户执法信息
     */
    SysEnforcementInfo selectEnforcementInfoById(SysEnforcementInfo enforcementInfo);

    /**
     * 通过用户ID查询用户执法信息
     *
     * @param userId 用户ID
     * @return 用户执法信息
     */
    SysEnforcementInfo selectById(String userId);
}
