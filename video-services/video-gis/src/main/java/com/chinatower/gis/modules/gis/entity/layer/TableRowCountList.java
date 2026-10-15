package com.chinatower.gis.modules.gis.entity.layer;

import com.chinatower.gis.modules.gis.entity.layer.TableRowCount;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableRowCountList {
    private List<TableRowCount> rows;

    public int CountByTableName(String name) {
        for (TableRowCount row : rows) {
            if (row.getTableName().equals(name)) {
                return row.getRowCount();
            }
        }
        return 0;
    }
}

