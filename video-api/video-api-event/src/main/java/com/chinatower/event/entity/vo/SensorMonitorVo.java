package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: SensorMonitorVo
 * @description: 物联设备监测因子
 * @author: yongqi.han
 * @date: 2022/7/19
 * @requestID 需求单号及需求名称
 */
@Data
public class SensorMonitorVo  implements Serializable {
    /**
     * 检测指标编号
     */
    private String monitorCode;
    /**
     * 检测指标名称
     */
    private String monitorName;

    /**
     * 监测值
     */
    private String monitorValue;

    /**
     * 告警阈值
     */
    private String thresholdValue;

}
