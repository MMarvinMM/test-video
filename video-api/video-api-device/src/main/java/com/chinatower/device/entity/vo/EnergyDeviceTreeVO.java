package com.chinatower.device.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class EnergyDeviceTreeVO {

    private String code;
    private String name;
    private String status;
    private Integer level;
    private Integer deviceNum = 0;
    private Integer onlineNum = 0;
    private List<EnergyDeviceTreeVO> list;
}
