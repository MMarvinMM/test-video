package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScopeDeviceInfoVO extends BaseDataScopeEntity {
    /**
     * 摄像机名称
     */
    private String deviceName;

    /**
     * 摄像机编码
     */
    private String deviceCode;

    /**
     * 告警压制策略ID
     */
    private Integer alarmSuppressId;

    /**
     * 限制条数
     */
    private Integer limit;

    /**
     * 0:not in 1:in
     */
    private String flag;
}
