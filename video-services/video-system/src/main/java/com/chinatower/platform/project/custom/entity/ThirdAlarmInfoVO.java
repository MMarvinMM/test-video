package com.chinatower.platform.project.custom.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ThirdAlarmInfoVO implements Serializable {

    /**
     * 第三方告警名称
     */
    private String thirdAlarmName;

    /**
     * ak
     */
    private String appKey;

    /**
     * sk
     */
    private String secretKey;

    /**
     * 简介
     */
    private String remark;

    /**
     * 第三方告警状态--0关闭 1开启
     */
    private Integer isOpen;

    private String tenantId;

    private String acceptNode;

    private String industryCode;

    private String appVerCode;

    private String modifier;

    private Date updateTime;

    private String creator;

    private Date createTime;

}
