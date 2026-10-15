package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LeaderAuditDto implements Serializable {

    /**
     * 工单号
     * @required true
     */
    private String orderId;

    /**
     * 告警原因
     * @required true
     */
    private String alarmReason;
}
