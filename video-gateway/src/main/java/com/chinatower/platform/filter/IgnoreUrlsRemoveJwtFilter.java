package com.chinatower.platform.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.chinatower.common.core.constant.CacheConstants;
import com.chinatower.common.core.utils.HMAC_SHA1;
import com.chinatower.platform.common.utils.security.MD5Util;
import com.chinatower.platform.config.security.IgnoreUrlsConfig;
import com.chinatower.platform.modle.CommonResult;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.nimbusds.jose.JWSObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 白名单路径访问时需要移除JWT请求头
 * @Author: 张帅
 * @Date: 2021/11/12 15:39
 **/
@Slf4j
@Component
public class IgnoreUrlsRemoveJwtFilter implements WebFilter {
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    private static final String AK_ERROR = "AK 无效";
    private static final String UTF_8 = "UTF-8";
    private static final String TOKEN_ERROR = "token 无效";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        URI uri = request.getURI();

        /*
         * {
         *   "industryCode":{
         *       "authPathLis":[url1,url2],
         *       "ak":"asaa",
         *       "sk":"sads"
         *   }
         * }
         *
         * */
        HandlerMethod handlerMethod = (HandlerMethod) exchange.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);

        if (Objects.nonNull(handlerMethod)) {
            RequestMapping requestMapping = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequestMapping.class);
            requestMapping.name();
            log.info("requestMapping.name():{}", requestMapping.name());
            log.info("requestMapping.path():{}", requestMapping.path());
            log.info("requestMapping.value():{}", requestMapping.value());
        }
        log.info("认证拦击 入参 path = {} header = {}", uri.getPath(), request.getHeaders());
        String industryCode = null;
        HttpHeaders headers = exchange.getRequest().getHeaders();
        //先判定是否存在industryCode
        List<String> industryCodeStrArray = headers.get("X-INDUSTRY_CODE");
        if (CollectionUtils.isNotEmpty(industryCodeStrArray)) {
            industryCode = industryCodeStrArray.get(0);
        }
        if (StrUtil.isNotEmpty(industryCode)) {
            log.info("AK 认证 industryCode = {}", industryCode);
            String industryAkAuthConfig = RedisUtil.hGet("industryAkAuthConfig", industryCode, RedisDBType.PRIMARY);
            log.info("AK 认证配置 industryAkAuthConfig = {}", industryAkAuthConfig);
            Object configBean = JSON.parse(industryAkAuthConfig);
            if (configBean != null) {
                JSONArray authPathArray = (JSONArray) JSONPath.eval(configBean, "$.authPathLis");
                if (CollectionUtils.isEmpty(authPathArray) || !authPathArray.contains(uri.getPath())) {
                    //当前路径没有配置ak 认证，无法鉴权
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    String body = JSON.toJSONString(CommonResult.unauthorized(AK_ERROR));
                    DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName(UTF_8)));
                    return response.writeWith(Mono.just(buffer));
                }

                //走AK认证逻辑
                String originAK = null;
                String originSignature = null;

                List<String> akstrArray = headers.get("X-AK");
                List<String> signatureStrArray = headers.get("X-SIGNATURE");
                if (CollectionUtils.isNotEmpty(akstrArray)) {
                    originAK = akstrArray.get(0);
                }
                if (CollectionUtils.isNotEmpty(signatureStrArray)) {
                    originSignature = signatureStrArray.get(0);
                }

                if (StrUtil.isEmpty(originAK)
                        && StrUtil.isEmpty(originSignature)
                        && StrUtil.isEmpty(industryCode)) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    String body = JSON.toJSONString(CommonResult.unauthorized(TOKEN_ERROR));
                    DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName(UTF_8)));
                    return response.writeWith(Mono.just(buffer));
                }

                if (StrUtil.isNotEmpty(originAK) && StrUtil.isNotEmpty(originSignature)) {

                    //ak 认证逻辑
                    String AK = (String) JSONPath.eval(configBean, "$.ak");
                    String SK = (String) JSONPath.eval(configBean, "$.sk");


                    if (StringUtils.isEmpty(AK) || StringUtils.isEmpty(SK) || CollectionUtils.isEmpty(authPathArray)) {
                        //当前查询的行业并未配置AK认证，无法鉴权
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
                        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                        String body = JSON.toJSONString(CommonResult.unauthorized(AK_ERROR));
                        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName(UTF_8)));
                        return response.writeWith(Mono.just(buffer));
                    }


                    String signature = encrypt(AK, SK);
                    if (!AK.equals(originAK) || !signature.equals(originSignature)) {
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
                        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                        String body = JSON.toJSONString(CommonResult.unauthorized(AK_ERROR));
                        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName(UTF_8)));
                        return response.writeWith(Mono.just(buffer));
                    }
                    return chain.filter(exchange);
                }
            }
        }

        PathMatcher pathMatcher = new AntPathMatcher();
        //1.判断是否是白名单的url，如果是则移除JWT请求头，并通过过
        List<String> ignoreUrls = ignoreUrlsConfig.getLevel1Urls();
        for (String ignoreUrl : ignoreUrls) {
            if (pathMatcher.match(ignoreUrl, uri.getPath()) || uri.getPath().contains("/VIDEO-APPLY-API/third/alarm/addThirdAlarmOrder")){
//                request = exchange.getRequest().mutate().header("Authorization", "").build();
//                exchange = exchange.mutate().request(request).build();
                return chain.filter(exchange);
            }
        }
        //2.判断token有误 如果没有返回401
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (StrUtil.isEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            String body = JSON.toJSONString(CommonResult.unauthorized(TOKEN_ERROR));
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName(UTF_8)));
            return response.writeWith(Mono.just(buffer));
        }
        try {
            String realToken = token.replace("Bearer ", "");

            JWSObject jwsObject = JWSObject.parse(realToken);
            String payLoad = jwsObject.getPayload().toString();
            JSONObject json = JSONObject.parseObject(payLoad);
            String cilent_id = json.getString("client_id");
            // 判断是否在黑名单内
            String tokenBlackListKey = CacheConstants.getTokenBlackListKey(cilent_id, MD5Util.hash(realToken));
            Boolean inBlack = RedisUtil.hasKey(tokenBlackListKey, RedisDBType.PRIMARY);
            if (inBlack != null && inBlack) {
                String cacheToken = RedisUtil.get(tokenBlackListKey, RedisDBType.PRIMARY);
                if (realToken.equalsIgnoreCase(cacheToken)) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    String body = JSON.toJSONString(CommonResult.unauthorized(TOKEN_ERROR));
                    DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                    return response.writeWith(Mono.just(buffer));
                }
            }

            //3. 判断token的过期时间如果小于5分钟则返回 让前台请求刷新token接口
            String exp = json.getString("exp");
            BigDecimal difference = new BigDecimal(exp).subtract(new BigDecimal(System.currentTimeMillis()).divide(new BigDecimal(1000)));
            // 0<过期时间-当前时间<=5分钟
            if (difference.compareTo(new BigDecimal(0)) > 0 && difference.compareTo(new BigDecimal(60 * 5)) <= 0) {
                response.setRawStatusCode(601);
                response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                String body = JSON.toJSONString(CommonResult.expToken("token即将失效 请刷新token"));
                DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName(UTF_8)));
                return response.writeWith(Mono.just(buffer));
            }

            //4.判断redis是否有用户信息 没有返回401
            String user_id = json.getString("userId");
            //String key = CommonConstants.WEB_LOGIN_TOKEN + cilent_id + ":" + user_id;
            String key = CacheConstants.USER_INDUSTRY_CODE + cilent_id + ":" + user_id;
            log.debug("cacheKey={}", key);
            if (!RedisUtil.hasKey(key, RedisDBType.PRIMARY)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                String body = JSON.toJSONString(CommonResult.unauthorized("token 失效"));
                DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(Charset.forName(UTF_8)));
                return response.writeWith(Mono.just(buffer));
            }
        } catch (ParseException e) {
            log.error("网关层解析token异常");
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return chain.filter(exchange);
    }

    private String encrypt(String ak, String sk) {
        return HMAC_SHA1.signByHmacSHA1(ak, sk);
    }
}
