package com.chinatower.video.api.entity;

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
public class PointsAndDeviceModelDto implements Serializable {

    private String configId;

    private String points;

    private String deviceCode;

    private String channelCode;

    private String modelCode;

    private String deviceModelCode;

    private String picSensorLength;

    private String picSensorWidth;

    private String minFocus;

}
