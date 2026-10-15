package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotNull;

/**
 * 巡航计划
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("SttPatrolSimpleReqVideo")
public class SttPatrolSimpleReq {
    
    @NotNull(message = "巡航路径id不能为空")
    @ApiModelProperty(value = "巡航路径id", required = true)
    private Long patrolId;
    
    @NotNull(message = "巡航控制类型不能为空")
    @ApiModelProperty(value = "巡航控制类型 1-停用；2-启用", required = true)
    private int patrolType;

    //受理节点编码
    private String acceptNode;
    //接入节点编码
    private String accessNode;
}
