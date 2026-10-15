package com.chinatower.event.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class DevicePointTurn {

    /**
     * 设备编码
     * @required true
     */
    @NotBlank(message = "设备编码不可以为空")
    private String deviceCode;

    /**
     * 通道编码
     * @required true
     */
    //@NotBlank(message = "通道编码不可以为空")
    private String channelCode;


    /**
     * 经度
     * @required true
     */
    private String longitude;

    /**
     * 纬度
     * @required true
     */
    private String latitude;

    /**
     * 摄像机挂高
     * @required true
     */
    private String height;

    /**
     * 百分比点位信息
     * @required true
     */
    @NotBlank(message = "百分比点位集合不可以为空")
    private List<DevicePointVo> points;

    /**
     * 自定义入参P值
     */
    private String paramPan;

    /**
     * 自定义入参T值
     */
    private String paramTitl;

    /**
     * 自定义入参Z值
     */
    private String paramZoom;

    /**
     * 自定义入参经度
     * @required true
     */
    private String paramLongitude;

    /**
     * 自定义入参纬度
     * @required true
     */
    private String paramLatitude;


}
