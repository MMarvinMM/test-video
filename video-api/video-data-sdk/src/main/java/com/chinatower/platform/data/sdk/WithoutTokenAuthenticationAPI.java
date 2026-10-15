package com.chinatower.platform.data.sdk;


import com.chinatower.common.core.utils.HMAC_SHA1;
import com.chinatower.platform.data.sdk.entity.AbstactEntity;
import com.chinatower.platform.data.sdk.entity.BasicResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
@Slf4j
public class WithoutTokenAuthenticationAPI implements InitializingBean {
    //nacos 配置
//    @Value("${host}")
//    private String host;
//    @Value("${sdkAkAuthPort}")
//    private String port;
    private String urlPrefix;


    @Value("${skAuth.ak}")
    private String ak;
    @Value("${skAuth.sk}")
    private String sk;
    @Value("${skAuth.industryCode}")
    private String industryCode;
    @Value("${skAuth.gatewayHost}")
    private String gatewayHost;
    @Value("${skAuth.gatewayPort}")
    private String gatewayPort;

    private static final Map<String, String> MAP = new HashMap<>();

    static {
        for (AbstactEntity.URL value : AbstactEntity.URL.values()) {
            MAP.put(value.getName(), value.getUrl());
        }
    }

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build();

    protected OkHttpClient getHttpClient() {
        return httpClient;
    }

    public <RES> BasicResponse<RES> requestForPost(AbstactEntity entity, Function<String, BasicResponse<RES>> convert) {
        try {
            String url = urlPrefix + MAP.get(entity.getBindMapping().getName());
            RequestBody requestBody = RequestBody
                    .create(MediaType.parse("application/json;charset=UTF-8"), entity.getRequestBody());
            Request request = new Request.Builder()
                    .post(requestBody)
                    .url(url)
                    .addHeader("X-AK", ak)
                    .addHeader("X-SIGNATURE", HMAC_SHA1.signByHmacSHA1(ak, sk))
                    .addHeader("X-INDUSTRY_CODE", industryCode)
                    .build();
            log.info("调用 executeScript 入参 header = {} , body = {} , url = {} ", request.headers(), requestBody, url);
            Call call = getHttpClient().newCall(request);
            Response response = call.execute();
            if (response.body() == null) {
                log.error("调用 executeScript 出参 为空 ");
                BasicResponse.createWithError("102", "无响应数据");
            }
            String content = new String(response.body().bytes());
            log.error("调用 executeScript 出参 {} ", content);

            return convert.apply(content);
        } catch (Exception e) {
            log.error("executeScript 调用错误", e);
            return BasicResponse.createWithError("103", "系统错误");
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        urlPrefix = "http://" + gatewayHost + ":" + gatewayPort;
    }
}
