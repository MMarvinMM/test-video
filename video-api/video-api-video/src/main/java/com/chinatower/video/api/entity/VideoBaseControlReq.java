package com.chinatower.video.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VideoBaseControlReq extends BaseDataScopeEntity {
    /**
     * 设备编码
     */
    @NotBlank(message = VideoConstants.NOT_EMPTY_DEV_CODE)
    private String deviceCode;

    /**
     * 通道编码
     */
    @NotBlank(message = VideoConstants.NOT_EMPTY_CHANNEL_CODE)
    private String channelCode;

    /**
     * 订单编码
     */
    private String orderCode;

    private int duration = 3;

    /**
     * 云台控制令牌
     */
    private String lockToken;

    /**
     * ptz 是否需要转义：1不需要；为空需要转义
     */
    private String ptzIsEsc;
    
    // 调用源头
    private String fromSource;

}
