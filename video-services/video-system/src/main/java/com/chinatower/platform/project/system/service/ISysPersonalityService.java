package com.chinatower.platform.project.system.service;

import com.chinatower.platform.project.system.domain.vo.SysInfoSetVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 个性化 服务层
 *
 * @author chinatower
 */
public interface ISysPersonalityService {
    /**
     * @Description 获取个性化配置
     * @author wjd
     * @Date 15:40 2021/7/7
     * @Param [sysInfoSetVO]
     **/
    List<SysInfoSetVO> getInfoSetList(SysInfoSetVO sysInfoSetVO);

    List<SysInfoSetVO> getThemeInfoByLoginUser(SysInfoSetVO vo);

    int saveSets(List<SysInfoSetVO> voList);

    int saveSetsByType(List<SysInfoSetVO> voList, String type);

    void reset(String tenantId,String type);

}
