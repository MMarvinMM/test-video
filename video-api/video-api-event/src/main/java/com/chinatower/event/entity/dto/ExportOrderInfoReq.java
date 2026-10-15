package com.chinatower.event.entity.dto;

import lombok.Data;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/4 10:09
 */
@Data
public class ExportOrderInfoReq {

    /**
     * 工单号
     */
    private String warningOrderId;

    /**
     * 导出来源（1：事件详情  2：回收站详情   4：属地事件详情  5：属地回收站详情  ）
     */
    private Integer exportSource;

    /**
     * 导出来源（1：转移告警  2：删除告警）
     */
    private Integer exportType;

    /**
     * 导出来源（1：铁塔  2：租户）
     */
    private Integer exportView;

    /** 水平方位角 */
    private String horiAzimuthAngle;

    /**
     * 摄像机俯仰角
     */
    private String devicePitch;

    /**
     * 摄像机水平角
     *
     */
    private String deviceHorizontal;

}
