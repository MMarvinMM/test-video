package com.chinatower.system.api.resp;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.chinatower.platform.model.resp.BaseResponse;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Set;

/**
 * @auther liuhuanhuan
 * @date 2022/3/10
 */
@ApiModel(value = "SysRoleResponse", description = "角色信息返回参数")
public class SysRoleResponse extends BaseResponse {

    /**
     * 角色ID
     */
    @ApiModelProperty(value = "角色ID")
    @JSONField(serializeUsing = ToStringSerializer.class)
    @JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long roleId;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    /**
     * 角色权限
     */
    @ApiModelProperty(value = "角色权限")
    private String roleKey;

    //角色类型:0=铁塔角色,1=公共角色,2=租户角色
    @ApiModelProperty(value = "角色类型:0=铁塔角色,1=公共角色,2=租户角色")
    private String roleType;

    //所属租户id
    @ApiModelProperty(value = "所属租户id")
    private String tenantId;

    @ApiModelProperty(value = "所属租户name")
    private String tenantName;

    /**
     * 角色排序
     */
    @ApiModelProperty(value = "角色排序")
    private String roleSort;

    /**
     * 角色状态（0正常 1停用）
     */
    @ApiModelProperty(value = "角色状态（0正常 1停用）")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    /**
     * 用户是否存在此角色标识 默认不存在
     */
    @ApiModelProperty(value = "用户是否存在此角色标识")
    private boolean flag = false;

    /**
     * 菜单组
     */
    @ApiModelProperty(value = "菜单组")
    private Long[] menuIds;

    /**
     * 部门组（数据权限）
     */
    @ApiModelProperty(value = "部门组")
    private String[] deptIds;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 权限列表
     */
    @ApiModelProperty(value = "权限列表")
    private Set<String> permissions;

    /**
     * 云台操控权限
     */
    @ApiModelProperty(value = "云台操控权限")
    private String cloudPower;

    /**
     * 省份名称
     */
    @ApiModelProperty(value = "省份名称")
    private String provinceName;


    /**
     * 地市名称
     */
    @ApiModelProperty(value = "地市名称")
    private String cityName;

    /**
     * 角色id
     */
    @ApiModelProperty(value = "角色id")
    private Long[] roleIds;

    /**
     * 操控权限
     */
    @ApiModelProperty(value = "操控权限")
    private String[] cloudPawers;


    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

    public static boolean isAdmin(Long roleId) {
        return roleId != null && ((2L == roleId) || (3L == roleId));
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public String getRoleSort() {
        return roleSort;
    }

    public void setRoleSort(String roleSort) {
        this.roleSort = roleSort;
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

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Long[] getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(Long[] menuIds) {
        this.menuIds = menuIds;
    }

    public String[] getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String[] deptIds) {
        this.deptIds = deptIds;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCloudPower() {
        return cloudPower;
    }

    public void setCloudPower(String cloudPower) {
        this.cloudPower = cloudPower;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    public String[] getCloudPawers() {
        return cloudPawers;
    }

    public void setCloudPawers(String[] cloudPawers) {
        this.cloudPawers = cloudPawers;
    }
}
