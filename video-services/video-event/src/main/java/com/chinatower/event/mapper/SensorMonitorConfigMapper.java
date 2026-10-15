package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.SenSorMonitorConfigDO;
import com.chinatower.event.vo.SensorMonitorConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SensorMonitorConfigMapper extends BaseMapper<SenSorMonitorConfigDO> {

    /**
     * 根据条件查询租户下物联设备的因子表达式和对应告警类型
     * @param param
     * @return
     */
    List<SensorMonitorConfigVO> queryDeviceSensorMonitor(@Param("param") SensorMonitorConfigVO param);

    /**
     * 根据条件查询租户下物联因子的因子表达式和对应告警类型
     * @param param
     * @return
     */
    List<SensorMonitorConfigVO> querySensorMonitor(@Param("param") SensorMonitorConfigVO param);
}
