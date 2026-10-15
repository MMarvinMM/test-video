package com.chinatower.system.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;


/**
 * 组织机构变更通知推送参数
 */
@Data
@ToString
public class OrganInfoPushVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 组织机构编码
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orgCode;//

    /**
     * 组织机构名称
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orgName;//

    /**
     * 上级机构编码
     */
    private String orgParentCode;//

    /**
     * 组织层级
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orgLevel;//

    /**
     * 01--正常；00--停用
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orgStatus;//

    /**
     * 组织机构变更事项 01-拆分；02-合并
     */
    private String orgChange;//

    /**
     * 组织机构路径 /370000/370100
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String orgPath;//

    /**
     * 创建时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String createTime;//

}
