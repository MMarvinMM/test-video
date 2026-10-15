package com.chinatower.platform.project.sysbusiness.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class DeptDeviceRefVO implements Serializable {


    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String orgId;
    private String deviceCode;
    private String devName;//设备名称
    private String creator;
    private Date creatorDate;
    private String modifier;
    private Date modifierDate;
    private String deleteFlag;
    private List<String> deviceCodeList;












}
