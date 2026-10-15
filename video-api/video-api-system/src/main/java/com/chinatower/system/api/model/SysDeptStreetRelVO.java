package com.chinatower.system.api.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @date 2022-03-21
 */
@Data
public class SysDeptStreetRelVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * 组织机构id
     */
    @ApiModelProperty(value = "组织机构id")
    private String deptId;
    //private String orgId;

    /**
     * 省
     */
    @ApiModelProperty(value = "省")
    private String provinceCode;

    /**
     * 市
     */
    @ApiModelProperty(value = "市")
    private String cityCode;

    /**
     * 区
     */
    @ApiModelProperty(value = "区")
    private String countyCode;

    /**
     * 街道
     */
    @ApiModelProperty(value = "街道")
    private String streetAreaCode;
    //private String streetCode;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 类型
     */
    private String type;

    /**
     * 行业应用版本
     */
    @ApiModelProperty(value = "行业应用版本")
    private String appVerCode;


}
