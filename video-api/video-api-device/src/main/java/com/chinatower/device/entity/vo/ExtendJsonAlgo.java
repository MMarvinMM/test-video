package com.chinatower.device.entity.vo;

import lombok.Data;

@Data
public class ExtendJsonAlgo {
    /**
     * 告警框颜色字段
     */
    private String alarmFrameColor;

    /**
     * 告警框粗细字段
     */
    private String alarmFrameWidth;

    /**
     * 是否开启双通道并行分析 0：否，1：是
     */
    private String doubleImgAnalysis;
}
