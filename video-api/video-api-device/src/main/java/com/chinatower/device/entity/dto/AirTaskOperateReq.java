package com.chinatower.device.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2025/1/23 10:06
 */
@Data
public class AirTaskOperateReq {

    /**
     * 设备编码(无人机)
     */
    private List<String> deviceCodeList;
    /**
     * 租户
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;
}
