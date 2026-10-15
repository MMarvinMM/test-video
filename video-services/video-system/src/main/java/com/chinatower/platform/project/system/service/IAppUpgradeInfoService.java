package com.chinatower.platform.project.system.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.AppUpgradeInfo;

import java.util.List;

/**
 * 功能说明: APP版本升级Service接口
 * @param:
 * @return:
 * @author: 王梦迪
 * @date: 2022/4/13 17:48
 * @requestID  需求单号及需求名称
 */
public interface IAppUpgradeInfoService {

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
     * 查询APP版本升级列表(ios+安卓)
     *
     * @return APP版本升级集合
     */
    List<AppUpgradeInfo> queryNewVersion();

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
     * 批量删除APP版本升级
     *
     * @param ids 需要删除的APP版本升级ID
     * @return 结果
     */
    int deleteAppUpgradeInfoByIds(Long[] ids);

    /**
     * 删除APP版本升级信息
     *
     * @param id APP版本升级ID
     * @return 结果
     */
    int deleteAppUpgradeInfoById(Long id);

    AjaxResult queryUpgrade(AppUpgradeInfo appUpgradeInfo);
}
