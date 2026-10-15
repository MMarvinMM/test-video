package com.chinatower.gis.modules.gis.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisMapServiceAPI;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.factory.service.impl.MapWorldServiceImpl;
import com.chinatower.gis.utils.HttpUtils;
import com.chinatower.platform.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GisTianService implements GisMapServiceAPI {

    private static final String PROVINCE = "province";

    @Resource
    MapWorldServiceImpl mapWorldService;

    /**
     * 天地图关键字搜索
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
        Map<String, String> maps = mapWorldService.getGdConfig();
        String url = maps.get(Constants.TD_MAP_URL).toString() + "/v2/search?";
        String key = maps.get(Constants.TD_MAP_KEY).toString();
        String keywords = gisKeyWordQueryReq.getKeyWord(); // 关键字

        JSONObject reqJob = new JSONObject();
        reqJob.put("keyWord", keywords);
        reqJob.put("level", 12);
        reqJob.put("mapBound", "-180,-90,180,90");
        reqJob.put("queryType", 1);
        reqJob.put("start", 0);
        reqJob.put("count", 10);

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("postStr", reqJob.toJSONString());
        paramMap.put("type", "query");
        paramMap.put(Constants.REQ_TK, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.TIAN + Constants.KEY_WORD_QUERY + Constants.RESPONSE_LOG + resStr);


        JSONObject json = JSONObject.parseObject(resStr);
        JSONObject statusJob = json.getJSONObject(Constants.WEATHER_STATUS);
        String infocode = statusJob.getString("infocode");
        //正确/错误返回
        if ("1000".equals(infocode)) {
            String resultType = json.getString("resultType");
            //poi搜索结果
            if ("1".equals(resultType)) {
                JSONArray jarr = json.getJSONArray("pois");
                result.put("count", jarr.size());

                for (int i = 0; i < jarr.size(); i++) {
                    Map<String, Object> map = new HashMap<>();
                    JSONObject job = jarr.getJSONObject(i);

                    map.put("name", job.get("name")); // 名称
                    map.put("location", job.get("lonlat")); // 经纬度
                    map.put("pname", job.get(PROVINCE)); // 所在省份名称
                    map.put("cityname", job.get("city")); // 城市名
                    map.put("adname", job.get("county")); // 所在区县名称
                    map.put("address", job.get("address")); // 详细地址
                    list.add(map);
                }
                result.put("result", list);
                return Result.data(result);
            } else {


                return Result.fail(Constants.TIAN + Constants.KEY_WORD_QUERY_EMPTY);
            }

        } else {
            return Result.fail(Constants.TIAN + Constants.KEY_WORD_QUERY_ERROR);
        }
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
        List<Map<String, Object>> list = new ArrayList<>();

        // 获取高德URL和KEY
        Map<String, String> maps = mapWorldService.getGdConfig();
        String url = maps.get(Constants.TD_MAP_URL).toString() + "/geocoder?";
        String key = maps.get(Constants.TD_MAP_KEY).toString();
        String lat = gisKeyWordQueryReq.getLat();
        String lon = gisKeyWordQueryReq.getLon();

        JSONObject reqJob = new JSONObject();
        reqJob.put("lon", lon);
        reqJob.put("lat", lat);
        reqJob.put("ver", 1);

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("type", "geocode");
        paramMap.put("postStr", reqJob.toJSONString());
        paramMap.put(Constants.REQ_TK, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.TIAN + Constants.CODE_TO_ADDRESS + Constants.RESPONSE_LOG + resStr);


        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("0".equals(status)) {
            JSONObject job = json.getJSONObject("result");

            JSONObject detail = job.getJSONObject("addressComponent");

            result.put(Constants.RES_FORMATTED_ADDRESS, job.get(Constants.FORMATTED_ADDRESS)); // 地址
            result.put(PROVINCE, detail.get(PROVINCE));
            result.put(Constants.CITY, detail.get(Constants.CITY)); // 市
            result.put(Constants.DISTRICT, detail.get("county")); // 区
//            result.put(Constants.TOWNSHIP,detail.get("town")); //乡镇/社区
//            result.put(Constants.STREET_NUMBER,detail.getString("road") ); // 门牌
            return Result.data(result);
        } else {
            return Result.fail(Constants.TIAN + Constants.CODE_TO_ADDRESS_ERROR);
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
        Map<String, String> maps = mapWorldService.getGdConfig();
        String url = maps.get(Constants.TD_MAP_URL).toString() + "/geocoder?";
        String key = maps.get(Constants.TD_MAP_KEY).toString();
        String address = gisKeyWordQueryReq.getAddress(); //地址

        JSONObject reqJob = new JSONObject();
        reqJob.put("keyWord", address);


        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("ds", reqJob.toJSONString());
        paramMap.put(Constants.REQ_TK, key);

        //发送http请求
        String resStr = HttpUtils.doGet(url, null, paramMap);
        log.info(Constants.TIAN + Constants.ADDRESS_TO_CODE + Constants.RESPONSE_LOG + resStr);

        JSONObject json = JSONObject.parseObject(resStr);
        String status = json.getString(Constants.WEATHER_STATUS);
        //正确/错误返回
        if ("0".equals(status)) {
            Map<String, Object> map = new HashMap<>();
            JSONObject job = json.getJSONObject("location");
            map.put("lon", job.getString("lon")); // 经度
            map.put("lat", job.getString("lat")); // 纬度
            result.add(map);
            return Result.data(result);
        } else {
            return Result.fail(Constants.TIAN + Constants.ADDRESS_TO_CODE_ERROR);
        }

    }

    @Override
    public Result weatherQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        return null;
    }

    @Override
    public Result coordinateTransformation(GisKeyWordQueryReq gisKeyWordQueryReq) {
        return null;
    }


    /**
     * 天地图 -- 将坐标信息转化行政区域信息
     *
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result countryAreaQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        return null;
    }

    @Override
    public DriveNavigationRes driveNavigation(DriveNavigationReq req) {
        if (StringUtils.isEmpty(req.getOrigin())
                || StringUtils.isEmpty(req.getDestination())
                || StringUtils.isEmpty(req.getMapType())) {
            throw new CustomException(Constants.PARAMS_ERROR);
        }

        // 获取天地图URL和KEY
        Map<String, String> maps = mapWorldService.getGdConfig();
        String url = maps.get(Constants.TD_MAP_URL).toString() + "/drive?type=search&";
        String key = maps.get(Constants.TD_MAP_KEY).toString();

        JSONObject reqJob = new JSONObject();
        reqJob.put("orig", req.getOrigin());
        reqJob.put("dest", req.getDestination());

        url = url + "postStr=" + reqJob.toJSONString() + "&tk=" + key;
        String body = HttpRequest.get(url).execute().body();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TianDiTuDriveNavigationRes.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            TianDiTuDriveNavigationRes res = (TianDiTuDriveNavigationRes) jaxbUnmarshaller.unmarshal(StrUtil.getReader(body));
//            return res;
        } catch (Exception e) {
            log.error("天地图驾驶导航错误", e);
        }

        throw new CustomException("天地图驾驶导航错误");
    }

//    public static void main(String[] args) {
//        String url = "http://api.tianditu.gov.cn/drive?tk=54445c5adf7947eb7a3e68475859eabd&type=search&postStr=";
//        JSONObject object = new JSONObject();
//        object.put("orig", "106.56,29.47");
//        object.put("dest", "106.59,29.49");
//        url = url + object.toJSONString();
//        System.out.println(url);
//        String body = HttpRequest.get(url).execute().body();
//        System.out.println(body);
//    }

    @Override
    public PedestrianNavigationRes pedestrianNavigation(PedestrianNavigationReq req) {
        return null;
    }

    @Override
    public BusNavigationRes busNavigation(BusNavigationReq req) {
        return null;
    }


    /**
     * 行政区域查询-天地图
     *
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result districtQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        return null;
    }
}
