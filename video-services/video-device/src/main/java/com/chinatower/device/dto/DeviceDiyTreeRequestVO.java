package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceDiyTreeRequestVO extends BaseDataScopeEntity {
    @NotNull(message = "设备树编码不能为空")
    private String treeId;
}
