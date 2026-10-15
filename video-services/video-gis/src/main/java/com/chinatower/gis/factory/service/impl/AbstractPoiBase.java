package com.chinatower.gis.factory.service.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.enums.SysAreaLevelEnums;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.ThreadParallelUtil;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.gis.api.entity.map.*;
import com.chinatower.gis.client.SysClient;
import com.chinatower.gis.factory.service.GisPoiContainer;
import com.chinatower.gis.function.PoiAreaCountyQuery;
import com.chinatower.gis.modules.gis.mapper.GisMapper;
import com.chinatower.gis.utils.PositionUtil;
import com.chinatower.gis.utils.StreamUtils;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.utils.BeanCopyUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysCapitalVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@SuppressWarnings("all")
public abstract class AbstractPoiBase {
    @Resource
    protected SysClient sysClient;
    @Resource
    protected GisMapper gisMapper;
    @Resource
    protected GisPoiContainer gisPoiContainer;
    @Resource
    protected SystemFeignClient systemFeignClient;

    //关键字查询
    protected static final String KEY_WORD_QUERY_URL = "/query?";
    //地理编号
    protected static final String GEOCODING_QUERY_URL = "/geo/get/xy?";
    //逆地理编号
    protected static final String CONTRARY_GEOCODING_QUERYU_RL = "/rgeo/get/address?";
    //行政区划
    protected static final String AREA_QUERY_URL = "/polygon/dataquery/district?";
    //行政区划街道查询
    protected static final String AREA_QUERY_street_URL = "/polygon/dataquery/street?";
    //轨迹查询
    protected static final String DRIVE_NAVIGATION_URL = "/local/direction/driving?";

    protected static final String gcj02 = "gcj02";
    protected static final String wgs84 = "wgs84";
    //高德离线需要的坐标key
    protected static final String coordsys = "coordsys";

    protected void setCoordsys(Map<String, Object> paramMap,Integer coordType){
        if (coordType != null && coordType == 1){
            paramMap.put(coordsys, gcj02);
        }else {
            paramMap.put(coordsys, wgs84);
        }
    }

    public static final String COUNTRY_CODE = "100000";
    //台湾
    public static final String TAIWAN_CODE = "710000";
    //省份标识
    public static final String PROVINCE = "province";
    /**
     * 可以查询下级边界的等级
     */
    public static final List<String> nextPolygonLevel = Arrays.asList("city", "province", "country");
    /**
     * 行政区域下级行政区域单独处理
     */
    public static final List<String> DISTRICT_ERROR_CODE = Arrays.asList("420000","460000");
    /***含有直辖区的市***/
    public static final List<String> DISTRICT_CHILD_ERROR_CODE = Arrays.asList("429000","469000");
    /***港澳特殊处理***/
    public static final List<String> HONG_KONG_AND_MACAO = Arrays.asList("810000","820000");
    /**
     * 直辖市
     */
    public static final String[] CITY_CODE = new String[]{"710000","500000", "120000", "110000", "310000"};
    /**
     * 省份编码
     */
    public static final String[] PROVINCE_CODES = {"130000", "140000", "150000", "210000", "220000", "230000", "320000", "330000", "340000", "350000", "360000"
            , "370000", "410000", "420000", "430000", "440000", "450000", "460000", "510000", "520000", "530000", "540000", "610000"
            , "620000", "630000", "640000", "650000", "710000", "110000", "500000", "120000", "810000"};

    protected static final String BJ_CODE = "110000";

    private static final Map<String, String> MAP = new HashMap<String, String>() {{
        put("+", "%2B");
        put("/", "%2F");
        put(" ", "%20");
        put("?", "%3F");
        put("#", "%23");
        put("&", "%26");
        put("=", "%3D");
    }};

    /**
     * 行政区划数据
     */
    protected SysArea getSysArea(String areaCode) {
      return gisPoiContainer.getSysArea(areaCode);
    }

    protected void initAreaCode() {
        log.info("清理本地缓存数据");
        gisPoiContainer.initSysArea();
    }

    protected String getConfigValue(String configKey,String defaultValue){
        AjaxResult ajaxResult_1 = systemFeignClient.getConfValue(configKey);
        if ("200".equals(String.valueOf(ajaxResult_1.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult_1.get(AjaxResult.DATA_TAG)), SysConfig.class);
            return com.chinatower.platform.common.utils.StringUtils.defaultIfBlank(sysConfig.getConfigValue(), "0");
        }else {
            return defaultValue;
        }
    }

