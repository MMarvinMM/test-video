package com.chinatower.platform.project.system.service.impl;

import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.common.utils.DateUtils;
import com.chinatower.platform.project.system.domain.SysLoginConfig;
import com.chinatower.platform.project.system.mapper.SysLoginConfigMapper;
import com.chinatower.platform.project.system.service.ISysLoginConfigService;
import com.chinatower.platform.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 【登录页配置】Service业务层处理
 *
 * @author ruoyi
 * @date 2021-11-15
 */
@Service
public class SysLoginConfigServiceImpl implements ISysLoginConfigService
{
    @Autowired
    private SysLoginConfigMapper sysLoginConfigMapper;

    /**
     * 查询【登录页配置】
     *
     * @param id 【登录页配置】主键
     * @return 【登录页配置】
     */
    @Override
    public SysLoginConfig selectSysLoginConfigById(Long id)
    {
        return sysLoginConfigMapper.selectSysLoginConfigById(id);
    }

    /**
     * 查询【登录页配置】列表
     *
     * @param sysLoginConfig 【登录页配置】
     * @return 【登录页配置】
     */
    @Override
    public List<SysLoginConfig> selectSysLoginConfigList(SysLoginConfig sysLoginConfig)
    {
        return sysLoginConfigMapper.selectSysLoginConfigList(sysLoginConfig);
    }

    /**
     * 新增【登录页配置】
     *
     * @param sysLoginConfig 【登录页配置】
     * @return 结果
     */
    @Override
    public int insertSysLoginConfig(SysLoginConfig sysLoginConfig)
    {
        sysLoginConfig.setCreateTime(DateUtils.getNowDate());
        sysLoginConfig.setCreator(SecurityUtils.getUsername());
        return sysLoginConfigMapper.insertSysLoginConfig(sysLoginConfig);
    }

    /**
     * 修改【登录页配置】
     *
     * @param sysLoginConfig 【登录页配置】
     * @return 结果
     */
    @Override
    public int updateSysLoginConfig(SysLoginConfig sysLoginConfig)
    {
        sysLoginConfig.setUpdateTime(DateUtils.getNowDate());
        return sysLoginConfigMapper.updateSysLoginConfig(sysLoginConfig);
    }

    /**
     * 批量删除【登录页配置】
     *
     * @param ids 需要删除的【登录页配置】主键
     * @return 结果
     */
    @Override
    public int deleteSysLoginConfigByIds(Long[] ids)
    {
        return sysLoginConfigMapper.deleteSysLoginConfigByIds(ids);
    }

    @Override
    public String checkLoginTitleUnique(String title) {
        int count = sysLoginConfigMapper.checkLoginTitleUnique(title);
        if (count > 0) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

}
