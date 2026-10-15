package com.chinatower.platform.auth.project.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "验证用户修改密码")
public class CheckPasswordDTO implements Serializable {

    @NotNull(message = "用户名不允许为空")
    @ApiModelProperty("用户名")
    private String username;

    @NotNull(message = "手机验证码不允许为空")
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 是否需要解密标识（1需要，0不需要）
     */
    private String decryptFlag;

}
