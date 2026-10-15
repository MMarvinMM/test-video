package com.chinatower.outdata.api.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RelayDeviceResultVO implements Serializable {

    private String code;
    private String msg;
    private RelayDeviceInfo data;
}
