package com.chinatower.platform.modules.alarm.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.order.api.IWeatherApiService;
import com.chinatower.platform.entity.order.QueryWeatherAlarmDTO;
import com.chinatower.platform.entity.order.WeatherAlarmSubWeatherVO;
import com.chinatower.platform.entity.order.WeatherAlarmVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * 告警分级：按告警时间查询气象并写入 Kafka 载荷 / 扩展字段
 */
@Slf4j
public final class AiJudgeWeatherHelper {

    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_HUMIDITY = "humidity";
    public static final String KEY_WIND_SPEED = "windSpeed";
    public static final String KEY_WIND_DIR = "windDir";

    private AiJudgeWeatherHelper() {
    }

    /**
     * 查询气象并回填到 payload；成功时返回仅含气象四字段的扩展 JSON，失败返回 null
     */
    public static String fillWeatherAndBuildExtendJson(IWeatherApiService weatherApiService, JSONObject payload) {
        if (weatherApiService == null || payload == null) {
            return null;
        }
        String adCode = resolveAdCode(payload);
        String alarmTime = payload.getString("alarmTime");
        if (StringUtils.isBlank(adCode) || StringUtils.isBlank(alarmTime)) {
            log.info("告警分级查气象跳过：adCode或alarmTime为空, alarmId={}, adCode={}",
                    payload.getString("alarm_id"), adCode);
            return null;
        }
        try {
            QueryWeatherAlarmDTO weatherReq = new QueryWeatherAlarmDTO();
            weatherReq.setAdCode(adCode);
            weatherReq.setAlarmTime(alarmTime);
            log.info("告警分级查询气象入参:{}", JSON.toJSONString(weatherReq));
            Result<WeatherAlarmVO> weatherResult = weatherApiService.getWeatherInfoByAlarmTime(weatherReq);
            log.info("告警分级查询气象出参:{}", JSON.toJSONString(weatherResult));
            if (weatherResult == null || ResultCode.SUCCESS.getCode() != weatherResult.getCode()
                    || weatherResult.getData() == null || weatherResult.getData().getWeatherData() == null) {
                return null;
            }
            WeatherAlarmSubWeatherVO weatherData = weatherResult.getData().getWeatherData();
            String temperature = formatWeatherNumber(weatherData.getTemp());
            String humidity = formatWeatherNumber(weatherData.getHumidity());
            String windSpeed = formatWeatherNumber(weatherData.getWindSpeed());
            String windDir = weatherData.getWindDir();

            payload.put(KEY_TEMPERATURE, temperature);
            payload.put(KEY_HUMIDITY, humidity);
            payload.put(KEY_WIND_SPEED, windSpeed);
            payload.put(KEY_WIND_DIR, windDir);

            JSONObject extend = new JSONObject();
            extend.put(KEY_TEMPERATURE, temperature);
            extend.put(KEY_HUMIDITY, humidity);
            extend.put(KEY_WIND_SPEED, windSpeed);
            extend.put(KEY_WIND_DIR, windDir);
            return extend.toJSONString();
        } catch (Exception e) {
            log.error("告警分级查询气象异常, alarmId={}", payload.getString("alarm_id"), e);
            return null;
        }
    }

    /**
     * 区域编码优先区县，其次地市、省份
     */
    static String resolveAdCode(JSONObject payload) {
        if (payload == null) {
            return null;
        }
        String adCode = payload.getString("countyCode");
        if (StringUtils.isBlank(adCode)) {
            adCode = payload.getString("cityCode");
        }
        if (StringUtils.isBlank(adCode)) {
            adCode = payload.getString("provinceCode");
        }
        return adCode;
    }

    static String formatWeatherNumber(float value) {
        return new BigDecimal(String.valueOf(value)).stripTrailingZeros().toPlainString();
    }
}
