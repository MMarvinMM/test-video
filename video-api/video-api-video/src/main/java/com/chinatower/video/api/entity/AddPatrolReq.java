package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 巡航计划
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("AddPatrolReqVideo")
public class AddPatrolReq extends DeviceSouthCommonDTO {

    @ApiModelProperty(value = "巡航名称", required = true)
    @NotNull(message = "巡航名称不能为空")
//    @Size(min = 0, max = 20, message = "巡航名称长度不能超过20个字符")
    private String patrolName;

    @ApiModelProperty(value = "巡航计划时间段")
    private String startTimePeriod;
    @ApiModelProperty(value = "预约启动状态：1-停用；2-启用；")
    private String startStop;
    @ApiModelProperty(value = "预约启动状态：1-停用；2-启用；")
    private String ifCruise;
    @ApiModelProperty(value = "预约启动时间")
    private String startStopDate;
    @ApiModelProperty(value = "预置点列表", required = true)
    @Valid
    private List<AddPatrolPresetReq> presets;

    private String userName;

    private String industryCode;

    private String appVerCode;

    //受理节点编码
    private String acceptNode;
    //接入节点编码
    private String accessNode;

}
