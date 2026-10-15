package com.chinatower.system.api.model;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户对象 sys_user
 *
 * @author liuxt
 */
@Data
@ToString
public class SysUser4A implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String userName;


    /**
     * 用户昵称
     */
    private String nickName;


    /**
     * 用户类型（0系统用户 2租户）
     */
    private String userType;


    /**
     * 用户邮箱
     */
    private String email;


    /**
     * 手机号码
     */
    private String phonenumber;


    /**
     * 固定电话
     */
    private String telephone;


    /**
     * 固定电话
     */
    private String telPhone;


    /**
     * 用户性别（0男 1女）
     */
    private String sex;


    /**
     * 密码
     */
    private String password;

    /**
     * 加密后的密码
     */
    private String enPassword;


    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;


    /**
     * 创建者
     */
    private String createBy;


    /**
     * 创建时间
     */
    private String createTime;


    /**
     * 更新者
     */
    private String updateBy;


    /**
     * 更新时间
     */
    private String updateTime;


    /**
     * 部门ID
     */
    private String deptId;


    /**
     * 租户id
     */
    private String tenantId;


    /**
     * 是否租户管理员(0否,1是)
     */
    private String isTenantAdmin;


    /**
     * 铁塔账号来源   0:4a 1:自建
     */
    private String towerUserSource;


    /**
     * 来源：0铁塔 1租户
     */
    private String source;


    /**
     * 省份
     */
    private String provinceId;


    /**
     * 地市
     */
    private String cityId;


    /**
     * 账号来源(0-4A,1-自建)
     */
    private String userSource;


    /**
     * 1:新增；2:修改；3：删除
     */
    private String flag ;


    private Long[] roleIds;

}