package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;

@Data
public class HornStatusReportV2 implements Serializable {

    /**
     * {
     * 	"deviceCode": "",
     * 	"devStatus": "",
     * 	"regionCode": "",
     * "accessNode": ""
     * }
     */

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     *  0离线
     *  1空闲（在线）
     *  2工作中（在线）
     *  3故障（离线）
     */
    private Integer devStatus;

    /**
     * 监测时间
     */
    private String regionCode;

    /**
     * 接入节点
     */
    private String accessNode;
}
