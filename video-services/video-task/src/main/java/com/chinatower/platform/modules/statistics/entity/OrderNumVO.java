package com.chinatower.platform.modules.statistics.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警数量统计VO
 */
@Data
public class OrderNumVO implements Serializable {
    private static final long serialVersionUID = 1L;
    //租户ID
    private String tenantId;
    //应用版本
    private String appVerCode;
    //告警状态
    private String orderStatus;
    //告警来源
    private String warningSource;
    //设备编号
    private String deviceCode;
    //省编码
    private String provinceId;
    //市编码
    private String cityId;
    //区县编码
    private String countyId;
    private int orderNum;
    private int overtimeNum;
    //行业编码
    private String industryCode;


    public OrderNumVO(String tenantId, String appVerCode, String orderStatus, String warningSource, String deviceCode,
                      String provinceId, String cityId, String countyId, int orderNum, int overtimeNum, String industryCode) {
        this.tenantId = tenantId;
        this.appVerCode = appVerCode;
        this.orderStatus = orderStatus;
        this.warningSource = warningSource;
        this.deviceCode = deviceCode;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.countyId = countyId;
        this.orderNum = orderNum;
        this.overtimeNum = overtimeNum;
        this.industryCode = industryCode;
    }
}
