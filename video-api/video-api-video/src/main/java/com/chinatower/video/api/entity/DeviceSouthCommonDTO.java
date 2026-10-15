package com.chinatower.video.api.entity;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;

import com.chinatower.common.core.entity.BaseDataScopeEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by zheng.yan on 2022/2/20.
 */
@Data
@Alias("DeviceSouthCommonDTOVideo")
public class DeviceSouthCommonDTO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "订购编码", required = true)
    private String orderCode;
    @ApiModelProperty(value = "客户编码", required = true)
    private String tenantId;
    @ApiModelProperty(value = "用户编码", required = true)
    private String userCode;
    @ApiModelProperty(value = "设备编码", required = true)
    private String deviceCode;
    @ApiModelProperty(value = "通道编码", required = true)
    private String channelCode;
    @ApiModelProperty(value = "铁塔用户编码")
    private String towerCode;
}
