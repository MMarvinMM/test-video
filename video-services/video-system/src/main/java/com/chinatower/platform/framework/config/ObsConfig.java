package com.chinatower.platform.framework.config;

import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: obs配置
 * @Author: zs
 * @Date: 2021/03/14 22:52
 **/
@Data
@Configuration
public class ObsConfig {

    @Value("${obs.ak}")
    private String ak;
    @Value("${obs.sk}")
    private String sk;
    @Value("${obs.endPoint}")
    private String endPoint;


    @Bean
    public ObsClient obsClient(){
        ObsConfiguration config = new ObsConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        ObsClient obsClient = new ObsClient(ak, sk, config);
        return obsClient;
    };
}
