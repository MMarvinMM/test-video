package com.chinatower.platform.entity.admin;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 部门表 sys_dept
 *
 * @author bruce
 * @Date 2021-11-18
 */
@Data
@SuppressWarnings("all")
public class SysDept extends BaseDataScopeEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 街道编码集合
     */
    private List<String> applyScope;

    /**
     * 是否具有子节点
     */
    private Boolean hasChildren;
    /**
     * 租户IP
     */
    private String tenantId;

    /**
     * 租户组织归属铁塔组织ID
     */
    private String tenantTowerDeptId;

    /**
     * 租户名称
     */
    private String tenantName;

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
     * 祖级列表
     */
    private String ancestors;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 显示顺序
     */
    private String orderNum;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
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

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态:0正常,1停用
     */
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 省分公司编码
     */
    private String province;
    /**
     * 备注
     */
    private String remark;

    /**
     * 市分公司编码
     */
    private String city;

    /**
     * 父部门名称
     */
    private String parentName;
    /**
     * 部门级别
     */
    private String deptLevel;
    /**
     * 部门级别数字
     */
    private Integer deptLevelInt;
    /**
     * 归属主体
     */
    private String bussnessUnit;

    /** 开始时间 */
    private String beginTime;

    /** 结束时间 */
    private String endTime;

    /**用户id*/
    private String userId;

    /**昵称*/
    private String nickName;

    /**角色编码*/
    private String roleId;

    /** 行业应用版本编码 */
    private String appVerCode;

    /** 行业编码 */
    private String industryCode;

    /** 主数据编码 */
    private String mdmOrgCode;
    /** 部门编码集合 */
    private List<String> deptIdList;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 子部门
     */
    private List<SysDept> children = new ArrayList<SysDept>();

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
    }

    @NotBlank(message = "组织名称不能为空")
//    @Size(min = 0, max = 30, message = "组织名称长度不能超过30个字符")
    public String getDeptName() {
        return deptName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    /*@Size(min = 0, max = 11, message = "联系电话长度不能超过11个字符")*/
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

   /* @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")*/
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public List<SysDept> getChildren() {
        return children;
    }

    public void setChildren(List<SysDept> children) {
        this.children = children;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDeptLevel() {
        return deptLevel;
    }

    public void setDeptLevel(String deptLevel) {
        this.deptLevel = deptLevel;
    }

    public String getAppVerCode() {
        return appVerCode;
    }

    public void setAppVerCode(String appVerCode) {
        this.appVerCode = appVerCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SysDept d = (SysDept) o;
        return d.getDeptId().equalsIgnoreCase(deptId);
    }

    public void setDeptIdList(List<String> deptIdList) {
        this.deptIdList = deptIdList;
    }

    public List<String> getDeptIdList() {
        return deptIdList;
    }
}
