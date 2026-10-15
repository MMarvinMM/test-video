package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.vo.PersonalLoginConfVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 个性化登录页面配置 数据层
 *
 * @author chinatower
 */
public interface PersonalLoginConfMapper {

    /**
     * 查询个性化登录页面配置
     *
     * @param personalLoginConfVO 查询条件
     * @return 配置列表
     */
    List<PersonalLoginConfVO> selectPersonalLoginConf(PersonalLoginConfVO personalLoginConfVO);

    /**
     * 逻辑删除个性化登录页面配置
     *
     * @param tenantId 客户id
     * @return 删除结果
     */
    int logicDeleteByTenantId(@Param("tenantId") String tenantId, @Param("configType") Integer configType);

    /**
     * 新增个性化登录页面配置
     *
     * @param personalLoginConfVO 配置信息
     * @return 新增结果
     */
    int insertPersonalLoginConf(PersonalLoginConfVO personalLoginConfVO);

    /**
     * 根据客户id查询配置（用于登录页）
     *
     * @param tenantId 客户id
     * @return 配置信息
     */
    PersonalLoginConfVO selectByTenantId(@Param("tenantId") String tenantId, @Param("configType") Integer configType);
}