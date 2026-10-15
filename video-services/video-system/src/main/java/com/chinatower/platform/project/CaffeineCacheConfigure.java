package com.chinatower.platform.project;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class CaffeineCacheConfigure {

    @Bean(name = "caffeineCacheManager")
//    @Primary
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        Caffeine<Object, Object> build = Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(7, TimeUnit.DAYS)
                // 初始的缓存空间大小
                .initialCapacity(500)
                .removalListener(((key, value, cause) -> log.info("key:{} removed, removalCause:{}.", key, cause.name())))
                // 缓存的最大条数
                .maximumSize(1000);
        caffeineCacheManager.setCaffeine(build);
        // 不缓存空值
        caffeineCacheManager.setAllowNullValues(false);
        return caffeineCacheManager;
    }

}
