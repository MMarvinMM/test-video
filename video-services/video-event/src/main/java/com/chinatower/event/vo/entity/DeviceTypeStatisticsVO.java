package com.chinatower.event.vo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceTypeStatisticsVO implements Serializable {
        private static final long serialVersionUID = 1L;
        // 设备类型编号
        private String categoryCode;

        // 设备类型名称
        private String deviceTypeName;

        // 设备类型对应设备数量
        private int  deviceTypeNum;

}
