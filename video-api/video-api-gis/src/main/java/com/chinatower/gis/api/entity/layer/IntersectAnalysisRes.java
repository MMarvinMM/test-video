package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntersectAnalysisRes {
    /**
     * 五种类型统计分析结果
     * */
    private ArrayList<IntersectAnalysisItem> items;
}
