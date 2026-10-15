package com.chinatower.event.entity.dto;

import com.chinatower.common.core.dto.BaseModel;
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
public class AlarmFlowConfigModel extends BaseModel implements Serializable {
    private static final long serialVersionUID = -6966106571145117171L;
    /** 主键 */
    private Long id;
    /** 租户ID */
    private String tenantId;
    /** '事件来源' */
    private String warningSource;
    /** 行业应用版本 */
    private String appVerCode;
    /** 事件来源集合 */
    private List<String> warningSourceList;
    /** '告警编码' */
    private String alarmCode;
    /** 设备编码 */
    private String deviceCode;
    /** 流程模板ID */
    private Long flowId;
    /** 环节ID */
    private Long linkId;
    /** 1生效 0失效 */
    private String status;
    /** 组织ID*/
    private String orgId;
    /** 角色ID */
    private Long roleId;
    /** 判断是否为拦截告警 */
    private Long intercept;
    /** 流程属性集合 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FlowLinkAttrModel> data;
    /** 行业编码 */
    private String industryCode;
    /** 模板id */
    private Long templateId;
    /** 环节集合 */
    private List<Long> linkList;
    /** 环节类型 */
    private Long realLinkType;
    /** 环节别名 */
    private String linkAlias;
    /** 转派标识 */
    private String turnFlag;

    /**
     * 是否有网格员标识（空：没有 1：有）
     */
    private String gridFlag;

}
