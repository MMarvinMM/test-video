package com.chinatower.platform.config.security;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.chinatower.platform.authorization.AuthorizationManager;
import com.chinatower.platform.component.RestAuthenticationEntryPoint;
import com.chinatower.platform.component.RestfulAccessDeniedHandler;
import com.chinatower.platform.constant.AuthConstant;
import com.chinatower.platform.filter.IgnoreUrlsRemoveJwtFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 资源服务器配置
 * Created by macro on 2020/6/19.
 */
@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
@Slf4j
@SuppressWarnings("all")
public class ResourceServerConfig {
    private final AuthorizationManager authorizationManager;


    private final IgnoreUrlsConfig ignoreUrlsConfig;
    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter;
    private final TokenStore tokenStore;
//    private final WebLogFilter webLogFilter;

    @Resource
    RedisTemplate redisTemplate;
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        Map<Object,Object> industryAkAuthConfig = redisTemplate.opsForHash().entries("industryAkAuthConfig");
        log.info(" springSecurityFilterChain config industryAkAuthConfig = {}", JSON.toJSONString(industryAkAuthConfig));
//        SecurityWebFilterChain
//        Map<Object, Object> industryAkAuthConfig = RedisUtil.hGetAll("industryAkAuthConfig", RedisDBType.PRIMARY);
        List<String> authUrlList = industryAkAuthConfig.values().stream().map(o -> {
            List<String> authPathArray = (List<String>) JSONPath.eval(o, "$.authPathLis");
            return authPathArray.stream().toArray();
        }).flatMap(Stream::of).map(x -> ((String) x)).collect(Collectors.toList());
        //如果第一次启动，列表中没有配置会导致启动失败，添加一个保底配置，以保证启动正常
        if (CollectionUtils.isEmpty(authUrlList)) {
            authUrlList.add("/thisIsMockForStartServer");
        }
        log.info(" springSecurityFilterChain config authUrlList = {}", authUrlList);
        ReactiveAuthenticationManager tokenAuthenticationManager =
                new JwtAuthenticationManager(tokenStore);
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(tokenAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(new ServerBearerTokenAuthenticationConverter());

        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
        //自定义处理JWT请求头过期或签名错误的结果
        http.oauth2ResourceServer().authenticationEntryPoint(restAuthenticationEntryPoint);
        //对白名单路径，直接移除JWT请求头
        http.addFilterBefore(ignoreUrlsRemoveJwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        http
//                .authenticationManager(tokenAuthenticationManager)//待研究应该是认证相关的
                .authorizeExchange()
                .pathMatchers(ArrayUtil.toArray(ignoreUrlsConfig.getLevel1Urls(), String.class)).permitAll()//白名单配置
                .pathMatchers(ArrayUtil.toArray(authUrlList, String.class)).permitAll()//SDK 非token认证方式注入
                .anyExchange().access(authorizationManager)//自定义鉴权管理器配置
                .and().exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)//处理未授权
                .authenticationEntryPoint(restAuthenticationEntryPoint)//处理未认证
                .and().csrf().disable()
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);//增加自定义认证处理过滤器
//        http.addFilterBefore(webLogFilter, SecurityWebFiltersOrder.LAST);
        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(AuthConstant.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(AuthConstant.AUTHORITY_CLAIM_NAME);
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
