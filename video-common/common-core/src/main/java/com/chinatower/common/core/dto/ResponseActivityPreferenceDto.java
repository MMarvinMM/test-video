package com.chinatower.common.core.dto;

import com.chinatower.common.core.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description:查询活动优惠返回值
 * @author: linxq
 * @date: 2021年06月5日 15:57
 */
public class ResponseActivityPreferenceDto implements Serializable {

    @Excel(name = "最大优惠")
    private BigDecimal biggestDiscount;

    /** 此次支付优惠后价格 */
    private BigDecimal payAmount;


    /** 此次支付原价 */
    private BigDecimal orgPayAmount;


    /** 优惠类型：1满减,2折扣 */
    @Excel(name = "优惠类型：1满减,2折扣")
    private int biggestDiscountType;

    /** 优惠值 */
    @Excel(name = "优惠值")
    private BigDecimal biggestDiscountValue;


    /**
     * 活动优惠后总价
     */
    private BigDecimal totalAmount;

    /**
     * 活动优惠最大天数
     */
    private int maxDay;

    /**
     * 活动优惠最大设备
     */
    private int maxDevice;

    /**
     * 剩余优惠数量
     */
    private int leftCount;

    /**
     * 活动优惠最大天数
     */
    private boolean deviceEffective;

    /**
     * 活动优惠最大天数
     */
    private boolean dayEffective;


    /**
     * 订购设备数量
     */
    private int orderNumber;




    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getBiggestDiscount() {
        return biggestDiscount==null?new BigDecimal(0):biggestDiscount;
    }

    public void setBiggestDiscount(BigDecimal biggestDiscount) {
        this.biggestDiscount = biggestDiscount;
    }



    public int getMaxDay() {
        return maxDay;
    }

    public void setMaxDay(int maxDay) {
        this.maxDay = maxDay;
    }

    public int getMaxDevice() {
        return maxDevice;
    }

    public void setMaxDevice(int maxDevice) {
        this.maxDevice = maxDevice;
    }

    public int getLeftCount() {
        return leftCount;
    }

    public void setLeftCount(int leftCount) {
        this.leftCount = leftCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getBiggestDiscountType() {
        return biggestDiscountType;
    }

    public void setBiggestDiscountType(int biggestDiscountType) {
        this.biggestDiscountType = biggestDiscountType;
    }

    public BigDecimal getBiggestDiscountValue() {
        return biggestDiscountValue;
    }

    public void setBiggestDiscountValue(BigDecimal biggestDiscountValue) {
        this.biggestDiscountValue = biggestDiscountValue;
    }

    public boolean isDeviceEffective() {
        return deviceEffective;
    }

    public void setDeviceEffective(boolean deviceEffective) {
        this.deviceEffective = deviceEffective;
    }

    public boolean isDayEffective() {
        return dayEffective;
    }

    public void setDayEffective(boolean dayEffective) {
        this.dayEffective = dayEffective;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getOrgPayAmount() {
        return orgPayAmount;
    }

    public void setOrgPayAmount(BigDecimal orgPayAmount) {
        this.orgPayAmount = orgPayAmount;
    }
}
