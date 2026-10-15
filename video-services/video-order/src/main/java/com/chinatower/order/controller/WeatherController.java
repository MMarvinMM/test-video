package com.chinatower.order.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.order.dto.*;
import com.chinatower.order.service.IWeatherService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.order.*;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * video-order
 * @ClassName WeatherController
 * @Deacription 天气接口
 * @Author chenmf
 * @Date 2022/3/29 17:18
 * @Version 1.0
 **/
@Api(tags = "天气模块")
@RestController
@Slf4j
public class WeatherController {

    private static final String ENTITY_CLASS_STR = "entityClass";

    @Autowired
    private IWeatherService weatherService;

    /**
     * 查询实时天气接口
     *
     * @param dto 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询实时天气")
    @PostMapping("/weather/now")
    public Result<JSONObject> getWeatherNow(@Validated @RequestBody QueryWeatherDTO dto) {
        log.info("WeatherController getWeatherNow param:{}", JSON.toJSONString(dto));
        JSONObject json = weatherService.getWeatherNow(dto);
        return Result.data(json);
    }

    /**
     * 查询连续无降水天数接口
     * @param dto 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询连续无降水天数")
    @PostMapping("/weather/getWeatherPrecip")
    public Result<WeatherPrecipDTO> getWeatherPrecip(@Validated @RequestBody QueryWeatherDTO dto) {
        log.info("WeatherController getWeatherPrecip param:{}", JSON.toJSONString(dto));
        WeatherPrecipDTO json = weatherService.getWeatherPrecip(dto);
        return Result.data(json);
    }

    /**
     * 查询未来天气数据接口
     *
     * @param dto 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询未来的天气")
    @PostMapping("/weather/day")
    public Result<JSONObject> getWeatherDay(@Validated @RequestBody QueryWeatherDTO dto) {
        if (dto.getDay() != 3 &&
                dto.getDay() != 7 &&
                dto.getDay() != 10 &&
                dto.getDay() != 15 &&
                dto.getDay() != 30) {
            return Result.fail("逐天查询天数为3/7/10/15/30天");
        }
        JSONObject json = weatherService.getWeatherDay(dto);
        return Result.data(json);
    }

    /**
     * 加载区域对照表缓存信息
     *
     * @return 返回值
     */
    @ApiOperation(value = "加载区域对照表缓存")
    @PostMapping("/loadLocation")
    public Result<String> loadLocationRedis() {
        weatherService.loadLocationRedis();
        weatherService.loadAreaRedis("2");
        weatherService.loadAreaRedis("3");
        weatherService.loadAreaRedis("4");
        return Result.success("刷新加载区域对照表成功!");
    }

    /**
     * 获取当前所有城市全量天气数据信息
     *
     * @return 返回值
     */
    @ApiOperation(value = "获取当前所有城市全量天气数据")
    @PostMapping("/weatherByCity")
    public Result<String> weatherByCity() {
        weatherService.weatherByCity();
        return Result.success("获取当前所有城市全量天气数据成功!");
    }

    /**
     * 获取当前所有城市全量空气质量数据
     *
     * @return 返回值
     */
    @ApiOperation(value = "获取当前所有城市全量空气质量数据")
    @PostMapping("/weatherAirByCity")
    public Result<String> weatherAirByCity() {
        weatherService.weatherAirByCity();
        return Result.success("获取当前所有城市全量空气质量数据成功!");
    }

