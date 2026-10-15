package com.chinatower.common.core.dto.bdm;

import com.chinatower.common.core.web.page.PageDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @desription: 图斑查询
 * @data 创建时间：2023/4/10 9:12
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class LayerPolygonQueryDTO extends PageDomain {

    /**
     * 图斑数据ID
     */
    @ApiModelProperty(value = "图斑数据ID")
    private String layerPolygonId;

    /**
     * 图斑ID
     */
    @ApiModelProperty(value = "图斑ID")
    private String featureId;

    /**
     * 图层ID
     */
    @ApiModelProperty(value = "图层ID")
    private String layerId;

    @ApiModelProperty(value = "租户id")
    private String tenantId;

    @ApiModelProperty(value = "行业编码")
    private String industryCode;
}
