package com.chinatower.platform.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.data.sdk.entity.DebugClassMethodEntity;
import com.chinatower.platform.data.sdk.entity.OpenApiCodeEntity;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class JavaCodeExampleController {

    private static final String result = "\n" +
            "public class Test {\n" +
            "\n" +
            "    @Autowired \n" +
            "    private %s  %s \n" +
            "\n" +
//            "    %s \n" +
            "    public Result %s(%s) {\n" +
            "        return Result.data(%s.%s(%s));\n" +
            "    }\n" +
            "\n" +
            "}";

    private static final String IpHost = "https://slwyytest.tower0788.cn/api/";
    private static final String DebugUserKey = "debugUserBody";
    private static final String dependencyStart = "        " + "<dependency>\n";
    private static final String dependencyEnd = "        " + "</dependency>";
    private static final String version = "            " + "<version>2.6.0</version>\n";
    private static final String group = "            " + "<groupId>com.chinatower.platform</groupId>\n";
    private static final String analysis = "analysis";
    private static final String device = "device";
    private static final String event = "event";
    private static final String manage = "manage";
    private static final String message = "message";
    private static final String order = "order";
    private static final String outdata = "outdata";


    private static final Map<String, String> modelToPom = new HashMap<>();
    private static final Map<String, String> modelApi = new HashMap<>();
    static {
        modelToPom.put("gis", dependencyStart +
                group +
                "            <artifactId>video-api-gis</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put("gis", "gis");
        modelToPom.put(analysis, dependencyStart +
                group +
                "            <artifactId>video-api-analysis</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put(analysis, analysis);
        modelToPom.put("bdm", dependencyStart +
                group +
                "            <artifactId>video-api-bdm</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put("bdm", "bdm");
        modelToPom.put(device, dependencyStart +
                group +
                "            <artifactId>video-api-device</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put(device, device);
        modelToPom.put("industry", dependencyStart +
                group +
                "            <artifactId>video-api-device</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put("industry", device);
        modelToPom.put(event, dependencyStart +
                group +
                "            <artifactId>video-api-event</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put(event, event);
        modelToPom.put("file", dependencyStart +
                group +
                "            <artifactId>video-api-file</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put("file", "file");
        modelToPom.put("platform", dependencyStart +
                group +
                "            <artifactId>video-api-log</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put("platform", "log-service");
        modelToPom.put(manage, dependencyStart +
                group +
                "            <artifactId>video-api-manage</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put(manage, manage);
        modelToPom.put(message, dependencyStart +
                group +
                "            <artifactId>video-api-message</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put(message, message);
        modelToPom.put(order, dependencyStart +
                group +
                "            <artifactId>video-api-order</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put(order, order);
        modelToPom.put(outdata, dependencyStart +
                group +
                "            <artifactId>video-api-outdata</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put(outdata, outdata);
        modelToPom.put("system", dependencyStart +
                group +
                "            <artifactId>video-api-system</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put("system", "admin");
        modelToPom.put("video", dependencyStart +
                group +
                "      <artifactId>video-api-video</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put("video", "video-video");
        modelToPom.put("msg", dependencyStart +
                group +
                "            <artifactId>video-msg-api</artifactId>\n" +
                version +
                dependencyEnd);
        modelApi.put("msg", "msg");
    }

    @GetMapping("/java/code/getAll")
    public Result<String> getAll() throws IOException, ClassNotFoundException {
        Reflections reflections = new Reflections("com.chinatower.");  // 指定包名
        // 扫描指定包中的所有类
        Set<Class<?>> allClasses = reflections.getTypesAnnotatedWith(FeignClient.class);

        // 筛选实现了特定接口的类
        Set<Class<?>> interfaceImplementations = allClasses.stream()
                .filter(clazz -> clazz.isInterface() && clazz.getPackage().getName().contains("api"))
                .collect(Collectors.toSet());

        for (Class<?> clazz : interfaceImplementations) {
            log.info(clazz.getPackage().getName());
            log.info("Found interface implementation: " + clazz.getName());
        }
        return Result.success("");
    }

    @PostMapping("/java/code/getOne")
    public Result<OpenApiCodeEntity> getOne(@RequestBody DebugClassMethodEntity classMethod) {
        OpenApiCodeEntity openApiCodeEntity = new OpenApiCodeEntity();
        try {
            String [] strs = classMethod.getClassMethod().split("#");
            // 根据类名获取类对象
            Class<?> clazz = Class.forName(strs[0]);
            String [] packages = strs[0].split("\\.");
            String className = packages[packages.length - 1];
            String classFirstLower = Character.toLowerCase(className.charAt(0)) + className.substring(1);
            // 获取类的方法列表
            Method[] methods = clazz.getDeclaredMethods();
            // 方法入参
            StringBuffer params = new StringBuffer();
            // 调用入参
            StringBuffer paramsLower = new StringBuffer();
            // 遍历方法列表并打印方法名
            for (Method method : methods) {
                log.info("Method name: " + method.getName());
                if(method.getName().equals(strs[1])){
                    // 获取方法返回类型
                    Class<?> returnType = method.getReturnType();
                    log.info("returnType:{}", returnType.getName());
                    // 获取方法参数
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    // 获取方法注解
                    Annotation[] annotations = method.getAnnotations();

                    for (Class<?> paramType : parameterTypes) {
                        String singleParm = paramType.getSimpleName();
                        paramsLower.append(Character.toLowerCase(singleParm.charAt(0)) + singleParm.substring(1) + ",");
                        params.append(singleParm + "  " +  Character.toLowerCase(singleParm.charAt(0)) + singleParm.substring(1) + ",");
                    }
                    params.deleteCharAt(params.length() - 1);
                    paramsLower.deleteCharAt(paramsLower.length() - 1);
                    // 打印方法注解信息
                    String requestType = "";
                    for (Annotation annotation : annotations) {
                        log.info(" 注解 " + annotation.annotationType().getName());
                        if (annotation.annotationType().getName().contains("Mapping")) {
                            requestType = annotation.annotationType().getSimpleName();
                        }
                    }
                    // 替换占位符
                    String formattedMessage = String.format(result, className, classFirstLower, strs[1],
                            params, classFirstLower, strs[1], paramsLower);
                    log.info("formattedMessage:{}", formattedMessage);

                    String key = packages[2];
                    openApiCodeEntity.setInductPom(modelToPom.get(key));
                    openApiCodeEntity.setJavaCodeExample(formattedMessage);

                    realRequest(classMethod, openApiCodeEntity, method, key);
                }
            }
        } catch (Exception e) {
            log.error("JavaCodeExampleController getList e:", e);
            return Result.fail("请求失败");
        }
        return Result.data(openApiCodeEntity);
    }

    private void realRequest(DebugClassMethodEntity classMethod, OpenApiCodeEntity openApiCodeEntity, Method method, String key) {
        // 发起请求--各个场景 post  get  url中带参数

        // 调用登录获取token
        String loginUrl = IpHost + "auth/login";
        // 从redis配置获取
        String requestLoginBody = RedisUtil.get(DebugUserKey, RedisDBType.PRIMARY);
        if (StringUtils.isEmpty(requestLoginBody)) {
            requestLoginBody = "{\"username\":\"CH8myNoYB43rURegyAYectfVCM7B+xWE+jIBnKeJcsIeew4C4jMDEe3EKMuMeizuINIerx45YO9bmvX/P+vXLMiDmKRDOQ9W1fZTf0ciiw9I77oFVpFjMTBxopL/+8Kycx3QTLChhDyX+tH/0Rqznz7085eNOdHu8P5a03YbevI=\",\"password\":\"s/1FoSHZ2KSZEiLFuwjMe/9WFPwW+KCCCAuB19xMl3f7VTVNgE41IPDEQh7K8rvX3nhjtWYcFXoiU98TH3Zg80dq6XHmRQNYm0r74JLhAV47TxRU3/D7SfYHlGl96Z+LkxW26F/NROW90BIztYL4NQLqRGNrUqeTchoN5Fd02Ds=\",\"rememberMe\":false,\"uuid\":\"6b4331f8846d462facad83b7c6a20001\",\"code\":\"1234\",\"appId\":\"CTyuKiTs45tR869Y2h4PzaK7fpSemj9nNzDCHDno7pTSPpxVAQv7y/+CTcgc7NJ3gPGWfcijzglB2+35QYrfpA+bXnMkzWzUXNnx5GWkPzogbLF7/3yW0fy4eo23MsEZ606c+gS02gPK3mEBLsgc9Kfp6tnGaMlRUeoX3xh2ZL4=\",\"loginType\":\"1\",\"templateKey\":\"login\"}";
        }

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 封装请求体和请求头
        HttpEntity<String> requestLoginEntity = new HttpEntity<>(requestLoginBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Result> responseLoginEntity = restTemplate.exchange(loginUrl, HttpMethod.POST, requestLoginEntity, Result.class);
        String token = "";
        if (responseLoginEntity.getStatusCode().is2xxSuccessful()){
            log.info("登录成功！");
            // 获取响应体
            Result responseBody = responseLoginEntity.getBody();
            Map<String, String> data = (Map<String, String>) responseBody.getData();
            token = "Bearer " + data.get("access_token");
            log.info("Authorization:{}", token);
        }else{
            log.info("登录失败！{}",responseLoginEntity.getBody().toString());
        }

        headers.add("Authorization", token);
        String url = "";
        String preUrl = IpHost + modelApi.get(key);
        if (method.isAnnotationPresent(PostMapping.class)) {
            PostMapping postMapping = method.getAnnotation(PostMapping.class);
            // 获取 @PostMapping 注解上的 URL 地址
            String[] urls = postMapping.value();
            if (urls.length > 0) {
                url = urls[0];
            }
            // 封装请求体和请求头
            HttpEntity<String> requestEntity = new HttpEntity<>(classMethod.getParams(), headers);
            // 发送POST请求并获取响应
            ResponseEntity<Result> responseEntity = restTemplate.exchange(preUrl+url, HttpMethod.POST, requestEntity, Result.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                openApiCodeEntity.setResult(responseEntity.getBody());
            } else {
                log.info("请求失败状态:{}, 返回:{}", responseEntity.getStatusCode(), responseEntity.getBody());
            }

        } else if (method.isAnnotationPresent(GetMapping.class)) {
            GetMapping getMapping = method.getAnnotation(GetMapping.class);
            // 获取 @GetMapping 注解上的 URL 地址
            String[] urls = getMapping.value();
            if (urls.length > 0) {
                url = urls[0];
            }

            // 使用 Fastjson 将 JSON 字符串解析为 JSONObject
            JSONObject jsonObject = JSON.parseObject(classMethod.getParams());

            // 将 JSONObject 转换为 Map
            Map<String, Object> resultMap = jsonObject.getInnerMap();
            if(url.contains("{")){
                Pattern pattern = Pattern.compile("\\{([^}]+)}"); // 正则表达式匹配大括号内的内容

                Matcher matcher = pattern.matcher(url);
                // 使用Matcher来替换匹配到的大括号部分
                StringBuffer resultStr = new StringBuffer();
                while (matcher.find()) {
                    String match = matcher.group(1); // 获取匹配到的大括号内的内容
                    log.info("获取匹配到的大括号内的内容:{}", match);
                    String value = Objects.isNull(resultMap.get(match))?"":resultMap.get(match).toString();
                    // 根据匹配到的内容选择替换的参数值
                    matcher.appendReplacement(resultStr, value);
                }
                matcher.appendTail(resultStr);

                url = resultStr.toString();
            }

            // 使用 UriComponentsBuilder 来构建 URL，并添加参数
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(preUrl+url);
            // 输出 Map 内容
            for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                builder.queryParam(entry.getKey(), entry.getValue());
            }

            // 发送 GET 请求并获取响应
            ResponseEntity<Result> response = restTemplate.getForEntity(builder.toUriString(), Result.class);

            // 检查响应状态码
            if (response.getStatusCode().is2xxSuccessful()) {
                openApiCodeEntity.setResult(response.getBody());
            } else {
                log.info("请求失败状态:{}, 返回:{}", response.getStatusCode(), response.getBody());
            }

        }
    }

    @PostMapping("/java/code/postTest")
    public Result<OpenApiCodeEntity> postTest(@RequestBody DebugClassMethodEntity classMethod) {
        log.info("classMethod:{}", classMethod);
        return Result.data(null);
    }

    public static void main(String[] args)throws IOException, ClassNotFoundException {
        String classMethod = "com.chinatower.gis.api.GisLayerAPI#taoheAnalysisLayerTable";

        String [] strs = classMethod.split("#");
        // 根据类名获取类对象
        Class<?> clazz = Class.forName(strs[0]);
        String [] packages = strs[0].split("\\.");
        String className = packages[packages.length - 1];
        String classFirstLower = Character.toLowerCase(className.charAt(0)) + className.substring(1);
        // 获取类的方法列表
        Method[] methods = clazz.getDeclaredMethods();
        // 方法入参
        StringBuffer params = new StringBuffer();
        // 调用入参
        StringBuffer paramsLower = new StringBuffer();
        // 遍历方法列表并打印方法名
        for (Method method : methods) {
            log.info("Method name: " + method.getName());
            if(method.getName().equals(strs[1])){
                // 获取方法返回类型
                Class<?> returnType = method.getReturnType();
                log.info("returnType:{}", returnType.getName());
                // 获取方法参数
                Class<?>[] parameterTypes = method.getParameterTypes();
                // 获取方法注解
                Annotation[] annotations = method.getAnnotations();

                for (Class<?> paramType : parameterTypes) {
                    log.info(" 参数 " + paramType.getName());
                    String singleParm = paramType.getSimpleName();
                    paramsLower.append(Character.toLowerCase(singleParm.charAt(0)) + singleParm.substring(1) + ",");
                    params.append(singleParm + "  " +  Character.toLowerCase(singleParm.charAt(0)) + singleParm.substring(1) + ",");
                }
                params.deleteCharAt(params.length() - 1);
                paramsLower.deleteCharAt(paramsLower.length() - 1);

                // 打印方法注解信息
                String requestType = "";
                for (Annotation annotation : annotations) {
                    log.info(" 注解 " + annotation.annotationType().getName());
                    if (annotation.annotationType().getName().contains("Mapping")) {
                        requestType = annotation.annotationType().getSimpleName();
                    }
                }
                // 替换占位符
                String formattedMessage = String.format(result, className, classFirstLower, strs[1],
                        params, classFirstLower, strs[1], paramsLower);
                log.info("formattedMessage:{}", formattedMessage);
                OpenApiCodeEntity openApiCodeEntity = new OpenApiCodeEntity();
                String key = packages[2];
                openApiCodeEntity.setInductPom(modelToPom.get(key));
                openApiCodeEntity.setJavaCodeExample(formattedMessage);
//                resultList.add(openApiCodeEntity);

                // TODO start
                // 发起请求--各自场景 post  get  url中带参数

                // 调用登录获取token
                String loginUrl = "https://slwyytest.tower0788.cn/api/auth/login";
                String requestLoginBody = "{\"username\":\"CH8myNoYB43rURegyAYectfVCM7B+xWE+jIBnKeJcsIeew4C4jMDEe3EKMuMeizuINIerx45YO9bmvX/P+vXLMiDmKRDOQ9W1fZTf0ciiw9I77oFVpFjMTBxopL/+8Kycx3QTLChhDyX+tH/0Rqznz7085eNOdHu8P5a03YbevI=\",\"password\":\"s/1FoSHZ2KSZEiLFuwjMe/9WFPwW+KCCCAuB19xMl3f7VTVNgE41IPDEQh7K8rvX3nhjtWYcFXoiU98TH3Zg80dq6XHmRQNYm0r74JLhAV47TxRU3/D7SfYHlGl96Z+LkxW26F/NROW90BIztYL4NQLqRGNrUqeTchoN5Fd02Ds=\",\"rememberMe\":false,\"uuid\":\"6b4331f8846d462facad83b7c6a20001\",\"code\":\"1234\",\"appId\":\"CTyuKiTs45tR869Y2h4PzaK7fpSemj9nNzDCHDno7pTSPpxVAQv7y/+CTcgc7NJ3gPGWfcijzglB2+35QYrfpA+bXnMkzWzUXNnx5GWkPzogbLF7/3yW0fy4eo23MsEZ606c+gS02gPK3mEBLsgc9Kfp6tnGaMlRUeoX3xh2ZL4=\",\"loginType\":\"1\",\"templateKey\":\"login\"}";

                // 设置请求头
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // 封装请求体和请求头
                HttpEntity<String> requestLoginEntity = new HttpEntity<>(requestLoginBody, headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Result> responseLoginEntity = restTemplate.exchange(loginUrl, HttpMethod.POST, requestLoginEntity, Result.class);
                String token = "";
                if (responseLoginEntity.getStatusCode().is2xxSuccessful()){
                    log.info("登录成功！");
                    // 获取响应体
                    Result responseBody = responseLoginEntity.getBody();
                    Map<String, String> data = (Map<String, String>) responseBody.getData();
                    token = "Bearer " + data.get("access_token");
                    log.info("Authorization:{}", token);
                }else{
                    log.info("登录失败！{}",responseLoginEntity.getBody().toString());
                }

                headers.add("Authorization", token);
                String url = "";
                if (method.isAnnotationPresent(PostMapping.class)) {
                    PostMapping postMapping = method.getAnnotation(PostMapping.class);
                    // 获取 @PostMapping 注解上的 URL 地址
                    String[] urls = postMapping.value();
                    if (urls.length > 0) {
                        url = urls[0];
                    }
                // 封装请求体和请求头
//                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//                // 发送POST请求并获取响应
//                ResponseEntity<Result> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Result.class);
                } else if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
                    // 获取 @GetMapping 注解上的 URL 地址
                    String[] urls = getMapping.value();
                    if (urls.length > 0) {
                        url = urls[0];
                    }
                }

//
//                // 获取响应状态码
//                HttpStatus statusCode = responseEntity.getStatusCode();
//                // 获取响应体
//                Result responseBody = responseEntity.getBody();
//                restTemplate.execute();

                // TODO end
                break;
            }
        }
    }
}
