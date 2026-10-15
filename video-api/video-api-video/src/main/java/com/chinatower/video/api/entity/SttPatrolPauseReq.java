package com.chinatower.video.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 巡航计划
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SttPatrolPauseReq implements Serializable {

    @ApiModelProperty(value = "巡航路径id", required = true)
    private Long patrolId;

    /**
     * 设备编码
     * @required true
     */
    @ApiModelProperty(value = "设备编码", required = true)
    @NotNull(message = "设备编码不能为空")
    private String deviceCode;

    @ApiModelProperty(value = "通道编码", required = true)
//    @NotNull(message = "通道编码不能为空")
    private String channelCode;

    /**
     * 暂停/恢复状态:3-暂停；4-恢复
     * @required true
     */
    @ApiModelProperty(value = "暂停/恢复状态:3-暂停；4-恢复", required = true)
    @NotNull(message = "巡航暂停/恢复状态不能为空")
    private String pauseResumeStatus;

    //暂停时间
    private String pauseDuration;

    //暂停落入时间
    private String pauseDate;

    private Long patrolType;
    private Integer resource = 1;

    //受理节点编码
    private String acceptNode;
    //接入节点编码
    private String accessNode;

    public boolean ifPauseResumeStatusThree() {
        return VideoConstants.THREE_STR.equals(pauseResumeStatus);
    }

    public boolean ifPauseResumeStatusFour() {
        return VideoConstants.FOUR_STR.equals(pauseResumeStatus);
    }

}
