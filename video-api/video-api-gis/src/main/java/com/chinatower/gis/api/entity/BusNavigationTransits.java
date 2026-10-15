package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @desription: 公交方案
 * @data 创建时间：2023/8/9 11:26
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class BusNavigationTransits {

    /**
     * 本条路线的总距离，单位：米
     * @required false
     */
    @ApiModelProperty(value = "本条路线的总距离，单位：米")
    private String distance;

    /**
     * 0：非夜班车；1：夜班车
     * @required false
     */
    @ApiModelProperty(value = "0：非夜班车；1：夜班车")
    private String nightflag;

    /**
     * 步行距离，单位：米
     * @required false
     */
    @ApiModelProperty(value = "步行距离，单位：米")
    private String walkingDistance;

    /**
     * 路线分段
     * @required false
     */
    @ApiModelProperty(value = "路线分段")
    private List<BusNavigationSegments> segments;
}
