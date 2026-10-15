package com.chinatower.device.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 广播任务策略表
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HornTaskStrategyConfVO implements Serializable {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("任务id")
    private Long taskId;

    @ApiModelProperty("启动策略 0-单次 1-循环")
    private Integer startupPolicy;

    @ApiModelProperty("有效日期")
    private String validDate;

    @ApiModelProperty("失效日期")
    private String invalidDate;

    @ApiModelProperty("循环日期 如 1,2,3,4,5,6,7")
    private List<Integer> cycleDate;

    @ApiModelProperty("播放策略 0-播放次数 1-持续时长 2-播放时间段")
    private Integer playPolicy;

    @ApiModelProperty("播放次数")
    private Integer playTimes;

    @ApiModelProperty("播放时长")
    private Integer playDuration;

    @ApiModelProperty("播放时长")
    private List<HornTimeInfo> timeInfo;
}
