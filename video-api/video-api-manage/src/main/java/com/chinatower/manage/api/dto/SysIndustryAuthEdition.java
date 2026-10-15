package com.chinatower.manage.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SysIndustryAuthEdition {

    private Long authId;
    /** 权限类型 1-菜单 2-组件 */
    private Integer authType;
    /** 组件id */
    private Long compoId;
    /** 菜单id */
    private Long menuId;
    /** 组件所属大屏id */
    private Long viewId;
    /** 客户id */
    private String tenantId;
    /** 行业编码 */
    private String industryCode;
    /** 行业应用编码 */
    private String appVerCode;
    /**
     * 属地版本号id
     */
    private Long localizedVersionId;

    /** 创建人 */
    private String creator;

    /** 创建时间 */
    private Date createTime;

    private Long localAuthId;
}
