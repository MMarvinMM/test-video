package com.chinatower.platform.entity.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 功能说明：系统区域平铺
 *
 * @author 王金贵
 * @date 2021-11-30
 * @requestID R-ZBZB-202107050748380
 */
@Data
public class SysTiledArea implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "省份ID")
    @NotBlank(message = "省份ID")
    private String provinceId;

    @ApiModelProperty(value = "省份名称")
    @NotBlank(message = "省份名称")
    private String provinceName;

    @ApiModelProperty(value = "城市ID")
    @NotBlank(message = "城市ID")
    private String cityId;

    @ApiModelProperty(value = "城市名称")
    @NotBlank(message = "城市名称")
    private String cityName;

    @ApiModelProperty(value = "区县ID")
    @NotBlank(message = "区县ID")
    private String regionId;

    @ApiModelProperty(value = "区县名称")
    @NotBlank(message = "区县名称")
    private String regionName;
}
