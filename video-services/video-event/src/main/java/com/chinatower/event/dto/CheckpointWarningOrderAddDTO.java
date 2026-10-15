package com.chinatower.event.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@ApiModel("车辆进出记录添加DTO")
public class CheckpointWarningOrderAddDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "车牌号", required = true)
    @NotBlank(message = "车牌号不能为空")
    private String vehicleNumber;

    @ApiModelProperty("车辆类型：1-小型车，14-小型新能源车，20-大型车")
    private String vehicleType;

    @ApiModelProperty("车牌颜色")
    private String vehicleNumColor;

    @ApiModelProperty("车辆颜色")
    private String vehicleColor;

    @ApiModelProperty(value = "经度", required = true)
    @NotBlank(message = "经度不能为空")
    private String longitude;

    @ApiModelProperty(value = "纬度", required = true)
    @NotBlank(message = "纬度不能为空")
    private String latitude;

    @ApiModelProperty(value = "省编码", required = true)
    @NotBlank(message = "省编码不能为空")
    private String provinceCode;

    @ApiModelProperty(value = "省名称", required = true)
    @NotBlank(message = "省名称不能为空")
    private String provinceName;

    @ApiModelProperty(value = "市编码", required = true)
    @NotBlank(message = "市编码不能为空")
    private String cityCode;

    @ApiModelProperty(value = "市名称", required = true)
    @NotBlank(message = "市名称不能为空")
    private String cityName;

    @ApiModelProperty(value = "县编码", required = true)
    @NotBlank(message = "县编码不能为空")
    private String countyCode;

    @ApiModelProperty(value = "县名称", required = true)
    @NotBlank(message = "县名称不能为空")
    private String countyName;

    @ApiModelProperty(value = "告警时间", required = true)
    @NotBlank(message = "告警时间不能为空")
    private String alarmTime;

    @ApiModelProperty(value = "设备编码", required = true)
    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    @ApiModelProperty(value = "设备名称", required = true)
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    @ApiModelProperty(value = "通道编码", required = true)
    @NotBlank(message = "通道编码不能为空")
    private String channelCode;

    @ApiModelProperty(value = "通道名称", required = true)
    @NotBlank(message = "通道名称不能为空")
    private String channelName;

    @ApiModelProperty("报警框图URL")
    private String alarmPictureUrl;

    @ApiModelProperty("原始图片URL")
    private String originalPictureUrl;

    @ApiModelProperty(value = "租户ID", required = true)
    @NotBlank(message = "租户ID不能为空")
    private String tenantId;

    @ApiModelProperty(value = "租户名称", required = true)
    @NotBlank(message = "租户名称不能为空")
    private String tenantName;

    @ApiModelProperty(value = "行业编码")
    private String industryCode;

    @ApiModelProperty(hidden = true)
    private String warningOrderId;

    @ApiModelProperty(hidden = true)
    private String warningSource;

    @ApiModelProperty(hidden = true)
    private String alarmType;

    @ApiModelProperty(hidden = true)
    private String alarmBody;

    @ApiModelProperty(hidden = true)
    private String alarmCode;

    @ApiModelProperty(hidden = true)
    private String deleteFlag;

    @ApiModelProperty(hidden = true)
    private String appVerCode;

    @ApiModelProperty(hidden = true)
    private String importanceLevel;

    @ApiModelProperty(hidden = true)
    private String pan;

    @ApiModelProperty(hidden = true)
    private String tilt;

    @ApiModelProperty(hidden = true)
    private String zoom;

    @ApiModelProperty(hidden = true)
    private String algorithmCode;

    @ApiModelProperty(hidden = true)
    private String alarmVideoUrl;

    @ApiModelProperty(hidden = true)
    private String taskId;
}
