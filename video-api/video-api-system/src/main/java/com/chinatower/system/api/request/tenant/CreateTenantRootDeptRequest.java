package com.chinatower.system.api.request.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @auther liuhuanhuan
 * @date 2022/2/11
 */
@Data
@ApiModel(value = "CreateTenantRootDeptRequest",description = "创建租户组织树根节点入参")
public class CreateTenantRootDeptRequest {

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id",required = true, example = "TEN10001")
    @NotBlank(message = "租户编码不能为空")
    private String tenantId;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称",required = true, example = "测试租户")
    @NotBlank(message = "租户名称不能为空")
    private String tenantName;

    /**
     * 租户所属铁塔组织编码
     */
    @ApiModelProperty(value = "租户所属铁塔组织编码",required = true, example = "110000")
    @NotBlank(message = "租户所属铁塔组织编码不能为空")
    private String tenantTowerDeptId;
}
