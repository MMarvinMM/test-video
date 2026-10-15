package com.chinatower.event.dataobject;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
@Getter
@Setter
@TableName("vm_order_leader_audit_t")
public class OrderLeaderAuditDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String auditId;

    /**
     * 事件ID
     */
    private String orderId;

    /**
     * 申请内容
     */
    private String content;

    /**
     * 申请人
     */
    private String userId;

    /**
     * 领导ID
     */
    private String leaderId;

    /**
     * 审批意见：0-待审批；1-同意；2-不同意
     */
    private String status;

    /**
     * 批示内容
     */
    private String auditContent;

    /**
     * 创建时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 批示时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String auditTime;

    /**
     * 0-有效；1-删除
     */
    private String deleteFlag;

    /**
     * 流程节点ID
     */
    private String orderFlowId;

    /**
     * 批示语音
     */
    private String voice;

    /**
     * 申请领导批示语音
     */
    private String applyVoice;

    /**
     * 完成时限;单位：分钟
     */
    private Integer timeLimit;

    /**
     * 当前环节开始时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String flowNodeTime;

    /**
     * 事件等级
     */
    private String emergencyLevel;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatus;

    /**
     * 告警内容
     */
    private String alarmBody;

    /**
     * 县名称
     */
    private String countyCode;

    /**
     * 县编码
     */
    private String countyName;

    /**
     * 地市编码
     */
    private String cityCode;

    /**
     * 所在市名称
     */
    private String cityName;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 所在省名称
     */
    private String provinceName;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 形成告警时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String alarmTime;

    /**
     * 告警类型编码
     */
    private String warningTypeId;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 地址
     */
    private String address;

    /**
     * 原图
     */
    private String fileImgUrlSrc;

    /**
     * 重要等级
     */
    private String importanceLevel;

    /**
     * 受理状态
     */
    private String enableAcceptanceMode;


    /**
     * 摄像机俯仰角
     */
    private String devicePitch;

    /**
     * 告警原因
     */
    private String alarmReason;

}
