package com.chinatower.event.api.model;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/27 9:37
 */
@Data
public class ApiAlarmFlowConfigModel extends CommonFeignEntity implements Serializable {
    private static final long serialVersionUID = -6966106571145117171L;
    /** 主键 */
    private Long alarmFlowId;
    /** 租户ID */
    private String tenantId;
    /**
     * 事件来源
     * @required false
     */
    private String warningSource;
    /** 事件来源集合 */
    private List<String> warningSourceList;
    /** '告警编码' */
    private String alarmCode;
    /** 设备编码 */
    private String deviceCode;
    /**
     * 流程模板ID
     * @required true
     */
    private Long flowId;
    /**
     * 环节ID
     * @required true
     */
    private Long linkId;
    /** 环节属性ID */
    //private Long flowLinkId;
    /** 1生效 0失效 */
    private String status;
    /** 组织ID*/
    private String orgId;
    /** 角色ID */
    private Long roleId;
    /**
     * 模板ID
     * @required true
     */
    private Long templateId;
    /** 流程属性集合 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ApiFlowLinkAttrModel> data;
}
