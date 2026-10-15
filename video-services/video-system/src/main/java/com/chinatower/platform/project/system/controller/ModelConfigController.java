package com.chinatower.platform.project.system.controller;


import com.chinatower.common.entity.Result;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.platform.project.system.domain.vo.TutorialModelConfigVO;
import com.chinatower.platform.project.system.service.ModelConfigService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * 功能描述: 教程所属模块配置
 * @auther: liuxt
 * @date: 2024/12/04
 * @version: release_v1.7.10
 */
@Slf4j
@RestController
@RequestMapping("/modelConfig")
public class ModelConfigController extends TowerBaseController {

    @Resource
    private ModelConfigService modelConfigService;

    /**
     * 所属模块配置分页列表
     * @param tutorialModelConfigVO
     * @return
     */
    @PostMapping(value = "/queryModelConfigPage")
    public Result<PageInfo<TutorialModelConfigVO>> queryModelConfigPage(@RequestBody TutorialModelConfigVO tutorialModelConfigVO)
    {
        return modelConfigService.queryModelConfigPage(tutorialModelConfigVO);
    }

    /**
     * 模块配置下拉框列表
     * @param tutorialModelConfigVO
     * @return
     */
    @PostMapping(value = "/queryModelConfig")
    public Result<List<TutorialModelConfigVO>> queryModelConfig(@RequestBody TutorialModelConfigVO tutorialModelConfigVO)
    {
        return Result.data(modelConfigService.queryModelConfig(tutorialModelConfigVO));
    }

    /**
     * 新增所属模块
     * @param tutorialModelConfigVO
     * @return
     */
    @PostMapping(value = "/addModelConfig")
    public Result<String> addModelConfig(@Validated @RequestBody TutorialModelConfigVO tutorialModelConfigVO) {
        return modelConfigService.addModelConfig(tutorialModelConfigVO);
    }

    /**
     * 修改所属模块
     * @param tutorialModelConfigVO
     * @return
     */
    @PostMapping(value = "/updateModelConfig")
    public Result<String> updateModelConfig(@Validated @RequestBody TutorialModelConfigVO tutorialModelConfigVO) {
        return modelConfigService.updateModelConfig(tutorialModelConfigVO);
    }

    /**
     * 删除所属模块
     * @param tutorialModelConfigVO
     * @return
     */
    @PostMapping(value = "/deleteModelConfig")
    public Result<String> deleteCoopModeConfig(@RequestBody TutorialModelConfigVO tutorialModelConfigVO) {
        return modelConfigService.deleteModelConfig(tutorialModelConfigVO);
    }

}
