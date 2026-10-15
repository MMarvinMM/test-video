package com.chinatower.platform.project.system.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.system.domain.vo.MiniProgramUpgradeInfoVO;
import com.chinatower.platform.project.system.domain.vo.WgtUpgradeInfoVO;

import java.util.List;


/**
 * 功能说明: WGT版本管理Service接口
 * @author: lzt
 * @date: 2024/01/31
 */
public interface IWgtUpgradeInfoService {

    /**
     * 查询属地版本
     * @param colonyCode
     * @return
     */
    String  queryColonyVersion(String colonyCode);

    /**
     * 查询WGT最新版本信息
     * @param colonyVersion
     * @return
     */
    WgtUpgradeInfoVO queryWgtUpgradeInfo(String colonyVersion, Integer type);

    /**
     * 分页查询wgt版本管理信息
     * @param wgtUpgradeInfoVO
     * @return
     */
    List<WgtUpgradeInfoVO> queryWgtList(WgtUpgradeInfoVO wgtUpgradeInfoVO);

    /**
     * 更新wgt版本信息
     * @param wgtUpgradeInfoVO
     * @return
     */
    int updateWgt(WgtUpgradeInfoVO wgtUpgradeInfoVO);

    /**
     * 下载地址
     * @param wgtUpgradeInfoVO
     * @return
     */
    WgtUpgradeInfoVO getDownloadUrl(WgtUpgradeInfoVO wgtUpgradeInfoVO);

    /**
     * 查询小程序最新版本信息
     * @param colonyVersion
     * @return
     */
    MiniProgramUpgradeInfoVO queryMiniProgramUpgradeInfo(String colonyVersion);

    /**
     * 分页查询小程序版本管理信息
     * @param miniProgramUpgradeInfoVO
     * @return
     */
    List<MiniProgramUpgradeInfoVO> queryMiniProgramList(MiniProgramUpgradeInfoVO miniProgramUpgradeInfoVO);


    /**
     * 更新小程序版本信息
     * @param miniProgramUpgradeInfoVO
     * @return
     */
    int updateMiniProgram(MiniProgramUpgradeInfoVO miniProgramUpgradeInfoVO);

    /**
     * 新增子节点
     * @param wgtUpgradeInfoVO
     * @return
     */
    Result<String> addChildWgt(WgtUpgradeInfoVO wgtUpgradeInfoVO);




}
