package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskStrategyInfo {

    @ApiModelProperty(value = "时长 单位:ms")
    private Integer duration;

    @ApiModelProperty(value = "循环次数 1~99")
    private Integer loopTimes;

    @ApiModelProperty(value = "停止时间")
    private String stopTime;

    @ApiModelProperty(value = "间隔时间")
    private Integer intervalTime;

    @ApiModelProperty(value = "设置循环播放需要排除的日期")
    private List<String> taskExceptDateList;

    @ApiModelProperty(value = "开始有效期 周循环任务必填")
    private String validTimeBegin;

    @ApiModelProperty(value = "结束有效期 周循环任务必填")
    private String validTimeEnd;

}
