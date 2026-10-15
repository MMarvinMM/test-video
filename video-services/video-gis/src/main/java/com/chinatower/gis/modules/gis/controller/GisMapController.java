package com.chinatower.gis.modules.gis.controller;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.GisMapServiceAPI;
import com.chinatower.gis.api.entity.Constants;
import com.chinatower.gis.api.entity.GisKeyWordQueryReq;
import com.chinatower.gis.api.entity.map.*;
import com.chinatower.gis.factory.GisMapFactory;
import com.chinatower.gis.factory.service.GisMapService;
import com.chinatower.gis.modules.gis.mapper.GridMapper;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.enums.GisMapServiceEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @desription: 调用外部地图
 * @data 创建时间：2023/8/15 13:36
 * @author chuan hao
 * @version 1.0
 *
 */
@Api(value = "调用外部地图-Api", tags = "调用外部地图接口")
@Slf4j
@RestController
@RequiredArgsConstructor
@SuppressWarnings("all")
public class GisMapController implements GisMapApi {

    private final GisMapFactory gisMapFactory;

    @Resource
    private GridMapper gridMapper;


    @ApiOperation(value = "POI-关键字搜索接口", notes = "POI-关键字搜索接口")
    @Override
    @PostMapping("/gis/poi/keyWordQuery")
    public Result<PoiKeyWordQueryRes> poiKeyWordQuery(GisPoiKeyWordQueryReq req) {
        try {
            GisMapServiceEnums enums = GisMapServiceEnums.get(req.getMapType());
            if (StringUtils.isEmpty(req.getMapType()) || enums.equals(GisMapServiceEnums.MAP_WORLD)){
                enums = GisMapServiceEnums.GD_WORLD;
            }
            GisMapService mapService = gisMapFactory.get(enums.getServiceName());
            PoiKeyWordQueryRes res = mapService.poiKeyWordQuery(req);
            return Result.data(res);
        }catch (Exception e){
            log.error("关键字搜索接口查询错误",e);
        }
        return Result.success("关键字搜索接口查询错误");
    }

    @ApiOperation(value = "POI-半径搜索接口", notes = "POI-半径搜索接口")
    @Override
    @PostMapping("/gis/poi/poiRadiusQuery")
    public Result<PoiRadiusQueryResTableInfo> poiRadiusQuery(PoiRadiusQueryReq req) {
        try {
            GisMapServiceEnums enums = GisMapServiceEnums.get(req.getMapType());
            if (StringUtils.isEmpty(req.getMapType()) || enums.equals(GisMapServiceEnums.GD_WORLD)){
                enums = GisMapServiceEnums.MAP_WORLD;
            }
            GisMapService mapService = gisMapFactory.get(enums.getServiceName());
            PoiRadiusQueryResTableInfo res = mapService.poiRadiusQuery(req);
            return Result.data(res);
        }catch (Exception e){
            log.error("半径搜索接口查询错误",e);
        }
        return Result.success("半径搜索接口查询错误");
    }

    @ApiOperation(value = "POI-多边形搜索接口", notes = "POI-多边形搜索接口")
    @Override
    @PostMapping("/gis/poi/poiPolygonQuery")
    public Result<List<PoiPolygonQueryRes>> poiPolygonQuery(PoiPolygonQueryReq req) {
       try {
           GisMapServiceEnums enums = GisMapServiceEnums.get(req.getMapType());
           if (StringUtils.isEmpty(req.getMapType()) || enums.equals(GisMapServiceEnums.GD_WORLD)){
               enums = GisMapServiceEnums.MAP_WORLD;
           }
           GisMapService mapService = gisMapFactory.get(enums.getServiceName());
           List<PoiPolygonQueryRes> res = mapService.poiPolygonQuery(req);
           return Result.data(res);
       }catch (Exception e){
           log.error("多边形搜索接口查询错误",e);
       }
        return Result.success("多边形搜索接口查询错误");
    }

    @ApiOperation(value = "地理编码查询", notes = "地理编码查询")
    @Override
    @PostMapping("/gis/poi/poiGeocodingQuery")
    public Result<List<PoiGeocodingQueryRes>> poiGeocodingQuery(PoiGeocodingQueryReq req) {
        try {
            GisMapServiceEnums enums = GisMapServiceEnums.get(req.getMapType());
            if (StringUtils.isEmpty(req.getMapType()) || enums.equals(GisMapServiceEnums.MAP_WORLD)){
                enums = GisMapServiceEnums.GD_WORLD;
            }
            GisMapService mapService = gisMapFactory.independentGet(enums.getServiceName());
            List<PoiGeocodingQueryRes> res = mapService.poiGeocodingQuery(req);
            return Result.data(res);
        }catch (Exception e){
            log.error("地理编码查询查询错误",e);
        }
        return Result.success("地理编码查询错误");
    }

