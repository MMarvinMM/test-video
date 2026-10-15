package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;


/**
 * 4.10.中心和属地设备/通道同步-到其他系统-接口拉取 请求实体类
 */
@Data
public class AuditDeviceInfoRsp implements Serializable {
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
    private AuditDeviceInfoDataRsp data;
}

