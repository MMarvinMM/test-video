package com.chinatower.file.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.file.domain.SysConfig;
import org.springframework.stereotype.Component;

import java.util.List;


public interface SysConfigService {

    /**
     * 根据键名查询参数值
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    public String selectConfigValueByKey(String configKey);

    SysConfig selectConfigByKey(String configKey);

    /**
     * 获取中心节点
     * @return
     */
    String getCenterColonyCode();

    List<String> getAllDownPlatformCode();
}
