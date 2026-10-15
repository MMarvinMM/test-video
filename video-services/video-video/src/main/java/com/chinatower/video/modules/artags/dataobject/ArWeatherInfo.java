package com.chinatower.video.modules.artags.dataobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @Author: doujc
* @Description: 天气信息回参
* @Date: 2023/6/25
* @Param:
* @Return:
* @version:
*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArWeatherInfo {

    @ApiModelProperty("摄像机编码")
    private String deviceCode;

    @ApiModelProperty("温度，默认单位：摄氏度")
    private float temp;

    @ApiModelProperty("体感温度，默认单位：摄氏度")
    private float feelsLike;

    @ApiModelProperty("天气状况和图标的代码")
    private String icon;

    @ApiModelProperty("天气状况的文字描述，包括阴晴雨雪等天气状态的描述")
    private String text;

    @ApiModelProperty("空气质量指数")
    private float aqi;

    @ApiModelProperty("空气质量指数等级")
    private int level;

    @ApiModelProperty("空气质量指数级别")
    private String category;

    @ApiModelProperty("空气质量的主要污染物，空气质量为优时，返回值为NA")
    private String primary;

    @ApiModelProperty("PM10")
    private String pm10;

    @ApiModelProperty("pm2p5")
    private String pm2p5;



}
