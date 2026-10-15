package com.chinatower.platform.project.config;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wechat mp properties
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
@ConfigurationProperties(prefix = "wx")
public class WxMpProperties {
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
     * 微信小程序appid
     */
    private String miniAppId;

    /**
     * 公众号推送通知模板ID
     */
    private String templateId;

    /**
     * 小程序访问地址
     */
    private String miniAppEventUrl;

    /**
     * 服务地址
     */
    private String serverHost;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
