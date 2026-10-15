package com.chinatower.platform;

import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.platform.feign.FeignAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description: 网关启动类
 * @Author: 张帅
 * @Date: 2021/11/04 11:39
 **/
@Slf4j
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableFeignClients(basePackages = "com.chinatower")
@EnableDiscoveryClient
@Import(FeignAutoConfiguration.class)
//@ComponentScan(basePackages = { "com.chinatower"})
@EnableScheduling
@SpringBootApplication(exclude = { RedisAutoConfiguration.class,MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, SecurityAutoConfiguration.class})
public class GatewayApplication {

    public static void main( String[] args )
    {
        SpringApplication.run(GatewayApplication.class, args);
        log.info("项目启动成功，接口访问地址：http://127.0.0.1:8080/");
    }
}
