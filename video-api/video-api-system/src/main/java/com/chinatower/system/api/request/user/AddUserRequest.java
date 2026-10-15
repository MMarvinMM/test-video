package com.chinatower.system.api.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @auther liuhuanhuan
 * @date 2022/2/15
 */
@Data
@ApiModel(value = "AddUserRequest", description = "用户新增请求参数")
public class AddUserRequest {

    /**
     * 部门ID
     */
    @ApiModelProperty(value = "部门ID", example = "110000", required = true)
    @NotEmpty(message = "部门ID不能为空")
    private String deptId;
    /**
     * 用户账号
     */
    @ApiModelProperty(value = "登录账号", example = "test", required = true)
    @NotEmpty(message = "登录账号不能为空")
    private String userName;

    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称", example = "张三", required = true)
    @NotEmpty(message = "用户昵称不能为空")
    private String nickName;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    @ApiModelProperty(value = "用户邮箱", example = "xxxx@163.com")
    private String email;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "13324321234", required = true)
    @NotEmpty(message = "手机号码不能为空")
    private String phonenumber;

    /**
     * 固定电话
     */
    private String telPhone;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别(0=男,1=女)", example = "0")
    private String sex;

    /**
     * 用户类型
     */
    @ApiModelProperty(value = "用户类型(0=铁塔人员,2=行业客户)", example = "0", required = true)
    @NotEmpty(message = "用户类型不能为空")
    private String userType;

    /**
     * 帐号状态（0正常 1停用）
     */
    @ApiModelProperty(value = "帐号状态（0正常 1停用）", example = "0", required = true)
    @NotEmpty(message = "帐号状态不能为空")
    private String status;

    /**
     * 租户编码
     */
    @ApiModelProperty(value = "租户编码", example = "TEN100001")
    private String tenantId;
    /**
     * 是否租户管理员(0否,1是)
     */
    @ApiModelProperty(value = "是否租户管理员(0否,1是)", example = "1")
    private String isTenantAdmin;

    /**
     * 角色组
     */
    @ApiModelProperty(value = "角色集合", example = "[1,2]")
    private Long[] roleIds;

    /**
     * 岗位组
     */
    @ApiModelProperty(value = "岗位集合", example = "[1,2]")
    private Long[] postIds;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "xx")
    private String remark;

    /**
     * 加密userName
     */
    @ApiModelProperty(value = "加密userName", example = "xx")
    private String AesAccount;


    /**
     * 新手机号
     */
    @ApiModelProperty(value = "新手机号", example = "xx")
    private String newPhone;

    /**
     * 新密码
     */
    @ApiModelProperty(value = "新密码", example = "xx")
    private String newPassWord;

    /**
     * 验证码
     */
    @ApiModelProperty(value = "验证码", example = "xx")
    private String code;

    /**
     * 新验证码
     */
    @ApiModelProperty(value = "新验证码", example = "xx")
    private String newCode;

    @ApiModelProperty(value = "用户来源", example = "0,1")
    private String towerUserSource;

    /** 账号有效期 */
    private String accountValidityPeriod;

    /** 账号有效期状态(1 永久有效  2 自定义日期) */
    private String validityPeriodState;
}
