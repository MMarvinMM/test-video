package com.chinatower.bdm.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseIndustryBdmEntity implements Serializable {
    @ApiModelProperty("租户id")
    private String tenantId;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("应用版本编码")
    private String appVerCode;

    @ApiModelProperty("行业编码")
    private String industryCode;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String modifier;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标记")
    private Integer deleteFlag;

}
