package com.chinatower.gis.modules.gis.entity;

import lombok.Data;

@Data
public class GeoDataFeatures {

    private String type;
    private Object geometry;
    private Object properties;
}
