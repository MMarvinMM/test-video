package com.chinatower.event.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2025/8/27 14:48
 */

@Data
public class TradAddLableDto {

    /**
     * 租户
     */
    private String tenantId;

    /**
     * 行业
     */
    private String industryCode;

    /**
     * 事件编号集合
     */
    private List<String> warningOrderIds;

    /**
     * 告警标签集合
     */
    private List<String> lableNames;
}
