package com.chinatower.order.service;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.order.*;

import java.util.List;

/**
 * 功能描述
 * @author chenmf
 * @date 2022/1/6
 *
 * @return
 */
public interface IWeatherService {

    /**
     * 查询实时天气
     *
     * @return
     * @author chenmf
     * @date 2022/3/30
     * @version
     */
    JSONObject getWeatherNow(QueryWeatherDTO dto);

    /**
     * 查询连续无降水天数
     * @param dto 参数
     * @return 返回值
     */
    WeatherPrecipDTO getWeatherPrecip(QueryWeatherDTO dto);

    /**
     * 查询未来的天气
     *
     * @return
     * @author chenmf
     * @date 2022/3/30
     * @version
     */
    JSONObject getWeatherDay(QueryWeatherDTO dto);

    /**
     * 加载区域对照表缓存
     *
     * @return
     * @author chenmf
     * @date 2022/3/30
     * @version
     */
    void loadLocationRedis();

    /**
     * 获取当天所有城市全量天气数据
     *
     * @return
     * @author chenmf
     * @date 2022/3/31
     * @version
     */
    void weatherByCity();

    /**
     * 查询mongodb天气历史数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/3
     * @version
     */
    List<WeatherDailyDTO> weatherForHistory(QueryWeatherHsitoryVO weatherHsitoryVO);

    /**
     * 查询mongodb空气质量历史数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/3
     * @version
     */
    List<WeatherAirDailyDTO> weatherAirForHistory(QueryWeatherHsitoryVO weatherHsitoryVO);

    /**
     * 查询实时空气质量
     *
     * @return
     * @author chenmf
     * @date 2022/4/6
     * @version
     */
    JSONObject getAirNow(QueryWeatherDTO dto);

    /**
     * 查询天气灾害预警信息
     *
     */
    List<WeatherWarningVO> weatherForWarning(QueryWeatherDTO dto);

    /**
     * 查询天气灾害预警历史信息
     */
    List<List<WeatherWarningVO>> weatherForWarningHistory(QueryWeatherDTO dto);

    /**
     * 获取当天所有城市全量空气质量数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/11
     * @version
     */
    void weatherAirByCity();

    /**
     * 获取当天所有城市全量空气灾害预警数据
     *
     * @return
     */
    void weatherWarningByCity();

    /**
     * 获取未来一天所有城市全量天气数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/21
     * @version
     */
    void weatherDayByCity();

    /**
     * 加载区域数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/20
     * @version
     */
    void loadAreaRedis(String areaLevel);

    /**
     * 查询mongodb未来天气数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/11
     * @version
     */
    List<WeatherFutureDailyDTO> weatherForFuture(QueryWeatherHsitoryVO weatherHsitoryVO);

    /**
     * 查询空气质量预报
     *
     * @return
     * @author chenmf
     * @date 2022/4/25
     * @version
     */
    JSONObject getAirDay(QueryWeatherDTO dto);

    /**
     * 根据经纬度查询天气
     *
     * @return
     * @author luo.zhenghong
     * @date 2022/5/26
     * @version
     */
    String getWeatherNowByPoint(QueryWeatherDTO dto);

    /**
     * 获取当天所有城市全量空气质量数据(每小时)
     *
     * @return
     * @author dencycheng
     * @date 2022/11/9
     * @version
     */
    void weatherAirByCityEveryHour();

    /**
     *
     * @param req
     * @return 查案租户区域空气质量排行
     */
    List<TenantWeatherRankRes> tenantWeatherRank(TenantWeatherRankReq req) ;

    /**
     * 获取租户的权限区域范围
     * @return
     */
    List<SysArea> getTenantSysArea();

    /**
     * 根据告警时间查询天气信息
     * @param dto 查询参数
     * @return 天气信息
     */
    WeatherAlarmVO getWeatherInfoByAlarmTime(QueryWeatherAlarmDTO dto);

    /**
     * 根据时间查询天气信息
     * @param dto 查询参数
     * @return 天气信息
     */
    WeatherTimeVO getWeatherInfoByTime(QueryWeatherTimeDTO dto);

    /**
     * 查询天气统计信息
     * @param dto 查询参数
     * @return 天气统计信息
     */
    List<WeatherStatisticsVO> queryWeatherInfoStatistics(QueryWeatherStatisticsDTO dto);
}
