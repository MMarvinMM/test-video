package com.chinatower.platform.project.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("行业列表视图")
public class IndustrySelectVO implements Serializable {

    @ApiModelProperty(value = "行业名称")
    private String industryName;

    @ApiModelProperty(value = "行业版本名称")
    private String appVerName;

    @ApiModelProperty(value = "行业编码")
    private String industryCode;

    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;


}
