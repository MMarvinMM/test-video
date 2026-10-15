package com.chinatower.platform.project.config;

import cn.jiguang.sdk.api.PushApi;
import cn.jpush.api.JPushClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 为满足临时需求，需要同时朝3个极光应用推送消息
 * @author yy
 */
@Slf4j
@Configuration
public class JpushConfig {

    @Value("${jpush.one.appKey:5f41de36652a830a0d7f680c}")
    private String appKey;

    @Value("${jpush.one.masterSecret:4600c47b6c4be1f9ca823d47}")
    private String masterSecret;

    @Value("${jpush.two.appKey:6d296b5fd2059cb06aa5c0da}")
    private String appKey2;

    @Value("${jpush.two.masterSecret:2112a69bc764777fb11dbd3b}")
    private String masterSecret2;

    @Value("${jpush.three.appKey:f94bae622a1e3f57c78d5054}")
    private String appKey3;

    @Value("${jpush.three.masterSecret:c6e0c0b716f8dd19f2667b0c}")
    private String masterSecret3;



    @Bean("jPushClient1")
    public JPushClient jPushClient1() {
        log.info("创建 jpush client 1");
        log.info(appKey, masterSecret);
        return new JPushClient(masterSecret, appKey);
    }

    @Bean("jPushClient2")
    public JPushClient jPushClient2() {
        log.info("创建 jpush client 2");
        log.info(appKey2, masterSecret2);
        return new JPushClient(masterSecret2, appKey2);
    }

//    @Bean("jPushClient3")
    public JPushClient jPushClient3() {
        log.info("创建 jpush client 3");
        log.info(appKey3, masterSecret3);
        return new JPushClient(masterSecret3, appKey3);
    }

    /**
     * 鸿蒙推送 PushApi（使用新SDK jiguang-sdk）
     * 对应上架app
     */
    @Bean("pushApi1")
    public PushApi pushApi1() {
        log.info("创建 pushApi1 用于鸿蒙推送");
        return new PushApi.Builder()
                .setAppKey(appKey)
                .setMasterSecret(masterSecret)
                .build();
    }

    /**
     * 鸿蒙推送 PushApi（使用新SDK jiguang-sdk）
     * 对应蒲公英
     */
    @Bean("pushApi2")
    public PushApi pushApi2() {
        log.info("创建 pushApi2 用于鸿蒙推送");
        return new PushApi.Builder()
                .setAppKey(appKey2)
                .setMasterSecret(masterSecret2)
                .build();
    }
}
