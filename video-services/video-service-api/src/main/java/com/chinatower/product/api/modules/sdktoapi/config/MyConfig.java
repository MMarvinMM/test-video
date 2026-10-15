package com.chinatower.product.api.modules.sdktoapi.config;

import com.chinatower.product.api.modules.sdktoapi.filter.ApiRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MyConfig implements WebMvcConfigurer {
    @Resource
    private ApiRequestInterceptor apiRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiRequestInterceptor).addPathPatterns("/api/sdkToApi");
    }
}
