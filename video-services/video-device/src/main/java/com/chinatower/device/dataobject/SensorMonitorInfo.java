package com.chinatower.device.dataobject;

import com.chinatower.device.entity.vo.SensorAttrVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SensorMonitorInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 传感器名称
     */
    private String sensorName;

    /**
     * 传感器编码
     */
    private String sensorCode;

    List<SensorAttrVO> monitorList;
}
