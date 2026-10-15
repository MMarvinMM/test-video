package com.chinatower.bdm.modules.grid.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "iserver")
@Data
public class IServerProperties {
    @Value("${iserver.account:}")
    public  String account;

    @Value("${iserver.password:}")
    public  String password;

    @Value("${iserver.url:}")
    public  String url;

    @Value("${iserver.showUrl:}")
    public  String showUrl;

    @Value("${iserver.savaDirectory:}")
    public  String savaDirectory;

}
