package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaoheAnalysisDefaultLayersRes {
    /**
     * 五种默认套盒图层
     * */
    private ArrayList<TaoheAnalysisLayerItem> layerItems;
}
