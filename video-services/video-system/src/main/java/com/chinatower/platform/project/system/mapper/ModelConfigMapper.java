package com.chinatower.platform.project.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.project.system.domain.vo.TutorialModelConfigVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ModelConfigMapper extends BaseMapper<TutorialModelConfigVO> {

    /**
     * 查询所属模块配置列表
     * @param tutorialModelConfigVO
     * @return
     */
    List<TutorialModelConfigVO> queryModelConfigList(TutorialModelConfigVO tutorialModelConfigVO);

    /**
     * 检查所属模块配置信息
     * @param tutorialModelConfigVO
     * @return
     */
    TutorialModelConfigVO checkConfigInfo(TutorialModelConfigVO tutorialModelConfigVO);

    /**
     * 新增所属模块
     * @param tutorialModelConfigVO
     * @return
     */
    int addModelConfig(TutorialModelConfigVO tutorialModelConfigVO);

    /**
     * 修改所属模块
     * @param tutorialModelConfigVO
     * @return
     */
    int updateModelConfig(TutorialModelConfigVO tutorialModelConfigVO);

    /**
     * 删除所属模块
     * @param tutorialModelConfigVO
     * @return
     */
    int deleteModelConfig(TutorialModelConfigVO tutorialModelConfigVO);

}
