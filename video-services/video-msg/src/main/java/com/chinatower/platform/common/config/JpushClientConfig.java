package com.chinatower.platform.common.config;

import cn.jpush.api.JPushClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpushClientConfig {

    @Value("${JpushAPPKey:6d296b5fd2059cb06aa5c0da}")
    private String JpushAPPKey;
    @Value("${JpushMasterSecret:2112a69bc764777fb11dbd3b}")
    private String JpushMasterSecret;

    @Bean
    public JPushClient jPushClient(){
        return new JPushClient(JpushMasterSecret,JpushAPPKey);
    };
}
