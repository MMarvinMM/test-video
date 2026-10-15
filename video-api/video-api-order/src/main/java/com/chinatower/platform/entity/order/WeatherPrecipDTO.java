package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class WeatherPrecipDTO implements Serializable {

    @ApiModelProperty(value = "连续无降水天数")
    private String precip;
}