package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SensorStatusInfoDTO implements Serializable {

    /**
     * 设备编码列表
     */
    private List<String> deviceCodeList;

    /**
     * 物联因子状态 1-在线 0-离线
     */
    private Integer monitorStatus;
}