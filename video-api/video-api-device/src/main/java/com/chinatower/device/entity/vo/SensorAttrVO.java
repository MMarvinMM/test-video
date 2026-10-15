package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zheng.yan on 2022/5/8.
 */
@Data
public class SensorAttrVO extends SensorMonitorRelInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 监测因子编码
     */
    private String attrCode;

    /**
     * 监测因子名称
     */
    private String attrName;

    /**
     * 监测数据
     */
    private String attrValue;

    /**
     * 数据单位
     */
    private String attrUnits;

    /**
     * 监测时间 格式yyyy-MM-dd HH:mm:ss
     */
    private String monitorTime;

    /**
     * 设备编码
     */
    private String deviceCode;
}
