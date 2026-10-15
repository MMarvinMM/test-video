package com.chinatower.sync.consumer.item;

import com.chinatower.sync.consumer.entity.ChannelStatusReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 无人机设备状态实体类
 * @author Lenovo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UavDeviceStatusReport extends CommonYgReport implements Serializable {

    @ApiModelProperty(value = "设备编码")
    @NotBlank(message = "设备编码不能为空!")
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备类型 NEST: 机巢设备 UAV: 无人机设备")
    private String deviceType;

    @ApiModelProperty(value = "运行状态(0待机（没在飞，也没有准备等动作），1运行中（任务准备、降落回收、传数据等），2飞行中（飞行中）)")
    private Integer runStatus;

    @ApiModelProperty(value = "设备状态(1离线，0在线，3故障)")
    private Integer deviceStatus;

    @ApiModelProperty(value = "通道信息")
    private List<ChannelStatusReq> channelInfos;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "海拔")
    private String altitude;

}
