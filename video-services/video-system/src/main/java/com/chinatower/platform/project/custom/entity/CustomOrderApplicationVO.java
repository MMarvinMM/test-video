package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


@Data
@ToString
public class CustomOrderApplicationVO implements Serializable {

    private String customerCode;
    private String packageCode;
    private String orderCode;
    private String appCode;
    private String productName;
    private String productCode;
    private String capabilitiesStr;

}
