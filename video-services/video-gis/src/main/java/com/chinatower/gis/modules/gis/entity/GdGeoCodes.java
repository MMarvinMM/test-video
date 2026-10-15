package com.chinatower.gis.modules.gis.entity;

import lombok.Data;

@Data
public class GdGeoCodes {
    private String formatted_address;
    private String country;
    private String province;
    private String city;
    private String citycode ;
    private String district;
    private String street;
    private String number;
    private String adcode;
    private String location;
    private String level;
}