    /**
     * 全国数据特殊处理
     */
    protected PoiAreaQueryRes poiAreaCountyQuery(String areaQueryKey,PoiAreaQueryReq req,PoiAreaCountyQuery countyQuery){
        GdDistricts districts = new GdDistricts();
        districts.setLevel(SysAreaLevelEnums.COUNTRY.getLevel());
        PoiAreaQueryRes res = new PoiAreaQueryRes();
        res.setProvinceCode(COUNTRY_CODE);
        res.setCityCode(COUNTRY_CODE);
        res.setName("全国");
        res.setLevel(SysAreaLevelEnums.COUNTRY.getLevel());
        res.setLocation("116.4011447,39.90277565");
        poiAreaQueryChildFill(res,districts);
        List<District> list = BeanCopyUtils.copyList(districts.getDistricts(), District.class);
        if (Boolean.TRUE.equals(req.getNextPolygonFlag()) && nextPolygonLevel.contains(districts.getLevel())){
            ThreadParallelUtil.run(list, district -> {
                countyQuery.nextPolygon(district);
                return null;
            },null);
        }
        res.setDistricts(list);
        RedisUtil.set(areaQueryKey, JSON.toJSONString(res), 30, TimeUnit.DAYS, RedisDBType.PRIMARY);
        return res;
    }

    /**
     * 省会数据
     */
    protected SysCapitalVO getSysCapital(String areaCode) {
        return gisPoiContainer.getSysCapital(areaCode);
    }

    /**
     * ng 特殊字符住转换
     */
    protected static String offlineNginxReplace(String str) {
        str = str.replace("%", "%25");
        for (Map.Entry<String, String> entry : MAP.entrySet()) {
            str = str.replace(entry.getKey(), entry.getValue());
        }
        return str;
    }

    /**
     * 将Map集合转换为URL参数串
     *
     * @param map 参数
     */
    public static String mapToUrlParams(Map<String, Object> map) {
        // 使用StringBuilder拼接URL参数串
        StringBuilder params = new StringBuilder();
        // 遍历Map集合，拼接参数
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String && isContainChinese((String) entry.getValue())) {
                params.append(entry.getKey()).append("=").append(URLEncoder.encode((String) entry.getValue())).append("&");
            } else {
                params.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        // 去除末尾的"&"
        if (params.length() > 0) {
            params.deleteCharAt(params.length() - 1);
        }
        return params.toString();
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    protected static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.find();
    }

    private static Map<String, PoiAreaQueryRes> areas = new HashMap<>();

    /**
     * 边界数据
     */
    public static PoiAreaQueryRes getArea(String areaCode) {
        return areas.get(areaCode);
    }

    public static void clearProMap() {
        areas.clear();
    }

    /**
     * 保存边界数据
     */
    public static void putArea(String areaCode, PoiAreaQueryRes res) {
        areas.put(areaCode, res);
    }

    protected void perfectRegion(Region region, String adcode) {
        SysArea sysArea = getSysArea(adcode);
        if (sysArea != null) {
            if ("2".equals(sysArea.getAreaLevel())) {
                region.setProvinceCode(sysArea.getAreaCode());
                region.setProvince(sysArea.getAreaName());
            } else if ("4".equals(sysArea.getAreaLevel())) {
                region.setCountyCode(sysArea.getAreaCode());
                region.setCounty(sysArea.getAreaName());
            } else if ("3".equals(sysArea.getAreaLevel())) {
                region.setCityCode(sysArea.getAreaCode());
                region.setCity(sysArea.getAreaName());
            }
        }
    }

