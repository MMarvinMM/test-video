package com.chinatower.platform.filter;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.platform.api.LogFeign;
import com.chinatower.platform.api.dto.InterfaceOpeLog;
import com.chinatower.platform.authorization.AuthorizationManager;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.FeignConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@Order(2147483647)
public class WebLogFilter implements WebFilter {

    @Autowired
    private AuthorizationManager authorizationManager;

    @Autowired
    private LogFeign logFeign;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String uri = request.getURI().getPath();
        log.info("进入到过滤器中:doFilter() 方法");
        // 获取uri
        log.info("请求uri:{}", uri);
        HttpHeaders headers = request.getHeaders();
        log.info("请求headers:{}", headers);
        HttpStatus httpStatus = response.getStatusCode();
        Integer resultStatus;
        if (httpStatus.equals(HttpStatus.OK)) {
            resultStatus = 0;
        } else {
            resultStatus = 1;
        }
        MediaType contentType = request.getHeaders().getContentType();
        log.info("contentType:{}", contentType);
        
        try {
            // 如果是GET请求，直接处理
            if (isGetRequest(request)) {
                log.info("处理GET请求");
                return handleGetRequest(exchange, request, uri, resultStatus, contentType, chain);
            }
            
            // 如果是JSON内容的请求，处理请求体
            if (isJsonContentType(contentType)) {
                log.info("处理JSON请求");
                return handleJsonRequest(exchange, request, uri, resultStatus, contentType, chain);
            }
            
            log.info("处理其他类型请求");
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("WebLogFilter filter error: {}", e.getMessage());
            return chain.filter(exchange);
        }
    }

    private boolean isJsonContentType(MediaType contentType) {
        return Objects.nonNull(contentType) && contentType.toString().contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private boolean isGetRequest(ServerHttpRequest request) {
        return request.getMethod().name().toUpperCase().equals("GET");
    }

    private Mono<Void> handleJsonRequest(ServerWebExchange exchange, ServerHttpRequest request, 
            String uri, Integer resultStatus, MediaType contentType, WebFilterChain chain) {
        log.info("开始处理JSON请求");
        Flux<DataBuffer> body = request.getBody();
        return body.collectList()
                .flatMap(dataBuffers -> {
                    if (dataBuffers == null || dataBuffers.isEmpty()) {
                        log.info("请求体为空");
                        return chain.filter(exchange);
                    }

                    // 合并所有DataBuffer
                    DataBuffer joinedBuffer = exchange.getResponse().bufferFactory().join(dataBuffers);
                    try {
                        byte[] bytes = new byte[joinedBuffer.readableByteCount()];
                        joinedBuffer.read(bytes);
                        DataBufferUtils.release(joinedBuffer);
                        String content = new String(bytes, StandardCharsets.UTF_8);
                        log.info("req.getBody():{}", content);
                        log.info("req.getBody().length:{}", content.length());
                        
                        ServerHttpRequest wrappedRequest = createWrappedRequest(exchange, request, content);
                        ServerWebExchange wrappedExchange = exchange.mutate().request(wrappedRequest).build();
                        
                        // 异步记录日志
                        Mono.fromRunnable(() -> {
                            try {
                                extracted(request, uri, resultStatus, contentType, new StringBuffer(content));
                            } catch (Exception e) {
                                log.error("Error processing JSON request log: {}", e.getMessage());
                            }
                        }).subscribeOn(Schedulers.boundedElastic()).subscribe();
                        
                        return chain.filter(wrappedExchange);
                    } catch (Exception e) {
                        log.error("Error processing JSON request: {}", e.getMessage());
                        return chain.filter(exchange);
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error in JSON request handling: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }

    private ServerHttpRequest createWrappedRequest(ServerWebExchange exchange, 
            ServerHttpRequest request, String content) {
        return new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                return Mono.just(content)
                        .flatMapMany(body -> {
                            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                            return Flux.just(buffer);
                        });
            }
        };
    }

    private Mono<Void> handleGetRequest(ServerWebExchange exchange, ServerHttpRequest request,
            String uri, Integer resultStatus, MediaType contentType, WebFilterChain chain) {
        // 异步记录日志
        Mono.fromRunnable(() -> {
            try {
                extracted(request, uri, resultStatus, contentType, new StringBuffer(""));
            } catch (Exception e) {
                log.error("Error processing GET request log: {}", e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic()).subscribe();
        
        // 直接继续处理请求，不修改请求体
        return chain.filter(exchange);
    }

    private void extracted(ServerHttpRequest request, String uri, Integer resultStatus, MediaType contentType, StringBuffer body) {
        InterfaceOpeLog requestLogEntity = new InterfaceOpeLog();
        requestLogEntity.setAuthValue(request.getHeaders().getFirst("Authorization"));
        String clientType = request.getHeaders().getFirst(FeignConstants.HEADER_CLIENT_TYPE);
        String clientName = request.getHeaders().getFirst(FeignConstants.HEADER_CLIENT_NAME);
        if (StringUtils.isNotBlank(clientType)) {
            requestLogEntity.setClientName(clientType);
        } else if (StringUtils.isNotBlank(clientName)) {
            requestLogEntity.setClientName(clientName);
        } else {
            requestLogEntity.setClientName(FeignConstants.HEADER_CLIENT_PC);
        }
        requestLogEntity.setQueryStr(request.getURI().getQuery());
        requestLogEntity.setRequestMethod(request.getMethod().name());
        requestLogEntity.setOperUrl(uri);
        requestLogEntity.setStatus(resultStatus);
        requestLogEntity.setOperParam(StringUtils.isBlank(body.toString())?requestLogEntity.getQueryStr(): body.toString());
        if (Objects.nonNull(contentType) && StringUtils.isNotBlank(contentType.toString())) {
            requestLogEntity.setContentType(contentType.toString());
        }
        requestLogEntity.setOperIp(request.getRemoteAddress().getHostString());
        String authValue = request.getHeaders().getFirst("Authorization");
        log.info("authValue:{}", authValue);
        // 获取当前的用户
        LoginUser loginUser = authorizationManager.getLoginUser();
        log.info("WebLogFilter extracted loginUser:{}", JSONObject.toJSONString(loginUser));
        if ( StringUtils.isNotBlank(authValue) && Objects.nonNull(loginUser) && Objects.nonNull(loginUser.getUser())) {
            SysUser user = loginUser.getUser();
            String deptName = user.getDept().getDeptName();
            String userName = user.getUserName();
            String userType = user.getUserType();
            if(Objects.nonNull(loginUser.getSwitchdLoginUser())){
                deptName = loginUser.getSwitchdLoginUser().getUser().getDept().getDeptName();
                userName = loginUser.getSwitchdLoginUser().getUser().getUserName();
                userType = loginUser.getSwitchdLoginUser().getUser().getUserType();
            }else{
                //add by liuxt 20231115 客户账号则取客户id和客户名称
                if(Constants.TWO.equals(user.getUserType())){
                    requestLogEntity.setTenantId(user.getTenantId());
                    requestLogEntity.setTenantName(user.getTenantInfo().getTenantName());
                }
            }
            requestLogEntity.setDeptName(deptName);
            requestLogEntity.setOperName(userName);
            requestLogEntity.setUserType(userType);
        }

        //api请求，不登录，从请求头获取客户编码
        String customerCode = request.getHeaders().getFirst("X-CustomerCode");
        if(!StringUtils.isEmpty(customerCode)){
            requestLogEntity.setCustomerCode(customerCode);
        }

        try{
            logFeign.addLog(requestLogEntity);
        }catch (Exception e){
            log.error("logFeign addLog exception:{}",e.getMessage());
        }
    }
}
