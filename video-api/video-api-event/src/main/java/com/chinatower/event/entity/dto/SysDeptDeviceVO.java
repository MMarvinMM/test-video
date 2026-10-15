package com.chinatower.event.entity.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "组织设备视图")
public class SysDeptDeviceVO implements Serializable {

    private String deviceName;

    private String deviceCode;

    private String deviceType;

    private String provinceId;

    private String  cityId;

    private String townCode;

    private String countyId;

}
