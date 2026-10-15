package com.chinatower.airline.dto.response;

import lombok.Data;

@Data
public class UavInfo {
    /**
     * 设备编码
     */
    private String uavCode;

    /**
     * 飞机sn
     */
    private String uavSnCode;

    /**
     * 机型
     */
    private String uavModelName;
}
