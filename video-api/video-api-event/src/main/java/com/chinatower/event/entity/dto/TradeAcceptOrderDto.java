package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TradeAcceptOrderDto implements Serializable {

    /**
     * 工单号
     * @required true
     */
    private String warningOrderId;

    /**
     * 当前环节ID
     * @required true
     */
    private String linkId;

    /**
     * 当前环节名称
     * @required true
     */
    private String linkName;

    /**
     * 流程ID
     * @required true
     */
    private String flowId;

    /**
     * 环节超限时间
     * @required true
     */
    private String timeLimit;


    /**
     * 事发地点
     * @required true
     */
    private String address;

    /**
     * 事件状态
     * @required true
     */
    private String orderStatus;

    /**
     * 告警类型ID
     * @required true
     */
    private String warningTypeId;

    /**
     * 行业版本
     * @required true
     */
    private String appVerCode;

    /**
     * 行业编码
     * @required true
     */
    private String industryCode;

    /**
     * 告警时间
     * @required true
     */
    private String alarmTime;


    /**
     * 摄像机俯仰角
     */
    private String devicePitch;

    /**
     * 告警原因
     */
    private String alarmReason;

    /**
     * 告警等级
     */
    private String importanceLevel;

    private String deviceHorizontal;
}
