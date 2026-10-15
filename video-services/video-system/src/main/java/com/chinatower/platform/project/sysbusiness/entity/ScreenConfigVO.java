package com.chinatower.platform.project.sysbusiness.entity;

import lombok.Data;

@Data
public class ScreenConfigVO {
    /**
     * 大屏配置ID
     */
    private Long screenDataConfigId;

    /**
     * 数据类型 9-告警类型
     */
    private String dataType;

    /**
     * 数据编码（告警类型编码）
     */
    private String dataCode;

    /**
     * 告警来源
     */
    private String dataSource;

    /**
     * 大屏ID
     */
    private String screenId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 应用版本编码
     */
    private String appVerCode;
}
