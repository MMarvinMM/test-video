package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class WeatherStatisticsVO implements Serializable {

    //数据观测时间
    @ApiModelProperty(value = "数据观测时间")
    private String date;

    //温度，默认单位：摄氏度
    @ApiModelProperty(value = "温度，默认单位：摄氏度")
    private String temp;

    //当天每小时相对湿度，百分比数值
    @ApiModelProperty(value = "操作的集合类型")
    private String humidity;
}
