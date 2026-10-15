package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterVO {
    /**
     * 属性字段名
     * */
    private String proName;
    /**
     * 比较类型  “>” "<" "=" "like"
     * */
    private String proType;
    /**
     * 比较值
     * */
    private String proValue;
    /**
     * 比较值
     * */
    private Boolean isString;
}
