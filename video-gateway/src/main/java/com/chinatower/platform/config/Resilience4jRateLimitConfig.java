package com.chinatower.platform.config;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jRateLimitConfig {

    @Value("${resilience4j.rateLimit.limitForPeriod}")
    private Integer limitForPeriod;

    @Value("${resilience4j.rateLimit.limitRefreshPeriod}")
    private Integer limitRefreshPeriod;

    @Value("${resilience4j.rateLimit.timeoutDuration}")
    private Integer timeoutDuration;

    // 针对特定URL的限流配置
    @Bean
    public RateLimiterConfig specificUrlRateLimiterConfig() {
        return RateLimiterConfig.custom()
                .limitForPeriod(limitForPeriod)           // 时间窗口内允许的请求数
                .limitRefreshPeriod(Duration.ofSeconds(limitRefreshPeriod)) // 时间窗口大小
                .timeoutDuration(Duration.ofMillis(timeoutDuration))     // 等待时间，0表示立即拒绝
                .build();
    }

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.of(specificUrlRateLimiterConfig());
    }
}
