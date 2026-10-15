package com.chinatower.order.api.factory;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.order.api.IWeatherApiService;
import com.chinatower.platform.entity.order.*;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 天气服务降级处理
 *
 * @author chenmf
 */
@Component
public class WeatherApiFallbackFactory implements FallbackFactory<IWeatherApiService>
{

    private static final Logger log = LoggerFactory.getLogger(WeatherApiFallbackFactory.class);

    @Override
    public IWeatherApiService create(Throwable throwable) {
        log.error("天气服务调用失败:{}", throwable.getMessage());
        return new IWeatherApiService()
        {
            @Override
            public Result<JSONObject> getWeatherNow(QueryWeatherDTO dto) {
                return Result.fail("查询实时天气数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<WeatherPrecipDTO> getWeatherPrecip(QueryWeatherDTO dto) {
                return Result.fail("查询连续无降水天数数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<JSONObject> getWeatherDay(QueryWeatherDTO dto) {
                return Result.fail("查询未来的天气数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherDailyDTO>> weatherForhistory(QueryWeatherHsitoryVO weatherHsitoryVO) {
                return Result.fail("查询mongodb天气历史数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherDailyV2DTO>> weatherForHistoryV2(QueryWeatherHsitoryVO param) {
                return Result.fail("查询mongodb天气历史数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<JSONObject> getAirNow(QueryWeatherDTO dto) {
                return Result.fail("查询实时空气质量数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<WeatherAlarmVO> getWeatherInfoByAlarmTime(QueryWeatherAlarmDTO dto) {
                return Result.fail("根据告警时间查询天气数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<WeatherTimeVO> getWeatherInfoByTime(QueryWeatherTimeDTO dto) {
                return Result.fail("根据时间查询天气信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherStatisticsVO>> queryWeatherInfoStatistics(QueryWeatherStatisticsDTO dto) {
                return Result.fail("查询天气统计信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherWarningVO>> weatherForWarning(QueryWeatherDTO dto) {
                return Result.fail("查询天气灾害预警信息失败:" + throwable.getMessage());
            }

            @Override
            public Result<JSONObject> getAirDay(QueryWeatherDTO dto) {
                return Result.fail("查询空气质量预报数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherAirDailyDTO>> weatherAirForhistory(QueryWeatherHsitoryVO weatherHsitoryVO) {
                return Result.fail("查询mongodb空气质量历史数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherAirDailyV2DTO>> weatherAirForHistoryV2(QueryWeatherHsitoryVO param) {
                return Result.fail("查询mongodb空气质量历史数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherAirDailyV2DTO>> weatherAirForHistoryByDate(QueryWeatherByDateVO param) {
                return Result.fail("查询mongodb历史空气质量数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherFutureDailyDTO>> weatherForFuture(QueryWeatherHsitoryVO weatherHsitoryVO) {
                return Result.fail("查询mongodb未来天气数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherFutureDailyV2DTO>> weatherForFutureV2(QueryWeatherHsitoryVO param) {
                return Result.fail("查询mongodb未来天气v2数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherFutureDailyDTO>> weatherForFutureByDate(QueryWeatherByDateVO weatherHsitoryVO) {
                return Result.fail("按日期查询mongodb未来天气数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherFutureDailyV2DTO>> weatherForFutureByDateV2(QueryWeatherByDateVO param) {
                return Result.fail("按日期查询mongodb未来天气v2数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherDailyDTO>> weatherForhistoryByDate(QueryWeatherByDateVO weatherHsitoryVO) {
                return Result.fail("查询mongodb历史天气数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<WeatherDailyV2DTO>> weatherForHistoryByDateV2(QueryWeatherByDateVO param) {
                return Result.fail("查询mongodb历史天气数据失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> getWeatherNowByPoint(QueryWeatherDTO dto) {
                return Result.fail("根据经纬度查询和风天气失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> getCache(String cacheKey) {
                return Result.fail("根据缓存key查询缓存失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> setCache(RedisCacheVO redisCacheVO) {
                return Result.fail("设置缓存失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<TenantWeatherRankRes>> tenantWeatherRank(TenantWeatherRankReq req) {
                return Result.fail("调用获取租户天气质量排行失败:" + throwable.getMessage());
            }
        };
    }
}
