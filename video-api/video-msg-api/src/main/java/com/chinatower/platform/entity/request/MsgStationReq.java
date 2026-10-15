package com.chinatower.platform.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;


/**
 * @ClassName MsgStationReq
 * @Description 消息VO
 * @Author wjd
 * @Date 2022/1/18 17:31
 */
@Data
@ToString
@Builder
@ApiModel(value = "消息请求-对象", description = "消息请求-对象")
@AllArgsConstructor
public class MsgStationReq {
    /**
     * 1系统提醒
     * */
    public static final String SYSTEM_WARNING="1";
    /**
     * 空字符串
     * */
    public static final String EMPTY = "";

    /**
     * 唯一id
     * */
    @ApiModelProperty(value = "唯一id")
    @NotNull
    private String uniId;
    /**
     * 告警/内容ID
     * */
    @ApiModelProperty(value = "告警/内容ID")
    private String msgId;
    /**
     * 用户ID
     * */
    @ApiModelProperty(value = "用户ID")
    private String userId;
    /**
     * 通知类型:1系统提醒
     * */
    @ApiModelProperty(value = "通知类型:1系统提醒")
    private String notifyType;
    /**
     * 通知消息标题
     * */
    @ApiModelProperty(value = "通知消息标题")
    private String notifyTitle;
    /**
     * 创建人
     * */
    @ApiModelProperty(value = "创建人")
    private String creator;
    /**
     * 创建时间
     * */
    @ApiModelProperty(value = "创建时间")
    private String creatorDate;
    /**
     * 创建人
     * */
    @ApiModelProperty(value = "更新人")
    private String modifier;
    /**
     * 开始时间
     * */
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    /**
     * 结束时间
     * */
    @ApiModelProperty(value = "结束时间")
    private String endTime;
    /**
     * 是否已读：1是0否
     * */
    @ApiModelProperty(value = "是否已读：1是0否")
    private String ifRead;
    /**
     * 页码
     * */
    @ApiModelProperty(value = "页码")
    private int pageNum ;
    /**
     * 页数量
     * */
    @ApiModelProperty(value = "页数量")
    private int pageSize;
    /**
     * 删除标识
     * */
    @ApiModelProperty(value = "删除标识")
    private String deleteFlag;
    /**
     * 地址
     * */
    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 告警事件状态
     * */
    @ApiModelProperty(value = "告警事件状态")
    private String orderStatus;




}
