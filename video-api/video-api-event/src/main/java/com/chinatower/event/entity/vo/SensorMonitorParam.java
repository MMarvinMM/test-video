package com.chinatower.event.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/5 14:57
 */

@Data
public class SensorMonitorParam implements Serializable {

    /**
     * 监测指标编码
     */
    private String monitorCode;

    /**
     * 监测指标
     */
    private String monitorName;

    /**
     * 检测值
     */
    private String monitorValue;

    /**
     * 告警阈值
     */
    private String thresholdValue;

    private List<OneFactoryVO> newMonitors;
}
