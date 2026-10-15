package com.chinatower.gis.modules.gis.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisAPI;
import com.chinatower.gis.api.GisMapServiceAPI;
import com.chinatower.gis.api.entity.*;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiContraryGeocodingQueryRes;
import com.chinatower.gis.api.entity.map.PoiGeocodingQueryReq;
import com.chinatower.gis.api.entity.map.PoiGeocodingQueryRes;
import com.chinatower.gis.factory.GisMapFactory;
import com.chinatower.gis.factory.GisMapServiceFactory;
import com.chinatower.gis.factory.service.GisMapService;
import com.chinatower.gis.modules.gis.service.GisService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.enums.GisMapServiceEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * controller
 * Created by ldl on 2022/2/8.
 */
@Api(value = "调用外部地图-Api", tags = "调用外部地图接口")
@Slf4j
@RestController
public class GisController implements GisAPI {
    @Autowired
    GisMapServiceFactory gisMapServiceFactory;

    @Autowired
    GisService gisService;

    @Resource
    GisMapFactory gisMapFactory;

    @ApiOperation(value = "关键字搜索", notes = "关键字搜索")
    @Override
    @PostMapping("/gis/keyWordQuery")
    public Result keyWordQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        Result res;
        try {
            GisMapServiceAPI gisMapService = gisMapServiceFactory.getGisMapService(gisKeyWordQueryReq.getMapType());
            res = gisMapService.keyWordQuery(gisKeyWordQueryReq);
        } catch (Exception e) {
            res = Result.fail(Constants.KEY_WORD_ERROR);
            log.error(Constants.KEY_WORD_ERROR , e);
        }
        return res;
    }

    @ApiOperation(value = "将经纬度转换为详细结构化的地址", notes = "将经纬度转换为详细结构化的地址")
    @Override
    @PostMapping("/gis/codeToAdress")
    public Result codeToAdress(GisKeyWordQueryReq gisKeyWordQueryReq) {
        Result res;
        try {
//            GisMapServiceAPI gisMapService = gisMapServiceFactory.getGisMapService(gisKeyWordQueryReq.getMapType());
//            res = gisMapService.codeToAdress(gisKeyWordQueryReq);
            PoiContraryGeocodingQueryReq req = new PoiContraryGeocodingQueryReq();
            req.setLocation(gisKeyWordQueryReq.getLon() + "," + gisKeyWordQueryReq.getLat());
            GisMapServiceEnums enums = GisMapServiceEnums.get(req.getMapType());
            if (StringUtils.isEmpty(req.getMapType()) || enums.equals(GisMapServiceEnums.MAP_WORLD)){
                enums = GisMapServiceEnums.GD_WORLD;
            }
            GisMapService mapService = gisMapFactory.get(enums.getServiceName());
            req.setEncryptRequestFlag(enums == GisMapServiceEnums.OFFLINE_ENCRYPT_WORLD);
            List<PoiContraryGeocodingQueryRes> queryRes = mapService.poiContraryGeocodingQuery(req);
            res = Result.data(queryRes);
        } catch (Exception e) {
            res = Result.fail(Constants.CODE_TO_ADDRESS_ERROR);
            log.error(Constants.CODE_TO_ADDRESS_ERROR , e);
        }
        return res;
    }

    @ApiOperation(value = "查询目标区域当前/未来的天气情况", notes = "查询目标区域当前/未来的天气情况")
    @Override
    @PostMapping("/gis/weatherQuery")
    public Result weatherQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        Result res;
        try {
            GisMapServiceAPI gisMapService = gisMapServiceFactory.getGisMapService(gisKeyWordQueryReq.getMapType());
            res = gisMapService.weatherQuery(gisKeyWordQueryReq);
        } catch (Exception e) {
            res = Result.fail(Constants.WEATHER_ERROR);
            log.error(Constants.WEATHER_ERROR , e);
        }
        return res;
    }

    @ApiOperation(value = "将84坐标转换为当前地图可用坐标", notes = "将84坐标转换为当前地图可用坐标")
    @Override
    @PostMapping("/gis/coordinateTransformation")
    public Result coordinateTransformation(GisKeyWordQueryReq gisKeyWordQueryReq) {
        Result res;
        try {
            GisMapServiceAPI gisMapService = gisMapServiceFactory.getGisMapService(gisKeyWordQueryReq.getMapType());
            res = gisMapService.coordinateTransformation(gisKeyWordQueryReq);
        } catch (Exception e) {
            res = Result.fail(Constants.COORDINATE_TRANSFORMATION_ERROR);
            log.error(Constants.COORDINATE_TRANSFORMATION_ERROR , e);
        }
        return res;
    }

    @ApiOperation(value = "将详细的结构化地址转换为经纬度坐标", notes = "将详细的结构化地址转换为经纬度坐标")
    @Override
    @PostMapping("/gis/adressToCode")
    public Result adressToCode(GisKeyWordQueryReq gisKeyWordQueryReq) {
        Result res;
        try {
//            GisMapServiceAPI gisMapService = gisMapServiceFactory.getGisMapService(gisKeyWordQueryReq.getMapType());
//            res = gisMapService.adressToCode(gisKeyWordQueryReq);
            PoiGeocodingQueryReq req = new PoiGeocodingQueryReq();
            req.setAddress(gisKeyWordQueryReq.getAddress());
            GisMapServiceEnums enums = GisMapServiceEnums.get(req.getMapType());
            if (StringUtils.isEmpty(req.getMapType()) || enums.equals(GisMapServiceEnums.MAP_WORLD)){
                enums = GisMapServiceEnums.GD_WORLD;
            }
            GisMapService mapService = gisMapFactory.get(enums.getServiceName());
            req.setEncryptRequestFlag(enums == GisMapServiceEnums.OFFLINE_ENCRYPT_WORLD);
            List<PoiGeocodingQueryRes> queryRes = mapService.poiGeocodingQuery(req);
            res = Result.data(queryRes);
        } catch (Exception e) {
            res = Result.fail(Constants.ADDRESS_TO_CODE_ERROR);
            log.error(Constants.ADDRESS_TO_CODE_ERROR , e);
        }
        return res;
    }


    /**
     * 改用codeToAdress
     *
     * @param gisKeyWordQueryReq
     * @return
     */
    @ApiOperation(value = "将坐标信息转化行政区域信息", notes = "将坐标信息转化行政区域信息")
    @Override
    @PostMapping("/gis/countryAreaQuery")
    @Deprecated
    public Result countryAreaQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        Result res;
        try {
            GisMapServiceAPI gisMapService = gisMapServiceFactory.getGisMapService(gisKeyWordQueryReq.getMapType());
//            res = gisService.lnglat2Geo(gisKeyWordQueryReq);
            res = gisMapService.countryAreaQuery(gisKeyWordQueryReq);
        } catch (Exception e) {
            res = Result.fail(Constants.ACOUNTRY_AREA_QUERY_ERROR);
            log.error(Constants.ACOUNTRY_AREA_QUERY_ERROR , e);
        }
        return res;
    }

    @ApiOperation(value = "获得行政区经纬度", notes = "获得行政区经纬度")
    @Override
    @PostMapping("/gis/getLatitude")
    public Result getLatitude(GisKeyWordQueryReq gisKeyWordQueryReq) {
        Result res;
        //请求参数不为空,获取行政区经纬度
        if (StringUtils.isNotEmpty(gisKeyWordQueryReq.getAdCode())
                || StringUtils.isNotEmpty(gisKeyWordQueryReq.getAreaName())) {
            Map<String, Object> map = gisService.getLatitude(gisKeyWordQueryReq);
            res = Result.data(map);
        } else {
            res = Result.fail(Constants.PARAMS_ERROR);
        }
        return res;

    }

    @ApiOperation(value = "行政区域查询", notes = "行政区域查询")
    @Override
    @PostMapping("/gis/districtQuery")
    public Result<List<DistrictsVO>> districtQuery(GisKeyWordQueryReq gisKeyWordQueryReq) {
        try {
            log.info("查询行政区划 {}", JSON.toJSONString(gisKeyWordQueryReq));
            GisMapServiceAPI gisMapService = gisMapServiceFactory.getGisMapService(gisKeyWordQueryReq.getMapType());
            JSONArray jsonObjectArr = JSONArray.parseArray(gisMapService.districtQuery(gisKeyWordQueryReq).getData().toString());
            List<DistrictsVO> list = jsonObjectArr.toJavaList(DistrictsVO.class);

            return Result.data(list);
        } catch (Exception e) {
            log.error(Constants.DISTRICT_ERROR , e);
            return Result.fail(Constants.DISTRICT_ERROR);

        }
    }

    @ApiOperation(value = "驾驶导航", notes = "驾驶导航")
    @Override
    @PostMapping("/gis/driveNavigation")
    public Result<DriveNavigationRes> driveNavigation(DriveNavigationReq req) {
        try {
            log.info("查询驾驶导航 {}", JSON.toJSONString(req));
            GisMapServiceAPI gisMapService = gisMapServiceFactory.getGisMapService(req.getMapType());
            return Result.data(gisMapService.driveNavigation(req));
        } catch (Exception e) {
            log.error("驾驶导航查询失败" , e);
            return Result.fail("驾驶导航查询失败");

        }
    }

    @ApiOperation(value = "驾驶导航", notes = "驾驶导航")
    @Override
    @PostMapping("/gis/pedestrianNavigation")
    public Result<PedestrianNavigationRes> pedestrianNavigation(PedestrianNavigationReq req) {
        try {
            log.info("查询步行导航 {}", JSON.toJSONString(req));
            GisMapServiceAPI gisMapService = gisMapServiceFactory.getGisMapService(req.getMapType());
            return Result.data(gisMapService.pedestrianNavigation(req));
        } catch (Exception e) {
            log.error("步行导航查询失败" , e);
            return Result.fail("步行导航查询失败");

        }
    }
    @ApiOperation(value = "驾驶导航", notes = "驾驶导航")
    @Override
    @PostMapping("/gis/busNavigation")
    public Result<BusNavigationRes> busNavigation(BusNavigationReq req) {
        try {
            log.info("查询公交驶导航 {}", JSON.toJSONString(req));
            GisMapServiceAPI gisMapService = gisMapServiceFactory.getGisMapService(req.getMapType());
            return Result.data(gisMapService.busNavigation(req));
        } catch (Exception e) {
            log.error("公交导航查询失败" , e);
            return Result.fail("公交导航查询失败");

        }
    }


}