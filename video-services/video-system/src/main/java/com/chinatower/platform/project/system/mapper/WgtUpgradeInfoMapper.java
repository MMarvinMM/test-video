package com.chinatower.platform.project.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.project.system.domain.WgtUpgradeInfoDO;
import com.chinatower.platform.project.system.domain.vo.MiniProgramUpgradeInfoVO;
import com.chinatower.platform.project.system.domain.vo.WgtUpgradeInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能说明: Wgt版本管理Mapper接口
 * @author: lzt
 * @date: 2024/1/31
 */
public interface WgtUpgradeInfoMapper extends BaseMapper<WgtUpgradeInfoDO> {

    /**
     * 查询Wgt属地版本
     *
     * @param colonyCode
     * @return
     */
    String queryColonyVersion(String colonyCode);

    /**
     * 查询Wgt版本信息
     * @param colonyVersion
     * @return
     */
     WgtUpgradeInfoVO queryWgtUpgradeInfo(@Param("colonyVersion") String colonyVersion, @Param("type") Integer type);

    /**
     * 分页查询wgt版本管理信息
     * @param wgtUpgradeInfoVO
     * @return
     */
     List<WgtUpgradeInfoVO> queryWgtList(@Param("param") WgtUpgradeInfoVO wgtUpgradeInfoVO);

    /**
     * 更新wgt版本信息
     * @param wgtUpgradeInfoVO
     * @return
     */
    int updateWgt(@Param("param") WgtUpgradeInfoVO wgtUpgradeInfoVO);

    /**
     * 获取下载地址
     * @param wgtUpgradeInfoVO
     * @return
     */
    WgtUpgradeInfoVO getDownloadUrl(@Param("param") WgtUpgradeInfoVO wgtUpgradeInfoVO);


    /**
     * 查询小程序版本信息
     * @param colonyVersion
     * @return
     */
    MiniProgramUpgradeInfoVO queryMiniProgramUpgradeInfo(@Param("colonyVersion") String colonyVersion);


    /**
     * 分页查询小程序版本管理信息
     * @param miniProgramUpgradeInfoVO
     * @return
     */
    List<MiniProgramUpgradeInfoVO> queryMiniProgramList(@Param("param") MiniProgramUpgradeInfoVO miniProgramUpgradeInfoVO);


    /**
     * 更新小程序版本信息
     * @param miniProgramUpgradeInfoVO
     * @return
     */
    int updateMiniProgram(@Param("param") MiniProgramUpgradeInfoVO miniProgramUpgradeInfoVO);

    int addChildWgt(WgtUpgradeInfoVO wgtUpgradeInfoVO);


}
