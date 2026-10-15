package com.chinatower.device.dataobject;

import com.chinatower.device.entity.vo.SensorAttrMapVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SensorPeriodPageResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 传感器和物联因子关系对象
     */
    private List<SensorMonitorInfo> sensorMonitorInfo;

    /**
     * 监测时间列表
     */
    private List<SensorAttrMapVO> monitorDataList;


    /**
     * 数量
     */
    Long count;
}
