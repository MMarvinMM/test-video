package com.chinatower.platform;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.platform.annotation.EnableRyFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动程序
 *
 * @author chinatower
 */
@Slf4j
@EnableRyFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, RedisAutoConfiguration.class,DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@ComponentScan(basePackages = {"com.chinatower.platform","com.chinatower.system","com.chinatower.common"})
@EnableCaching
@EnableGlobalEmbeddedPointInteceptor
public class CompAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompAdminApplication.class, args);
        log.info("SUCCESS：项目启动成功");
    }
}
