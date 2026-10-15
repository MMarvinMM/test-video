package com.chinatower.manage.api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysIndustryConfigInfoModel implements Serializable {

    /**
     * 客户编码
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 大屏id
     */
    private Long industryViewId;

    /**
     * 大屏名称
     */
    private String industryViewName;

    /**
     * 大屏打开方式：0-在原页面打开 1-在新页面打开
     */
    private Integer displayType;

    /**
     * 大屏类型 0-应用大屏 1-第三方大屏
     */
    private Integer industryViewType;

    /**
     * 大屏类型 1-旧大屏 2-新大屏
     */
    private Integer viewType;

    /**
     * 大屏别名（角色配置）
     */
    private String industryViewNameAlias;

    /**
     * 排序序号
     */
    private Integer sort;

    private String topicType;
}
