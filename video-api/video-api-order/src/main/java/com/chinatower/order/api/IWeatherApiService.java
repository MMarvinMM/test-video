package com.chinatower.order.api;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.entity.Result;
import com.chinatower.order.api.factory.WeatherApiFallbackFactory;
import com.chinatower.platform.entity.order.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "weatherApiService", value = ServiceNameConstants.ORDER_SERVICE, fallbackFactory = WeatherApiFallbackFactory.class)
public interface  IWeatherApiService {

    /**
     * 根据地市编码查询实时天气
     * @apiNote 根据地市编码查询实时天气
     * @apiVersion 11.1
     * @tag 十一、数据能力
     * @requestDemo {
     *     "adcode": "11000"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *      vis: 能见度，默认单位：公里
     *      temp: 温度，默认单位：摄氏度
     *      obsTime: 数据观测时间
     *      icon: 天气状况的图标代码
     *      wind360: 风向360角度
     *      windDir: 风向
     *      pressure: 大气压强，默认单位：百帕
     *      feelsLike: 体感温度，默认单位：摄氏度
     *      cloud: 云量，百分比数值。可能为空
     *      precip: 过去1小时降水量，默认单位：毫米
     *      dew: 露点温度。可能为空
     *      humidity: 相对湿度，百分比数值
     *      text: 天气状况的文字描述，包括阴晴雨雪等天气状态的描述
     *      windSpeed: 风速，公里/小时
     *      windScale: 风力等级
     * 返回参数示例：
     *  {
     *     "code":"200",
     *     "msg":"请求成功",
     *     "time":1648625451777,
     *     "data":{
     *         "vis":"29",
     *         "temp":"9",
     *         "obsTime":"2022-03-30T15:13+08:00",
     *         "icon":"101",
     *         "wind360":"45",
     *         "windDir":"东北风",
     *         "pressure":"1020",
     *         "feelsLike":"5",
     *         "cloud":"43",
     *         "precip":"0.0",
     *         "dew":"-2",
     *         "humidity":"64",
     *         "text":"多云",
     *         "windSpeed":"15",
     *         "windScale":"3"
     *     }
     * }
     */
    @PostMapping("/weather/now")
    Result<JSONObject> getWeatherNow(@Validated @RequestBody QueryWeatherDTO dto);

    /**
     * 根据地市编码查询连续无降水天数
     * @apiNote 根据地市编码查询连续无降水天数
     * @apiVersion 11.3
     * @tag 十一、数据能力
     * @requestDemo {
     *     "adcode": "11000"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *      precip: 过去1小时降水量，默认单位：毫米
     * 返回参数示例：
     *  {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1720000610068,
     *     "data": {
     *         "precip": "5"
     *     }
     * }
     */
    @PostMapping("/weather/getWeatherPrecip")
    Result<WeatherPrecipDTO> getWeatherPrecip(@Validated @RequestBody QueryWeatherDTO dto);

