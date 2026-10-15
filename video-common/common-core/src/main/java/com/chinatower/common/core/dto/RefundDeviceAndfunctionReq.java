package com.chinatower.common.core.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RefundDeviceAndfunctionReq {

    //客户编号
    private String custId;

    //订单编号
    private String orderCode;

    //操作员
    private String opperatorId;

    //基本费用退费
    private List<DeviceAndfunction> refundBaseAmountList;

    //存储费退费
    private List<DeviceAndfunction> refundStorageAmountList;

    //维护费退费
    private List<DeviceAndfunction> refundMaintainAmountList;

    //网络费退费
    private List<DeviceAndfunction> refundNetworkAmountList;

    //选配功能退费
    private List<DeviceAndfunction> refundFunctionAmountList;

    //新增选配功能
    private List<DeviceAndfunction> payFunctionAmountList;

    private List<PartnerOrderModiItem> delFunctionList;
    private List<PartnerOrderModiItem> addFunctionList;
    private List<PartnerOrderModiItem> delDeviceList;

    public List<PartnerOrderModiItem> getDelFunctionList() {
        return delFunctionList;
    }

    public void setDelFunctionList(List<PartnerOrderModiItem> delFunctionList) {
        this.delFunctionList = delFunctionList;
    }

    public List<PartnerOrderModiItem> getAddFunctionList() {
        return addFunctionList;
    }

    public void setAddFunctionList(List<PartnerOrderModiItem> addFunctionList) {
        this.addFunctionList = addFunctionList;
    }

    public List<PartnerOrderModiItem> getDelDeviceList() {
        return delDeviceList;
    }

    public void setDelDeviceList(List<PartnerOrderModiItem> delDeviceList) {
        this.delDeviceList = delDeviceList;
    }

    public static class PartnerOrderModiItem {

        private String itemName;

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        private String functionName;

        public String getFunctionName() {
            return functionName;
        }

        public void setFunctionName(String functionName) {
            this.functionName = functionName;
        }

        private BigDecimal price;

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        private BigDecimal taxRate;

        public BigDecimal getTaxRate() {
            return taxRate;
        }

        public void setTaxRate(BigDecimal taxRate) {
            this.taxRate = taxRate;
        }

        private Integer deviceNumber;

        public Integer getDeviceNumber() {
            return deviceNumber;
        }

        public void setDeviceNumber(Integer deviceNumber) {
            this.deviceNumber = deviceNumber;
        }

        private BigDecimal sumPrice;

        public BigDecimal getSumPrice() {
            return sumPrice;
        }

        public void setSumPrice(BigDecimal sumPrice) {
            this.sumPrice = sumPrice;
        }


    }
}
