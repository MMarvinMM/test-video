package com.chinatower.gis.modules.gis.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

/**
 * @desription: 套合分析的有效图层
 *
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaoheLayerInfo {
    private String layerName;
    private String layerArea;
    private String layerId;
    private String areaField;
    private String layerTypeSimple;
    private String layerCustomName;
}
