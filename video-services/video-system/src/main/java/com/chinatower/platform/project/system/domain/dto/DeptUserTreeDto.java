package com.chinatower.platform.project.system.domain.dto;

import com.chinatower.common.entity.TreeSelect;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 部门用户树实体
 *
 * @author qq on 2023/2/1
 */
@Data
@NoArgsConstructor
public class DeptUserTreeDto implements Serializable {

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
     * 部门状态（0启用,1删除)
     */
    private String status;

    /**
     * 子部门
     */
    private List<DeptUserTreeDto> children = new ArrayList<>();

    /**
     * 部门人员
     */
    private List<User> users = new ArrayList<>();

    public DeptUserTreeDto(SysDept dept) {
        this.deptId = dept.getDeptId();
        this.parentId = dept.getParentId();
        this.deptName = dept.getDeptName();
        this.status = dept.getStatus();
    }

    public DeptUserTreeDto(TreeSelect dept) {
        this.deptId = dept.getId();
        this.parentId = dept.getPid();
        this.deptName = dept.getLabel();
    }

    /**
     * 用户
     */
    @Data
    @NoArgsConstructor
    public static class User implements Serializable {
        /**
         * 用户ID
         */
        private String userId;
        /**
         * 部门ID
         */
        private String deptId;

        /**
         * 用户账号
         */
        private String userName;

        /**
         * 用户姓名
         */
        private String nickName;

        /**
         * 用户头像
         */
        private String avatar;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 帐号状态（0正常 1停用）
         */
        private String status;

        /**
         * 是否租户管理员
         */
        private String isTenantAdmin;

        /**
         * 角色
         */
        private List<String> roles;

        public User(SysUser sysUser, List<String> roles) {
            this.userId = sysUser.getUserId();
            this.deptId = sysUser.getDeptId();
            this.userName = sysUser.getUserName();
            this.nickName = sysUser.getNickName();
            this.avatar = sysUser.getAvatar();
            this.createTime = sysUser.getCreateTime();
            this.status = sysUser.getStatus();
            this.isTenantAdmin = sysUser.getIsTenantAdmin();
            this.roles = roles;
        }

    }
}
