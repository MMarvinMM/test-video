package com.chinatower.device.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName: ChannelVideoPlanVO
 * @Description: 通道录像计划实体
 * @Author: chenmf
 * @Date: 2022-01-24 11:51
 **/
@Data
@ToString
public class ChekcChannelVideoPlanVO implements Serializable {
    private static final long serialVersionUID = 1L;

    //设备编号
    @NotNull(message = "设备编号不能为空")
    @ApiModelProperty(value = "设备编号")
    private String deviceCode;

    //通道编号
    @NotNull(message = "通道编号不能为空")
    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    //客户唯一编码
    @ApiModelProperty(value = "客户编码")
    private String tenantId;

    
}
