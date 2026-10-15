package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskScheduleModelInfo {

    @ApiModelProperty(value = "所选作业类型")
    private String jobType;

    @ApiModelProperty(value = "执行时间")
    private String date;

    @ApiModelProperty(value = "一周的哪几天")
    private List<Integer> dayOfWeeks;

    @ApiModelProperty(value = "周循环任务使用")
    private List<TimeInfo> playTimeInfoList;
}
