package com.chinatower.event.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/2/19 16:22
 */
@Data
public class SysInfo implements Serializable {
    private static final long serialVersionUID = -6502540918082138673L;

    private String userId;
    private String userName;
    private String nickName;
    private Long roleId;
    private String roleName;
    private String deptId;
    /** 父级组织ID */
    private String parentId;
    private String deptName;

    /** 节点ID、节点名称(前端展示树形使用) */
    private String nodeId;
    private String nodeName;
    /**
     * 头像地址
     */
    private String avatar;
    private String purviewFlag;
    /**
     * 是否租户管理员(0否,1是)
     */
    private String tenantAdmin;

    private List<SysInfo> children;

    private String status;

    private String phonenumber;

    public SysInfo(String userId, String userName, String deptId) {
        this.userId = userId;
        this.userName = userName;
        this.deptId = deptId;
    }

    public SysInfo(String deptId, String parentId, String deptName, List<SysInfo> children) {
        this.deptId = deptId;
        this.parentId = parentId;
        this.deptName = deptName;
        this.children = children;
    }

    public SysInfo(String nodeId, String nodeName) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }

    public SysInfo() {
    }
}
