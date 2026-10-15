package com.chinatower.device.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CasCadeSyncOrgDTO implements Serializable {

    private String orgCode;

    private String orgName;

    private String parentOrgCode;
}