    /**
     * 查询天气历史数据接口
     *
     * @param weatherHsitoryVO 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询mongodb天气历史数据")
    @PostMapping("/weather/history")
    public Result<List<WeatherDailyDTO>> weatherForhistory(@RequestBody QueryWeatherHsitoryVO weatherHsitoryVO) {
        List<WeatherDailyDTO> list = weatherService.weatherForHistory(weatherHsitoryVO);
        return Result.data(list);
    }

    /**
     * 查询天气历史数据接口V2
     * @param param 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询mongodb天气历史数据")
    @PostMapping("/weather/history/v2")
    public Result<List<WeatherDailyV2DTO>> weatherForHistoryV2(@RequestBody QueryWeatherHsitoryVO param) {
        List<WeatherDailyV2DTO> resultList = Lists.newArrayList();
        List<WeatherDailyDTO> list = weatherService.weatherForHistory(param);
        log.info("-----出参：{}", JSON.toJSONString(list));
        if (CollectionUtils.isNotEmpty(list)) {
            resultList = list.stream().map(x -> {
                WeatherDailyV2DTO dto = new WeatherDailyV2DTO();
                BeanUtils.copyProperties(x, dto);
                return dto;
            }).collect(Collectors.toList());
        }
        return Result.data(resultList);
    }

    /**
     * 查询空气质量历史数据接口
     *
     * @param weatherHsitoryVO 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询mongodb天气历史数据")
    @PostMapping("/air/history")
    public Result<List<WeatherAirDailyDTO>> weatherAirForhistory(@RequestBody QueryWeatherHsitoryVO weatherHsitoryVO) {
        log.info("查询mongodb天气历史数据接口,参数:{}", JSON.toJSONString(weatherHsitoryVO));
        if (weatherHsitoryVO.getQueryEntity() == null) {
            weatherHsitoryVO.setQueryEntity(new QueryEntity());
        }
        if (weatherHsitoryVO.getQueryEntity().size() == 0) {
            String time = RedisUtil.get("weatherAirDataToMongodbTime", RedisDBType.PRIMARY);
            log.info("RedisUtil==weatherAirDataToMongodbTime=={}", time);
            if (StringUtils.isEmpty(time)) {
                time = DateUtils.getTime();
                log.info("RedisUtil==weatherAirDataToMongodbTime== is null == new =={}", time);
            }
            Date date = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    0, 0, 0);
            time = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, calendar.getTime());
            weatherHsitoryVO.getQueryEntity().gt("pubTime", time);
        }
        //兼容
        if (weatherHsitoryVO.getQueryEntity().containsKey(Constants.PUB_TIME_DATE)) {
            HashMap<String, Object> pubTimeDateMap = (HashMap<String, Object>) weatherHsitoryVO.getQueryEntity().get(Constants.PUB_TIME_DATE);
            if (pubTimeDateMap != null && pubTimeDateMap.containsKey(Constants.BETWEEN)) {
                JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(pubTimeDateMap.get(Constants.BETWEEN)));
                weatherHsitoryVO.getQueryEntity().between(Constants.PUB_TIME_DATE,
                        DateUtils.parseDate(jsonArray.get(0)),DateUtils.parseDate(jsonArray.get(1)));
            }
        }
        //兼容
        weatherHsitoryVO.getQueryEntity().forEach((k,v) -> {
            HashMap<String, Object>  val = JSON.parseObject(JSON.toJSONString(v), HashMap.class);
            if (val.containsKey("in")) {
                JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(val.get("in")));
                String[] inStr = jsonArray.toArray(new String[0]);
                weatherHsitoryVO.getQueryEntity().in(k,inStr);
            }
        });
        log.info("=======查询mongo入参weatherHsitoryVO:{}", JSON.toJSONString(weatherHsitoryVO));
        List<WeatherAirDailyDTO> list = weatherService.weatherAirForHistory(weatherHsitoryVO);
        log.info("=======查询mongo出参:{}", JSON.toJSONString(list));
        list = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(WeatherAirDailyDTO::getAdcode, Comparator.nullsFirst(Comparator.naturalOrder())))), ArrayList::new));
        return Result.data(list);
    }

    /**
     * 查询空气质量历史数据接口V2
     *
     * @param param 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询mongodb天气历史数据")
    @PostMapping("/air/history/v2")
    public Result<List<WeatherAirDailyV2DTO>> weatherAirForHistoryV2(@RequestBody QueryWeatherHsitoryVO param) {
        Result<List<WeatherAirDailyDTO>> result = weatherAirForhistory(param);
        log.info("查询mongodb天气历史数据:{}", JSON.toJSONString(result));
        List<WeatherAirDailyV2DTO> resultList = Lists.newArrayList();
        if (result != null && CollectionUtils.isNotEmpty(result.getData())) {
            resultList = result.getData().stream().map(x -> {
                WeatherAirDailyV2DTO dto = new WeatherAirDailyV2DTO();
                BeanUtils.copyProperties(x, dto);
                return dto;
            }).collect(Collectors.toList());
        }
        return Result.data(resultList);
    }

    /**
     * 查询实时空气质量接口
     *
     * @param dto 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询实时空气质量")
    @PostMapping("/air/now")
    Result<JSONObject> getAirNow(@Validated @RequestBody QueryWeatherDTO dto) {

        JSONObject json = weatherService.getAirNow(dto);
        return Result.data(json);
    }

    /**
     * 查询天气灾害预警信息接口
     *
     * @param dto 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询天气灾害预警信息")
    @PostMapping("/weather/warning")
    Result<List<WeatherWarningVO>> weatherForWarning(@Validated @RequestBody QueryWeatherDTO dto) {
        List<WeatherWarningVO> resultList = weatherService.weatherForWarning(dto);
        return Result.data(resultList);
    }

    /**
     * 查询天气灾害预警信息
     * @param dto 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询天气灾害预警信息")
    @PostMapping("/weather/warning/history")
    public Result<List<List<WeatherWarningVO>>> weatherForWarningHistory(@Validated @RequestBody QueryWeatherDTO dto) {
        List<List<WeatherWarningVO>> resultList = weatherService.weatherForWarningHistory(dto);
        return Result.data(resultList);
    }

    /**
     * 查询mongodb未来天气数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/11
     * @version
     */
    @PostMapping("/weather/future")
    Result<List<WeatherFutureDailyDTO>> weatherForFuture(@RequestBody QueryWeatherHsitoryVO weatherHsitoryVO) {
        List<WeatherFutureDailyDTO> list = weatherService.weatherForFuture(weatherHsitoryVO);
        return Result.data(list);
    }

