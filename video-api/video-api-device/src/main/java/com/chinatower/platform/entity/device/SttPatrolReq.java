package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 巡航计划
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SttPatrolReq extends DeviceSouthCommonDTO {

    @ApiModelProperty(value = "巡航路径id", required = true)
    @NotNull(message = "巡航路径id不能为空")
    private Long patrolId;
    
    @ApiModelProperty(value = "巡航控制类型 1-停用；2-启用", required = true)
    @NotNull(message = "巡航控制类型不能为空")
    private int patrolType;
}
