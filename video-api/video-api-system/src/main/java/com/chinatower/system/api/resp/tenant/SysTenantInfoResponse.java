package com.chinatower.system.api.resp.tenant;

import com.chinatower.platform.model.resp.BaseResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 租户列表返回参数
 *
 * @auther liuhuanhuan
 * @date 2022/2/10
 */
@Data
@ApiModel(value = "SysTenantInfoResponse",description = "租户列表返回参数")
public class SysTenantInfoResponse extends BaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    /**
     * 租户描述
     */
    @ApiModelProperty(value = "租户描述")
    private String tenantDesc;

    /**
     * 行业信息
     */
    @ApiModelProperty(value = "行业信息")
    private String tradeInfo;

    /**
     * 删除标志（0未删除 1已删除）
     */
    @ApiModelProperty(value = "删除标志（0未删除 1已删除）")
    private Integer delFlag;

    /**
     * 省份id
     */
    @ApiModelProperty(value = "省份id")
    private String provinceId;

    /**
     * 地市id
     */
    @ApiModelProperty(value = "地市id")
    private String cityId;

    /**
     * 省份名称
     */
    @ApiModelProperty(value = "省份名称")
    private String provinceName;

    /**
     * 地市名称
     */
    @ApiModelProperty(value = "地市名称")
    private String cityName;

    /**
     * 区县id
     */
    @ApiModelProperty(value = "区县id")
    private String countyId;

    /**
     * 区县名称
     */
    @ApiModelProperty(value = "区县id")
    private String countyName;

    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id")
    private String deptId;
}
