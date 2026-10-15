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
public class ModPatrolReq extends AddPatrolReq {
    
    @NotNull(message = "巡航路径id不能为空")
    @ApiModelProperty(value = "巡航路径id", required = true)
    private Long patrolId;
}
