package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新订单状态
 *
 * @author: fangjun
 */
@Data
public class PartnerOrderParamDTO implements Serializable {

    /**
     * 订单编码.
     */
    private String orderCode;

    /**
     * 目标支付状态
     */
    private Integer status;


}
