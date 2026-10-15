package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceDiyTreeVO extends BaseDataScopeEntity {
    private Long id;
    private String treeId;
    @NotNull(message = "设备树名称不能为空")
    private String treeName;

    /**
     * 模板类型 1行政区划 2组织 3标签 4自定义
     */
    private String templateType;

    /**
     * 状态 0启用  1停用
     */
    private String treeStatus;

    @NotEmpty(message ="设备树节点集合不能为空")
    private List<DeviceDiyTreeNodeVO> nodeList;
}
