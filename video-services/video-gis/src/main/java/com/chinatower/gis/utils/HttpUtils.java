package com.chinatower.gis.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.Map;

/**
 * @ClassName HttpUtils
 * @Description HTTP请求工具类
 * @Author wjd
 * @Date 2021-08-26 17:40
 * @Version 1.0
 */
@Slf4j
public class HttpUtils {

    public static final String UTF_8 = "utf-8";

    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setSocketTimeout(10000)
            .setConnectionRequestTimeout(3000)
            .build();

    /**
     * GET请求，参数K-V形式
     *
     * @param url      请求地址
     * @param headMap  请求头参数
     * @param paramMap 请求参数
     * @return 返回结果
     */
    public static String doGet(String url, Map<String, String> headMap, Map<String, String> paramMap) {
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(REQUEST_CONFIG).build()) {
            URIBuilder builder = new URIBuilder(url);
            if (paramMap != null && !paramMap.isEmpty()) {
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    builder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpGet httpGet = new HttpGet(builder.build());
            if (headMap != null && !headMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    return EntityUtils.toString(response.getEntity(), UTF_8);
                }
                log.warn("HTTP GET请求返回非200状态码, url={}, statusCode={}", url, response.getStatusLine().getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("HTTP GET请求失败, url={}", url, e);
            return null;
        }
    }

    public static String doPost(String url, Map<String, String> headMap, Map<String, String> paramMap) {
        String result = "";
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(REQUEST_CONFIG).build()) {
            URIBuilder builder = new URIBuilder(url);
            if (paramMap != null && !paramMap.isEmpty()) {
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    builder.addParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpPost httpPost = new HttpPost(builder.build());
            if (headMap != null && !headMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                result = EntityUtils.toString(response.getEntity(), UTF_8);
            }
        } catch (Exception e) {
            log.error("HTTP POST请求失败, url={}", url, e);
        }
        return result;
    }

    /**
     * POST请求，请求体为JSON格式
     */
    public static String doPost(String url, Map<String, String> header, String jsonParams) {
        String result = "";
        try (CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(REQUEST_CONFIG).build()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json");
            StringEntity entity = new StringEntity(jsonParams, UTF_8);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            if (null != header && !header.isEmpty()) {
                for (Map.Entry<String, String> e : header.entrySet()) {
                    httpPost.setHeader(e.getKey(), e.getValue());
                }
            }
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                result = EntityUtils.toString(response.getEntity(), UTF_8);
            }
        } catch (Exception e) {
            log.error("HTTP POST请求失败, url={}", url, e);
        }
        return result;
    }
}


