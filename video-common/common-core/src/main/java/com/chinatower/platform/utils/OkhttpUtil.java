package com.chinatower.platform.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.Map;

@Slf4j
public class OkhttpUtil {

    /**
     * post请求第三方接口
     *
     * @param params
     * @param url
     * @return
     */
    public static JSONObject doPost(String url, String body, Map<String, String> header) {
        log.info("post请求第三方接口入参:{},{},{}", url, body, JSON.toJSONString(header));
        JSONObject object = null;
        Response response = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .headers(Headers.of(header))
                    .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), body))
                    .build();
            response = client.newCall(request).execute();
            log.info("post请求第三方接口返回状态:{}", response.code());
            if (200 == response.code()) {
                object = JSONObject.parseObject(response.body().string());
            }
            log.info("post请求第三方接口返回信息:{}", JSON.toJSONString(object));
        } catch (Exception e) {
            log.error("doPost返回错误信息:", e);
        } finally {
            try {
                if (response != null) {
                    //释放资源
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }
}
