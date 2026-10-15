package com.chinatower.platform.filter;

import cn.hutool.core.lang.UUID;
import com.chinatower.common.constant.CompTechConstant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 给请求增加IP地址和TraceId
 *
 * @author lizheng
 */
@Slf4j
@Component
public class PreTraceIdRequestFilter implements GlobalFilter, Ordered {
    private boolean trace = true;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 是否开启traceId追踪
        if (trace) {
            // ID生成
            String traceId = UUID.fastUUID().toString();
            MDC.put(CompTechConstant.LOG_TRACE_ID, traceId);
            ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                    .headers(h -> h.add(CompTechConstant.TOWER_TRACE_ID_HEADER, traceId))
                    .build();
            ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
            return chain.filter(build);
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
