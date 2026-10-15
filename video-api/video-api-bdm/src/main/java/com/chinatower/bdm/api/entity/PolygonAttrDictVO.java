package com.chinatower.bdm.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolygonAttrDictVO {
    /**
     * 编码
     * @required false
     */
    private String code;
    /**
     * 名称
     * @required false
     */
    private String name;
}
