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
import com.chinatower.gis.utils.AESUtil;
import com.chinatower.gis.utils.PositionUtil;
import com.chinatower.gis.utils.WKTSimplifier;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.model.SysCapitalVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 线下
 * @data 创建时间：2023/12/14 9:21
 */
@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class OfflineWorldServiceImpl extends AbstractPoiBase implements GisMapService {

    private static final String OFFLINE_MAP_URL = "offline.map.url";

    private static final String OFFLINE_ENCRYPT_MAP_URL = "offline.encrypted.url";
    private static final String OFFLINE_ENCRYPT_ENCRYPTED_KEY = "offline.encrypted.key";


    private Map<String, String> getOfflineConfig() {
        Map<String, String> map = new HashMap<>();
        try {
            map.put(OFFLINE_MAP_URL, getConfigValue(OFFLINE_MAP_URL,"http://10.43.86.213:18082"));
            return map;
        } catch (Exception e) {
            log.error("查询高德离线地址错误", e);
        }
        return map;
    }

    /**
     * 加密配置
     */
    private Map<String, String> getOfflineEncryptConfig() {
        Map<String, String> map = new HashMap<>();
        try {
            map.put(OFFLINE_ENCRYPT_MAP_URL, getConfigValue(OFFLINE_ENCRYPT_MAP_URL,"https://sl-gaodemap-proxy.chinatowercom.cn:9711/json_decrypt?encrypted="));
            map.put(OFFLINE_ENCRYPT_ENCRYPTED_KEY, getConfigValue(OFFLINE_ENCRYPT_ENCRYPTED_KEY,"abcdef0123456789"));
            return map;
        } catch (Exception e) {
            log.error("查询高德离线地址错误", e);
        }
        return map;
    }

    /**
     * 获得离线数据
     * @param paramMap 请求参数
     * @param offlineUrl 请求地址
     */
    private String getOfflineEncryptBody(Map<String, Object> paramMap,String offlineUrl,Integer coordsys){
        String hearderUUID = UUID.randomUUID().toString();
        try {
            setCoordsys(paramMap,coordsys);

            long beginTime = System.currentTimeMillis();
            Map<String, String> map = getOfflineEncryptConfig();
            String url = map.get(OFFLINE_ENCRYPT_MAP_URL);
            String key = map.get(OFFLINE_ENCRYPT_ENCRYPTED_KEY);
            String params = offlineUrl + mapToUrlParams(paramMap);
            log.info("高德离线加密参数 {}",params);
            log.info("高德离线加密地址 {} key {}",url,key);
            String encrypted = AESUtil.encrypt(params,key);

            log.info("高德离线加密结果 {}",encrypted);
            String p = offlineNginxReplace(encrypted);
            log.info("高德离线特殊字符转换 {}",p);
            log.info("高德离线请求标识 {}",hearderUUID);
            //发送http请求
            String body = HttpRequest.get(url + p)
                    .header("gaode-uuid",hearderUUID)
                    .setConnectionTimeout(5000)
                    .setReadTimeout(5000)
                    .execute()
                    .body();
            long endTime = System.currentTimeMillis();
            log.info("高德离线返回 请求时长 {} \r\n {}", (endTime - beginTime), body);
            return body;
        }catch (Exception e){
            log.error("高德离线加密错误 {}",hearderUUID,e);
            throw new CustomException("高德离线加密错误");
        }
    }


    /**
     * 获得离线数据
     * @param paramMap 请求参数
     * @param offlineUrl 请求地址
     */
    private String getOfflineBody(Map<String, Object> paramMap,String offlineUrl,Integer coordsys){
        try {
            setCoordsys(paramMap,coordsys);

            long beginTime = System.currentTimeMillis();
            Map<String, String> map = getOfflineConfig();
            String url = map.get(OFFLINE_MAP_URL) + offlineUrl;
            log.info("高德离线地址 {}",url);
            //发送http请求
            String body = HttpRequest.get(url).form(paramMap).execute().body();
            long endTime = System.currentTimeMillis();
            log.info("高德离线返回 请求时长 {} \r\n {}", (endTime - beginTime), body);
            return body;
        }catch (Exception e){
            log.error("高德离线加密错误",e);
            throw new CustomException("高德离线加密错误");
        }
    }


    @Override
    public PoiKeyWordQueryRes poiKeyWordQuery(GisPoiKeyWordQueryReq req) {
        log.info("关键字穿参数",JSON.toJSONString(req));
        GdKeyWords keyWords = getGdOfflineKeyWords(req);
        //正确/错误返回
        if (keyWords.getStatus() == 1) {
            if (keyWords.getPois() != null && !keyWords.getPois().isEmpty()) {
                return gdKeyWordQuery(keyWords,req.getEncryptRequestFlag());
            }
            if (keyWords.getPois() == null || keyWords.getPois().isEmpty()) {
                req.setAdCode(BJ_CODE);//默认北京
                keyWords = getGdOfflineKeyWords(req);
                return gdKeyWordQuery(keyWords,req.getEncryptRequestFlag());
            }
            return new PoiKeyWordQueryRes();
        }
        throw new CustomException(keyWords.getInfo());
    }

    /**
     * 高德离线关键字
     */
    private GdKeyWords getGdOfflineKeyWords(GisPoiKeyWordQueryReq req) {
        if (StringUtils.isEmpty(req.getKeyWord())) {
            throw new CustomException("参数错误");
        }
        //判断是否省份code
        boolean match = Arrays.stream(PROVINCE_CODES).anyMatch(s -> s.equals(req.getAdCode()));
        if (match){//查询省会城市
            SysCapitalVO capitalVO = getSysCapital(req.getAdCode());
            if (capitalVO != null){
                req.setAdCode(capitalVO.getCapitalCode());
            }
        }
        // 获取高德URL和KEY
        String keywords = req.getKeyWord(); //地址

        //请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keywords", keywords); // 关键字
        paramMap.put("apiid", "place/text"); // 关键字
        if (StringUtils.isNotEmpty(req.getPoiTypes())) {
            paramMap.put("types", req.getPoiTypes()); // json格式
        }
        paramMap.put("page", req.getPageNum()); // json格式
        paramMap.put("offset", req.getPageSize()); // json格式
        //判断需要返回坐标格式
        if (StringUtils.isNotEmpty(req.getAdCode())) {
            SysArea area = getSysArea(req.getAdCode());
            if (area != null) {
                if (HONG_KONG_AND_MACAO.contains(area.getAreaParentCode())){
                    paramMap.put("city", area.getAreaParentName());
                }else {
                    paramMap.put("city", area.getAreaName());
                }
            } else {
                paramMap.put("city", req.getAdCode());
            }
        }

        if (StringUtils.isEmpty(req.getAdCode()) && StringUtils.isNotEmpty(req.getLocation())) {
            PoiContraryGeocodingQueryReq queryReq = new PoiContraryGeocodingQueryReq();
            queryReq.setLocation(req.getLocation());
            queryReq.setCoordType(req.getCoordType());
            queryReq.setParamType(req.getParamType());
            queryReq.setEncryptRequestFlag(req.getEncryptRequestFlag());
            //查询逆地理编码获取Code
            List<PoiContraryGeocodingQueryRes> res = poiContraryGeocodingQuery(queryReq);
            if (res != null && !res.isEmpty()) {
                PoiContraryGeocodingQueryRes queryRes = res.get(0);
                if (StringUtils.isNotEmpty(queryRes.getRegion().getProvinceCode())) {
                    paramMap.put("city", queryRes.getRegion().getCity());
                }
            }
        }
        String body = "";
        if(Boolean.TRUE.equals(req.getEncryptRequestFlag())){
            body = getOfflineEncryptBody(paramMap,KEY_WORD_QUERY_URL,req.getCoordType());
        }else {
            body = getOfflineBody(paramMap,KEY_WORD_QUERY_URL,req.getCoordType());
        }
        GdKeyWords keyWords = JSON.parseObject(body, GdKeyWords.class);
        return keyWords;
    }



    /**
     * @param keyWords
     * @return 解析补全数据
     */
    private PoiKeyWordQueryRes gdKeyWordQuery(GdKeyWords keyWords,boolean encryptRequestFlag) {
        PoiKeyWordQueryRes queryRes = new PoiKeyWordQueryRes();
        queryRes.setTotal(keyWords.getCount());
        List<GisPoiKeyWordQueryRes> list = new LinkedList<>();
        for (GdKeyWordsPoi poi : keyWords.getPois()) {
            GisPoiKeyWordQueryRes res = new GisPoiKeyWordQueryRes();
            res.setAlias(poi.getName());
            res.setDistance(poi.getDistance());

            res.setTag(poi.getTag());//离线没有
            res.setBrand(poi.getType());//离线没有
            res.setBrandCode(poi.getTypeCode());//离线没有
            Region region = new Region();

            region.setProvinceCode(poi.getPcode());//离线没有
            region.setProvince(poi.getPname());//离线没有
            //离线没有
            if (StringUtils.isNotEmpty(poi.getCitycode()) && poi.getCitycode().length() >= 6) {
                region.setCityCode(poi.getCitycode());
                region.setCity(poi.getCityname());
            }
            region.setCountyCode(poi.getAdcode());
            region.setCounty(poi.getAdname());


            {
                PoiContraryGeocodingQueryReq geocodingQueryReq = new PoiContraryGeocodingQueryReq();
                geocodingQueryReq.setLocation(poi.getLocation());
                geocodingQueryReq.setEncryptRequestFlag(encryptRequestFlag);
                List<PoiContraryGeocodingQueryRes> resList = poiContraryGeocodingQuery(geocodingQueryReq);
                if (resList != null && !resList.isEmpty()) {
                    region = resList.get(0).getRegion();
                } else {
                    //完善区域编码
                    perfectRegion(region);
                }
            }


            region.setLocation(poi.getLocation());

            res.setRegion(region);

            List<Pois> pois = new LinkedList<>();

            {
                Pois build = Pois.builder()
                        .location(poi.getLocation())
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

            res.setPois(pois);
            list.add(res);
        }
        queryRes.setList(list);
        return queryRes;
    }

    @Override
    public PoiRadiusQueryResTableInfo poiRadiusQuery(PoiRadiusQueryReq req) {

        GdKeyWords result = getGdOfflineRadius(req);
        if (result.getStatus() == 1) {
            List<PoiRadiusQueryRes> list = new ArrayList<>();
            List<GdKeyWordsPoi> pois = result.getPois();
            if (pois != null && !pois.isEmpty()) {
                for (GdKeyWordsPoi keyWordResultPois : pois) {
                    PoiRadiusQueryRes res = new PoiRadiusQueryRes();
                    List<Pois> ps = new ArrayList<>();
                    Pois p = Pois.builder()
                            .location(keyWordResultPois.getLocation())
                            .name(keyWordResultPois.getName())
                            .address(keyWordResultPois.getAddress())
                            .distance(keyWordResultPois.getDistance())
                            .nid(keyWordResultPois.getId())
                            .type(keyWordResultPois.getType())
                            .typecode(keyWordResultPois.getTypeCode())
                            .build();
                    res.setAlias(keyWordResultPois.getName());
                    res.setHit(keyWordResultPois.getWeight());
                    res.setBrandCode(keyWordResultPois.getTypeCode());
                    res.setBrand(keyWordResultPois.getType());
                    res.setTag("");
                    res.setDistance(keyWordResultPois.getDistance());
                    Region region = BeanCopyUtils.copy(keyWordResultPois, Region.class);
                    //逆地理编码补全数据
                    PoiContraryGeocodingQueryReq geocodingQueryReq = new PoiContraryGeocodingQueryReq();
                    geocodingQueryReq.setLocation(keyWordResultPois.getLocation());
                    geocodingQueryReq.setCoordType(req.getCoordType());
                    geocodingQueryReq.setParamType(req.getParamType());
                    geocodingQueryReq.setEncryptRequestFlag(req.getEncryptRequestFlag());
                    List<PoiContraryGeocodingQueryRes> resList = poiContraryGeocodingQuery(geocodingQueryReq);
                    if (resList != null && !resList.isEmpty()) {
                        region = resList.get(0).getRegion();
                    } else {
                        perfectRegion(region, keyWordResultPois.getAdcode());
                        //完善区域编码
                        perfectRegion(region);
                    }
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
            throw new CustomException(result.getInfo());
        }
    }

    /**
     * 高德离线半径
     */
    private GdKeyWords getGdOfflineRadius(PoiRadiusQueryReq req) {
        if (StringUtils.isEmpty(req.getLocation())) {
            throw new CustomException("参数错误");
        }
        // 获取高德URL和KEY
        String keywords = req.getKeyWord(); //地址

        //请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("apiid", "place/around"); // 周边
        paramMap.put("keywords", keywords); // 关键字
        paramMap.put("location", req.getLocation()); // 关键字
        paramMap.put("radius", req.getRadius()); // 关键字
        if (StringUtils.isNotEmpty(req.getPoiTypes())) {
            paramMap.put("types", req.getPoiTypes()); // json格式
        }
        paramMap.put("page", req.getPageNum()); // json格式
        paramMap.put("offset", req.getPageSize()); // json格式
        //发送http请求
        String body = "";
        if(Boolean.TRUE.equals(req.getEncryptRequestFlag())){
            body = getOfflineEncryptBody(paramMap,KEY_WORD_QUERY_URL,req.getCoordType());
        }else {
            body = getOfflineBody(paramMap,KEY_WORD_QUERY_URL,req.getCoordType());
        }
        log.info("离线高德周边返回 \r\n {}", body);
        GdKeyWords keyWords = JSON.parseObject(body, GdKeyWords.class);
        return keyWords;
    }


    @Override
    public List<PoiPolygonQueryRes> poiPolygonQuery(PoiPolygonQueryReq req) {
        if (StringUtils.isEmpty(req.getPolygon()) || StringUtils.isEmpty(req.getKeyWord())) {
            throw new CustomException("参数错误");
        }
        GdKeyWords words = getGdOfflinePolygon(req);
        if (words.getStatus() == 1) {
            List<PoiPolygonQueryRes> list = new ArrayList<>();
            List<GdKeyWordsPoi> pois = words.getPois();
            if (pois != null && !pois.isEmpty()) {
                for (GdKeyWordsPoi keyWordResultPois : pois) {
                    PoiPolygonQueryRes res = new PoiPolygonQueryRes();
                    List<Pois> ps = new ArrayList<>();
                    Pois p = Pois.builder()
                            .location(keyWordResultPois.getLocation())
                            .name(keyWordResultPois.getName())
                            .address(keyWordResultPois.getAddress())
                            .distance(keyWordResultPois.getDistance())
                            .nid(keyWordResultPois.getId())
                            .type(keyWordResultPois.getType())
                            .typecode(keyWordResultPois.getTypeCode())
                            .build();
                    res.setAlias(keyWordResultPois.getName());
                    res.setHit(keyWordResultPois.getScore());
                    res.setBrandCode(keyWordResultPois.getType());
                    res.setBrand(keyWordResultPois.getTypeCode());
                    res.setTag("");
                    res.setDistance(keyWordResultPois.getDistance());
                    Region region = BeanCopyUtils.copy(keyWordResultPois, Region.class);
                    //逆地理编码补全数据
                    PoiContraryGeocodingQueryReq geocodingQueryReq = new PoiContraryGeocodingQueryReq();
                    geocodingQueryReq.setLocation(keyWordResultPois.getLocation());
                    geocodingQueryReq.setCoordType(req.getCoordType());
                    geocodingQueryReq.setParamType(req.getParamType());
                    geocodingQueryReq.setEncryptRequestFlag(req.getEncryptRequestFlag());
                    List<PoiContraryGeocodingQueryRes> resList = poiContraryGeocodingQuery(geocodingQueryReq);
                    if (resList != null && !resList.isEmpty()) {
                        region = resList.get(0).getRegion();
                    } else {
                        perfectRegion(region, keyWordResultPois.getAdcode());
                        //完善区域编码
                        perfectRegion(region);
                    }
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
            throw new CustomException(words.getInfo());
        }
    }

    /**
     * 高德离线半径
     */
    private GdKeyWords getGdOfflinePolygon(PoiPolygonQueryReq req) {
        if (StringUtils.isEmpty(req.getPolygon())) {
            throw new CustomException("参数错误");
        }
        String[] split = req.getPolygon().replace(";", ",").split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length - 2; i += 2) {
            String lngLat = split[i] + "," + split[(i + 1)];
            sb.append(lngLat).append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        log.info("离线高德多边形 {}", sb);
        // 获取高德URL和KEY
        String keywords = req.getKeyWord(); //地址

        //请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keywords", keywords); // 关键字
        paramMap.put("apiid", "place/polygon"); // 关键字
        paramMap.put("polygon", sb.toString()); // 关键字
        paramMap.put("types", req.getPoiTypes()); // json格式
        paramMap.put("page", req.getPageNum()); // json格式
        paramMap.put("offset", req.getPageSize()); // json格式
        //发送http请求
        String body = "";
        if(Boolean.TRUE.equals(req.getEncryptRequestFlag())){
            body = getOfflineEncryptBody(paramMap,KEY_WORD_QUERY_URL,req.getCoordType());
        }else {
            body = getOfflineBody(paramMap,KEY_WORD_QUERY_URL,req.getCoordType());
        }
        long timeTime = System.currentTimeMillis();

        log.info("离线高德多边形返回  {}", body);

        GdKeyWords keyWords = JSON.parseObject(body, GdKeyWords.class);
        return keyWords;
    }

    @Override
    public List<PoiGeocodingQueryRes> poiGeocodingQuery(PoiGeocodingQueryReq req) {
        if (StringUtils.isEmpty(req.getAddress())) {
            throw new CustomException("参数错误");
        }
        //请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("address", req.getAddress()); // 地址
        if (StringUtils.isNotEmpty(req.getCity())){
            paramMap.put("city", req.getCity()); // json格式
        }


        //发送http请求
        String body = "";
        if(Boolean.TRUE.equals(req.getEncryptRequestFlag())){
            body = getOfflineEncryptBody(paramMap,GEOCODING_QUERY_URL,req.getCoordType());
        }else {
            body = getOfflineBody(paramMap,GEOCODING_QUERY_URL,req.getCoordType());
        }
        log.info("高德离线地理编码搜索返回 {}", body);
        JSONObject json = JSON.parseObject(body);
        String status = json.getString(Constants.WEATHER_STATUS);

        List<PoiGeocodingQueryRes> list = new ArrayList<>();
        if ("1".equals(status)) {
            JSONObject listObj = JSON.parseObject(json.getString("data"));
            JSONArray array = JSON.parseArray(listObj.getString("list"));
            if (CollectionUtils.isEmpty(array)){
                return list;
            }
            JSONObject object = JSON.parseObject(array.get(0).toString());

            Region region = new Region();
            region.setCounty(object.getString("district"));
            region.setCountyCode(object.getString("adcode"));
            region.setTown(object.getString("street"));
            //完善区域
            perfectRegion(region);
            PoiGeocodingQueryRes res = BeanCopyUtils.copy(region, PoiGeocodingQueryRes.class);
            res.setFormattedAddress(object.getString("formatted_address"));
            res.setLocation(object.getString("location"));
            list.add(res);
            return list;
        } else {
            throw new CustomException("查询地理编码失败");
        }
    }

    @Override
    public List<PoiContraryGeocodingQueryRes> poiContraryGeocodingQuery(PoiContraryGeocodingQueryReq req) {
        if (StringUtils.isEmpty(req.getLocation())) {
            throw new CustomException("参数错误");
        }
        String[] split = req.getLocation().split(",");
        StringBuilder sb = new StringBuilder();
        //判断是否包含小数点，如果不好喊兼容.00
        for (int i = 0; i < split.length ; i ++) {
           String s = split[i];
            if (!s.contains(".")){
                sb.append(s).append(".").append("00");
            }else {
                sb.append(s);
            }
            if (i == 0){
                sb.append(",");
            }
        }
        log.info("逆地理编码转换后的经纬度 {}", sb.toString());
        req.setLocation(sb.toString());
        String location = req.getLocation();
//        if (req.getParamType() == null || req.getParamType() == 1) {
//            req.setLocation(PositionUtil.gcj02toWgs84Str(location));
//        }
        req.setLocation(PositionUtil.convertLocation(location,req.convert84Flag(),req.convert02Flag()));
        List<PoiContraryGeocodingQueryRes> list = new LinkedList<>();
        // 获取高德URL和KEY
        //请求参数
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("location", req.getLocation()); //经纬度


        //发送http请求
        String body = "";
        if(Boolean.TRUE.equals(req.getEncryptRequestFlag())){
            body = getOfflineEncryptBody(paramMap,CONTRARY_GEOCODING_QUERYU_RL,req.getCoordType());
        }else {
            body = getOfflineBody(paramMap,CONTRARY_GEOCODING_QUERYU_RL,req.getCoordType());
        }
        log.info("离线高德逆地理编码 \r\n {}", body);
        JSONObject json = JSON.parseObject(body);
        String status = json.getString(Constants.WEATHER_STATUS);
        if ("1".equals(status)) {
            JSONObject listObj = JSON.parseObject(json.getString("data"));
            JSONArray array = JSON.parseArray(listObj.getString("list"));
            JSONObject object = JSON.parseObject(array.get(0).toString());

            PoiContraryGeocodingQueryRes queryRes = new PoiContraryGeocodingQueryRes();
            queryRes.setLocation(location);
            //结构化地址信息
            queryRes.setAddress(object.getString("formatted_address"));
            //地址元素信息
            JSONObject obj = JSON.parseObject(object.getString("addressComponent"));
            Region region = new Region();
            region.setCountyCode(obj.getString("adcode"));//区编码
            if ("900000".equals(region.getCountyCode()) || "100000".equals(region.getCountyCode())){
                throw new CustomException("当前地理位置没有地理信息");
            }
            region.setCounty(obj.getString("district"));//区名称
            region.setTownCode(obj.getString("towncode"));//街道编码
            region.setTown(obj.getString("township"));//街道名称
            perfectRegion(region);
            queryRes.setRegion(region);
            List<Pois> pois = new LinkedList<>();
            List<GdGeocodeRegeoPoi> poiList = JSON.parseArray(object.getString("pois"), GdGeocodeRegeoPoi.class);
            for (GdGeocodeRegeoPoi poi : poiList) {
                Pois build = Pois.builder()
                        .address(poi.getAddress())
                        .location(poi.getLocation())
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
            throw new CustomException(json.getString("res_desc"));
        }

    }

    @Override
    public PoiAreaQueryRes poiAreaQuery(PoiAreaQueryReq req) {
        if (StringUtils.isEmpty(req.getKeyWord()) && StringUtils.isEmpty(req.getAdCode())) {
            throw new CustomException("关键字/区域编码必传一个!");
        }
        String areaQueryKey = "poiAreaQuery:" + req.getChouxi() +":";
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
            RedisUtil.delete(areaQueryKey + ":*", RedisDBType.PRIMARY);
        }

        //清楚缓存
        if (req.getClearCacheFlag()){
            RedisUtil.delete(areaQueryKey, RedisDBType.PRIMARY);
            clearProMap();
            initAreaCode();
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
                    String polygon = areaQuery.getPolygon();
                    if (StringUtils.isNotEmpty(polygon)) {
                        copy.setPolygon(getGdOfflinePolyon(polygon));
                    }
                }
                RedisUtil.set(areaQueryKey, JSON.toJSONString(copy), 1, TimeUnit.DAYS, RedisDBType.PRIMARY);
                return copy;
            }
            throw new CustomException("未查询到行政区域信息");
        }

        PoiAreaQueryRes res = new PoiAreaQueryRes();
        res.setName(districts.getName());
        res.setLevel(districts.getLevel());

        poiAreaQueryFill(res,districts);

        perfectRegion(res);

        poiAreaQueryChildFill(res,districts);

        if (districts.getDistricts() != null && !districts.getDistricts().isEmpty()) {
            List<District> list = BeanCopyUtils.copyList(districts.getDistricts(), District.class);
            list.forEach(district -> {
                district.setCenter(district.getCenter().replace("POINT(", "").replace(")", "").replace(" ",","));
            });

            if (Boolean.TRUE.equals(req.getNextPolygonFlag()) && nextPolygonLevel.contains(districts.getLevel())){
                ThreadParallelUtil.run(list, district -> {
                    nextPolygon(district);
                    return null;
                },null);
            }
            res.setDistricts(list);
        }

        res.setLocation(districts.getCenter().replace("POINT(", "").replace(")", "").replace(" ",","));
        String polygon = districts.getPolygon();
        if (req.getChouxi() != null && req.getChouxi() == 1 && StringUtils.isNotEmpty(polygon)){
            polygon = WKTSimplifier.simplifyWKT(polygon,req.getEpsilon());
            res.setPolygon(getGdOfflineChouxiPolyon(polygon));
        }else {
            if (StringUtils.isNotEmpty(polygon)) {
                res.setPolygon(getGdOfflinePolyon(polygon));
            }
        }

        RedisUtil.set(areaQueryKey, JSON.toJSONString(res), 1, TimeUnit.DAYS, RedisDBType.PRIMARY);
        return res;
    }
    /**
     * 下级行政边界数据
     * @param district
     */
    private void nextPolygon(District district){
        PoiAreaQueryReq req = new PoiAreaQueryReq();
        req.setAdCode(district.getAdCode());
        GdDistricts gdDistricts = gdPoiAreaQuery(req);
        if (gdDistricts != null){
            String polygon = gdDistricts.getPolygon();
            if (StringUtils.isNotEmpty(polygon)) {
                district.setPolygon(getGdOfflinePolyon(polygon));
            }
        }
    }

    private GdDistricts gdPoiAreaQuery(PoiAreaQueryReq req) {
        // 获取高德URL和KEY

        //请求参数
        Map<String, Object> paramMap = new HashMap<>();
        if (StringUtils.isNotEmpty(req.getKeyWord())) {
            paramMap.put("keywords", req.getKeyWord());
        } else {
            paramMap.put("adcode", req.getAdCode());
        }
        paramMap.put("subdistrict", "1");
        paramMap.put("showPolygon", "1");
        String path = AREA_QUERY_URL;
        if (!StringUtils.isEmpty(req.getAdCode()) && req.getAdCode().length() >= 12){
            path = path = AREA_QUERY_street_URL;
            paramMap.put("streetid", req.getAdCode());
        }
        //发送http请求
        log.info("查询离线高德行政区划的  参数 {}", paramMap);
        long oldTime = System.currentTimeMillis();
        String body = "";
        if(Boolean.TRUE.equals(req.getEncryptRequestFlag())){
            body = getOfflineEncryptBody(paramMap,path,req.getCoordType());
        }else {
            body = getOfflineBody(paramMap,path,req.getCoordType());
        }
        log.info("离线高德行政区域返回 {}", body);
        long nowTime = System.currentTimeMillis();
        log.info("查询离线高德行政区划的时间 {}", (nowTime - oldTime) + "毫秒");
        JSONObject json = JSON.parseObject(body);
        String status = json.getString(Constants.WEATHER_STATUS);
        if ("1".equals(status)) {
            JSONObject listObj = JSON.parseObject(json.getString("data"));
            JSONArray array = JSON.parseArray(listObj.getString("list"));
            if (array.size() <= 0){
                return null;
            }
            GdDistricts districts = JSON.parseObject(array.get(0).toString(), GdDistricts.class);
//            if (req.getAdCode().length() >= 12){
//                districts.setAdCode(req.getAdCode());
//            }
            return districts;
        }
        throw new CustomException("未查询到行政区域信息");
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

//        if (req.getParamType() == 1) {
//            req.setOrigin(PositionUtil.gcj02toWgs84Str(origin));
//            req.setEnd(PositionUtil.gcj02toWgs84Str(end));
//        }

        req.setOrigin(PositionUtil.convertLocation(origin,req.convert84Flag(),req.convert02Flag()));
        req.setEnd(PositionUtil.convertLocation(end,req.convert84Flag(),req.convert02Flag()));

        DriveNavigationRes driveNavigation = driveNavigation(req);
        TrajectoryQueryRes res = new TrajectoryQueryRes();
        res.setCount(driveNavigation.getPaths().size());

        List<Paths> paths = new LinkedList<>();
        for (DriveNavigationPaths path : driveNavigation.getPaths()) {
            List<Routs> routs = new ArrayList<>();
            Paths onePath = new Paths();
            for (DriveNavigationSteps step : path.getSteps()) {
                Routs rout = new Routs();
                rout.setDistance(step.getDistance());
                if (req.getCoordType() !=  null && req.getCoordType() == 1){
                    rout.setPolyline(getGdOfflineTrajectoryPolyon(step.getPolyline()));
                }else {
                    rout.setPolyline(getGdOfflinePolyon(step.getPolyline()));
                }
                rout.setRoadname(step.getRoadName());
                rout.setAction(step.getInstruction());
                rout.setDuration(step.getDuration());
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

        Map<String, Object> map = new HashMap<>();
        map.put("origin", req.getOrigin());
        map.put("destination", req.getEnd());
        String body = "";
        if(Boolean.TRUE.equals(req.getEncryptRequestFlag())){
            body = getOfflineEncryptBody(map,DRIVE_NAVIGATION_URL,req.getCoordType());
        }else {
            body = getOfflineBody(map,DRIVE_NAVIGATION_URL,req.getCoordType());
        }

        log.info("离线驾驶导航信息返回{}", body);
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
