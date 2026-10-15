package com.chinatower.order.util;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.order.QueryWeatherDTO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName WeatherApiUtil
 * @Deacription 天气调用类
 * @Author chenmf
 * @Date 2022/3/30 10:21
 * @Version 1.0
 **/
@Slf4j
@Component
public class WeatherApiUtil {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${component.weather.api.url}")
    private String componentApiUrl;

    public static final String ADCODE_INFO_KEY = "adcodeInfo:";
    public static final String WEATHER_INFO_KEY = "weather:";

    public static final String WEATHER_COMPONENT_INFO_KEY = "weatherComponent:";

    private static final String IN_CACHE_MSG = "在缓存中存在!";

    private static final String AREA_CODE_NOT_EXIST_MSG = "不存在对应的区域编码";

    private static final String AD_CODE_STR = "adcode:";

    /**
     * 调用天气接口公共方法
     *
     * @return
     * @author chenmf
     * @date 2022/3/24
     * @version
     */
    public JSONObject httpWeatherApi(QueryWeatherDTO dto) {
        return httpWeatherApi(dto, (long)120+NonceUtil.buildRandom(1));
    }

    public JSONObject httpWeatherApiFromComponent(QueryWeatherDTO dto) {
        String adcode = dto.getAdcode();
        String weatherComponentKey = WeatherApiUtil.WEATHER_COMPONENT_INFO_KEY + dto.getServiceName() + "_" +adcode;
        JSONObject redisJson = RedisUtil.get(weatherComponentKey, JSONObject.class, RedisDBType.PRIMARY);

        if (null != redisJson) {
            log.info("key:" + weatherComponentKey + IN_CACHE_MSG);
            return redisJson;
        }
        //查询区域对照缓存
        String cacheKey = WeatherApiUtil.ADCODE_INFO_KEY + dto.getAdcode();
        String locationId = RedisUtil.get(cacheKey, String.class, RedisDBType.PRIMARY);

        if (StringUtils.isEmpty(locationId)) {
            log.error(AD_CODE_STR + adcode + AREA_CODE_NOT_EXIST_MSG);
            locationId = adcode;
        } else {
            RedisUtil.expire(cacheKey, 1, TimeUnit.DAYS, RedisDBType.PRIMARY);
        }

        StringBuilder url = new StringBuilder();
        url.append(componentApiUrl).append(dto.getServiceName()).append(Constants.LOCATION_PARAM).append(locationId);
//        url.append(Constants.KEY_PARAM).append(apiKey);
//        appendTenantIdToUrl(url);
        log.info("连续无降水天数查询url:"+url);
        HttpResponse httpResponse = HttpRequest.get(url.toString()).timeout(30000).execute();
        log.info("{} httpResponse code {}", url, httpResponse.getStatus());
        //返回数据解析
        if(httpResponse.isOk()){
            JSONObject json = JSONObject.parseObject(httpResponse.body());
            log.info("{} httpResponse content {}", url, JSON.toJSONString(json));
            // 兼容属地化格式，不报空指针
            if (json == null) {
                return null;
            }
            JSONObject jsonObject = json.getJSONObject(Constants.DATA);
            if (null != jsonObject) {
                RedisUtil.set(weatherComponentKey, jsonObject, RedisDBType.PRIMARY);
                //失效时间增加到一小时加十分钟以内的随机数
                RedisUtil.expire(weatherComponentKey, (long)60+NonceUtil.buildRandom(1), TimeUnit.MINUTES, RedisDBType.PRIMARY);
                return jsonObject;
            }
        }
        log.info("httpWeatherApiFromComponent未查询到adcode为 {} 的地区天气数据, 返回数据:{}",adcode, JSON.toJSONString(httpResponse));
        return null;
    }

