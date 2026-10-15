package com.chinatower.platform.entity.admin;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.dto.PageDTO;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.common.core.serializer.FastJsonLongArrayToStringSerializer;
import com.chinatower.common.core.serializer.JacksonLongArrayToStringSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户对象 sys_user
 *
 * @author bruce
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUser extends PageDTO {
    private static final long serialVersionUID = 1L;

    /**
     * 密码修改时间标识
     */
    private String pwdUpdateTimeFlag;
    /**
     * 角色标识 （1默认角色 2自定义角色）
     */
    private String roleFlag;

    /** 账号有效期 */
    private String accountValidityPeriod;

    /** 账号有效期状态(1 永久有效  2 自定义日期) */
    private String validityPeriodState;


    /**
     * 模糊搜索(账号、姓名、手机号)
     */
    private String searchKeyword;

    /**
     * 客户端类型集合
     */
    private List<String> clientType;

    /**
     * 部门ID集合
     */
    private List<String> deptIds;
    /**
     * 企业ID
     */
    private Long enterpriseId;



    /**
     * 移动端号码
     */
    private String mobileTerminalNum;
    /**
     * 控制台号码
     */
    private String dispatchDeskNum;

    /**
     * 用户昵称
     */
    @Excel(name = "*姓名")
    private String nickName;

    /**
     * 用户账号
     * @@required true
     */
    @Excel(name = "*账号")
    private String userName;

    /**
     * 用户账号【用于精确匹配】
     */
    private String exactUserName;

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;

    public List<String> getParentNames() {
        return parentNames;
    }

    public void setParentNames(List<String> parentNames) {
        this.parentNames = parentNames;
    }

    /**
     * 父级组织名称
     */
    private List<String> parentNames;


    /**
     * 请求来源
     */
    private String requestSource;

    /**
     * 手机号码
     */
    @Excel(name = "*手机号码")
    private String phonenumber;

    /**
     * 加密手机号码
     */
    private String aesPhoneNumber;

    /**
     * 密码修改时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pwdUpdateTime;



    /**
     *行业应用
     */
    private String industryName;
    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }
    /**
     * 固定电话
     */
    @Excel(name = "固定电话")
    private String telPhone;

    /**
     * 加密固定电话
     */
    private String aesTelPhone;

    @Excel(name="组织名称")
    /**
     * 组织名称
     */
    private String deptName;

    /**
     * 部门ID
     */
    @Excel(name = "*组织编码",type = Excel.Type.IMPORT)
    private String deptId;

    /**
     * 用户性别
     */
    @Excel(name = "性别",readConverterExp = "0=男,1=女")
    private String sex;

    /**
     * 用户邮箱
     */
    @Excel(name = "邮箱")
    private String email;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 盐加密
     */
    private String salt;

    /**
     * 用户类型 （0系统用户 2租户）
     */
    private String userType;

    /**
     * 帐号状态（0正常 1停用）
     */
