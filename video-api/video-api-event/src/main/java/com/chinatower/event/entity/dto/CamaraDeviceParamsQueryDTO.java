package com.chinatower.event.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *  获取摄像机经纬度信息查询对象
 *
 * @author lisy
 */
@Data
@ApiModel(value = "获取摄像机经纬度信息查询对象",description = "获取摄像机经纬度信息查询对象")
public class CamaraDeviceParamsQueryDTO implements Serializable {

    /**
     * 设备编码（一键告警额外需要传设备编码）
     * @required true
     */
    @NotBlank(message = "设备编码不可以为空")
    @ApiModelProperty(value = "设备编码（一键告警额外需要传设备编码）")
    private String deviceCode;

    /**
     * 通道编码（一键告警额外需要传通道编码）
     * @required true
     */
    @ApiModelProperty(value = "通道编码（一键告警额外需要传通道编码）")
    private String channelCode;


    /**
     * 百分比点位横坐标
     * @required true
     */
    @NotBlank(message = "百分比点位横坐标不可以为空")
    @ApiModelProperty(value = "百分比点位横坐标")
    private String percentX;

    /**
     * 百分比点位纵坐标
     * @required true
     */
    @NotBlank(message = "百分比点位纵坐标不可以为空")
    @ApiModelProperty(value = "百分比点位纵坐标")
    private String percentY;

}
