package com.chinatower.platform.filter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class SpecificUrlRateLimitFilter implements GlobalFilter, Ordered {

    private final RateLimiterRegistry rateLimiterRegistry;

    @Value("${resilience4j.rateLimit.urls}")
    private String rateLimitUrls;

    // 定义需要限流的URL模式
    private Map<String, String> urlPatterns;

    @PostConstruct
    public void initUrlPatterns() {
        Map<String, String> tempMap = new HashMap<>();
        if (rateLimitUrls != null && !rateLimitUrls.isEmpty()) {
            String[] split = rateLimitUrls.split(",");
            for (String s : split) {
                tempMap.put(s, s);
            }
        }
        urlPatterns = Collections.unmodifiableMap(tempMap);
    }

    public SpecificUrlRateLimitFilter(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();


        // 查找匹配的URL模式
        Optional<String> rateLimiterName = urlPatterns.entrySet().stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();


        if (rateLimiterName.isPresent()) {
            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(rateLimiterName.get());

            // 尝试获取许可
            boolean acquired = rateLimiter.acquirePermission();

            if (!acquired) {
                log.warn("Rate limit exceeded for path: {}, rateLimiter: {},ip:{}", path, rateLimiterName.get(), this.getClientIp(exchange));
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                String errorBody = "{\"code\": 429, \"msg\": \"请求频率过高，请稍后重试\", \"path\": \"" + path + "\"}";
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorBody.getBytes());
                return exchange.getResponse().writeWith(Mono.just(buffer));
            }
            log.debug("Request allowed for path: {}, rateLimiter: {}", path, rateLimiterName.get());
        }

        return chain.filter(exchange);
    }

    private String getClientIp(ServerWebExchange exchange){
        ServerHttpRequest request = exchange.getRequest();

        // 1. 优先处理代理场景：读取 X-Forwarded-For 头（最常用）
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (isValidIp(xForwardedFor)) {
            // X-Forwarded-For 格式：客户端IP, 代理1IP, 代理2IP... 取第一个有效IP
            String[] ips = xForwardedFor.split(",");
            for (String ip : ips) {
                if (isValidIp(ip.trim())) {
                    return ip.trim();
                }
            }
        }

        // 2. 若 X-Forwarded-For 无效，尝试 X-Real-IP 头（部分代理使用）
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (isValidIp(xRealIp)) {
            return xRealIp.trim();
        }

        // 3. 若无代理，直接从连接中获取原始 IP
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            return remoteAddress.getAddress().getHostAddress();
        }

        // 4. 所有方式失败，返回默认值
        return "unknown";
    }

    /**
     * 校验 IP 是否有效（非 null、非 empty、非 unknown）
     */
    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip.trim());
    }

    @Override
    public int getOrder() {
        return -1; // 高优先级
    }
}
