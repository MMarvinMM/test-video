package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaoheAnalysisItemRes {
    /**
     * 返回的geojson
     * */
    private String data;
    /**
     * 总行数
     * */
    private int allCount;
}
