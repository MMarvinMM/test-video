package com.chinatower.common.core.dto.bdm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @desription: 新增图斑实体
 * @data 创建时间：2023/4/10 9:21
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerPolygonAddDTO {
    /**
     * 图层ID
     */
    @ApiModelProperty(value = "图层ID")
    @NotBlank(message = "图层不能为空")
    private String layerId;

    /**
     * 图斑名称
     */
    @ApiModelProperty(value = "图斑名称")
    @NotBlank(message = "图斑名称不能为空")
    private String patternSpotName;

    /**
     * 图斑ID
     */
    @ApiModelProperty(value = "图斑ID")
    @NotBlank(message = "图斑ID名称不能为空")
    private String featureId;

    /**
     * 面和/平方米
     */
    @ApiModelProperty(value = "面和/平方米")
    private String spotArea;

    /**
     * 图斑元数据
     */
    @ApiModelProperty(value = "图斑元数据")
    private String properties;

    /**
     * GeoJson字符串
     */
    @ApiModelProperty(value = "GeoJson字符串")
    @NotBlank(message = "GeoJson字符串名称不能为空")
    private String geometry;
}
