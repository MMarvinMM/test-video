package com.chinatower.gis.modules.gis.entity;

import lombok.Data;

@Data
public class AllMapCodeToAddressReturn {
    private String formattedAddress;
    private String province;
    private String city;
    private String district;
    private String township;
    private String street;
    private String lat;
    private String lon;

    public AllMapCodeToAddressReturn(String formattedAddress, String province, String city, String district, String township, String street, String lat, String lon) {
        this.formattedAddress = formattedAddress;
        this.province = province;
        this.city = city;
        this.district = district;
        this.township = township;
        this.street = street;
        this.lat = lat;
        this.lon = lon;
    }

    public AllMapCodeToAddressReturn() {
    }

    public AllMapCodeToAddressReturn(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