    /**
     * 根据地市编码和未来天数查询未来的天气
     * @apiNote 根据地市编码和未来天数查询未来的天气
     * @apiVersion 11.2
     * @tag 十一、数据能力
     * @requestDemo {
     *     "adcode":"110000",
     *     "day":7
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *      moonset: 当天月落时间，可能为空
     *      windSpeedDay: 预报白天风速，公里/小时
     *      sunrise: 日出时间，在高纬度地区可能为空
     *      moonPhaseIcon: 月相图标
     *      windScaleDay: 预报白天风力等级
     *      windScaleNight: 预报夜间风力等级
     *      wind360Day: 预报白天风向360角度
     *      iconDay:  预报白天天气状况的图标
     *      wind360Night: 预报夜间风向360角度
     *      tempMax:预报当天最高温度
     *      cloud: 云量，百分比数值。可能为空
     *      textDay: 预报白天天气状况文字描述，包括阴晴雨雪等天气状态的描述
     *      precip: 预报当天总降水量，默认单位：毫米
     *      textNight: 预报晚间天气状况文字描述，包括阴晴雨雪等天气状态的描述
     *      humidity: 相对湿度，百分比数值
     *      moonPhase: 月相名称
     *      windDirDay: 预报白天风向
     *      windDirNight: 预报夜间当天风向
     *      vis: 能见度，默认单位：公里
     *      fxDate: 预报日期
     *      moonrise: 当天月升时间，可能为空
     *      pressure: 大气压强，默认单位：百帕
     *      iconNight: 预报夜间天气状况的图标
     *      sunset: 日落时间，在高纬度地区可能为空
     *      windSpeedNight: 预报夜间风速，公里/小时
     *      uvIndex: 紫外线强度指数
     *      tempMin: 预报当天最低温度
     * 返回参数示例：
     * {
     *     "code":200,
     *     "msg":"请求成功",
     *     "time":1648625457865,
     *     "data":{
     *         "daily":[
     *             {
     *                 "moonset":"16:25",
     *                 "windSpeedDay":"3",
     *                 "sunrise":"06:03",
     *                 "moonPhaseIcon":"807",
     *                 "windScaleDay":"1-2",
     *                 "windScaleNight":"1-2",
     *                 "wind360Day":"180",
     *                 "iconDay":"305",
     *                 "wind360Night":"270",
     *                 "tempMax":"12",
     *                 "cloud":"55",
     *                 "textDay":"小雨",
     *                 "precip":"1.0",
     *                 "textNight":"多云",
     *                 "humidity":"71",
     *                 "moonPhase":"残月",
     *                 "windDirDay":"南风",
     *                 "windDirNight":"西风",
     *                 "vis":"24",
     *                 "fxDate":"2022-03-30",
     *                 "moonrise":"05:22",
     *                 "pressure":"1021",
     *                 "iconNight":"151",
     *                 "sunset":"18:36",
     *                 "windSpeedNight":"3",
     *                 "uvIndex":"4",
     *                 "tempMin":"3"
     *             }
     *         ]
     *     }
     * }
     */
    @PostMapping("/weather/day")
    Result<JSONObject> getWeatherDay(@Validated @RequestBody QueryWeatherDTO dto);

    /**
     * 查询天气历史数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/3
     * @version
     */
    @PostMapping("/weather/history")
    @Deprecated
    Result<List<WeatherDailyDTO>> weatherForhistory(@RequestBody QueryWeatherHsitoryVO weatherHsitoryVO);

    @PostMapping("/weather/history/v2")
    Result<List<WeatherDailyV2DTO>> weatherForHistoryV2(@RequestBody QueryWeatherHsitoryVO param);

    /**
     * 根据地市编码查询实时空气质量
     * @apiNote 根据地市编码查询实时空气质量
     * @apiVersion 11.4
     * @tag 十一、数据能力
     * @requestDemo {
     *     "adcode":"500116"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *      pubTime: 空气质量数据发布时间
     *      aqi: 空气质量指数
     *      level: 空气质量指数等级
     *      category: 空气质量指数级别
     *      primary: 空气质量的主要污染物，空气质量为优时，返回值为NA
     *      pm10: PM10
     *      pm2p5: PM2.5
     *      no2: 二氧化氮
     *      so2: 二氧化硫
     *      co: 一氧化碳
     *      o3: 臭氧
     * 返回参数示例：
     * {
     *  "code": 200,
     *  "msg": "请求成功",
     *  "time": 1648625457865,
     *  "data": {
     *     "pubTime": "2021-02-16T14:00+08:00",
     *     "aqi": "28",
     *     "level": "1",
     *     "category": "优",
     *     "primary": "NA",
     *     "pm10": "28",
     *     "pm2p5": "5",
     *     "no2": "3",
     *     "so2": "2",
     *     "co": "0.2",
     *     "o3": "76"
     * }
     */
    @PostMapping("/air/now")
    Result<JSONObject> getAirNow(@Validated @RequestBody QueryWeatherDTO dto);

