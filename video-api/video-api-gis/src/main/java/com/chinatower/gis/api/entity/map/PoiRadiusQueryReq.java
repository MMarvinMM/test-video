package com.chinatower.gis.api.entity.map;

import com.chinatower.gis.api.entity.GisBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 半径搜索
 * @data 创建时间：2023/8/15 14:55
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PoiRadiusQueryReq extends GisBaseReq {
    /**
    * 关键字
    * @required false
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
     * 单位：米 默认3000，返回数据按照经纬度距离排序
     * @required false
     */
    @ApiModelProperty(value = "单位：米 默认3000，返回数据按照经纬度距离排序")
    private Integer radius = 3000;
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
