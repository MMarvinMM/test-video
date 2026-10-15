package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SensorAttrInfoVO implements Serializable {

    /**
     * 时间频率
     */
    private String timeFrequency;

    /**
     * 提交监测数据列表
     */
    private List<SensorAttrVO> sensorCommitData;
}