    /**
     * 根据地市编码查询传参时间之前最新一条天气、空气质量信息
     * @apiNote 根据地市编码查询传参时间之前最新一条天气、空气质量信息
     * @apiVersion 11.11
     * @tag 十一、数据能力
     * @requestDemo {
     *     "adCode":"110000",
     *     "alarmTime":"2024-05-09 11:00:00"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     adcode: 地市区域编码
     *     locationNameZh: 地市名称
     *     pubTime: 空气质量数据发布时间
     *     pubTimeDate: 空气质量数据发布时间
     *     aqi: 空气质量指数
     *     level: 空气质量指数等级
     *     category: 空气质量指数级别
     *     primary: 空气质量的主要污染物，空气质量为优时，返回值为NA
     *     pm10: PM10
     *     pm2p5: pm2p5
     *     no2: 二氧化氮
     *     so2: 二氧化硫
     *     co: 一氧化碳
     *     o3: 臭氧
     *     provinceId: 省份编码
     *     cityId: 地市编码
     *     countyId: 区县编码
     *     provinceName: 省份名称
     *     cityName: 地市名称
     *     countyName: 区县名称
     *     obsTime: 数据观测时间
     *     obsTimeDate: 数据观测日期
     *     temp: 温度，默认单位：摄氏度
     *     feelsLike: 体感温度，默认单位：摄氏度
     *     icon: 天气状况和图标的代码
     *     text: 天气状况的文字描述，包括阴晴雨雪等天气状态的描述
     *     wind360: 风向360角度
     *     windDir: 风向
     *     windScale: 风力等级
     *     windSpeed: 风速，公里/小时
     *     humidity: 操作的集合类型
     *     precip: 当前小时累计降水量，默认单位：毫米
     *     pressure: 大气压强，默认单位：百帕
     *     vis: 能见度，默认单位：公里
     *     cloud: 云量，百分比数值
     *     dew: 露点温度
     * 返回参数示例：
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1715234930178,
     *     "data": {
     *         "airData": {
     *             "adcode": "110000",
     *             "locationNameZh": "北京",
     *             "pubTime": "2024-05-09 11:00:00",
     *             "pubTimeDate": "2024-05-09 11:00:00",
     *             "aqi": 73.0,
     *             "level": 2,
     *             "category": "良",
     *             "primary": "PM10",
     *             "pm10": 95,
     *             "pm2p5": 43,
     *             "no2": 24.0,
     *             "so2": 4.0,
     *             "co": 0.5,
     *             "o3": 112.0,
     *             "provinceId": "110000",
     *             "cityId": null,
     *             "countyId": null,
     *             "provinceName": "北京市",
     *             "cityName": null,
     *             "countyName": null
     *         },
     *         "weatherData": {
     *             "adcode": "110000",
     *             "locationNameZh": "北京",
     *             "obsTime": "2024-04-24 14:32:00",
     *             "obsTimeDate": "2024-04-24 22:32:00",
     *             "temp": 24.0,
     *             "feelsLike": 20.0,
     *             "icon": "100",
     *             "text": "晴",
     *             "wind360": 217.0,
     *             "windDir": "西南风",
     *             "windScale": 3.0,
     *             "windSpeed": 19.0,
     *             "humidity": 32.0,
     *             "precip": 0.0,
     *             "pressure": 1002.0,
     *             "vis": 17.0,
     *             "cloud": 0.0,
     *             "dew": 6.0,
     *             "provinceId": "110000",
     *             "cityId": null,
     *             "countyId": null,
     *             "provinceName": "北京市",
     *             "cityName": null,
     *             "countyName": null
     *         }
     *     }
     * }
     */
    @PostMapping("/weather/getWeatherInfoByAlarmTime")
    Result<WeatherAlarmVO> getWeatherInfoByAlarmTime(@Validated @RequestBody QueryWeatherAlarmDTO dto);

