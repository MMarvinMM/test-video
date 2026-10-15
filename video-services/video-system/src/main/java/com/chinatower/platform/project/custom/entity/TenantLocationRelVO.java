package com.chinatower.platform.project.custom.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class TenantLocationRelVO implements Serializable {

    /**
     * 客户编码
     */
    private String tenantId;

    private String tenantName;

    /**
     * 节点编码
     */
    private String locationCode;

    /**
     * 节点名称
     */
    private String locationName;

    /**
     * 是否是当前节点
     */
    private Boolean isCurrentNode;

    String isOperation;

    String isOperationName;

    String operationModifier;

    private String operationModifierName;

    String operationModifierDate;

    private String industryCode;

    private String industryName;

    private String appVerCode;

    private String locationOrderNum;

    private String locationValidOrderNum;

    private String locationValidDevNum;

    private String deleteFlag;

    /**
     * 第三方告警状态
     */
    private Integer thirdAlarmStatus;

    private String thirdAlarmStatusName;

    private String thirdAlarmModifier;

    private String thirdAlarmModifierName;

    private String thirdAlarmModifierDate;

    private String generalUrl;

    private String programUrl;

}
