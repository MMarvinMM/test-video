package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RangeOrgInfo {

    @ApiModelProperty(value = "区域编码")
    private String regionCode;

    @ApiModelProperty(value = "区域名称")
    private String regionName;

    @ApiModelProperty(value = "排序序号")
    private Integer order;
}
