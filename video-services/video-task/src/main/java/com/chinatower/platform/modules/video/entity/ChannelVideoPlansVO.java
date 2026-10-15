package com.chinatower.platform.modules.video.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: ChannelVideoPlanVO
 * @Description: 通道录像计划实体
 * @Author: chenmf
 * @Date: 2022-01-24 11:51
 **/
@Data
@ToString
public class ChannelVideoPlansVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    //是否全程录像 默认为0；1-是；0-否；如果为1，timeSections省略；
    @Range(min=0, max=1, message = "是否全程录像值有误")
    @ApiModelProperty(value = "是否全程录像值有误")
    private int isAllDay;
    
    @Valid
    //通道录像计划时间段 当isAllDay字段为1时必填；
    @ApiModelProperty(value = "通道录像计划时间段")
    private List<TimeSectionsVO> timeSections;

    //码流类型 1-主码流；0-子码流；
    @Range(min=0, max=1, message = "码流类型值有误")
    @ApiModelProperty(value = "码流类型值有误")
    private int streamType;

    //存储类型 1-中心存储；0-前端存储；
    @Range(min=0, max=1, message = "存储类型值有误")
    @ApiModelProperty(value = "存储类型值有误")
    private int storageType;

    //存储时长 录像存储的最长周期 单位:天
    @Min(value=1, message = "存储时长需大于等于1天！")
    @ApiModelProperty(value = "存储时长需大于等于1天")
    private int storageDay;

    //是否开启音频 1-是；0-否；
    @Range(min=0, max=1, message = "是否开启音频值有误")
    @ApiModelProperty(value = "是否开启音频值有误")
    private int isAudio;

    //录像计划id
    @ApiModelProperty(value = "录像计划id")
    private Long recordPlanId;

    //录像计划名称
    @ApiModelProperty(value = "录像计划名称")
    private String recordPlanName;

    //录像计划状态
    @Range(min=0, max=1, message = "录像计划状态值有误")
    @ApiModelProperty(value = "录像计划状态")
    private int recordPlanStatus;

    //生效开始日期
    @NotNull(message = "生效开始日期不能为空")
    @ApiModelProperty(value = "生效开始日期")
    private String startDate;

    //生效结束日期
    @NotNull(message = "生效结束日期不能为空")
    @ApiModelProperty(value = "生效结束日期")
    private String endDate;

    //设备编号
    @NotNull(message = "设备编号不能为空")
    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    //通道编号
    @NotNull(message = "通道编号不能为空")
    @ApiModelProperty(value = "通道编号")
    private String channelCode;
    
    //设备名称
    @ApiModelProperty(value = "设备名称")
    private String devName;
    
    //通道名称
    @ApiModelProperty(value = "通道名称")
    private String channelName;

    //客户唯一编码
    @NotNull(message = "客户编码不能为空")
    @ApiModelProperty(value = "客户编码")
    private String tenantId;

    //用户编码
    @ApiModelProperty(value = "用户编码")
    private String userCode;
    
    //订购关系编码
    @ApiModelProperty(value = "订购关系编码")
    private String orderCode;

    //星期
    @ApiModelProperty(value = "星期")
    private String dayOfWeek;

    //开始时间
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    //结束时间
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    
}
