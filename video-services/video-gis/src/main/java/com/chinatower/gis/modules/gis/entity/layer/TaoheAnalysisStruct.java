package com.chinatower.gis.modules.gis.entity.layer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaoheAnalysisStruct {
    private String type;
    private String tmpName;
    private double overArea;
    private String sourceTableName;
}
