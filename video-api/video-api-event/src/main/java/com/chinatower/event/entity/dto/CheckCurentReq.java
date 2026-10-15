package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CheckCurentReq implements Serializable {
    /**
     * 工单号
     * @required true
     */
    private String orderId;
    /**
     * 当前环节ID
     * @required true
     */
    private String linkId;

    /**
     * 真实环节类型(1研判2调度4处置5核实6办结)
     * @required false
     */
    private String realLinkType;

    /**
     * 是否开启受理模式（0:不开启 1:开启）
     * @required false
     */
    private String enableAcceptanceMode;

    /**
     * 表名
     */
    private String tableName;
}
