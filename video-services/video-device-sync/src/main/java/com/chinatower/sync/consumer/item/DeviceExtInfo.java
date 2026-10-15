package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;

/**
 * 4.8.设备/通道同步-到其他系统-队列推送 请求实体类
 */
@Data
public class DeviceExtInfo implements Serializable {

    /**
     * 扩展字段编码
     */
    private String extendField;

    /**
     * 扩展字段编码名称
     */
    private String extendName;

    /**
     * 扩展字段值
     */
    private String extendvalue;

}