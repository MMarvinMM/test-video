package com.chinatower.platform.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 功能说明:
 *
 * @author 李峥
 * @date 2021-12-05 15:40
 * @requestID obs配置文件
 */
@Data
@ConfigurationProperties(ObsProperties.PREFIX)
public class ObsProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "obs";

    private boolean enable = false;

    private String ak;

    private String sk;

    private String endPoint;

    private String bucketName;

    private String suffixRules;

    private int connectTimeOut;

    private int socketTimeOut;

    private long expires;
}
