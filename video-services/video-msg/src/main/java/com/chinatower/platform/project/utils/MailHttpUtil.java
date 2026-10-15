package com.chinatower.platform.project.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

/**
 * 邮件组件 HTTP 工具类
 */
@Slf4j
public class MailHttpUtil {

    private MailHttpUtil() {
    }

    /**
     * 调用邮件组件发送接口（multipart/form-data）
     *
     * @param requestJson 参与签名的 request JSON 字符串
     * @param url         邮件组件地址
     * @return 响应结果
     */
    public static JSONObject doPost(String requestJson, String url) {
        log.info("MailHttpUtil.doPost url:{}, request:{}", url, requestJson);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost postRequest = new HttpPost(url);
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.RFC6532)
                    .setCharset(StandardCharsets.UTF_8);
            entityBuilder.addPart("request",
                    new StringBody(requestJson, ContentType.APPLICATION_JSON));
            postRequest.setEntity(entityBuilder.build());
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                log.info("MailHttpUtil.doPost response status:{}, body:{}", response.getStatusLine().getStatusCode(), responseString);
                return JSONObject.parseObject(responseString);
            }
        } catch (Exception e) {
            log.error("MailHttpUtil.doPost异常", e);
            return null;
        }
    }
}
