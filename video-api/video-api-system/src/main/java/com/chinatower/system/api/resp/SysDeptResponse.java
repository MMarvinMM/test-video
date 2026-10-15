package com.chinatower.system.api.resp;

import lombok.Data;

import java.util.List;

/**
 * 部门表返回对象 SysDeptResponse 去除不必要字段,减少网络流量传输
 *
 * @author bruce
 * @Date 2021-11-18
 */
@Data
public class SysDeptResponse {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 父部门ID
     */
    private String parentId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 租户ID
     */
    private String tenantId;


    /**
     * 部门等级
     */
    private String deptLevel;

    /**
     * 子部门
     */
    private List<SysDeptResponse> children;
}
