package com.chinatower.outdata.api.trafficEvent;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.HttpStatus;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.outdata.api.trafficEvent.enums.TrafficEventEnum;

import com.chinatower.platform.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Slf4j
@Component
public class TrafficEventUtil {

    @Value("${traffic.baidu.ak:1BWVhFt75WnfUoMSkHAgMF6EgrNlTlcs}")
    private String TRAFFIC_EVENT_AK;

    public String restGet(Object inParam, TrafficEventEnum.InterfaceConfig inf) {
        return restGet(inParam, inf, TRAFFIC_EVENT_AK);
    }

    public String restGet(Object inParam, TrafficEventEnum.InterfaceConfig inf, String trafficEventAk) {
        String url = buildGetUrl(inf.getInfUrl(), inParam, StringUtils.isNotEmpty(trafficEventAk) ? trafficEventAk : TRAFFIC_EVENT_AK);
        log.info("请求交通百度请求uil:{}", url);
        HttpResponse httpResponse = HttpRequest.get(url).timeout(30000).execute();
        String responseBody = httpResponse.body();
        log.info("请求交通百度请求返回:{}", responseBody);
        JSONObject json = JSONObject.parseObject(responseBody);
        String status = json.getString(TrafficEventEnum.TRAFFIC_BAIDU_STATUS);
        if (!StringUtils.equals(TrafficEventEnum.TRAFFIC_BAIDU_SUCCESS_STATUS, status)) {
            String message = json.getString(TrafficEventEnum.TRAFFIC_BAIDU_MESSAGE);
            throw new CustomException(message, HttpStatus.ERROR_4001);
        }
        return json.getString(TrafficEventEnum.TRAFFIC_BAIDU_RESULT);
    }

    public String buildGetUrl(String baseUrl, Object obj) {
        return buildGetUrl(baseUrl, obj, TRAFFIC_EVENT_AK);
    }

    public String buildGetUrl(String baseUrl, Object obj, String trafficEventAk) {
        try {
            Class<?> objClass = obj.getClass();
            Field[] fields = objClass.getDeclaredFields();
            StringBuilder queryString = new StringBuilder();
            boolean isFirstParam = true;

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value != null) {
                    String encodedKey = URLEncoder.encode(field.getName(), String.valueOf(StandardCharsets.UTF_8));
                    String encodedValue = URLEncoder.encode(value.toString(), String.valueOf(StandardCharsets.UTF_8));

                    if (!isFirstParam) {
                        queryString.append("&");
                    } else {
                        queryString.append("?");
                        isFirstParam = false;
                    }

                    queryString.append(encodedKey).append("=").append(encodedValue);
                }
            }
            return baseUrl + queryString.append("&ak=").append(trafficEventAk);
        }
        catch (Exception e) {
            log.info("参数反射异常：", e);
            throw new CustomException("参数勾建异常");
        }
    }
}
