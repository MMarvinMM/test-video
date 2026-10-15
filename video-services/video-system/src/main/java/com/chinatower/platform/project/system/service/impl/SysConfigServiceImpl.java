package com.chinatower.platform.project.system.service.impl;

import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.common.utils.Aes;
import com.chinatower.platform.common.utils.OauthConstants;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.framework.config.CacheRunner;
import com.chinatower.platform.framework.config.ObsConfig;
import com.chinatower.platform.project.system.constants.Constants;
import com.chinatower.platform.project.tool.redis.domain.RedisReceiver;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.chinatower.platform.common.constant.UserConstants;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.mapper.SysConfigMapper;
import com.chinatower.platform.project.system.service.ISysConfigService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * 参数配置 服务层实现
 *
 * @author chinatower
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService
{
    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private ObsConfig obsConfig;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    CacheRunner cacheRunner;

    private static final String SALT = "china.tower";

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfig selectConfigById(Long configId)
    {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        return configMapper.selectConfig(config);
    }


    @Override
    public SysConfig selectConfigByKey(String configKey) {
//        SysConfig config = RedisUtil.get(SYS_CONFIG_KEY+configKey, SysConfig.class, RedisDBType.PRIMARY);
        SysConfig config = CacheRunner.SYS_CONFIG_MAP_CONFIG_KEY.get(configKey);
        if (config == null){
            config = RedisUtil.get(SYS_CONFIG_KEY+configKey, SysConfig.class, RedisDBType.PRIMARY);
        }
        if(ObjectUtil.isNull(config)){
            config = new SysConfig();
            config.setConfigKey(configKey);
            config = configMapper.selectConfig(config);
            if(ObjectUtil.isNotNull(config)){
                RedisUtil.set(SYS_CONFIG_KEY+configKey,config, RedisDBType.PRIMARY);
            }
        }
        //为不影响其他调用，24/6/14登录只返回configValue
        if(configKey.equals(OauthConstants.LOGIN_TYPE)){
            SysConfig sysConfig = new SysConfig();
            sysConfig.setConfigValue(config.getConfigValue());
            return sysConfig;
        }else {
            return config;
        }

    }


    @Override
    public Map<String,SysConfig> selectConfigByConfigKeys(String[] configKeys) {
        List<String> noCacheList = new ArrayList<>();
        Map<String, SysConfig> cacheMap = new HashMap<>();
        for(String configKey:configKeys){
            SysConfig config = CacheRunner.SYS_CONFIG_MAP_CONFIG_KEY.get(configKey);
            if (ObjectUtils.isEmpty(config)){
                if(RedisUtil.hasKey(SYS_CONFIG_KEY+configKey,RedisDBType.PRIMARY)){
                    config = RedisUtil.get(SYS_CONFIG_KEY+configKey, SysConfig.class, RedisDBType.PRIMARY);
                }
            }
            if (!ObjectUtils.isEmpty(config)){
                cacheMap.put(configKey,config);
                CacheRunner.SYS_CONFIG_MAP_CONFIG_KEY.put(configKey,config);
            }else {
                noCacheList.add(configKey);
            }
        }
        if(!CollectionUtils.isEmpty(noCacheList)){
            List<SysConfig> list = configMapper.selectConfigListByConfigKeys(noCacheList);
            for(SysConfig sysConfig:list){
                RedisUtil.set(SYS_CONFIG_KEY+sysConfig.getConfigKey(), sysConfig, RedisDBType.PRIMARY);
                cacheMap.put(sysConfig.getConfigKey(),sysConfig);
                CacheRunner.SYS_CONFIG_MAP_CONFIG_KEY.put(sysConfig.getConfigKey(),sysConfig);
            }
        }
        return cacheMap;
    }



    @Override
    public String getMsgConfig(String appVerCode) {
        String flag;
        flag = configMapper.getMsgConfig(appVerCode);

        return flag;
    }


    @Override
    public String selectConfigValueByKey(String configKey) {
        SysConfig config = selectConfigByKey(configKey);
        return ObjectUtil.isNull(config)?"":config.getConfigValue();
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config)
    {
        return configMapper.selectConfigList(config);
    }

    /**
     * 导出参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> exportConfigList(SysConfig config)
    {
        return configMapper.exportConfigList(config);
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config) {
        config.setCreateTime(new Date());
        int res = configMapper.insertConfig(config);
        if(res>0){
            RedisUtil.set(SYS_CONFIG_KEY+config.getConfigKey(),config, RedisDBType.PRIMARY);

            RedisReceiver.Msg msg = new RedisReceiver.Msg();
            msg.setTable(RedisReceiver.Msg.Table.SysConfig);
            redisTemplate.convertAndSend(Constants.PUB_CHANNEL, msg);

        }
        return res;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config) {
        config.setUpdateTime(new Date());
        int res = configMapper.updateConfig(config);
        if(res>0){
            RedisUtil.set(SYS_CONFIG_KEY+config.getConfigKey(),config, RedisDBType.PRIMARY);

            RedisReceiver.Msg msg = new RedisReceiver.Msg();
            msg.setTable(RedisReceiver.Msg.Table.SysConfig);
            redisTemplate.convertAndSend(Constants.PUB_CHANNEL, msg);

        }
        return res;
    }

    /**
     * 删除参数配置信息
     *
     * @param configId 参数ID
     * @return 结果
     */
    @Override
    public int deleteConfigById(Long configId) {
        SysConfig config = selectConfigById(configId);
        int res = configMapper.deleteConfigById(configId);
        if(res>0){
            RedisUtil.delete(SYS_CONFIG_KEY+config.getConfigKey(),RedisDBType.PRIMARY);

            RedisReceiver.Msg msg = new RedisReceiver.Msg();
            msg.setTable(RedisReceiver.Msg.Table.SysConfig);
            redisTemplate.convertAndSend(Constants.PUB_CHANNEL, msg);

        }
        return res;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     * @return 结果
     */
    @Override
    public int deleteConfigByIds(Long[] configIds) {
        int count = 0;
        for(Long configId:configIds){
            count += deleteConfigById(configId);
        }
        return count;
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public String checkConfigKeyUnique(SysConfig config) {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 获取obs密钥
     *
     * @return
     */
    @Override
    public Result<Map<String, String>> selectObsByKeys() {
        Map<String, String> map = new HashMap<>();
        String timesTamp = String.valueOf(System.currentTimeMillis());
        String ak = obsConfig.getAk();
        String sk = obsConfig.getSk();
        String aesKey = SALT + timesTamp.substring(8, timesTamp.length());
        try {
            map.put("ak", Aes.aesEncrypt(ak, aesKey));
            map.put("sk", Aes.aesEncrypt(sk, aesKey));
            map.put("endPoint", obsConfig.getEndPoint());
            map.put("timesTamp", timesTamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.data(map);

    }

    @Override
    public void batchRefreshConfigCache(Long[] configIds) {
        List<SysConfig> sysConfigs = configMapper.selectConfigListByConfigIds(configIds);

        if(CollUtil.isNotEmpty(sysConfigs)){
            Map<String, Object> mulitMap = new HashMap<>();
            sysConfigs.forEach(item -> mulitMap.put(SYS_CONFIG_KEY+item.getConfigKey(), item));
            RedisUtil.multiSet(mulitMap, RedisDBType.PRIMARY);
//            sysConfigs.forEach(config-> RedisUtil.set(SYS_CONFIG_KEY+config.getConfigKey(), config, RedisDBType.PRIMARY));
        }
    }
}
