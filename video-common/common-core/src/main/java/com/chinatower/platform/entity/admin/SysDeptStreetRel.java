package com.chinatower.platform.entity.admin;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 组织行政区域权限表
 */
@Data
public class SysDeptStreetRel extends BaseDataScopeEntity {
    private static final long serialVersionUID = 1L;
    /** 租户id */
    @ApiModelProperty(value = "租户id")
    private String tenantId;
    /** 组织机构id */
    @ApiModelProperty(value = "组织机构id")
    //private String deptId;
    private String orgId;
    /** 省 */
    @ApiModelProperty(value = "省")
    private String provinceCode;
    /** 市 */
    @ApiModelProperty(value = "市")
    private String cityCode;
    /** 区 */
    @ApiModelProperty(value = "区")
    private String countyCode;
    /** 街道 */
    @ApiModelProperty(value = "街道")
    //private String streetAreaCode;
    private String streetCode;
    /** 行业应用版本 */
    @ApiModelProperty(value = "行业应用版本")
    private String appVerCode;

    //private String type;


}
