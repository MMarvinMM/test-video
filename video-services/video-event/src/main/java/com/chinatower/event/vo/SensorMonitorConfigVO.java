package com.chinatower.event.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SensorMonitorConfigVO implements Serializable {

    /**
     * 联告警因子配置表id
     */
    private Long id;

    /**
     *  物联因子编码
     */
    private String monitorCode;

    /**
     *  物联因子名称
     */
    private String monitorName;

    /**
     *  物联因子对应告警类型
     */
    private String alarmType;

    /**
     * 最小值符号
     */
    private String minChar;

    /**
     * 最小值
     */
    private String minNum;
    /**
     * 最大值符号（<   <=）
     */
    private String maxChar;

    /**
     * 最大值
     */
    private String maxNum;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 应用版本编码
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 配置方式 0 物联因子  1 物联设备
     */
    private String configMethod;

    /**
     * 物联设备编码
     */
    private String deviceCode;

    /**
     * 监测值
     */
    private String monitorValue;

    /**
     * 物联因子编码
     */
    private List<String> monitorCodeList;

    /**
     * 租户id
     */
    private List<String> tenantIdList;

    /**
     * 物联设备编码
     */
    private List<String> deviceCodeList;
}
