package com.chinatower.platform.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @className: HwObsProperties
 * @description:
 * @author: yongqi.han
 * @date: 2022/7/4
 * @requestID 需求单号及需求名称
 */
@Data
@Component
@ConfigurationProperties(HwObsProperties.PREFIX)
public class HwObsProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "hwobs";

    private boolean enable = false;

    private String ak;

    private String sk;

    private String endPoint;

    private String bucketName;

    private List<Map<String,String>> bucketList;

    private String suffixRules;

    private int connectTimeOut;

    private int socketTimeOut;

    private long expires;

}
