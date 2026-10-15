package com.chinatower.platform.modules.alarm.entity.delaysendmsg;

import lombok.Data;

import java.io.Serializable;

@Data
public class DelayFlowVo implements Serializable {

    /**
     * 流程id
     */
    private String flowId;

    /**
     * 环节id
     */
    private String linkId;

    /**
     * 行业版本
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 告警声音开始时段
     */
    private String timeStartAlram;

    /**
     * 告警声音结束时段
     */
    private String timeEndAlram;

    /**
     * 告警提示音
     */
    private String voiceAlram;

    /**
     * 提醒时长类型，0： 手动关闭、1：自动停止
     */
    private String timeRemind;

    /**
     * 提醒时长自动停止单位 0: 秒，1：分钟
     */
    private String timeRemindAuto;

    /**
     * 提醒时长自动停止
     */
    private String timeRemindNumber;

    /**
     * 是否稍后提醒
     */
    private String ifLaterRemind;

    /**
     * 稍后提醒单位
     */
    private String laterRemindCompany;

    /**
     * 稍后提示时间
     */
    private String laterRemind;

}
