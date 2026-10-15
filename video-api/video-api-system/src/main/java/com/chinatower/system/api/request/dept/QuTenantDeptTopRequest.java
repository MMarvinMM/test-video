package com.chinatower.system.api.request.dept;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @auther liuhuanhuan
 * @date 2022/2/16
 */
@Data
@ApiModel(value = "QuTenantDeptTopRequest", description = "查询租户组织顶级结构请求参数")
public class QuTenantDeptTopRequest {

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", example = "TEN100001")
    private String tenantId;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称", example = "财政局")
    private String deptName;

    /**
     * 部门状态:0正常,1停用
     */
    @ApiModelProperty(value = "部门状态(0正常,1停用)", example = "0")
    private String status;

    /**
     * 行业应用版本编码
     */
    private String appVerCode;

    /**
     * 行业编码
     */
    private String industryCode;
}
