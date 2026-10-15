package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TradeQueryReassignLinkDto implements Serializable {

    /**
     * 告警流程ID
     * @required true
     */
    private String alarmFlowId;

    /**
     * 流程模板ID
     * @required true
     */
    private String templateId;

    /**
     * 当前环节ID
     * @required true
     */
    private String curLink;

    /**
     * 行业应用编码
     * @required true
     */
    private String industryCode;

    /**
     * 行业版本
     * @required true
     */
    private String appVerCode;

    /**
     * 告警来源
     * @required false
     */
    private String warningSource;
}
