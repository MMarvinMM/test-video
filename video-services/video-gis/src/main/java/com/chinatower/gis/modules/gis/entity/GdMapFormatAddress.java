package com.chinatower.gis.modules.gis.entity;

import lombok.Data;

@Data
public class GdMapFormatAddress {
    private String formatted_address;
    private GdAddressComponent addressComponent;
}
