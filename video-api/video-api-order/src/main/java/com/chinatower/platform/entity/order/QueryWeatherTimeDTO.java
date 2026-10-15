package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "根据时间查询天气入参")
public class QueryWeatherTimeDTO implements Serializable {

    @ApiModelProperty(value = "地区编码")
    private String adCode;

    @ApiModelProperty(value = "天气时间")
    private String weatherTime;
}
