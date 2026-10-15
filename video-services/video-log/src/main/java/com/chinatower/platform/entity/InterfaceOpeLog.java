package com.chinatower.platform.entity;

import com.chinatower.common.entity.CommonLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class InterfaceOpeLog extends CommonLog {

    @ApiModelProperty(
            name = "操作描述"
    )
    private String operateDesc;

    @ApiModelProperty(
            name = "操作类型描述"
    )
    private String operateTypeDesc;

    @ApiModelProperty(name = "头部Authorization值")
    private String authValue;

    @ApiModelProperty(name = "get参数")
    private String queryStr;

    private String contentType;

    private String clientName;

    /**
     * 客户id
     */
    private String tenantId;

    /**
     * 客户名称
     */
    private String tenantName;

    /**
     * 请求头的客户编码
     */
    private String customerCode;
}
