package com.chinatower.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.mongodb.enums.MongoDBType;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.order.domain.SysAdcodeLocationDO;
import com.chinatower.order.dto.AreaInfoDTO;
import com.chinatower.order.dto.TenantIdIndustryDTO;
import com.chinatower.order.dto.WeatherAirHourDailyDTO;
import com.chinatower.order.mapper.SysTenantInfoMapper;
import com.chinatower.order.mongodb.*;
import com.chinatower.order.util.MongoHashUtil;
import com.chinatower.order.util.NonceUtil;
import com.chinatower.order.util.PasswordUtil;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.order.*;
import com.chinatower.platform.entity.order.QueryWeatherDTO;
import com.chinatower.order.repository.ISysAdcodeLocationRepository;
import com.chinatower.order.service.IWeatherService;
import com.chinatower.order.util.WeatherApiUtil;
import com.chinatower.platform.entity.order.QueryWeatherHsitoryVO;
import com.chinatower.platform.entity.order.WeatherFutureDailyDTO;
import com.chinatower.platform.enums.VideoSystemEnums;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.DeptVO;
import com.chinatower.system.api.model.TownShipDictData;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @ClassName WeatherServiceImpl
 * @Deacription
 * @Author chenmf
 * @Date 2022/3/30 9:15
 * @Version 1.0
 **/
@Slf4j
@Service
public class WeatherServiceImpl implements IWeatherService {

    @Autowired
    private WeatherApiUtil weatherApiUtil;

    @Autowired
    private ISysAdcodeLocationRepository sysAdcodeLocationRepository;

    @Autowired
    private WeatherDailyMongoDAO weatherDailyMongoDAO;

    @Autowired
    private WeatherAirDailyMongoDAO weatherAirDailyMongoDAO;

    @Autowired
    private WeatherAirHourDailyMongoDAO weatherAirDailyHourMongoDAO;

    @Autowired
    private WeatherFutureDailyMongoDAO weatherFutureDailyMongoDAO;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Autowired
    private WeatherWarningMongoDAO weatherWarningMongoDAO;

    @Resource
    SysTenantInfoMapper sysTenantInfoMapper;

    @Resource
    private MongoHashUtil mongoHashUtil;

    private static final String PUB_TIME_STR = "pubTime";

    private static final String OBS_TIME_STR = "obsTime";

    private static final String AD_CODE_STR = "adcode";

    private static final String HUMIDITY_STR = "humidity";

    private static final String TEMP_STR = "temp";

    private static final String DATE_STR = "date";

    /**
     * 查询实时天气
     *
     * @return
     * @author chenmf
     * @date 2022/3/30
     * @version
     */
    @Override
    public JSONObject getWeatherNow(QueryWeatherDTO dto) {
        dto.setServiceName(Constants.WEATHER_NOW);
        return weatherApiUtil.httpWeatherApi(dto);
    }

    @Override
    public WeatherPrecipDTO getWeatherPrecip(QueryWeatherDTO dto) {
        dto.setServiceName(Constants.WEATHER_PRECIP);
        // 校验adCode是否是经纬度，如果是经纬度则精度取两位
        checkAndReplaceAdCode(dto);
        JSONObject jsonObject = weatherApiUtil.httpWeatherApiFromComponent(dto);
        WeatherPrecipDTO resultData = new WeatherPrecipDTO();
        if (jsonObject != null) {
            resultData.setPrecip(jsonObject.getString("precip"));
        }
        return resultData;
    }

    private void checkAndReplaceAdCode(QueryWeatherDTO dto) {
        try {
            if (dto == null || StringUtils.isBlank(dto.getAdcode())) {
                return;
            }
            // 如果逗号分割后是数组长度是2，认为是经纬度
            String[] arr = dto.getAdcode().split(",");
            if (arr.length != 2) {
                return;
            }
            String lon = new BigDecimal(arr[0]).setScale(2, RoundingMode.HALF_UP).toPlainString();
            String lat = new BigDecimal(arr[1]).setScale(2, RoundingMode.HALF_UP).toPlainString();
            String newAdCode = String.format("%s,%s", lon, lat);
            log.info("WeatherServiceImpl checkAndReplaceAdCode, oldCode:{}, newCode:{}", dto.getAdcode(), newAdCode);
            dto.setAdcode(newAdCode);
        } catch (Exception e) {
            log.error("WeatherServiceImpl checkAndReplaceAdCode error:", e);
        }
    }

    /**
     * 查询未来的天气
     *
     * @return
     * @author chenmf
     * @date 2022/3/30
     * @version
     */
    @Override
    public JSONObject getWeatherDay(QueryWeatherDTO dto) {
        dto.setServiceName("weather/" + dto.getDay()+"d");
        return weatherApiUtil.httpWeatherApi(dto);
    }

    /**
     * 加载区域对照表缓存
     *
     * @return
     * @author chenmf
     * @date 2022/3/30
     * @version
     */
    @Override
    public void loadLocationRedis() {
        int pageNum = 1;
        int pageSize = 500;
        PageMethod.startPage(pageNum,pageSize);
        List<SysAdcodeLocationDO> sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
        PageInfo<SysAdcodeLocationDO> pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        while (!CollectionUtils.isEmpty(pageInfo.getList())) {
            Map<String, Object> redisMap = Maps.newHashMap();
            for (SysAdcodeLocationDO sysAdcodeLocationDO : sysAdcodeLocationDOList) {
                String cacheKey = WeatherApiUtil.ADCODE_INFO_KEY + sysAdcodeLocationDO.getAdcode();
                redisMap.put(cacheKey, sysAdcodeLocationDO.getLocationId());
            }
            log.info("WeatherServiceImpl redisMap:{}", JSON.toJSONString(redisMap));
            RedisUtil.multiSet(redisMap, RedisDBType.PRIMARY);
            pageNum ++;
            PageMethod.startPage(pageNum,pageSize);
            sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
            pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        }

    }