    /**
     * 完成区域编码
     *
     * @param region
     */
    protected void perfectRegion(Region region) {
        if (StringUtils.isNotEmpty(region.getTownCode()) && StringUtils.isEmpty(region.getCountyCode())) {
            SysArea sysArea = getSysArea(region.getTownCode());

            if (sysArea != null) {
                region.setCountyCode(sysArea.getAreaParentCode());
                region.setCounty(sysArea.getAreaParentName());
            }
        }
        if (StringUtils.isNotEmpty(region.getCountyCode()) && StringUtils.isEmpty(region.getCityCode())) {
            SysArea sysArea = getSysArea(region.getCountyCode());
            if (sysArea != null && "4".equals(sysArea.getAreaLevel())) {
                region.setCityCode(sysArea.getAreaParentCode());
                region.setCity(sysArea.getAreaParentName());
            } else if (sysArea != null && "3".equals(sysArea.getAreaLevel())) {
                region.setCountyCode(null);
                region.setCounty(null);
                if (!StringUtils.isEmpty(region.getTownCode())){
                    SysArea townArea = getSysArea(region.getTownCode());
                    if (townArea != null){
                        SysArea countyArea = getSysArea(townArea.getAreaParentCode());
                        if (countyArea != null && "4".equals(countyArea.getAreaLevel())) {
                            region.setCountyCode(countyArea.getAreaCode());
                            region.setCounty(countyArea.getAreaName());
                        }
                    }
                }

                region.setCityCode(sysArea.getAreaCode());
                region.setCity(sysArea.getAreaName());
            }
        }
        if (StringUtils.isNotEmpty(region.getCityCode()) && StringUtils.isEmpty(region.getProvinceCode())) {
            SysArea sysArea = getSysArea(region.getCityCode());
            if (sysArea != null) {
                region.setProvinceCode(sysArea.getAreaParentCode());
                region.setProvince(sysArea.getAreaParentName());
            }
        }
        //查询中心省会编码
        if (StringUtils.isNotEmpty(region.getProvinceCode()) && StringUtils.isEmpty(region.getCityCode())) {
            SysCapitalVO vo = getSysCapital(region.getProvinceCode());
            if (vo != null) {
                region.setCityCode(vo.getCapitalCode());
                region.setCity(vo.getCapitalName());
            } else if (Arrays.stream(CITY_CODE).anyMatch(s -> s.equals(region.getProvinceCode()))) {
                region.setCityCode((Long.valueOf(region.getProvinceCode()) + 100) + "");
                region.setCity(region.getProvince());
            }
        }
        if (StringUtils.isNotEmpty(region.getCityCode()) && DISTRICT_CHILD_ERROR_CODE.contains(region.getCityCode())){
            region.setCityCode(region.getCountyCode());
        }
    }

    /**
     * 小范围点的重复数据
     */
    private static final String chouxi_regex = "\\)\\s*,\\s*\\([^()]+\\)\\s*,\\s*\\(";

    protected String getGdOfflineChouxiPolyon(String polygon) {
        polygon = polygon.replace(")), ((", "|");
        polygon = polygon.replace("MULTIPOLYGON (((", "");
        polygon = polygon.replace("POLYGON ((", "");
        polygon = polygon.replace(")))", "");
        polygon = polygon.replace("))", "");
        polygon = polygon.replace(", ", ";");
        polygon = polygon.replace(" ", ",");
        return polygon;
    }

    public static String removeByRegex(String input, String regex) {
        if (input == null || regex == null) {
            return input;
        }
        return input.replaceAll(regex, ";");
    }

    protected String getGdOfflinePolyon(String polygon) {
        polygon = polygon.replace(")),((", "|");
        polygon = polygon.replace("MULTIPOLYGON(((", "");
        polygon = polygon.replace("POLYGON((", "");
        polygon = polygon.replace(")))", "");
        polygon = polygon.replace("))", "");
        polygon = polygon.replace(",", ";");
        polygon = polygon.replace(" ", ",");
        return polygon;
    }

    protected String getGdOfflineTrajectoryPolyon(String polygon) {
        polygon = polygon.replace(")),((", "|");
        polygon = polygon.replace("MULTIPOLYGON(((", "");
        polygon = polygon.replace("POLYGON((", "");
        polygon = polygon.replace(")))", "");
        polygon = polygon.replace("))", "");
        polygon = polygon.replace(" ", ",");
        return polygon;
    }

    /**
     * 数据补全
     * @param res
     * @param districts
     */
    protected void poiAreaQueryFill(PoiAreaQueryRes res, GdDistricts districts){
        //直辖市属于省份
        if (Arrays.stream(CITY_CODE).anyMatch(s -> s.equals(districts.getAdCode()))){
            districts.setLevel(PROVINCE);
            res.setLevel(PROVINCE);
        }
        if (PROVINCE.equals(districts.getLevel())) {
            res.setProvinceCode(districts.getAdCode());
            res.setProvince(districts.getName());
        }
        if ("city".equals(districts.getLevel())) {
            res.setCityCode(districts.getAdCode());
            res.setCity(districts.getName());
        }
        if ("district".equals(districts.getLevel())) {
            res.setCountyCode(districts.getAdCode());
            res.setCounty(districts.getName());
        }
        if ("street".equals(districts.getLevel())) {
            res.setTownCode(districts.getStreetid());
            res.setTown(districts.getName());
        }
    }

