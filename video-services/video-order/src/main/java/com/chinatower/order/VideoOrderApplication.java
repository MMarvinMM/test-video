package com.chinatower.order;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.platform.annotation.EnableRyFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
/**
 * 订单管理模块
 *
 * @author chinatower
 */

@EnableRyFeignClients
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@SpringCloudApplication
@EnableAsync
@ComponentScan(basePackages={"com.chinatower.order","com.chinatower.platform", "com.chinatower.bdm", "com.chinatower.device",
        "com.chinatower.industry", "com.chinatower.video", "com.chinatower.event", "com.chinatower.system", "com.chinatower.manage"})
@Slf4j
@EnableGlobalEmbeddedPointInteceptor
public class VideoOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoOrderApplication.class, args);
        log.info("订单模块启动成功");
    }
}