    /**
     * 根据地市编码查询传参时间之前最新一条天气信息
     * @apiNote 根据地市编码查询传参时间之前最新一条天气信息
     * @apiVersion 11.12
     * @tag 十一、数据能力
     * @requestDemo {
     *     "adCode":"110000",
     *     "weatherTime":"2024-05-09"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     adCode: 地市区域编码
     *     obsTime: 数据观测时间
     *     temp: 温度，默认单位：摄氏度
     *     humidity: 操作的集合类型
     * 返回参数示例：
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1717124773002,
     *     "data": {
     *         "adCode": "450103",
     *         "obsTime": "2024-05-31 11:00:00",
     *         "temp": "23",
     *         "humidity": "88"
     *     }
     * }
     */
    @PostMapping("/weather/getWeatherInfoByTime")
    Result<WeatherTimeVO> getWeatherInfoByTime(@Validated @RequestBody QueryWeatherTimeDTO dto);

    /**
     * 根据时间和统计类型查询天气统计信息
     * @apiNote 根据时间和统计类型查询天气统计信息
     * @apiVersion 11.13
     * @tag 十一、数据能力
     * @requestDemo {
     *     "adCode":"450103",
     *     "statisticType":"2",
     *     "startTime":"2023-02-22 00:00:00",
     *     "endTime":"2023-02-23 23:00:00"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     date: 数据观测时间
     *     temp: 温度，默认单位：摄氏度
     *     humidity: 操作的集合类型
     * 返回参数示例【按天 参数statisticType = 2】：
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1717491924274,
     *     "data": [
     *         {
     *             "date": "2023-02-22",
     *             "temp": "18.0",
     *             "humidity": "65.0"
     *         },
     *         {
     *             "date": "2023-02-23",
     *             "temp": "21.0",
     *             "humidity": "61.0"
     *         }
     *     ]
     * }
     * 返回参数示例【按月 参数statisticType = 1】：
     * {
     * 	"code": 200,
     * 	"msg": "请求成功",
     * 	"time": 1739955811100,
     * 	"data": [
     *                {
     * 			"date": "2023-02",
     * 			"temp": "20",
     * 			"humidity": "63"
     *        }
     * 	]
     * }
     */
    @PostMapping("/weather/queryWeatherInfoStatistics")
    Result<List<WeatherStatisticsVO>> queryWeatherInfoStatistics(@Validated @RequestBody QueryWeatherStatisticsDTO dto);

    /**
     * 查询天气灾害预警信息
     * @apiNote 查询天气灾害预警信息
     * @param dto
     * @return
     */
    @PostMapping("/weather/warning")
    Result<List<WeatherWarningVO>> weatherForWarning(@Validated @RequestBody QueryWeatherDTO dto);

    /**
     * 根据地市编码查询空气质量预报
     * @apiNote 根据地市编码查询空气质量预报
     * @apiVersion 11.5
     * @tag 十一、数据能力
     * @requestDemo {
     *     "adcode":"500116"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     level: 空气质量指数等级
     *     aqi: 空气质量指数
     *     fxDate: 预报日期
     *     category: 空气质量指数级别
     *     primary: 空气质量的主要污染物，空气质量为优时，返回值为NA
     * 返回参数示例：
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1650867414525,
     *     "data": {
     *         "daily": [
     *             {
     *                 "level": "3",
     *                 "aqi": "127",
     *                 "fxDate": "2022-04-25",
     *                 "category": "轻度污染",
     *                 "primary": "O3"
     *             },
     *             {
     *                 "level": "2",
     *                 "aqi": "94",
     *                 "fxDate": "2022-04-26",
     *                 "category": "良",
     *                 "primary": "PM10"
     *             },
     *             {
     *                 "level": "2",
     *                 "aqi": "51",
     *                 "fxDate": "2022-04-27",
     *                 "category": "良",
     *                 "primary": "PM10"
     *             },
     *             {
     *                 "level": "1",
     *                 "aqi": "49",
     *                 "fxDate": "2022-04-28",
     *                 "category": "优",
     *                 "primary": "NA"
     *             },
     *             {
     *                 "level": "2",
     *                 "aqi": "68",
     *                 "fxDate": "2022-04-29",
     *                 "category": "良",
     *                 "primary": "O3"
     *             }
     *         ]
     *     }
     * }
     */
    @PostMapping("/air/day")
    Result<JSONObject> getAirDay(@Validated @RequestBody QueryWeatherDTO dto);

