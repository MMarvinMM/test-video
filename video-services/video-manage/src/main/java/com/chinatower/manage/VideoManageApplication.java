package com.chinatower.manage;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.platform.annotation.EnableRyFeignClients;
import com.chinatower.swagger.config.SwaggerAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 应用管理模块
 *
 * @author chinatower
 */

@EnableRyFeignClients
@SpringBootApplication(exclude={SecurityAutoConfiguration.class, SwaggerAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@SpringCloudApplication
@EnableAsync
@EnableGlobalEmbeddedPointInteceptor
@ComponentScan(basePackages = {"com.chinatower.manage", "com.chinatower.system", "com.chinatower.file"})
public class VideoManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoManageApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  应用管理启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " 　　………　　　　　………　　　　　　　………　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　\n" +
                "　　…………　　　　………　　　　　　　………　　　　　　　　　　　……………………　　　　　　　…………………　　　　\n" +
                "　　　………　　　…………　　　　　　　………　　　　　　　　　　…………………………　　　　　　…………………　　　　\n" +
                "　　　…………　　………　　　　　　　　………　　　　　　　　　　…………　　…………　　　　　　………　…………　　　\n" +
                "　　　…………　　………　　　　　　　　………　　　　　　　　　　………　　　　………　　　　　　………　…………　　　\n" +
                "　　　　………　………　　　　　　　　　………　　　　　　　　　…………　　　　………　　　　　　……………………　　　\n" +
                "　　　　………　………　　　　　　　　　………　　　　　　　　　…………　　　　………　　　　　　…………………　　　　\n" +
                "　　　　　………………　　　　　　　　　………　　　　　　　　　…………　　　　………　　　　　　………………　　　　　\n" +
                "　　　　　……………　　　　　　　　　　………　　　　　　　　　　…………　　…………　　　　　　………　　　　　　　　\n" +
                "　　　　　……………　　　　　　　　　　…………………　　　　　　………………………　　　　　　　………　　　　　　　　\n" +
                "　　　　　　…………　　　　　　　　　　…………………　　　　　　　…………………　　　　　　　　………　　　　　　　　\n" +
                "　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　………　　　　　 ");
    }

}
