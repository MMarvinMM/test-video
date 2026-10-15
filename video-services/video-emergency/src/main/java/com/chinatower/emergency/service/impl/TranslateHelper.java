package com.chinatower.emergency.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
@Component
public class TranslateHelper {
    @Value("${tencent.translate.secret-id:}")
    private String secretId;

    @Value("${tencent.translate.secret-key:}")
    private String secretKey;

    @PostConstruct
    public void validateConfiguration() {
        if (secretId.trim().isEmpty() || secretKey.trim().isEmpty()) {
            throw new IllegalStateException("tencent.translate.secret-id and tencent.translate.secret-key must be configured");
        }
    }

    public String translateV2(String txt) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        log.info(txt + "-翻译开始" + System.currentTimeMillis());

        String token = "";
        String service = "tmt";
        String version = "2018-03-21";
        String action = "TextTranslate";
        String body = "{\"SourceText\":\"" + txt + "\",\"Source\":\"zh\",\"Target\":\"en\",\"ProjectId\":0}";
        String region = "ap-beijing";

        String rspTxt = doRequest(secretId, secretKey, service, version, action, body, region, token);

        log.info(rspTxt);

        JSONObject json = JSONObject.parseObject(rspTxt);


        log.info(json.toJSONString());

        log.info(txt + "-翻译结束" + System.currentTimeMillis());

        if(json.containsKey("Response")){
            return json.getJSONObject("Response").getString("TargetText");
        }else{

            throw new RuntimeException("翻译出错");
        }
    }

    // singleton client for connection reuse and better performance
    private static final OkHttpClient client = new OkHttpClient();

    public static String doRequest(
            String secretId, String secretKey,
            String service, String version, String action,
            String body, String region, String token
    ) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        Request request = buildRequest(secretId, secretKey, service, version, action, body, region, token);

        log.info(request.method() + " " + request.url());
        log.info(JSON.toJSONString(request.headers()));
        log.info(body);

        Response response = client.newCall(request).execute();

        return response.body().string();
    }
    public static Request buildRequest(
            String secretId, String secretKey,
            String service, String version, String action,
            String body, String region, String token
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        String host = "tmt.tencentcloudapi.com";
        String endpoint = "https://" + host;
        String contentType = "application/json; charset=utf-8";
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String auth = getAuth(secretId, secretKey, host, contentType, timestamp, body);
        return new Request.Builder()
                .header("Host", host)
                .header("X-TC-Timestamp", timestamp)
                .header("X-TC-Version", version)
                .header("X-TC-Action", action)
                .header("X-TC-Region", region)
                .header("X-TC-Token", token)
                .header("X-TC-RequestClient", "SDK_JAVA_BAREBONE")
                .header("Authorization", auth)
                .url(endpoint)
                .post(RequestBody.create(MediaType.parse(contentType), body))
                .build();
    }

    private static String getAuth(
            String secretId, String secretKey, String host, String contentType,
            String timestamp, String body
    ) throws NoSuchAlgorithmException, InvalidKeyException {
        String canonicalUri = "/";
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:" + contentType + "\nhost:" + host + "\n";
        String signedHeaders = "content-type;host";

        String hashedRequestPayload = sha256Hex(body.getBytes(StandardCharsets.UTF_8));
        String canonicalRequest = "POST"
                + "\n"
                + canonicalUri
                + "\n"
                + canonicalQueryString
                + "\n"
                + canonicalHeaders
                + "\n"
                + signedHeaders
                + "\n"
                + hashedRequestPayload;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String date = sdf.format(new Date(Long.valueOf(timestamp + "000")));
        String service = host.split("\\.")[0];
        String credentialScope = date + "/" + service + "/" + "tc3_request";
        String hashedCanonicalRequest =
                sha256Hex(canonicalRequest.getBytes(StandardCharsets.UTF_8));
        String stringToSign =
                "TC3-HMAC-SHA256\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

        byte[] secretDate = hmac256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = hmac256(secretDate, service);
        byte[] secretSigning = hmac256(secretService, "tc3_request");
        String signature =
                printHexBinary(hmac256(secretSigning, stringToSign)).toLowerCase();
        return "TC3-HMAC-SHA256 "
                + "Credential="
                + secretId
                + "/"
                + credentialScope
                + ", "
                + "SignedHeaders="
                + signedHeaders
                + ", "
                + "Signature="
                + signature;
    }

    public static String sha256Hex(byte[] b) throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(b);
        return printHexBinary(d).toLowerCase();
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public static byte[] hmac256(byte[] key, String msg) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }
}
