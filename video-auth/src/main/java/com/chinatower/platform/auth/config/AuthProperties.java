package com.chinatower.platform.auth.config;

import com.chinatower.platform.model.dto.AuthConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * token配置
 *
 * @author qq on 2023/4/20
 */
@Component
@RefreshScope
public class AuthProperties extends AuthConfig {
    /**
     * web token失效时间，默认3小时
     */
    @Value("${security.web.business.token_validity:10800}")
    @Override
    public void setWebTokenValiditySeconds(int webTokenValiditySeconds) {
        super.setWebTokenValiditySeconds(webTokenValiditySeconds);
    }
    /**
     * app token失效时间，默认3小时
     */
    @Value("${security.app.business.token_validity:10800}")
    @Override
    public void setAppTokenValiditySeconds(int appTokenValiditySeconds) {
        super.setAppTokenValiditySeconds(appTokenValiditySeconds);
    }
    /**
     * app 刷新token失效时间，默认30天
     */
    @Value("${mall.auth.app.refreshTokenValidity:2592000}")
    @Override
    public void setAppRefreshTokenValiditySeconds(int appRefreshTokenValiditySeconds) {
        super.setAppRefreshTokenValiditySeconds(appRefreshTokenValiditySeconds);
    }
    /**
     * web 刷新token失效时间，默认25小时
     */
    @Value("${mall.auth.web.refreshTokenValidity:90000}")
    @Override
    public void setWebRefreshTokenValiditySeconds(int webRefreshTokenValiditySeconds) {
        super.setWebRefreshTokenValiditySeconds(webRefreshTokenValiditySeconds);
    }
}
