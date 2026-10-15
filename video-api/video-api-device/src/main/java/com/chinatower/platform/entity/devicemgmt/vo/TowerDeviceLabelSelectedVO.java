package com.chinatower.platform.entity.devicemgmt.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TowerDeviceLabelSelectedVO implements Serializable {

    private Long labelTypeId;

    private String labelTypeName;

    private Integer labelCount;

    private List<TowerDeviceLabelSelectedSubVO> labelInfoList;
}
