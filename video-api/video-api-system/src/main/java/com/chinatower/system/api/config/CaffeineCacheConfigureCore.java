package com.chinatower.system.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableCaching
public class CaffeineCacheConfigureCore {
    private static final String LOG_MSG = "key:{} removed, removalCause:{}.";
    @Primary
    @Bean("customCacheManager")
    public CacheManager customCacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        List<Cache> cacheList = new ArrayList<>();
        cacheList.add(cacheDictDataType());
        cacheList.add(cacheGetConfValue());
        cacheList.add(cacheDictDataTypeBatch());
        cacheList.add(cacheDictDataTypeNew());
        simpleCacheManager.setCaches(cacheList);
        return simpleCacheManager;
    }

    public Cache cacheDictDataTypeNew() {
        return new CaffeineCache("cacheDictDataTypeNew", Caffeine.newBuilder()
                // 设置最后一次写入经过固定时间过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 缓存的最大条数
                .maximumSize(1000)
                // 初始的缓存空间大小
                .initialCapacity(200)
                .removalListener(((key, value, cause) -> log.info(LOG_MSG, key, cause.name())))
                .build(),
                true);
    }

    public Cache cacheDictDataType() {
        return new CaffeineCache("cacheDictDataType", Caffeine.newBuilder()
                // 设置最后一次写入经过固定时间过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 缓存的最大条数
                .maximumSize(1000)
                // 初始的缓存空间大小
                .initialCapacity(200)
                .removalListener(((key, value, cause) -> log.info(LOG_MSG, key, cause.name())))
                .build(),
                true);
    }

    public Cache cacheDictDataTypeBatch() {
        return new CaffeineCache("cacheDictDataTypeBatch", Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 缓存的最大条数
                .maximumSize(1000)
                // 初始的缓存空间大小
                .initialCapacity(200)
                .removalListener(((key, value, cause) -> log.info(LOG_MSG, key, cause.name())))
                .build(),
                true);
    }

    public Cache cacheGetConfValue() {
        return new CaffeineCache("cacheGetConfValue", Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 缓存的最大条数
                .maximumSize(500)
                // 初始的缓存空间大小
                .initialCapacity(100)
                .removalListener(((key, value, cause) -> log.info(LOG_MSG, key, cause.name())))
                .build(),
                true);
    }
}
