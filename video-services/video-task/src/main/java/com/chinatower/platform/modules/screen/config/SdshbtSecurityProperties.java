package com.chinatower.platform.modules.screen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * sdshbt 第三方大屏安全接口配置（Nacos 注入，username/password 使用 Jasypt ENC 密文）
 */
@Data
@ConfigurationProperties(prefix = "sdshbt.security")
public class SdshbtSecurityProperties {

    /**
     * 安全接口根地址，如 https://sdshbt.wcare.cn:19180/hb2/rest/security
     */
    private String baseUrl;

    /**
     * AES 加密密钥（与前端 keyone 一致，16 字节明文；不建议 ENC 加密）
     */
    private String aesKey;

    /**
     * 登录用户名（ENC 密文）
     */
    private String username;

    /**
     * 登录密码（ENC 密文）
     */
    private String password;

    /**
     * HTTP 超时毫秒
     */
    private int connectTimeout = 10000;
}
