package com.chinatower.system.api.resp.tenant;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @desription: 租户基本信息
 * @data 创建时间：2023/8/22 9:14
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@ApiModel(value = "租户基本信息")
public class SysTenantInfoBaseResponse implements Serializable {
    /**
     * 数据ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;
}
