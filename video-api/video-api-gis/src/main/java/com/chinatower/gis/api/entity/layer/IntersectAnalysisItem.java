package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntersectAnalysisItem extends TaoheAnalysisLayerItem {
    /**
     * 图层面积
     * */
    private String layerArea;
    /**
     * 相交面积
     * */
    private String intersectArea;
    /**
     * 输入图形面积
     * */
    private String inputArea;
    /**
     * 相交面积占图层面积的比例
     * */
    private String radio;
    private String layerId;

}
