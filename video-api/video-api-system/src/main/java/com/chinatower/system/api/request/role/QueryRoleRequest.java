package com.chinatower.system.api.request.role;

import com.chinatower.common.core.dto.PageDTO;
import com.chinatower.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @auther liuhuanhuan
 * @date 2022/2/15
 */
@Data
@ApiModel(value = "QueryRoleRequest", description = "查询角色信息请求参数")
public class QueryRoleRequest extends PageDTO {

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称", example = "xxx")
    private String roleName;

    /**
     * 行业应用编码
     */
    @ApiModelProperty(value = "行业应用编码", example = "xxx")
    private String tradeInfo;

    @ApiModelProperty(value = "行业应用版本编码", example = "xxx")
    private String appVerCode;

    @ApiModelProperty(value = "客户名称/客户ID", example = "xxx")
    private String tenantSearch;
    /**
     * 角色权限
     */
    @ApiModelProperty(value = "角色权限", example = "admin")
    private String roleKey;

    //角色类型:0=铁塔角色,1=公共角色,2=租户角色
    @ApiModelProperty(value = "角色类型(0=铁塔角色,1=公共角色,2=租户角色)", example = "0")
    private String roleType;

    @ApiModelProperty(value = "所属租户编码", example = "Ten100001")
    private String tenantId;
    /**
     * 角色状态（0正常 1停用）
     */
    @ApiModelProperty(value = "角色状态（0正常 1停用）", example = "0")
    private String status;

    @ApiModelProperty(value = "行业编码")
    private String industryCode;

    public String getDefaultOrderBy() {
        String orderBy = getOrderBy();
        if (StringUtils.isBlank(orderBy)) {
            orderBy = " role_sort + 0  desc";
        }
        return orderBy;
    }

}
