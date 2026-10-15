package com.chinatower.platform.entity.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DeviceByPlatformVO
 * @Deacription 设备所属平台信息对象
 * @Author chenmf
 * @Date 2022/3/7 9:49
 * @Version 1.0
 **/
@Data
@ApiModel(value = "deviceByPlatformVO",description = "设备所属平台信息对象")
public class DeviceByPlatformVO implements Serializable {
    
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;
    
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "通道编码")
    private String channelCode;
    
    @ApiModelProperty(value = "通道名称")
    private String channelName;
    
    @ApiModelProperty(value = "设备状态")
    private String deviceStatus;
    
    @ApiModelProperty(value = "设备地址")
    private String location;
    
    @ApiModelProperty(value = "站址编码")
    private String siteCode;
    
    @ApiModelProperty(value = "站址名称")
    private String siteName;
    
    @ApiModelProperty(value = "经度")
    private String longitude;
    
    @ApiModelProperty(value = "纬度")
    private String latitude;
    
    @ApiModelProperty(value = "行政区域")
    private String admCode;
    
    @ApiModelProperty(value = "省份")
    private String provinceId;
    
    @ApiModelProperty(value = "地市")
    private String cityId;
    
    @ApiModelProperty(value = "区县")
    private String countyId;

    @ApiModelProperty(value = "设备类型")
    private int categoryCode;

    @ApiModelProperty(value = "设备归属平台编码")
    private String platFormCode;
    
    @ApiModelProperty(value = "摄像头ip地址")
    private String ip;
    
    @ApiModelProperty(value = "摄像头端口")
    private String port;
    
    @ApiModelProperty(value = "设备登录用户名")
    private String devUserName;
    
    @ApiModelProperty(value = "设备登录密码")
    private String devPassword;
    
    @ApiModelProperty(value = "挂高")
    private String height;
}
