package com.chinatower.platform.project.common.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author：liuxt
 * @Description：发送消息
 * @Date：2022/8/5 11:34
 * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
 */
@Service
@Slf4j
public class SendToOthersUtil {

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * @Author：liuxt
     * @Description：将API配置信息发送给网关系统
     * @Date：2022/8/5 11:34
     * @param url
     * @param params
     * @param header
     * @return HttpResponse
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    public static HttpResponse sendAPI(String url, String params, Map<String, String> header) {
        String msgId = simpleUUID();
        HttpResponse result = null;
        Map<String, String> headerMap = getHeaderMap(header);
        if (params == null || "".equals(params)) {
            params = "{}";
        }
        try {
            result = ((HttpRequest)HttpRequest.post(url).addHeaders(headerMap)).body(params).timeout(30000).execute();//发送请求
            log.info("msgId:{},请求地址{},请求参数{},返回状态:{},返回信息{}", new Object[]{msgId, url, params, result.getStatus(), result.body()});
            return result;
        } catch (Exception e) {
            log.error("===将API配置信息发送给网关系统 error",e);
            return result;
        }
    }

    /**
     * @Author：liuxt
     * @Description：获取请求头信息
     * @Date：2022/8/5 11:34
     * @param headerMap
     * @return Map<String, String>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    public static Map<String, String> getHeaderMap(Map<String, String> headerMap) {
        if (headerMap == null) {
            headerMap = new HashMap(16);
        }

        ((Map)headerMap).put("Content-Type", "application/json;charset=UTF-8");
        return (Map)headerMap;
    }

    /**
     * @Author：liuxt
     * @Description：生成uuid
     * @Date：2022/8/5 11:34
     * @param
     * @return String
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString();
    }

}
