package com.chinatower.common.core.dto.bdm;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BaseLayerListQueryDTO extends BaseDataScopeEntity {
    /**
     * 类型ID
     */
    @ApiModelProperty(value = "图层类别")
    private String layerTypeId;

    /**
     * 类型简称
     */
    @ApiModelProperty(value = "类型简称")
    private String abbreviation;

    /**
     * 图层名称
     */
    @ApiModelProperty(value = "自定义图层名称")
    private String customName;

    /**
     * 图层ID数组
     */
    private List<String> layerIds;

    /**
     * 图层ID
     */
    private String layerId;

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
}
