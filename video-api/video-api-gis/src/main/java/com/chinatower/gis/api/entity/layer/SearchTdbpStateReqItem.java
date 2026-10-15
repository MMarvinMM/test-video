package com.chinatower.gis.api.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchTdbpStateReqItem {

    /**
     * 标识
     * */
    private String id;

    /**
     * 查询图形的 wkt 字符串
     * */
    private String wkt;
}
