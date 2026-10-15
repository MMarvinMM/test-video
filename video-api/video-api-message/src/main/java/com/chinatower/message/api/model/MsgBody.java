package com.chinatower.message.api.model;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName MsgBody
 * @Description TODO
 * @Author D
 * @Date 2022/3/24 16:30
 */
@Data
public class MsgBody extends CommonFeignEntity implements Serializable {
    /**
     * 告警/内容ID
     * @required true
     * */
    @ApiModelProperty(value = "告警/内容ID")
    private String msgId;
    /**
     * 用户ID
     * @required true
     * */
    @ApiModelProperty(value = "用户ID")
    private String userId;
    /**
     * 通知类型:1系统提醒
     * @required true
     * */
    @ApiModelProperty(value = "通知类型:1系统提醒")
    private String notifyType;
    /**
     * 通知消息标题
     * @required true
     * */
    @ApiModelProperty(value = "通知消息标题")
    private String notifyTitle;

    /**
     * 地址
     * @required true
     * */
    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 告警事件状态
     * @required false
     * */
    @ApiModelProperty(value = "告警事件状态")
    private String orderStatus;

    /**
     * 告警事件状态
     * @required false
     * */
    @ApiModelProperty(value = "告警事件状态名称")
    private String orderStatusName;

    /**
     * 行业版本号
     * @required true
     * */
    @ApiModelProperty(value = "行业版本号")
    private String appVerCode;
    /**
     * 行业编码
     * @required true
     * */
    @ApiModelProperty(value = "行业编码")
    private String industryCode;
    /**
     * 告警状态
     * @required true
     * */
    @ApiModelProperty(value = "告警状态")
    private String alarmFlag;
    /**
     * list
     * @required false
     * */
    private List<MsgBody> MsgBodyList;
    /**
     * 显示位置
     * @required false
     * */
    @ApiModelProperty(value = "显示位置")
    private String showType;

    /**
     * 行业FLAG
     * @required false
     * */
    @ApiModelProperty(value = "行业FLAG")
    private String industryFlag;


    /**
     * 扩展信息
     * @required false
     * */
    @ApiModelProperty(value = "扩展信息")
    private String industryExtend;

    /**
     * 声音关闭方式 1：自动关闭，2：手动关闭
     * @required true
     * */
    @ApiModelProperty(value = "声音关闭方式 1：自动关闭，2：手动关闭")
    private String soundCloseType;

    /**
     * 播放时长（单位：秒）
     * @required true
     * */
    @ApiModelProperty(value = "播放时长（单位：秒）")
    private String soundDuration;

    /**
     * 声音唯一标识
     * @required true
     * */
    @ApiModelProperty(value = "声音唯一标识")
    private String soundId;

    /**
     * 声音唯一标识
     * @required true
     * */
    @ApiModelProperty(value = "声音url")
    private String soundUrl;

    @ApiModelProperty(value = "告警来源")
    private String alarmSource;

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

}
