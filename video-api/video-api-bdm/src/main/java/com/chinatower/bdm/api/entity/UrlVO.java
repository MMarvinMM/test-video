package com.chinatower.bdm.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 功能描述：
 * 实用工具-侦测返回结果-播放流实体
 * @author：hued
 * @date：2022/3/31 14:16
 * @version: release_v1.1
 */
@Data
@ToString
public class UrlVO  implements Serializable {
    @ApiModelProperty("流url地址")
    private String streamUrl;
    @ApiModelProperty("设备编码")
    private String deviceCode;
    @ApiModelProperty("通道编码")
    private String channelCode;
    @ApiModelProperty("经度")
    private String longitude;
    @ApiModelProperty("纬度")
    private String latitude;
    @ApiModelProperty("站址编码")
    private String siteCode;
}
