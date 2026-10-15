package com.chinatower.video.modules.artags.dataobject;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
* @Author: doujc
* @Description: 设备、天气信息查询响应类
* @Date: 2023/6/25
* @Param:
* @Return:
* @version:
*/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArDeviceDTO {

    @ApiModelProperty("摄像机编码")
    private List<String> codeList;

    @ApiModelProperty("物联设备编码/雷达设备编码")
    private List<String> iotCodeList;
    
    @ApiModelProperty("喇叭设备编码")
    private List<String> hornCodeList;

    @ApiModelProperty("无人机设备编码")
    private List<String> uavCodeList;

    @ApiModelProperty("摄像机设备天气信息")
    private List<ArWeatherInfo> weatherList;

    @ApiModelProperty("物联设备天气信息/雷达设备天气信息")
    private List<ArWeatherInfo> iotWeatherList;

    @ApiModelProperty("喇叭设备天气信息")
    private List<ArWeatherInfo> hornWeatherList;

    @ApiModelProperty("无人机设备天气信息")
    private List<ArWeatherInfo> uavWeatherList;
}
