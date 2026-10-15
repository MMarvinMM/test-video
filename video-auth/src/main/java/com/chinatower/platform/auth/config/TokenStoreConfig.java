package com.chinatower.platform.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;

/**
 * @Description: tokenStore配置
 * @Author: 张帅
 * @Date: 2021/11/24 16:43
 **/
@Configuration
@SuppressWarnings("all")
public class TokenStoreConfig {

    private final static String KEY_PASSWORD = "1qaz#EDC";
    private final static String KEY_ALIAS = "jryadmin";


    @Bean
    public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
        return new JwtTokenStore(accessTokenConverter);
    }

//    /**
//     * redis扩展
//     * @return
//     */
//    @Bean
//    public TokenStore redisTokenStore() {
//        return new RedisTokenStore(redisConnectionFactory);
//    }
@Bean
public JwtAccessTokenConverter accessTokenConverter() {
    JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
    jwtAccessTokenConverter.setKeyPair(keyPair());
    return jwtAccessTokenConverter;
}

    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("pk.jks"), KEY_PASSWORD.toCharArray());
        return keyStoreKeyFactory.getKeyPair(KEY_ALIAS, KEY_PASSWORD.toCharArray());
    }



}
