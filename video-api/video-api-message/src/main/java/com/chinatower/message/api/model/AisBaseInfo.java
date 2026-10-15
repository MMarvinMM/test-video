package com.chinatower.message.api.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AisBaseInfo implements Serializable {

    /**
     * 呼号
     */
    private String callSign;

    /**
     * 目的地
     */
    private String destination;

    /**
     * MMSI号
     */
    private String mmsi;

    /**
     * 设备分类
     */
    private String shipClass;

    /**
     * 船舶类型
     */
    private String shipType;

    /**
     * 船名
     */
    private String vesselName;

    /**
     * 船宽(单位:米)
     */
    private Integer wide;

    /**
     * 吃水深度
     */
    private String draught;

    /**
     * 预计到港时间
     */
    private String etaTime;

}