//    @Excel(name = "帐号状态",readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 最后登陆IP
     */
    private String loginIp;

    /**
     * 最后登陆时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 部门对象
     */
    private SysDept dept;

    public List<String> getIndustryCodes() {
        return industryCodes;
    }

    public void setIndustryCodes(List<String> industryCodes) {
        this.industryCodes = industryCodes;
    }

    /**
     * 多行业编码
     * @return
     */
    private  List<String> industryCodes;

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    /**
     * 用户编码集合
     * @return
     */
    private List<String> userIdList;


    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * 租户对象
     */
    private SysTenantInfo tenantInfo;

    /**
     * 角色对象
     */
    private List<SysRole> roles;

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }



    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 行业
     */
    private String tradeInfo;

    /**
     * 是否租户管理员(0否,1是)
     */
    private String isTenantAdmin;

    /**
     * 角色组
     */
    @JSONField(serializeUsing = FastJsonLongArrayToStringSerializer.class)
    @JsonSerialize(using = JacksonLongArrayToStringSerializer.class)
    private Long[] roleIds;

    /**
     * 角色编码
     */
    private Long roleIdquery;

    public Long getRoleIdquery() {
        return roleIdquery;
    }

    public void setRoleIdquery(Long roleIdquery) {
        this.roleIdquery = roleIdquery;
    }

    /**
     * 角色组
     */
    private String[] userNames;

    /**
     * 岗位组
     */
    private Long[] postIds;

    /**
     * 备注
     */
    @Excel(name="备注")
    private String remark;

    /**
     * 角色名称
     */
    @Excel(name="*角色")
    private String roleNames;

    /**
     * 角色对象
     */
    private String roleName;

    /**
     * 登录失败数
     */
    private Integer loginFailNum;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 地区编码
     */
    private String areaCode;

    /**
     * 设备列表
     */
    private List<Map<String,Object>> deviceList;
    /**
     * 设备编码列表
     */
    private List<String> deviceCodeList;
    /**
     * 角色类型
     */
    private String roleType;

    /**
     * 省份id
     */
    @ApiModelProperty(value = "省份id")
    @TableField(exist = false)
    private String provinceCode;

    /**
     * 地市id
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "地市id")
    private String cityCode;

    /**
     * 0:普通员工 1:部门负责人 2：公司领导
     */
    private String grade;

    /**
     * 工号
     */
    private String empNo;

    /**
     * 排序
     */
    private String sortNo;

    /** 省份 */
    private String provinceId;

    /** 城市 */
    private String cityId;

    /** 行业版本编码 */
    private String chooseAppVerCode;

    /** 行业编码 */
    private String chooseTradeId;

    /** 本级平台编码 */
    private String platformCode;

    /** 平台级别 0:中心节点 1:省份节点 2:地市节点 */
    private String platformLevel;

    /** 本级平台名称 */
    private String platformName;

    /** 是否判断登录IP白名单 0:不判断白名单，1：判断白名单 */
    private Integer whiteIpFlag;

    public Integer getWhiteIpFlag() {
        return whiteIpFlag;
    }

    public void setWhiteIpFlag(Integer whiteIpFlag) {
        this.whiteIpFlag = whiteIpFlag;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getPlatformLevel() {
        return platformLevel;
    }

    public void setPlatformLevel(String platformLevel) {
        this.platformLevel = platformLevel;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public List<Map<String,Object>> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Map<String,Object>> deviceList) {
        this.deviceList = deviceList;
    }

    public List<String> getDeviceCodeList() {
        return deviceCodeList;
    }

    public void setDeviceCodeList(List<String> deviceCodeList) {
        this.deviceCodeList = deviceCodeList;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * 角色ID
     * @required true
     */
    private Long roleId;

    /**用户id逗号分割*/
    private String[] userIds;

    public String[] getUserIds() {
        return userIds;
    }

    public void setUserIds(String[] userIds) {
        this.userIds = userIds;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }


    /** 账号来源 */
    private String source;

    /** 铁塔账号来源 */
    private String towerUserSource;

    /** 租户编码or名称 */
    private String tenantIdOrName;
    /** 行政区划树 */
    private List<TreeSelect> areaTree;

    /** 组织行政区域权限集合 */
    private List<SysDeptStreetRel> areaList;

    /** 账号来源 */
    private String userSource;


    public String getChooseTradeId() {
        return chooseTradeId;
    }

    public void setChooseTradeId(String chooseTradeId) {
        this.chooseTradeId = chooseTradeId;
    }

    public String getChooseAppVerCode() {
        return chooseAppVerCode;
    }

    public void setChooseAppVerCode(String chooseAppVerCode) {
        this.chooseAppVerCode = chooseAppVerCode;
    }


    public SysUser() {
    }
    public List<TreeSelect> getAreaTree() {
        return areaTree;
    }
    public void setAreaTree(List<TreeSelect> areaTree) {
        this.areaTree = areaTree;
    }

    public List<SysDeptStreetRel> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<SysDeptStreetRel> areaList) {
        this.areaList = areaList;
    }

    public SysUser(String userId) {
        setUserId(userId);
    }

    /**
     * @deprecated Use {@link com.chinatower.platform.entity.admin.SysUser#isAdminNew()} instead.
     */
    @Deprecated
    public boolean isAdmin() {
        return isAdmin(getUserId());
    }

    /**
     * @deprecated Use {@link com.chinatower.platform.entity.admin.SysUser#isAdminNew(List)} instead.
     */
    @Deprecated
    public static boolean isAdmin(String userId) {
        return userId != null && "1".equals(userId);
    }

    public boolean isAdminNew(){
        return isAdminNew(getRoles());
    }

    public static boolean isAdminNew(List<SysRole> userRoleList) {
        if (CollectionUtils.isEmpty(userRoleList)) {
            return false;
        }
        return userRoleList.stream().anyMatch(sysRole -> Long.valueOf(3L).equals(sysRole.getRoleId()));
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    @NotBlank(message = "手机号码不能为空") 前端校验，后端修改时允许为空，不修改
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

    public SysTenantInfo getTenantInfo() {
        return tenantInfo;
    }

    public String getMobileTerminalNum() {
        return mobileTerminalNum;
    }

    public void setMobileTerminalNum(String mobileTerminalNum) {
        this.mobileTerminalNum = mobileTerminalNum;
    }

    public String getDispatchDeskNum() {
        return dispatchDeskNum;
    }

    public void setDispatchDeskNum(String dispatchDeskNum) {
        this.dispatchDeskNum = dispatchDeskNum;
    }
    public void setTenantInfo(SysTenantInfo tenantInfo) {
        this.tenantInfo = tenantInfo;
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

    public SysDept getDept() {
        return dept;
    }

    public void setDept(SysDept dept) {
        this.dept = dept;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    public String[] getUserNames() {
        return userNames;
    }

    public void setUserNames(String[] userNames) {
        this.userNames = userNames;
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

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public List<String> getDeptIds() {
        if(CollUtil.isEmpty(deptIds) && StringUtils.isNotEmpty(deptId)){
            deptIds = Arrays.asList(deptId.split(","));
        }
        return deptIds;
    }

    public void setDeptIds(List<String> deptIds) {
        this.deptIds = deptIds;
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

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getSortNo() {
        return sortNo;
    }

    public void setSortNo(String sortNo) {
        this.sortNo = sortNo;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "SysUser{" +
                "userId=" + getUserId() +
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
                ", dept=" + dept +
                ", roles=" + roles +
                ", tenantId='" + getTenantId() + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", tradeInfo='" + tradeInfo + '\'' +
                ", isTenantAdmin='" + isTenantAdmin + '\'' +
                ", roleIds=" + Arrays.toString(roleIds) +
                ", postIds=" + Arrays.toString(postIds) +
                ", remark='" + remark + '\'' +
                ", loginFailNum=" + loginFailNum +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", mobileTerminalNum='" + mobileTerminalNum + '\'' +
                ", dispatchDeskNum='" + dispatchDeskNum + '\'' +
                '}';
    }


    public String getRequestSource() {
        return requestSource;
    }

    public void setRequestSource(String requestSource) {
        this.requestSource = requestSource;
    }

    public String getExactUserName() {
        return exactUserName;
    }

    public void setExactUserName(String exactUserName) {
        this.exactUserName = exactUserName;
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

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAesPhoneNumber() {
        return aesPhoneNumber;
    }

    public void setAesPhoneNumber(String aesPhoneNumber) {
        this.aesPhoneNumber = aesPhoneNumber;
    }

    public Date getPwdUpdateTime() {
        return pwdUpdateTime;
    }

    public void setPwdUpdateTime(Date pwdUpdateTime) {
        this.pwdUpdateTime = pwdUpdateTime;
    }

    public List<String> getClientType() {
        return clientType;
    }

    public void setClientType(List<String> clientType) {
        this.clientType = clientType;
    }

    public String getAesTelPhone() {
        return aesTelPhone;
    }

    public void setAesTelPhone(String aesTelPhone) {
        this.aesTelPhone = aesTelPhone;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public String getRoleFlag() {
        return roleFlag;
    }

    public void setRoleFlag(String roleFlag) {
        this.roleFlag = roleFlag;
    }

    public String getAccountValidityPeriod() {
        return accountValidityPeriod;
    }

    public void setAccountValidityPeriod(String accountValidityPeriod) {
        this.accountValidityPeriod = accountValidityPeriod;
    }

    public String getValidityPeriodState() {
        return validityPeriodState;
    }

    public void setValidityPeriodState(String validityPeriodState) {
        this.validityPeriodState = validityPeriodState;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setPwdUpdateTimeFlag(String pwdUpdateTimeFlag) {
        this.pwdUpdateTimeFlag = pwdUpdateTimeFlag;
    }

    public String getPwdUpdateTimeFlag() {
        return pwdUpdateTimeFlag;
    }
}