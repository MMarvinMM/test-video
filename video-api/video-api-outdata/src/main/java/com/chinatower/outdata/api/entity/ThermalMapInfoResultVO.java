package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ThermalMapInfoResultVO implements Serializable {

    private String code;

    private String msg;

    private List<ThermalMapInfo> data;
}
