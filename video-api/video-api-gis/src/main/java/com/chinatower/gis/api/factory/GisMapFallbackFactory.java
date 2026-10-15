package com.chinatower.gis.api.factory;

import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.GisMapApi;
import com.chinatower.gis.api.entity.GisKeyWordQueryReq;
import com.chinatower.gis.api.entity.map.*;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @desription: gis 地图接口
 * @data 创建时间：2023/8/15 11:26
 * @author chuan hao
 * @version 1.0
 *
 */
@Component
public class GisMapFallbackFactory  implements FallbackFactory<GisMapApi> {
    @Override
    public GisMapApi create(Throwable throwable) {
        return new GisMapApi() {
            @Override
            public Result<PoiKeyWordQueryRes> poiKeyWordQuery(GisPoiKeyWordQueryReq req) {
                return Result.fail("POI关键字查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<GisPoiInputtipsQueryRes>> inputtipsQuery(GisPoiKeyWordQueryReq req) {
                return Result.fail("输入提示查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<PoiRadiusQueryResTableInfo> poiRadiusQuery(PoiRadiusQueryReq req) {
                return Result.fail("POI半径查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<PoiPolygonQueryRes>> poiPolygonQuery(PoiPolygonQueryReq req) {
                return Result.fail("POI多边形查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<PoiGeocodingQueryRes>> poiGeocodingQuery(PoiGeocodingQueryReq req) {
                return Result.fail("地理编码查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<List<PoiContraryGeocodingQueryRes>> poiContraryGeocodingQuery(PoiContraryGeocodingQueryReq req) {
                return Result.fail("逆地理编码查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<PoiAreaQueryRes> poiAreaQuery(PoiAreaQueryReq req) {
                return Result.fail("行政区域查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<TrajectoryQueryRes> trajectoryQuery(TrajectoryQueryReq req) {
                return Result.fail("轨迹查询失败:" + throwable.getMessage());
            }

            @Override
            public Result<String> gridCompress(String gridRange) {
                return Result.fail("网格压缩失败:" + throwable.getMessage());
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
    }
}
