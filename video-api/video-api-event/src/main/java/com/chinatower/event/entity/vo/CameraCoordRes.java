package com.chinatower.event.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 提供视频视图联动功能实时视频画面中增加测高度返回参数
 * @data 2024-5-7 15:27:26
 * @author lisy
 * @version 1.0
 *
 */
@Data
@ApiModel(value = "CameraCoordRes", description = "提供视频视图联动功能实时视频画面中增加测高度返回参数")
public class CameraCoordRes {

    /**
     * 高度差值
     */
    @ApiModelProperty(value = "高度差值")
    private Double difheight;

    /**
     * 经纬度
     */
    @ApiModelProperty(value = "经纬度")
    private String coord;

    /**
     * 经纬度文本
     */
    @ApiModelProperty(value = "经纬度文本")
    private String coordtesxt;
}
