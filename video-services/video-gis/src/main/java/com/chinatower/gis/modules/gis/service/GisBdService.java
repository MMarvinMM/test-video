package com.chinatower.gis.modules.gis.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisMapServiceAPI;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.client.SysClient;
import com.chinatower.gis.utils.HttpUtils;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Slf4j
public class GisBdService implements GisMapServiceAPI {



    @Resource
    SysClient sysClient;
    @Resource
    SystemFeignClient systemFeignClient;

    private Map<String, String> getGdConfig() {
        Map<String, String> map = new HashMap<>();
        try {
            AjaxResult ajaxResult_1 = systemFeignClient.getConfValue(Constants.BD_MAP_URL);
            if ("200".equals(String.valueOf(ajaxResult_1.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult_1.get(AjaxResult.DATA_TAG)), SysConfig.class);
                String url = com.chinatower.platform.common.utils.StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "0");
                map.put(Constants.BD_MAP_URL, url);
            }

            AjaxResult ajaxResult_2 = systemFeignClient.getConfValue(Constants.BD_MAP_KEY);
            if ("200".equals(String.valueOf(ajaxResult_2.get(AjaxResult.CODE_TAG)))) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult_2.get(AjaxResult.DATA_TAG)), SysConfig.class);
                String key = com.chinatower.platform.common.utils.StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "0");
                map.put(Constants.BD_MAP_KEY, key);
            }

            return map;
        } catch (Exception e) {
            log.error("查询高德地址错误", e);
        }
        map.put(Constants.BD_MAP_URL, "https://api.map.baidu.com");
        map.put(Constants.BD_MAP_KEY, "IROrobOi2n7OnPqWuVTdfTWOzG0Hgxuu");
        return map;
    }

    private static final String OUTPUT = "output";
    private static final String LOCATION = "location";
    private static final String PROVINCE = "province";
    private static final String ADDRESS = "address";
    private static final String RESULT = "result";
    private static final String LOCATIONS = "locations";
    /**
     * 关键字搜索
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result keyWordQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        //请求参数非空返回
        if(StringUtils.isEmpty(gisKeyWordQueryReq.getKeyWord())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getRegion())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())){
            return Result.fail(Constants.PARAMS_ERROR );
        }
        //返回结果集
        Map<String,Object> result = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();

        // 获取高德URL和KEY
        Map<String, String> maps = getGdConfig();
        String url = maps.get(Constants.BD_MAP_URL).toString() + "/place/v2/search?";
        String key = maps.get(Constants.BD_MAP_KEY).toString();
        String keywords = gisKeyWordQueryReq.getKeyWord(); // 地址
        String region = gisKeyWordQueryReq.getRegion(); // 检索行政区划区域

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("query", keywords); // 关键字
        paramMap.put(OUTPUT,"json"); // json格式
        paramMap.put("region",region); // 范围
        paramMap.put(Constants.REQ_AK, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.BAIDU + Constants.KEY_WORD_QUERY + Constants.RESPONSE_LOG + resStr);


        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("0".equals(status)) {
            JSONArray jarr = json.getJSONArray("results");
            result.put("count",jarr.size());

            for(int i = 0; i < jarr.size(); i++) {
                Map<String,Object> map = new HashMap<>();
                JSONObject job = jarr.getJSONObject(i);

                map.put("name",job.get("name")); // 名称
                JSONObject jwd = job.getJSONObject(LOCATION);
                map.put(LOCATION,jwd.getString("lng") + "," + jwd.getString("lat") ); // 经纬度
                map.put("pname",job.get(PROVINCE)); // 所在省份名称
                map.put("cityname",job.get("city")); // 城市名
                map.put("adname",job.get("area")); // 所在区县名称
                map.put(ADDRESS,job.get(ADDRESS)); // 详细地址
                list.add(map);
            }
            result.put(RESULT,list);
            return Result.data(result);
        } else {
            return Result.fail(Constants.BAIDU + Constants.KEY_WORD_QUERY_ERROR);
        }
    }

    /**
     * 经纬度转地址
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result codeToAdress(GisKeyWordQueryReq gisKeyWordQueryReq) {
        //请求参数非空返回
        if(StringUtils.isEmpty(gisKeyWordQueryReq.getLon())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getLat())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())){
            return Result.fail(Constants.PARAMS_ERROR );
        }
        //返回结果集
        Map<String,Object> result = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();

        // 获取高德URL和KEY
        Map<String, String> maps = getGdConfig();
        String url = maps.get(Constants.BD_MAP_URL).toString() + "/reverse_geocoding/v3/?";
        String key = maps.get(Constants.BD_MAP_KEY).toString();
        String lat = gisKeyWordQueryReq.getLat();
        String lon = gisKeyWordQueryReq.getLon();
        String location = lat + "," + lon; // 纬经度

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(OUTPUT,"json"); // json格式
        paramMap.put(LOCATION,location); // 经纬度
        paramMap.put(Constants.REQ_AK, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.BAIDU + Constants.CODE_TO_ADDRESS + Constants.RESPONSE_LOG + resStr);


        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("0".equals(status)) {
            JSONObject job = json.getJSONObject(RESULT);

           JSONObject detail = job.getJSONObject("addressComponent");

            result.put(Constants.RES_FORMATTED_ADDRESS,job.get(Constants.FORMATTED_ADDRESS)); // 地址
            result.put(Constants.PROVINCE,detail.get(Constants.PROVINCE));// 省
            result.put(Constants.CITY,detail.get(Constants.CITY)); // 市
            result.put(Constants.DISTRICT,detail.get(Constants.DISTRICT)); // 区
            result.put(Constants.TOWNSHIP,detail.get("town")); //乡镇/社区
            result.put(Constants.STREET_NUMBER,detail.getString("street") + detail.getString("street_number")); // 门牌
            return Result.data(result);
        } else {
            return Result.fail(Constants.BAIDU + Constants.CODE_TO_ADDRESS_ERROR);
        }
    }


    /**
     * 地址转为经纬度
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
        Map<String, Object> result = new HashMap<>();

        // 获取高德URL和KEY
        Map<String, String> maps = getGdConfig();
        String url = maps.get(Constants.BD_MAP_URL).toString() + "/geocoding/v3/?";
        String key = maps.get(Constants.BD_MAP_KEY).toString();
        String address = gisKeyWordQueryReq.getAddress(); //地址

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(ADDRESS, address); // 地址
        paramMap.put(OUTPUT, "json"); // json格式
        paramMap.put(Constants.REQ_AK, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.BAIDU + Constants.ADDRESS_TO_CODE + Constants.RESPONSE_LOG + resStr);

        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("0".equals(status)) {
            JSONObject job = json.getJSONObject(RESULT);
            JSONObject location = job.getJSONObject(LOCATION);
            result.put("lon", location.getString("lng"));  //经度
            result.put("lat", location.getString("lat"));  //纬度
            return Result.data(result);
        } else {
            return Result.fail(Constants.BAIDU + Constants.ADDRESS_TO_CODE_ERROR);
        }

    }

    /**
     * 查询天气
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result weatherQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        //请求参数非空返回
        if (StringUtils.isEmpty(gisKeyWordQueryReq.getAdCode())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())) {
            return Result.fail(Constants.PARAMS_ERROR);
        }

        //返回结果集
        Map<String, Object> result = new HashMap<>();

        // 获取高德URL和KEY
        Map<String, String> maps = getGdConfig();
        String url = maps.get(Constants.BD_MAP_URL).toString() + "/weather/v1/?";
        String key = maps.get(Constants.BD_MAP_KEY).toString();

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("district_id", gisKeyWordQueryReq.getAdCode());
        paramMap.put("data_type","all");
        paramMap.put(Constants.REQ_AK, key);
        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.BAIDU + Constants.WEATHER_QUERY + Constants.RESPONSE_LOG + resStr);

        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("0".equals(status)) {
            JSONObject job = json.getJSONObject(RESULT);
            JSONObject location = job.getJSONObject(LOCATION);
            JSONObject now = job.getJSONObject("now");

            result.put(PROVINCE,location.getString(PROVINCE)); // 省份
            result.put("city",location.getString("city")); // 城市
            result.put("weather",now.getString("text")); // 天气现象
            result.put("temperature",now.getString("temp")); // 温度（℃）
            result.put("winddirection",now.getString("wind_dir")); // 风向描述
            result.put("windpower",now.getString("wind_class")); // 风力等级
            result.put("humidity",now.getString("rh")); // 湿度
            result.put("reporttime",now.getString("uptime")); // 更新时间

            return Result.data(result);
        } else {
            return Result.fail(Constants.BAIDU + Constants.WEATHER_ERROR);
        }
    }


    /**
     * 84转百度坐标系
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
        Map<String, String> maps = getGdConfig();
        String url = maps.get(Constants.BD_MAP_URL).toString() + "/geoconv/v1/?";
        String key = maps.get(Constants.BD_MAP_KEY).toString();
        String lon = gisKeyWordQueryReq.getLon(); //经度
        String lat = gisKeyWordQueryReq.getLat(); //纬度

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        String location = lon + "," + lat;
        paramMap.put("coords", location);
        paramMap.put("from","1");
        paramMap.put("to","5");
        paramMap.put(Constants.REQ_AK, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.BAIDU + Constants.COORDINATE_CONVERSION + Constants.RESPONSE_LOG + resStr);

        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);

        //正确/错误返回
        if ("0".equals(status)) {
            JSONArray jarr = json.getJSONArray(RESULT);
            JSONObject job = jarr.getJSONObject(0);
            if(job.size() > 0 ){
                result.put(LOCATIONS, job.getString("x") + "," + job.getString("y")); // 经纬度
            }
            return Result.data(result);
        } else {
            return Result.fail(Constants.BAIDU + Constants.COORDINATE_CONVERSION_ERROR);
        }


//        try{
//            //84转高德
//            String lon = gisKeyWordQueryReq.getLon(); //经度
//            String lat = gisKeyWordQueryReq.getLat(); //纬度
//            double[] gdres = PositionUtil.wgs84togcj02(Double.parseDouble(lon),Double.parseDouble(lat));
//
//            //高德转百度
//            double jd  = gdres[0];
//            double wd = gdres[1];
//            double[] bdres = PositionUtil.gcj02tobd09(jd,wd);
//
//            return Result.data(bdres);
//        }catch (Exception e){
//            return  Result.fail("请求参数缺失:经度,纬度不能为空");
//        }
    }

    /**
     * 将坐标信息转化行政区域信息
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result countryAreaQuery(GisKeyWordQueryReq gisKeyWordQueryReq)
    {
        log.info("将坐标信息转化行政区域信息 请求参数 {}",JSON.toJSONString(gisKeyWordQueryReq));
        //请求参数非空返回
        if(StringUtils.isEmpty(gisKeyWordQueryReq.getLon())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getLat())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())){
            return Result.fail(Constants.PARAMS_ERROR );
        }
        //返回结果集
        Map<String,Object> result = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();

        // 获取高德URL和KEY
        Map<String, String> maps = getGdConfig();
        String url = maps.get(Constants.BD_MAP_URL).toString() + "/reverse_geocoding/v3/?";
        String key = maps.get(Constants.BD_MAP_KEY).toString();
        String lat = gisKeyWordQueryReq.getLat();
        String lon = gisKeyWordQueryReq.getLon();
        String location = lat + "," + lon; // 纬经度

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(OUTPUT,"json"); // json格式
        paramMap.put(LOCATION,location); // 经纬度
        paramMap.put(Constants.REQ_AK, key);
        paramMap.put("extensions_town","true"); //显示乡镇
        log.info("将坐标信息转化行政区域信息 {}",url);
        long oldTime = System.currentTimeMillis();
        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.BAIDU + Constants.COUNTRY_AREA_QUERY + Constants.RESPONSE_LOG + resStr);
        long nowTime = System.currentTimeMillis();
        log.info("将坐标信息转化行政区域信息返回时间 {}",(nowTime - oldTime) + "毫秒");

        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("0".equals(status)) {
            JSONObject job = json.getJSONObject(RESULT);

            JSONObject detail = job.getJSONObject("addressComponent");


            //获取省和国家编码
            String code = detail.getString(Constants.AD_CODE);
            if(StringUtils.isNotEmpty(code)){
                String jsonObject = JSON.toJSONString(sysClient.getAreaInfo(code).getData());
                SysArea cityArea = JSONObject.parseObject(jsonObject, SysArea.class);
                if(null != cityArea){
                    String cityCode = cityArea.getAreaParentCode();
                    result.put("cityCode",cityCode); // 城市编码

                    if(StringUtils.isNotEmpty(cityCode)){
                        String jsonObject1 = JSON.toJSONString(sysClient.getAreaInfo(cityCode).getData());
                        SysArea provinceArea = JSONObject.parseObject(jsonObject1, SysArea.class);//地区信息

                        if(null != provinceArea){
                            String provinceCode = provinceArea.getAreaParentCode();
                            result.put("provinceCode",provinceCode); // 省编码
                        }
                    }
                }


            }
            log.info("将坐标信息转化行政区域信息本地接口时间 {}",(System.currentTimeMillis() - nowTime) + "毫秒");
            result.put(PROVINCE,detail.get(PROVINCE));// 省
            result.put("city",detail.get("city")); // 市
            result.put("district",detail.get("district")); // 区
            result.put("town",detail.get("town")); //乡镇/社区
            result.put("townCode",detail.get("town_code"));
            result.put("districtCode",detail.get("adcode")); // 行政区编码
            result.put(LOCATION,location);

            return Result.data(result);
        } else {
            return Result.fail(Constants.BAIDU + Constants.ACOUNTRY_AREA_QUERY_ERROR);
        }
    }

    @Override
    public DriveNavigationRes driveNavigation(DriveNavigationReq req) {
        return null;
    }

    @Override
    public PedestrianNavigationRes pedestrianNavigation(PedestrianNavigationReq req) {
        return null;
    }

    @Override
    public BusNavigationRes busNavigation(BusNavigationReq req) {
        return null;
    }

    /**
     * 行政区域查询-百度
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result districtQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        return null;
    }
}
