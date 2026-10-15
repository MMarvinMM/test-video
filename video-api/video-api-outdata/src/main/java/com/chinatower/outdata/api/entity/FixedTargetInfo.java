package com.chinatower.outdata.api.entity;

import lombok.Data;

@Data
public class FixedTargetInfo {

    /**
     * 设备ID
     */
    private String id;

    /**
     * 坐标信息
     */
    private String pos;

    /**
     * 置信度
     */
    private Double confidence;

    /**
     * 确认状态
     */
    private Integer isVerified;
}
