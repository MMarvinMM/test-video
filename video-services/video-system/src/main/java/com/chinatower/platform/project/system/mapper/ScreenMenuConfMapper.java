package com.chinatower.platform.project.system.mapper;


import com.chinatower.system.api.model.ScreenMenuConfVO;
import java.util.List;

/**
 * 角色表 数据层
 *
 * @author chinatower
 */
public interface ScreenMenuConfMapper
{




    /**
     * 查询大屏菜单配置
     * @param screenMenuConfVO
     * @return
     */
    List<ScreenMenuConfVO> getScreenMenuConf(ScreenMenuConfVO screenMenuConfVO);

    /**
     * 查询最大排序
     * @param screenMenuConfVO
     * @return
     */
    int getMaxSort(ScreenMenuConfVO screenMenuConfVO);


    /**
     * 功能说明: 物理删除大屏菜单配置
     * @author liuxt
     * @param screenMenuConfVO
     * @return 删除结果
     * @throws
     * @throws
     * @requestID       *
     */
    int deleteScreenMenuConf(ScreenMenuConfVO screenMenuConfVO);


    /**
     * 功能说明: 保存角色大屏关系
     * @author liuxt
     * @param screenMenuConfVOList
     * @return 新增结果
     * @throws
     * @throws
     * @requestID       *
     */
    int insertScreenMenuConf(List<ScreenMenuConfVO> screenMenuConfVOList);


}
