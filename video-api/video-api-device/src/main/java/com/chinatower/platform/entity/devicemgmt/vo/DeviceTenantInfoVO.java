package com.chinatower.platform.entity.devicemgmt.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chmod
 * @version 1.0
 * @date 2021/12/8 10:11
 */
@Data
public class DeviceTenantInfoVO implements Serializable {
    private static final long serialVersionUID = 5442011825851957292L;

    private String deviceCode; //设备编码
    private String custCode; //客户编码
    private String custName; //客户名称
    private String orderStatus; //订购状态
}
