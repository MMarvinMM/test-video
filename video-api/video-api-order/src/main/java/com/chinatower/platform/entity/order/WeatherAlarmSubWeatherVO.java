package com.chinatower.platform.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WeatherAlarmSubWeatherVO implements Serializable {

    @ApiModelProperty(value = "地市区域编码")
    private String adcode;

    @ApiModelProperty(value = "地市名称")
    private String locationNameZh;

    //数据观测时间
    @ApiModelProperty(value = "数据观测时间")
    private String obsTime;

    //数据观测时间
    @ApiModelProperty(value = "数据观测时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date obsTimeDate;

    //温度，默认单位：摄氏度
    @ApiModelProperty(value = "温度，默认单位：摄氏度")
    private float temp;

    //体感温度，默认单位：摄氏度
    @ApiModelProperty(value = "体感温度，默认单位：摄氏度")
    private float feelsLike;

    //天气状况和图标的代码
    @ApiModelProperty(value = "天气状况和图标的代码")
    private String icon;

    //天气状况的文字描述，包括阴晴雨雪等天气状态的描述
    @ApiModelProperty(value = "天气状况的文字描述，包括阴晴雨雪等天气状态的描述")
    private String text;

    //风向360角度
    @ApiModelProperty(value = "风向360角度")
    private float wind360;

    //风向
    @ApiModelProperty(value = "风向")
    private String windDir;

    //风力等级
    @ApiModelProperty(value = "风力等级")
    private float windScale;

    //风速，公里/小时
    @ApiModelProperty(value = "风速，公里/小时")
    private float windSpeed;

    //当天每小时相对湿度，百分比数值
    @ApiModelProperty(value = "操作的集合类型")
    private float humidity;

    //当前小时累计降水量，默认单位：毫米
    @ApiModelProperty(value = "当前小时累计降水量，默认单位：毫米")
    private float precip;

    //大气压强，默认单位：百帕
    @ApiModelProperty(value = "大气压强，默认单位：百帕")
    private float pressure;

    //能见度，默认单位：公里
    @ApiModelProperty(value = "能见度，默认单位：公里")
    private float vis;

    //云量，百分比数值
    @ApiModelProperty(value = "云量，百分比数值")
    private float cloud;

    //露点温度。可能为空
    @ApiModelProperty(value = "露点温度")
    private float dew;

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
