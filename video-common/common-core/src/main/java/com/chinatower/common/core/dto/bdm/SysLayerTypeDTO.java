package com.chinatower.common.core.dto.bdm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @desription: 图层类型
 * @data 创建时间：2024/1/22 9:19
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class SysLayerTypeDTO {


    private String id;

    @ApiModelProperty(value = "图层列表名称")
    private String typeName;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "行业编码")
    private String industryCode;

    @ApiModelProperty(value = "行业版本编码")
    private String appVerCode;

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
}
