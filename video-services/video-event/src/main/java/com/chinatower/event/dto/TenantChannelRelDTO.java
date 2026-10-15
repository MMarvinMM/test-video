package com.chinatower.event.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: TenantChannelRelDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/4 16:13
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class TenantChannelRelDTO implements Serializable {
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
     * 通道编码
     */
    private String channelCode;

    /**
     * 通道名称
     */
    private String channelName;

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

}
