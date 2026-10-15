package com.chinatower.event.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;


@Slf4j
public class OkhttpUtil {



    /**
     * post请求第三方接口
     * @param params
     * @param url
     * @return
     */
    public static JSONObject doPost(String params, String url){
        log.info("doPost入参:{},{}",params,url);
        JSONObject object =null;
        Response response = null;
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), params);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            response = client.newCall(request).execute();
            log.info("doPost返回状态:{}",JSON.toJSONString(response));
            //log.info("doPost返回结果:{}",response.body().string());
            if(200 == response.code()){
                return JSONObject.parseObject(response.body().string());
            }

        }catch (Exception e){
            log.error("doPost返回错误信息:",e);
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




    public static String getSignature(String appKey,String SecretKey, String misId, String requestTime) throws Exception {
        log.info("SecretKey:{},misId:{},requestTime:{},APP_KEY:{}",SecretKey,misId,requestTime,appKey);
        String singStr = appKey + misId + requestTime;
        String generateSign = SignatureUtil.signByHmacSHA1(SecretKey, singStr);
        System.out.println(generateSign);
        return  generateSign;
    }


}
