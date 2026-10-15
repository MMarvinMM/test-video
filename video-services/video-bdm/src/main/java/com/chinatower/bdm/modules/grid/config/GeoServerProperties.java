package com.chinatower.bdm.modules.grid.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "geoserver")
@Data
public class GeoServerProperties {

    @Value("${geoserver.url:}")
    private String url;

    @Value("${geoserver.showUrl:}")
    private String showUrl;

    @Value("${geoserver.username:}")
    private String geoUsername;

    @Value("${geoserver.password:}")
    private String geoPassword;

    @Value("${geoserver.shpworkspace:chinatower}")
    public String shpWorkspace;

    @Value("${geoserver.layerShpWorkspace:tenant_layers}")
    public String layerShpWorkspace;
    /**
     * 国土命名空间
     */
    @Value("${geoserver.guotuWorkspace:guotu}")
    public String guotuWorkspace;
    /**
     * 国土存储仓库
     */
    @Value("${geoserver.guotuStore:guotu_pg_layer_store}")
    public String guotuStore;
    /**
     * geoserver 临时存储目录
     */
    @Value("${geoserver.geoTemDir:/usr/local}")
    private String geoTemDir;

    /**
     * SDK上传文件大小 （M）
     */
    @Value("${geoserver.fileSize:500}")
    private Integer fileSize;
}
