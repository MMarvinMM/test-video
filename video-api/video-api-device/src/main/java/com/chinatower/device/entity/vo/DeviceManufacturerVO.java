package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


/**
 * 设备厂家信息VO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceManufacturerVO extends BaseDataScopeEntity {

    @ApiModelProperty("设备型号ID")
    private Long id;

    @ApiModelProperty("厂家编码")
    private String manufacturerCode;

    @ApiModelProperty("厂家名称")
    private String manufacturerName;

    @ApiModelProperty("类型 1-无人机 2-机巢 3-无人机型号 4-机巢型号 5-相机型号 6-挂载型号")
    private int type;
}
