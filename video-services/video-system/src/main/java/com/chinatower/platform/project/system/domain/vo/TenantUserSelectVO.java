package com.chinatower.platform.project.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "租户用户列表视图")
public class TenantUserSelectVO implements Serializable {

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户ID")
    private String userId;
}
