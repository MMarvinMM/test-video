package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Name IotDeviceConfigDTO
 * @Description
 * @Author lzt
 * @Date 2024/01/19/
 */
@Data
public class IotDeviceConfigVO extends BaseDataScopeEntity implements Serializable {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "序号")
    private String rown;

    @ApiModelProperty(value = "摄像机编号")
    private String deviceCode;

    @ApiModelProperty(value = "摄像机名称")
    private String deviceName;

    @ApiModelProperty(value = "是否绑定物联设备（已绑定/未绑定）:0已绑定，1未绑定")
    private String bindFlag;

    @ApiModelProperty(value = "物联设备编号")
    private String iotDeviceCode;

    @ApiModelProperty(value = "物联设备名称")
    private String iotDeviceName;

    @ApiModelProperty(value = "当前页")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页显示条数")
    private Integer pageSize = 10;

    private static final long serialVersionUID = 1L;

}
