package com.chinatower.platform.entity.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @ClassName VideoOrderReq
 * @Deacription 查询订单编码对象
 * @Author chenmf
 * @Date 2022/1/19 9:27
 * @Version 1.0
 **/
@Data
@ApiModel(value = "查询订单编码对象",description = "查询订单编码对象")
public class VideoOrderReq implements Serializable {

    /**
     * 租户Id
     * @required true
     */
    @NotBlank(message = "租户Id不能为空")
    @ApiModelProperty(value = "租户Id")
    public String tenantId;

    /**
     * 设备编码
     * @required true
     */
    @NotBlank(message = "设备编码不能为空")
    @ApiModelProperty(value = "设备编码")
    public String deviceCode;

    /**
     * 通道编码
     * @required true
     */
    @NotBlank(message = "通道编码不能为空")
    @ApiModelProperty(value = "通道编码")
    public String channelCode;

    /**
     * 行业编码
     * @required false
     */
    @ApiModelProperty(value = "行业编码")
    public String industryCode;
}