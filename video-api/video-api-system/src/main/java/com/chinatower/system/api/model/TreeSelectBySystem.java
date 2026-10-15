
package com.chinatower.system.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.io.Serializable;
import java.util.List;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 查询组织人员树
 * @data 创建时间：2023/8/22 10:08
 */
public class TreeSelectBySystem implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     */
    private String id;
    /**
     * 标签
     */
    private String label;
    /**
     * pid
     */
    private String pid;
    /**
     * 部门
     */
    private String dept;
    /**
     * 下级人员
     */
    @JsonInclude(Include.NON_EMPTY)
    private List<TreeSelectBySystem> children;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 账号
     */
    private String account;
    /**
     * 电话
     */
    private String phone;
    /**
     * 性别
     */
    private String sex;
    /**
     * 头像
     */
    private String avatar;

    public TreeSelectBySystem() {
    }

    public TreeSelectBySystem(String id, String label, String pid) {
        this.id = id;
        this.label = label;
        this.pid = pid;
    }

    public TreeSelectBySystem(String id, String label, String pid, String dept) {
        this.id = id;
        this.label = label;
        this.pid = pid;
        this.dept = dept;
    }

    public TreeSelectBySystem(String id, String label, String pid, String dept, String account, String phone, String sex) {
        this.id = id;
        this.label = label;
        this.pid = pid;
        this.dept = dept;
        this.account = account;
        this.phone = phone;
        this.sex = sex;
    }

    public TreeSelectBySystem(String id, String label, String pid, String dept, String account, String phone, String sex, String avatar) {
        this.id = id;
        this.label = label;
        this.pid = pid;
        this.dept = dept;
        this.account = account;
        this.phone = phone;
        this.sex = sex;
        this.avatar = avatar;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<TreeSelectBySystem> getChildren() {
        return this.children;
    }

    public void setChildren(List<TreeSelectBySystem> children) {
        this.children = children;
    }

    public String getDept() {
        return this.dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
