package com.chinatower.order.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 资源初始化转换
 */
@Data
public class ResourceTypeInitDTO implements Serializable {

    @ApiModelProperty("租户id")
    private String tenantId;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("应用版本编码")
    private String appVerCode;

    @ApiModelProperty("行业编码")
    private String industryCode;
}
