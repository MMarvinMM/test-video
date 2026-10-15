package com.chinatower.device.dto;

import com.chinatower.device.entity.vo.SensorEventVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SensorEventInfoVO implements Serializable {


    /**
     * 数量
     */
    private Long count;


    /**
     * 监测事件列表
     */
    private List<SensorEventVO> SensorEventList;
}
