package com.chinatower.platform.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 小塔单点登录配置
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "sso.xiaota")
public class SsoProperties {

    /**
     * 能开平台申请接口地址
     */
    private String fourAUrl = "http://iservice.chinatowercom.cn:18085/api/rest";

    /**
     * 能开平台系统的应用标识
     */
    private String appId = "96016";

    /**
     * 能开平台密钥
     */
    private String nkSecretKey = "0ab12b4ef7996559";

    /**
     * 能开平台发送方编码
     */
    private String nkSendCode = "60.6129";

    /**
     * 能开平台接口方法名
     */
    private String nkMethod = "createProwadaToken";

    /**
     * 能开平台接口版本
     */
    private String nkVersion = "1.0";

    /**
     * 能开平台服务编码
     */
    private String nkServerCode = "40.4015";

    /**
     * 4A请求体密钥（固定值）
     */
    private String fourA3DesKey = "6#dPz>3F";

    /**
     * 4A DES密钥
     */
    private String fourASecret = "6&#*^!#*";

    /**
     * 4A被访问系统应用标识
     */
    private String sysCode = "CHNTJSZC_ZGTTAIZNTYYYF";

    /**
     * 4A申请系统编码
     */
    private String applySysCode = "CHNTSLW_SLYYPT_006";

    /**
     * 小塔单点登录接口地址
     */
    private String xiaotaUrl = "http://10.19.41.72:25003/ai-sso/newSsoLogin";

    /**
     * 小塔3DES密钥（固定值）
     */
    private String xiaota3DesKey = "6#dPz>3F";

    /**
     * 小塔发送方编码
     */
    private String fromSysCode = "CHNTSLW_SLYYPT_006";

    /**
     * 小塔目标编码（可选）
     */
    private String toSysCode;

    /**
     * 小塔重定向地址（可选）
     */
    private String defaultRedirectCode;
}
