package com.chinatower.bdm.modules.grid.dao;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 图层解析返回数据
 * @data 创建时间：2023/2/17 16:24
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@ApiModel(value = "网格解析返回实体")
public class Wgs84DTO extends BboxDTO{
    /**
     * 前缀地址
     */
    @ApiModelProperty(value = "geoserver 前缀地址")
    private String url;
    /**
     * 前缀地址
     */
    @ApiModelProperty(value = "geoserver 前缀地址")
    private String viewUrl;
    /**
     * 工作空间:图层名称
     */
    @ApiModelProperty(value = "工作空间:图层名称")
    private String layer_name;
    /**
     * 经纬度属性名称
     */
    @ApiModelProperty(value = "经纬度属性名称")
    private String the_geom;
    private String rootUrl;
    private String rootName;
    private String workspace;
    private String layerName;


}
