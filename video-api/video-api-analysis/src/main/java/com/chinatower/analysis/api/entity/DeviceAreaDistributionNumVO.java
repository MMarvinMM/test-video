package com.chinatower.analysis.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 告警进度处理统计
 *
 * @author lisy
 */
@Data
public class DeviceAreaDistributionNumVO implements Serializable {

    private static final long serialVersionUID = 2934248871780343431L;
    /**
     *  告警总数
     *  @required true
     */
    private int allNum;

    /**
     *  设备名称
     * @required true
     */
    private String deviceName;

    /**
     *  设备编码
     * @required true
     */
    private String deviceCode;

}