    public JSONObject httpWeatherApi(QueryWeatherDTO dto, long expireTime) {

        String adcode = dto.getAdcode();
        String weatherKey = WeatherApiUtil.WEATHER_INFO_KEY + dto.getServiceName() + "_" +adcode;
        JSONObject redisJson = RedisUtil.get(weatherKey, JSONObject.class, RedisDBType.PRIMARY);

        if (null != redisJson) {
            log.info("key:" + weatherKey + IN_CACHE_MSG);
            return redisJson;
        }
        //查询区域对照缓存
        String cacheKey = WeatherApiUtil.ADCODE_INFO_KEY + dto.getAdcode();
        String locationId = RedisUtil.get(cacheKey, String.class, RedisDBType.PRIMARY);

        if (StringUtils.isEmpty(locationId)) {
            log.error(AD_CODE_STR + adcode + AREA_CODE_NOT_EXIST_MSG);
            locationId = adcode;
        }
        else
        {
            RedisUtil.expire(cacheKey, 1, TimeUnit.DAYS, RedisDBType.PRIMARY);
        }

        StringBuilder url = new StringBuilder();
        url.append(apiUrl).append(dto.getServiceName()).append(Constants.LOCATION_PARAM).append(locationId);
        url.append(Constants.KEY_PARAM).append(apiKey);
        // 不是定时任务才查询客户信息，否则会报错
        if (BooleanUtils.isNotTrue(dto.getTaskFlag())) {
            appendTenantIdToUrl(url);
        }
        log.info("天气查询url:"+url);
        HttpResponse httpResponse = HttpRequest.get(url.toString()).timeout(30000).execute();
        log.info("{} response code {}", url, httpResponse.getStatus());
        //返回数据解析
        if(httpResponse.isOk()){
            JSONObject json = JSONObject.parseObject(httpResponse.body());
            log.info("{} respose content {}", url, JSON.toJSONString(json));
            // 兼容属地化格式，不报空指针
            if (json == null) {
                return null;
            }
            JSONArray dailyJson = json.getJSONArray(Constants.DAILY);
            JSONObject nowJson = json.getJSONObject("now");
            if (null != dailyJson) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.DAILY, dailyJson);
                RedisUtil.set(weatherKey, jsonObject, RedisDBType.PRIMARY);
                //失效时间增加到两小时加十分钟以内的随机数
                RedisUtil.expire(weatherKey, expireTime, TimeUnit.MINUTES, RedisDBType.PRIMARY);
                return jsonObject;
            }
            else if (null != nowJson) {
                RedisUtil.set(weatherKey, nowJson, RedisDBType.PRIMARY);
                RedisUtil.expire(weatherKey, expireTime, TimeUnit.MINUTES, RedisDBType.PRIMARY);
                return nowJson;
            }
        }
        log.info("message{}", "未查询到adcode为" + adcode + "的地区天气数据"+", 返回数据:"+ JSON.toJSONString(httpResponse));
        return null;
    }

    private static void appendTenantIdToUrl(StringBuilder url) {
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (loginUser == null || loginUser.getUser() == null) {
                return;
            }
            String tenantId = loginUser.getUser().getTenantId();
            if (org.apache.commons.lang3.StringUtils.isEmpty(tenantId)) {
                return;
            }
            String toAppended
                    = URLUtil.buildQuery(ImmutableMap.of("tenantId", tenantId), StandardCharsets.UTF_8);
            url.append("&").append(toAppended);
        } catch (Throwable e) {
            log.error("append tenantId error:", e);
        }
    }

    /**
     * 调用天气接口公共方法(缓存1小时)
     *
     * @return
     * @author dencycheng
     * @date 2022/11/9
     * @version
     */
    public JSONObject httpWeatherApiCacheOneHour(QueryWeatherDTO dto) {

        String adcode = dto.getAdcode();
        String weatherKey = WeatherApiUtil.WEATHER_INFO_KEY + dto.getServiceName() + "_" +adcode;
        JSONObject redisJson = RedisUtil.get(weatherKey, JSONObject.class, RedisDBType.PRIMARY);

        if (null != redisJson) {
            log.info("key:" + weatherKey + IN_CACHE_MSG);
            return redisJson;
        }
        //查询区域对照缓存
        String cacheKey = WeatherApiUtil.ADCODE_INFO_KEY + dto.getAdcode();
        String locationId = RedisUtil.get(cacheKey, String.class, RedisDBType.PRIMARY);

        if (StringUtils.isEmpty(locationId)) {
            log.error(AD_CODE_STR + adcode + AREA_CODE_NOT_EXIST_MSG);
            locationId = adcode;
        }
        else
        {
            RedisUtil.expire(cacheKey, 1, TimeUnit.DAYS, RedisDBType.PRIMARY);
        }

        StringBuilder url = new StringBuilder();
        url.append(apiUrl).append(dto.getServiceName()).append(Constants.LOCATION_PARAM).append(locationId);
        url.append(Constants.KEY_PARAM).append(apiKey);
        log.info("天气查询url:"+url);
        HttpResponse httpResponse = HttpRequest.get(url.toString()).timeout(30000).execute();
        //返回数据解析
        if(httpResponse.isOk()){
            JSONObject json = JSONObject.parseObject(httpResponse.body());
            JSONArray dailyJson = json.getJSONArray(Constants.DAILY);
            JSONObject nowJson = json.getJSONObject("now");
            if (null != dailyJson) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.DAILY, dailyJson);
                RedisUtil.set(weatherKey, jsonObject, RedisDBType.PRIMARY);
                RedisUtil.expire(weatherKey, 30, TimeUnit.MINUTES, RedisDBType.PRIMARY);
                return jsonObject;
            }
            else if (null != nowJson) {
                RedisUtil.set(weatherKey, nowJson, RedisDBType.PRIMARY);
                RedisUtil.expire(weatherKey, 30, TimeUnit.MINUTES, RedisDBType.PRIMARY);
                return nowJson;
            }
        }
        log.info("message{}", "未查询到adcode为" + adcode + "的地区天气数据");
        return null;
    }

    /**
     * 调用和风天气API接口获取指定经纬度地点的实时天气
     * @param dto
     */
    public String getWeatherFromAPI(QueryWeatherDTO dto) {
        // 调用和风天气API获取天气信息
        String icon = "";
        try {
            StringBuilder url = new StringBuilder();
            url.append(apiUrl);
            url.append(dto.getServiceName());
            url.append(Constants.LOCATION_PARAM);
            url.append(dto.getLongitude());
            url.append(",");
            url.append(dto.getLatitude());
            url.append(Constants.KEY_PARAM);
            url.append(apiKey);
            log.info("getWeatherFromAPI 请求：{}", url);
            HttpResponse httpResponse = HttpRequest.get(url.toString()).timeout(30000).execute();
            log.info("getWeatherFromAPI 返回：{}", httpResponse);
            //返回数据解析
            if(httpResponse.isOk()) {
                JSONObject json = JSONObject.parseObject(httpResponse.body());
                JSONObject now = json.getJSONObject("now");
                if (now != null) {
                    icon = now.get("icon").toString();
                }
            }
        }
        catch (Exception e) {
            log.error("和风天气API获取天气信息失败:", e);
        }
        return icon;
    }

    /**
     * 调用和风天气--天气灾害预警api接口
     * @param dto
     * @return
     */
    public JSONObject httpWeatherWarningApi(QueryWeatherDTO dto) {

        String adCode = dto.getAdcode();
//        String weatherKey = WeatherApiUtil.WEATHER_INFO_KEY + dto.getServiceName() + "_" + adCode;
//        JSONObject redisJson = RedisUtil.get(weatherKey, JSONObject.class, RedisDBType.PRIMARY);

//        if (null != redisJson) {
//            log.info("key:" + weatherKey + IN_CACHE_MSG);
//            return redisJson;
//        }
        //查询区域对照缓存
        String cacheKey = WeatherApiUtil.ADCODE_INFO_KEY + dto.getAdcode();
        String locationId = RedisUtil.get(cacheKey, String.class, RedisDBType.PRIMARY);

        if (StringUtils.isEmpty(locationId)) {
            log.error(AD_CODE_STR + adCode + AREA_CODE_NOT_EXIST_MSG);
            locationId = adCode;
        } else {
            RedisUtil.expire(cacheKey, 1, TimeUnit.DAYS, RedisDBType.PRIMARY);
        }

        StringBuilder url = new StringBuilder();
        url.append(apiUrl).append(dto.getServiceName()).append(Constants.LOCATION_PARAM).append(locationId);
        // 拼接非必填参数 开始时间、结束时间，用于历史预警信息查询
        if (StringUtils.isNotEmpty(dto.getStartDate())) {
            url.append(Constants.BEGIN_DATE_PARAM).append(dto.getStartDate());
        }
        if (StringUtils.isNotEmpty(dto.getEndDate())) {
            url.append(Constants.END_DATE_PARAM).append(dto.getEndDate());
        }
        url.append(Constants.KEY_PARAM).append(apiKey);
        // 不是定时任务才查询客户信息，否则会报错
        if (BooleanUtils.isNotTrue(dto.getTaskFlag())) {
            appendTenantIdToUrl(url);
        }
        log.info("天气灾害预警查询url:"+url);
        HttpResponse httpResponse = HttpRequest.get(url.toString()).timeout(30000).execute();
        log.info("{} response code {}", url, httpResponse.getStatus());
        //返回数据解析
        if(httpResponse.isOk()){
            JSONObject json = JSONObject.parseObject(httpResponse.body());
            log.info("{} respose content {}", url, JSON.toJSONString(json));
            // 兼容属地化格式，不报空指针
            if (json == null) {
                return null;
            }
            JSONArray warningJson = json.getJSONArray(Constants.WARNING);
            if (null != warningJson) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.WARNING, warningJson);
//                RedisUtil.set(weatherKey, jsonObject, RedisDBType.PRIMARY);
//                //失效时间增加到两小时加十分钟以内的随机数
//                RedisUtil.expire(weatherKey, expireTime, TimeUnit.MINUTES, RedisDBType.PRIMARY);
                return jsonObject;
            }
        }
        log.info("httpWeatherWarningApi message{}", "未查询到adCode为" + adCode + "的地区天气灾害预警"+", 返回数据:"+ JSON.toJSONString(httpResponse));
        return null;
    }

    public List<JSONObject> httpWeatherWarningHistoryApi(QueryWeatherDTO dto) {

        String adCode = dto.getAdcode();
        //查询区域对照缓存
        String cacheKey = WeatherApiUtil.ADCODE_INFO_KEY + dto.getAdcode();
        String locationId = RedisUtil.get(cacheKey, String.class, RedisDBType.PRIMARY);

        if (StringUtils.isEmpty(locationId)) {
            log.error(AD_CODE_STR + adCode + AREA_CODE_NOT_EXIST_MSG);
            locationId = adCode;
        } else {
            RedisUtil.expire(cacheKey, 1, TimeUnit.DAYS, RedisDBType.PRIMARY);
        }

        StringBuilder url = new StringBuilder();
        url.append(apiUrl).append(dto.getServiceName()).append(Constants.LOCATION_PARAM).append(locationId);
        // 拼接非必填参数 开始时间、结束时间，用于历史预警信息查询
        if (StringUtils.isNotEmpty(dto.getStartDate())) {
            url.append(Constants.BEGIN_DATE_PARAM).append(dto.getStartDate());
        }
        if (StringUtils.isNotEmpty(dto.getEndDate())) {
            url.append(Constants.END_DATE_PARAM).append(dto.getEndDate());
        }
        url.append(Constants.KEY_PARAM).append(apiKey);
        appendTenantIdToUrl(url);
        log.info("天气灾害预警查询url:"+url);
        HttpResponse httpResponse = HttpRequest.get(url.toString()).timeout(30000).execute();
        log.info("url:{} response code:{}", url, httpResponse.getStatus());
        //返回数据解析
        if(httpResponse.isOk()){
            JSONArray jsonArray = JSONObject.parseArray(httpResponse.body());
            log.info("url:{} response content:{}", url, JSON.toJSONString(jsonArray));
            // 兼容属地化格式，不报空指针
            if (jsonArray == null) {
                return null;
            }
            List<JSONObject> resultList = Lists.newArrayList();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                JSONArray warningJson = json.getJSONArray(Constants.WARNING);
                if (null != warningJson) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Constants.WARNING, warningJson);
                    resultList.add(jsonObject);
                }
            }
            if (CollectionUtils.isNotEmpty(resultList)) {
                return resultList;
            }
        }
        log.info("httpWeatherWarningHistoryApi message,未查询到adCode为{}的地区天气灾害预警, 返回数据:{}", adCode, JSON.toJSONString(httpResponse));
        return Lists.newArrayList();
    }
}
