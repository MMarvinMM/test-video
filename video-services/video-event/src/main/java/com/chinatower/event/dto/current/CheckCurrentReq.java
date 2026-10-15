package com.chinatower.event.dto.current;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckCurrentReq  implements Serializable {
    /**
     * 工单号
     */
    private String orderId;
    /**
     * 环节号
     */
    private String linkId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 真实环节类型
     */
    private String realLinkType;

    /**
     * 受理状态
     */
    private String enableAcceptanceMode;
}
