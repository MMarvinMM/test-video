package com.chinatower.gis.modules.gis.entity;

import lombok.Data;

import java.util.List;

@Data
public class GeoData {
    private String type;
    private String id;
    private List<GeoDataFeatures> features;
}
