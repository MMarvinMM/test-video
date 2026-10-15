package com.chinatower.video.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 视频设备编码通道编码
 * Created by zhujun on 2022/1/18.
 */
@Data
public class VideoBaseReq extends BaseDataScopeEntity {
    /**
     * 设备编码
     * @required true
     */
    @NotBlank(message = VideoConstants.NOT_EMPTY_DEV_CODE)
    private String deviceCode;

    /**
     * 通道编码
     * @required true
     */
    @NotBlank(message = VideoConstants.NOT_EMPTY_CHANNEL_CODE)
    private String channelCode;

    /**
     * 订单编码
     */
    private String orderCode;

    private String userCode;

    /**
     * 设备类型
     */
    private String deviceType;
}
