package com.chinatower.gis.modules.gis.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @desription: 图层详情
 * @data 创建时间：2023/8/10 23:43
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("layer_info")
@ApiModel(value="LayerInfo对象", description="图层管理")
public class LayerInfo {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "图层ID")
    private String layerId;

    @ApiModelProperty(value = "图层名称")
    private String layerName;

    @ApiModelProperty(value = "图层类别")
    private Integer layerType;

    @ApiModelProperty(value = "图层类别名称")
    private String layerTypeName;

    @ApiModelProperty(value = "图层类别名称简称")
    private String layerTypeSimple;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "删除状态: 原字典值：normal正常，删除deleted 现字典值：0正常，删除1")
    private Boolean deleteFlag;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "行业编码")
    private String industryCode;

    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;

    @ApiModelProperty(value = "几何类型")
    private String geoType;
}
