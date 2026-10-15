package com.chinatower.product.api.modules.sdktoapi.config;

import com.chinatower.product.api.modules.common.Constants;
import com.chinatower.product.api.modules.sdktoapi.filter.ApiRequestInterceptor;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfiguration implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Content-Type","application/json");

        String key = Constants.SDK_TO_API + ApiRequestInterceptor.USER_CODE_LOCAL.get();
        log.info("preHandle-redisKey=" + key);
        if(RedisUtil.hasKey(key, RedisDBType.PRIMARY)){
            requestTemplate.header("Cur-Industry-Code", RedisUtil.hGet(key, Constants.INDUSTRY_CODE,RedisDBType.PRIMARY));
            requestTemplate.header("Authorization", RedisUtil.hGet(key, Constants.TOKEN,RedisDBType.PRIMARY));
            requestTemplate.header("userKey", RedisUtil.hGet(key, Constants.USER_CODE,RedisDBType.PRIMARY));
        }
        log.info("feign interceptor header:{}",requestTemplate);
    }
}
