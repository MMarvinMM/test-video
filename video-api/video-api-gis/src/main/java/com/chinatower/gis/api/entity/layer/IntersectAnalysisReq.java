package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntersectAnalysisReq extends TaoheAnalysisAllReq {
    /**
     * 分析的几何列表，
     * */
    private List<InputGeoItem> inputGeoItems;

    /**
     * 分析的图层列表，
     * */
    private List<TaoheAnalysisLayerItem> layerItems;
}
