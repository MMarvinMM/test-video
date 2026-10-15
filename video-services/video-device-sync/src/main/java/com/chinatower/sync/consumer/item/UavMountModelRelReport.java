package com.chinatower.sync.consumer.item;

import lombok.Data;

import java.io.Serializable;

/**
 * 运管同步device_uav_mount_model 实体
 */
@Data
public class UavMountModelRelReport extends CommonYgReport implements Serializable {

    private Long id;

    private Long deviceId;

    private String deviceCode;

    private String mountDictCode;

    private String mountSn;
}
