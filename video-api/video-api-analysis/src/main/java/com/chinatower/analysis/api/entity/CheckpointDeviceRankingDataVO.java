package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckpointDeviceRankingDataVO implements Serializable {

    private static final long serialVersionUID = 977247955872059181L;
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
     * 车辆识别数量
     * @required true
     */
    private int num;

}
