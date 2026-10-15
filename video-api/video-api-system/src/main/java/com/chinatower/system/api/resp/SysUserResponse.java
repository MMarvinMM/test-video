package com.chinatower.system.api.resp;

import com.chinatower.platform.model.resp.BaseResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Arrays;
import java.util.Date;

/**
 * @auther liuhuanhuan
 * @date 2022/3/10
 */
@ApiModel(value = "SysUserResponse", description = "用户信息返回参数")
public class SysUserResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 部门ID
     */
    @ApiModelProperty(value = "部门ID")
    private String deptId;
    /**
     * 企业ID
     */
    private Long enterpriseId;
    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    private String userName;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    /**
     * 用户邮箱
     */
    @ApiModelProperty(value = "用户邮箱")
    private String email;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码")
    private String phonenumber;

    /**
     * 固定电话
     */
    @ApiModelProperty(value = "固定电话")
    private String telPhone;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别")
    private String sex;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatar;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 盐加密
     */
    @ApiModelProperty(value = "盐加密")
    private String salt;

    /**
     * 用户类型
     */
    @ApiModelProperty(value = "用户类型")
    private String userType;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    /**
     * 最后登陆IP
     */
    @ApiModelProperty(value = "最后登陆IP")
    private String loginIp;

    /**
     * 最后登陆时间
     */
    @ApiModelProperty(value = "最后登陆时间")
    private Date loginDate;

    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码")
    private String tenantId;
    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;
    /**
     * 行业
     */
    @ApiModelProperty(value = "行业")
    private String tradeInfo;

    /**
     * 是否租户管理员(0否,1是)
     */
    @ApiModelProperty(value = "是否租户管理员(0否,1是)")
    private String isTenantAdmin;

    /**
     * 角色组
     */
    @ApiModelProperty(value = "角色组")
    private Long[] roleIds;

    /**
     * 岗位组
     */
    @ApiModelProperty(value = "岗位组")
    private Long[] postIds;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "失败次数")
    private Integer loginFailNum;

    /**
     * 账号来源
     */
    @ApiModelProperty(value = "账号来源")
    private String source;

    /**
     * 铁塔账号来源
     */
    @ApiModelProperty(value = "铁塔账号来源")
    private String towerUserSource;

    /**
     * 租户编码or名称
     */
    @ApiModelProperty(value = "租户编码or名称")
    private String tenantIdOrName;

    /**
     * 账号来源
     */
    @ApiModelProperty(value = "账号来源")
    private String userSource;

    @ApiModelProperty(value = "所选行业ID")
    private String chooseTradeId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(String userId) {
        return userId != null && "1".equalsIgnoreCase(userId);
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getSex() {
        return sex;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

    public Long[] getPostIds() {
        return postIds;
    }

    public void setPostIds(Long[] postIds) {
        this.postIds = postIds;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getLoginFailNum() {
        return loginFailNum;
    }

    public void setLoginFailNum(Integer loginFailNum) {
        this.loginFailNum = loginFailNum;
    }

    public String getChooseTradeId() {
        return chooseTradeId;
    }

    public void setChooseTradeId(String chooseTradeId) {
        this.chooseTradeId = chooseTradeId;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTradeInfo() {
        return tradeInfo;
    }

    public void setTradeInfo(String tradeInfo) {
        this.tradeInfo = tradeInfo;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getIsTenantAdmin() {
        return isTenantAdmin;
    }

    public void setIsTenantAdmin(String isTenantAdmin) {
        this.isTenantAdmin = isTenantAdmin;
    }

    public String getTelPhone() {
        return telPhone;
    }

    public void setTelPhone(String telPhone) {
        this.telPhone = telPhone;
    }

    public String getUserSource() {
        return userSource;
    }

    public void setUserSource(String userSource) {
        this.userSource = userSource;
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "userId=" + userId +
                ", deptId='" + deptId + '\'' +
                ", enterpriseId=" + enterpriseId +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", sex='" + sex + '\'' +
                ", avatar='" + avatar + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", userType='" + userType + '\'' +
                ", status='" + status + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", loginIp='" + loginIp + '\'' +
                ", loginDate=" + loginDate +
                ", tenantId='" + tenantId + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", tradeInfo='" + tradeInfo + '\'' +
                ", isTenantAdmin='" + isTenantAdmin + '\'' +
                ", roleIds=" + Arrays.toString(roleIds) +
                ", postIds=" + Arrays.toString(postIds) +
                ", remark='" + remark + '\'' +
                ", loginFailNum=" + loginFailNum +
                '}';
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTowerUserSource() {
        return towerUserSource;
    }

    public void setTowerUserSource(String towerUserSource) {
        this.towerUserSource = towerUserSource;
    }

    public String getTenantIdOrName() {
        return tenantIdOrName;
    }

    public void setTenantIdOrName(String tenantIdOrName) {
        this.tenantIdOrName = tenantIdOrName;
    }
}
