package com.chinatower.event.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.web.domain.ApiResult;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.file.feign.client.FileObsFeignClient;
import com.chinatower.file.request.FileObsEncryptRequest;
import com.chinatower.file.response.FileObsEncryptResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description:
 * @author: yongqi.han
 * @date: 2022/8/5
 */
@Slf4j
@RestControllerAdvice
public class EventObsResponseBodyAdvice implements ResponseBodyAdvice {

    @Autowired
    private FileObsFeignClient fileObsFeignClient;

    private static Pattern p = Pattern.compile("(^/.+(bmp|gif|jpeg|png|zip|pdf|svg|jpg|mp4|mp3|docx|txt|doc|ppt|pptx)$)|(http.*|https.*)", Pattern.CASE_INSENSITIVE);

    /**
     * Whether this component supports the given controller method return type
     * and the selected {@code HttpMessageConverter} type.
     *
     * @param methodParameter the return type   方法返回的类型
     * @param aClass          the selected converter type   参数类型装换
     * @return {@code true} if {@link #beforeBodyWrite} should be invoked;
     * {@code false} otherwise
     * 返回 true 则下面 beforeBodyWrite方法被调用, 否则就不调用下述方法
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        AnnotatedElement annotatedElement = methodParameter.getAnnotatedElement();
        EventObsEncrypt eventObsEncrypt = AnnotationUtils.findAnnotation(annotatedElement, EventObsEncrypt.class);
        return eventObsEncrypt != null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        body.getClass();
        log.info("对接口 :{} ，返回数据：{}，进行obs加密", serverHttpRequest.getURI().getPath(), JSON.toJSONString(body));
        String bodyStr = "";
        try {
            bodyStr = filterUrlNew(JSON.toJSONStringWithDateFormat(body, "yyyy-MM-dd HH:mm:ss"));
            log.info("bodyStr = {}", bodyStr);
            if (body instanceof String) {
                return bodyStr;
            }
        } catch (Exception e) {
            log.error("obsEncrypt 全局拦截出现异常{}", e.getMessage());
            log.error(e.getMessage(), e);
            bodyStr = JSON.toJSONStringWithDateFormat(body, "yyyy-MM-dd HH:mm:ss");
        }
        return JSON.parseObject(bodyStr, body.getClass());
    }

    /**
     * 对整个字符串中涉及指定桶的obs文件进行加密处理
     *
     * @param body
     * @return
     */
    public String filterUrlNew(@NonNull String body) {
        Set<String> collector = new HashSet<>();
        if (body.startsWith("[")) {
            JSONArray jsonObject = JSON.parseArray(body);
            fill(collector, jsonObject);
        }
        if (body.startsWith("{")) {
            JSONObject jsonObject = JSON.parseObject(body);
            fill(collector, jsonObject);
        }
        FileObsEncryptRequest obsEncryptRequest = new FileObsEncryptRequest();
        obsEncryptRequest.setFileUrls(new ArrayList<>(collector));
        obsEncryptRequest.setUseOfs(true);
        log.info("obsEncryptRequest  = {}", JSON.toJSONString(obsEncryptRequest));
        ApiResult<List<FileObsEncryptResponse>> result = fileObsFeignClient.getObsEncryptUrls(obsEncryptRequest);
        log.info("result  = {}", JSON.toJSONString(result));
        if (ApiResult.SUCCESS_CODE_0000.equals(result.getReturnCode())) {
            List<FileObsEncryptResponse> responseList = result.getData();
            responseList = responseList.stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FileObsEncryptResponse::getFileUrl))), ArrayList::new));
            for (FileObsEncryptResponse tempObsUrls : responseList) {
                String fileUrl = tempObsUrls.getFileUrl();
                String encryptUrl = tempObsUrls.getEncryptUrl();
                if (fileUrl != null && encryptUrl != null && !fileUrl.contains("\\")) {
                    body = body.replace(fileUrl, encryptUrl);
                }
            }
            return body;
        }
        return body;
    }

    static void fill(Set<String> collector, Object o) {

        if (o == null) {
            return;
        }

        if (o instanceof JSONObject) {
            Set<String> strings = ((JSONObject) o).keySet();
            for (String key : strings) {
                Object o1 = ((JSONObject) o).get(key);
                fill(collector, o1);
            }
        }
        if (o instanceof JSONArray) {
            for (Object o1 : ((JSONArray) o)) {
                fill(collector, o1);
            }
        }
        if (o.toString().contains(",")) {
            String[] split = o.toString().split(",");
            for (String s : split) {
                if (p.matcher(s).matches()) {
                    collector.add(s);
                }
            }
        } else {
            if(p.matcher(o.toString()).matches()) {
                collector.add(o.toString());
            }
        }

    }

}
