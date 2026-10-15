package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class EventStatisticDataVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 告警类型编码
     * @required true
     */
    private String typeId;

    /**
     * 告警类型名称
     * @required true
     */
    private String typeName;

    /**
     * 告警时间
     * @required true
     */
    private String eventDate;

    /**
     * 数量
     * @required true
     */
    private int num;
}
