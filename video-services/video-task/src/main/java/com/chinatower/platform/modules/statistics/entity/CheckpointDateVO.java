package com.chinatower.platform.modules.statistics.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @className: CheckpointDateVO
 * @description:
 * @author: csj
 * @date: 2023/12/27
 * @requestID 需求单号及需求名称
 */
@Data
public class CheckpointDateVO implements Serializable {

    private static final long serialVersionUID = 2704796666613104862L;
    //主键ID
    private String id;
    //省编码
    private String provinceCode;
    //市编码
    private String cityCode;
    //区县编码
    private String countyCode;
    //设备编码
    private String deviceCode;
    //设备名称
    private String deviceName;
    private String warningHour;
    //告警数量
    private String warningNum;
    private String year;
    private String month;
    private String day;
    private String handleDate;
    //租户编码
    private String tenantId;
    //行业编码
    private String industryCode;
    //应用版本
    private String appVerCode;
    //告警来源
    private String warningSource;
    //告警时间
    private String alarmTime;
}
