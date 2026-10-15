package com.chinatower.airline.dto.request;

import com.chinatower.airline.anno.EnumTypeValid;
import com.chinatower.airline.enums.ReturnModeTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 返航参数
 *
 * @author ljy
 * @date 2026/1/4
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReturnHomeReq extends BaseAirDeviceWithNodeInfoReq{
    
    @ApiModelProperty(value = "返航模式",notes = "0-直线返航；1-原路返航；2-原地降落")
    @NotNull(message = "返航模式不能为空")
    @EnumTypeValid(enumClass = ReturnModeTypeEnum.class, message = "返航模式支持 0|1|2 (直线返航|原路返航|原地降落)")
    private Integer returnMode;
}
