package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaoheAnalysisSerialItem {
    /**
     * 统计结果名称
     * */
    private String name;
    /**
     * 显示名称
     * */
    private String lable;
    /**
     * 统计结果面积，单位平方米
     * */
    private String value;
    /**
     * 面积占输入多边形的比例
     * */
    private String radio;
}
