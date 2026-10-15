package com.chinatower.bdm.modules.layer.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 图层图斑管理
 * </p>
 *
 * @author ch
 * @since 2023-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_layer_polygon")
@ApiModel(value="LayerPolygon对象", description="图层图斑管理")
public class LayerPolygon  {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 图层ID
     */
    @ApiModelProperty(value = "图层ID")
    private String layerId;
    /**
     * 图斑名称
     */
    @ApiModelProperty(value = "图斑名称")
    private String patternSpotName;
    /**
     * 图斑ID
     */
    @ApiModelProperty(value = "图斑ID")
    private String featureId;
    /**
     * 图层名称
     */
    @ApiModelProperty(value = "图层名称")
    private String customName;
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
    private String geometry;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String creator;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String modifier;
    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private String tenantId;
    /**
     * 行业编码
     */
    @ApiModelProperty(value = "行业编码")
    private String industryCode;
    /**
     * 行业版本编码
     */
    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;



}
