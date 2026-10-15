package com.chinatower.platform.entity;

import lombok.Data;

/**
 * @className: TenantInfoVo
 * @description:
 * @author: shijie.cui
 * @date: 2022/12/13
 * @requestID 需求单号及需求名称
 */
@Data
public class TenantInfoVo {

    /**
     * 客户id
     */
    private String tenantId;

    /**
     * 客户名称
     */
    private String tenantName;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 地市名称
     */
    private String cityName;

    /**
     * 有效设备数量
     */
    private int devNum;

}
