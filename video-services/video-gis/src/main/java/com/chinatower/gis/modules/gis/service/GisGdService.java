package com.chinatower.gis.modules.gis.service;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.JsonUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisMapServiceAPI;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.client.SysClient;
import com.chinatower.gis.common.service.GisCommonService;
import com.chinatower.gis.factory.service.impl.GdWorldServiceImpl;
import com.chinatower.gis.utils.HttpUtils;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@SuppressWarnings("all")
public class GisGdService implements GisMapServiceAPI {
    @Resource
    GisCommonService gisCommonService;

    @Resource
    SysClient sysClient;

    @Resource
    GdWorldServiceImpl gdWorldService;

    private static final String LOCATIONS = "locations";
    private static final String OUTPUT = "output";
    private static final String LOCATION = "location";
    private static final String PROVINCE_CODE = "provinceCode";
    private static final String CITY_CODE = "cityCode";
    private static final String ADDRESS = "address";

    /**
     * 查询天气情况-高德
     *
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result weatherQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        // 获取高德URL和KEY
        Map<String, String> map = gdWorldService.getGdConfig();
        String url = map.get(Constants.GD_MAP_URL).toString() + "/weather/weatherInfo?";
        String key = map.get(Constants.GD_MAP_KEY).toString();

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(Constants.REQ_CITY, gisKeyWordQueryReq.getAdCode());
        paramMap.put(Constants.REQ_KEY, key);
        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.GAODE + Constants.WEATHER_QUERY + Constants.RESPONSE_LOG + resStr);

        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("1".equals(status)) {
            return Result.data(json.getJSONArray(Constants.WEATHER_LIVES));
        } else {
            return Result.fail(Constants.GAODE + Constants.WEATHER_ERROR);
        }
    }


    /**
     * 行政区域查询-高德
     *
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result districtQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        // 获取高德URL和KEY
        Map<String, String> map = gdWorldService.getGdConfig();
        String url = map.get(Constants.GD_MAP_URL).toString() + "/config/district?";
        String key = map.get(Constants.GD_MAP_KEY).toString();

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(Constants.KEYWORDS, gisKeyWordQueryReq.getAdCode());
        paramMap.put(Constants.SUBDISTRICT, "0");
        paramMap.put(Constants.EXTENSIONS, "all");
        paramMap.put(Constants.REQ_KEY, key);
        //发送http请求
        log.info("查询行政区划的地址 {}  参数 {}", url, paramMap);
        long oldTime = System.currentTimeMillis();
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.GAODE + Constants.WEATHER_QUERY + Constants.RESPONSE_LOG + resStr);
        long nowTime = System.currentTimeMillis();
        log.info("查询行政区划的返回 {}  时间 {}", url, (nowTime - oldTime) + "毫秒");
        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("1".equals(status)) {

            return Result.data(json.getJSONArray(Constants.DISTRICTS));
        } else {
            return Result.fail(Constants.GAODE + Constants.DISTRICT_ERROR);
        }
    }


    /**
     * 84坐标转高德坐标
     *
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result coordinateTransformation(GisKeyWordQueryReq gisKeyWordQueryReq) {
        //请求参数非空返回
        if (StringUtils.isEmpty(gisKeyWordQueryReq.getLon())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getLat())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())) {
            return Result.fail(Constants.PARAMS_ERROR);
        }

        //返回结果集
        Map<String, Object> result = new HashMap<>();

        // 获取高德URL和KEY

        Map<String, String> map = gdWorldService.getGdConfig();
        String url = map.get(Constants.GD_MAP_URL).toString() + "/assistant/coordinate/convert?";
        String key = map.get(Constants.GD_MAP_KEY).toString();
        String lon = gisKeyWordQueryReq.getLon(); //经度
        String lat = gisKeyWordQueryReq.getLat(); //纬度

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        String location = lon + "," + lat;
        paramMap.put(LOCATIONS, location); // 经纬度
        paramMap.put(OUTPUT, "JSON"); //json格式
        paramMap.put("coordsys", "gps");
        paramMap.put(Constants.REQ_KEY, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.GAODE + Constants.COORDINATE_CONVERSION + Constants.RESPONSE_LOG + resStr);

        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);

        //正确/错误返回
        if ("1".equals(status)) {
            result.put(LOCATIONS, json.getString(LOCATIONS)); // 经纬度
            return Result.data(result);
        } else {
            return Result.fail(Constants.GAODE + Constants.COORDINATE_CONVERSION_ERROR);
        }


//        try{
//            String lon = gisKeyWordQueryReq.getLon(); //经度
//            String lat = gisKeyWordQueryReq.getLat(); //纬度
//            double[] gdres = PositionUtil.wgs84togcj02(Double.parseDouble(lon),Double.parseDouble(lat));
//            return Result.data(gdres);
//        }catch (Exception e){
//            return  Result.fail(Constants.COORDINATE_TRANSFORMATION_ERROR);
//        }
    }


    /**
     * 经纬度转地址
     *
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result codeToAdress(GisKeyWordQueryReq gisKeyWordQueryReq) {
        //请求参数非空返回
        if (StringUtils.isEmpty(gisKeyWordQueryReq.getLon())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getLat())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())) {
            return Result.fail(Constants.PARAMS_ERROR);
        }
        //返回结果集
        Map<String, Object> result = new HashMap<>();

        // 获取高德URL和KEY
        Map<String, String> map = gdWorldService.getGdConfig();
        String url = map.get(Constants.GD_MAP_URL).toString() + "/geocode/regeo?";
        String key = map.get(Constants.GD_MAP_KEY).toString();
        String lon = gisKeyWordQueryReq.getLon(); //经度
        String lat = gisKeyWordQueryReq.getLat(); //纬度

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        String location = lon + "," + lat;
        paramMap.put(LOCATION, location); //经纬度
        paramMap.put(OUTPUT, "JSON"); // json格式
        paramMap.put(Constants.REQ_KEY, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.GAODE + Constants.CODE_TO_ADDRESS + Constants.RESPONSE_LOG + resStr);

        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("1".equals(status)) {
            JSONObject job = json.getJSONObject(Constants.REGEO_CODE);
            JSONObject detail = job.getJSONObject(Constants.ADDRESS_COMPONENT);
            result.put(Constants.RES_FORMATTED_ADDRESS, job.get(Constants.FORMATTED_ADDRESS)); // 地址
            result.put(Constants.PROVINCE, detail.get(Constants.PROVINCE));// 省

            //获取省和国家编码
            String code = detail.getString(Constants.AD_CODE);
            String areaParentName = "";
            if (StringUtils.isNotEmpty(code)) {
                String jsonObject = JSON.toJSONString(sysClient.getAreaInfo(code).getData());
                SysArea cityArea = JSONObject.parseObject(jsonObject, SysArea.class);
                if (null != cityArea) {
                    log.info("cityArea：{}", jsonObject);
                    switch (cityArea.getAreaLevel()) {
                        case "1":
                            break;
                        case "2":
                            result.put(PROVINCE_CODE, cityArea.getAreaCode()); // 省编码
                            break;
                        case "3":
                            String cityCode = cityArea.getAreaCode();
                            result.put(CITY_CODE, cityCode); // 城市编码
                            areaParentName = cityArea.getAreaName();
                            result.put(PROVINCE_CODE, cityArea.getAreaParentCode());
                            break;
                        case "4":
                            String cityCode2 = cityArea.getAreaParentCode();
                            result.put(CITY_CODE, cityCode2);
                            String jsonObject2 = JSON.toJSONString(sysClient.getAreaInfo(cityCode2).getData());
                            SysArea cityArea2 = JSONObject.parseObject(jsonObject2, SysArea.class);
                            areaParentName = cityArea2.getAreaName();
                            if (cityArea2 != null) {
                                result.put(PROVINCE_CODE, cityArea2.getAreaParentCode());
                            }
                            break;
                        default:
                            log.error("arealevel未匹配上:{}", jsonObject);
                            break;
                    }

                }


            }

            //如果CITY为空，则将areaParentName传值前台
            if (detail.get(Constants.CITY).toString().equals("[]")) {
                result.put("city", areaParentName); // 市
            } else {
                result.put("city", detail.get(Constants.CITY)); // 市
            }
            //result.put(Constants.CITY,detail.get(Constants.CITY)); // 市
            result.put(Constants.DISTRICT, detail.get(Constants.DISTRICT)); // 区
            result.put(Constants.AD_CODE, detail.get(Constants.AD_CODE)); // 区

            result.put(Constants.TOWNSHIP, detail.get(Constants.TOWNSHIP)); //乡镇/社区
            result.put(Constants.TOWN_CODE, detail.get(Constants.TOWN_CODE)); //乡镇/社区
            result.put(Constants.STREET_NUMBER, detail.get(Constants.STREET_NUMBER)); // 门牌
            log.info("codeToAdress返回结果：{}", JSON.toJSONString(result));
            return Result.data(result);
        } else {
            return Result.fail(Constants.GAODE + Constants.CODE_TO_ADDRESS_ERROR);
        }

    }


    /**
     * 地址转坐标
     *
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result adressToCode(GisKeyWordQueryReq gisKeyWordQueryReq) {
        //请求参数非空返回
        if (StringUtils.isEmpty(gisKeyWordQueryReq.getAddress())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())) {
            return Result.fail(Constants.PARAMS_ERROR);
        }
        //返回结果集
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取高德URL和KEY
        Map<String, String> maps = gdWorldService.getGdConfig();
        String url = maps.get(Constants.GD_MAP_URL).toString() + "/geocode/geo?";
        String key = maps.get(Constants.GD_MAP_KEY).toString();
        String address = gisKeyWordQueryReq.getAddress(); //地址

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(ADDRESS, address); // 地址
        paramMap.put(OUTPUT, "JSON"); // json格式
        paramMap.put(Constants.REQ_KEY, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.GAODE + Constants.ADDRESS_TO_CODE + Constants.RESPONSE_LOG + resStr);

        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("1".equals(status)) {

            JSONArray jarr = json.getJSONArray("geocodes");
            for (int i = 0; i < jarr.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                JSONObject job = jarr.getJSONObject(i);
                String location = job.getString(LOCATION);
                String[] zb = location.split(",");
                map.put("formattedAddress", job.get("formatted_address")); //地址
                map.put("lon", zb[0]);  //经度
                map.put("lat", zb[1]);  //纬度
                result.add(map);
            }
            return Result.data(result);
        } else {
            return Result.fail(Constants.GAODE + Constants.ADDRESS_TO_CODE_ERROR);
        }

    }

    /**
     * 关键词搜索
     *
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result keyWordQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        //请求参数非空返回
        if (StringUtils.isEmpty(gisKeyWordQueryReq.getKeyWord())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())) {
            return Result.fail(Constants.PARAMS_ERROR);
        }
        //返回结果集
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();

        // 获取高德URL和KEY
        Map<String, String> maps = gdWorldService.getGdConfig();
        String url = maps.get(Constants.GD_MAP_URL).toString() + "/place/text?";
        String key = maps.get(Constants.GD_MAP_KEY).toString();
        String keywords = gisKeyWordQueryReq.getKeyWord(); //地址

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("keywords", keywords); // 关键字
        paramMap.put(OUTPUT, "JSON"); // json格式
        paramMap.put(Constants.REQ_KEY, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.GAODE + Constants.KEY_WORD_QUERY + Constants.RESPONSE_LOG + resStr);


        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("1".equals(status)) {
            result.put("count", json.getString("count"));
            JSONArray jarr = json.getJSONArray("pois");
            for (int i = 0; i < jarr.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                JSONObject job = jarr.getJSONObject(i);
                map.put("name", job.get("name")); // 名称
                map.put(LOCATION, job.get(LOCATION)); // 经纬度
                map.put("pname", job.get("pname")); // 所在省份名称
                map.put("cityname", job.get("cityname")); // 城市名
                map.put("adname", job.get("adname")); // 所在区县名称
                map.put(ADDRESS, job.get(ADDRESS)); // 详细地址
                list.add(map);
            }
            result.put("result", list);
            return Result.data(result);
        } else {
            return Result.fail(Constants.GAODE + Constants.ADDRESS_TO_CODE_ERROR);
        }
    }


    /**
     * 将坐标信息转化行政区域信息
     *
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result countryAreaQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {

        if (StringUtils.isEmpty(gisKeyWordQueryReq.getLon())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getLat())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())) {
            return Result.fail(Constants.PARAMS_ERROR);
        }

        //返回结果集
        Map<String, Object> result = new HashMap<>();

        // 获取高德URL和KEY
        Map<String, String> map = gdWorldService.getGdConfig();
        String url = map.get(Constants.GD_MAP_URL).toString() + "/geocode/regeo?";
        String key = map.get(Constants.GD_MAP_KEY).toString();
        String lon = gisKeyWordQueryReq.getLon(); //经度
        String lat = gisKeyWordQueryReq.getLat(); //纬度


        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        String location = lon + "," + lat;
        paramMap.put(LOCATION, location); //经纬度
        paramMap.put(OUTPUT, "JSON"); // json格式
        paramMap.put(Constants.REQ_KEY, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.GAODE + Constants.COUNTRY_AREA_QUERY + Constants.RESPONSE_LOG + resStr);

        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("1".equals(status)) {
            JSONObject job = json.getJSONObject(Constants.REGEO_CODE);
            JSONObject detail = job.getJSONObject(Constants.ADDRESS_COMPONENT);

            //获取省和国家编码
            String code = detail.getString(Constants.AD_CODE);
            String areaParentName = "";
            if (StringUtils.isNotEmpty(code)) {
                String jsonObject = JSON.toJSONString(sysClient.getAreaInfo(code).getData());
                SysArea cityArea = JSONObject.parseObject(jsonObject, SysArea.class);
                if (null != cityArea) {
                    String cityCode = cityArea.getAreaParentCode();
                    result.put(CITY_CODE, cityCode); // 城市编码
                    areaParentName = cityArea.getAreaParentName();

                    if (StringUtils.isNotEmpty(cityCode)) {
                        String jsonObject1 = JSON.toJSONString(sysClient.getAreaInfo(cityCode).getData());
                        SysArea provinceArea = JSONObject.parseObject(jsonObject1, SysArea.class);//地区信息

                        if (null != provinceArea) {
                            String provinceCode = provinceArea.getAreaParentCode();
                            result.put(PROVINCE_CODE, provinceCode); // 省编码
                        }
                    }
                }


            }

            result.put("province", detail.get(Constants.PROVINCE));// 省
            //如果CITY为空，则将areaParentName传值前台
            if (detail.get(Constants.CITY).toString().equals("[]")) {
                result.put("city", areaParentName); // 市
            } else {
                result.put("city", detail.get(Constants.CITY)); // 市
            }
            result.put("district", detail.get(Constants.DISTRICT)); // 区
            result.put("town", detail.get(Constants.TOWNSHIP)); //乡镇/社区
//            result.put("citycode",detail.get(Constants.CITY_CODE)); // 城市编码
            result.put("townCode", detail.get(Constants.TOWN_CODE)); // 街道编码
            result.put("districtCode", detail.get(Constants.AD_CODE)); // 行政区编码
            result.put(LOCATION, location); // 经纬度
            return Result.data(result);
        } else {
            return Result.fail(Constants.GAODE + Constants.ACOUNTRY_AREA_QUERY_ERROR);
        }


    }

    @Override
    public DriveNavigationRes driveNavigation(DriveNavigationReq req) {
        if (StringUtils.isEmpty(req.getOrigin())
                || StringUtils.isEmpty(req.getDestination())
                || StringUtils.isEmpty(req.getMapType())) {
            throw new CustomException(Constants.PARAMS_ERROR);
        }
        // 获取高德URL和KEY
        Map<String, String> maps = gdWorldService.getGdConfig();
        String url = maps.get(Constants.GD_MAP_URL).toString() + "/direction/driving";
        String key = maps.get(Constants.GD_MAP_KEY).toString();

        Map<String, Object> map = objectToMap(req);
        map.put("key", key);
        if (StringUtils.isNotEmpty(req.getOriginId())){
            map.put("origin_id",req.getOriginId());
        }else  if (StringUtils.isNotEmpty(req.getDestinationId())){
            map.put("destination_id",req.getDestinationId());
        }else  if (req.getOriginType() != null){
            map.put("origin_type",req.getOriginType());
        }else  if (StringUtils.isNotEmpty(req.getShowFields())){
            map.put("show_fields",req.getShowFields());
        }
        String body = HttpRequest.get(url.replace("v3", "v5")).form(map).execute().body();
        log.info("驾驶导航信息返回{}",body);
        JSONObject obj = JSON.parseObject(body);
        if ("1".equals(obj.getString("status")) && "10000".equals(obj.getString("infocode"))) {
            try {
                return JsonUtils.toSnakeObject(obj.getString("route"), DriveNavigationRes.class);
            } catch (Exception e) {
                log.error("导航数据错误", e);
                throw new CustomException("驾驶导航信息错误");
            }
        }
        throw new CustomException("驾驶导航信息错误");
    }

    @Override
    public PedestrianNavigationRes pedestrianNavigation(PedestrianNavigationReq req) {
        if (StringUtils.isEmpty(req.getOrigin())
                || StringUtils.isEmpty(req.getDestination())
                || StringUtils.isEmpty(req.getMapType())) {
            throw new CustomException(Constants.PARAMS_ERROR);
        }
        // 获取高德URL和KEY
        Map<String, String> maps = gdWorldService.getGdConfig();
        String url = maps.get(Constants.GD_MAP_URL).toString() + "/direction/walking";
        String key = maps.get(Constants.GD_MAP_KEY).toString();

        Map<String, Object> map = objectToMap(req);
        map.put("key", key);
        if (StringUtils.isNotEmpty(req.getAlternativeRoute())){
            map.put("alternative_route",req.getAlternativeRoute());
        }else if (StringUtils.isNotEmpty(req.getShowFields())){
            map.put("show_fields",req.getShowFields());
        }
        String body = HttpRequest.get(url.replace("v3", "v5")).form(map).execute().body();
        log.info("步行导航信息返回{}",body);
        JSONObject obj = JSON.parseObject(body);
        if ("1".equals(obj.getString("status")) && "10000".equals(obj.getString("infocode"))) {
            try {
                return JsonUtils.toSnakeObject(obj.getString("route"), PedestrianNavigationRes.class);
            } catch (Exception e) {
                log.error("导航数据错误", e);
                throw new CustomException("步行导航信息错误");
            }
        }
        throw new CustomException("步行导航信息错误");
    }

    @Override
    public BusNavigationRes busNavigation(BusNavigationReq req) {
        if (StringUtils.isEmpty(req.getOrigin())
                || StringUtils.isEmpty(req.getDestination())
                || StringUtils.isEmpty(req.getMapType())) {
            throw new CustomException(Constants.PARAMS_ERROR);
        }
        // 获取高德URL和KEY
        Map<String, String> maps = gdWorldService.getGdConfig();
        String url = maps.get(Constants.GD_MAP_URL).toString() + "/direction/transit/integrated";
        String key = maps.get(Constants.GD_MAP_KEY).toString();

        Map<String, Object> map = objectToMap(req);
        map.put("key", key);
        if (req.getMaxTrans() != null){
            map.put("max_trans",req.getMaxTrans());
        }else if (StringUtils.isNotEmpty(req.getShowFields())){
            map.put("show_fields",req.getShowFields());
        }
        String body = HttpRequest.get(url.replace("v3", "v5")).form(map).execute().body();
        log.info("公交导航信息返回{}",body);
        JSONObject obj = JSON.parseObject(body);
        if ("1".equals(obj.getString("status")) && "10000".equals(obj.getString("infocode"))) {
            try {
                return JsonUtils.toSnakeObject(obj.getString("route"), BusNavigationRes.class);
            } catch (Exception e) {
                log.error("公交数据错误", e);
                throw new CustomException("步行导航信息错误");
            }
        }
        throw new CustomException("公交导航信息错误");
    }


    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        // 获取对象的所有字段(包括私有字段)
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                // 设置可访问私有字段
                field.setAccessible(true);
                // 获取字段的值
                Object value = field.get(obj);
                // 如果字段值不为null，则将字段名和值添加到目标Map中
                if (value != null) {
                    map.put(field.getName(), value);

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
