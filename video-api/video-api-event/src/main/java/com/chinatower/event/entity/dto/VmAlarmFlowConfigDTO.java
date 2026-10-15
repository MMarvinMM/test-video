package com.chinatower.event.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author lyy
 * @version 1.0
 * @date 2022/7/20 10:20
 */
@Data
@ApiModel(description = "告警流程配置查询入参")
public class VmAlarmFlowConfigDTO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -6966106571145117171L;
    /**
     *  主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 告警流程配置名称
     */
    private String alarmFlowName;
    /**
     * 流程名称
     */
    private String flowName;
    /**
     * 自定义租户流程ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long templateId;
    /**
     * 状态：1-生效，0-失效
     */
    private String status;

    /**
     * 告警来源
     */
    private String warningSource;
    /**
     * 告警类型
     */
    private String alarmType;

    /**
     * 告警来源+告警类型集合
     */
    private List<Map<String, String>> alarmSourceAndTypeList;
    private List<String> sourceAndTypeList;

    /**
     * 流程模板关联告警类型集合
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<VmAlarmFlowTemplateTypeDO> flowTemplateTypeList;

    /**
     * 流程模板ID集合 (v1.6.0 提供给app)
     */
    private List<String> templateIdList;

    /**
     * 提供给APP, 模糊搜索(告警流程配置名称、创建人姓名)
     */
    private String searchKeyword;

    // 用户ID集合
    private List<String> userIdList;

    //add v1.6.00 APP告警流程配置需求 创建时间
    private String createTimeStart;
    private String createTimeEnd;

    /** 创建时间排序 */
    private String createDateOrder;
    /** 修改时间排序 */
    private String updateDateOrder;
    /** sql排序 */
    private String orderSql;

    /**
     * 分页参数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageNum;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageSize;
}
