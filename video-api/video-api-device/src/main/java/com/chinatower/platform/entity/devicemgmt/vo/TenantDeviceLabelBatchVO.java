package com.chinatower.platform.entity.devicemgmt.vo;

import lombok.Data;

import java.util.List;

@Data
public class TenantDeviceLabelBatchVO {

    private String tenantId;
    private String deviceCode;
    List<Long> labelList;
}
