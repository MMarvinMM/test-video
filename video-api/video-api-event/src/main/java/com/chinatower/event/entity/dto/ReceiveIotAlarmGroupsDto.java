package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data

public class ReceiveIotAlarmGroupsDto implements Serializable {

    /**
     * 主键
     */
    private String id;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date updateTime;

    private Integer deleteFlag;

    /**告警防抖类型
     *  1:触发次数  连续「时长」符合阈值「次数」时产生告警
     *  2:持续超标  连续「次数」符合阈值时产生告警
     *  3:突然超标  连续「次数」不符合阈值后符合阈值则产生告警
     *  */
    private String antiShakeType;

    /**规则名称*/
    private String groupsName;

    /**告警防抖次数*/
    private String antiShakeNum;

    /**告警防抖时间*/
    private String antiShakeTime;

    /**告警防抖时间单位  时分秒*/
    private String antiShakeTimeUnit;

    /**物联因子关系 1:且  2:或*/
    private String relation;

    /**物联告警配置id(vm_iot_alarm_config_t主键 )*/
    private String iotConfigId;

    /**
     * 物联设备类型
     */
    private String deviceType;


    /**物联规则下的物联设备*/
    private List<ReceiveIotAlarmDeviceDto> deviceList;

    /**物联规则下的物联因子*/
    private List<ReceiveIotAlarmFactorDto> factorList;
}
