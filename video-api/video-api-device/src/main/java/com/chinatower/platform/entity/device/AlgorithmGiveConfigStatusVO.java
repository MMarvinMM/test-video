package com.chinatower.platform.entity.device;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AlgorithmGiveConfigStatusVO extends BaseDataScopeEntity {

    @NotNull(message = "主键不能为空")
    @ApiModelProperty(value = "主键", required = true)
    private int id;

    @NotNull(message = "前端算法状态不能为空")
    @ApiModelProperty(value = "算法任务状态：0:启用 1:停用", required = true)
    private String status;
}
