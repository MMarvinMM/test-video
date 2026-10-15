package com.chinatower.industry.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 功能描述：
 * 看这里-站址实体
 * @author：hued
 * @date：2022/3/31 14:16
 * @version: release_v1.1
 */
@Data
@ToString
public class SiteVO {
    @ApiModelProperty("经度")
    private String longitude;
    @ApiModelProperty("纬度")
    private String latitude;
    @ApiModelProperty("站址编码")
    private String siteCode;
    @ApiModelProperty("站址名称")
    private String siteName;
    @ApiModelProperty("是否在线状态 0：在线 1：离线")
    private String status;
}