    /**
     * 获取前一天所有城市全量天气数据
     *
     * @return
     * @author chenmf
     * @date 2022/3/31
     * @version
     */
    @Override
    public void weatherByCity() {

        int pageNum = 1;
        int pageSize = 500;
        PageMethod.startPage(pageNum,pageSize);
        List<SysAdcodeLocationDO> sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
        PageInfo<SysAdcodeLocationDO> pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        while (!CollectionUtils.isEmpty(pageInfo.getList())) {
            log.info("天气查询pageNum:"+pageNum);
            List<WeatherDailyDTO> weatherList = new ArrayList<>();
            for (SysAdcodeLocationDO sysAdcodeLocationDO : pageInfo.getList()) {
                QueryWeatherDTO dto = new QueryWeatherDTO();
                dto.setServiceName(Constants.WEATHER_NOW);
                dto.setAdcode(sysAdcodeLocationDO.getAdcode());
                dto.setTaskFlag(true);
                JSONObject json = null;
                try {
                    json = weatherApiUtil.httpWeatherApi(dto);
                    log.info("天气量查询json:"+json);
                } catch (Exception e) {
                    log.error("天气查询失败" ,e);
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    log.error("休眠异常", e);
                }

                if (null != json) {
                    WeatherDailyDTO weatherDaily = new WeatherDailyDTO();
                    weatherDaily.setCloud(json.getFloat("cloud"));
                    weatherDaily.setDew(json.getFloat("dew"));
                    weatherDaily.setHumidity(json.getFloat(HUMIDITY_STR));
                    weatherDaily.setFeelsLike(json.getFloat("feelsLike"));
                    weatherDaily.setIcon(json.getString("icon"));
                    weatherDaily.setPrecip(json.getFloat("precip"));
                    weatherDaily.setPressure(json.getFloat("pressure"));
                    weatherDaily.setTemp(json.getFloat(TEMP_STR));
                    weatherDaily.setText(json.getString("text"));
                    weatherDaily.setWindDir(json.getString("windDir"));
                    weatherDaily.setWind360(json.getFloat("wind360"));
                    weatherDaily.setWindSpeed(json.getFloat("windSpeed"));
                    weatherDaily.setWindScale(json.getFloat("windScale"));
                    weatherDaily.setVis(json.getFloat("vis"));

                    String obsTime = json.getString(OBS_TIME_STR).replace("T"," ").replace("+08","");

                    String parentCacheKey = Constants.AREA_INFO_CACHE + sysAdcodeLocationDO.getAdcode();
                    AreaInfoDTO areaInfoDTO = RedisUtil.get(parentCacheKey, AreaInfoDTO.class, RedisDBType.PRIMARY);
                    if (null != areaInfoDTO) {
                        weatherDaily.setCityId(areaInfoDTO.getCityId());
                        weatherDaily.setCityName(areaInfoDTO.getCityName());
                        weatherDaily.setCountyName(areaInfoDTO.getCountyName());
                        weatherDaily.setCountyId(areaInfoDTO.getCountyId());
                        weatherDaily.setProvinceId(areaInfoDTO.getProvinceId());
                        weatherDaily.setProvinceName(areaInfoDTO.getProvinceName());
                    }

                    weatherDaily.setObsTime(obsTime);
                    Date addDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, obsTime);
                    weatherDaily.setObsTimeDate(DateUtils.addHours(addDate,8));
                    weatherDaily.setAdcode(sysAdcodeLocationDO.getAdcode());
                    weatherDaily.setLocationNameZh(sysAdcodeLocationDO.getLocationNameZh());
                    weatherList.add(weatherDaily);
                }

            }
            try{
                //保存地市天气数据到mongodb
                log.info("保存天气数据到mongodb");
                weatherDailyMongoDAO.insertAll(weatherList, "weather_day_info", MongoDBType.PRIMARY);
            } catch (Exception e) {
                log.error("保存天气数据异常", e);
            }
            pageNum ++;
            PageMethod.startPage(pageNum,pageSize);
            sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
            pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        }
    }

    /**
     * 查询实时空气质量
     *
     * @return
     * @author chenmf
     * @date 2022/4/6
     * @version
     */
    @Override
    public JSONObject getAirNow(QueryWeatherDTO dto) {
        dto.setServiceName(Constants.AIR_NOW);
        JSONObject jsonObject = getAirInfoFromDB(dto);
        // 如果mongodb数据库中的发布时间在2小时之前，则实时查询一次
        if (!isMongodbDataValid(jsonObject)) {
            // redis缓存失效时间改为1小时
            jsonObject = weatherApiUtil.httpWeatherApi(dto, (long)60+NonceUtil.buildRandom(1));
        }
        return jsonObject;
    }

    /**
     * 查询天气灾害预警信息
     * @param dto
     * @return
     */
    @Override
    public List<WeatherWarningVO> weatherForWarning(QueryWeatherDTO dto) {
        dto.setServiceName(Constants.WARNING_NOW);
        JSONObject jsonObject = weatherApiUtil.httpWeatherWarningApi(dto);
        return convert2WeatherWarningVOList(jsonObject);
    }

    @Override
    public List<List<WeatherWarningVO>> weatherForWarningHistory(QueryWeatherDTO dto) {
        dto.setServiceName(Constants.WARNING_HISTORY);
        List<JSONObject> jsonObjectList = weatherApiUtil.httpWeatherWarningHistoryApi(dto);
        List<List<WeatherWarningVO>> resultList = Lists.newArrayList();
        for (JSONObject jsonObject : jsonObjectList) {
            List<WeatherWarningVO> subList = convert2WeatherWarningVOList(jsonObject);
            if (CollectionUtils.isNotEmpty(subList)) {
                resultList.add(subList);
            }
        }
        return resultList;
    }

    private List<WeatherWarningVO> convert2WeatherWarningVOList(JSONObject jsonObject) {
        List<WeatherWarningVO> resultList = new ArrayList<>();
        if (jsonObject == null) {
            return resultList;
        }
        List<Object> warningList = jsonObject.getJSONArray("warning");
        String[] dictTypes = {Constants.WARNING_SEVERITY,Constants.WARNING_SEVERITY_COLOR};
        Result<Map<String, List<TownShipDictData>>> warningDictResult = systemFeignClient.dictType(dictTypes);
        Map<String, List<TownShipDictData>> dictMap = warningDictResult.getData();
        List<TownShipDictData> warningSeverityDictList = dictMap.get(Constants.WARNING_SEVERITY);
        List<TownShipDictData> warningSeverityColorDictList = dictMap.get(Constants.WARNING_SEVERITY_COLOR);
        //预警严重等级字典--key:severity  value:预警严重等级名称
        Map<String, String> warningSeverityMap = warningSeverityDictList.stream().collect(Collectors.toMap(TownShipDictData::getDictValue, TownShipDictData::getDictLabel));
        //预警严重等级字典--key:severityColor  value:预警严重等级颜色名称
        Map<String, String> warningSeverityColorMap = warningSeverityColorDictList.stream().collect(Collectors.toMap(TownShipDictData::getDictValue, TownShipDictData::getDictLabel));
        for (Object obj : warningList){
            WeatherWarningVO weatherWarning = JSON.parseObject(JSON.toJSONString(obj), WeatherWarningVO.class);
            //预警严重等级名称
            weatherWarning.setSeverityName(warningSeverityMap.get(weatherWarning.getSeverity()));
            //预警严重等级颜色名称
            weatherWarning.setSeverityColorName(warningSeverityColorMap.get(weatherWarning.getSeverityColor()));
            String pubTime = weatherWarning.getPubTime().replace("T"," ").replace("+08","");
            weatherWarning.setPubTime(pubTime);
            resultList.add(weatherWarning);
        }
        return resultList;
    }

    private boolean isMongodbDataValid(JSONObject jsonObject) {
        boolean isValid = false;
        try {
            if (jsonObject == null || jsonObject.get(PUB_TIME_STR) == null) {
                return false;
            }
            String pubTimeStr = jsonObject.getString(PUB_TIME_STR);
            Date pubDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, pubTimeStr);
            // 如果mongodb数据库中的发布时间在2小时之前，则实时查询一次
            isValid = !DateUtils.addHours(pubDate, 2).before(DateUtils.getNowDate());
            log.info("WeatherServiceImpl isMongodbDataValid pubTime:{}, isValid:{}", pubTimeStr, isValid);
        } catch (Exception e) {
            log.error("WeatherServiceImpl isMongodbDataValid error, e:", e);
        }
        return isValid;
    }

    public JSONObject getAirInfoFromDB(QueryWeatherDTO dto) {
        Query query = new Query();
        query.addCriteria(Criteria.where(AD_CODE_STR).is(dto.getAdcode()))
                .with(Sort.by(Sort.Order.desc(PUB_TIME_STR)))
                .limit(1);
        List<WeatherAirHourDailyDTO> weatherAirInfoDataList = weatherAirDailyHourMongoDAO.findPageList(query,
                MongoDBType.PRIMARY, WeatherAirHourDailyDTO.class, new WeatherAirHourDailyDTO().getCollectionName());
        log.info("WeatherServiceImpl getAirInfoFromDB weatherAirInfoList:{}", JSON.toJSONString(weatherAirInfoDataList));
        if (CollectionUtils.isEmpty(weatherAirInfoDataList)) {
            return null;
        }
        return JSONObject.parseObject(JSONObject.toJSONString(weatherAirInfoDataList.get(0)));
    }

    @Override
    public void weatherAirByCity() {
        int pageNum = 1;
        int pageSize = 500;
        PageMethod.startPage(pageNum,pageSize);
        List<SysAdcodeLocationDO> sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
        PageInfo<SysAdcodeLocationDO> pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        while (!CollectionUtils.isEmpty(pageInfo.getList())) {
            log.info("天气空气质量查询pageNum:"+pageNum);
            List<WeatherAirDailyDTO> weatherAirList = new ArrayList<>();
            for (SysAdcodeLocationDO sysAdcodeLocationDO : pageInfo.getList()) {
                QueryWeatherDTO dto = new QueryWeatherDTO();
                dto.setServiceName(Constants.AIR_NOW);
                dto.setAdcode(sysAdcodeLocationDO.getAdcode());
                dto.setTaskFlag(true);
                JSONObject json = null;
                try {
                    json = weatherApiUtil.httpWeatherApi(dto);
                    log.info("天气空气质量查询json:"+json);
                } catch (Exception e) {
                    log.error("天气空气质量查询失败" ,e);
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    log.error("休眠异常", e);
                }

                if (null != json) {
                    WeatherAirDailyDTO weatherAirDaily = new WeatherAirDailyDTO();
                    weatherAirDaily.setAqi(json.getFloat("aqi"));
                    weatherAirDaily.setLevel(json.getInteger("level"));
                    weatherAirDaily.setCo(json.getDouble("co"));
                    weatherAirDaily.setSo2(json.getFloat("so2"));
                    weatherAirDaily.setO3(json.getFloat("o3"));
                    weatherAirDaily.setCategory(json.getString("category"));
                    weatherAirDaily.setNo2(json.getFloat("no2"));
                    weatherAirDaily.setPm2p5(json.getString("pm2p5"));
                    weatherAirDaily.setPm10(json.getString("pm10"));
                    weatherAirDaily.setPrimary(json.getString("primary"));

                    String pubTime = json.getString(PUB_TIME_STR).replace("T"," ").replace("+08","");

                    weatherAirDaily.setPubTime(pubTime);
                    String date = weatherAirDaily.getPubTime();
                    Date addDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, date);
                    weatherAirDaily.setPubTimeDate(DateUtils.addHours(addDate,8));

                    weatherAirDaily.setAdcode(sysAdcodeLocationDO.getAdcode());
                    weatherAirDaily.setLocationNameZh(sysAdcodeLocationDO.getLocationNameZh());

                    String parentCacheKey = Constants.AREA_INFO_CACHE + sysAdcodeLocationDO.getAdcode();
                    AreaInfoDTO areaInfoDTO = RedisUtil.get(parentCacheKey, AreaInfoDTO.class, RedisDBType.PRIMARY);
                    if (null != areaInfoDTO) {
                        weatherAirDaily.setCityId(areaInfoDTO.getCityId());
                        weatherAirDaily.setCityName(areaInfoDTO.getCityName());
                        weatherAirDaily.setCountyName(areaInfoDTO.getCountyName());
                        weatherAirDaily.setCountyId(areaInfoDTO.getCountyId());
                        weatherAirDaily.setProvinceId(areaInfoDTO.getProvinceId());
                        weatherAirDaily.setProvinceName(areaInfoDTO.getProvinceName());
                    }

                    weatherAirList.add(weatherAirDaily);
                }

            }
            try{
                //保存地市天气数据到mongodb
                log.info("保存空气质量数据到mongodb");
                weatherAirDailyMongoDAO.insertAll(weatherAirList, "weather_air_day_info", MongoDBType.PRIMARY);
            } catch (Exception e) {
                log.error("保存空气质量数据异常", e);
            }
            pageNum ++;
            PageMethod.startPage(pageNum,pageSize);
            sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
            pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        }
    }

    @Override
    public void weatherWarningByCity() {
        int pageNum = 1;
        int pageSize = 500;
        PageMethod.startPage(pageNum,pageSize);
        List<SysAdcodeLocationDO> sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
        PageInfo<SysAdcodeLocationDO> pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        while (!CollectionUtils.isEmpty(pageInfo.getList())) {
            log.info("天气灾害预警查询pageNum:"+pageNum);
            List<WeatherWarningDTO> weatherWarningList = new ArrayList<>();
            for (SysAdcodeLocationDO sysAdcodeLocationDO : pageInfo.getList()) {
                QueryWeatherDTO dto = new QueryWeatherDTO();
                dto.setServiceName(Constants.WARNING_NOW);
                dto.setAdcode(sysAdcodeLocationDO.getAdcode());
                dto.setTaskFlag(true);
                List<WeatherWarningVO> warningVOS = new ArrayList<>();
                try {
                    warningVOS = weatherForWarning(dto);
                    log.info("天气灾害预警查询数据:{}",JSON.toJSONString(warningVOS));
                } catch (Exception e) {
                    log.error("天气灾害预警查询失败" ,e);
                }
                try {
                    //休眠100ms
                    Thread.sleep(100);
//                    long startTime = System.currentTimeMillis();
//                    XxlJobLogger.log("休眠开始时间为:{}", startTime);
//                    AsyncUtils.me().execute(null);
//                    long endTime = System.currentTimeMillis();
//                    XxlJobLogger.log("休眠结束，耗时:{}", endTime - startTime);
                } catch (Exception e) {
                    log.error("休眠异常", e);
                }

                if (!CollectionUtils.isEmpty(warningVOS)) {
                    for (WeatherWarningVO weatherWarningVO : warningVOS){
                        WeatherWarningDTO weatherWarningDTO = new WeatherWarningDTO();
                        BeanUtils.copyProperties(weatherWarningVO,weatherWarningDTO);
                        String pubTime = weatherWarningVO.getPubTime().replace("T"," ").replace("+08","");
                        weatherWarningDTO.setPubTime(pubTime);
                        String date = weatherWarningDTO.getPubTime();
                        Date addDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, date);
                        weatherWarningDTO.setPubTimeDate(DateUtils.addHours(addDate,8));
                        weatherWarningDTO.setAdcode(sysAdcodeLocationDO.getAdcode());
                        weatherWarningDTO.setLocationNameZh(sysAdcodeLocationDO.getLocationNameZh());
                        String parentCacheKey = Constants.WARNING_INFO_CACHE + sysAdcodeLocationDO.getAdcode();
                        AreaInfoDTO areaInfoDTO = RedisUtil.get(parentCacheKey, AreaInfoDTO.class, RedisDBType.PRIMARY);
                        if (null != areaInfoDTO) {
                            weatherWarningDTO.setCityId(areaInfoDTO.getCityId());
                            weatherWarningDTO.setCityName(areaInfoDTO.getCityName());
                            weatherWarningDTO.setCountyName(areaInfoDTO.getCountyName());
                            weatherWarningDTO.setCountyId(areaInfoDTO.getCountyId());
                            weatherWarningDTO.setProvinceId(areaInfoDTO.getProvinceId());
                            weatherWarningDTO.setProvinceName(areaInfoDTO.getProvinceName());
                        }
                        weatherWarningList.add(weatherWarningDTO);
                    }
                }

            }
            try{
                //保存地市天气灾害预警数据到mongodb
                log.info("保存天气灾害预警数据到mongodb");
                weatherWarningMongoDAO.insertAll(weatherWarningList, "weather_warning_info", MongoDBType.PRIMARY);
            } catch (Exception e) {
                log.error("保存天气灾害预警数据异常", e);
            }
            pageNum ++;
            PageMethod.startPage(pageNum,pageSize);
            sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
            pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        }
        //关闭线程池
