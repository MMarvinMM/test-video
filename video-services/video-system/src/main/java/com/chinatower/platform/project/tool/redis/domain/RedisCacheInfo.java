package com.chinatower.platform.project.tool.redis.domain;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import lombok.Data;

/**
 * 缓存项信息表
 * @author wangzic
 * @date 2020-04-21
 */
@Data
public class RedisCacheInfo extends SystemBaseEntity {
    private static final long serialVersionUID = 1L;

    private String cacheKey;

    private String cacheValue;

    private String cacheType;

    private Long cacheTimeout;

    private Integer pageNum;

    private Integer pageSize;

    public RedisCacheInfo() {
    }

    public RedisCacheInfo(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public RedisCacheInfo(String cacheKey, String cacheValue) {
        this.cacheKey = cacheKey;
        this.cacheValue = cacheValue;
    }

    public RedisCacheInfo(String cacheKey, String cacheValue, String cacheType, long cacheTimeout) {
        this.cacheKey = cacheKey;
        this.cacheValue = cacheValue;
        this.cacheType = cacheType;
        this.cacheTimeout = cacheTimeout;
    }
}
