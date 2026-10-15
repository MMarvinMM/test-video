package com.chinatower.gis.modules.gis.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GdMapReturn {
    private String status;
    private String info;
    private List<GdMapPoiVo> pois;
    private String count;
    private GdMapFormatAddress regeocodes;
    private GdGeoCodes geocodes;
    private GdWeatherInfo lives;
    private String locations;
    private List<GdCountryAreaInfo> districts;
}
