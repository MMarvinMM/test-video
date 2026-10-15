package com.chinatower.gis.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "geoserver")
@Data
public class GeoserverProperties {

    /**
     * 数据目录
     */
    @Value("${geoserver.dir:}")
    private String dir;
}
