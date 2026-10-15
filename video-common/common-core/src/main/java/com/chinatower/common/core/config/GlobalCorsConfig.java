//package com.chinatower.common.core.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//@Configuration
//public class GlobalCorsConfig {
//
//    @Value("${tower.cors.allowedOrigin:*}")
//    public String allowedOrigin;
//
//    @Bean
//    public CorsFilter corsFilter() {
//        //1.添加CORS配置信息
//        CorsConfiguration config = new CorsConfiguration();
//        //1) 允许的域,不要写*，否则cookie就无法使用了
//        config.addAllowedOrigin(allowedOrigin);
//        config.setAllowCredentials(true);
//        config.addAllowedMethod("GET");
//        config.addAllowedMethod("POST");
//        config.addAllowedHeader("*");
//
//
//        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
//        configSource.registerCorsConfiguration("/**", config);
//
//        return new CorsFilter(configSource);
//    }
//}
