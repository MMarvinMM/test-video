package com.chinatower.gis.modules.gis.entity;

import lombok.Data;

@Data
public class GdAddressComponent {
    private String province;
    private String city;
    private String citycode;
    private String district;
    private String adcode;
    private String township;
    private String towncode;
    private GdStreetInfo streetNumber;
}
