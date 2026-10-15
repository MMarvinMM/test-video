package com.chinatower.platform;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.log.config.TowerLogConfiguration;
import com.chinatower.platform.annotation.EnableRyFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import com.chinatower.platform.feign.FeignAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 功能说明:
 *
 * @author 李峥
 * @date 2021-11-21 10:45
 */
@Slf4j
@EnableRyFeignClients
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(basePackages = "com.chinatower")
@Import(FeignAutoConfiguration.class)
@EnableGlobalEmbeddedPointInteceptor
@SpringBootApplication(exclude = {RedisAutoConfiguration.class,DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class, TowerLogConfiguration.class})
public class TowerLogApplication {
    public static void main(String[] args) {
        SpringApplication.run(TowerLogApplication.class, args);
        log.info("TowerLogApplication项目启动成功");
    }
}
