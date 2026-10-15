package com.chinatower.common.core.dto.bdm;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @className: VmLayerTempConfigVO
 * @description: 图层模板配置 bean
 * @author: lyy
 * @date: 2024/04/29 14:47
 * @version v1.6.60
 */
@Data
public class LayerTempConfigVO extends BaseIndustryEntity {

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "图层模板名称")
    private String layerTempName;

    @ApiModelProperty(value = "告警来源名称")
    private String warningSourceName;

    @ApiModelProperty(value = "告警类型")
    private String alarmType;

    @ApiModelProperty(value = "告警类型名称")
    private String alarmTypeName;

    @ApiModelProperty(value = "0-默认 1-自定义")
    private Integer category;

    @ApiModelProperty(value = "简称")
    private String abbreviation;

    @ApiModelProperty(value = "父类类型名称")
    private String superTypeName;

    @ApiModelProperty(value = "祖类类型名称")
    private String initialTypeName;

    @ApiModelProperty(value = "面积字段")
    private String areaRef;

    @ApiModelProperty(value = "图斑编号字段")
    private String tbbhRef;

}
