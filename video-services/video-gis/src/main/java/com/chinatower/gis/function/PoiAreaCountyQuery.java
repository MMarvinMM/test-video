package com.chinatower.gis.function;

import com.chinatower.gis.api.entity.map.District;

@FunctionalInterface
public interface PoiAreaCountyQuery {
    void nextPolygon(District district);
}