    @ApiOperation(value = "逆地理编码查询", notes = "逆地理编码查询")
    @Override
    @PostMapping("/gis/poi/poiContraryGeocodingQuery")
    public Result<List<PoiContraryGeocodingQueryRes>> poiContraryGeocodingQuery(PoiContraryGeocodingQueryReq req) {
        try {
            log.info("逆地理编码查询开始 {}",req.getLocation());
            GisMapServiceEnums enums = GisMapServiceEnums.get(req.getMapType());
            if (StringUtils.isEmpty(req.getMapType()) || enums.equals(GisMapServiceEnums.MAP_WORLD)){
                enums = GisMapServiceEnums.GD_WORLD;
            }
            GisMapService mapService = gisMapFactory.independentGet(enums.getServiceName());
            List<PoiContraryGeocodingQueryRes> res = mapService.poiContraryGeocodingQuery(req);
            return Result.data(res);
        }catch (CustomException e){
            log.error("自定义错误",e);
            return Result.success(e.getMessage());
        }catch (Exception e){
            log.error("逆地理编码查询查询错误",e);
        }
        return Result.success("逆地理编码查询查询错误");
    }

    @ApiOperation(value = "行政区域查询", notes = "行政区域查询")
    @Override
    @PostMapping("/gis/poi/poiAreaQuery")
    public Result<PoiAreaQueryRes> poiAreaQuery(PoiAreaQueryReq req) {
        try {
            GisMapServiceEnums enums = GisMapServiceEnums.get(req.getMapType());
            if (StringUtils.isEmpty(req.getMapType()) || enums.equals(GisMapServiceEnums.MAP_WORLD)){
                enums = GisMapServiceEnums.GD_WORLD;
            }
            GisMapService mapService = gisMapFactory.get(enums.getServiceName());
            PoiAreaQueryRes res = mapService.poiAreaQuery(req);
            return Result.data(res);
        }catch (Exception e){
            log.error("行政区域查询查询错误",e);
        }
        return Result.success("行政区域查询查询错误");
    }

    @ApiOperation(value = "轨迹查询", notes = "轨迹查询")
    @Override
    @PostMapping("/gis/poi/trajectoryQuery")
    public Result<TrajectoryQueryRes> trajectoryQuery(TrajectoryQueryReq req) {
        try {
            GisMapServiceEnums enums = GisMapServiceEnums.get(req.getMapType());
            if (StringUtils.isEmpty(req.getMapType()) || enums.equals(GisMapServiceEnums.MAP_WORLD)){
                enums = null;
            }
            GisMapService mapService = gisMapFactory.get(enums,GisMapServiceEnums.GD_WORLD);
            TrajectoryQueryRes res = mapService.trajectoryQuery(req);
            return Result.data(res);
        }catch (Exception e){
            log.error("轨迹查询查询错误",e);
        }
        return Result.success("轨迹查询查询错误");
    }

    /**
     * 功能描述: POI-输入提示
     * start#
     * capabilityCode:gis_inputtipsQuery |
     * currentVersion:3.5.0 |
     * minVersion:3.5.0 |
     * tempVersion:3.5.0-1 |
     * moduleCode: gis |
     * supplier: 传浩@华苏 |
     * callerDeclaration:
     * interfacePre:/api/gis
     * #end
     **/
    @ApiOperation(value = "输入提示", notes = "输入提示")
    @Override
    @PostMapping("/gis/poi/inputtips")
    public Result<List<GisPoiInputtipsQueryRes>> inputtipsQuery(@RequestBody GisPoiKeyWordQueryReq req) {
        try {
            GisMapService mapService = gisMapFactory.get(GisMapServiceEnums.GD_WORLD,GisMapServiceEnums.GD_WORLD);
            List<GisPoiInputtipsQueryRes> res = mapService.inputtipsQuery(req);
            return Result.data(res);
        }catch (Exception e){
            log.error("输入提示查询查询错误",e);
        }
        return Result.success("输入提示查询查询错误");
    }


    @ApiOperation(value = "网格压缩", notes = "网格压缩")
    @Override
    @PostMapping("/gis/gridCompress")
    public Result<String> gridCompress(String gridRange) {
        log.info("gridRange初始值为："+gridRange);
        try {
            gridMapper.updateChouxi(gridRange);
            String a = gridMapper.gridCompress();
            log.info("gridRange返回值为："+ a);
            return Result.data(a);
        }catch (Exception e){
            log.error("网格压缩错误",e);
        }
        return Result.success("网格压缩错误");
    }
}
