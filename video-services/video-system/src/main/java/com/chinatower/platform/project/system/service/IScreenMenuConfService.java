package com.chinatower.platform.project.system.service;


import com.chinatower.system.api.model.ScreenMenuConfVO;
import java.util.List;
import java.util.Set;

/**
 * 大屏菜单配置业务层
 *
 * @author chinatower
 */
public interface IScreenMenuConfService
{


    /**
     * 查询大屏菜单配置
     */
    List<ScreenMenuConfVO> getScreenMenuConf(ScreenMenuConfVO screenMenuConfVO);


    /**
     * 保存大屏菜单配置
     */
    int saveRoleScreenRef(List<ScreenMenuConfVO> screenMenuConfVOList);





}
