package com.chinatower.platform.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RadarAlarmBlackWhiteExportDTO implements Serializable {

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 名单类型 1-白名单 2-黑名单
     */
    private Integer listType;
}
