package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskContentStrategyInfo {

    @ApiModelProperty(value = "循环次数 默认设置1")
    private Integer loopTimes = 1;

    @ApiModelProperty(value = "播放顺序")
    private Integer playOrder;

    @ApiModelProperty(value = "大小")
    private Integer size;
}