    /**
     * 查询空气质量历史数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/11
     * @version
     */
    @PostMapping("/air/history")
    @Deprecated
    Result<List<WeatherAirDailyDTO>> weatherAirForhistory(@RequestBody QueryWeatherHsitoryVO weatherHsitoryVO);

    @PostMapping("/air/history/v2")
    Result<List<WeatherAirDailyV2DTO>> weatherAirForHistoryV2(@RequestBody QueryWeatherHsitoryVO param);

    @PostMapping("/air/history/date")
    Result<List<WeatherAirDailyV2DTO>> weatherAirForHistoryByDate(@RequestBody QueryWeatherByDateVO param);

    /**
     * 查询未来天气数据
     */
    @PostMapping("/weather/future")
    @Deprecated
    Result<List<WeatherFutureDailyDTO>> weatherForFuture(@RequestBody QueryWeatherHsitoryVO weatherHsitoryVO);

    /**
     * 查询未来天气数据
     * @apiNote 查询未来天气数据
     * @apiVersion 11.7
     * @tag 十一、数据能力
     * @requestDemo {
     *     "pageNo": 1,
     *     "pageSize": 10
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     moonset: 月落时间
     *     windSpeedDay: 预报白天风速，公里/小时
     *     sunrise: 日出时间
     *     moonPhaseIcon: 月相图标代码，图标可通过天气状况和图标下载
     *     windScaleDay: 预报白天风力等级
     *     windScaleNight: 预报夜间风力等级
     *     wind360Day: 预报白天风向360角度
     *     iconDay: 预报白天天气状况的图标代码，图标可通过天气状况和图标下载
     *     wind360Night: 预报夜间风向360角度
     *     tempMax: 预报当天最高温度
     *     cloud: 云量，百分比数值。可能为空
     *     textDay: 预报白天天气状况文字描述，包括阴晴雨雪等天气状态的描述
     *     precip: 预报当天总降水量，默认单位：毫米
     *     textNight: 预报晚间天气状况文字描述，包括阴晴雨雪等天气状态的描述
     *     humidity: 相对湿度，百分比数值
     *     moonPhase: 月相名称
     *     windDirDay: 预报白天风向
     *     windDirNight: 预报夜间当天风向
     *     vis: 能见度，默认单位：公里
     *     fxDate: 预报日期
     *     fxTimeDate: 预报日期
     *     moonrise: 月升时间
     *     pressure: 大气压强，默认单位：百帕
     *     iconNight: 预报夜间天气状况的图标代码，图标可通过天气状况和图标下载
     *     sunset: 日落时间
     *     windSpeedNight: 预报夜间风速，公里/小时
     *     uvIndex: 紫外线强度指数
     *     tempMin: 预报当天最低温度
     *     cityName: 地市名称
     *     countyId: 区县编码
     *     cityId: 地市编码
     *     provinceName: 省份名称
     *     provinceId: 省份编码
     *     countyName: 区县名称
     * 返回参数示例：
     * {
     *     "msg":"操作成功",
     *     "code":200,
     *     "data":[
     *         {
     *             "moonset":"16:25",
     *             "windSpeedDay":"3",
     *             "sunrise":"06:03",
     *             "moonPhaseIcon":"807",
     *             "windScaleDay":"1-2",
     *             "windScaleNight":"1-2",
     *             "wind360Day":"180",
     *             "iconDay":"305",
     *             "wind360Night":"270",
     *             "tempMax":"12",
     *             "cloud":"55",
     *             "textDay":"小雨",
     *             "precip":"1.0",
     *             "textNight":"多云",
     *             "humidity":"71",
     *             "moonPhase":"残月",
     *             "windDirDay":"南风",
     *             "windDirNight":"西风",
     *             "vis":"24",
     *             "fxDate":"2022-03-30",
     *             "fxTimeDate":2022-04-21 14:00:00.000Z,
     *             "moonrise":"05:22",
     *             "pressure":"1021",
     *             "iconNight":"151",
     *             "sunset":"18:36",
     *             "windSpeedNight":"3",
     *             "uvIndex":"4",
     *             "tempMin":"3",
     *             "cityName":"鸡西市",
     *             "countyId":"230321",
     *             "cityId":"230300",
     *             "provinceName":"黑龙江省",
     *             "provinceId":"230000",
     *             "countyName":"鸡东县"
     *         }
     *     ]
     * }
     */
    @PostMapping("/weather/future/v2")
    Result<List<WeatherFutureDailyV2DTO>> weatherForFutureV2(@RequestBody QueryWeatherHsitoryVO param);

