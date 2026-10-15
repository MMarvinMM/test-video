package com.chinatower.algorithm.dataobject;

import lombok.Data;

/**
 * 订单算法信息DTO
 * 用于查询vm_order_algorithms表的结果
 *
 * @author AutoGen
 */
@Data
public class OrderAlgorithmDTO {
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 算法编码
     */
    private String algorithmCode;
}

