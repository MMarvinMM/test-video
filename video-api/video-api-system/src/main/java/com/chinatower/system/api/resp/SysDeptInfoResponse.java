package com.chinatower.system.api.resp;

import com.chinatower.platform.model.resp.BaseResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther liuhuanhuan
 * @date 2022/3/10
 */
@ApiModel(value = "SysDeptInfoResponse",description = "部门信息返回参数")
public class SysDeptInfoResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 是否具有子节点
     */
    @ApiModelProperty(value = "是否具有子节点")
    private Boolean hasChildren;
    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    /**
     * 租户组织归属铁塔组织ID
     */
    @ApiModelProperty(value = "租户组织归属铁塔组织ID")
    private String tenantTowerDeptId;

    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 部门ID
     */
    @ApiModelProperty(value = "部门ID")
    private String deptId;

    /**
     * 父部门ID
     */
    @ApiModelProperty(value = "父部门ID")
    private String parentId;

    /**
     * 祖级列表
     */
    @ApiModelProperty(value = "祖级列表")
    private String ancestors;

    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String deptName;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    private String orderNum;

    /**
     * 负责人
     */
    @ApiModelProperty(value = "负责人")
    private String leader;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 部门状态:0正常,1停用
     */
    @ApiModelProperty(value = "部门状态:0正常,1停用")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "区删除标志（0代表存在 2代表删除）域编码")
    private String delFlag;

    /**
     * 省分公司编码
     */
    @ApiModelProperty(value = "省分公司编码")
    private String province;

    /**
     * 市分公司编码
     */
    @ApiModelProperty(value = "市分公司编码")
    private String city;

    /**
     * 父部门名称
     */
    @ApiModelProperty(value = "父部门名称")
    private String parentName;

    @ApiModelProperty(value = "级别")
    private String deptLevel;

    private String bussnessUnit;

    /**
     * 子部门
     */
    @ApiModelProperty(value = "子部门")
    private List<SysDeptInfoResponse> children = new ArrayList<SysDeptInfoResponse>();

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantTowerDeptId() {
        return tenantTowerDeptId;
    }

    public void setTenantTowerDeptId(String tenantTowerDeptId) {
        this.tenantTowerDeptId = tenantTowerDeptId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getDeptName() {
        return deptName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getDeptLevel() {
        return deptLevel;
    }

    public void setDeptLevel(String deptLevel) {
        this.deptLevel = deptLevel;
    }

    public String getBussnessUnit() {
        return bussnessUnit;
    }

    public void setBussnessUnit(String bussnessUnit) {
        this.bussnessUnit = bussnessUnit;
    }


    public List<SysDeptInfoResponse> getChildren() {
        return children;
    }

    public void setChildren(List<SysDeptInfoResponse> children) {
        this.children = children;
    }
}
