package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.project.system.domain.SysLoginConfig;

import java.util.List;

/**
 * 【登录页配置】Mapper接口
 *
 * @author ruoyi
 * @date 2021-11-15
 */
public interface SysLoginConfigMapper
{
    /**
     * 查询【登录页配置】
     *
     * @param id 【登录页配置】主键
     * @return 【登录页配置】
     */
    public SysLoginConfig selectSysLoginConfigById(Long id);

    /**
     * 查询【登录页配置】列表
     *
     * @param sysLoginConfig 【登录页配置】
     * @return 【登录页配置】集合
     */
    public List<SysLoginConfig> selectSysLoginConfigList(SysLoginConfig sysLoginConfig);

    /**
     * 新增【登录页配置】
     *
     * @param sysLoginConfig 【登录页配置】
     * @return 结果
     */
    public int insertSysLoginConfig(SysLoginConfig sysLoginConfig);

    /**
     * 修改【登录页配置】
     *
     * @param sysLoginConfig 【登录页配置】
     * @return 结果
     */
    public int updateSysLoginConfig(SysLoginConfig sysLoginConfig);


    /**
     * 批量删除【登录页配置】
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysLoginConfigByIds(Long[] ids);

    int checkLoginTitleUnique(String title);
}
