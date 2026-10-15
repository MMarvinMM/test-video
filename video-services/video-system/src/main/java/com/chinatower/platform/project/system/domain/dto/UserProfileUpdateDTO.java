package com.chinatower.platform.project.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserProfileUpdateDTO {

    /**
     * 用户昵称
     */
    @NotBlank(message = "姓名不允许为空")
    private String nickName;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    @Pattern(regexp = "/^[1][1,2,3,4,5,6,7,8,9][0-9]{9}$/",message = "手机号格式不正确")
    @NotBlank(message = "手机号不允许为空")
    private String phonenumber;

    /**
     * 固定电话
     */
    private String telPhone;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 手机号修改验证标识
     */
    private String phoneEditCode;

    /**
     * 用户id
     */
    @NotBlank(message = "用户id不允许为空")
    private String userId;
}
