package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 公交线路
 * @data 创建时间：2023/8/9 11:33
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class BusNavigationSegments {

    /**
     * 此分段中需要步行导航的信息
     * @required false
     */
    @ApiModelProperty(value = "此分段中需要步行导航的信息")
    public BusNavigationSegmentsWalking walking;

    /**
     * 此分段中需要公交导航的信息
     * @required false
     */
    @ApiModelProperty(value = "此分段中需要公交导航的信息")
    public BusNavigationSegmentsBus bus;

    /**
     * 此分段中需要火车的信息
     * @required false
     */
    @ApiModelProperty(value = "此分段中需要火车的信息")
    public String railway;

    /**
     * 此分段中需要步行导航的信息
     * @required false
     */
    @ApiModelProperty(value = "此分段中需要步行导航的信息")
    public BusNavigationSegmentsTaxi taxi;
}
