package com.chinatower.platform.entity.admin;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrganStreetVO implements Serializable {

    private String provinceCode;

    private String cityCode;

    private String countyCode;
}
