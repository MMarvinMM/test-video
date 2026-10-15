package com.chinatower.platform.project.system.service;

import com.chinatower.platform.project.system.domain.SysTutorialUser;
import com.chinatower.platform.project.system.domain.vo.TutorialIndustryVO;

import java.util.List;

/**
 * @description 用户教程
 * @author luo_ji
 * @date 2022-12-15
 */
public interface ISysTutorialUserService {

    /**
     * 新增
     */
    public Integer insert(SysTutorialUser sysTutorialUser);

    /**
     * 删除
     */
    public int delete(Long tutorialId);

    /**
     * 更新
     */
    public int update(SysTutorialUser sysTutorialUser);

    /**
     * 根据主键 id 查询
     */
    public SysTutorialUser load(Long tutorialId);

    /**
     * 分页查询
     */
    public List<SysTutorialUser> pageList(SysTutorialUser sysTutorialUser);

    /**
     * 分页查询
     */
    public String checkRepeat(SysTutorialUser sysTutorialUser);

    /**
     * 查询所属行业接口
     *
     * @param tutorialIndustryVO
     * @return
     */
    List<TutorialIndustryVO> getIndustryList(TutorialIndustryVO tutorialIndustryVO);


}