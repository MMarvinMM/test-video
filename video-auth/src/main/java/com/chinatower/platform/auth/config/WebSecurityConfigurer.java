package com.chinatower.platform.auth.config;

import com.chinatower.platform.auth.service.DESPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 功能说明: webSecurity配置类
 * @author bruce
 * @date 2021-11-18
 */
@Primary
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    /**
     * WebSecurity是Spring Security对外的唯一出口 是在httpsecurity过滤链的外面一层
     * 设置不需要拦截的请求
     * 包含静态资源 登录请求
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/getUserStateByUserName",
                "/getStateByUserNameAndPhone",
                "/sendVerifyCode",
                "/checkVerifyCode",
                "/checkPasswordRepeat",
                "/updateByForgerPassword",
                "/oauth/login",
                "/oauth/findPhoneByUserName",
                "/findPhoneByUserName",
                "/sendMsgCodeByUserName",
                "/updatePassword",
                "/refreshToken",
                "/login/refreshWebLoginTime",
                "/login",
                "/valid",
                "/getConfig",
                "/loginTmp",
                "/checkPassword",
                "/checkPwd",
                "/chooseTrade",
                "/switchUser",
                "/captchaImage",
                "/base/logout",
                "/user/login",
                "/user/logout",
                "/oauth/interface/login",
                "/oauth/toLogin",
                "/oauth/getResources",
                "/oauth/refreshToken",
                "/oauth/checkToken",
                "/oauth/wechatLoginToken",
                "/oauth/wechatLogin",
                "/druid/**",
                "/oauth/register",
                "/oauth/sendSms",
                "/doc.html",
                "/user/**",
                "/dict/**",
                "/menu/**",
                "/oauth/appLogin",
                "/oauth/appRefreshToken",
                "/rsa/publicKey",
                "/delaySwitchEndTime",
                "/delaySwitch",
                "/wxLogin",
                "/wxLogout",
                "/wxBind",
                "/wx/portal",
                "/wx/createMenu",
                "/wx/chooseTrade",
                "/wxUpdateMsgControl",
                "/wx/invalidUser",
                "/getQrCode",
                "/checkQrCode",
                "/updateQrInfo",
                "/doQrLogin",
                "/doSwitchLogin",
                "/checkSwitchId",
                "/switchUserNew",
                "/setTokenBlackList",
                "/loginGenerateCode",
                "/userIdLogin",
                "/wx/colonyBind",
                "/checkOnlyPasswordRepeat",
                "/sdkToApiLogin",
                "/doUserIdLogin",
                "/fingerprint/toggleFingerprint",
                "/fingerprint/fingerprintLogin",
                "/fingerprint/isFingerprintButtonGray",
                "/fingerprint/isShowPasswordConfirmDialog",
                "/fingerprint/confirmPassword",
                "/sso/xiaoTaLogin",
                "/sso/appIdAndSecretSyncToTongyong"
                /*",/login.html",
                "/css/**",
                "/data/**",
                "/fonts/**",
                "/img/**",
                "/js/**"*/
//                "/oauth/check_token"
        );
    }

    /**
     * 创建授权管理认证对象
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 设置默认的加密方式
//        return new BCryptPasswordEncoder();
        return new DESPasswordEncoder();
    }



    /**
     * HttpSecurity是用来构建包含了一系列过滤器链的过滤器SecurityFilterChain
     * 配置必须拦截的请求
     * 配置登录表单
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .httpBasic()        //启用Http基本身份验证
                .and()
                .formLogin()       //启用表单身份验证
                .and()
                .authorizeRequests()    //限制基于Request请求访问
                .anyRequest()
                .authenticated();       //其他请求都需要经过验证

        //配置springSecurity要跳转登录页面的访问路径(暂时无用)
        http.formLogin().loginPage("/oauth/toLogin").
                //配置springSecurity执行登录的访问路径
                        loginProcessingUrl("/oauth/login");

    }


}
