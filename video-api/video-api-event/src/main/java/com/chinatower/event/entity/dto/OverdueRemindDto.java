package com.chinatower.event.entity.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OverdueRemindDto {

    private String warningOrderId;

    private String address;

    private String orderStatus;

    private String linkId;

    private String overdueRemindSign;

    private String remindTime;

    private String remindProcessId;

    private String warningTypeId;

    private String appVerCode;

    private String industryCode;

    private Date createTime;

    private Date updateTime;

    private String warningTypeName;

    private String alarmTime;

    private String warningSource;

    private String deviceCode;

    private String deviceName;
}
