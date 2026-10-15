package com.chinatower.gis.factory.service;

import com.chinatower.common.entity.Result;
import com.chinatower.gis.api.entity.map.*;

import java.util.List;

/**
 * @desription:
 * @data 创建时间：2023/8/15 13:39
 * @author chuan hao
 * @version 1.0
 *
 */
public interface GisMapService {

    /**
     *
     * @param req 请求参数
     * @return poi 关键子查询
     */
    PoiKeyWordQueryRes poiKeyWordQuery(GisPoiKeyWordQueryReq req);

    default List<GisPoiInputtipsQueryRes> inputtipsQuery(GisPoiKeyWordQueryReq req){
        return null;
    }

    /**
     *
     * @param req 搜索参数
     * @return 半径搜索
     */
    PoiRadiusQueryResTableInfo poiRadiusQuery(PoiRadiusQueryReq req);

    /**
     *
     * @param req
     * @return 多边形搜索
     */
    List<PoiPolygonQueryRes> poiPolygonQuery(PoiPolygonQueryReq req);


    /**
     *
     * @param req
     * @return 地理编码查询
     */
    List<PoiGeocodingQueryRes> poiGeocodingQuery(PoiGeocodingQueryReq req);

    /**
     *
     * @param req
     * @return 逆地理编码查询
     */
    List<PoiContraryGeocodingQueryRes> poiContraryGeocodingQuery(PoiContraryGeocodingQueryReq req);

    /**
     *
     * @param req
     * @return 行政区域返回
     */
    PoiAreaQueryRes poiAreaQuery(PoiAreaQueryReq req);

    /**
     *
     * @param req 请求参数
     * @return 轨迹查询
     */
    TrajectoryQueryRes trajectoryQuery(TrajectoryQueryReq req);
}
