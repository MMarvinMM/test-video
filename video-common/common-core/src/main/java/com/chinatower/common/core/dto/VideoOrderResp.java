package com.chinatower.common.core.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName VideoOrderResp
 * @Deacription 订单编码对象
 * @Author chenmf
 * @Date 2022/1/19 9:27
 * @Version 1.0
 **/
@Data
@ApiModel(value = "订单编码对象",description = "订单编码对象")
public class VideoOrderResp implements Serializable {

    @ApiModelProperty(value = "租户Id")
    public String tenantId;

    @ApiModelProperty(value = "订单编码")
    public String orderCode;

    @ApiModelProperty(value = "套餐编码")
    public String packageCode;

    //行业版本编码
    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;

    //行业编码
    @ApiModelProperty(value = "行业编码")
    private String industryCode;
}
