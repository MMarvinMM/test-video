package com.chinatower.event.entity.dto.usertree;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderInfoUserTreeReq implements Serializable {

    /**
     * 所有处理人
     */
    private String processIds;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业版本
     */
    private String industryCode;
}
