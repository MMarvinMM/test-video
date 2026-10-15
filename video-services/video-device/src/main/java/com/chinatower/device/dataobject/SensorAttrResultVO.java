package com.chinatower.device.dataobject;

import com.chinatower.device.entity.vo.SensorAttrVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SensorAttrResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物联因子列表
     */
    private List<SensorAttrVO> sensorAttrsList;

    /**
     * 是否存在传感器
     */
    boolean isHaveSensor = false;
}
