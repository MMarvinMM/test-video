package com.chinatower.common.core.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author jwk
 * @version 1.0
 * @date 2021/5/28 18:05
 * @description
 **/
@Data
public class PaymentRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "客户编码不能为空")
    @NotEmpty(message = "客户编码不能为空")
    private String custId;

    /**
     * 订单实际支付金额，分期支付时为首期金额
     */
    private Double paymentAmount;

    /**
     * 主订单编码
     */
    @NotNull(message = "支付订单编码不能为空")
    @NotEmpty(message = "支付订单编码不能为空")
    private String orderCode;

    @NotNull(message = "操作人不能为空")
    @NotEmpty(message = "操作人不能为空")
    private String operatorName;

    /**
     * 支付类型，0：一次性付清，1：分期支付；默认0
     */
    private int payType;

    /**
     * 分期支付时必传，订单总金额
     */
    private Double totalAmount;

    /**
     * 分期支付时必传，合同生效时间,yyyymmddhh24miss
     */
    private String validTime;

    /**
     * 分期支付时必传，合同失效时间,yyyymmddhh24miss
     */
    private String invalidTime;

    /**
     * 订单类型，0：主订单，1：子订单；默认0
     */
    private int orderType;

    /**
     * 子订单时必传，子订单编码
     */
    private String requestOrderCode;

    @NotNull(message = "业务属性不能为空")
    @NotEmpty(message = "业务属性不能为空")
    private String businessProperty;

    /**
     * 省份编码
     */
    @NotNull(message = "地市编码不能为空")
    @NotEmpty(message = "地市编码不能为空")
    private String provinceCode;

    /**
     * 订单归属地市编码
     */
    @NotNull(message = "地市编码不能为空")
    @NotEmpty(message = "地市编码不能为空")
    private String cityCode;
}
