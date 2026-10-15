package com.chinatower.platform.entity.device;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by zheng.yan on 2022/2/20.
 */
@Data
public class DeviceSouthCommonDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 订单编码 */
    @ApiModelProperty(value = "订购编码", required = true)
    private String orderCode;
    /** 客户编码 */
    @ApiModelProperty(value = "客户编码", required = true)
    private String tenantId;
    /** 用户编码 */
    @ApiModelProperty(value = "用户编码", required = true)
    private String userCode;
    /** 设备编码 */
    @ApiModelProperty(value = "设备编码", required = true)
    private String deviceCode;
    /** 通道编码 */
    @ApiModelProperty(value = "通道编码", required = true)
    private String channelCode;
    /** 铁塔用户编码 */
    @ApiModelProperty(value = "铁塔用户编码")
    private String towerCode;
    /** 行业编码 */
    private String industryCode;

    /** 行业版本编码 */
    private String appVerCode;

}
