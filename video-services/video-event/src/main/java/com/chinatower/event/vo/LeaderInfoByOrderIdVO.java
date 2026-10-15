package com.chinatower.event.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：领导批示信息对象
 * <p>
 * 类名称: LeaderAuidtListVo.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/26 16:09
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class LeaderInfoByOrderIdVO implements Serializable {
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     * 批示时间
     */
    private String auditTime;

    /**
     * 流程节点ID
     */
    private String orderFlowId;

    /**
     * 语音
     */
    private String voice;

    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatus;
    /**
     * 0误报、1研判环节 、v2待调度、3待认领、4待处置、5待核实、6已完结、7重复告警
     */
    private String orderStatusName;

    /**
     * 领导名称
     */
    private String leaderName;

    /**
     * 领导组织名称
     */
    private String leaderOrgName;

    /**
     * 环节名称
     */
    private String linkName;


    /**
     * 领导手机号
     */
    private String mobilePhone;

    /**
     * 用户手机号
     */
    private String userMobilePhone;


    /**
     * 环节名称
     */
    private String userOrgName;

    /**
     * 用户名称
     */
    private String userName;


    /**
     * 受理状态
     */
    private String enableAcceptanceMode;

}
