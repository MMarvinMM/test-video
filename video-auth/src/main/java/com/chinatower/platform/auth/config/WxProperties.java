package com.chinatower.platform.auth.config;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "wx")
public class WxProperties {
    /**
     * 设置微信公众号的appid
     */
    private String appId;

    /**
     * 设置微信公众号的app secret
     */
    private String secret;

    /**
     * 设置微信公众号的token
     */
    private String token;

    /**
     * 设置微信公众号的EncodingAESKey
     */
    private String aesKey;

    /**
     * 微信公众号appid
     */
    private String miniAppId;

    /**
     * 公众号推送通知模板ID
     */
    private String templateId;

    /**
     * 公众号访问地址
     */
    private String miniAppEventUrl;

    /**
     * 登录访问地址
     */
    private String miniAppEventLogin;

    /**
     * 服务地址
     */
    private String serverHost;

    /**
     * 设置微信小程序的app secret
     */
    private String miniSecret;

    /**
     * 设置微信小程序的token
     */
    private String miniToken;

    /**
     * 设置微信小程序的EncodingAESKey
     */
    private String miniAesKey;

    /**
     * 公众号推送通知模板ID
     */
    private String pwdTemplateId;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
