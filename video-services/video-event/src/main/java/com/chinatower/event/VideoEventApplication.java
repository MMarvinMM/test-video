package com.chinatower.event;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.chinatower.common.connect.NetConnectUtils;
import com.chinatower.common.core.annotation.EnableGlobalEmbeddedPointInteceptor;
import com.chinatower.common.core.annotation.EnableMongoExecutionLog;
import com.chinatower.event.configuration.TaskThreadPoolConfig;
import com.chinatower.platform.annotation.EnableRyFeignClients;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 事件管理模块
 *
 * @author chinatower
 */

@EnableRyFeignClients
@SpringBootApplication(scanBasePackages = {"com.chinatower.product.*", "com.chinatower.event.*","com.chinatower.receive.*","com.chinatower.order","com.chinatower.video",
        "com.chinatower.platform.*","com.chinatower.system.*", "com.chinatower.file.*","com.chinatower.common.core.cache","com.chinatower.device","com.chinatower.common","com.chinatower.industry","com.chinatower.bdm"},
        exclude = {SecurityAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@SpringCloudApplication
@EnableAsync
@EnableScheduling
@MapperScan(basePackages = "com.chinatower.event.mapper")
@EnableConfigurationProperties({TaskThreadPoolConfig.class} )
@Slf4j
@EnableMongoExecutionLog
@EnableGlobalEmbeddedPointInteceptor
@EnableCaching
public class VideoEventApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoEventApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  事件管理模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
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
