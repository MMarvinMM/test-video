package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceAccessNodeDTO implements Serializable {

    /**
     * 设备编码
     */
    private List<String> deviceCodeList;

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;
}