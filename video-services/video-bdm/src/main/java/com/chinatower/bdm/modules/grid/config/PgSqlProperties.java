package com.chinatower.bdm.modules.grid.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "pgsql")
@Data
public class PgSqlProperties {

    @Value("${pgsql.host:10.43.86.38}")
    private String host;

    @Value("${pgsql.port:5432}")
    private Integer port;

    @Value("${pgsql.user:gtgis}")
    private String user;

    @Value("${pgsql.password:CFVTjmmoSXN56Rol}")
    private String password;

    @Value("${pgsql.database:gtgis}")
    private String database;
}
