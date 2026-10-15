package com.chinatower.bdm.modules.timeline.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LayerInfoDto {
    private String layerId;
    private String fileUrl;
    private String baselinePicUrl;
    /**
     * 最大经度
     */
    @ApiModelProperty("最大经度")
    private String maxx;
    /**
     * 最大纬度
     */
    @ApiModelProperty("最大纬度")
    private String maxy;

    /**
     * 最小经度
     */
    @ApiModelProperty("最小经度")
    private String minx;

    /**
     * 最小纬度
     */
    @ApiModelProperty("最小纬度")
    private String miny;
    /**
     * 服务器地址
     */
    @ApiModelProperty(value = "服务器地址")
    private String rootGeoserverUrl;

    /**
     * 二维对象属性
     */
    @ApiModelProperty(value = "二维对象属性")
    private String geoType;

    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 更新时间
     */
    private String updateTime;
    /**
     * 对象存储地址
     */
    private String obsUrl;

    /**
     * 原始图片地址
     */
    private String originUrl;

    /**
     * 图层名称
     */
    private String customName;
}
