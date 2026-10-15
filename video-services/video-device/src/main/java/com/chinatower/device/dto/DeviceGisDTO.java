package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DeviceGisDTO {

    @JsonProperty("formattedAddress")
    private String formattedAddress;
    @JsonProperty("province")
    private String province;
    @JsonProperty("city")
    private String city;
    @JsonProperty("adcode")
    private String adcode;
    @JsonProperty("cityCode")
    private String cityCode;
    @JsonProperty("provinceCode")
    private String provinceCode;
    @JsonProperty("towncode")
    private String towncode;
    @JsonProperty("district")
    private String district;
    @JsonProperty("township")
    private String township;
}
