package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 租户订单关系参数
 */
@Data
@ToString
public class TenantOrderVO {

    private String customerCode;
    private String customerName;
    private String admin;
    private String packageCode;
    private String packageName;
    private String description;
    private String appCode;
    private String productName;
    private String productCode;
    private String capabilitiesStr;
    private String serviceCode;
    private String serviceName;
    private String algorithmCode;
    private String algorithmName;
    private String deviceCode;
    private String channelName;
    private Integer categoryCode;
    private String areaCode;
    private Integer exclusive;

    private String tenantId;
    private String custCode;

    private String orderCode;
    private String validTime;
    private String invalidTime;
    private String deleteFlag;
    private String createTime;
    private String subscriber;
    private List<CustomOrderApplicationVO> appData;
    private List<OrderServicesVO> serviceData;
    private List<CustomOrderAlgorithmsVO> algorithmData;
    private List<CustomOrderAlgorithmsVO> giftAlgorithmData;
    private List<CustomOrderDevicesVO> deviceData;

    private int pageNum;
    private int pageSize;

}
