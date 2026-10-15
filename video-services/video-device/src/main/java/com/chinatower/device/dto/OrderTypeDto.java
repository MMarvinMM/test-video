package com.chinatower.device.dto;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/18 17:45
 */
@Data
public class OrderTypeDto {
    /**
     * 事件编号
     */
    private String warningOrderId;

    /**
     * 事件类型（0：告警  1：回收站）
     */
    private String orderType;

    /**
     * 算法编码
     */
    private String algorithmCode;
}
