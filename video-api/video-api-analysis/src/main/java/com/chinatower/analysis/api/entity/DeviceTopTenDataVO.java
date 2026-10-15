package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceTopTenDataVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 设备编码
     * @required true
     */
    private String deviceCode;
    /**
     * 设备名称
     * @required true
     */
    private String deviceName;
    /**
     * 告警总数
     * @required true
     */
    private int total;
    /**
     * 排名
     * @required true
     */
    private int ranking;

    private String warningSource;

    /**
     * 真警数量
     * @required true
     */
    private int realNum;
}
