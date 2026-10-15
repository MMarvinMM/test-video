package com.chinatower.common.core.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: yuexuebin
 * @Date: 2021/6/3 09:13
 * @Description:
 * 支付参数
 */
public class PayOrderReq implements Serializable {
    @NotNull(message = "订单编号不能为空")
    private String partnerOrderCode;
    @NotNull(message = "支付金额不能为空")
    private BigDecimal payAmount;
    @NotNull(message = "客户编码不能为空")
    private String custId;
    @NotNull(message = "支付类型不能为空")
    private Integer payType;

    private Date invalidTime;

    public Date getInvalidTime() {
        return invalidTime;
    }

    public void setInvalidTime(Date invalidTime) {
        this.invalidTime = invalidTime;
    }

    public String getPartnerOrderCode() {
        return partnerOrderCode;
    }

    public void setPartnerOrderCode(String partnerOrderCode) {
        this.partnerOrderCode = partnerOrderCode;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }
}
