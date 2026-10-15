package com.chinatower.platform.res;

import lombok.Data;

/**
 * @desription: 租户成功率排行
 * @data 创建时间：2023/3/29 16:04
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class TenantSuccessRatioRes {
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
    private String scale;
}
