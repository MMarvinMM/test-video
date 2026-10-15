package com.chinatower.airline.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 飞行计划安全员信息（vm_air_plan_safety_officer_t）
 *
 * @author
 */
@Data
public class AirPlanSafetyOfficer implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 安全员姓名
     */
    private String safetyOfficer;

    /**
     * 安全员手机号
     */
    private String safetyOfficerMobile;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 删除标记位 1已删除 0未删除
     */
    private String deleteFlag;
}

