package com.chinatower.gis.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @desription: 步行导航
 * @data 创建时间：2023/8/9 10:44
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PedestrianNavigationRes {

    /**
    * 起点经纬度
    * @required false
    */
    @ApiModelProperty(value = "起点经纬度")
    private String origin;

    /**
     * 终点经纬度
     * @required false
     */
    @ApiModelProperty(value = "终点经纬度")
    private String destination;

    /**
     * 算路方案详情
     * @required true
     */
    @ApiModelProperty(value = "算路方案详情")
    private List<DriveNavigationPaths> paths;
}
