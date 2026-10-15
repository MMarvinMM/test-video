package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class CustomOrderDevicesVO implements Serializable {

    private String customerCode;
    private String orderCode;
    private String packageCode;
    private String deviceCode;
    private String devName;
    private String siteName;
    private String location;

}
