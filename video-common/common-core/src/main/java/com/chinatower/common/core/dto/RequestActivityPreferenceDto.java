package com.chinatower.common.core.dto;

import com.chinatower.common.core.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description:查询活动优惠列表
 * @author: linxq
 * @date: 2021年05月18日 15:57
 */
public class RequestActivityPreferenceDto implements Serializable {

    private String partnerOrderPackage;
    @Excel(name = "产品ID")
    private String productId;

    @Excel(name = "产品价格")
    private BigDecimal productPrice;

    @Excel(name = "活动ID")
    private Long activityId;

    @Excel(name = "客户Id")
    private String custId;

    @Excel(name = "管理区域省份")
    private String areaProvince;

    @Excel(name = "管理区域市")
    private String areaCity;

    @Excel(name = "订购摄像头数量")
    private String orderNumber;

    @Excel(name = "支付周期")
    private String payPeriod;

    @Excel(name = "订购周期")
    private String orderPeriod;

    //是否首次付款 0 否 1 是
    private Integer firstPay ;

    /** 此次支付优惠后价格 */
    private BigDecimal payAmount;


    /** 此次支付原价 */
    private BigDecimal orgPayAmount;

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getOrgPayAmount() {
        return orgPayAmount;
    }

    public void setOrgPayAmount(BigDecimal orgPayAmount) {
        this.orgPayAmount = orgPayAmount;
    }

    public Integer getFirstPay() {
        return firstPay;
    }

    public void setFirstPay(Integer firstPay) {
        this.firstPay = firstPay;
    }

    public String getPartnerOrderPackage() {
        return partnerOrderPackage;
    }

    public void setPartnerOrderPackage(String partnerOrderPackage) {
        this.partnerOrderPackage = partnerOrderPackage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getAreaProvince() {
        return areaProvince;
    }

    public void setAreaProvince(String areaProvince) {
        this.areaProvince = areaProvince;
    }

    public String getAreaCity() {
        return areaCity;
    }

    public void setAreaCity(String areaCity) {
        this.areaCity = areaCity;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(String payPeriod) {
        this.payPeriod = payPeriod;
    }

    public String getOrderPeriod() {
        return orderPeriod;
    }

    public void setOrderPeriod(String orderPeriod) {
        this.orderPeriod = orderPeriod;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }
}
