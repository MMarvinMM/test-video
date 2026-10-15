package com.chinatower.platform.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.dto.ColonyApp;
import com.chinatower.platform.dto.ColonyNode;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.util.CascadeSignatureUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.google.common.base.Strings;
import feign.*;
import feign.form.spring.SpringFormEncoder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.chinatower.common.core.constant.Constants.SYS_CONFIG_KEY;

/**
 * from-to工具类
 */
@Getter
@Component
@Slf4j
public class RemoteNodeUtils implements ApplicationContextAware {

    @Setter
    private static ApplicationContext context;

    private String remoteNodeCode;

    @Value("${local:10000000001}")
    private String localNodeCode;

    public final String GENERAL = "general";

    private final String CASCADE = "cascade";

    private static final String CASCADE_PATH = "/gw/proxy";

    private static final Integer ZERO = 0;

    private String urlPre = "";

    public RemoteNodeUtils() {
    }

    public RemoteNodeUtils(String remoteNodeCode) {
        this.remoteNodeCode = remoteNodeCode;
        SysConfig config = RedisUtil.get(SYS_CONFIG_KEY + Constants.PLATFORM_CODE, SysConfig.class, RedisDBType.PRIMARY);
        if (!ObjectUtils.isEmpty(config)) {
            this.localNodeCode = config.getConfigValue();
        } else {
            log.info("获取缓存失败:=" + SYS_CONFIG_KEY + Constants.PLATFORM_CODE);
        }

    }

    public static RemoteNodeUtils create(String remoteNodeCode) {
        return new RemoteNodeUtils(remoteNodeCode);
    }

    private String code2Host(String code, String appId) {
        String str = RedisUtil.get(Constants.COLONY_RELATION_ALL + code, RedisDBType.PRIMARY);
        if (StringUtils.isEmpty(str)) {
            throw new CustomException("平台缓存信息异常！");
        }
        ColonyNode cascadeConfigSyncVO = JSON.parseObject(str, ColonyNode.class);
        String url = null;
        if (appId.equals(RemoteUrlEenum.GENERAL.getCode())) {
            for (ColonyApp app : cascadeConfigSyncVO.getApps()) {
                if (GENERAL.equals(app.getAppId())) {
                    url = app.getAppAddr();
                    String extra = app.getExtra();
                    JSONObject jsonObject = JSON.parseObject(extra);
                    if (ObjectUtil.isNotNull(jsonObject)) {
                        String generalInternetUrl = (String) jsonObject.get("generalInternetUrl");
                        if (StringUtils.isNotEmpty(generalInternetUrl)) {
                            url = generalInternetUrl;
                        }
                    }
                }
            }
        } else {
            url = cascadeConfigSyncVO.getGwAddr();
        }
        log.info("获取的透传url:{}", url);
        //测试  url = "http://192.168.1.88:18080";
        return url;
    }

    public String remoteHost(String headerEnum) {
        return this.code2Host(headerEnum.equals(GENERAL) ? this.remoteNodeCode : this.localNodeCode, headerEnum);
    }

    private String remoteUri(Class glass, String headerEnum) {
        //从注解中读取微服务名称;
        boolean annotationPresent = glass.isAnnotationPresent(FeignClient.class);
        if (annotationPresent) {
            FeignClient annotation = (FeignClient) glass.getAnnotation(FeignClient.class);
            String microServiceName = annotation.value();
            if (headerEnum.equals(GENERAL)) {
                return this.remoteHost(headerEnum) + "/api/" + microServiceName;
            } else {
                urlPre = "/api/" + microServiceName;
                return this.remoteHost(headerEnum);
            }

        }
        return null;
    }

