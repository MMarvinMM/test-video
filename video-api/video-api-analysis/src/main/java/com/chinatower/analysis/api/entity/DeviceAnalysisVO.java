package com.chinatower.analysis.api.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * 设备分析
 */
@Data
public class DeviceAnalysisVO extends AnalysisVO {

    @NotNull(message = "查询类型不能为空")
    private Integer queryDeviceLabel;
}
