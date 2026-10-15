package com.chinatower.platform.project.system.service;


import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.system.domain.vo.SysVideoTutorialVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 系统引导 服务层
 *
 * @author chinatower
 */
public interface ISysTutorialService {
    Result<String> save(SysVideoTutorialVO vo);

    /**
     * 编辑视频系统教程
     * @param vo
     * @return 新增结果
     */
    Result<String> updateVideoTutorial(SysVideoTutorialVO vo);

    Result<PageInfo<SysVideoTutorialVO>> getVideoTutorial(SysVideoTutorialVO sysVideoTutorialVO);


    /**
     * 根据教程ID获取视频系统教程详情
     * @param sysVideoTutorialVO
     * @return 新增结果
     */
    SysVideoTutorialVO getVideoTutorialInfo(SysVideoTutorialVO sysVideoTutorialVO);

    int delete(SysVideoTutorialVO vo);

}
