package com.chinatower.platform.project.system.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

@Slf4j
public class HttpUtils {

    public static JSONObject doPostHeaders(String params, String url, String SECRET_KEY,String APP_KEY){
        log.info("doPostHeaders入参:{},{},{}",params,url,SECRET_KEY,APP_KEY);
        JSONObject object =null;
        Response response = null;
        try {
            Headers headers = Headers.of(
                    "Content-Type", "application/json",
                    "Accept", "application/json"
            );
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), params);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .headers(headers)
                    .build();
            response = client.newCall(request).execute();
            log.info("doPost返回状态:{}", JSON.toJSONString(response));
            if(200 == response.code()){
                return JSONObject.parseObject(response.body().string());
            }
        }catch (Exception e){
            log.error("doPost返回错误信息:{}",e);
        }finally {
            try {
                if(response != null){
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
