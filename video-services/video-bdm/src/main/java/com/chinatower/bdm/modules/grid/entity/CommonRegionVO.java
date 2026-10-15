package com.chinatower.bdm.modules.grid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonRegionVO implements Serializable {
    private long id;

    private String longitude;

    private String latitude;

    private String provinceCode;

    private String provinceName;

    private String cityCode;

    private String cityName;

    private String countyCode;

    private String countyName;

    private String townCode;

    private String townName;


}


