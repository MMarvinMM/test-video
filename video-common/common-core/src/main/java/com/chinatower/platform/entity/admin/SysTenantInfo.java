package com.chinatower.platform.entity.admin;

import com.baomidou.mybatisplus.annotation.*;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 租户对象 sys_tenant_info
 * 
 * @author liuhuanhuan
 * @date 2022-01-19
 */
@Data
@ApiModel(value = "SysTenantInfo",description = "租户信息表")
@Accessors(chain = true)
@TableName(value = "sys_tenant_info")
public class SysTenantInfo extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    /**
     * 租户描述
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "租户描述")
    private String tenantDesc;

    /**
     * 行业信息
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "行业信息")
    private String tradeInfo;
    /**
     * 删除标志（0未删除 1已删除）
     */
    @ApiModelProperty(value = "删除标志（0未删除 1已删除）")
    @TableLogic
    private Integer delFlag;
    /**
     * 省份id
     */
    @ApiModelProperty(value = "省份id")
    private String provinceId;
    /**
     * 地市id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "地市id")
    private String cityId;
    /**
     * 区县id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @ApiModelProperty(value = "区县id")
    private String countyId;
    /**
     * 省份名称
     */
    @ApiModelProperty(value = "省份名称")
    @TableField(exist = false)
    private String provinceName;
    /**
     * 地市名称
     */
    @ApiModelProperty(value = "地市名称")
    @TableField(exist = false)
    private String cityName;
    /**
     * 区县名称
     */
    @ApiModelProperty(value = "区县名称")
    @TableField(exist = false)
    private String countyName;
    /**
     * 部门id
     */
    @ApiModelProperty(value = "部门id")
    @TableField(exist = false)
    private String deptId;
    /**
     * 运营研判模式 0：关闭 1：串行 2：并行
     */
    @ApiModelProperty(value = "运营研判模式 0：关闭 1：串行 2：并行")
    private String isOperation;
    /**
     * 租户是否有效订单 1：是 0：否
     */
    @ApiModelProperty(value = "租户是否有效订单 1：是 0：否")
    private String status;

    /**
     * 租户订购的行业版本
     */
    @TableField(exist = false)
    private List<SysTenantTrade> tenantTrades;

}
