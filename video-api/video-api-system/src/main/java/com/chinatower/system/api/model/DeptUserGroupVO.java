package com.chinatower.system.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 应急用户表
 *
 */
@Data
public class DeptUserGroupVO implements Serializable {

    /**
     * （1：组织，2：用户）
     */
    private Integer flag;

    /**
     * 组织id/用户id
     */
    private String id;


    /**
     * 组织名称/用户名称
     */
    private String name;

    /**
     * 用户账号
     */
    private String userName;


    /**
     * 电话
     */
    private String phone;


    /**
     * 头像
     */
    private String avatar;

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
    private String parentId;

    /**
     * 子级
     */
    private List<DeptUserGroupVO> child;


    private static final long serialVersionUID = 1L;
}