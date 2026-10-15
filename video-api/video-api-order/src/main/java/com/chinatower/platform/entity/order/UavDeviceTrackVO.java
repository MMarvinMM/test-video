package com.chinatower.platform.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "无人机遥控信息",description = "无人机遥控信息")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UavDeviceTrackVO implements Serializable {

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "轨迹时间点")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date trackTime;

    @ApiModelProperty(value = "经度")
    private String lng;

    @ApiModelProperty(value = "纬度")
    private String lat;

    @ApiModelProperty(value = "绝对高度，单位：m")
    private String height;

    @ApiModelProperty(value = "水平速度;单位：m/s")
    private String horizontalSpeed;

    @ApiModelProperty(value = "垂直速度;单位：m/s")
    private String verticalSpeed;

    @ApiModelProperty(value = "电池剩余电量百分比;单位：%")
    private String capacityPercent;

    @ApiModelProperty(value = "俯仰轴角度;单位：度")
    private String attitudePtch;

    @ApiModelProperty(value = "横滚轴角度;单位：度")
    private String attitudeRoll;

    @ApiModelProperty(value = "偏航轴角度;单位：度")
    private String attitudeHead;

    @ApiModelProperty(value = "风速;单位：m/s")
    private String windSpeed;

    @ApiModelProperty(value = "剩余飞行时间;单位：秒")
    private String remainFlightTime;

    @ApiModelProperty(value = "飞行状态;0:待机,1:起飞准备,2:起飞准备完毕,3:手动飞行,4:自动起飞,5:航线飞行,6:全景拍照,7:智能跟随,8:ADS-B 躲避,9:自动返航,10:自动降落,11:强制降落,12:三桨叶降落,13:升级中,14:未连接,15:辅助飞行功能 - APAS,16:虚拟摇杆")
    private String flightStatus;
}
