package com.chinatower.bdm.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class DetectResultVO implements Serializable {
    @ApiModelProperty("站址信息")
    private List<SiteVO> siteList;
    @ApiModelProperty("播放流信息")
    private List<UrlVO> urlList;
}
