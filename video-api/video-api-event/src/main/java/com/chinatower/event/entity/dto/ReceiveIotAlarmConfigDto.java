package com.chinatower.event.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data

public class ReceiveIotAlarmConfigDto implements Serializable {

    /**
     * 配置id
     */
    private String configId;

    /**
     * 物联告警配置名称
     */
    private String iotAlarmConfigName;


    /**
     * 租户id
     */
    private String tenantId;


    /**
     * 行业编码
     */
    private String industryCode;

    /**物联告警类型*/
    private String iotAlarmType;

    /**
     * 物联设备类型
     */
    private String deviceType;

    /**告警开始时间*/
    private String receiveStartTime;

    /**告警结束时间*/
    private String receiveEndTime;

    /**告警时间段*/
    private String receivePeriodTime;

    /**告警等级 字典配置*/
    private String alarmLevel;

    /**告警压制时间*/
    private String interceptTime;

    /**告警压制时间单位  时分秒*/
    private String interceptTimeUnit;

    /**告警生效设备组合json*/
    private String deviceGroupsJson;

    /**告警生效设备组合*/
    private List<String> iotDeviceGroups;

    /**
     * 物联配置下的规则信息
     */
    private List<ReceiveIotAlarmGroupsDto> iotAlarmConfigGroupsList;
}
