package com.chinatower.common.core.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PayAndRefundAmount {
    private long payAmount;

    private long refundAmount;

    private BigDecimal amountPay;

    public BigDecimal getAmountPay() {
        return amountPay;
    }

    public void setAmountPay(BigDecimal amountPay) {
        this.amountPay = amountPay;
    }

    private BigDecimal amountRefund;

    public BigDecimal getAmountRefund() {
        return amountRefund;
    }

    public void setAmountRefund(BigDecimal amountRefund) {
        this.amountRefund = amountRefund;
    }

    private List<RefundDeviceAndfunctionReq.PartnerOrderModiItem> delFunctionList;
    private List<RefundDeviceAndfunctionReq.PartnerOrderModiItem> addFunctionList;
    private List<RefundDeviceAndfunctionReq.PartnerOrderModiItem> delDeviceList;

    public List<RefundDeviceAndfunctionReq.PartnerOrderModiItem> getDelFunctionList() {
        return delFunctionList;
    }

    public void setDelFunctionList(List<RefundDeviceAndfunctionReq.PartnerOrderModiItem> delFunctionList) {
        this.delFunctionList = delFunctionList;
    }

    public List<RefundDeviceAndfunctionReq.PartnerOrderModiItem> getAddFunctionList() {
        return addFunctionList;
    }

    public void setAddFunctionList(List<RefundDeviceAndfunctionReq.PartnerOrderModiItem> addFunctionList) {
        this.addFunctionList = addFunctionList;
    }

    public List<RefundDeviceAndfunctionReq.PartnerOrderModiItem> getDelDeviceList() {
        return delDeviceList;
    }

    public void setDelDeviceList(List<RefundDeviceAndfunctionReq.PartnerOrderModiItem> delDeviceList) {
        this.delDeviceList = delDeviceList;
    }

}
