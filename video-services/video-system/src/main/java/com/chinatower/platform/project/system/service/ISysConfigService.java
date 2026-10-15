package com.chinatower.platform.project.system.service;

import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.system.domain.SysConfig;

import java.util.List;
import java.util.Map;

/**
 * 参数配置 服务层
 *
 * @author iwhalecloud
 */
public interface ISysConfigService
{
    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    public SysConfig selectConfigById(Long configId);

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    public SysConfig selectConfigByKey(String configKey);

    /**
     * 根据键名批量查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    public Map<String,SysConfig> selectConfigByConfigKeys(String[] configKey);

    /**
     * 根据行业版本号获取开关值
     *
     * @param appVerCode 参数键名
     * @return 参数键值
     */
    public String getMsgConfig(String appVerCode);



    /**
     * 根据键名查询参数值
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    public String selectConfigValueByKey(String configKey);

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 导出参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SysConfig> exportConfigList(SysConfig config);

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    public int insertConfig(SysConfig config);

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    public int updateConfig(SysConfig config);

    /**
     * 删除参数配置信息
     *
     * @param configId 参数ID
     * @return 结果
     */
    public int deleteConfigById(Long configId);

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    public int deleteConfigByIds(Long[] configIds);

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数信息
     * @return 结果
     */
    public String checkConfigKeyUnique(SysConfig config);

    /**
     * 获取obs密钥
     * @return
     */
    Result<Map<String, String>> selectObsByKeys();

    /**
     * @description 刷新参数配置缓存
     * @author WANGZIC
     * @date 2022.03.01 11:25
     * @param configIds 参数配置id
     */
    void batchRefreshConfigCache(Long[] configIds);
}
