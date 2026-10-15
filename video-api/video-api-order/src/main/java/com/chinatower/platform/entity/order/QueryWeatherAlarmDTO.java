package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "告警天气查询入参")
public class QueryWeatherAlarmDTO implements Serializable {

    @ApiModelProperty(value = "地区编码")
    private String adCode;

    @ApiModelProperty(value = "告警时间")
    private String alarmTime;
}
