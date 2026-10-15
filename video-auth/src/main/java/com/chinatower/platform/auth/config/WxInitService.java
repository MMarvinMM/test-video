package com.chinatower.platform.auth.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * wechat mp wxInitService
 * @author zhujun
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(WxProperties.class)
public class WxInitService {
    // 配置参数
    private final WxProperties properties;

    @Bean
    public WxMpService wxMpService() {
        if (properties == null) {
            throw new RuntimeException("未配置相关参数！");
        }
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(properties.getAppId());
        config.setSecret(properties.getSecret());
        config.setToken(properties.getToken());
        config.setAesKey(properties.getAesKey());
        WxMpService mpService = new WxMpServiceImpl();
        mpService.setWxMpConfigStorage(config);
        return mpService;
    }

    @Bean
    public WxMaService wxMaService() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getMiniAppId());
        config.setSecret(properties.getMiniSecret());
        WxMaService maService = new WxMaServiceImpl();
        maService.setWxMaConfig(config);
        return maService;
    }
}
