package com.chinatower.emergency.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 应急用户表
 * vm_emergency_user
 */
@Data
public class EmergencyGroupVO implements Serializable {

    /**
     * 特殊Id
     */
    private String specialId;

    /**
     * （1：组织，2：用户）
     */
    private Integer informationFlag;

    /**
     * 组织id/用户id
     */
    private String messageId;


    /**
     * 组织名称/用户名称
     */
    private String emessageName;


    /**
     * 通信号码
     */
    private String correspondenceNumber;

    /**
     * 号码标识（调度台：1、移动端：2、空：未配置）
     */
    private String numberMarking;

    /**
     * 上级组织id
     */
    private String emessageParentId;

    /**
     * 子级
     */
    private List<EmergencyGroupVO> child;


    /**
     * 祖级列表
     */
    private String ancestors;

    /**
     * 组织等级
     */
    private String deptLevel;


    private static final long serialVersionUID = 1L;
}