package com.chinatower.platform;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.platform.annotation.EnableRyFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

/**
 * @Description: msg 启动类
 * @Author: 张帅
 * @Date: 2021/11/12 16:05
 **/
@EnableAsync
@Slf4j
@EnableFeignClients(basePackages = "com.chinatower")
@EnableDiscoveryClient
@EnableGlobalEmbeddedPointInteceptor
@SpringBootApplication(exclude = { RedisAutoConfiguration.class, DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@ComponentScan({"com.chinatower"})
//@SpringBootApplication(exclude={SecurityAutoConfiguration.class, RedisAutoConfiguration.class})
public class MsgApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(MsgApplication.class, args);
        log.info("msg项目启动成功");
    }
}
