package com.chinatower.gis;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.platform.annotation.DataPermissions;
import com.chinatower.platform.annotation.EnableRyFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * gis模块
 *
 * @author chinatower
 */

@EnableRyFeignClients
@SpringBootApplication(exclude={SecurityAutoConfiguration.class, DruidDataSourceAutoConfigure.class, MongoAutoConfiguration.class})
@SpringCloudApplication
@EnableAsync
@EnableGlobalEmbeddedPointInteceptor
@ComponentScan({"com.chinatower.platform","com.chinatower.bdm","com.chinatower.gis","com.chinatower.system"})
public class VideoGisApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoGisApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  gis模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
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
