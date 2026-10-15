package com.chinatower.outdata.api.entity;
import lombok.Data;

import java.io.Serializable;

@Data
public class MonitorFakeDeptSubVO implements Serializable {

    private String deviceCode;
    private String deptId;
    private String deptName;
}
