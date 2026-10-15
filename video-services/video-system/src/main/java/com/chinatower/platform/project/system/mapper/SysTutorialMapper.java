package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.vo.SysVideoTutorialVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName SysTutorialMapper
 * @Description 系统引导视频教程 数据层
 * @Author D
 * @Date 2022/2/23 17:02
 */
public interface SysTutorialMapper {

    /**
     * @Description 新增系统引导视频教程
     * @author ll
     * @Date 10:38 2022/08/19
     * @Param [SysVideoTutorialVO]
     **/
   int insert(SysVideoTutorialVO vo);


    /**
     * 更新视频系统教程
     * @param sysVideoTutorialVO
     */
    int updateVideoTutorial(SysVideoTutorialVO sysVideoTutorialVO);


    /**
     * 校验视频教程名称唯一性
     * @param sysVideoTutorialVO
     */
    SysVideoTutorialVO checkSysVideoTutorialInfo(SysVideoTutorialVO sysVideoTutorialVO);

    /**
     * @Description 查询系统引导视频教程
     * @author ll
     * @Date 11:07 2022/08/19
     * @Param [SysVideoTutorialVO]
     **/
   List<SysVideoTutorialVO> getTutorialVideoList(SysVideoTutorialVO vo);


    /**
     * 视频教程基本信息详情
     * @param mallBaseVO
     */
    SysVideoTutorialVO getVideoTutorialInfo(SysVideoTutorialVO mallBaseVO);


    /**
     * @Description 删除系统引导视频教程
     * @author liuxt
     * @Date 10:38 2022/08/19
     * @Param [SysVideoTutorialVO]
     **/
    int delete(SysVideoTutorialVO vo);



}
