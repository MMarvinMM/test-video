package com.chinatower.platform.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.platform.constants.FeignConstants;
import com.nimbusds.jose.JWSObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;

/**
 * @Description: 将登录用户的JWT转化成用户信息的全局过滤器
 * @Author: 张帅
 * @Date: 2021/11/12 15:40
 **/
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String industryCode = exchange.getRequest().getHeaders().getFirst("Cur-Industry-Code");
        if (StrUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }
        try {
            //从token中解析用户信息并设置到Header中去
            String realToken = token.replace("Bearer ", "");
            JWSObject jwsObject = JWSObject.parse(realToken);
            String payLoad = jwsObject.getPayload().toString();
            JSONObject json = JSONObject.parseObject(payLoad);
            String clientId = json.getString("client_id");
            String userId = json.getString("userId");
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header(FeignConstants.HEADER_USERID, userId)
                    .header(FeignConstants.HEADER_USER_KEY, clientId.concat(":").concat(userId))
                    .header(FeignConstants.HEADER_INDUSTRY_CODE, industryCode)
                    .build();
            exchange = exchange.mutate().request(request).build();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
