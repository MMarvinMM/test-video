package com.chinatower.industry.dto;

import com.chinatower.platform.entity.devicemgmt.vo.ChannelInfoVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author yy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceAndIotDeviceDTO implements Serializable {

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 设备类型：1-摄像机  2-物联网
     */
    private String deviceType;

    /**
     * 设备状态:0在线，1离线
     */
    private String status;

    private String longitude;

    private String latitude;

    /**
     * 通道
     */
    private List<ChannelInfoVO> channels;

}
