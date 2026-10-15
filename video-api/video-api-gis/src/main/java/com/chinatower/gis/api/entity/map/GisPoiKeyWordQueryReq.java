package com.chinatower.gis.api.entity.map;


import com.chinatower.gis.api.entity.GisBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: POI-关键字搜索接口
 * @data 创建时间：2023/8/15 11:27
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class GisPoiKeyWordQueryReq extends GisBaseReq {
    /**
    * 关键字
    * @required true
    */
    @ApiModelProperty(value = "关键字")
    private String keyWord;
    /**
     * 地图经纬度 (“经度,维度”)
     * @required false
     */
    @ApiModelProperty(value = "地图经纬度 (“经度,维度”)")
    private String location;
    /**
     * 指定行政区的国标码（行政区划编码表）严格按照sys_area表
     * @required false
     */
    @ApiModelProperty(value = "指定行政区的国标码（行政区划编码表）严格按照sys_area表")
    private String adCode;
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
