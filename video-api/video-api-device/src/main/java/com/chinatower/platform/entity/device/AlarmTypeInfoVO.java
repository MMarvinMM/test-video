package com.chinatower.platform.entity.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlarmTypeInfoVO {

    @ApiModelProperty(value = "告警类型", required = true)
    private String alarmType;

    @ApiModelProperty(value = "告警类型名称", required = true)
    private String alarmTypeName;

}
