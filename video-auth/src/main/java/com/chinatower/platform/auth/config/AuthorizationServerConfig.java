package com.chinatower.platform.auth.config;


//import com.chinatower.platform.auth.config.JwtTokenEnhancer;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明: 认证服务器配置
 * @author bruce
 * @date 2021-11-18
 */
@Configuration
@EnableAuthorizationServer
@RefreshScope
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;  //加密

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    /**
     * app token失效时间，默认24小时
     */
    @Value("${mall.auth.app.tokenValidity:86400}")
    private int appTokenValiditySeconds;
    /**
     * app 刷新token失效时间，默认30天
     */
    @Value("${mall.auth.app.refreshTokenValidity:2592000}")
    private int appRefreshTokenValiditySeconds;

    /**
     * web token失效时间，默认24小时
     */
    @Value("${mall.auth.web.tokenValidity:86400}")
    private int webTokenValiditySeconds;
    /**
     * web 刷新token失效时间，默认25小时
     */
    @Value("${mall.auth.web.refreshTokenValidity:90000}")
    private int webRefreshTokenValiditySeconds;

    @Autowired
    private TokenEnhancer jwtTokenEnhancer;


    private static final String ONE_SIX = "123456";
    private static final String PASSWORD = "password";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String AUTHORIZATION_CODE = "authorization_code";


    /**
     * 客户端详细信息服务配置器
     * 提供了JdbcClientDetailsService、InMemoryClientDetailsService 两种方式
     * @param clients
     */
    @Override
    @SneakyThrows
    public void configure(ClientDetailsServiceConfigurer clients) {
        clients.inMemory()
                // 客户端 id
                .withClient("web_manage")
                // 客户端密码
                .secret(passwordEncoder.encode(ONE_SIX))
                // 资源 id
                .resourceIds("")
                // 该客户端允许授权的类型
                .authorizedGrantTypes(AUTHORIZATION_CODE,REFRESH_TOKEN,PASSWORD)
                // 允许授权的范围
                .scopes("all")
                .accessTokenValiditySeconds(webTokenValiditySeconds)
                .refreshTokenValiditySeconds(webRefreshTokenValiditySeconds)
                .and()
                .withClient("app_manage")
                // 客户端密码
                .secret(passwordEncoder.encode(ONE_SIX))
                // 资源 id
                .resourceIds("")
                // 该客户端允许授权的类型
                .authorizedGrantTypes(AUTHORIZATION_CODE,REFRESH_TOKEN,PASSWORD)
                // 允许授权的范围
                .scopes("all")
//                .accessTokenValiditySeconds(appTokenValiditySeconds)
                .refreshTokenValiditySeconds(appRefreshTokenValiditySeconds)
                .and()
                .withClient("wx_manage")
                // 客户端密码
                .secret(passwordEncoder.encode(ONE_SIX))
                // 资源 id
                .resourceIds("")
                // 该客户端允许授权的类型
                .authorizedGrantTypes(AUTHORIZATION_CODE,REFRESH_TOKEN,PASSWORD)
                // 允许授权的范围
                .scopes("all")
//                .accessTokenValiditySeconds(appTokenValiditySeconds)
                .refreshTokenValiditySeconds(appRefreshTokenValiditySeconds)
                .and()
                // 客户端 id
                .withClient("win_manage")
                // 客户端密码
                .secret(passwordEncoder.encode(ONE_SIX))
                // 资源 id
                .resourceIds("")
                // 该客户端允许授权的类型
                .authorizedGrantTypes(AUTHORIZATION_CODE,REFRESH_TOKEN,PASSWORD)
                // 允许授权的范围
                .scopes("all")
                .accessTokenValiditySeconds(webTokenValiditySeconds)
                .refreshTokenValiditySeconds(webRefreshTokenValiditySeconds)
                .and()
                .withClient("pad_manage")
                // 客户端密码
                .secret(passwordEncoder.encode(ONE_SIX))
                // 资源 id
                .resourceIds("")
                // 该客户端允许授权的类型
                .authorizedGrantTypes(AUTHORIZATION_CODE,REFRESH_TOKEN,PASSWORD)
                // 允许授权的范围
                .scopes("all")
                .accessTokenValiditySeconds(appTokenValiditySeconds)
                .refreshTokenValiditySeconds(appRefreshTokenValiditySeconds);
    }


    /**
     * 令牌访问端点
     *AuthorizationServerEndpointsConfigurer其实是一个装载类，
     * 装载Endpoints所有相关的类配置（AuthorizationServer、TokenServices、TokenStore、ClientDetailsService、UserDetailsService）。
     * @param endpoints AuthorizationServerEndpointsConfigurer
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(jwtTokenEnhancer);
        delegates.add(jwtAccessTokenConverter);
        enhancerChain.setTokenEnhancers(delegates); //配置JWT的内容增强器
        endpoints
                .accessTokenConverter(jwtAccessTokenConverter)
                .authenticationManager(authenticationManager) //认证管理器
                .tokenStore(tokenStore)                       //保存令牌  默认使用内存当做存储介质。
                .userDetailsService(userDetailsService)        //校验 消费者信息 并授权
                .reuseRefreshTokens(true)   //refreshToken是否重复使用
                .tokenEnhancer(enhancerChain);
    }
    /**
     * 配置令牌端点(Token Endpoint)的安全约束
     * *  配置：安全检查流程,用来配置令牌端点（Token Endpoint）的安全与权限访问
     * 	 *  默认过滤器：BasicAuthenticationFilter
     * 	 *  1、oauth_client_details表中clientSecret字段加密【ClientDetails属性secret】
     * 	 *  2、CheckEndpoint类的接口 oauth/check_token 无需经过过滤器过滤，默认值：denyAll()
     * 	 * 对以下的几个端点进行权限配置：
     * 	 * /oauth/authorize：授权端点
     * 	 * /oauth/token：令牌端点
     * 	 * /oauth/confirm_access：用户确认授权提交端点
     * 	 * /oauth/error：授权服务错误信息端点
     * 	 * /oauth/check_token：用于资源服务访问的令牌解析端点
     * 	 * /oauth/token_key：提供公有密匙的端点，如果使用JWT令牌的话
     * anyRequest          |   匹配所有请求路径
     * access              |   SpringEl表达式结果为true时可以访问
     * anonymous           |   匿名可以访问
     * denyAll             |   用户不能访问
     * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
     * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
     * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
     * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
     * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
     * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
     * permitAll           |   用户可以任意访问
     * rememberMe          |   允许通过remember-me登录的用户访问
     * authenticated       |   用户登录后可访问
     * @param oauthServer AuthorizationServerSecurityConfigurer
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        /*
         * 获取令牌不需要认证，校验令牌需要认证，允许表单认证
         */
        oauthServer
                // 允许客户表单认证,不加的话/oauth/token无法访问
                .allowFormAuthenticationForClients()
                .passwordEncoder(passwordEncoder)
                // 对于CheckEndpoint控制器[框架自带的校验]的/oauth/token端点允许所有客户端发送器请求而不会被Spring-security拦截
                // 开启/oauth/token_key验证端口无权限访问
                .tokenKeyAccess("permitAll()")
                // 要访问/oauth/check_token必须设置为permitAll()，但这样所有人都可以访问了，设为isAuthenticated()又导致访问不了，这个问题暂时没找到解决方案
                // 开启/oauth/check_token验证端口认证权限访问
                .checkTokenAccess("permitAll()");

    }




//    @Bean
//    public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
//        return new JwtTokenStore(accessTokenConverter);
//    }

//    @Bean
//    @Primary
//    public DefaultTokenServices tokenServices() {
//        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
//        defaultTokenServices.setTokenStore(tokenStore);
//        defaultTokenServices.setSupportRefreshToken(true);
//        defaultTokenServices.setAccessTokenValiditySeconds(tokenValiditySeconds);
//        defaultTokenServices.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
//        return defaultTokenServices;
//    }



}
