package com.chinatower.gis.modules.gis.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.gis.api.GisMapServiceAPI;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.modules.gis.entity.GisMapTypeEnum;
import com.chinatower.gis.modules.gis.mapper.GisMapper;
import com.dengxq.lnglat2Geo.GeoTrans;
import com.dengxq.lnglat2Geo.entity.Admin;
import com.dengxq.lnglat2Geo.entity.AdminNode;
import com.dengxq.lnglat2Geo.entity.CoordinateSystem;
import com.dengxq.lnglat2Geo.entity.DistrictLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Enumeration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@Service
public class GisOfflineService implements GisMapServiceAPI {
    @Autowired
    GisMapper     gisMapper;

    @PostConstruct
    public void init(){
//        log.info("开始初始化离线行政区划信息...");
//        CompletableFuture.runAsync(() -> GeoTrans.init(true,true,false,true));
//        log.info("完成离线行政区划信息的初始化...");
    }

    /**
     * 关键词搜索:根据关键词搜索获取经纬度坐标以及行政区划和详细地址信息(仅支持输入区县级别以上区划名称获取区划信息以及坐标信息)
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result keyWordQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        if(StringUtils.isEmpty(gisKeyWordQueryReq.getKeyWord())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())){
            return Result.fail(Constants.PARAMS_ERROR );
        }
        //关键词
        String keyword = gisKeyWordQueryReq.getKeyWord();
        List<AdminNode> datas = new ArrayList<>();
        //区县级别的数据
        List<AdminNode> district = GeoTrans.normalizeName(keyword,DistrictLevel.District(),false);
        if(CollectionUtils.isNotEmpty(district)){
            datas.addAll(district);
        }
        //地市级别的数据
        List<AdminNode> city = GeoTrans.normalizeName(keyword,DistrictLevel.City(),false);
        if(CollectionUtils.isNotEmpty(city)){
            datas.addAll(city);
        }
        //省级别数据
        List<AdminNode> province = GeoTrans.normalizeName(keyword,DistrictLevel.Province(),false);
        if(CollectionUtils.isNotEmpty(province)){
            datas.addAll(province);
        }
        datas = datas.stream().filter(e->(e != null && e.center() != null)).collect(Collectors.toList());
        log.info("根据关键词获取区域信息：{}",JSONObject.toJSONString(datas));
        GisKeyWordQueryReq where = new GisKeyWordQueryReq();
        where.setMapType(GisMapTypeEnum.GAODE_MAP.getCode());
        List<Map<String,Object>> list = new ArrayList<>();
        for(AdminNode adminNode : datas){
            if(adminNode == null){
                continue;
            }
            where.setLat(""+adminNode.center().lat());
            where.setLon(""+adminNode.center().lng());
            Result rr = codeToAdress(where);
            if(ResultCode.SUCCESS.getCode() == rr.getCode()){
                Map<String,Object> data = new HashMap<>();
                Map address = (Map<String,Object>)rr.getData();
                //经纬度
                data.put("location",adminNode.center().lng()+","+adminNode.center().lat());
                // 所在省份名称
                data.put("pname",address.get(Constants.PROVINCE));
                // 城市名
                data.put("cityname",address.get(Constants.CITY));
                // 所在区县名称
                data.put("adname",address.get(Constants.DISTRICT));
                data.put("name",address.get(Constants.DISTRICT));
                list.add(data);
            }else{
                return Result.fail("无法通过关键词："+keyword+"获取相关的区域信息");
            }
        }
        //返回结果集
        Map<String,Object> result = new HashMap<>();
        result.put("count",datas.size());
        result.put("result",list);
        log.info("响应结果：{}",JSONObject.toJSONString(result));
        return Result.data(result);
    }

    /**
     * 通过坐标获取省市区县信息以及详细地址
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
        //根据地图类型定位坐标系
        Enumeration.Value zb = null ;
        if("01".equals(gisKeyWordQueryReq.getMapType())){
            zb = CoordinateSystem.GCJ02();
        }else if("02".equals(gisKeyWordQueryReq.getMapType())){
            zb = CoordinateSystem.BD09();
        }else if("03".equals(gisKeyWordQueryReq.getMapType())){
            zb = CoordinateSystem.WGS84();
        }else if("04".equals(gisKeyWordQueryReq.getMapType())){
            zb = CoordinateSystem.GCJ02();
        }
        //返回结果集
        Map<String,Object> result = new HashMap<>();
        //经度
        Double lon = Double.parseDouble(gisKeyWordQueryReq.getLon());
        //纬度
        Double lat = Double.parseDouble(gisKeyWordQueryReq.getLat());
        Admin admin = GeoTrans.determineAdmin(lon,lat,zb,true);

        if(admin== null || admin.provinceCode() < 0){
            return Result.fail(ResultCode.GLOBAL_PARAM_ERROR,"输入的经纬度非中国范围内,无法有效获取行政区划信息："+lon+","+lat);
        }else{
            //省
            result.put(Constants.PROVINCE,admin.province());
            result.put(Constants.PROVINCE_CODE, String.valueOf(admin.provinceCode()));
            //北京、上海、重庆、天津四个直辖市的city、cityCode转换为与4A一致
            String provinceCode = ""+admin.provinceCode();
            if(municipalities(provinceCode)){
                GisKeyWordQueryReq where = new GisKeyWordQueryReq();
                where.setAdCode(String.valueOf(admin.cityCode()));
                Map map = gisMapper.getLatitude(where);
                if(!map.isEmpty()){
                    //城市名
                    result.put(Constants.CITY,map.get("areaName"));
                    //城市编码
                    result.put(Constants.CITY_CODE,map.get("code"));
                }
            }else{
                //市
                result.put(Constants.CITY,admin.city());
                result.put(Constants.CITY_CODE, String.valueOf(admin.cityCode()));
            }
            //区
            result.put(Constants.DISTRICT,admin.district());
            result.put(Constants.DISTRICT_CODE, String.valueOf(admin.districtCode()));
            result.put(Constants.AD_CODE, String.valueOf(admin.districtCode()));
            //乡镇||街道
            result.put(Constants.TOWNSHIP, admin.town());
            result.put(Constants.TOWN_CODE, String.valueOf(admin.townCode()));
            //拼接地址
            StringBuffer address = new StringBuffer();
            address.append(result.get(Constants.PROVINCE));
            if(!municipalities(provinceCode)){
                address.append(result.get(Constants.CITY));
            }
            address.append(result.get(Constants.DISTRICT))
                    .append(result.get(Constants.TOWNSHIP));
            result.put(Constants.RES_FORMATTED_ADDRESS,address.toString());
            log.info("返回响应结果：{}",JSONObject.toJSONString(result));
            return Result.data(result);
        }
    }

    private boolean municipalities(String provinceCode){
        if("110000".equals(provinceCode)
                ||"120000".equals(provinceCode)
                || "310000".equals(provinceCode)
                ||"500000".equals(provinceCode)){
            return true;
        }
        return false;
    }

    /**
     * 通过地址转经纬度坐标
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result adressToCode(GisKeyWordQueryReq gisKeyWordQueryReq) {
        return Result.fail("离线模式不支持根据地址获取经纬度坐标信息");
    }

    /**
     * 查询天气信息
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result weatherQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        return Result.fail("离线模式不支持天气查询");
    }

    /**
     *  行政区域模糊查询
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result districtQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        //请求参数非空返回
        if(StringUtils.isEmpty(gisKeyWordQueryReq.getAdCode())
                || StringUtils.isEmpty(gisKeyWordQueryReq.getMapType())){
            return Result.fail(Constants.PARAMS_ERROR );
        }
        AdminNode adminNode = GeoTrans.normalizeName(Integer.valueOf(gisKeyWordQueryReq.getAdCode()));
        JSONArray array = new JSONArray();
        if(adminNode != null){
            JSONObject data = new JSONObject();
            String parentCode = ""+adminNode.parentId();
            String code = ""+adminNode.id();
            data.put("adcode",code);
            if(municipalities(parentCode)){
                GisKeyWordQueryReq where = new GisKeyWordQueryReq();
                where.setAdCode(code);
                Map map = gisMapper.getLatitude(where);
                if(!map.isEmpty()){
                    //城市名
                    data.put("name",map.get("areaName"));
                }
            }else{
                //城市名
                data.put("name",adminNode.name());
            }
            data.put("center",adminNode.center().lng()+","+adminNode.center().lat());
            data.put("level",adminNode.level().toString());
            array.add(data);
        }
        log.info("返回结果：{}",array.toJSONString());
        return Result.data(array);
    }

    /**
     * 84转坐标系
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result coordinateTransformation(GisKeyWordQueryReq gisKeyWordQueryReq) {
        return Result.fail("离线模式不支持坐标系转换");
    }

    /**
     * 根据坐标获取行政区划信息
     * @param gisKeyWordQueryReq
     * @return
     */
    @Override
    public Result countryAreaQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        return codeToAdress(gisKeyWordQueryReq);
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
}
