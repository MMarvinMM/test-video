package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author cuibaoluo
 * @date 2025/12/18
 * @description：
 */
@Data
public class AirDeviceDto extends BaseDataScopeEntity implements Serializable {

    /**
     * 机场编号
     */
    @ApiModelProperty(value = "机场编号")
    private String uavNestCode;

    @ApiModelProperty(value = "接入节点")
    private String acceptNode;

    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;
}
