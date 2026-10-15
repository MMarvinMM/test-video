package com.chinatower.platform.auth;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.platform.annotation.EnableRyFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


/**
 * 认证中心
 *
 * @author bruce
 * @date 2021年11月11日
 * *
 */
@Slf4j
@EnableDiscoveryClient
@EnableRyFeignClients
@SpringBootApplication(exclude = {RedisAutoConfiguration.class, SecurityAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@EnableGlobalEmbeddedPointInteceptor
//@ComponentScan({"com.chinatower.system"})
public class Oauth2Application {
    public static void main(String[] args) {
        SpringApplication.run(Oauth2Application.class, args);
        log.info("项目启动成功，接口访问地址：http://127.0.0.1:8082/");

    }
}
