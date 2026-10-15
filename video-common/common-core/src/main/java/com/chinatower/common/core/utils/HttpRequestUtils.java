package com.chinatower.common.core.utils;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author luzc
 * @date 2021/5/21 11:07
 * @desc 发送http请求工具类
 */
public class HttpRequestUtils {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestUtils.class);
    private static final CloseableHttpClient httpclient = HttpClients.createDefault();
    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";

    /**
     * 发送post请求
     * @param url    请求路径
     * @param param  请求参数
     * @param header 请求头
     * @return 请求结果
     */
    public static String sendPostWithHeader(String url, Map<String, String> param, Map<String, String> header) {
        // 设置参数
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        // 编码
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        // 取得HttpPost对象
        HttpPost httpPost = new HttpPost(url);
        // 防止被当成攻击添加的
        httpPost.setHeader("User-Agent", userAgent);

        // 加入请求头
        for (Map.Entry<String, String> entry : header.entrySet()) {
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            httpPost.setHeader(mapKey, mapValue);
        }


        // 参数放入Entity
        httpPost.setEntity(formEntity);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            // 执行post请求
            log.info("发送时间戳：" + header.get("timestamp"));
            log.info("当前时间戳：" + System.currentTimeMillis());
            response = httpclient.execute(httpPost);
            // 得到entity
            HttpEntity entity = response.getEntity();
            // 得到字符串
            result = EntityUtils.toString(entity);
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return result;
    }
}
