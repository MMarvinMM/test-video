package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetXlsjFeatureRes {
    /**
     * 查询结果GUID
     * */
    private String bsm;
    /**
     * 图斑名称
     * */
    private String tbbh;
    /**
     * 图斑名称
     * */
    private double area;
}