//package com.chinatower.platform.filter;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.web.cors.reactive.CorsUtils;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
///**
// * @desription: 解决跨域问题
// * @data 创建时间：2023/8/8 22:07
// * @author chuan hao
// * @version 1.0
// *
// */
//@Configuration
//public class CorsConfig {
//
//    //这里为支持的请求头，如果有自定义的header字段请自己添加（不知道为什么不能使用*）
//
//    private static final String ALLOWED_HEADERS = "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN,token,username,client,apiKey,dataRegion";
//
//    private static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS";
//
//    private static final String ALLOWED_ORIGIN = "*";
//
//    private static final String ALLOWED_EXPOSE = "*";
//
//    private static final String MAX_AGE = "18000L";//预检缓存时长
//
//    @Bean
//
//    public WebFilter corsFilter() {
//
//        return (ServerWebExchange ctx, WebFilterChain chain) -> {
//
//            ServerHttpRequest request = ctx.getRequest();
//
//            if (CorsUtils.isCorsRequest(request)) {
//
//                ServerHttpResponse response = ctx.getResponse();
//
//                HttpHeaders headers = response.getHeaders();
//
//                headers.add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
//
//                headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);//允许请求的方法;
//
//                headers.add("Access-Control-Max-Age", MAX_AGE);
//
//                headers.add("Access-Control-Allow-Headers", ALLOWED_HEADERS);//允许什么请求头 *表示所有
//
//                headers.add("Access-Control-Expose-Headers", ALLOWED_EXPOSE);//配置前端js允许访问的自定义响应头
//
//                headers.add("Access-Control-Allow-Credentials", "true");//允许后端服务跨域cookie传到前端
//
//                if (request.getMethod() == HttpMethod.OPTIONS) {
//
//                    response.setStatusCode(HttpStatus.OK);
//
//                    return Mono.empty();
//
//                }
//
//            }
//
//            return chain.filter(ctx);
//
//        };
//
//    }
//
//}