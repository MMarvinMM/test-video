package com.chinatower.platform.entity.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderServiceRelVO implements Serializable {
    //订购关系编码
    private String orderCode;

    //监控服务编码
    private String serviceCode;

    //监控服务名称
    private String serviceName;

    //省份编码
    private String proviceCode;
    private String provinceName;
    //市编码
    private String cityCode;
    private String cityName;
    //区县编码
    private String countyCode;
    private String countyName;
    //街道编码
    private String streetCode;
    private String streetName;

}
