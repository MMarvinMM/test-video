package com.chinatower.manage.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SysIndustryViewRelEdition {

    /** 主键ID */
    private Long id;
    /** 大屏id */
    private Long industryViewId;
    /**
     * 大屏类型 1-旧大屏 2-新大屏
     */
    private Integer industryViewType;
    /** 大屏别名 */
    private String viewName;
    /** 行业编码 */
    private String industryCode;
    /** 应用版本编码 */
    private String appVerCode;
    /** 目录id */
    private String pageId;
    /** 大屏排序 */
    private String sort;
    /**
     * 展示类型 0-系统内展示 1-跳转外部地址
     */
    private Integer displayType;

    /**
     * 大屏主题风格 wiseblue-睿智蓝 aquamarine-碧山绿 terracotta-赤土黄
     */
    private String topicType;

    /**
     * 属地版本号id
     */
    private Long localizedVersionId;

    private String isSelect;

    /** 创建人 */
    private String creator;

    /** 创建时间 */
    private Date createTime;

}
