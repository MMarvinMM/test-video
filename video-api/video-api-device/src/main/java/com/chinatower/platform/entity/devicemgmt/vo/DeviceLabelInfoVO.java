package com.chinatower.platform.entity.devicemgmt.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceLabelInfoVO implements Serializable {

    private String deviceCode;

    private List<DeviceLabelSubVO> labelInfoList;
}
