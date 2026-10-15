package com.chinatower.platform.project.custom.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.Set;

/**
 * 设备及通道信息
 */
@Data
@ToString
public class DeviceVO {
    //客户编码
    private String orderCode;

    //客户编码
    private String customerCode;

    //设备编码
    private String deviceCode;

    //设备通道编码列表
    private String[] channelCodes;

    //设备通道编码列表
    private String channelCodesStr;

    //归属行政区域
    private String areaCode;

    //是否独占,1-是；0-否
    private Long exclusive;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String provinceId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cityId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String countyId;

    private Set<String> deviceCodeSet;


}