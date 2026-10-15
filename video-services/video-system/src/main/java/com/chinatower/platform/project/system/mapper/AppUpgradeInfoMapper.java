package com.chinatower.platform.project.system.mapper;


import com.chinatower.platform.project.system.domain.AppUpgradeInfo;

import java.util.List;

/**
 * 功能说明: APP版本升级Mapper接口
 * @param:
 * @return:
 * @author: 王梦迪
 * @date: 2022/4/13 17:49
 * @requestID  需求单号及需求名称
 */
public interface AppUpgradeInfoMapper {

    /**
     * 查询APP版本升级
     *
     * @param id APP版本升级ID
     * @return APP版本升级
     */
    AppUpgradeInfo selectAppUpgradeInfoById(Long id);

    /**
     * 查询APP版本升级列表
     *
     * @param appUpgradeInfo APP版本升级
     * @return APP版本升级集合
     */
    List<AppUpgradeInfo> selectAppUpgradeInfoList(AppUpgradeInfo appUpgradeInfo);

    /**
     * 新增APP版本升级
     *
     * @param appUpgradeInfo APP版本升级
     * @return 结果
     */
    int insertAppUpgradeInfo(AppUpgradeInfo appUpgradeInfo);

    /**
     * 修改APP版本升级
     *
     * @param appUpgradeInfo APP版本升级
     * @return 结果
     */
    int updateAppUpgradeInfo(AppUpgradeInfo appUpgradeInfo);

    /**
     * 删除APP版本升级
     *
     * @param id APP版本升级ID
     * @return 结果
     */
    int deleteAppUpgradeInfoById(Long id);

    /**
     * 批量删除APP版本升级
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteAppUpgradeInfoByIds(Long[] ids);

    AppUpgradeInfo selectByAppCode(String appCode);
}
