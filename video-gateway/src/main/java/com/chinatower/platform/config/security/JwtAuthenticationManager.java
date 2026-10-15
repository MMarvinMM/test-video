package com.chinatower.platform.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import reactor.core.publisher.Mono;

@Slf4j
//@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private TokenStore tokenStore;

    public JwtAuthenticationManager(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(a -> a instanceof BearerTokenAuthenticationToken)
                .cast(BearerTokenAuthenticationToken.class)
                .map(BearerTokenAuthenticationToken::getToken)
                .flatMap((accessToken -> {
                    log.info("accessToken is :{}", accessToken);
                    OAuth2AccessToken oAuth2AccessToken = null;
                    try {
                        oAuth2AccessToken = this.tokenStore.readAccessToken(accessToken);
                    } catch (Exception e) {
                        log.error("解析token异常1{}", e);
                        e.printStackTrace();
                        authentication.setAuthenticated(false);
                        return Mono.just(authentication);
                    }

                    if (oAuth2AccessToken == null) {//为空
                        log.info("Access Token 为空");
                        authentication.setAuthenticated(false);
                        return Mono.just(authentication);
                    } else if (oAuth2AccessToken.isExpired()) {//过期
                        log.info("Access Token 已经过期");
                        authentication.setAuthenticated(false);
                        return Mono.just(authentication);
                    }
                    OAuth2Authentication oAuth2Authentication = this.tokenStore.readAuthentication(accessToken);
                    if (oAuth2Authentication == null) {//无效
                        log.info("Access Token 无效");
                        authentication.setAuthenticated(false);
                        return Mono.just(authentication);
                    } else {
                        return Mono.just(oAuth2Authentication);
                    }
                }))
                .cast(Authentication.class);
    }
}