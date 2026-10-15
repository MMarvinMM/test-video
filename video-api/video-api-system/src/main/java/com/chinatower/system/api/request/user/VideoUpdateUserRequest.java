package com.chinatower.system.api.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @auther liuhuanhuan
 * @date 2022/3/31
 */
@Data
@ApiModel(value = "VideoUpdateUserRequest", description = "用户修改请求参数")
public class VideoUpdateUserRequest {

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", example = "948739", required = true)
    @NotNull(message = "用户ID不能为空")
    private String userId;


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
    @ApiModelProperty(value = "手机号码", example = "13324321234")
    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    private String phonenumber;

    /**
     * 用户性别
     */
    @ApiModelProperty(value = "用户性别(0=男,1=女)", example = "0")
    private String sex;

    /**
     * 固定电话
     */
    @ApiModelProperty(value = "固定电话", example = "03713984989")
    private String telPhone;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", example = "xxx")
    private String remark;
}
