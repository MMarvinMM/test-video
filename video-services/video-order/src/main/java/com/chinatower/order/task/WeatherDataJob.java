package com.chinatower.order.task;

import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.order.service.IWeatherService;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName WeatherDataJob
 * @Deacription 天气定时任务
 * @Author chenmf
 * @Date 2022/3/31 15:10
 * @Version 1.0
 **/
@Component
@Slf4j
public class WeatherDataJob {

    @Autowired
    private IWeatherService weatherService;

    /**
     * 获取当前所有城市全量天气数据
     *
     * @param
     * @return
     */
    @XxlJob("weatherDataToMongodbHandler")
    public ReturnT<String> weatherByCity(String param) {
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("weatherDataToMongodbHandler开始执行,开始时间为:{}", startTime);
        weatherService.weatherByCity();
        weatherService.weatherDayByCity();
        long endTime = System.currentTimeMillis();
        //同步完成标识
        RedisUtil.set("weatherDataFlag", "true", RedisDBType.PRIMARY);
        RedisUtil.persist("weatherDataFlag", RedisDBType.PRIMARY);
        XxlJobLogger.log("weatherDataToMongodbHandler执行完成,耗时:{}", endTime - startTime);
        return new ReturnT<>("weatherDataToMongodbHandler执行结束");
    }


    /**
     * 获取当前所有城市全量天气质量数据
     *
     * @param
     * @return
     */
    @XxlJob("weatherAirDataToMongodbHandler")
    public ReturnT<String> weatherAirByCity(String param) {
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("weatherAirDataToMongodbHandler开始执行,开始时间为:{}", startTime);
        weatherService.weatherAirByCity();
        long endTime = System.currentTimeMillis();
        RedisUtil.set("weatherAirDataToMongodbTime", DateUtils.getTime(), RedisDBType.PRIMARY);
        XxlJobLogger.log("weatherAirDataToMongodbHandler执行完成,耗时:{}", endTime - startTime);
        return new ReturnT<>("weatherAirDataToMongodbHandler执行结束");
    }

    /**
     * 获取当前所有城市全量天气灾害预警数据
     *
     * @param
     * @return
     */
    @XxlJob("weatherWarningDataToMongodbHandler")
    public ReturnT<String> weatherWarningByCity(String param) {
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("weatherWarningDataToMongodbHandler,开始时间为:{}", startTime);
        weatherService.weatherWarningByCity();
        long endTime = System.currentTimeMillis();
        RedisUtil.set("weatherWarningDataToMongodbTime", DateUtils.getTime(), RedisDBType.PRIMARY);
        XxlJobLogger.log("weatherWarningDataToMongodbHandler,耗时:{}", endTime - startTime);
        return new ReturnT<>("weatherWarningDataToMongodbHandler");
    }


    /**
     * 获取当前所有城市全量天气质量数据(每小时)
     *
     * @param
     * @return
     */
    @XxlJob("weatherAirDataToMongodbEveryHourHandler")
    public ReturnT<String> weatherAirByCityEveryHour(String param) {
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("weatherAirDataToMongodbEveryHourHandler,开始时间为:{}", startTime);
        weatherService.weatherAirByCityEveryHour();
        long endTime = System.currentTimeMillis();
        RedisUtil.set("weatherAirDataToMongodbEveryHourTime", DateUtils.getTime(), RedisDBType.PRIMARY);
        XxlJobLogger.log("weatherAirDataToMongodbEveryHourHandler执行完成,耗时:{}", endTime - startTime);
        return new ReturnT<>("weatherAirDataToMongodbEveryHourHandler执行结束");
    }
}
