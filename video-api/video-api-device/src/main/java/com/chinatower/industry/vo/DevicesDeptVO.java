package com.chinatower.industry.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DevicesDeptVO implements Serializable {

    //摄像机list
    private List<DevicesInfoVO> devicesList;

    //组织编码
    private String deptId;
}
