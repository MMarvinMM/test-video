package com.chinatower.event.api.model;

import lombok.Data;


/**
 * Copyright: Copyright(c) 2024 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: OrganInfoReq.java
 *
 * @author lyy
 * @version v1.7.00
 * @date 2024/10/23 19:20
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class OrganInfoReq {

    /**
     * 告警单ID(yyyyMM+租户id+)
     * @required true
     */
    private String warningOrderId;

    /**
     * 告警来源: 1-AI上报； 2-人工上报；3-一键告警
     */
    private String warningSource;

    /**
     * 省份编码
     */
    private String provinceId;

    /**
     * 地市编码
     */
    private String cityId;

    /**
     * 县编码
     */
    private String countyId;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 事件发现人姓名
     */
    private String findName;

    /**
     * 所属组织名称
     */
    private String orgName;

    private String tenantId;

    private String industryCode;

}
