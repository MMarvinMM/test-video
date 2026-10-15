package com.chinatower.platform.consumer.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 单期遥感解析;
 * @date : 2026-3-4
 */
@ApiModel(value = "单期遥感解析",description = "")
@Data
public class EpochalGeoVo {
    /** 主键 */
    @ApiModelProperty(name = "主键",notes = "")
    private Long id ;
    /** 任务名称 */
    @ApiModelProperty(name = "任务名称",notes = "")
    @NotNull
    private String taskName ;
    /** 任务状态;0 创建任务 1 执行中 2 执行成功 3 执行失败 */
    @ApiModelProperty(name = "任务状态",notes = "0 创建任务 1 执行中 2 执行成功 3 执行失败")
    private Integer taskStatus ;
    /** 算法类型编码 */
    @ApiModelProperty(name = "算法类型编码",notes = "")
    private String algorithmCode ;
    /** 图层ID */
    @ApiModelProperty(name = "图层ID",notes = "")
    private String layerId ;
    @ApiModelProperty(name = "原始文件地址",notes = "")
    private String originUrl;
    @ApiModelProperty(name = "对比图层ID",notes = "")
    private String baseLineLayerId ;
    @ApiModelProperty(name = "对比原始文件地址",notes = "")
    private String baseLineOriginUrl;
    /** 失败原因 */
    @ApiModelProperty(name = "失败原因",notes = "")
    private String reason ;

}