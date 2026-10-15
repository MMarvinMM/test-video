package com.chinatower.event.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IotDeviceTree implements Serializable {

    private String deviceCode;

    private int deviceStatus;

    private String name;

    private String id;

    private List<IotDeviceTree> children;
}
