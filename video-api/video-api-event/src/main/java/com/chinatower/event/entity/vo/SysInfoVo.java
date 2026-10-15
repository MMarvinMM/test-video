package com.chinatower.event.entity.vo;

import com.chinatower.event.entity.dto.SysInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/2/19 16:22
 */
@Data
public class SysInfoVo implements Serializable {
    private static final long serialVersionUID = -6502540918082138673L;

    /**
     * 用户ID
     * @required true
     */
    private String userId;

    /**
     * 用户名称
     * @required true
     */
    private String userName;

    /**
     * 用户昵称
     * @required true
     */
    private String nickName;
    private Long roleId;
    private String roleName;

    /**
     * 部门ID
     * @required true
     */
    private String deptId;

    /**
     * 父级组织ID
     * @required true
     */
    private String parentId;

    /**
     * 部门名称
     * @required true
     */
    private String deptName;

    /**
     * 节点ID(前端展示树形使用)
     * @required true
     */
    private String nodeId;

    /**
     * 节点名称(前端展示树形使用)
     * @required true
     */
    private String nodeName;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 该用户是否有数据权限（0否，1是）
     * @required true
     */
    private String purviewFlag;

    /**
     * 是否租户管理员(0否,1是)
     * @required true
     */
    private String tenantAdmin;

    private List<SysInfoVo> children;

    private String status;

    private String phonenumber;

    public SysInfoVo(String userId, String userName, String deptId) {
        this.userId = userId;
        this.userName = userName;
        this.deptId = deptId;
    }

    public SysInfoVo(String deptId, String parentId, String deptName, List<SysInfoVo> children) {
        this.deptId = deptId;
        this.parentId = parentId;
        this.deptName = deptName;
        this.children = children;
    }

    public SysInfoVo(String nodeId, String nodeName) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }

    public SysInfoVo() {
    }


    /**
     * 所有有权限的用户
     * @required true
     */
    private SysInfoVo sysPowerInfo;

    /**
     * 所有配置用户
     * @required true
     */
    private SysInfoVo sysAllInfo;

    /**
     * 所有有权限列表人物
     */
    private List<SysInfo> sysPowerList;

    /**
     * 所有可选人物
     */
    private List<SysInfo> sysAllList;
}
