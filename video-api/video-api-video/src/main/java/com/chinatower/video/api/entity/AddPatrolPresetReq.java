package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 巡航计划
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("AddPatrolPresetReqVideo")
public class AddPatrolPresetReq  implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @NotNull(message = "预置点id不能为空")
    @ApiModelProperty(value = "预置点id", required = true)
    private Integer presetId;
    
    @NotNull(message = "停留时间不能为空")
    @Min(value = 30, message = "停留时间不能小于30秒")
    @ApiModelProperty(value = "停留时间 单位：秒", required = true)
    private Long staySeconds;
    
    @NotNull(message = "转置到目标预置点的速度不能为空")
    @ApiModelProperty(value = "转置到目标预置点的速度：0-全速； 1-1/2倍速； 2-1/4倍速； 3-1/8倍速； 4-1/16倍速", required = true)
    private Integer speed;
    
    @ApiModelProperty(value = "预置位排序", required = true)
    @NotNull(message = "预置位排序不能为空")
    private Integer sort;

    private String channelCode;

    private String patrolId;

    private String operator;

    private String deviceCode;

    private String tenantId;

    private String industryCode;

    private List<AddPatrolPresetReq> list;

}
