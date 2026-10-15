package com.chinatower.platform.project.system.domain;

import java.io.Serializable;
import lombok.Data;

/**
 * 风险管理记录表
 * system_risk_management
 */
@Data
public class SystemRiskManagement implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 合规风险编号
     */
    private String riskCode;

    /**
     * 业务流程名称
     */
    private String processName;

    /**
     * 风险行为描述
     */
    private String behaviorDescription;

    /**
     * 判断方式
     */
    private String judgeMethod;

    /**
     * 风险等级:HIGH 高;MEDIUM 中;LOW 低;
     */
    private String riskLevel;

    /**
     * 风险判断结果1:是;0:否
     */
    private String isRisk;

    /**
     * 风险环节
     */
    private String apprAct;

    /**
     * 处置人账号
     */
    private String userCode;

    /**
     * 处置人姓名
     */
    private String userName;

    /**
     * 处置时间
     */
    private String scanTime;

    /**
     * 审核方式1：人工审核 2：系统判断
     */
    private String auditType;

    /**
     * 业务主键（userid-角色id）
     */
    private String businessKey;

    /**
     * 业务名称（人员名称-userid-角色名称）
     */
    private String businessName;

    /**
     * 所属省分编码
     */
    private String provinceCode;

    /**
     * 所属地市编码
     */
    private String cityCode;

    /**
     *  被调整用户名称
     */
    private String toUserName;

    /**
     *  被调整角色名称
     */
    private String toRoleName;

    private static final long serialVersionUID = 1L;
}