package com.chinatower.bdm.modules.layer.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 图层类别管理
 * </p>
 *
 * @author ch
 * @since 2023-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_layer_type")
@ApiModel(value="LayerType对象", description="图层类别管理")
public class LayerType {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.ASSIGN_UUID)
    private String id;

    @ApiModelProperty(value = "图层列表名称")
    private String typeName;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String modifier;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "删除状态: 原字典值：normal正常，删除deleted 现字典值：0正常，删除1")
    @TableField(fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleteFlag;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "行业编码")
    private String industryCode;

    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;

    @ApiModelProperty(value = "0-默认 1-自定义")
    private Integer category;

    @ApiModelProperty(value = "简称")
    private String abbreviation;

    @ApiModelProperty(value = "面积字段")
    private String areaRef;

    @ApiModelProperty(value = "图斑编号字段")
    private String tbbhRef;

    @ApiModelProperty(value = "父类类型名称")
    private String superTypeName;

    @ApiModelProperty(value = "祖类类型名称")
    private String initialTypeName;

    @ApiModelProperty(value = "属性显示名称（套合）")
    private String label;

    @ApiModelProperty(value = "唯一值的字段名（套合）")
    private String uniqueFieldCode;

    @ApiModelProperty(value = "覆盖分类字段code（套合）")
    private String coveredFieldCode;

    @ApiModelProperty(value = "覆盖显示的标签名（套合）")
    private String coveredLabel;

    @ApiModelProperty(value = "未覆盖显示的标签名（套合）")
    private String notCoveredLabel;

    @ApiModelProperty(value = "是否记录未填写的字段")
    private Integer addOtherFieldValuesFlag;
    private String initSql;
}
