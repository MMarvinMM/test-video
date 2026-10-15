package com.chinatower.event.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/13 10:04
 */

@Data
public class ExportDocSensorMonitorVo {

    private List<SensorMonitorParam> monitors;
}
