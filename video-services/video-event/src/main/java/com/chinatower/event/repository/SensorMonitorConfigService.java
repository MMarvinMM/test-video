package com.chinatower.event.repository;

import com.chinatower.event.vo.SensorMonitorConfigVO;

import java.util.List;

public interface SensorMonitorConfigService {

    /**
     * 根据条件查询租户下物联因子的因子表达式和对应告警类型
     * @param param
     * @return
     */
    List<SensorMonitorConfigVO> queryDeviceSensorMonitor(SensorMonitorConfigVO param);

    /**
     * 根据条件查询租户下物联因子的因子表达式和对应告警类型
     * @param param
     * @return
     */
    List<SensorMonitorConfigVO> querySensorMonitor(SensorMonitorConfigVO param);
}
