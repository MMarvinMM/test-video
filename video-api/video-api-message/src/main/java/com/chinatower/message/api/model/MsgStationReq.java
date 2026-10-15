package com.chinatower.message.api.model;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


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
public class MsgStationReq   extends BaseDataScopeEntity implements Serializable {
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
//    /**
//     * 删除标识
//     * */
//    @ApiModelProperty(value = "删除标识")
//    private String deleteFlag;
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

    /**
     * 行业版本号
     * */
    @ApiModelProperty(value = "行业版本号")
    private String appVerCode;
    /**
     * 告警状态
     * */
    @ApiModelProperty(value = "告警状态")
    private String alarmFlag;

    /**
     * 显示位置
     * */
    @ApiModelProperty(value = "显示位置")
    private String showType;

    /**
     * 行业FLAG
     * */
    @ApiModelProperty(value = "行业FLAG")
    private String industryFlag;


    /**
     * 扩展信息
     * */
    @ApiModelProperty(value = "扩展信息")
    private String industryExtend;

    @ApiModelProperty(value = "行业编码")
    private String industryCode;

    /**
     * 声音关闭方式 1：自动关闭，2：手动关闭
     * */
    @ApiModelProperty(value = "声音关闭方式 1：自动关闭，2：手动关闭")
    private String soundCloseType;

    /**
     * 播放时长（单位：秒）
     * */
    @ApiModelProperty(value = "播放时长（单位：秒）")
    private String soundDuration;

    /**
     * 声音唯一标识
     * */
    @ApiModelProperty(value = "声音唯一标识")
    private String soundId;


    /**
     * 声音url
     * */
    @ApiModelProperty(value = "声音url")
    private String soundUrl;

    @ApiModelProperty(value = "告警事件状态名称")
    private String orderStatusName;

    @ApiModelProperty(value = "告警来源")
    private String alarmSource;

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;
}

