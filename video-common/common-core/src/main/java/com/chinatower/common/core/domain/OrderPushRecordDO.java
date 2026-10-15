package com.chinatower.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderPushRecordDO implements Serializable {

    private Long id;

    private String orderCode;

    private String orderTime;

    private Date pushTime;

    private Integer ordStatus;

    private Integer pushStatus;

    private String pushFailReason;

    private String acceptanceNode;

    private String tenantId;

    private Integer deleteFlag;

    private Date createTime;

    private Date updateTime;
}
