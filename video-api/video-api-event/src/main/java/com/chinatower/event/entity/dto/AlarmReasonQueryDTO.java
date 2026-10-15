package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AlarmReasonQueryDTO implements Serializable {

    /**
     * 告警类型值
     */
    private String typeValue;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 告警类型集合
     */
    private List<String> typeValueList;
}
