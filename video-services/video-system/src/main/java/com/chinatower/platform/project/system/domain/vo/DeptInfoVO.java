package com.chinatower.platform.project.system.domain.vo;


import lombok.Data;


@Data
public class DeptInfoVO {

    /**
     * 组织id
     */
    private String deptId;

    /**
     * 父级组织id
     */
    private String parentId;

    /**
     * 部门等级
     */
    private String deptLevel;

}
