package com.chinatower.platform;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.platform.annotation.EnableRyFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@ComponentScan({"com.chinatower.system","com.chinatower.platform","com.chinatower.common.core.config","com.chinatower.common.connect","com.chinatower.order"})
@MapperScan(basePackages = {"com.chinatower.platform.*"})
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
@EnableRyFeignClients
@EnableScheduling
@EnableGlobalEmbeddedPointInteceptor
public class VideoOutDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoOutDataApplication.class, args);
        log.info("项目启动成功，接口访问地址：http://127.0.0.1:9220/");
    }
}
