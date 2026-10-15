package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeInfo {

    @ApiModelProperty(value = "小时")
    private Integer hour;

    @ApiModelProperty(value = "分钟")
    private Integer minute;

    @ApiModelProperty(value = "秒")
    private Integer second;
}
