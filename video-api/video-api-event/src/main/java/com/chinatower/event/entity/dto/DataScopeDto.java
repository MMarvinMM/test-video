package com.chinatower.event.entity.dto;

import lombok.Data;

@Data
public class DataScopeDto {

    private String deviceColumn;
    private String tenantColumn;
    private String industryCode;
    private String alarmSourceColumn;
    private String alarmSourceVal;
    private String areaPro;
    private String areaCity;
    private String areaCode;
}
