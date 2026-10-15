package com.chinatower.platform.config.security;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;


/**
* @Description:  网关白名单配置
* @Author: 张帅
* @Date: 2021/11/12 15:35
**/
@Data
@EqualsAndHashCode(callSuper = false)
@Component
@ConfigurationProperties(prefix="secure.ignore")
@RefreshScope
public class IgnoreUrlsConfig {

    /**
     * 一级过滤url  这些url无需token可以访问
     */
    private List<String> level1Urls;
    /**
     * 二级过滤url 这些url 需要token 但是无需配置权限可以访问
     */
    private List<String> level2Urls;

}
