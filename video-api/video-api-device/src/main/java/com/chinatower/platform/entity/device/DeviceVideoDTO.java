package com.chinatower.platform.entity.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DeviceVideoDTO
 * @Deacription
 * @Author chenmf
 * @Date 2022/5/17 14:40
 * @Version 1.0
 **/
@Data
@ApiModel(description = "设备操作对象")
public class DeviceVideoDTO implements Serializable {
    /** 操作类型 */
    @ApiModelProperty(value = "操作类型")
    private String videoType;
    
    /** 设备编码 */
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;
}
