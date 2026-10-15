package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceDiyTreeNodeVO extends BaseDataScopeEntity {
    private String treeId;

    //节点类型 1文本 2设备
    private String nodeType;

    private String nodeCode;

    private String nodeValue;

    private int nodeLevel;

    private String parentNodeCode;

    private String deviceCode;

    private String channelCode;
}
