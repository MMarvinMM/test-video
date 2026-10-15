package com.chinatower.gis.factory.service.impl;

import cn.hutool.core.util.StrUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.utils.HttpUtils;
import com.chinatower.gis.utils.PositionUtil;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.gis.api.entity.map.Region;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.gis.api.entity.map.*;
import com.chinatower.gis.factory.service.GisMapService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.system.api.model.SysCapitalVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 天地图实现
 * @data 创建时间：2023/8/15 13:55
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class MapWorldServiceImpl extends AbstractPoiBase implements GisMapService {

    public Map<String, String> getGdConfig() {

        Map<String, String> map = new HashMap<>();
        try {
            map.put(Constants.TD_MAP_URL, getConfigValue(Constants.TD_MAP_URL,"http://api.tianditu.gov.cn"));
            map.put(Constants.TD_MAP_KEY, getConfigValue(Constants.TD_MAP_KEY,"4c051d23bae23b679b38c127dd90b1f6"));
            return map;
        } catch (Exception e) {
            log.error("查询天地图地址错误", e);
        }

        return map;
    }

    /**
     * @param req 请求参数
     * @return 关键字搜索接口
     */
    private MapWorldKeyWordResult poiKeyWordQueryBody(GisPoiKeyWordQueryReq req) {
        if (StringUtils.isEmpty(req.getKeyWord())) {
            throw new CustomException("参数错误");
        }

        Map<String, String> map = getGdConfig();

        String url = map.get(Constants.TD_MAP_URL).toString() + "/v2/search";
        String key = map.get(Constants.TD_MAP_KEY).toString();

        JSONObject reqJob = new JSONObject();
        //searchType 1-关键字 0-区域编码
        reqJob.put("keyWord", req.getKeyWord());
        if (StringUtils.isNotEmpty(req.getLocation())) {
            reqJob.put("mapBound", req.getLocation() + "," + req.getLocation());
        } else {
            reqJob.put("mapBound", "-180,-90,180,90");
        }
        if (StringUtils.isNotEmpty(req.getAdCode())) {
            reqJob.put("specify", "156" + req.getAdCode());
        }
        reqJob.put("level", req.getLevel() == null ? "18" : req.getLevel());
        reqJob.put("queryType", "1");
        reqJob.put("show", "2");
        reqJob.put("start", req.getPageNum() - 1);
        reqJob.put("count", req.getPageSize());
        if (StringUtils.isNotEmpty(req.getPoiTypes())) {
            reqJob.put("poiTypes", req.getPoiTypes());
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("postStr", reqJob.toJSONString());
        paramMap.put("type", "query");
        paramMap.put(Constants.REQ_TK, key);
        String body = HttpUtils.doGet(url, null, paramMap);

        log.info("天地图POI关键字搜索返回 {}", body);
        MapWorldKeyWordResult result = JSON.parseObject(body, MapWorldKeyWordResult.class);
        return result;
    }

    /**
     * @param req 请求参数
     * @return POI-关键字查询
     */
    @Override
    public PoiKeyWordQueryRes poiKeyWordQuery(GisPoiKeyWordQueryReq req) {
        try {
            MapWorldKeyWordResult result = poiKeyWordQueryBody(req);
            Integer resultCount = result.getCount() > 100 ? 100 : result.getCount();
            if (result.getStatus().getInfocode() == 1000) {
                List<GisPoiKeyWordQueryRes> list = mapWorldQueryKeywords(result);
                PoiKeyWordQueryRes queryRes = new PoiKeyWordQueryRes();
                queryRes.setList(list);
                queryRes.setTotal(resultCount);
                return queryRes;
                //判断是否是统计
            }else {
                throw new CustomException(result.getStatus().getCndesc());
            }
        } catch (CustomException e) {
            log.error("查询关键字错误", e);
            throw e;
        } catch (Exception e) {
            log.error("查询关键字错误", e);
        }
        throw new CustomException("查询关键字错误");
    }


    /**
     * 递归查询关键字搜素
     *
     * @param list
     * @param req
     * @param result
     */
    public void mapWorldQueryKeywordsBody(List<GisPoiKeyWordQueryRes> list, GisPoiKeyWordQueryReq req, MapWorldKeyWordResult result) {
        if (list.size() >= req.getPageNum() * req.getPageSize()) {
            return;
        }
        //判断是否是统计
        if ("2".equals(result.getResultType())) {
            MapWorldKeyWordResultStatistics statistics = result.getStatistics();
            List<StatisticsPriorityCitys> priorityCitys = statistics.getPriorityCitys();
            for (StatisticsPriorityCitys priority : priorityCitys) {
                if (list.size() >= req.getPageNum() * req.getPageSize()) {
                    return;
                }
                int pageNum = req.getPageNum();
                int pageSize = req.getPageSize();
                req.setAdCode(priority.getAdminCode().substring(3));
                MapWorldKeyWordResult body = poiKeyWordQueryBody(req);
                if (body.getStatus().getInfocode() == 2007) {
                    req.setPageNum(1);
                    req.setPageSize(10);
                    body = poiKeyWordQueryBody(req);

                    req.setPageNum(pageNum);
                    req.setPageSize(pageSize);
                }
                if ("1".equals(body.getResultType())) {

                    int count = priority.getCount() / 10 + 1;
                    int num = 1;
                    while (num <= count) {
                        req.setPageNum(num);
                        req.setPageSize(10);
                        list.addAll(mapWorldQueryKeywords(poiKeyWordQueryBody(req)));
                        num++;
                        if (list.size() >= pageNum * pageSize) {
                            return;
                        }
                    }
                }
                if (list.size() >= req.getPageNum() * req.getPageSize()) {
                    return;
                }
                mapWorldQueryKeywordsBody(list, req, body);
            }
        } else if ("1".equals(result.getResultType())) {
            list.addAll(mapWorldQueryKeywords(result));
        } else if ("3".equals(result.getResultType())) {
            MapWorldKeyWordResultArea area = result.getArea();
            PoiRadiusQueryReq poiRadiusQueryReq = new PoiRadiusQueryReq();
            poiRadiusQueryReq.setKeyWord(result.getKeyWord());
            poiRadiusQueryReq.setLocation(area.getLonlat());
            poiRadiusQueryReq.setEncryptRequestFlag(req.getEncryptRequestFlag());
            PoiRadiusQueryResTableInfo resList = poiRadiusQuery(poiRadiusQueryReq);
            List<GisPoiKeyWordQueryRes> res = BeanCopyUtils.copyList(resList.getList(), GisPoiKeyWordQueryRes.class);
            list.addAll(res);
        }
    }

    /**
     * @return 天地图搜索关键字
     */
    public List<GisPoiKeyWordQueryRes> mapWorldQueryKeywords(MapWorldKeyWordResult result) {
        List<GisPoiKeyWordQueryRes> list = new ArrayList<>();
        List<MapWorldKeyWordResultPois> pois = result.getPois();
        if (pois != null && !pois.isEmpty()) {
            for (MapWorldKeyWordResultPois keyWordResultPois : pois) {
                GisPoiKeyWordQueryRes res = new GisPoiKeyWordQueryRes();
                List<Pois> ps = new ArrayList<>();
                Pois p = Pois.builder()
                        .location(keyWordResultPois.getLonlat())
                        .name(keyWordResultPois.getName())
                        .sname(keyWordResultPois.getEname())
                        .address(keyWordResultPois.getAddress())
                        .distance(keyWordResultPois.getDistance())
                        .nid(keyWordResultPois.getElemid())
                        .type(keyWordResultPois.getTypeName())
                        .typecode(keyWordResultPois.getTypeCode())
                        .build();
                res.setAlias(keyWordResultPois.getName());
                res.setHit(keyWordResultPois.getHotPointID());
                res.setBrandCode("");
                res.setBrand("");
                res.setTag("");
                res.setDistance(keyWordResultPois.getDistance());
                Region region = BeanCopyUtils.copy(keyWordResultPois, Region.class);
                region.setCountyCode(region.getCountyCode());
                region.setCityCode(region.getCityCode());
                region.setProvinceCode(region.getProvinceCode());
                //完善区域编码
                perfectRegion(region);
                res.setRegion(region);
                ps.add(p);
                res.setPois(ps);
                list.add(res);
            }
        }
        return list;
    }

    @Override
    public PoiRadiusQueryResTableInfo poiRadiusQuery(PoiRadiusQueryReq req) {
        if (StringUtils.isEmpty(req.getLocation()) || StringUtils.isEmpty(req.getKeyWord())) {
            throw new CustomException("参数错误");
        }

        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.TD_MAP_URL).toString() + "/v2/search";
        String key = map.get(Constants.TD_MAP_KEY).toString();
        JSONObject reqJob = new JSONObject();
        //searchType 1-关键字 0-区域编码
        reqJob.put("keyWord", req.getKeyWord());
        reqJob.put("pointLonlat", req.getLocation());
        reqJob.put("queryRadius", req.getRadius());
        reqJob.put("level", req.getLevel() == null ? "18" : req.getLevel());
        reqJob.put("queryType", "3");
        reqJob.put("show", "2");
        reqJob.put("start", req.getPageNum());
        reqJob.put("count", req.getPageSize());
        if (StringUtils.isNotEmpty(req.getPoiTypes())) {
            reqJob.put("poiTypes", req.getPoiTypes());
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("postStr", reqJob.toJSONString());
        paramMap.put("type", "query");
        paramMap.put(Constants.REQ_TK, key);
        String body = HttpUtils.doGet(url, null, paramMap);
        log.info("天地图POI半径搜索返回 {}", body);

        MapWorldKeyWordResult result = JSON.parseObject(body, MapWorldKeyWordResult.class);
            if (result.getStatus().getInfocode() == 1000) {
            List<PoiRadiusQueryRes> list = new ArrayList<>();
            List<MapWorldKeyWordResultPois> pois = result.getPois();
            if (pois != null && !pois.isEmpty()) {
                for (MapWorldKeyWordResultPois keyWordResultPois : pois) {
                    PoiRadiusQueryRes res = new PoiRadiusQueryRes();
                    List<Pois> ps = new ArrayList<>();
                    Pois p = Pois.builder()
                            .location(keyWordResultPois.getLonlat())
                            .name(keyWordResultPois.getName())
                            .sname(keyWordResultPois.getEname())
                            .address(keyWordResultPois.getAddress())
                            .distance(keyWordResultPois.getDistance())
                            .nid(keyWordResultPois.getElemid())
                            .type(keyWordResultPois.getTypeName())
                            .typecode(keyWordResultPois.getTypeCode())
                            .build();
                    res.setAlias(keyWordResultPois.getName());
                    res.setHit(keyWordResultPois.getHotPointID());
                    res.setBrandCode("");
                    res.setBrand("");
                    res.setTag("");
                    res.setDistance(keyWordResultPois.getDistance());
                    Region region = BeanCopyUtils.copy(keyWordResultPois, Region.class);
                    region.setCountyCode(region.getCountyCode());
                    region.setCityCode(region.getCityCode());
                    region.setProvinceCode(region.getProvinceCode());
                    //完善区域编码
                    perfectRegion(region);
                    res.setRegion(region);
                    ps.add(p);
                    res.setPois(ps);
                    list.add(res);
                }
            }
            PoiRadiusQueryResTableInfo info = new PoiRadiusQueryResTableInfo();
            info.setTotal(result.getCount() > 100 ? 100 : result.getCount());
            info.setList(list);
            return info;
        } else {
            throw new CustomException(result.getStatus().getCndesc());
        }
    }

    @Override
    public List<PoiPolygonQueryRes> poiPolygonQuery(PoiPolygonQueryReq req) {
        if (StringUtils.isEmpty(req.getPolygon()) || StringUtils.isEmpty(req.getKeyWord())) {
            throw new CustomException("参数错误");
        }
        req.setPolygon(req.getPolygon().replace(";",","));

        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.TD_MAP_URL).toString() + "/v2/search";
        String key = map.get(Constants.TD_MAP_KEY).toString();
        JSONObject reqJob = new JSONObject();
        //searchType 1-关键字 0-区域编码
        reqJob.put("keyWord", req.getKeyWord());
        reqJob.put("polygon", req.getPolygon());
        reqJob.put("queryType", "10");
        reqJob.put("show", "2");
        reqJob.put("start", req.getPageNum());
        reqJob.put("count", req.getPageSize());
//        if (StringUtils.isNotEmpty(req.getQueryType())) {
//            reqJob.put("dataTypes", req.getQueryType());
//        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("postStr", reqJob.toJSONString());
        paramMap.put("type", "query");
        paramMap.put(Constants.REQ_TK, key);
        String body = HttpUtils.doGet(url, null, paramMap);

        log.info("天地图POI多边形搜索返回 {}", body);

        MapWorldKeyWordResult result = JSON.parseObject(body, MapWorldKeyWordResult.class);
        if (result.getStatus().getInfocode() == 1000) {
            List<PoiPolygonQueryRes> list = new ArrayList<>();
            List<MapWorldKeyWordResultPois> pois = result.getPois();
            if (pois != null && !pois.isEmpty()) {
                for (MapWorldKeyWordResultPois keyWordResultPois : pois) {
                    PoiPolygonQueryRes res = new PoiPolygonQueryRes();
                    List<Pois> ps = new ArrayList<>();
                    Pois p = Pois.builder()
                            .location(keyWordResultPois.getLonlat())
                            .name(keyWordResultPois.getName())
                            .sname(keyWordResultPois.getEname())
                            .address(keyWordResultPois.getAddress())
                            .distance(keyWordResultPois.getDistance())
                            .nid(keyWordResultPois.getElemid())
                            .type(keyWordResultPois.getTypeName())
                            .typecode(keyWordResultPois.getTypeCode())
                            .build();
                    res.setAlias(keyWordResultPois.getName());
                    res.setHit(keyWordResultPois.getHotPointID());
                    res.setBrandCode("");
                    res.setBrand("");
                    res.setTag("");
                    res.setDistance(keyWordResultPois.getDistance());
                    Region region = BeanCopyUtils.copy(keyWordResultPois, Region.class);
                    region.setCountyCode(region.getCountyCode());
                    region.setCityCode(region.getCityCode());
                    region.setProvinceCode(region.getProvinceCode());
                    //完善区域编码
                    perfectRegion(region);
                    res.setRegion(region);
                    ps.add(p);
                    res.setPois(ps);
                    list.add(res);
                }
            }
            return list;
        } else {
            throw new CustomException(result.getStatus().getCndesc());
        }
    }

    @Override
    public List<PoiGeocodingQueryRes> poiGeocodingQuery(PoiGeocodingQueryReq req) {
        if (StringUtils.isEmpty(req.getAddress())) {
            throw new CustomException("参数错误");
        }

        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.TD_MAP_URL).toString() + "/geocoder";
        String key = map.get(Constants.TD_MAP_KEY).toString();
        JSONObject reqJob = new JSONObject();
        reqJob.put("keyWord", req.getAddress());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("ds", reqJob.toJSONString());
        paramMap.put(Constants.REQ_TK, key);
        String body = HttpUtils.doGet(url, null, paramMap);

        log.info("天地图POI地理编码搜索返回 {}", body);
        JSONObject json = JSON.parseObject(body);
        String status = json.getString(Constants.WEATHER_STATUS);

        List<PoiGeocodingQueryRes> list = new ArrayList<>();
        if ("0".equals(status)) {
            JSONObject job = json.getJSONObject("location");
            MapWorldLonLatResult result = LonLatQuery(job.getString("lon"), job.getString("lat"));
            Region region = BeanCopyUtils.copy(result.getAddressComponent(), Region.class);

            if (StringUtils.isNotEmpty(region.getProvinceCode())){
                region.setProvinceCode(region.getProvinceCode().replace("156",""));
            }
            if (StringUtils.isNotEmpty(region.getCityCode())){
                region.setCityCode(region.getCityCode().replace("156",""));
            }
            if (StringUtils.isNotEmpty(region.getCountyCode())){
                region.setCountyCode(region.getCountyCode().replace("156",""));
            }
            if (StringUtils.isNotEmpty(region.getTownCode())){
                region.setTownCode(padRightZero12(region.getTownCode().replace("156","")));
            }

            //完善区域
            perfectRegion(region);
            PoiGeocodingQueryRes res = BeanCopyUtils.copy(region, PoiGeocodingQueryRes.class);
            res.setFormattedAddress(req.getAddress());
            res.setLocation(job.getString("lon") + "," + job.getString("lat"));
            list.add(res);
            return list;
        } else {
            throw new CustomException("查询地理编码失败");
        }
    }




    /**
     * @param lon 经度
     * @param lat 纬度
     * @return 逆地理编码查询
     */
    public MapWorldLonLatResult LonLatQuery(String lon, String lat) {

        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.TD_MAP_URL).toString() + "/geocoder";
        String key = map.get(Constants.TD_MAP_KEY).toString();
        JSONObject reqJob = new JSONObject();
        reqJob.put("lon", lon);
        reqJob.put("lat", lat);
        reqJob.put("ver", 1);

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("postStr", reqJob.toJSONString());
        paramMap.put("type", "geocode");
        paramMap.put(Constants.REQ_TK, key);
        String body = HttpUtils.doGet(url, null, paramMap);

        log.info("天地图经纬度查询搜索返回 {}", body);
        JSONObject json = JSON.parseObject(body);
        String status = json.getString(Constants.WEATHER_STATUS);
        if ("0".equals(status)) {
            MapWorldLonLatResult result = JSON.parseObject(json.getString("result"), MapWorldLonLatResult.class);
            return result;
        } else {
            throw new CustomException("查询地理编码失败");
        }
    }


    @Override
    public List<PoiContraryGeocodingQueryRes> poiContraryGeocodingQuery(PoiContraryGeocodingQueryReq req) {
        if (StringUtils.isEmpty(req.getLocation())) {
            throw new CustomException("参数错误");
        }

        if (req.getParamType() == null && req.getParamType() == 1) {
            req.setLocation(PositionUtil.gcj02toWgs84Str(req.getLocation()));
        }

        String[] location = req.getLocation().split(",");
        MapWorldLonLatResult result = LonLatQuery(location[0], location[1]);

        PoiRadiusQueryReq queryReq = new PoiRadiusQueryReq();
        queryReq.setLocation(req.getLocation());
        queryReq.setKeyWord(result.getFormattedAddress());
        queryReq.setRadius(req.getRadius() == null ? 3000 : req.getRadius());
        queryReq.setEncryptRequestFlag(req.getEncryptRequestFlag());
        PoiRadiusQueryResTableInfo res = poiRadiusQuery(queryReq);
        List<PoiContraryGeocodingQueryRes> list = BeanCopyUtils.copyList(res.getList(), PoiContraryGeocodingQueryRes.class);

        list.forEach(r -> {
            if (r.getPois() != null && !r.getPois().isEmpty()) {
                Pois pois = r.getPois().get(0);
                r.setAddress(pois.getName());
                r.setLocation(pois.getLocation());

                r.setAddress(result.getFormattedAddress());
                r.setLocation(req.getLocation());
                Region region = BeanCopyUtils.copy(result.getAddressComponent(), Region.class);
                if (StringUtils.isNotEmpty(region.getProvinceCode())){
                    region.setProvinceCode(region.getProvinceCode().replace("156",""));
                }
                if (StringUtils.isNotEmpty(region.getCityCode())){
                    region.setCityCode(region.getCityCode().replace("156",""));
                }
                if (StringUtils.isNotEmpty(region.getCountyCode())){
                    region.setCountyCode(region.getCountyCode().replace("156",""));
                }
                if (StringUtils.isNotEmpty(region.getTownCode())){
                    region.setTownCode(padRightZero12(region.getTownCode().replace("156","")));
                }
                perfectRegion(region);
                r.setRegion(region);
            }

        });

        if (CollectionUtils.isEmpty(list)){
            PoiContraryGeocodingQueryRes r = new PoiContraryGeocodingQueryRes();
            r.setAddress(result.getFormattedAddress());
            r.setLocation(req.getLocation());

            r.setAddress(result.getFormattedAddress());
            r.setLocation(req.getLocation());
            Region region = BeanCopyUtils.copy(result.getAddressComponent(), Region.class);
            if (StringUtils.isNotEmpty(region.getProvinceCode())){
                region.setProvinceCode(region.getProvinceCode().replace("156",""));
            }
            if (StringUtils.isNotEmpty(region.getCityCode())){
                region.setCityCode(region.getCityCode().replace("156",""));
            }
            if (StringUtils.isNotEmpty(region.getCountyCode())){
                region.setCountyCode(region.getCountyCode().replace("156",""));
            }
            if (StringUtils.isNotEmpty(region.getTownCode())){
                region.setTownCode(padRightZero12(region.getTownCode().replace("156","")));
            }
            perfectRegion(region);
            r.setRegion(region);
            r.setPois(new ArrayList<>());
            list.add(r);
        }
        return list;
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

        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.TD_MAP_URL).toString() + "/administrative";
        String key = map.get(Constants.TD_MAP_KEY).toString();
        JSONObject reqJob = new JSONObject();
        //searchType 1-关键字 0-区域编码
        if (StringUtils.isNotEmpty(req.getAdCode())) {
            reqJob.put("searchWord", "156" + req.getAdCode());
            reqJob.put("searchType", "0");
        } else {
            reqJob.put("searchWord", req.getKeyWord());
            reqJob.put("searchType", "1");
        }
        reqJob.put("needPre", "true");//上级

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("postStr", reqJob.toJSONString());
        paramMap.put(Constants.REQ_TK, key);
        String body = HttpUtils.doGet(url, null, paramMap);

        log.info("天地图行政区域返回 {}", body);
        JSONObject resultObj = JSON.parseObject(body);
        if ("100".equals(resultObj.getString("returncode"))) {
            MapWorldAreaData data = null;
            if (StringUtils.isNotEmpty(req.getKeyWord())) {
                List<MapWorldAreaData> list = JSON.parseArray(resultObj.getString("data"), MapWorldAreaData.class);
                if (list.isEmpty()) {
                    throw new CustomException("未找到行政区划信息");
                }
                data = list.get(0);
            } else {
                data = JSON.parseObject(resultObj.getString("data"), MapWorldAreaData.class);
            }
            PoiAreaQueryRes res = new PoiAreaQueryRes();
            Parents parents = data.getParents();

            res.setPolygon(data.getBound());
            res.setLocation(data.getLnt() + "," + data.getLat());
            ParentsChild city = parents.getCity();
            ParentsChild province = parents.getProvince();

            if ("province".equals(data.getAdminType())) {
                res.setProvince(data.getName());
                res.setProvinceCode(data.getCityCode());
            } else if ("city".equals(data.getAdminType())) {
                res.setCity(data.getName());
                res.setCityCode(data.getName());
            } else if ("county".equals(data.getAdminType())) {
                res.setCounty(data.getName());
                res.setCountyCode(data.getCityCode());
                if (city == null) {
                    SysArea area = getSysArea(res.getCountyCode());
                    if (area != null) {
                        city = new ParentsChild();
                        city.setCityCode(area.getAreaParentCode());
                        city.setName(area.getAreaParentName());
                    }
                }
            }

            if (city != null) {
                res.setCity(city.getName());
                res.setCityCode(city.getCityCode());
            }
            if (province != null) {
                res.setProvince(province.getName());
                res.setProvinceCode(province.getCityCode());
            }
            //查询中心省会编码
            if (StringUtils.isNotEmpty(res.getProvinceCode()) && StringUtils.isEmpty(res.getCityCode())) {
                SysCapitalVO vo = getSysCapital(res.getProvinceCode());
                if (vo != null) {
                    res.setCityCode(vo.getCapitalCode());
                    res.setCity(vo.getCapitalName());
                } else if (Arrays.stream(CITY_CODE).anyMatch(s -> s.equals(res.getProvinceCode()))) {
                    res.setCityCode((Long.valueOf(res.getProvinceCode()) + 100) + "");
                    res.setCity(res.getProvince());
                }
            }
            return res;
        } else {
            throw new CustomException(resultObj.getString("msg"));
        }
    }

    /**
     *
     * @param req
     * @return 天地图轨迹
     */
    public TrajectoryQueryRes trajectoryQuery(TrajectoryQueryReq req) {
        if (StringUtils.isEmpty(req.getOrigin())
                || StringUtils.isEmpty(req.getEnd())) {
            throw new CustomException(Constants.PARAMS_ERROR);
        }
            if (req.getParamType() != null && req.getParamType() == 1) {
                req.setOrigin(PositionUtil.gcj02toWgs84Str(req.getOrigin()));
                req.setEnd(PositionUtil.gcj02toWgs84Str(req.getEnd()));
            }
        // 获取天地图URL和KEY

        Map<String, String> map = getGdConfig();
        String url = map.get(Constants.TD_MAP_URL).toString() + "/drive";
        String key = map.get(Constants.TD_MAP_KEY).toString();

        JSONObject reqJob = new JSONObject();
        reqJob.put("orig", req.getOrigin());
        reqJob.put("dest", req.getEnd());

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("postStr", reqJob.toJSONString());
        paramMap.put("type", "search");
        paramMap.put(Constants.REQ_TK, key);
        String body = HttpUtils.doGet(url, null, paramMap);
        log.info("驾驶轨迹 \r\n {}",body);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(TianDiTuDriveNavigationRes.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            TianDiTuDriveNavigationRes mapWorldNavigationRes = (TianDiTuDriveNavigationRes) jaxbUnmarshaller.unmarshal(StrUtil.getReader(body));
            Simple routes = mapWorldNavigationRes.getSimple();
            TrajectoryQueryRes res = new TrajectoryQueryRes();
            if (routes.getItem() != null && !routes.getItem().isEmpty()) {
                res.setCount(routes.getItem().size());
                List<Routs> routs = new ArrayList<>();
                for (SimpleItem item : routes.getItem()) {
                    Routs rout = new Routs();
                    rout.setDistance(item.getStreetDistance());
                    rout.setPolyline(item.getStreetLatLon());
                    rout.setRoadname(item.getStreetNames());
                    rout.setAction(item.getStrguide());
                    routs.add(rout);
                }
//                res.setRouts(routs);
            }
            return res;
        } catch (Exception e) {
            log.error("天地图驾驶导航错误", e);
        }

        throw new CustomException("天地图驾驶导航错误");
    }



//    public static void main(String[] args) {
//        String url = "https://api.tianditu.gov.cn/administrative?";
//        JSONObject reqJob = new JSONObject();
//        reqJob.put("searchWord", "1");
//        reqJob.put("searchType", "0");
//        reqJob.put("needPre", "true");
//        url = url + "postStr=" + reqJob.toJSONString() + "&tk=" + "54445c5adf7947eb7a3e68475859eabd";
//        String body = HttpRequest.get(url).execute().body();
//
//        System.out.println(body);
//    }
}