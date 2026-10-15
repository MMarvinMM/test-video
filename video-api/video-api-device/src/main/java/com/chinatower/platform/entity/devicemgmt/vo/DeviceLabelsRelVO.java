package com.chinatower.platform.entity.devicemgmt.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备标签关系实体
 * @author chmod
 * @version 1.0
 * @date 2022/1/5 14:38
 */
@Data
public class DeviceLabelsRelVO implements Serializable {
    private static final long serialVersionUID = 4533252092355055002L;
    private Long id;
    private Long labelId;
    private String tenantId;
    private String deviceCode;
    private Date creatorDate;

    public DeviceLabelsRelVO() {
    }

    public DeviceLabelsRelVO(Long labelId, String tenantId, String deviceCode) {
        this.labelId = labelId;
        this.tenantId = tenantId;
        this.deviceCode = deviceCode;
    }
}