//        AsyncUtils.me().shutdown();
    }

    /**
     * 获取当天所有城市全量空气质量数据(每小时)
     *
     * @return
     * @author dencycheng
     * @date 2022/11/9
     * @version
     */
    @Override
    public void weatherAirByCityEveryHour() {
        Date nowDate = new Date();
        String nowDateTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,nowDate);

        int pageNum = 1;
        int pageSize = 500;
        PageMethod.startPage(pageNum,pageSize);
        List<SysAdcodeLocationDO> sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
        PageInfo<SysAdcodeLocationDO> pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        while (!CollectionUtils.isEmpty(pageInfo.getList())) {
            log.info("天气空气质量查询pageNum:"+pageNum);
            List<WeatherAirHourDailyDTO> weatherAirList = new ArrayList<>();
            for (SysAdcodeLocationDO sysAdcodeLocationDO : pageInfo.getList()) {
                QueryWeatherDTO dto = new QueryWeatherDTO();
                dto.setServiceName(Constants.AIR_NOW);
                dto.setAdcode(sysAdcodeLocationDO.getAdcode());
                dto.setTaskFlag(true);
                JSONObject json = null;
                try {
                    json = weatherApiUtil.httpWeatherApiCacheOneHour(dto);
                    log.info("天气空气质量查询json:"+json);
                } catch (Exception e) {
                    log.error("天气空气质量查询失败" ,e);
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    log.error("休眠异常", e);
                }

                if (null != json) {
                    WeatherAirHourDailyDTO weatherAirDaily = new WeatherAirHourDailyDTO();
                    weatherAirDaily.setAqi(json.getFloat("aqi"));
                    weatherAirDaily.setLevel(json.getInteger("level"));
                    weatherAirDaily.setCo(json.getDouble("co"));
                    weatherAirDaily.setSo2(json.getFloat("so2"));
                    weatherAirDaily.setO3(json.getFloat("o3"));
                    weatherAirDaily.setCategory(json.getString("category"));
                    weatherAirDaily.setNo2(json.getFloat("no2"));
                    weatherAirDaily.setPm2p5(json.getString("pm2p5"));
                    weatherAirDaily.setPm10(json.getString("pm10"));
                    weatherAirDaily.setPrimary(json.getString("primary"));

                    //存储当前定时任务执行的时间
                    weatherAirDaily.setPubTime(nowDateTime);
                    weatherAirDaily.setPubTimeDate(nowDate);

                    weatherAirDaily.setAdcode(sysAdcodeLocationDO.getAdcode());
                    weatherAirDaily.setLocationNameZh(sysAdcodeLocationDO.getLocationNameZh());


                    String parentCacheKey = Constants.AREA_INFO_CACHE + sysAdcodeLocationDO.getAdcode();
                    AreaInfoDTO areaInfoDTO = RedisUtil.get(parentCacheKey, AreaInfoDTO.class, RedisDBType.PRIMARY);
                    if (null != areaInfoDTO) {
                        weatherAirDaily.setCityId(areaInfoDTO.getCityId());
                        weatherAirDaily.setCityName(areaInfoDTO.getCityName());
                        weatherAirDaily.setCountyName(areaInfoDTO.getCountyName());
                        weatherAirDaily.setCountyId(areaInfoDTO.getCountyId());
                        weatherAirDaily.setProvinceId(areaInfoDTO.getProvinceId());
                        weatherAirDaily.setProvinceName(areaInfoDTO.getProvinceName());
                    }else {
                        //缓存中获取不到，从数据库中查询
                        String adcode = sysAdcodeLocationDO.getAdcode();
                        if(StringUtils.isNotEmpty(adcode) && adcode.length() == 6){
                            String provinceId = adcode.substring(0,2)+"0000";
                            String provinceName = sysAdcodeLocationDO.getAdm1NameZh();
                            String cityId = adcode.substring(0,4)+"00";
                            String cityName = sysAdcodeLocationDO.getAdm2NameZh();
                            String countyId =sysAdcodeLocationDO.getAdcode();
                            String countyName = sysAdcodeLocationDO.getLocationNameZh();

                            weatherAirDaily.setCityId(cityId);
                            weatherAirDaily.setCityName(cityName);
                            weatherAirDaily.setCountyName(countyName);
                            weatherAirDaily.setCountyId(countyId);
                            weatherAirDaily.setProvinceId(provinceId);
                            weatherAirDaily.setProvinceName(provinceName);
                        }
                    }
                    log.info("保存空气质量数据(每小时)到mongodb");
                    weatherAirList.add(weatherAirDaily);
                }

            }
            try{
                //保存地市天气数据到mongodb
                log.info("保存空气质量数据(每小时)到mongodb");
                weatherAirDailyHourMongoDAO.insertAll(weatherAirList, "weather_air_hour_info", MongoDBType.PRIMARY);
            } catch (Exception e) {
                log.error("保存空气质量数据(每小时)异常", e);
            }
            pageNum ++;
            PageMethod.startPage(pageNum,pageSize);
            sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
            pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        }
    }

    /**
     * 查询mongodb天气历史数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/3
     * @version
     */
    @Override
    public List<WeatherDailyDTO> weatherForHistory(QueryWeatherHsitoryVO weatherHsitoryVO) {
        QueryEntity queryEntity = weatherHsitoryVO.getQueryEntity();
        queryEntity.put("entityClass", WeatherDailyDTO.class);
        SortEntity sortEntity = weatherHsitoryVO.getSortEntity();
        return weatherDailyMongoDAO.findList(queryEntity, sortEntity, MongoDBType.PRIMARY);
    }

    /**
     * 查询mongodb空气质量历史数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/3
     * @version
     */
    @Override
    public List<WeatherAirDailyDTO> weatherAirForHistory(QueryWeatherHsitoryVO weatherHsitoryVO) {
        QueryEntity queryEntity = weatherHsitoryVO.getQueryEntity();
        queryEntity.put("entityClass", WeatherAirDailyDTO.class);
        SortEntity sortEntity = weatherHsitoryVO.getSortEntity();
        log.info("-----查询mongodb空气质量历史数据-----入参：{}，sortEntity:{}", JSON.toJSONString(queryEntity), JSON.toJSONString(sortEntity));
        return weatherAirDailyMongoDAO.findList(queryEntity, sortEntity, MongoDBType.PRIMARY);
    }

    /**
     * 查询mongodb未来天气数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/11
     * @version
     */
    @Override
    public List<WeatherFutureDailyDTO> weatherForFuture(QueryWeatherHsitoryVO weatherHsitoryVO) {
        QueryEntity queryEntity = weatherHsitoryVO.getQueryEntity();
        SortEntity sortEntity = weatherHsitoryVO.getSortEntity();
        return weatherFutureDailyMongoDAO.findList(queryEntity, sortEntity, MongoDBType.PRIMARY);
    }

    /**
     * 查询空气质量预报
     *
     * @return
     * @author chenmf
     * @date 2022/4/25
     * @version
     */
    @Override
    public JSONObject getAirDay(QueryWeatherDTO dto) {
        dto.setServiceName("air/5d");
        return weatherApiUtil.httpWeatherApi(dto);
    }

    /**
     * 获取未来一天所有城市全量天气数据
     *
     * @return
     * @author chenmf
     * @date 2022/4/21
     * @version
     */
    @Override
    public void weatherDayByCity() {

        int pageNum = 1;
        int pageSize = 500;
        PageMethod.startPage(pageNum,pageSize);
        List<SysAdcodeLocationDO> sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
        PageInfo<SysAdcodeLocationDO> pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        while (!CollectionUtils.isEmpty(pageInfo.getList())) {
            log.info("天气查询pageNum:"+pageNum);
            Map<String, List<WeatherFutureDailyDTO>> weatherFutureDailyDTOMap = new HashMap<>();
            for (SysAdcodeLocationDO sysAdcodeLocationDO : pageInfo.getList()) {
                QueryWeatherDTO dto = new QueryWeatherDTO();
                dto.setServiceName("weather/10d");
                dto.setAdcode(sysAdcodeLocationDO.getAdcode());
                dto.setTaskFlag(true);
                JSONObject json = null;
                try {
                    json = weatherApiUtil.httpWeatherApi(dto);
                    log.info("天气量查询json:"+json);
                } catch (Exception e) {
                    log.error("天气查询失败" ,e);
                }
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    log.error("休眠异常", e);
                }
                if (null != json) {
                    JSONArray dailyJson = json.getJSONArray("daily");
                    String parentCacheKey = Constants.AREA_INFO_CACHE + sysAdcodeLocationDO.getAdcode();
                    AreaInfoDTO areaInfoDTO = RedisUtil.get(parentCacheKey, AreaInfoDTO.class, RedisDBType.PRIMARY);
                    for (int i = 0; i < dailyJson.size(); i++) {
                        JSONObject dayJson = dailyJson.getJSONObject(i);
                        String mapKey = String.valueOf(i);
                        WeatherFutureDailyDTO weatherDaily = JSONObject.parseObject(dayJson.toJSONString(), WeatherFutureDailyDTO.class);
                        weatherDaily.setCollectionName(Constants.WEATHER_FUTURE_DAY_INFO + mapKey);
                        this.buildFutureDayInfo(mapKey, areaInfoDTO, weatherDaily, sysAdcodeLocationDO, weatherFutureDailyDTOMap);
                    }

                }

            }
            try{
                boolean isCenter = false;
                AjaxResult ajaxResult = systemFeignClient.getConfValue(PasswordUtil.DEPENDENCE_MANAGE);
                if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
                    SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                    if (StringUtils.equals(sysConfig.getConfigValue(), com.chinatower.platform.constants.Constants.ONE)) {
                        isCenter = true;
                    }
                }


                //保存地市天气数据到mongodb
                log.info("保存未来天气数据到mongodb");
                for (Map.Entry<String, List<WeatherFutureDailyDTO>> entry : weatherFutureDailyDTOMap.entrySet()) {
                    String collectionName = Constants.WEATHER_FUTURE_DAY_INFO + entry.getKey();
                    weatherFutureDailyMongoDAO.insertAll(entry.getValue(), collectionName, MongoDBType.PRIMARY);

                    // 只有中心节点才处理分片
                    if (isCenter) {
                        // 创建hash索引
                        mongoHashUtil.createHashedIndexIfNeed(collectionName, AD_CODE_STR, "idx_adCode_hash");
                        // 处理分片
                        mongoHashUtil.shardCollectionIfNeed(collectionName, AD_CODE_STR);
                    }
                }
            } catch (Exception e) {
                log.error("保存未来天气数据异常", e);
            }
            pageNum ++;
            PageMethod.startPage(pageNum,pageSize);
            sysAdcodeLocationDOList = sysAdcodeLocationRepository.getAllList();
            pageInfo = new PageInfo<>(sysAdcodeLocationDOList);
        }
    }

    private void buildFutureDayInfo(String mapKey, AreaInfoDTO areaInfoDTO, WeatherFutureDailyDTO weatherDaily, SysAdcodeLocationDO sysAdcodeLocationDO, Map<String, List<WeatherFutureDailyDTO>> weatherFutureDailyDTOMap) {
        if (null != areaInfoDTO) {
            weatherDaily.setCityId(areaInfoDTO.getCityId());
            weatherDaily.setCityName(areaInfoDTO.getCityName());
            weatherDaily.setCountyName(areaInfoDTO.getCountyName());
            weatherDaily.setCountyId(areaInfoDTO.getCountyId());
            weatherDaily.setProvinceId(areaInfoDTO.getProvinceId());
            weatherDaily.setProvinceName(areaInfoDTO.getProvinceName());
        }

        Date addDate = DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, weatherDaily.getFxDate() + " 14:00:00");
        weatherDaily.setFxTimeDate(DateUtils.addHours(addDate,8));

        weatherDaily.setAdcode(sysAdcodeLocationDO.getAdcode());
        weatherDaily.setLocationNameZh(sysAdcodeLocationDO.getLocationNameZh());
        if (!CollectionUtils.isEmpty(weatherFutureDailyDTOMap.get(mapKey))) {
            weatherFutureDailyDTOMap.get(mapKey).add(weatherDaily);
        }
        else {
            List<WeatherFutureDailyDTO> weatherFutureDailyDTOList = new ArrayList<>();
            weatherFutureDailyDTOList.add(weatherDaily);
            weatherFutureDailyDTOMap.put(mapKey, weatherFutureDailyDTOList);
        }
    }

    /**
     * 加载区域数据
     * 2=省份级,3=地市级,4=区县级
     * @return
     * @author chenmf
     * @date 2022/4/20
     * @version
     */
    @Override
    public void loadAreaRedis(String areaLevel) {
        //省
        if (areaLevel.equals("2")) {
            SysArea sysArea = new SysArea();
            sysArea.setAreaLevel(areaLevel);
            String jsonObject = JSON.toJSONString(systemFeignClient.getRegion(sysArea).getData());
            List<SysArea> areaList = JSONObject.parseArray(jsonObject, SysArea.class);
            if (!CollectionUtils.isEmpty(areaList)) {
                Map<String, Object> redisMap = Maps.newHashMap();
                for (SysArea sysAreaInfo : areaList) {
                    AreaInfoDTO areaInfoDTO = new AreaInfoDTO();
                    areaInfoDTO.setProvinceId(sysAreaInfo.getAreaCode());
                    areaInfoDTO.setProvinceName(sysAreaInfo.getAreaName());
                    String cacheKey = Constants.AREA_INFO_CACHE + sysAreaInfo.getAreaCode();
                    redisMap.put(cacheKey, areaInfoDTO);
                }
                RedisUtil.multiSet(redisMap, RedisDBType.PRIMARY);
            }
        }
        //市
        else if (areaLevel.equals("3")) {
            SysArea sysArea = new SysArea();
            sysArea.setAreaLevel(areaLevel);
            String jsonObject = JSON.toJSONString(systemFeignClient.getRegion(sysArea).getData());
            List<SysArea> areaList = JSONObject.parseArray(jsonObject, SysArea.class);
            if (!CollectionUtils.isEmpty(areaList)) {
                Map<String, Object> redisMap = Maps.newHashMap();
                for (SysArea sysAreaInfo : areaList) {
                    AreaInfoDTO areaInfoDTO = new AreaInfoDTO();
                    areaInfoDTO.setCityId(sysAreaInfo.getAreaCode());
                    areaInfoDTO.setCityName(sysAreaInfo.getAreaName());

                    String parentCacheKey =Constants.AREA_INFO_CACHE + sysAreaInfo.getAreaParentCode();
                    AreaInfoDTO areaParentInfoDTO = RedisUtil.get(parentCacheKey, AreaInfoDTO.class, RedisDBType.PRIMARY);
                    areaInfoDTO.setProvinceId(areaParentInfoDTO.getProvinceId());
                    areaInfoDTO.setProvinceName(areaParentInfoDTO.getProvinceName());

                    String cacheKey = Constants.AREA_INFO_CACHE + sysAreaInfo.getAreaCode();
                    redisMap.put(cacheKey, areaInfoDTO);
                }
                RedisUtil.multiSet(redisMap, RedisDBType.PRIMARY);
            }
        }
        //县
        else if (areaLevel.equals("4")) {

            SysArea sysArea = new SysArea();
            sysArea.setAreaLevel(areaLevel);
            String jsonObject = JSON.toJSONString(systemFeignClient.getRegion(sysArea).getData());
            List<SysArea> areaList = JSONObject.parseArray(jsonObject, SysArea.class);
            if (!CollectionUtils.isEmpty(areaList)) {
                Map<String, Object> redisMap = Maps.newHashMap();
                for (SysArea sysAreaInfo : areaList) {

                    AreaInfoDTO areaInfoDTO = new AreaInfoDTO();
                    areaInfoDTO.setCountyId(sysAreaInfo.getAreaCode());
                    areaInfoDTO.setCountyName(sysAreaInfo.getAreaName());

                    String parentCacheKey =Constants.AREA_INFO_CACHE + sysAreaInfo.getAreaParentCode();
                    AreaInfoDTO areaParentInfoDTO = RedisUtil.get(parentCacheKey, AreaInfoDTO.class, RedisDBType.PRIMARY);
                    areaInfoDTO.setProvinceId(areaParentInfoDTO.getProvinceId());
                    areaInfoDTO.setProvinceName(areaParentInfoDTO.getProvinceName());
                    areaInfoDTO.setCityId(areaParentInfoDTO.getCityId());
                    areaInfoDTO.setCityName(areaParentInfoDTO.getCityName());

                    String cacheKey = Constants.AREA_INFO_CACHE + sysAreaInfo.getAreaCode();
                    redisMap.put(cacheKey, areaInfoDTO);
                }
                RedisUtil.multiSet(redisMap, RedisDBType.PRIMARY);
            }
        }
    }

    /**
     * 根据经纬度查询和风天气
     * @return
     * @author luo.zhenghong
     * @date 2022/5/26
     * @version
     */
    @Override
    public String getWeatherNowByPoint(QueryWeatherDTO dto) {
        dto.setServiceName(Constants.WEATHER_NOW);
        return weatherApiUtil.getWeatherFromAPI(dto);
    }

    /**
     *
     * @param req
     * @return 查案租户区域空气质量排行
     */
    public List<TenantWeatherRankRes> tenantWeatherRank(TenantWeatherRankReq req) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isEmpty(req.getTenantId())){
            req.setTenantId(loginUser.getUser().getTenantId());
            req.setIndustryCode(loginUser.getUser().getIndustryCode());
        }
        List<SysArea> areas = null;
        //客户管理区域
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            SysDept sysDept = new SysDept();
            sysDept.setTenantId(loginUser.getUser().getTenantId());
            sysDept.setIndustryCode(loginUser.getUser().getIndustryCode());
            areas = sysTenantInfoMapper.selectStreet(sysDept);
        }else {
            areas = sysTenantInfoMapper.getTenantSysArea(req);
        }

        List<TenantWeatherRankRes> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(req.getAdCode())){
            List<SysArea> sysArea = sysTenantInfoMapper.getSysArea(req.getAdCode());
            areas.addAll(sysArea);
            areas = areas.stream().collect(Collectors.toMap(SysArea::getAreaCode, area -> area, (u1, u2) -> u1))
                    .values().stream().collect(Collectors.toList());
        }
        for (SysArea area: areas) {
            if(area == null){
                continue;
            }
            QueryWeatherDTO dto = new QueryWeatherDTO();
            dto.setAdcode(area.getAreaCode());
            dto.setLongitude(area.getLongitude());
            dto.setLatitude(area.getLatitude());
            JSONObject result = getAirNow(dto);
            if (result != null){
                TenantWeatherRankRes copy = BeanCopyUtils.copy(area, TenantWeatherRankRes.class);
                float api = result.getFloatValue("aqi");
                copy.setAqi(api);
                list.add(copy);
            }
        }
        //排序
        if (!list.isEmpty()){
            list = list.stream().sorted(Comparator.comparingDouble(TenantWeatherRankRes::getAqi).thenComparing(TenantWeatherRankRes::getAqi)).collect(Collectors.toList());
        }
        return list;
    }

    public Set<DeptVO> getDeptListByUserDataScope(SysUser sysUser) {
        Set<DeptVO> deptList = new HashSet<>();

        Set<String> dataScopes = sysUser.getRoles().stream().map(BaseDataScopeEntity::getDataScope).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        if (dataScopes.contains(Constants.DATA_SCOPE_ALL)) {
            TenantIdIndustryDTO dto = new TenantIdIndustryDTO();
            dto.setTenantId(sysUser.getTenantId());
            dto.setIndustryCode(sysUser.getIndustryCode());
            List<SysDept> treeselectByTenantId = sysTenantInfoMapper.selectTreeselectByTenantId(dto);
            if (CollUtil.isNotEmpty(treeselectByTenantId)) {
                treeselectByTenantId.forEach(d -> {
                    deptList.add(new DeptVO(d.getDeptId(), d.getParentId(), d.getParentName()));
                });
            }
        } else {
            for (String roleDataScope : dataScopes) {
                switch (roleDataScope) {
                    case Constants.DATA_SCOPE_CUSTOM:
                        List<SysDept> sysDepts = sysTenantInfoMapper.selectCustomTanentDept(sysUser);
                        if (CollUtil.isNotEmpty(sysDepts)) {
                            sysDepts.forEach(d -> {
                                deptList.add(new DeptVO(d.getDeptId(), d.getParentId(), d.getParentName()));
                            });
                        }
                        break;
                    case Constants.DATA_SCOPE_DEPT:
                    case Constants.DATA_SCOPE_SELF:
                        deptList.add(new DeptVO(sysUser.getDept().getDeptId(), sysUser.getDept().getParentId(), sysUser.getDept().getParentName()));
                        break;
                    case Constants.DATA_SCOPE_DEPT_AND_CHILD:
                        //如果不是根节点,加deptId数据过滤
                        List<SysDept> sysDepts1 = sysTenantInfoMapper.selectTenantChildDept(sysUser.getDept().getDeptId());
                        if (CollUtil.isNotEmpty(sysDepts1)) {
                            sysDepts1.forEach(d -> {
                                deptList.add(new DeptVO(d.getDeptId(), d.getParentId(), d.getParentName()));
                            });
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return deptList;
    }

    /**
     * 获取租户的权限区域
     * @return
     */
    @Override
    public List<SysArea> getTenantSysArea() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        List<SysArea> areas;
        //客户管理区域
        if (VideoSystemEnums.UserType.TENANT_USER.getIndex().equals(loginUser.getUser().getUserType())) {
            SysDept sysDept = new SysDept();
            sysDept.setTenantId(loginUser.getUser().getTenantId());
            sysDept.setIndustryCode(loginUser.getUser().getIndustryCode());
            areas = sysTenantInfoMapper.selectStreet(sysDept);
        }else {
            TenantWeatherRankReq req = new TenantWeatherRankReq();
            req.setTenantId(loginUser.getUser().getTenantId());
            req.setIndustryCode(loginUser.getUser().getIndustryCode());
            areas = sysTenantInfoMapper.getTenantSysArea(req);
        }
        return areas;
    }

    @Override
    public WeatherAlarmVO getWeatherInfoByAlarmTime(QueryWeatherAlarmDTO dto) {
        Stopwatch sw = Stopwatch.createStarted();
        Query airQuery = new Query();
        airQuery.addCriteria(Criteria.where(AD_CODE_STR).is(dto.getAdCode()).and(PUB_TIME_STR).lte(dto.getAlarmTime()))
                .with(Sort.by(Sort.Order.desc(PUB_TIME_STR)))
                .limit(1);
        List<WeatherAirHourDailyDTO> weatherAirInfoDataList = weatherAirDailyHourMongoDAO.findPageList(airQuery,
                MongoDBType.PRIMARY, WeatherAirHourDailyDTO.class, new WeatherAirHourDailyDTO().getCollectionName());
        WeatherAlarmSubAirVO airData = null;
        if (!CollectionUtils.isEmpty(weatherAirInfoDataList)) {
            airData = JSONObject.parseObject(JSONObject.toJSONString(weatherAirInfoDataList.get(0)), WeatherAlarmSubAirVO.class);
        }
        log.info("WeatherServiceImpl getWeatherInfoByAlarmTime step1 weatherAirInfoDataList:{} airData:{} sw:{}",
                JSON.toJSONString(weatherAirInfoDataList), airData != null ? JSON.toJSONString(airData) : null, sw.elapsed(TimeUnit.MILLISECONDS));

        Query weatherQuery = new Query();
        weatherQuery.addCriteria(Criteria.where(AD_CODE_STR).is(dto.getAdCode()).and(OBS_TIME_STR).lte(dto.getAlarmTime()))
                .with(Sort.by(Sort.Order.desc(OBS_TIME_STR)))
                .limit(1);
        List<WeatherDailyDTO> weatherInfoDataList = weatherDailyMongoDAO.findPageList(weatherQuery,
                MongoDBType.PRIMARY, WeatherDailyDTO.class, new WeatherDailyDTO().getCollectionName());
        WeatherAlarmSubWeatherVO weatherData = null;
        if (!CollectionUtils.isEmpty(weatherInfoDataList)) {
            weatherData = JSONObject.parseObject(JSONObject.toJSONString(weatherInfoDataList.get(0)), WeatherAlarmSubWeatherVO.class);
        }
        log.info("WeatherServiceImpl getWeatherInfoByAlarmTime step2 weatherInfoDataList:{} weatherData:{} sw:{}",
                JSON.toJSONString(weatherInfoDataList), weatherData != null ? JSON.toJSONString(weatherData) : null, sw.elapsed(TimeUnit.MILLISECONDS));

        WeatherAlarmVO result = new WeatherAlarmVO();
        result.setWeatherData(weatherData);
        result.setAirData(airData);
        return result;
    }

    @Override
    public WeatherTimeVO getWeatherInfoByTime(QueryWeatherTimeDTO dto) {
        String weatherTime = dto.getWeatherTime().trim() + " 23:59:59";
        Stopwatch sw = Stopwatch.createStarted();
        Query weatherQuery = new Query();
        weatherQuery.addCriteria(Criteria.where(AD_CODE_STR).is(dto.getAdCode()).and(OBS_TIME_STR).lte(weatherTime))
                .with(Sort.by(Sort.Order.desc(OBS_TIME_STR)))
                .limit(1);
        List<WeatherDailyDTO> weatherInfoDataList = weatherDailyMongoDAO.findPageList(weatherQuery,
                MongoDBType.PRIMARY, WeatherDailyDTO.class, new WeatherDailyDTO().getCollectionName());
        WeatherTimeVO resultData = new WeatherTimeVO();
        resultData.setAdCode(dto.getAdCode());
        if (!CollectionUtils.isEmpty(weatherInfoDataList)) {
            WeatherAlarmSubWeatherVO weatherData = JSONObject.parseObject(JSONObject.toJSONString(weatherInfoDataList.get(0)), WeatherAlarmSubWeatherVO.class);
            if (weatherData != null) {
                String humidity = new BigDecimal(String.valueOf(weatherData.getHumidity())).stripTrailingZeros().toPlainString();
                String temp = new BigDecimal(String.valueOf(weatherData.getTemp())).stripTrailingZeros().toPlainString();
                resultData.setHumidity(humidity);
                resultData.setTemp(temp);
                resultData.setObsTime(weatherData.getObsTime());
            }
        }

        // 当天时间 格式：yyyy-MM-dd
        String currentDateStr = DateUtils.getDate();
        log.info("WeatherServiceImpl getWeatherInfoByTime weatherInfoDataList:{} weatherData:{} currentDateStr:{}",
                JSON.toJSONString(weatherInfoDataList), JSON.toJSONString(resultData), currentDateStr);

        // 如果参数查询时间是当天，如果mongodb数据库中当天的天气数据还没有入库，则查询实时天气接口获取天气数据
        if (StringUtils.equals(currentDateStr, dto.getWeatherTime())) {

            boolean isNeedQueryWeatherNow = isNeedQueryWeatherNow(resultData.getObsTime(), dto.getWeatherTime());
            log.info("WeatherServiceImpl getWeatherInfoByTime obsTime:{}, weatherTime:{}, isNeedQueryWeatherNow:{}",
                    resultData.getObsTime(), dto.getWeatherTime(), isNeedQueryWeatherNow);
            if (isNeedQueryWeatherNow) {
                QueryWeatherDTO queryWeatherParam = new QueryWeatherDTO();
                queryWeatherParam.setAdcode(dto.getAdCode());
                JSONObject jsonResult = getWeatherNow(queryWeatherParam);
                log.info("WeatherServiceImpl getWeatherInfoByTime jsonResult:{}", jsonResult != null ? JSON.toJSONString(jsonResult) : null);
                if (jsonResult != null) {
                    resultData.setHumidity(jsonResult.getString(HUMIDITY_STR));
                    resultData.setTemp(jsonResult.getString(TEMP_STR));
                    String obsTime = jsonResult.getString(OBS_TIME_STR).replace("T"," ").replace("+08","");
                    resultData.setObsTime(obsTime);
                }
            }
        }

        return resultData;
    }

    private boolean isNeedQueryWeatherNow(String obsTime, String weatherTime) {
        if (StringUtils.isEmpty(obsTime)) {
            return true;
        }
        try {
            Date mongodbDataDate = DateUtils.parseDate(obsTime, DateUtils.YYYY_MM_DD_HH_MM_SS);
            return !StringUtils.equals(DateUtils.dateTime(mongodbDataDate), weatherTime);
        } catch (ParseException e) {
            log.error("WeatherServiceImpl isNeedQueryWeatherNow error:", e);
            return false;
        }
    }

    @Override
    public List<WeatherStatisticsVO> queryWeatherInfoStatistics(QueryWeatherStatisticsDTO dto) {
        List<WeatherStatisticsVO> resultList = Lists.newArrayList();
        try {
            Criteria timeCriteria = Criteria.where(AD_CODE_STR).is(dto.getAdCode())
                    .and(OBS_TIME_STR).lte(dto.getEndTime()).gte(dto.getStartTime());

            List<AggregationOperation> operations = new ArrayList<>();
            operations.add(Aggregation.match(timeCriteria));
            if (Objects.equals(dto.getStatisticType(), 1)) {
                operations.add(Aggregation.project(AD_CODE_STR, TEMP_STR, HUMIDITY_STR).and(OBS_TIME_STR).substring(0, 7).as(DATE_STR));
            } else if (Objects.equals(dto.getStatisticType(), 2)) {
                operations.add(Aggregation.project(AD_CODE_STR, TEMP_STR, HUMIDITY_STR).and(OBS_TIME_STR).substring(0, 10).as(DATE_STR));
            }
            operations.add(Aggregation.group(DATE_STR)
                    .first(DATE_STR).as(DATE_STR)
                    .avg(TEMP_STR).as(TEMP_STR)
                    .avg(HUMIDITY_STR).as(HUMIDITY_STR));
            operations.add(Aggregation.sort(Sort.Direction.ASC, DATE_STR));

            Aggregation aggregation = Aggregation.newAggregation(operations);

            resultList = weatherDailyMongoDAO.aggregate(aggregation, MongoDBType.PRIMARY, WeatherStatisticsVO.class, "weather_day_info");
            log.info("queryWeatherInfoStatistics resultList:{}", JSON.toJSONString(resultList));
            if (CollectionUtils.isNotEmpty(resultList)) {
                resultList.forEach(x -> {
                    if (StringUtils.isNotBlank(x.getTemp())) {
                        x.setTemp(new BigDecimal(x.getTemp()).setScale(0, RoundingMode.HALF_UP).toPlainString());
                    }
                    if (StringUtils.isNotBlank(x.getHumidity())) {
                        x.setHumidity(new BigDecimal(x.getHumidity()).setScale(0, RoundingMode.HALF_UP).toPlainString());
                    }
                });
            }
        } catch (Exception e) {
            log.error("queryWeatherInfoStatistics error:", e);
        }
        return resultList;
    }
}
