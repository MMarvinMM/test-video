package com.chinatower.platform.entity.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class WeatherAlarmVO implements Serializable {

    /**
     * 空气质量信息
     */
    private WeatherAlarmSubAirVO airData;

    /**
     * 天气信息
     */
    private WeatherAlarmSubWeatherVO weatherData;
}
