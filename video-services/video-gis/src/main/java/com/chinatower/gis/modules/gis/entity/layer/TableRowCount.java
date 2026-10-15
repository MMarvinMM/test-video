package com.chinatower.gis.modules.gis.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableRowCount {
    /**
     * 表名
     * */
    private String tableName;
    /**
     * 记录数
     * */
    private int rowCount;
}