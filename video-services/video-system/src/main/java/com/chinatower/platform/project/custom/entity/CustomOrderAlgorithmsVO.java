package com.chinatower.platform.project.custom.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class CustomOrderAlgorithmsVO implements Serializable {

    private String id;
    private String customerCode;
    private String orderCode;
    private String packageCode;
    private String algorithmCode;
    private String algorithmName;

}
