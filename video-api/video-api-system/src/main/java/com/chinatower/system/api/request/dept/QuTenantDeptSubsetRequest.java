package com.chinatower.system.api.request.dept;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @auther liuhuanhuan
 * @date 2022/2/16
 */
@Data
@ApiModel(value = "QuTenantDeptSubsetRequest", description = "查询租户组织部门子列表(树形)请求参数")
public class QuTenantDeptSubsetRequest implements Serializable {
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", required = true, example = "TEN100001")
    private String tenantId;
    /**
     * 行业编码
     */
    @ApiModelProperty(value = "行业编码", example = "000000")
    private String industryCode;
    /**
     * 版本编码
     */
    @ApiModelProperty(value = "版本编码", example = "1.0")
    private String appVerCode;

}
