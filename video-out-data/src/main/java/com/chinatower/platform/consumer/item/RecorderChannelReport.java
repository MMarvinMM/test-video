package com.chinatower.platform.consumer.item;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RecorderChannelReport extends BaseDataScopeEntity implements Serializable {

    /**
     * 通道编号
     */
    @ApiModelProperty(value = "通道编号")
    private String channelCode;

    /**
     * 设备编码
     */
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    /**
     * 通道名称
     */
    @ApiModelProperty(value = "通道名称")
    private String channelName;

    /**
     * 状态 0在线，1离线，2 从未连接
     */
    @ApiModelProperty(value = "状态 0在线，1离线，2 从未连接")
    private String status;

    /**
     * 删除标志, 0：正常，2：删除
     */
    @ApiModelProperty(value = "删除标志, 0：正常，2：删除")
    private String delFlag;

    //canal类型
    private String canalType;
    // 执行时间
    private long ts;
}
