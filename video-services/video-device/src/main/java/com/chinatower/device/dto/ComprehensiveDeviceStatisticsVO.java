package com.chinatower.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备统计
 */
@Data
public class ComprehensiveDeviceStatisticsVO implements Serializable {

    @ApiModelProperty(value = "摄像机数量")
    private Integer deviceNum;

    @ApiModelProperty(value = "在线摄像机数量")
    private Integer onlineDeviceNum;

    @ApiModelProperty(value = "卫星影像数量")
    private Integer satelliteLayerNum;

    @ApiModelProperty(value = "机场数量")
    private Integer uavNestNum;

    @ApiModelProperty(value = "无人机数量")
    private Integer uavNum;

    @ApiModelProperty(value = "执行任务中无人机数量")
    private Integer taskingUavNum;


}
