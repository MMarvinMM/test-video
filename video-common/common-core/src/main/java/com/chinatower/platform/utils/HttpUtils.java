package com.chinatower.platform.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class HttpUtils {

    public static String postRquest(String url,Map<String,String> params) throws Exception{
        return postRequest(url,null,params);
    }

    public static String postRequest(String url, String token, Map<String,String> params) throws Exception{
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if(!params.isEmpty()){
            params.forEach((key, value) -> {
                builder.addFormDataPart(key, value);
            });
        }
        MultipartBody body = builder.build();

        Request.Builder rb = new Request.Builder();
        rb.url(url);
        rb.method("POST",body);
        if(StringUtils.isNotEmpty(token)){
            rb.addHeader("token",token);
        }

        Request request =rb.build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String getRequest(String url) throws Exception{
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String postRequestNew(String url, Map params) throws Exception{
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(json, JSONObject.toJSONString(params));
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request requestPost = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = client.newCall(requestPost).execute();
        return response.body().string();
    }
}
