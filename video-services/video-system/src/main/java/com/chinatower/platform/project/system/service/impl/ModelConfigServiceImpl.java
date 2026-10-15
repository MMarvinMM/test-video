package com.chinatower.platform.project.system.service.impl;


import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.custom.common.Constants;
import com.chinatower.platform.project.system.domain.vo.SysVideoTutorialVO;
import com.chinatower.platform.project.system.domain.vo.TutorialModelConfigVO;
import com.chinatower.platform.project.system.mapper.ModelConfigMapper;
import com.chinatower.platform.project.system.mapper.SysTutorialMapper;
import com.chinatower.platform.project.system.service.ModelConfigService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 功能描述: 教程所属模块配置
 * @auther: liuxt
 * @date: 2024/12/04
 * @version: release_v1.7.10
 */
@Service
@Slf4j
public class ModelConfigServiceImpl implements ModelConfigService {

    @Resource
    private ModelConfigMapper modelConfigMapper;

    @Resource
    private SysTutorialMapper sysTutorialMapper;
 

    /**
     * 分页查询合作模式
     * @param tutorialModelConfigVO
     * @return
     */
    @Override
    public Result<PageInfo<TutorialModelConfigVO>> queryModelConfigPage(TutorialModelConfigVO tutorialModelConfigVO) {
        if (null == tutorialModelConfigVO.getPageSize() || null == tutorialModelConfigVO.getPageNum()) {
            tutorialModelConfigVO.setPageNum(1);
            tutorialModelConfigVO.setPageSize(10);
        }
        //分页查询合作模式
        PageHelper.startPage(tutorialModelConfigVO.getPageNum(), tutorialModelConfigVO.getPageSize());
        List<TutorialModelConfigVO> list = modelConfigMapper.queryModelConfigList(tutorialModelConfigVO);
        Result<PageInfo<TutorialModelConfigVO>> result;
        PageInfo<TutorialModelConfigVO> pageInfo = new PageInfo<>(list);
        result = Result.success("查询成功");
        result.setData(pageInfo);
        return result;
    }

    /**
     * 模块配置下拉框列表
     * @param tutorialModelConfigVO
     * @return
     */
    @Override
    public List<TutorialModelConfigVO> queryModelConfig(TutorialModelConfigVO tutorialModelConfigVO) {
        List<TutorialModelConfigVO> list = modelConfigMapper.queryModelConfigList(tutorialModelConfigVO);
        return list;
    }

    /**
     * 新增所属模块
     * @param tutorialModelConfigVO
     * @return
     */
    @Override
    public Result<String> addModelConfig(TutorialModelConfigVO tutorialModelConfigVO) {
        //校验所属模块唯一性，如果重复则提示“所属模块已存在”
        TutorialModelConfigVO configVO = modelConfigMapper.checkConfigInfo(tutorialModelConfigVO);
        if(ObjectUtils.isNotEmpty(configVO)){
            return Result.fail(Constants.MODEL_CONFIG_NAME_EXIST);
        }
        tutorialModelConfigVO.setCreator(SecurityUtils.getOperator());
        modelConfigMapper.addModelConfig(tutorialModelConfigVO);
        Long modelId = tutorialModelConfigVO.getModelId();
        return Result.data(modelId.toString(),Constants.SUCCESS_MESSAGE);
    }

    /**
     * 修改所属模块
     * @param tutorialModelConfigVO
     * @return
     */
    @Override
    public Result<String> updateModelConfig(TutorialModelConfigVO tutorialModelConfigVO) {
        //校验所属模块唯一性，如果重复则提示“所属模块已存在”
        TutorialModelConfigVO configVO = modelConfigMapper.checkConfigInfo(tutorialModelConfigVO);
        if(ObjectUtils.isNotEmpty(configVO)){
            return Result.fail(Constants.MODEL_CONFIG_NAME_EXIST);
        }
        // 更新所属模块
        tutorialModelConfigVO.setModifier(SecurityUtils.getOperator());
        modelConfigMapper.updateModelConfig(tutorialModelConfigVO);
        return Result.success(Constants.SUCCESS_MESSAGE);
    }

    /**
     * 删除所属模块
     * @param tutorialModelConfigVO
     * @return
     */
    @Override
    public Result<String> deleteModelConfig(TutorialModelConfigVO tutorialModelConfigVO) {
        //查询所属模块是否已有视频教程
        SysVideoTutorialVO sysVideoTutorialVO = new SysVideoTutorialVO();
        sysVideoTutorialVO.setModelId(tutorialModelConfigVO.getModelId());
        List<SysVideoTutorialVO> list = sysTutorialMapper.getTutorialVideoList(sysVideoTutorialVO);
        //如果存在视频教程，不允许删除合作模式
        if(ObjectUtils.isNotEmpty(list)){
            return Result.fail(Constants.DEL_MODEL_CONFIG_EXIST);
        }
        // 删除合作模式
        modelConfigMapper.deleteModelConfig(tutorialModelConfigVO);
        return Result.success(Constants.SUCCESS_MESSAGE);
    }

}




