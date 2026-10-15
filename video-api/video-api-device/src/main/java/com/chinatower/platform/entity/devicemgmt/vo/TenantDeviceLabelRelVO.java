package com.chinatower.platform.entity.devicemgmt.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 租户设备标签实体
 * @author chmod
 * @version 1.0
 * @date 2022/1/5 14:25
 */
@Data
public class TenantDeviceLabelRelVO implements Serializable {
    private List<Long> labelIdList;
    private String deviceCode;
    /**
     * app调用标识 1-APP调用
     */
    private String appFlag;
}
