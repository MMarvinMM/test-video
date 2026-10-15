package com.chinatower.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolygonNoByDeviceAndChannelDto implements Serializable {

    private Integer polygonNo;

    private String deviceCode;

    private String channelCode;

    private String presetId;

    private String tenantId;

    private String industryCode;
}
