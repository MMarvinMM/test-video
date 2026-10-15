package com.chinatower.device.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class UavNestLocationVO {

    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    /**
     * 纬度
     */
    @ApiModelProperty(value = "纬度")
    private String latitude;

    /**
     * 经度
     */
    @ApiModelProperty(value = "经度")
    private String longitude;

    /**
     * 省份编码
     */
    @ApiModelProperty(value = "省份编码")
    private String provinceCode;

    /**
     * 地市编码
     */
    @ApiModelProperty(value = "地市编码")
    private String cityCode;

    /**
     * 区县编码
     */
    @ApiModelProperty(value = "区县编码")
    private String countyCode;

    /**
     * 乡镇编码
     */
    @ApiModelProperty(value = "乡镇编码")
    private String townCode;

    /**
     * 无人机地址
     */
    @ApiModelProperty(value = "无人机地址")
    private String address;

    /**
     * 海拔
     */
    @ApiModelProperty(value = "海拔")
    private String altitude;

    /**
     * 是否位置固定 1 是  0 否
     */
    private Integer positionFix;

    /**
     * 经纬度更新时间
     */
    private Date latLonUpdateTime;
}
