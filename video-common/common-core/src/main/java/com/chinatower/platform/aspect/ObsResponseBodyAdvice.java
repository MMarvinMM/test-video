package com.chinatower.platform.aspect;

import com.alibaba.fastjson.JSON;
import com.chinatower.platform.annotation.ObsEncrypt;
import com.chinatower.platform.props.HwObsProperties;
import com.chinatower.platform.props.ObsProperties;
import com.chinatower.platform.util.ObsSignUrlUtils;
import com.chinatower.platform.util.ObsSignUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: obs全局响应报文处理
 * @Author: 张帅
 * @Date: 2022/06/03 01:00
 **/
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ObsResponseBodyAdvice implements ResponseBodyAdvice {

    private final ObsProperties obsProperties;

    @Autowired
    private HwObsProperties hwObsProperties;

    private static Pattern p = Pattern.compile("((https?|ftp|gopher|telnet|file|Unsure):((//)|(\\\\))[\\w\\d:#@%/;$~_?\\u4e00-\\u9fa5\\+-=\\\\\\.&]*)", Pattern.CASE_INSENSITIVE);


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
        ObsEncrypt obsEncrypt = AnnotationUtils.findAnnotation(annotatedElement, ObsEncrypt.class);
        return obsEncrypt != null;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        body.getClass();
        log.info("对接口 :{} ，返回数据：{}，进行obs加密", serverHttpRequest.getURI().getPath(), JSON.toJSONString(body));
        String bodyStr = "";
        try {
            String obsMark = obsProperties.getBucketName().concat(".").concat(obsProperties.getEndPoint());
            bodyStr = filterUrl(JSON.toJSONString(body), obsMark);
            if (body instanceof String) {
                return bodyStr;
            }
        } catch (Exception e) {
            log.error("obsEncrypt 全局拦截出现异常{}", e.getMessage());
            log.error(e.getMessage(), e);
        }
        return JSON.parseObject(bodyStr, body.getClass());
    }


    /**
     * 对整个字符串中涉及指定桶的obs文件进行加密处理
     *
     * @param url
     * @param obsMark
     * @return
     */
    public String filterUrl(String url, String obsMark) {
        Matcher m = p.matcher(url);
        while (url != null && m.find()) {
            String tempObsUrl = m.group(0);
            if (tempObsUrl.contains(obsMark)) {
                String encyyptUrl = new ObsSignUtils(obsProperties).signUrl(tempObsUrl);
                url = url.replaceAll(tempObsUrl, encyyptUrl);
            }
        }
        return url;
    }

    /**
     * 对整个字符串中涉及指定桶的obs文件进行加密处理(支持批量配置多个桶数据)
     * @param url
     * @param hwObsProperties
     * @return
     */
    public static String filterUrl(String url ,HwObsProperties hwObsProperties) {
        List<Map<String,String>> bucketList = hwObsProperties.getBucketList();
        Matcher m = p.matcher(url);
        while (url != null && m.find()) {
            String tempObsUrl = m.group(0);
            for(Map<String,String> bucketMap :bucketList){
                hwObsProperties.setAk(bucketMap.get("ak"));
                hwObsProperties.setSk(bucketMap.get("sk"));
                hwObsProperties.setBucketName(bucketMap.get("bucketName"));
                if (tempObsUrl.contains(hwObsProperties.getBucketName())) {
                    String encyyptUrl = new ObsSignUrlUtils(hwObsProperties).signUrl(tempObsUrl);
                    if(!url.contains(encyyptUrl)){
                        url = url.replaceAll(tempObsUrl, encyyptUrl);
                    }
                }
            }

        }
        return url;
    }
}
