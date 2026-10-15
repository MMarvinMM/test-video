package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class OrderServicesVO implements Serializable {

    private String id;
    private String customerCode;
    private String orderCode;
    private String serviceCode;
    private String serviceName;

}