    /**
     * 查询未来天气数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/11
     * @version
     */
    @PostMapping("/weather/future/date")
    @Deprecated
    Result<List<WeatherFutureDailyDTO>> weatherForFutureByDate(@RequestBody QueryWeatherByDateVO weatherHsitoryVO);

    /**
     * 根据时间查询未来天气数据
     * @apiNote 根据时间查询未来天气数据
     * @apiVersion 11.8
     * @tag 十一、数据能力
     * @requestDemo {
     *     "startDate": "2023-08-01 12:00:00",
     *     "endDate": "2023-08-01 20:00:00",
     *     "collectionName" : "weather_future_day_info_0"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     moonset: 月落时间
     *     windSpeedDay: 预报白天风速，公里/小时
     *     sunrise: 日出时间
     *     moonPhaseIcon: 月相图标代码，图标可通过天气状况和图标下载
     *     windScaleDay: 预报白天风力等级
     *     windScaleNight: 预报夜间风力等级
     *     wind360Day: 预报白天风向360角度
     *     iconDay: 预报白天天气状况的图标代码，图标可通过天气状况和图标下载
     *     wind360Night: 预报夜间风向360角度
     *     tempMax: 预报当天最高温度
     *     cloud: 云量，百分比数值。可能为空
     *     textDay: 预报白天天气状况文字描述，包括阴晴雨雪等天气状态的描述
     *     precip: 预报当天总降水量，默认单位：毫米
     *     textNight: 预报晚间天气状况文字描述，包括阴晴雨雪等天气状态的描述
     *     humidity: 相对湿度，百分比数值
     *     moonPhase: 月相名称
     *     windDirDay: 预报白天风向
     *     windDirNight: 预报夜间当天风向
     *     vis: 能见度，默认单位：公里
     *     fxDate: 预报日期
     *     fxTimeDate: 预报日期
     *     moonrise: 月升时间
     *     pressure: 大气压强，默认单位：百帕
     *     iconNight: 预报夜间天气状况的图标代码，图标可通过天气状况和图标下载
     *     sunset: 日落时间
     *     windSpeedNight: 预报夜间风速，公里/小时
     *     uvIndex: 紫外线强度指数
     *     tempMin: 预报当天最低温度
     *     cityName: 地市名称
     *     countyId: 区县编码
     *     cityId: 地市编码
     *     provinceName: 省份名称
     *     provinceId: 省份编码
     *     countyName: 区县名称
     * 返回参数示例：
     * {
     *     "msg":"操作成功",
     *     "code":200,
     *     "data":[
     *         {
     *             "moonset":"16:25",
     *             "windSpeedDay":"3",
     *             "sunrise":"06:03",
     *             "moonPhaseIcon":"807",
     *             "windScaleDay":"1-2",
     *             "windScaleNight":"1-2",
     *             "wind360Day":"180",
     *             "iconDay":"305",
     *             "wind360Night":"270",
     *             "tempMax":"12",
     *             "cloud":"55",
     *             "textDay":"小雨",
     *             "precip":"1.0",
     *             "textNight":"多云",
     *             "humidity":"71",
     *             "moonPhase":"残月",
     *             "windDirDay":"南风",
     *             "windDirNight":"西风",
     *             "vis":"24",
     *             "fxDate":"2022-03-30",
     *             "fxTimeDate":2022-04-21 14:00:00.000Z,
     *             "moonrise":"05:22",
     *             "pressure":"1021",
     *             "iconNight":"151",
     *             "sunset":"18:36",
     *             "windSpeedNight":"3",
     *             "uvIndex":"4",
     *             "tempMin":"3",
     *             "cityName":"鸡西市",
     *             "countyId":"230321",
     *             "cityId":"230300",
     *             "provinceName":"黑龙江省",
     *             "provinceId":"230000",
     *             "countyName":"鸡东县"
     *         }
     *     ]
     * }
     */
    @PostMapping("/weather/future/date/v2")
    Result<List<WeatherFutureDailyV2DTO>> weatherForFutureByDateV2(@RequestBody QueryWeatherByDateVO param);

