package com.chinatower.platform.entity.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName GuardDTO
 * @Description 看守位参数
 * @Author chenshaowu
 * @Date 2022/2/21
 **/
@Data
public class GuardDTO extends DeviceSouthCommonDTO {

    @ApiModelProperty(value = "订购编码")
    private String orderCode;

    @ApiModelProperty(value = "看守位名称")
    @NotNull(message = "看守位名称不能为空")
    private String guardName;
    
    @ApiModelProperty(value = "预置点id")
    private String presetId;

    @ApiModelProperty(value = "看守位id")
    private String guardId;

    @ApiModelProperty(value = "停留时间")
    @NotNull(message = "停留时间不能为空")
    private String staySeconds;
    
    @ApiModelProperty(value = "看守位状态")
    @NotNull(message = "看守位状态不能为空")
    private String guardStatus;

}
