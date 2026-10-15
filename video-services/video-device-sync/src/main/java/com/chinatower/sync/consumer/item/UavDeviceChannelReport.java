package com.chinatower.sync.consumer.item;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 无人机设备通道
 */
@Data
public class UavDeviceChannelReport extends BaseDataScopeEntity implements Serializable {

    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

    @ApiModelProperty(value = "状态 0在线，1离线，2 从未连接")
    private String status;

    @ApiModelProperty(value = "删除标志, 0：正常，2：删除")
    private String delFlag;

    //canal类型
    private String canalType;
    // 执行时间
    private long ts;
}
