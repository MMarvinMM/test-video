package com.chinatower.event.repository.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.mapper.SensorMonitorConfigMapper;
import com.chinatower.event.repository.SensorMonitorConfigService;
import com.chinatower.event.vo.SensorMonitorConfigVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@DS(EbikeDataBaseConstant.BASE)
public class SensorMonitorConfigServiceImpl implements SensorMonitorConfigService {

    @Resource
    private SensorMonitorConfigMapper sensorMonitorConfigMapper;

    /**
     * 根据条件查询租户下物联因子的因子表达式和对应告警类型
     * @param param
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SensorMonitorConfigVO> queryDeviceSensorMonitor(SensorMonitorConfigVO param) {
        return sensorMonitorConfigMapper.queryDeviceSensorMonitor(param);
    }

    /**
     * 根据条件查询租户下物联因子的因子表达式和对应告警类型
     * @param param
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SensorMonitorConfigVO> querySensorMonitor(SensorMonitorConfigVO param) {
        return sensorMonitorConfigMapper.querySensorMonitor(param);
    }
}
