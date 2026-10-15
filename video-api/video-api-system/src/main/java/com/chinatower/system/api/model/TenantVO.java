package com.chinatower.system.api.model;

import lombok.Data;
import lombok.ToString;

/**
 * @program: VideoPlatformGit
 * @description:
 * @author: hued@strongdata.com.cn
 * @create: 2021/05/17 15:48
 */
@Data
@ToString
public class TenantVO {
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 租户管理
     */
    private String tenantManager;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 省
     */
    private String provinceId;
    /**
     * 城市
     */
    private String cityId;
    /**
     * 区县
     */
    private String countyId;


}
