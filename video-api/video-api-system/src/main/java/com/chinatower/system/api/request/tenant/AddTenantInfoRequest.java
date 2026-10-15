package com.chinatower.system.api.request.tenant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @auther liuhuanhuan
 * @date 2022/2/15
 */
@Data
@ApiModel(value = "AddTenantInfoRequest", description = "租户新增请求参数")
public class AddTenantInfoRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", required = true, example = "TEN10001")
    @NotBlank(message = "租户编码不能为空")
    private String tenantId;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称", required = true, example = "测试租户名称")
    @NotBlank(message = "租户名称不能为空")
    private String tenantName;

    /**
     * 租户描述
     */
    @ApiModelProperty(value = "租户描述", example = "xxx")
    private String tenantDesc;

    /**
     * 行业信息
     */
    @ApiModelProperty(value = "行业信息", example = "1,2")
    private String tradeInfo;

    /**
     * 删除标志（0未删除 1已删除）
     */
    @ApiModelProperty(value = "删除标志（0未删除 1已删除）", example = "0")
    private Integer delFlag;

    /**
     * 省份id
     */
    @ApiModelProperty(value = "省份id", required = true, example = "110000")
    @NotBlank(message = "省份不能为空")
    private String provinceId;

    /**
     * 地市id
     */
    @ApiModelProperty(value = "地市id", example = "110100")
    private String cityId;

    /**
     * 区县id
     */
    @ApiModelProperty(value = "区县id", example = "110101")
    private String countyId;

    /**
     * 行业集合
     */
    @ApiModelProperty(value = "行业集合")
    private List<TenantTradeRequest> tenantTrades;

    /**
     * 部门编码
     */
    @ApiModelProperty(value = "部门编码")
    private String deptId;
}
