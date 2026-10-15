package com.chinatower.device.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 飞行计划安全员信息
 *
 * @author
 */
@Data
public class AirPlanSafetyOfficerReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 安全员姓名
     */
    private String safetyOfficer;

    /**
     * 安全员手机号
     */
    private String safetyOfficerMobile;

}

