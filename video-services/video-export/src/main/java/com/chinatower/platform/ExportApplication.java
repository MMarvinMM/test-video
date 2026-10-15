package com.chinatower.platform;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.common.core.annotation.EnableMongoExecutionLog;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @Description: 导出服务 启动类
 * @Author: 李峥
 * @Date: 2021/12/03 16:05
 **/
@EnableFeignClients(basePackages = "com.chinatower")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.chinatower.platform.*","com.chinatower.common.core.cache","com.chinatower.device"},exclude = { RedisAutoConfiguration.class, DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@MapperScan(basePackages = "com.chinatower.platform.mapper")
@EnableAsync
@EnableMongoExecutionLog
@Slf4j
@EnableGlobalEmbeddedPointInteceptor
public class ExportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExportApplication.class, args);
        log.info("Export项目启动成功");
    }
}
