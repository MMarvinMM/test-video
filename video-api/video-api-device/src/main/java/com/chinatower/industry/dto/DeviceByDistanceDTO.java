package com.chinatower.industry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DeviceByDistanceDTO implements Serializable {

    /**
     * 设备code
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型  1-摄像机  2-物联网设备
     */
    private String deviceType;

    /**
     * 在线状态  0-在线 1-离线
     */
    private String status;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 维度
     */
    private String latitude;

    /**
     * 距离    单位：米
     */
    private Integer distance;

    private String channelCode;

    private String channelName;
}
