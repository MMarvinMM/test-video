package com.chinatower.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderTransferConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 旧订单号
     */
    private String oldOrderCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本
     */
    private String appVerCode;

    /**
     * 设备编码
     */
    private List<String> deviceCodeList;

    /**
     * 算法编码
     */
    private List<String> algorithmCodeList;

    /**
     * 套餐编码
     */
    private String packageCode;
}