    public <T> T getFeign(Class<T> glass, String headerEnum) {
        String target = remoteUri(glass, headerEnum);
        return Feign.builder().contract(new SpringMvcContract())
                .encoder(new SpringEncoder(new SpringFormEncoder(), () -> context.getBean(HttpMessageConverters.class)))
                .encoder(new SpringEncoder(new SpringFormEncoder(),
                        () -> context.getBean(HttpMessageConverters.class)
                ))
                .mapAndDecode((response, type) -> {
                    try {
                        if (GENERAL.equals(headerEnum)) {
                            return response;
                        }
                        //统一去壳

                        String txt = Util.toString(response.body().asReader(Charset.defaultCharset()));
                        if (Strings.isNullOrEmpty(txt)) {
                            return response;
                        }

                        JSONObject resp = JSON.parseObject(txt);

                        String responseStr = null;


                        if (resp.getString("returnCode").equals("0000")) {
                            Object resultData = resp.get("data");
                            if (resultData != null) {
                                JSONArray colonyList = JSON.parseArray(String.valueOf(JSON.parseObject(String.valueOf(resultData)).get("colonyList")));
                                if (colonyList != null) {
                                    if(colonyList.size() == 1){
                                        responseStr = String.valueOf(JSON.parseObject(String.valueOf(colonyList.get(0))).get("result"));
                                    }else{
                                        Map<String, Object> res = new HashMap<>();
                                        res.put("code", 200);
                                        res.put("msg", "ok");
                                        res.put("data", resultData);
                                        responseStr = JSON.toJSONString(res);
                                    }
                                }
                            }
                        } else {
                            responseStr = Result.fail(resp.getString("returnMsg")).toString();
                        }
                        return response.toBuilder().
                                body(responseStr.getBytes())
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, new SpringDecoder(() -> context.getBean(HttpMessageConverters.class)))
                .requestInterceptor(template -> {
                    if (headerEnum.equals(CASCADE)) {
                        template.headers(RemoteNodeUtils.this.getCasCadeHeader(template));
                    }
                })
                .target(glass, target);
    }

    public <E extends Annotation> Map<String, Collection<String>> getCasCadeHeader(RequestTemplate requestTemplate) {

        Map<String, Collection<String>> headerMap = new HashMap<>();

        String httpMethod = requestTemplate.request().httpMethod().name();

        String str = RedisUtil.get(Constants.COLONY_RELATION_ALL + this.localNodeCode, RedisDBType.PRIMARY);
        if (StringUtils.isEmpty(str)) {
            throw new CustomException("平台缓存信息异常！");
        }
        ColonyNode cascadeConfigSyncVO = JSON.parseObject(str, ColonyNode.class);
        String requestId = DateUtils.parseDateToStr(DateUtils.YYYYMMDDHHMMSSSSSSSSSS, new Date());
        String uri = CASCADE_PATH;


        //-------------------------获取xSignature-------------------------
        LinkedHashMap<String, String> header = new LinkedHashMap<>();

        // 请求时间戳
        header.put("X-Cascade-RequestTime", String.valueOf(System.currentTimeMillis()));
        headerMap.put("X-Cascade-RequestTime", Collections.singleton(String.valueOf(System.currentTimeMillis())));

        // 请求唯一ID
        header.put("X-Cascade-RequestID", requestId);
        headerMap.put("X-Cascade-RequestID", Collections.singleton(requestId));

        // Appkey
        header.put("X-Cascade-AppKey", cascadeConfigSyncVO.getAppKey());
        headerMap.put("X-Cascade-AppKey", Collections.singleton(cascadeConfigSyncVO.getAppKey()));

        // 来源属地编码
        header.put("X-Cascade-From-Code", this.localNodeCode);
        headerMap.put("X-Cascade-From-Code", Collections.singleton(this.localNodeCode));

        // 目的地属地编码
        header.put("X-Cascade-To-Code", this.remoteNodeCode);
        headerMap.put("X-Cascade-To-Code", Collections.singleton(this.remoteNodeCode));

        // 属地应用
        header.put("X-Cascade-To-App", RemoteUrlEenum.GENERAL.getCode());
        headerMap.put("X-Cascade-To-App", Collections.singleton(RemoteUrlEenum.GENERAL.getCode()));

        // 属地请求接口Path
        header.put("X-Cascade-To-Path", this.urlPre + requestTemplate.url());
        headerMap.put("X-Cascade-To-Path", Collections.singleton(this.urlPre + requestTemplate.url()));

        // 请求类型
        header.put("X-Cascade-Method", httpMethod);
        headerMap.put("X-Cascade-Method", Collections.singleton(httpMethod));

        try {
            //级联网关提供的加签方法
            String xSignature = CascadeSignatureUtils.generateSignature(uri, header, cascadeConfigSyncVO.getSecretKey());
            headerMap.put("X-Cascade-Signature", Collections.singleton(xSignature));
        } catch (Exception e) {
            log.info("级联网关加密签名失败=", e);
        }
        log.info("========header=" + JSONObject.toJSONString(header));

        requestTemplate.uri(CASCADE_PATH);
        requestTemplate.method(Request.HttpMethod.POST);
        return headerMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (context == null) {
            setContext(applicationContext);
        }
    }

    @Getter
    public enum RemoteUrlEenum {
        CASCADE("cascade"),
        GENERAL("general");
        private final String code;

        RemoteUrlEenum(String code) {
            this.code = code;
        }
    }

}

