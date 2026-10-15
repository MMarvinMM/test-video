package com.chinatower.platform.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "历史空气质量对象",description = "历史空气质量对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherAirDailyV2DTO implements Serializable {

    @ApiModelProperty(value = "地市区域编码")
    private String adcode;

    @ApiModelProperty(value = "地市名称")
    private String locationNameZh;

    //空气质量数据发布时间
    @ApiModelProperty(value = "空气质量数据发布时间")
    private String pubTime;

    //空气质量数据发布时间
    @ApiModelProperty(value = "空气质量数据发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date pubTimeDate;

    //空气质量指数
    @ApiModelProperty(value = "空气质量指数")
    private float aqi;

    //空气质量指数等级
    @ApiModelProperty(value = "空气质量指数等级")
    private int level;

    //空气质量指数级别
    @ApiModelProperty(value = "空气质量指数级别")
    private String category;

    //空气质量的主要污染物，空气质量为优时，返回值为NA
    @ApiModelProperty(value = "空气质量的主要污染物，空气质量为优时，返回值为NA")
    private String primary;

    //PM10
    @ApiModelProperty(value = "PM10")
    private String pm10;

    //pm2p5
    @ApiModelProperty(value = "pm2p5")
    private String pm2p5;

    //二氧化氮
    @ApiModelProperty(value = "二氧化氮")
    private Float no2;

    //二氧化硫
    @ApiModelProperty(value = "二氧化硫")
    private Float so2;

    //一氧化碳
    @ApiModelProperty(value = "一氧化碳")
    private Double co;

    //臭氧
    @ApiModelProperty(value = "臭氧")
    private Float o3;

    /**
     * 省份编码
     */
    @ApiModelProperty(value = "省份编码")
    private String provinceId;

    /**
     * 地市编码
     */
    @ApiModelProperty(value = "地市编码")
    private String cityId;

    /**
     * 区县编码
     */
    @ApiModelProperty(value = "区县编码")
    private String countyId;

    /**
     * 省份名称
     */
    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    /**
     * 地市名称
     */
    @ApiModelProperty(value = "地市名称")
    private String cityName;

    /**
     * 区县名称
     */
    @ApiModelProperty(value = "区县名称")
    private String countyName;

}
