package com.chinatower.file.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.chinatower.file.domain.SysConfig;
import com.chinatower.file.mapper.SysFileInfoMapper;
import com.chinatower.file.service.SysConfigService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

@Service
public class SysConfigServiceImpl implements SysConfigService {
    @Autowired
    private SysFileInfoMapper sysFileInfoMapper;

    @Override
    public String selectConfigValueByKey(String configKey) {
        SysConfig config = selectConfigByKey(configKey);
        return ObjectUtil.isNull(config)?"":config.getConfigValue();
    }
    public SysConfig selectConfigByKey(String configKey) {
        SysConfig config = RedisUtil.get(SYS_CONFIG_KEY+configKey, SysConfig.class, RedisDBType.PRIMARY);
        if(ObjectUtil.isNull(config)){
            config = new SysConfig();
            config.setConfigKey(configKey);
            config = sysFileInfoMapper.selectConfig(config);
            if(ObjectUtil.isNotNull(config)){
                RedisUtil.set(SYS_CONFIG_KEY+configKey,config, RedisDBType.PRIMARY);
            }
        }
        return config;
    }

    @Override
    public String getCenterColonyCode() {
        return sysFileInfoMapper.getCenterColonyCode();
    }

    @Override
    public List<String> getAllDownPlatformCode() {
        return sysFileInfoMapper.getAllDownPlatformCode();
    }
}
