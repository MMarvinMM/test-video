package com.chinatower.gis.api.entity.map;

import com.chinatower.gis.api.entity.GisBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 多边形查询参数
 * @data 创建时间：2023/8/15 15:06
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PoiPolygonQueryReq extends GisBaseReq {
    /**
    * 关键字
    * @required true
    */
    @ApiModelProperty(value = "关键字")
    private String keyWord;
    /**
     * 	经度和纬度用","分割，首尾坐标对需相同。实例(x1,y1,x2,y2,x3,y3,x4,y4,x5,y5,x1,y1)
     * 	多边形区域。多个经纬度用“;”分割。首位需相交
     * 	@required true
     */
    @ApiModelProperty(value = "经度和纬度用,分割，首尾坐标对需相同。实例(x1,y1,x2,y2,x3,y3,x4,y4,x5,y5,x1,y1)")
    private String polygon;
    /**
     * 目前查询的级别 1-18级
     * @required false
     */
    @ApiModelProperty(value = "目前查询的级别 1-18级")
    private String level;
    /**
     * 1:普通搜索（含地铁公交） 7：地名搜索
     * @required false
     */
    @ApiModelProperty(value = "1:普通搜索（含地铁公交） 7：地名搜索")
    private String queryType;
    /**
     * POI数据类型 基于天地图的POI数据类型
     * @required false
     */
    @ApiModelProperty(value = " POI数据类型 基于天地图的POI数据类型")
    private String poiTypes;
}
