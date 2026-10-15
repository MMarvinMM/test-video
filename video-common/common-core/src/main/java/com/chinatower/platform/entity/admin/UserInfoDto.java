package com.chinatower.platform.entity.admin;


import com.chinatower.common.annotation.Excel;
import lombok.Data;

/**
 * 用户对象 sys_user
 *
 * @author bruce
 */
@Data
public class UserInfoDto {
    private static final long serialVersionUID = 1L;

    private String userId;

    /**
     * 用户账号
     */
    @Excel(name = "登录名称")
    private String userName;

    /**
     * 用户昵称
     */
    @Excel(name = "用户名称")
    private String nickName;

    /**
     * 手机号码
     */
    @Excel(name = "手机号码")
    private String phonenumber;

    /**
     * 用户头像
     */
    private String avatar;


    /**
     * 帐号状态（0正常 1停用）
     */
    @Excel(name = "帐号状态",readConverterExp = "0=正常,1=停用")
    private String status;

    private String deptId;

}