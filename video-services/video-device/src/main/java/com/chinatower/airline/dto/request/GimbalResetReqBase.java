package com.chinatower.airline.dto.request;

import com.chinatower.airline.anno.EnumTypeValid;
import com.chinatower.airline.enums.GimbalResetModeTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author ljy
 * @date 2025/12/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("云台控制基础参数类")
public class GimbalResetReqBase extends BaseAirDeviceWithNodeInfoReq {

    @ApiModelProperty(value = "重置模式类型",example = "0-回中,1-向下,2-偏航回中,3-俯仰向下")
    @NotNull(message = "重置模式类型不能为空")
    @EnumTypeValid(message = "重置类型支持 0｜1｜2｜3(回中|向下|偏航回中|俯仰向下)", enumClass = GimbalResetModeTypeEnum.class)
    private Integer resetMode;
    
    @ApiModelProperty("相对机头方向的朝向角度")
    private Integer yamAngle;
    
    @ApiModelProperty("相对水平方向的俯仰角")
    private Integer pitchAngle;
}
