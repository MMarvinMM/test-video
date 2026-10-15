package com.chinatower.industry.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 功能描述：
 * 看这里-播放流实体
 * @author：hued
 * @date：2022/3/31 14:16
 * @version: release_v1.1
 */
@Data
@ToString
public class UrlVO {
    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ApiModelProperty("通道编码")
    private String channelCode;
    @ApiModelProperty("经度")
    private String longitude;
    @ApiModelProperty("纬度")
    private String latitude;
    @ApiModelProperty("实时流地址")
    private String streamUrl;
    private float pan;
    private float tilt;
    private float zoom;
}
