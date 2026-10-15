package com.chinatower.sync.consumer.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 摄像机通道视频质量检测实体类
 */
@Data
public class ChannelDetectionFactorDTO extends BaseDataScopeEntity {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "通道编码")
    private String channelCode;

    @ApiModelProperty(value = "通道名称")
    private String channelName;

    @ApiModelProperty(value = "检测因子编码")
    private String factorCode;

    @ApiModelProperty(value = "检测因子名称")
    private String factorName;

    @ApiModelProperty(value = "检测因子")
    private String factorValue;

    private String diagnoseImageUrl;

    private String diagnoseVideoUrl;

    @ApiModelProperty(value = "检测时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date diagnoseTime;
}
