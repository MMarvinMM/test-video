package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetInOutBalanceRes {
    /**
     * 耕地流出面积中水田的面积
     * */
    private String lc0101;
    /**
     * 耕地流出面积中水浇地的面积
     * */
    private String lc0102;
    /**
     * 耕地流出面积中旱地的面积
     * */
    private String lc0103;
    /**
     * 耕地流入面积中水田的面积
     * */
    private String lr0101;
    /**
     * 耕地流入面积中水浇地的面积
     * */
    private String lr0102;
    /**
     * 耕地流入面积中旱地的面积
     * */
    private String lr0103;
}
