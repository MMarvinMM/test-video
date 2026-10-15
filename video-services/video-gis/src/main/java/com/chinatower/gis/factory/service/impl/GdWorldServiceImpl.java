package com.chinatower.gis.factory.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.JsonUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.gis.api.entity.Constants;
import com.chinatower.gis.api.entity.DriveNavigationPaths;
import com.chinatower.gis.api.entity.DriveNavigationRes;
import com.chinatower.gis.api.entity.DriveNavigationSteps;
import com.chinatower.gis.api.entity.map.*;
import com.chinatower.gis.factory.service.GisMapService;
import com.chinatower.gis.utils.HttpUtils;
import com.chinatower.gis.utils.PositionUtil;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 高德实现POI
 * @data 创建时间：2023/10/16 14:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class GdWorldServiceImpl extends AbstractPoiBase implements GisMapService {


    public Map<String, String> getGdConfig() {
        Map<String, String> map = new HashMap<>();
        try {

            map.put(Constants.GD_MAP_URL, getConfigValue(Constants.GD_MAP_URL,"https://restapi.amap.com/v3"));
            map.put(Constants.GD_MAP_KEY, getConfigValue(Constants.GD_MAP_KEY,"d6dec8c590af05d092033afc2cd51a57"));

            return map;
        } catch (Exception e) {
            log.error("查询高德地址错误", e);
        }


        return map;
    }

    /**
     * @param req 请求参数
     * @return 关键字查询
     */
    @Override
    public PoiKeyWordQueryRes poiKeyWordQuery(GisPoiKeyWordQueryReq req) {
        GdKeyWords keyWords = getGdKeyWords(req);
        //正确/错误返回
        if (keyWords.getStatus() == 1) {
            if (keyWords.getPois() != null && !keyWords.getPois().isEmpty()) {
                return gdKeyWordQuery(keyWords);
            }
            //数据不存在默认取第一个城市数据
            if (!keyWords.getSuggestion().getCities().isEmpty()) {
                req.setAdCode(keyWords.getSuggestion().getCities().get(0).getAdcode());
                keyWords = getGdKeyWords(req);
                return gdKeyWordQuery(keyWords);
            }
            return new PoiKeyWordQueryRes();
        }
        throw new CustomException(keyWords.getInfo());
    }

    @Override
    public List<GisPoiInputtipsQueryRes> inputtipsQuery(GisPoiKeyWordQueryReq req){
        if (StringUtils.isEmpty(req.getKeyWord())) {
            throw new CustomException("参数错误");
        }
        // 获取高德URL和KEY
        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.GD_MAP_URL) + "/assistant/inputtips?";
        String key = map.get(Constants.GD_MAP_KEY);
        String keywords = req.getKeyWord(); //地址

        //请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keywords", keywords); // 关键字
        paramMap.put("datatype", "all"); // json格式
        paramMap.put("output", "JSON"); // json格式
        if (StringUtils.isNotEmpty(req.getLocation())){
            if (req.getParamType() != null && req.getParamType() == 0){
                req.setLocation(PositionUtil.wgs84toGcj02Str(req.getLocation()));
            }
            paramMap.put("location", req.getLocation()); // json格式
        }
        paramMap.put(Constants.REQ_KEY, key);
        //发送http请求
        String body = HttpRequest.get(url)
                .form(paramMap)
                .setConnectionTimeout(3000)
                .setReadTimeout(10000)
                .execute().body();

        log.info("高德输入提示返回 \r\n {}", body);

        JSONObject json = JSONObject.parseObject(body);
        String status = json.getString(Constants.WEATHER_STATUS);
        if ("1".equals(status)) {
            List<GisPoiInputtipsQueryRes> tips = json.parseArray(json.getString("tips"), GisPoiInputtipsQueryRes.class);
            for (GisPoiInputtipsQueryRes tip : tips) {
                if ("[]".equals(tip.getId())) {
                    tip.setId("");
                }
                if ("[]".equals(tip.getName())) {
                    tip.setName("");
                }
                if ("[]".equals(tip.getDistrict())) {
                    tip.setDistrict("");
                }
                if ("[]".equals(tip.getAdcode())) {
                    tip.setAdcode("");
                }
                if ("[]".equals(tip.getLocation())) {
                    tip.setLocation("");
                }
                if ("[]".equals(tip.getAddress())) {
                    tip.setAddress("");
                }
                if ("[]".equals(tip.getTypecode())) {
                    tip.setTypecode("");
                }
                if ("[]".equals(tip.getCity())) {
                    tip.setCity("");
                }
                if (req.getCoordType() != null && req.getCoordType() == 0 && StringUtils.isNotEmpty(tip.getLocation())){
                    req.setLocation(PositionUtil.gcj02toWgs84Str(tip.getLocation()));
                }
            }
            return tips;
        }
        return null;
    }

    private GdKeyWords getGdKeyWords(GisPoiKeyWordQueryReq req) {
        if (StringUtils.isEmpty(req.getKeyWord())) {
            throw new CustomException("参数错误");
        }
        // 获取高德URL和KEY
        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.GD_MAP_URL) + "/place/text?";
        String key = map.get(Constants.GD_MAP_KEY);
        String keywords = req.getKeyWord(); //地址

        //请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keywords", keywords); // 关键字
        paramMap.put("output", "JSON"); // json格式
        paramMap.put("extensions", "all"); // json格式
        paramMap.put("children", "1"); // json格式
        paramMap.put("page", req.getPageNum() + ""); // json格式
        paramMap.put("offset", req.getPageSize() + ""); // json格式
        if (StringUtils.isNotEmpty(req.getAdCode())) {
            SysArea area = getSysArea(req.getAdCode());
            if (area != null) {
                paramMap.put("city", area.getAreaName());
            } else {
                paramMap.put("city", req.getAdCode());
            }
//            paramMap.put("citylimit", "true");
        }
        if (StringUtils.isNotEmpty(req.getLocation())) {
            if (req.getParamType() == null || req.getParamType() == 0) {
                req.setLocation(PositionUtil.wgs84toGcj02Str(req.getLocation()));
            }
            PoiContraryGeocodingQueryReq queryReq = new PoiContraryGeocodingQueryReq();
            queryReq.setLocation(req.getLocation());
            queryReq.setParamType(1);
            //查询逆地理编码获取Code
            List<PoiContraryGeocodingQueryRes> res = poiContraryGeocodingQuery(queryReq);
            if (res != null && !res.isEmpty()) {
                PoiContraryGeocodingQueryRes queryRes = res.get(0);
                if (StringUtils.isNotEmpty(queryRes.getRegion().getProvinceCode())) {
                    paramMap.put("city", queryRes.getRegion().getProvinceCode());
//                    paramMap.put("citylimit", "true");
                }
            }
        }
        paramMap.put(Constants.REQ_KEY, key);
        //发送http请求
        String body = HttpRequest.get(url).form(paramMap).execute().body();

        log.info("高德关键字返回 \r\n {}", body);

        GdKeyWords keyWords = JSON.parseObject(body, GdKeyWords.class);
        return keyWords;
    }

    /**
     * @param keyWords
     * @return 解析补全数据
     */
    private PoiKeyWordQueryRes gdKeyWordQuery(GdKeyWords keyWords) {
        PoiKeyWordQueryRes queryRes = new PoiKeyWordQueryRes();
        queryRes.setTotal(keyWords.getCount());
        List<GisPoiKeyWordQueryRes> list = new LinkedList<>();
        for (GdKeyWordsPoi poi : keyWords.getPois()) {
            GisPoiKeyWordQueryRes res = new GisPoiKeyWordQueryRes();
            res.setAlias(poi.getName());
            res.setDistance(poi.getDistance());
            res.setTag(poi.getTag());
            res.setBrand(poi.getType());
            res.setBrandCode(poi.getTypeCode());
            Region region = new Region();
            region.setProvinceCode(poi.getPcode());
            if (StringUtils.isNotEmpty(poi.getCitycode()) && poi.getCitycode().length() >= 6) {
                region.setCityCode(poi.getCitycode());
                region.setCity(poi.getCityname());
            }
            region.setCountyCode(poi.getAdcode());
            region.setProvince(poi.getPname());
            region.setCounty(poi.getAdname());
            region.setLocation(PositionUtil.gcj02toWgs84Str(poi.getLocation()));
            perfectRegion(region);
            res.setRegion(region);

            List<Pois> pois = new LinkedList<>();

            {
                Pois build = Pois.builder()
                        .location(PositionUtil.gcj02toWgs84Str(poi.getLocation()))
                        .name(poi.getName())
                        .type(poi.getType())
                        .typecode(poi.getTypeCode())
                        .address(poi.getAddress())
                        .distance(poi.getDistance())
                        .nid(poi.getId())
                        .photos(poi.getPhotos())
                        .build();
                pois.add(build);
            }

            for (GdKeyWordsPoiChildren children : poi.getChildren()) {
                Pois build = Pois.builder()
                        .location(PositionUtil.gcj02toWgs84Str(children.getLocation()))
                        .name(children.getName())
                        .sname(children.getSname())
                        .type(children.getSubtype())
                        .typecode(children.getTypecode())
                        .address(children.getAddress())
                        .distance(children.getDistance())
                        .nid(children.getId())
                        .photos(poi.getPhotos())
                        .build();
                pois.add(build);
            }
            res.setPois(pois);
            list.add(res);
        }
        queryRes.setList(list);
        return queryRes;
    }

    @Override
    public PoiRadiusQueryResTableInfo poiRadiusQuery(PoiRadiusQueryReq req) {
        return null;
    }

    @Override
    public List<PoiPolygonQueryRes> poiPolygonQuery(PoiPolygonQueryReq req) {
        return null;
    }

    /**
     * @param req
     * @return 地理编码
     */
    @Override
    public List<PoiGeocodingQueryRes> poiGeocodingQuery(PoiGeocodingQueryReq req) {
        if (StringUtils.isEmpty(req.getAddress())) {
            throw new CustomException("参数错误");
        }
        // 获取高德URL和KEY
        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.GD_MAP_URL) + "/geocode/geo?";
        String key = map.get(Constants.GD_MAP_KEY);

        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("address", req.getAddress()); // 地址
        paramMap.put("output", "JSON"); // json格式
        paramMap.put(Constants.REQ_KEY, key);

        //发送http请求
        String body = HttpUtils.doGet(url, null, paramMap);
        log.info("高德POI地理编码搜索返回 {}", body);
        JSONObject json = JSON.parseObject(body);
        String status = json.getString(Constants.WEATHER_STATUS);

        List<PoiGeocodingQueryRes> list = new ArrayList<>();
        if ("1".equals(status)) {
            JSONArray array = JSON.parseArray(json.getString("geocodes"));
            JSONObject object = JSON.parseObject(array.get(0).toString());
            Region region = new Region();
            region.setCounty(object.getString("district"));
            region.setCountyCode(object.getString("adcode"));
            //完善区域
            perfectRegion(region);
            PoiGeocodingQueryRes res = BeanCopyUtils.copy(region, PoiGeocodingQueryRes.class);
            res.setFormattedAddress(object.getString("formatted_address"));
            res.setLocation(PositionUtil.gcj02toWgs84Str(object.getString("location")));
            list.add(res);
            return list;
        } else {
            throw new CustomException("查询地理编码失败");
        }
    }

    /**
     * @param req
     * @return 逆地理编码
     */
    @Override
    public List<PoiContraryGeocodingQueryRes> poiContraryGeocodingQuery(PoiContraryGeocodingQueryReq req) {
        if (StringUtils.isEmpty(req.getLocation())) {
            throw new CustomException("参数错误");
        }
        String location = req.getLocation();
        if (req.getParamType() == null || req.getParamType() == 0) {
            req.setLocation(PositionUtil.wgs84toGcj02Str(location));
        }
        List<PoiContraryGeocodingQueryRes> list = new LinkedList<>();
        // 获取高德URL和KEY
        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.GD_MAP_URL) + "/geocode/regeo?";
        String key = map.get(Constants.GD_MAP_KEY);
        //请求参数
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("location", req.getLocation()); //经纬度
        paramMap.put("output", "JSON"); // json格式
        paramMap.put("extensions", "all"); // 返回结果控制
        paramMap.put("roadlevel", "0"); // 可选值：1，当roadlevel=1时，过滤非主干道路，仅输出主干道路数据
        paramMap.put("radius", req.getRadius() + ""); // json格式
        paramMap.put(Constants.REQ_KEY, key);

        //发送http请求
        String body = HttpUtils.doGet(url, null, paramMap);
        log.info("高德逆地理编码 \r\n {}", body);

        GdGeocode gdGeocode = JSON.parseObject(body, GdGeocode.class);
        if (gdGeocode.getStatus() == 1) {
            GdGeocodeRegeo regeocode = gdGeocode.getRegeocode();
            PoiContraryGeocodingQueryRes queryRes = new PoiContraryGeocodingQueryRes();
            queryRes.setLocation(location);
            if (StringUtils.isNotEmpty(regeocode.getFormattedAddress()) && !"[]".equals(regeocode.getFormattedAddress())) {
                queryRes.setAddress(regeocode.getFormattedAddress());
            }
            JSONObject obj = JSON.parseObject(regeocode.getAddressComponent().toString());
            Region region = new Region();
            region.setCountyCode(obj.getString("adcode"));
            region.setCounty(obj.getString("district"));
            region.setTownCode(obj.getString("towncode"));
            region.setTown(obj.getString("township"));
            perfectRegion(region);
            queryRes.setRegion(region);
            List<Pois> pois = new LinkedList<>();
            for (GdGeocodeRegeoPoi poi : regeocode.getPois()) {
                Pois build = Pois.builder()
                        .address(poi.getAddress())
                        .location(PositionUtil.gcj02toWgs84Str(poi.getLocation()))
                        .name(poi.getName())
                        .type(poi.getType())
                        .distance(poi.getDistance())
                        .nid(poi.getId())
                        .build();
                pois.add(build);
            }
            queryRes.setPois(pois);
            list.add(queryRes);
            return list;
        } else {
            throw new CustomException(gdGeocode.getInfo());
        }

    }

    private GdDistricts gdPoiAreaQuery(PoiAreaQueryReq req) {
        // 获取高德URL和KEY

        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.GD_MAP_URL) + "/config/district?";
        String key = map.get(Constants.GD_MAP_KEY);

        //请求参数
        Map<String, Object> paramMap = new HashMap<>();
        if (StringUtils.isNotEmpty(req.getKeyWord())) {
            paramMap.put(Constants.KEYWORDS, req.getKeyWord());
        } else {
            paramMap.put(Constants.KEYWORDS, req.getAdCode());
        }
        paramMap.put(Constants.SUBDISTRICT, "1");
        paramMap.put(Constants.EXTENSIONS, "all");
        paramMap.put(Constants.REQ_KEY, key);
        //发送http请求
        log.info("查询行政区划的地址 {}  参数 {}", url, paramMap);
        long oldTime = System.currentTimeMillis();
        String body = HttpRequest.get(url).form(paramMap).execute().body();
        log.info("高德行政区域返回 {}", body);
        long nowTime = System.currentTimeMillis();
        log.info("查询行政区划的返回 {}  时间 {}", url, (nowTime - oldTime) + "毫秒");
        JSONObject json = JSONObject.parseObject(body);
        String status = json.getString(Constants.WEATHER_STATUS);
        if ("1".equals(status)) {
            List<GdDistricts> gdDistricts = json.parseArray(json.getString(Constants.DISTRICTS), GdDistricts.class);
            if (gdDistricts != null && !gdDistricts.isEmpty()) {
                return gdDistricts.get(0);
            }
            return null;
        }
        throw new CustomException("未查询到行政区域信息");
    }

    /**
     * @param req
     * @return 行政区域
     */
    @Override
    public PoiAreaQueryRes poiAreaQuery(PoiAreaQueryReq req) {
        if (StringUtils.isEmpty(req.getKeyWord()) && StringUtils.isEmpty(req.getAdCode())) {
            throw new CustomException("关键字/区域编码必传一个!");
        }

        String areaQueryKey = "poiAreaQuery:";
        if (req.getRedisClearFlag()){
            log.info("GIS边界数据redis缓存 {}", areaQueryKey);
            RedisUtil.delete(areaQueryKey + ":*", RedisDBType.PRIMARY);
            return null;
        }
        if (StringUtils.isNotEmpty(req.getKeyWord())) {
            areaQueryKey = areaQueryKey + req.getKeyWord();
        } else {
            areaQueryKey = areaQueryKey + req.getAdCode();
        }
        areaQueryKey = areaQueryKey + ":" + req.getNextPolygonFlag();
        if (req.getHasCache() == 1) {
            log.info("清除行政区域缓存 {}", areaQueryKey);
            RedisUtil.delete(areaQueryKey, RedisDBType.PRIMARY);
        }

        boolean match = Arrays.stream(PROVINCE_CODES).anyMatch(s -> s.equals(req.getAdCode()));
        PoiAreaQueryRes queryRes = getArea(req.getAdCode());
        if (match && req.getHasCache() == 0 && queryRes != null) {
            return queryRes;
        }

        String body = RedisUtil.get(areaQueryKey, RedisDBType.PRIMARY);
        if (StringUtils.isNotEmpty(body) && req.getHasCache() == 0) {
            PoiAreaQueryRes res = JSON.parseObject(body, PoiAreaQueryRes.class);
            log.info("返回的缓存的区域边界数据 {}", areaQueryKey);
            if (match) {
                putArea(req.getAdCode(), res);
            }
            return res;
        }
        //全国数据
        if (COUNTRY_CODE.equals(req.getAdCode())) {
            return poiAreaCountyQuery(areaQueryKey,req, this::nextPolygon);
        }

        GdDistricts districts = gdPoiAreaQuery(req);
        if (districts == null) {
            SysArea area = getSysArea(req.getAdCode());
            if (area != null) {
                PoiAreaQueryRes copy = new PoiAreaQueryRes();
                if ("5".equals(area.getAreaLevel())) {
                    copy.setLevel("town");
                    copy.setTownCode(area.getAreaCode());
                    copy.setTown(area.getAreaName());
                } else if ("4".equals(area.getAreaLevel())) {
                    copy.setLevel("district");
                    copy.setCountyCode(area.getAreaCode());
                    copy.setCounty(area.getAreaName());
                } else if ("3".equals(area.getAreaLevel())) {
                    copy.setLevel("city");
                    copy.setCityCode(area.getAreaCode());
                    copy.setCity(area.getAreaName());
                } else if ("2".equals(area.getAreaLevel())) {
                    copy.setLevel("province");
                    copy.setProvinceCode(area.getAreaCode());
                    copy.setProvince(area.getAreaName());
                }
                copy.setName(area.getAreaName());
                copy.setLocation(area.getLongitude() + "," + area.getLatitude());
                copy.setPolygon("");
                perfectRegion(copy);
                String adCode = copy.getCountyCode();
                if (StringUtils.isEmpty(adCode)) {
                    adCode = copy.getCityCode();
                }
                req.setAdCode(adCode);
                GdDistricts areaQuery = gdPoiAreaQuery(req);
                if (areaQuery != null) {
                    String polygon = areaQuery.getPolyline();
                    copy.setPolygon(gdToMapPolyon(polygon));
                }
                if (match) {
                    putArea(req.getAdCode(), copy);
                } else {
                    RedisUtil.set(areaQueryKey, JSON.toJSONString(copy), 1, TimeUnit.DAYS, RedisDBType.PRIMARY);
                }
                return copy;
            }
            throw new CustomException("未查询到行政区域信息");
        }
        PoiAreaQueryRes res = new PoiAreaQueryRes();
        res.setName(districts.getName());
        res.setLevel(districts.getLevel());

        poiAreaQueryFill(res, districts);

        perfectRegion(res);

        poiAreaQueryChildFill(res, districts);


        if (districts.getDistricts() != null && !districts.getDistricts().isEmpty()) {
            List<District> list = BeanCopyUtils.copyList(districts.getDistricts(), District.class);
            list.forEach(district -> {
                district.setCenter(PositionUtil.gcj02toWgs84Str(districts.getCenter()));
            });
            if (Boolean.TRUE.equals(req.getNextPolygonFlag()) && nextPolygonLevel.contains(districts.getLevel())) {
                ThreadParallelUtil.run(list, district -> {
                    nextPolygon(district);
                    return null;
                }, null);
            }
            res.setDistricts(list);

        }


        res.setLocation(PositionUtil.gcj02toWgs84Str(districts.getCenter()));
        String polygon = districts.getPolyline();
        res.setPolygon(gdToMapPolyon(polygon));
        if (match) {
            putArea(req.getAdCode(), res);
        } else {
            RedisUtil.set(areaQueryKey, JSON.toJSONString(res), 1, TimeUnit.DAYS, RedisDBType.PRIMARY);
        }
        return res;
    }

    /**
     * 下级行政边界数据
     *
     * @param district
     */
    private void nextPolygon(District district) {
        PoiAreaQueryReq req = new PoiAreaQueryReq();
        req.setAdCode(district.getAdCode());
        GdDistricts gdDistricts = gdPoiAreaQuery(req);
        if (gdDistricts != null) {
            String polygon = gdDistricts.getPolyline();
            district.setPolygon(gdToMapPolyon(polygon));
        }
    }

    /**
     * @param req 请求参数
     * @return 轨迹查询
     */
    @Override
    public TrajectoryQueryRes trajectoryQuery(TrajectoryQueryReq req) {
        if (StringUtils.isEmpty(req.getOrigin())
                || StringUtils.isEmpty(req.getEnd())) {
            throw new CustomException(Constants.PARAMS_ERROR);
        }
        String origin = req.getOrigin();
        String end = req.getEnd();

        if (req.getParamType() == 0) {
            req.setOrigin(PositionUtil.wgs84toGcj02Str(origin));
            req.setEnd(PositionUtil.wgs84toGcj02Str(end));
        }

        DriveNavigationRes driveNavigation = driveNavigation(req);
        TrajectoryQueryRes res = new TrajectoryQueryRes();
        res.setCount(driveNavigation.getPaths().size());

        List<Paths> paths = new LinkedList<>();
        for (DriveNavigationPaths path : driveNavigation.getPaths()) {
            List<Routs> routs = new ArrayList<>();
            Paths onePath = new Paths();
            for (DriveNavigationSteps step : path.getSteps()) {
                Routs rout = new Routs();
                rout.setDistance(step.getStepDistance());
                rout.setPolyline(gdToMapPolyon(step.getPolyline()));
                rout.setRoadname(step.getRoadName());
                rout.setAction(step.getInstruction());
                routs.add(rout);
            }
            onePath.setDistance(path.getDistance());
            onePath.setRestriction(path.getRestriction());
            onePath.setSteps(routs);
            paths.add(onePath);
        }
        List<Paths> list = paths.stream().sorted(Comparator.comparing(Paths::getDistance)).collect(Collectors.toList());
        res.setRouts(list);
        return res;
    }


    public DriveNavigationRes driveNavigation(TrajectoryQueryReq req) {
        if (StringUtils.isEmpty(req.getOrigin())
                || StringUtils.isEmpty(req.getEnd())) {
            throw new CustomException(Constants.PARAMS_ERROR);
        }
        // 获取高德URL和KEY

        Map<String, String> configMap = getGdConfig();
        String url = configMap.get(Constants.GD_MAP_URL) + "/direction/driving";
        String key = configMap.get(Constants.GD_MAP_KEY);

        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("origin", req.getOrigin());
        map.put("destination", req.getEnd());
        map.put("extensions", "all");
        map.put("show_fields", "polyline");
        String body = HttpRequest.get(url.replace("v3", "v5")).form(map).execute().body();
        log.info("驾驶导航信息返回{}", body);
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

}