    /**
     * 查询天气历史数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/3
     * @version
     */
    @PostMapping("/weather/history/date")
    @Deprecated
    Result<List<WeatherDailyDTO>> weatherForhistoryByDate(@RequestBody QueryWeatherByDateVO weatherHsitoryVO);

    /**
     * 根据时间查询天气历史数据
     * @apiNote 根据时间查询天气历史数据
     * @apiVersion 11.9
     * @tag 十一、数据能力
     * @requestDemo {
     *      "startDate": "2023-08-01 12:00:00",
     *      "endDate": "2023-08-01 20:00:00"
     * }
     * @responseDemo
     * 返回参数data下字段解析：
     *     adcode: 地市区域编码
     *     locationNameZh: 地市名称
     *     obsTime: 数据观测时间
     *     obsTimeDate: 数据观测时间
     *     temp: 温度，默认单位：摄氏度
     *     feelsLike: 体感温度，默认单位：摄氏度
     *     icon: 天气状况和图标的代码
     *     text: 天气状况的文字描述，包括阴晴雨雪等天气状态的描述
     *     wind360: 风向360角度
     *     windDir: 风向
     *     windScale: 风力等级
     *     windSpeed: 风速，公里/小时
     *     humidity: 操作的集合类型
     *     precip: 当前小时累计降水量，默认单位：毫米
     *     pressure: 大气压强，默认单位：百帕
     *     vis: 能见度，默认单位：公里
     *     cloud: 云量，百分比数值
     *     dew: 露点温度
     *     cityName: 地市名称
     *     countyId: 区县编码
     *     cityId: 地市编码
     *     provinceName: 省份名称
     *     provinceId: 省份编码
     *     countyName: 区县名称
     * 返回参数示例：
     * {
     *     "msg":"操作成功",
     *     "code":200,
     *     "data":[
     *         {
     *             "adcode":"110000",
     *             "locationNameZh":"北京",
     *             "obsTime":"2022-04-04 15:06:00",
     *             "obsTimeDate":2022-04-21 14:00:00.000Z,
     *             "temp":21,
     *             "feelsLike":17,
     *             "icon":"100",
     *             "text":"晴",
     *             "wind360":225,
     *             "windDir":"西南风",
     *             "windScale":4,
     *             "windSpeed":24,
     *             "humidity":36,
     *             "precip":0,
     *             "pressure":1013,
     *             "vis":7,
     *             "cloud":10,
     *             "dew":3,
     *             "cityName":"鸡西市",
     *             "countyId":"230321",
     *             "cityId":"230300",
     *             "provinceName":"黑龙江省",
     *             "provinceId":"230000",
     *             "countyName":"鸡东县"
     *         }
     *     ]
     * }
     */
    @PostMapping("/weather/history/date/v2")
    Result<List<WeatherDailyV2DTO>> weatherForHistoryByDateV2(@RequestBody QueryWeatherByDateVO param);

    @PostMapping("/weather/point/now")
    Result<String> getWeatherNowByPoint(@RequestBody QueryWeatherDTO dto);

    @GetMapping("/weather/cache/getCache/{cacheKey}")
    Result<String> getCache(@PathVariable(value = "cacheKey") String cacheKey);

    @PostMapping("/weather/cache/setCache")
    Result<String> setCache(@RequestBody RedisCacheVO redisCacheVO);

    /**
     *
     * @param req
     * @return 租户天气质量排行
     */
    @PostMapping("/weather/tenantWeatherRank")
    Result<List<TenantWeatherRankRes>> tenantWeatherRank(@RequestBody TenantWeatherRankReq req);
}
