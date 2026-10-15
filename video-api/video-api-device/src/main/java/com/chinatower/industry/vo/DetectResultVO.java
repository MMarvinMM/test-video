package com.chinatower.industry.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 功能描述：
 * 看这里结果实体
 * @author：hued
 * @date：2022/3/31 14:16
 * @version: release_v1.1
 */
@Data
@ToString
public class DetectResultVO {
    @ApiModelProperty("站址信息")
    private List<SiteVO> siteList;
    @ApiModelProperty("播放流信息")
    private List<UrlVO> urlList;
}
