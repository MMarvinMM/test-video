package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;


/**
 * 4.26.设备状态同步-到其他系统-接口拉取
 */
@Data
public class AuditDeviceStatusRsp implements Serializable {
    /**
     * 返回编码
     */
    private String code;

    /**
     * 返回信息，信息说明
     */
    private String msg;

    /**
     * 时间戳
     */
    private Integer time;

    /**
     * 报文主体，返回查询结果列表
     */
    private AuditDeviceStatusDataRsp data;
}

