package com.chinatower.platform.res;

import lombok.Data;

/**
 * @desription: 客户登录次数排行
 * @data 创建时间：2023/3/29 15:22
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class TenantLoginCountRes {
    /**
     * 租户id
     */
    private String tenantId;
    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 登录次数
     */
    private Long count;
}
