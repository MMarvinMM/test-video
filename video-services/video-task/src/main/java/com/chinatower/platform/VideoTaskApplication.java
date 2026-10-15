package com.chinatower.platform;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.platform.annotation.EnableRyFeignClients;
import com.chinatower.swagger.config.SwaggerAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * task模块
 *
 * @author chinatower
 */

@EnableRyFeignClients
@SpringBootApplication(exclude={SecurityAutoConfiguration.class, DruidDataSourceAutoConfigure.class, SwaggerAutoConfiguration.class})
@SpringCloudApplication
@EnableAsync
@ComponentScan(basePackages = {"com.chinatower.system","com.chinatower.platform","com.chinatower.video","com.chinatower.common"})
@EnableGlobalEmbeddedPointInteceptor
public class VideoTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoTaskApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  task模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
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
