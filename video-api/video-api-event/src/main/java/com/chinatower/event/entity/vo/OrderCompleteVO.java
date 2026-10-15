package com.chinatower.event.entity.vo;

import lombok.Data;

/**
 * @className: OrderCompleteVO
 * @description: 设备未完结告警数量返回
 * @author: lyy
 * @date: 2025-01-10 10:37
 * @version v1.7.20
 */
@Data
public class OrderCompleteVO {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 是否是未完结告警标识（0-否，1-是）
     */
    private String isCompleteFlag;

    public OrderCompleteVO(String deviceCode, String s) {
        this.deviceCode = deviceCode;
        this.isCompleteFlag = s;
    }
}
