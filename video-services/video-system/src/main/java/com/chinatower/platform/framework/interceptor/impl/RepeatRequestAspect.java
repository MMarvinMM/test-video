package com.chinatower.platform.framework.interceptor.impl;


import com.chinatower.platform.framework.interceptor.annotation.RepeatRequestCheck;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author LQ
 */
@Aspect
@Component
@Log4j2
public class RepeatRequestAspect {

    private final DefaultRedisScript<Long> rateLimitScript;

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 在 RepeatRequestAspect 中新增 Lua 脚本
    public RepeatRequestAspect() {
        this.rateLimitScript = new DefaultRedisScript<>();
        this.rateLimitScript.setScriptText(
                "local key = KEYS[1]\n" +
                        "local member = ARGV[1]\n" +
                        "local now = tonumber(ARGV[2])\n" +
                        "local interval = tonumber(ARGV[3])\n" +
                        " \n" +
                        "-- 检查参数有效性\n" +
                        "if not now or not interval then\n" +
                        "    return redis.error_reply(\"Invalid timestamp or interval\")\n" +
                        "end\n" +
                        " \n" +
                        "local lastScore = redis.call('ZSCORE', key, member)\n" +
                        "if lastScore then\n" +
                        "    lastScore = tonumber(lastScore)\n" +
                        "    if lastScore and (lastScore >= (now - interval)) then\n" +
                        "        return 1\n" +
                        "    end\n" +
                        "end\n" +
                        " \n" +
                        "-- 确保分数是整数（Redis 要求）\n" +
                        "redis.call('ZADD', key, tostring(now), member)\n" +
                        "redis.call('PEXPIRE', key, tostring(interval * 2))\n" +
                        "redis.call('ZREMRANGEBYSCORE', key, '-inf', tostring(now - interval))\n" +
                        "return 0");
        this.rateLimitScript.setResultType(Long.class);
    }

    @Around("@annotation(repeatSubmitCheck)")
    public Object around(ProceedingJoinPoint joinPoint, RepeatRequestCheck repeatSubmitCheck) throws Throwable {
        // 获取请求信息
        HttpServletRequest currentRequest = getHttpServletRequest();
        if (currentRequest == null) {
            log.warn("无法获取HttpServletRequest，跳过防重校验");
            return joinPoint.proceed();
        }

        String url = currentRequest.getRequestURI();
        String params;
        try {
            params = getSelectedParams(currentRequest, repeatSubmitCheck.fields());
        } catch (Exception e) {
            log.warn("防重校验：参数解析失败，跳过校验", e);
            return joinPoint.proceed();
        }

        // 参数校验
        if (url.isEmpty() || params.isEmpty()) {
            log.debug("防重校验：URL或参数为空 - URL: {}, Params: {}", url, params);
            return joinPoint.proceed();
        }

        // 生成Redis Key和参数MD5
        String urlMd5 = DigestUtils.md5DigestAsHex(url.getBytes(StandardCharsets.UTF_8));
        String paramMd5 = DigestUtils.md5DigestAsHex(params.getBytes(StandardCharsets.UTF_8));
        String redisKey = repeatSubmitCheck.prefix() + urlMd5;

        long now = System.currentTimeMillis();
        long interval = repeatSubmitCheck.interval();

        // 执行Lua脚本
        try {
            Long result = redisTemplate.execute(
                    rateLimitScript,
                    Collections.singletonList(redisKey),
                    paramMd5,
                    now,
                    interval
            );

            if (result != null && result == 1) {
                log.warn("防重校验拦截：URL={}, ParamsMD5={}", url, paramMd5);
                throw new RuntimeException("请勿重复提交请求");
            }
        } catch (RuntimeException e) {
            log.error("防重校验Redis操作失败", e);
            throw new RuntimeException(e.getMessage());
        }

        // 继续执行
        return joinPoint.proceed();
    }


