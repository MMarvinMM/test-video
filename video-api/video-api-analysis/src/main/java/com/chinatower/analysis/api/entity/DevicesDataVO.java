package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 设备统计返回结果VO
 */
@Data
public class DevicesDataVO extends AreaDataVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 设备总数
     * @required true
     */
    private int deviceNum;

    /**
     * 在线设备总数
     * @required true
     */
    private int devOnlineNum;
}
