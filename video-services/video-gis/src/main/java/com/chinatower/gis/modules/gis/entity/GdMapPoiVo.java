package com.chinatower.gis.modules.gis.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)
public class GdMapPoiVo {
    private String name;
    private String id;
    private String location;
    private String type;
    private String typecode;
    private String pname;
    private String cityname;
    private String adname;
    private String address;
    private String pcode;
    private String adcode;
    private String citycode;
    private String township;
}
