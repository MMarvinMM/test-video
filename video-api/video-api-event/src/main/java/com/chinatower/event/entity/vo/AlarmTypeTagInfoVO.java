package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlarmTypeTagInfoVO implements Serializable {

    /**
     * 告警类型标签编码
     */
    private Long tagId;
    /**
     * 告警类型标签名称
     */
    private String tagName;
    /**
     * 告警类型编码
     */
    private String typeValue;
    /**
     * 告警类型名称
     */
    private String typeName;
}
