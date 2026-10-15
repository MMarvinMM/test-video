package com.chinatower.platform.project.system.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.system.domain.vo.TutorialModelConfigVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 功能描述: 教程所属模块配置
 * @auther: liuxt
 * @date: 2024/12/04
 * @version: release_v1.7.10
 */
public interface ModelConfigService {

    /**
     * 合作模式查询分页
     *
     * @param tutorialModelConfigVO
     * @return
     */
    Result<PageInfo<TutorialModelConfigVO>> queryModelConfigPage(TutorialModelConfigVO tutorialModelConfigVO);


    /**
     * 模块配置下拉框列表
     *
     * @param tutorialModelConfigVO
     * @return
     */
    List<TutorialModelConfigVO> queryModelConfig(TutorialModelConfigVO tutorialModelConfigVO);

    /**
     * 新增所属模块
     *
     * @param tutorialModelConfigVO
     * @return
     */
    Result<String> addModelConfig(TutorialModelConfigVO tutorialModelConfigVO);

    /**
     * 修改所属模块
     *
     * @param tutorialModelConfigVO
     * @return
     */
    Result<String> updateModelConfig(TutorialModelConfigVO tutorialModelConfigVO);

    /**
     * 删除所属模块
     *
     * @param tutorialModelConfigVO
     * @retur
     */
    Result<String> deleteModelConfig(TutorialModelConfigVO tutorialModelConfigVO);


}