    protected void poiAreaQueryChildFill(PoiAreaQueryRes res, GdDistricts districts){
        boolean anyMatch = Arrays.stream(CITY_CODE).anyMatch(s -> s.equals(res.getProvinceCode()) || COUNTRY_CODE.equals(res.getCityCode()) || DISTRICT_ERROR_CODE.contains(res.getProvinceCode()));
        //离线直辖市下级数据有问题需查询表
        if ((anyMatch || "district".equals(res.getLevel()))&& !"street".equals(res.getLevel())){
            List<SysArea> sysAreas = new ArrayList<>();
            //重庆数据特殊处理
            if ("500000".equals(res.getProvinceCode()) && StringUtils.isEmpty(res.getCountyCode())){
                List<SysArea> one = gisMapper.selectBySysParentCode("500100");
                List<SysArea> two = gisMapper.selectBySysParentCode("500200");
                sysAreas.addAll(one);
                sysAreas.addAll(two);
            }else {
                if (DISTRICT_ERROR_CODE.contains(res.getProvinceCode())){
                    if (SysAreaLevelEnums.DISTRICT.getLevel().equals(res.getLevel()) || SysAreaLevelEnums.CITY.getLevel().equals(res.getLevel())){
                        if (SysAreaLevelEnums.CITY.getLevel().equals(res.getLevel())){
                            sysAreas = gisMapper.selectBySysParentCode(res.getCityCode());
                        }else {
                            sysAreas = gisMapper.selectBySysParentCode(res.getCountyCode());
                        }
                    }else {
                        List<SysArea> areaList = gisMapper.selectBySysParentCode(res.getProvinceCode());
                        Map<String, SysArea> listMap = StreamUtils.toMap(areaList, SysArea::getAreaCode, d->d);
                        for (Map.Entry<String, SysArea> map : listMap.entrySet()){
                            if (DISTRICT_CHILD_ERROR_CODE.contains(map.getKey())){
                                List<SysArea> areaChildList  = gisMapper.selectBySysParentCode(map.getKey());
                                sysAreas.addAll(areaChildList);
                            }else {
                                sysAreas.add(map.getValue());
                            }
                        }
                    }

                }else {
                    //台湾省特殊处理
                    if (TAIWAN_CODE.equals(res.getProvinceCode())){
                        sysAreas = gisMapper.selectBySysParentCode(res.getProvinceCode());
                    }else if ("district".equals(res.getLevel())){
                        sysAreas = gisMapper.selectBySysParentCode(res.getCountyCode());
                    }else {
                        sysAreas = gisMapper.selectBySysParentCode(res.getCityCode());
                    }

                }

            }
            List<GdDistricts>  list = new ArrayList<>();
            for(SysArea sysArea : sysAreas) {
                GdDistricts district = new GdDistricts();
                district.setName(sysArea.getAreaName());
                district.setCenter(sysArea.getLongitude() + "," + sysArea.getLatitude());
                district.setAdCode(sysArea.getAreaCode());
                list.add(district);
            };
            districts.setDistricts(list);
        }
        String nextLevel = SysAreaLevelEnums.getNextLevel(res.getLevel());
        if (!TAIWAN_CODE.equals(res.getProvinceCode()) && SysAreaLevelEnums.CITY.getLevel().equals(nextLevel) && Arrays.stream(CITY_CODE).anyMatch(s -> s.equals(res.getProvinceCode()))){
            nextLevel = SysAreaLevelEnums.CITY.getNextLevel();
        }
        if (!StringUtils.isEmpty(nextLevel) && !CollectionUtils.isEmpty(districts.getDistricts())){
            for (GdDistricts district : districts.getDistricts()){
                district.setLevel(nextLevel);
            }
        }
    }

    /**
     * @param polygon
     * @return 高德转天地图坐标
     */
    protected String gdToMapPolyon(String polygon) {
        String[] lnglats = polygon.split(";");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lnglats.length; i++) {
            if (lnglats[i].contains("|")) {
                String[] lnglat_l = lnglats[i].split("\\|");
                for (int j = 0; j < lnglat_l.length; j++) {
                    String[] lnglat = lnglat_l[j].split(",");
                    double[] doubles = PositionUtil.gcj02towgs84(Double.parseDouble(lnglat[0]), Double.parseDouble(lnglat[1]));
                    sb.append(doubles[0] + "," + doubles[1]);
                    if (j != lnglat_l.length - 1) {
                        sb.append("|");
                    }
                }
            } else {
                String[] lnglat = lnglats[i].split(",");
                double[] doubles = PositionUtil.gcj02towgs84(Double.parseDouble(lnglat[0]), Double.parseDouble(lnglat[1]));
                sb.append(doubles[0] + "," + doubles[1]);
            }

            if (i != lnglats.length - 1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    /**
     * 右侧补0，总长度固定12位
     * 1. null转为空字符串
     * 2. 超过12位截取前12位
     * 3. 不足末尾补0
     */
    public static String padRightZero12(String str) {
        // 空值处理
        String content = str == null ? "" : str;
        // 超长截取
        if (content.length() >= 12) {
            return content.substring(0, 12);
        }
        // 末尾循环补0
        StringBuilder sb = new StringBuilder(content);
        while (sb.length() < 12) {
            sb.append('0');
        }
        return sb.toString();
    }
}
