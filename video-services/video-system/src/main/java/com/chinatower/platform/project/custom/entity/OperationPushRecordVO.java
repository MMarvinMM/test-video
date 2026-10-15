package com.chinatower.platform.project.custom.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OperationPushRecordVO implements Serializable {

    private String acceptNode;

    private String tenantId;

    private String industryCode;

    private String appVerCode;

    private Date pushTime;

    private String pushStatus;

    private String pushFailReason;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date updateTime;

    private Integer deleteFlag;
}
