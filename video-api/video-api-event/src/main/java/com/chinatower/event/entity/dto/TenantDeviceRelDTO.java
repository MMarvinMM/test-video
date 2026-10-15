package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TenantDeviceRelDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/4 16:09
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class TenantDeviceRelDTO implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 街道行政区域编码
     */
    private String townCode;

    /**
     * 街道行政区域名称
     */
    private String townName;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 创建时间
     */
    private Date creatorDate;

    /**
     * 修改时间
     */
    private Date modifierDate;

    /**
     * 字典值：1正常 0删除
     */
    private String deleteFlag;

    /**
     * 标签ID
     */
    private String labels;

    /**
     * 通道编码
     */
    private String channelCode;

    private List<String> deviceCodes;
    private List<String> channelCodes;



}
