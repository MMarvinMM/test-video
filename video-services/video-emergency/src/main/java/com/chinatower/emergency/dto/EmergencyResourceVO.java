package com.chinatower.emergency.dto;

import com.chinatower.bdm.api.entity.TreeSelectByBdm;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 功能说明：应急资源表 与vm_emergency_resource_info 表对应。
 *
 * @author liuxt
 * @date 2024/10/28
 * @requestID
 */
@Data
public class EmergencyResourceVO extends BaseDataScopeEntity implements Serializable {


    /**
     * 应急事件编码
     */
    private String eventCode;

    /**
     * 阶段编码
     */
    private String stageId;

    /**
     * 任务编码
     */
    private String taskId;

    private String id;
    private String label;
    private String pid;
    private List<TreeSelectByBdm> children;
    private String icon;
    private String type;
    /**
     * 资源属性
     */
    private String resourceProperty;

    private Integer isMonitor;

    private List<String> resourceIdList;

    private String resourceId;

    /**
     * 0-收藏
     * 1-全部
     */
    private String isMonitorFlag;

    private List<String> eventCodes;

}
