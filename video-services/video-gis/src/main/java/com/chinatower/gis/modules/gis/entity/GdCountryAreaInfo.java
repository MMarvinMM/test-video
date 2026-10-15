package com.chinatower.gis.modules.gis.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

@Data
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)
public class GdCountryAreaInfo {
    private String citycode;
    @JSONField(name = "adcode")
    private String code;
    private String name;
    private String polyline;
    private String center ;
    private String level;
}
