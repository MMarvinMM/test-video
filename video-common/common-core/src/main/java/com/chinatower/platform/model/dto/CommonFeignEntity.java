package com.chinatower.platform.model.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by zhujun on 2022/3/24.
 */
@Data
public class CommonFeignEntity extends BaseDataScopeEntity {

    /**
     * 行业版本编码
     */
    @ApiModelProperty(value = "行业版本编码")
//    @NotNull(message = "行业版本编码不能为空")
    private String appVerCode;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
//    @NotNull(message = "用户ID不能为空")
    private String userId;
    /**
     * 页码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageNum;
    /**
     * 页长
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageSize;


    /**
     * 资源属性
     */
    private String resourceProperty;
}
