package com.chinatower.industry.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceAggregationLayerRes {

    /**
     * 省编码
     */
    private String provinceId;


    /**
     * 市编码
     */
    private String cityId;


    /**
     * 区编码
     */
    private String countyId;


    /**
     * 街道编码
     */
    private String townCode;

    /**
     * 行政区划名称
     */
    private String areaName;

    /**
     * 摄像机数量
     */
    private long deviceCount;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;
}