    /**
     * 查询mongodb未来天气数据V2
     * @param param 参数
     * @return 返回值
     */
    @PostMapping("/weather/future/v2")
    public Result<List<WeatherFutureDailyV2DTO>> weatherForFutureV2(@RequestBody QueryWeatherHsitoryVO param) {
        List<WeatherFutureDailyV2DTO> resultList = Lists.newArrayList();
        List<WeatherFutureDailyDTO> list = weatherService.weatherForFuture(param);
        if (CollectionUtils.isNotEmpty(list)) {
            resultList = list.stream().map(x -> {
                WeatherFutureDailyV2DTO dto = new WeatherFutureDailyV2DTO();
                BeanUtils.copyProperties(x, dto);
                return dto;
            }).collect(Collectors.toList());
        }
        return Result.data(resultList);
    }

    /**
     * 查询未来一天天气数据
     *
     * @param weatherHsitoryVO 参数
     * @return 返回值
     */
    @SneakyThrows
    @ApiOperation(value = "查询mongodb天气历史数据")
    @PostMapping("/test/weather/history")
    public Result<List<WeatherDailyDTO>> weatherForhistoryTest(@RequestBody QueryWeatherHsitoryVO weatherHsitoryVO) {
        QueryWeatherHsitoryVO queryWeatherHsitoryVO = new QueryWeatherHsitoryVO();
        QueryEntity queryEntity = new QueryEntity();
        //默认同步当天数据+未来数据
        LocalDate today = LocalDate.now();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00");
        Date start = simpleDateFormat.parse(today.format(formatterDate));
        Date end = simpleDateFormat.parse(today.plusDays(1).format(formatterDate));
        queryEntity.between("obsTimeDate", start, end);
        log.info("LocalDate======================={}", today.format(formatterDate));
        log.info("时间打印start======================={}", start);
        log.info("时间打印end======================={}", end);
        log.info("查询实体queryEntity打印======================={}", queryEntity);
        log.info("查询实体toJSONString(queryEntity)打印======================={}", JSONObject.toJSONString(queryEntity));
        queryWeatherHsitoryVO.setQueryEntity(queryEntity);
        log.info("准备调通用接口queryWeatherHsitoryVO====================，入参：==={}", queryWeatherHsitoryVO);
        log.info("准备调通用接口toJSONString(queryWeatherHsitoryVO)====================，入参：==={}", JSONObject.toJSONString(queryWeatherHsitoryVO));
        return Result.data(weatherService.weatherForHistory(queryWeatherHsitoryVO));
    }

