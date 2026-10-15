package com.chinatower.platform.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.chinatower.platform.common.FileOperation;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.concurrent.TimeUnit;

/**
 * @Author: songxinfeng
 * @Date: 2023/11/2 10:49
 */

@Slf4j
public class OfsUtil {


    private static final String TENANT_ID = "tenantId";
    private static final String TIMESTAMP = "timestamp";
    private static final String STORE_TYPE = "storeType";
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String TEMP_URL = "/api/refresh/ofsTempUrl";

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .connectionPool(new ConnectionPool(100, 100, TimeUnit.SECONDS))
            .build();


    /**
     * 根绝ofs地址及相关AK配置获取ofs文件加密地址
     * @param oldUrl
     * @param storeType
     * @param appId
     * @param privateKey
     * @param serverPrefix
     * @return
     */
    public static String tempUrl(String oldUrl,String storeType,String appId,String privateKey,String serverPrefix) {
        try {
            if (StringUtils.isEmpty(oldUrl)) {
                return null;
            }
            if (oldUrl.startsWith("http") || oldUrl.startsWith("https")) {
                String time = DateUtil.date().getTime() + "";
                String toSignStr;
                if ("ter".equals(SpringUtil.getActiveProfile())) {
                    toSignStr = String.format("oldUrl=%s&storeType=%s&tenantId=%s&timestamp=%s&key=%s",
                            oldUrl, storeType, appId, time, privateKey);
                } else {
                    toSignStr = String.format("oldUrl=%s&tenantId=%s&timestamp=%s&key=%s",
                            oldUrl, appId, time, privateKey);
                }
                String sign = sign(toSignStr, privateKey);

                ImmutableMap<Object, Object> immutableMap;
                if ("ter".equals(SpringUtil.getActiveProfile())) {
                    immutableMap = ImmutableMap.builder()
                            .put("oldUrl", oldUrl)
                            .put(TENANT_ID, appId)
                            .put(STORE_TYPE, storeType)
                            .put("sign", sign)
                            .put(TIMESTAMP, time)
                            .build();
                }else {
                    immutableMap = ImmutableMap.builder()
                            .put("oldUrl", oldUrl)
                            .put(TENANT_ID, appId)
                            .put("sign", sign)
                            .put(TIMESTAMP, time)
                            .build();
                }


                Request request = new Request.Builder()
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .url(serverPrefix + TEMP_URL)
                        .post(RequestBody.create(MediaType.parse(APPLICATION_JSON), JSON.toJSONString(immutableMap)))
                        .build();
                log.info("入参 body  = {}", JSON.toJSONString(immutableMap));
                String string = parseResponsebodyString(request);
                JSONObject jsonObject = JSON.parseObject(string);
                return (String) JSONPath.eval(jsonObject, "$data");
            } else {
                String time = DateUtil.date().getTime() + "";
                String toSignStr =
                        String.format("fileName=%s&storeType=%s&targetTenantId=%s&tenantId=%s&timestamp=%s&key=%s",
                                oldUrl,storeType, appId, appId, time, privateKey);
                String sign = sign(toSignStr, privateKey);

                ImmutableMap<Object, Object> body = ImmutableMap
                        .builder()
                        .put("fileName", oldUrl)
                        .put("targetTenantId", appId)
                        .put(TENANT_ID, appId)
                        .put(STORE_TYPE, storeType)
                        .put("sign", sign)
                        .put(TIMESTAMP, time)
                        .build();
                log.info("入参 body  = {}", JSON.toJSONString(body));
                RequestBody requestBody = RequestBody.create(MediaType.parse(APPLICATION_JSON), JSON.toJSONString(body));
                Request request = new Request.Builder()
                        .url(serverPrefix + "/api/download/ofsTempUrl")
                        .post(requestBody)
                        .header(CONTENT_TYPE, APPLICATION_JSON)
                        .build();
                String responsebodyString = parseResponsebodyString(request);
                JSONObject jsonObject = JSON.parseObject(responsebodyString);
                return (String) JSONPath.eval(jsonObject, "$.data");
            }
        } catch (Exception e) {
            log.error("加密文件错误", e);
            log.error("oldUrl = " + oldUrl);

            return null;
        }
    }

    public static String parseResponsebodyString(Request request) throws IOException {
        Response response = CLIENT.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new FileOperation.FileOperationException("调用文件服务错误:  + " + response.code());
        }
        if (response.body() == null) {
            throw new FileOperation.FileOperationException("调用文件服务无返回数据");
        }
        String string = response.body().string();
        log.info("调用文件服务返回数据 {}", string);
        response.close();
        return string;
    }

    /**
     * 私钥签名
     *
     * @param preStr     待签名字符串
     * @param privateKey
     * @return base64字符串
     * @throws Exception
     */
    public static String sign(String preStr, String privateKey)
            throws Exception {
        String strSign = "";
        String suite = "SHA256WithRSA";
        //初始化算法SHA256
        Signature signature = Signature.getInstance(suite);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        //初始化私钥+RSA
        KeyFactory keyFac = KeyFactory.getInstance("RSA");
        PrivateKey privateKey2 = keyFac.generatePrivate(keySpec);
        signature.initSign(privateKey2);
        //待签名字符串转byte数组使用UTF8
        byte[] msgBuf = preStr.getBytes(StandardCharsets.UTF_8);
        signature.update(msgBuf);
        byte[] byteSign = signature.sign();
        //签名值byte数组转字符串用BASE64
        strSign = Base64.encodeBase64String(byteSign);
        return strSign;
    }

}
