package com.chinatower;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class, DruidDataSourceAutoConfigure.class},
        scanBasePackages = {"com.chinatower.product.*", "com.chinatower.framework.*","com.chinatower.platform.feign"
                ,"com.chinatower.device","com.chinatower.industry","com.chinatower.video"
})
@EnableFeignClients(basePackages = {"com.chinatower.*"})
@EnableDiscoveryClient
@MapperScan({"com.chinatower.**.dao", "com.chinatower.**.mapper"})
@EnableGlobalEmbeddedPointInteceptor
//@ComponentScan({"com.chinatower.system"})
public class VideoApplayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoApplayApiApplication.class, args);
    }
}

