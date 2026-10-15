package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 广播任务调用南向接口实体类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HornTaskNorthVO extends BaseDataScopeEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务ID")
    private String taskId;

    @ApiModelProperty(value = "任务类型")
    private String taskType;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "是否录音")
    private Boolean recorded = false;

    @ApiModelProperty(value = "加权优先级")
    private Integer weightedPriority;

    @ApiModelProperty(value = "音量")
    private Integer volume;

    @ApiModelProperty(value = "创建人")
    private String createUser;

    @ApiModelProperty(value = "播放模式")
    private String playMode = "ORDER";

    @ApiModelProperty(value = "广播类型")
    private String broadcastType;

    @ApiModelProperty(value = "优先播放文件序号")
    private Integer priorityIndex;

    @ApiModelProperty(value = "任务执行后是否删除")
    private Boolean autoDelete;

    @ApiModelProperty(value = "任务创建后是否立即启动")
    private Boolean autoExecute;

    @ApiModelProperty(value = "任务策略对象")
    private TaskStrategyInfo taskStrategyInfo;

    @ApiModelProperty(value = "任务内容对象")
    private List<TaskContentWithStrategyInfo> taskContentWithStrategyInfoList;

    @ApiModelProperty(value = "广播范围对象列表")
    private List<RangeInfo> rangeInfoList;

    @ApiModelProperty(value = "任务执行对象")
    private TaskScheduleModelInfo taskScheduleModelInfo;

    @ApiModelProperty(value = "受理节点")
    private String acceptNode;

    @ApiModelProperty(value = "用于调用不通节点的广播平台，调用前清掉")
    private String accessNode;

}
