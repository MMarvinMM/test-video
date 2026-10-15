package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "查询天气统计数据入参")
public class QueryWeatherStatisticsDTO implements Serializable {

    @ApiModelProperty(value = "地区编码")
    private String adCode;

    @ApiModelProperty(value = "统计类型 1-按月分组统计 2-按天分组统计")
    private Integer statisticType;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
