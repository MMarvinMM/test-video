package com.chinatower.gis.api.entity.map;

import com.chinatower.gis.api.entity.GisBaseReq;
import lombok.Data;

/**
 * @desription: 逆地理编码查询
 * @data 创建时间：2023/8/15 15:14
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PoiContraryGeocodingQueryReq extends GisBaseReq {


    /**
    * 地图经纬度(“经度,维度”) 经纬度用逗号 拼接
    * @required true
    */
    private String location;
    /**
     * POI数据类型
     * 基于天地图的POI数据类型（离线地图时需确认与天地图差异）
     * @required false
     */
    private String poiTypes;

    /**
     *搜索半径，单位米，默认1000米
     * @required false
     */
    private Integer radius = 3000;
}