    /**
     * 获取指定参数（或所有参数）
     *
     * @param request HttpServletRequest对象
     * @param fields 需要提取的字段名数组，如果为null则提取所有字段
     * @return 拼接的参数字符串（格式：key1=value1&key2=value2）
     */
    private String getSelectedParams(HttpServletRequest request, String[] fields) {
        Map<String, String> paramsMap = new TreeMap<>();
        StringBuilder result = new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String contentType = request.getContentType();
            if (contentType == null) {
                return "";
            }

            // 处理 JSON 请求体（application/json）
            if (contentType.contains("application/json")) {
                String body = getRequestBody(request);
                if (body != null && !body.isEmpty()) {
                    /*@SuppressWarnings("unchecked")
                    Map<String, Object> jsonMap = objectMapper.readValue(body, Map.class);*/
                    Map<String, Object> jsonMap = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
                    TreeMap<String, Object> sortedMap = new TreeMap<>(jsonMap);
                    if (fields == null || fields.length == 0) {
                        // 提取所有字段（递归处理嵌套 JSON）
                        sortedMap.forEach((key, value) -> appendParam(result, key, value));
                    } else {
                        // 提取指定字段
                        for (String field : fields) {
                            if (sortedMap.containsKey(field)) {
                                appendParam(result, field, sortedMap.get(field));
                            }
                        }
                    }
                }
            }
            // 处理表单数据（x-www-form-urlencoded 或 multipart/form-data）
            else if (contentType.contains("application/x-www-form-urlencoded")
                    || contentType.startsWith("multipart/form-data")) {

                Map<String, String[]> parameterMap = request.getParameterMap();

                if (fields != null) {
                    // 提取指定字段（支持多值）
                    for (String field : fields) {
                        String[] values = parameterMap.get(field);
                        if (values != null && values.length > 0) {
                            paramsMap.put(field, String.join(",", values));
                        }
                    }
                } else {
                    // 提取所有字段（支持多值）
                    for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                        String[] values = entry.getValue();
                        if (values != null && values.length > 0) {
                            paramsMap.put(entry.getKey(), String.join(",", values));
                        }
                    }
                }

                // 拼接参数字符串（自动 URL 编码）
                for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                    appendEncodedParam(result, entry.getKey(), entry.getValue());
                }
            }

            // 移除末尾的 "&"（如果有）
            if (result.length() > 0) {
                result.setLength(result.length() - 1);
            }

            return result.toString();

        } catch (Exception e) {
            log.warn("获取请求参数失败", e);
            return "";
        }
    }

    /**
     * 递归处理 JSON 参数（支持嵌套对象/数组）
     */
    private void appendParam(StringBuilder sb, String key, Object value) {
        if (value == null) {
            sb.append(key).append("=").append("&");
        } else if (value instanceof Map) {
            // 递归处理嵌套 Map（如 { "user": { "name": "John" } }）
            @SuppressWarnings("unchecked")
            Map<String, Object> nestedMap = (Map<String, Object>) value;
            nestedMap.forEach((nestedKey, nestedValue) ->
                    appendParam(sb, key + "." + nestedKey, nestedValue)
            );
        } else if (value instanceof Iterable) {
            // 处理数组/集合（如 { "tags": ["a", "b"] } → "tags=a,b"）
            sb.append(key).append("=");
            boolean first = true;
            for (Object item : (Iterable<?>) value) {
                if (!first) sb.append(",");
                sb.append(item);
                first = false;
            }
            sb.append("&");
        } else {
            // 普通值（自动调用 toString()）
            sb.append(key).append("=").append(value).append("&");
        }
    }

    /**
     * 拼接 URL 编码的参数（避免特殊字符破坏格式）
     */
    private void appendEncodedParam(StringBuilder sb, String key, String value) {
        try {
            String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8.name());
            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
            sb.append(encodedKey).append("=").append(encodedValue).append("&");
        } catch (Exception e) {
            log.warn("参数编码失败: key={}, value={}", key, value, e);
            //  fallback: 不编码直接拼接
            sb.append(key).append("=").append(value).append("&");
        }
    }
    /**
     * 获取请求体内容
     */
    private String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }


    /**
     * 获取 HttpServletRequest（兼容多种场景）
     */
    private HttpServletRequest getHttpServletRequest() {
        try {
            if (RequestContextHolder.getRequestAttributes() != null) {
                return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            }
        } catch (Exception e) {
            log.debug("从RequestContextHolder获取请求失败", e);
        }
        return this.request;
    }
}