    /**
     * 查询空气质量历史数据
     *
     * @param
     * @return
     */
    @ApiOperation(value = "查询空气质量历史数据")
    @PostMapping("/test/air/history")
    public Result<List<WeatherAirDailyDTO>> weatherAirForhistoryTest(@RequestBody QueryWeatherHsitoryVO weatherHsitoryVO) throws ParseException {
        QueryEntity queryEntity = new QueryEntity();
        //queryEntity.lte("pubTime", "2022-09-30 23:10:00"); //查询temp大于20.0
        // Map map = (Map)queryEntity.get("pubTime");
        //map.put("gte","2022-09-05 00:00:00");
        //queryEntity.gte("pubTime", "2022-09-05 00:00:00");//查询locationNameZh 是 北京
        // queryEntity.lt("aqi", 20.0);//查询temp小于20.0
        // queryEntity.in("aqi", args); //查询temp包含20.0,21.0,22.0

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);
        Date start = simpleDateFormat.parse("2022-09-05 00:00:00");
        Date end = simpleDateFormat.parse("2022-09-30 23:10:00");
        queryEntity.between(Constants.PUB_TIME_DATE, start, end);//查询日期范围
        // queryEntity.gt("pubTime", "2022-04-05 10:00:00");
        log.info(JSON.toJSONString(new Date[]{start, end}));
        SortEntity sortEntity = new SortEntity();
        sortEntity.desc("aqi");//按temp降序
        //sortEntity.asc("aqi");//按temp升序
        String[] strings = new String[]{"2022-09-05 00:00:00","2022-09-30 23:10:00"};
        queryEntity.put(Constants.PUB_TIME_DATE,new HashMap(){{put(Constants.BETWEEN,strings);}});
        weatherHsitoryVO.setQueryEntity(queryEntity);
        weatherHsitoryVO.setSortEntity(sortEntity);
        log.info(JSON.toJSONString(weatherHsitoryVO));
        List<WeatherAirDailyDTO> list = weatherService.weatherAirForHistory(weatherHsitoryVO);
        return Result.data(list);
    }

    /**
     * 查询未来天气数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/11
     * @version
     */
    @ApiOperation(value = "查询未来天气数据")
    @PostMapping("/weather/future/date")
    Result<List<WeatherFutureDailyDTO>> weatherForFutureByDate(@Validated @RequestBody QueryWeatherByDateVO weatherHsitoryDateVO) {
        if (StringUtils.isEmpty(weatherHsitoryDateVO.getCollectionName()) || !weatherHsitoryDateVO.getCollectionName().startsWith(Constants.WEATHER_FUTURE_DAY_INFO)) {
            return Result.fail("集合名为空或为非法参数！");
        }
        QueryEntity queryEntity = new QueryEntity();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);
        Date start = null;
        Date end = null;
        try {
            start = simpleDateFormat.parse(weatherHsitoryDateVO.getStartDate());
            end = simpleDateFormat.parse(weatherHsitoryDateVO.getEndDate());
        } catch (ParseException e) {
            log.error("未来天气日期转换异常", e);
        }

        queryEntity.between("fxTimeDate", start, end);//查询日期范围
        if ((Constants.WEATHER_FUTURE_DAY_INFO + 0).equals(weatherHsitoryDateVO.getCollectionName())) {
            queryEntity.put(ENTITY_CLASS_STR, WeatherFutureZeroDailyDTO.class);
        } else if ((Constants.WEATHER_FUTURE_DAY_INFO + 1).equals(weatherHsitoryDateVO.getCollectionName())) {
            queryEntity.put(ENTITY_CLASS_STR, WeatherFutureOneDailyDTO.class);
        } else if ((Constants.WEATHER_FUTURE_DAY_INFO + 2).equals(weatherHsitoryDateVO.getCollectionName())) {
            queryEntity.put(ENTITY_CLASS_STR, WeatherFutureTwoDailyDTO.class);
        } else if ((Constants.WEATHER_FUTURE_DAY_INFO + 3).equals(weatherHsitoryDateVO.getCollectionName())) {
            queryEntity.put(ENTITY_CLASS_STR, WeatherFutureThreeDailyDTO.class);
        } else if ((Constants.WEATHER_FUTURE_DAY_INFO + 4).equals(weatherHsitoryDateVO.getCollectionName())) {
            queryEntity.put(ENTITY_CLASS_STR, WeatherFutureFourDailyDTO.class);
        } else if ((Constants.WEATHER_FUTURE_DAY_INFO + 5).equals(weatherHsitoryDateVO.getCollectionName())) {
            queryEntity.put(ENTITY_CLASS_STR, WeatherFutureFiveDailyDTO.class);
        } else if ((Constants.WEATHER_FUTURE_DAY_INFO + 6).equals(weatherHsitoryDateVO.getCollectionName())) {
            queryEntity.put(ENTITY_CLASS_STR, WeatherFutureSixDailyDTO.class);
        } else if ((Constants.WEATHER_FUTURE_DAY_INFO + 7).equals(weatherHsitoryDateVO.getCollectionName())) {
            queryEntity.put(ENTITY_CLASS_STR, WeatherFutureSevenDailyDTO.class);
        } else if ((Constants.WEATHER_FUTURE_DAY_INFO + 8).equals(weatherHsitoryDateVO.getCollectionName())) {
            queryEntity.put(ENTITY_CLASS_STR, WeatherFutureEightDailyDTO.class);
        } else if ((Constants.WEATHER_FUTURE_DAY_INFO + 9).equals(weatherHsitoryDateVO.getCollectionName())) {
            queryEntity.put(ENTITY_CLASS_STR, WeatherFutureNineDailyDTO.class);
        }

        SortEntity sortEntity = new SortEntity();

        QueryWeatherHsitoryVO weatherHsitoryVO = new QueryWeatherHsitoryVO();
        weatherHsitoryVO.setQueryEntity(queryEntity);
        weatherHsitoryVO.setSortEntity(sortEntity);

        List<WeatherFutureDailyDTO> list = weatherService.weatherForFuture(weatherHsitoryVO);
        return Result.data(list);
    }

    /**
     * 查询未来天气数据V2
     * @param param 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询未来天气数据")
    @PostMapping("/weather/future/date/v2")
    public Result<List<WeatherFutureDailyV2DTO>> weatherForFutureByDateV2(@RequestBody QueryWeatherByDateVO param) {
        Result<List<WeatherFutureDailyDTO>> result = weatherForFutureByDate(param);
        List<WeatherFutureDailyV2DTO> resultList = Lists.newArrayList();
        if (result != null && CollectionUtils.isNotEmpty(result.getData())) {
            resultList = result.getData().stream().map(x -> {
                WeatherFutureDailyV2DTO dto = new WeatherFutureDailyV2DTO();
                BeanUtils.copyProperties(x, dto);
                return dto;
            }).collect(Collectors.toList());
        }
        return Result.data(resultList);
    }

    /**
     * 根据日期查询天气历史数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/3
     * @version
     */
    @ApiOperation(value = "查询天气历史数据")
    @PostMapping("/weather/history/date")
    Result<List<WeatherDailyDTO>> weatherForhistoryByDate(@Validated @RequestBody QueryWeatherByDateVO weatherHsitoryDateVO) {
        QueryEntity queryEntity = new QueryEntity();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);
        Date start = null;
        Date end = null;
        try {
            start = simpleDateFormat.parse(weatherHsitoryDateVO.getStartDate());
            start = DateUtils.addHours(start, 8);
            end = simpleDateFormat.parse(weatherHsitoryDateVO.getEndDate());
            end = DateUtils.addHours(end, 8);
        } catch (ParseException e) {
            log.error("历史天气日期转换异常", e);
        }

        queryEntity.between("obsTimeDate", start, end);//查询日期范围
        if (CollectionUtils.isNotEmpty(weatherHsitoryDateVO.getAdCodeList())) {
            queryEntity.in("adcode", weatherHsitoryDateVO.getAdCodeList().toArray(new String[0]));
        }
        SortEntity sortEntity = new SortEntity();

        QueryWeatherHsitoryVO weatherHsitoryVO = new QueryWeatherHsitoryVO();
        weatherHsitoryVO.setQueryEntity(queryEntity);
        weatherHsitoryVO.setSortEntity(sortEntity);

        List<WeatherDailyDTO> list = weatherService.weatherForHistory(weatherHsitoryVO);
        return Result.data(list);
    }

    /**
     * 根据日期查询天气历史数据
     * @param param 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询mongodb天气历史数据")
    @PostMapping("/air/history/date")
    public Result<List<WeatherAirDailyDTO>> weatherAirForHistoryByDate(@RequestBody QueryWeatherByDateVO param) {
        QueryEntity queryEntity = new QueryEntity();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.YYYY_MM_DD_HH_MM_SS);
        Date start = null;
        Date end = null;
        try {
            start = simpleDateFormat.parse(param.getStartDate());
            start = DateUtils.addHours(start, 8);
            end = simpleDateFormat.parse(param.getEndDate());
            end = DateUtils.addHours(end, 8);
        } catch (ParseException e) {
            log.error("历史空气质量日期转换异常", e);
        }

        queryEntity.between("pubTimeDate", start, end);//查询日期范围
        if (CollectionUtils.isNotEmpty(param.getAdCodeList())) {
            queryEntity.in("adcode", param.getAdCodeList().toArray(new String[0]));
        }
        SortEntity sortEntity = new SortEntity();

        QueryWeatherHsitoryVO weatherAirHistoryVO = new QueryWeatherHsitoryVO();
        weatherAirHistoryVO.setQueryEntity(queryEntity);
        weatherAirHistoryVO.setSortEntity(sortEntity);

        List<WeatherAirDailyDTO> list = weatherService.weatherAirForHistory(weatherAirHistoryVO);
        return Result.data(list);
    }

    /**
     * 根据日期查询天气历史数据V2
     * @param param 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询天气历史数据")
    @PostMapping("/weather/history/date/v2")
    public Result<List<WeatherDailyV2DTO>> weatherForHistoryByDateV2(@RequestBody QueryWeatherByDateVO param) {
        Result<List<WeatherDailyDTO>> result = weatherForhistoryByDate(param);
        List<WeatherDailyV2DTO> resultList = Lists.newArrayList();
        if (result != null && CollectionUtils.isNotEmpty(result.getData())) {
            resultList = result.getData().stream().map(x -> {
                WeatherDailyV2DTO dto = new WeatherDailyV2DTO();
                BeanUtils.copyProperties(x, dto);
                return dto;
            }).collect(Collectors.toList());
        }
        return Result.data(resultList);
    }

    /**
     * 查询空气质量预报信息
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "查询空气质量预报")
    @PostMapping("/air/day")
    Result<JSONObject> getAirDay(@Validated @RequestBody QueryWeatherDTO dto) {
        JSONObject json = weatherService.getAirDay(dto);
        return Result.data(json);
    }

    /**
     * 根据经纬度查询实时天气信息
     * @param dto
     * @return
     */
    @ApiOperation(value = "根据经纬度查询和风天气")
    @PostMapping("/weather/point/now")
    Result<String> getWeatherNowByPoint(@Validated @RequestBody QueryWeatherDTO dto) {
        return Result.data(weatherService.getWeatherNowByPoint(dto));
    }

    /**
     * 获取天气缓存信息
     * @param cacheKey 参数
     * @return 返回值
     */
    @ApiOperation(value = "根据缓存key查询缓存里存的值")
    @GetMapping("/weather/cache/getCache/{cacheKey}")
    Result<String> getCache(@PathVariable(value = "cacheKey") String cacheKey) {
        return Result.data(RedisUtil.get(cacheKey, RedisDBType.PRIMARY));
    }

    /**
     * 设置天气缓存信息
     * @param redisCacheVO 参数
     * @return 返回值
     */
    @ApiOperation(value = "根据缓存key查询缓存里存的值,给林业使用同步天气数据")
    @PostMapping("/weather/cache/setCache")
    Result<String> setCache(@RequestBody RedisCacheVO redisCacheVO) {
        if (null == redisCacheVO.getCacheTimeout()) {
            RedisUtil.set(redisCacheVO.getCacheKey(), redisCacheVO.getCacheValue(), RedisDBType.PRIMARY);
            RedisUtil.persist(redisCacheVO.getCacheKey(), RedisDBType.PRIMARY);
        } else {
            RedisUtil.set(redisCacheVO.getCacheKey(), redisCacheVO.getCacheValue(), redisCacheVO.getCacheTimeout(), RedisDBType.PRIMARY);
        }

        return Result.success("执行成功");
    }

    /**
     * 查询租户下天气质量排行
     * @param req 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询租户下天气质量排行", notes = "查询租户下天气质量排行")
    @PostMapping("/weather/tenantWeatherRank")
    public Result<List<TenantWeatherRankRes>> tenantWeatherRank(@RequestBody TenantWeatherRankReq req) {
        try {
            log.info("查询行政区划 {}", JSON.toJSONString(req));
            return Result.data(weatherService.tenantWeatherRank(req));
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询租户下天气质量排行", e);
            return Result.fail("查询租户下天气质量排行错误");
        }
    }

    /**
     * 查询租户下空气质量排行
     * @param param 参数
     * @return 返回值
     */
    @ApiOperation(value = "查询租户下空气质量排行", notes = "查询租户下空气质量排行")
    @PostMapping("/weather/getAirQualityList")
    public Result<List<WeatherAirDailyV2DTO>> getAirQualityList(@RequestBody QueryWeatherHsitoryVO param) {
        try {
            List<SysArea> sysAreaList = weatherService.getTenantSysArea();
            SortEntity sortEntity = new SortEntity();
            sortEntity.asc("aqi");
            param.setSortEntity(sortEntity);
            Result<List<WeatherAirDailyV2DTO>> weatherResult = this.weatherAirForHistoryV2(param);
            List<WeatherAirDailyV2DTO> resultList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(weatherResult.getData())) {
                weatherResult.getData().forEach(weather -> {
                    for (SysArea sysArea : sysAreaList) {
                        if (Objects.nonNull(sysArea) && Objects.equals(weather.getAdcode(), sysArea.getAreaCode())) {
                            resultList.add(weather);
                            break;
                        }
                    }

                });
            }
            return Result.data(resultList);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询租户下天气质量排行", e);
            return Result.fail("查询租户下天气质量排行错误");
        }
    }

    /**
     * 根据告警时间查询天气信息
     * @param dto 参数
     * @return 返回值
     */
    @ApiOperation(value = "根据告警时间查询天气信息")
    @PostMapping("/weather/getWeatherInfoByAlarmTime")
    public Result<WeatherAlarmVO> getWeatherInfoByAlarmTime(@Validated @RequestBody QueryWeatherAlarmDTO dto) {
        if (dto == null || dto.getAlarmTime() == null || StringUtils.isEmpty(dto.getAdCode())) {
            throw new CustomException("必填参数不能为空");
        }
        return Result.data(weatherService.getWeatherInfoByAlarmTime(dto));
    }

    /**
     * 根据时间查询天气信息
     * @param dto 参数
     * @return 返回值
     */
    @ApiOperation(value = "根据时间查询天气信息")
    @PostMapping("/weather/getWeatherInfoByTime")
    public Result<WeatherTimeVO> getWeatherInfoByTime(@Validated @RequestBody QueryWeatherTimeDTO dto) {
        if (dto == null || dto.getWeatherTime() == null || StringUtils.isEmpty(dto.getAdCode())) {
            throw new CustomException("必填参数不能为空");
        }
        return Result.data(weatherService.getWeatherInfoByTime(dto));
    }

    /**
     * 根据时间查询天气统计信息
     * 查询天气统计信息 - 给林业病虫害大屏统计使用
     * @param dto 参数
     * @return 天气数据信息
     */
    @ApiOperation(value = "查询天气统计信息")
    @PostMapping("/weather/queryWeatherInfoStatistics")
    public Result<List<WeatherStatisticsVO>> queryWeatherInfoStatistics(@Validated @RequestBody QueryWeatherStatisticsDTO dto) {
        if (dto == null || dto.getStatisticType() == null || StringUtils.isEmpty(dto.getAdCode())
                || StringUtils.isEmpty(dto.getStartTime()) || StringUtils.isEmpty(dto.getEndTime())) {
            throw new CustomException("查询天气统计必填参数不能为空");
        }
        return Result.data(weatherService.queryWeatherInfoStatistics(dto));
    }
}
